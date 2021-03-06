package br.inf.ufes.ppd.master;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import br.inf.ufes.ppd.*;
import br.inf.ufes.ppd.methods.Log;

public class MasterImpl implements Master {

    private List<Attack> atks = new ArrayList<Attack>();
    private Map<UUID, Slave> slaves = new HashMap<UUID, Slave>();
    private Map<UUID, String> slavesNames = new HashMap<UUID, String>();
    private ExecutorService executor = Executors.newFixedThreadPool(8);
	private int dictionaryLength = 80368;

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "10.10.10.1");
            // System.setProperty("java.rmi.server.hostname", "localhost");
            Registry registry = LocateRegistry.createRegistry(1099);

            Log.log("MASTER", "Criando referencia remota do mestre...");
            MasterImpl master = new MasterImpl();
            Master masterRef = (Master) UnicastRemoteObject.exportObject(master, 0);

            Log.log("MASTER", "Fazendo o binding do mestre no registry...");
            registry.rebind("mestre", masterRef);
            Log.log("MASTER", "Binding concluido. Master's ready!");

            while(true);

        } catch (Exception e) {
            Log.log("MASTER", "Erro: Mestre não pode ser registrado: ");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void addSlave(Slave s, String slaveName, UUID slaveKey) throws RemoteException {
        Log.log("MASTER", "Pedido de (re)registro de \"" + slaveName + "\"");

        synchronized (slaves) {
            slaves.put(slaveKey, s);
            slavesNames.put(slaveKey, slaveName);
        }

        Log.log("MASTER", "Registro de " + slaveName + " concluido com sucesso.");
    }

    @Override
    public void removeSlave(UUID slaveKey) throws RemoteException {
        Log.log("MASTER", "Removendo o Slave \"" + this.slavesNames.get(slaveKey) + "\"");
        synchronized (slaves) {
            slaves.remove(slaveKey);
            slavesNames.remove(slaveKey);
        }
    }

    @Override
    public void foundGuess(UUID slaveKey, int attackNumber, long currentindex, Guess currentguess) throws RemoteException {
        synchronized(this.atks) {
            Attack attack = null;
            // Busca o attack da lista de atks correspondente ao attackNumber
            for(Attack a : this.atks) {
                if(a.getAttackNumber() == attackNumber) {
                    attack = a;
                    break;
                }
            }
            if (attack == null) return; // Caso nao encontre, sai da funcao.

            long time = System.nanoTime();
            
            synchronized(attack) {
                SubAttack subAttack = attack.getSubAttack(slaveKey);
                if(subAttack == null) return;
                
                subAttack.setCurrentIndex(currentindex); // Atualiza o current index
                subAttack.setLastCheckpointTime(time); // Atualiza o LastCheckpointTime

                long timeSinceStart = time - subAttack.getStartTime();
                double seconds = (double) timeSinceStart / 1000000000.0;
                
                Log.log("MASTER", "[FoundGuess] Slave: " + this.slavesNames.get(slaveKey) + 
                            " Index: " + currentindex + 
                            " Chave candidata: " + currentguess.getKey() +
                            " Tempo desde o start: " + seconds + " segundos."
                );
                
                attack.addGuess(currentguess);
            }
		}
    }

    @Override
    public void checkpoint(UUID slaveKey, int attackNumber, long currentindex) throws RemoteException {
        synchronized(this.atks) {
            Attack attack = null;
            // Busca o attack da lista de atks correspondente ao attackNumber
            for(Attack a : this.atks) {
                if(a.getAttackNumber() == attackNumber) {
                    attack = a;
                    break;
                }
            }

            if (attack == null) return; // Caso nao encontre, sai da funcao.

            SubAttack subAttack = attack.getSubAttack(slaveKey);
			if(subAttack == null) return;
            
            long time = System.nanoTime();
			
			subAttack.setLastCheckpointTime(time);
            subAttack.setCurrentIndex(currentindex);

            String cp = "Checkpoint";

            // Se o subAttack está concluido, modifica a mensagem dizendo que refere-se ao
            // final checkpoint
            if (subAttack.isDone()) {
                cp = "Final Checkpoint";
            }
			
            long timeSinceStart = time - subAttack.getStartTime();
            double seconds = (double) timeSinceStart / 1000000000.0;

            Log.log("MASTER", "[" + cp + "]" + 
                        " Slave: " + this.slavesNames.get(slaveKey) + 
                        " Index: " + currentindex + 
                        " Tempo desde o start: " + seconds + " segundos."
            );
	        
			synchronized (attack) {
				if(attack.isDone()) {
					attack.notifyAll();
				}
			}
		}
    }

    @Override
    public Guess[] attack(byte[] ciphertext, byte[] knowntext) throws RemoteException {
        Log.log("MASTER", "Requisicao de ataque recebida...");

        Attack a = new Attack(ciphertext, knowntext);

        int attackNumber;
        synchronized (atks) {
            attackNumber = this.atks.size() + 1;
            a.setAttackNumber(attackNumber);
            atks.add(a);
        }
        
        int avaiableSlavesNumber = slaves.size();

		if (avaiableSlavesNumber == 0) {
            Log.log("MASTER", "Nenhum escravo disponivel");
            return new Guess[0];
		}

        Log.log("MASTER", "Iniciando ataque...");

        int i = 0;
		synchronized (slaves) {
            int intervalSize = dictionaryLength / avaiableSlavesNumber;
            int excedent = dictionaryLength % avaiableSlavesNumber;

			for (Map.Entry<UUID, Slave> s : slaves.entrySet()) {
				long initialwordindex = i * intervalSize;
                long finalwordindex   = ((i + 1) * intervalSize) - 1;

                // Ajusta o finalwordindex para que o ultimo range considere todos os indices excedentes
                // (para o caso em que o tamanho do intervalo do dicionario a ser avaliado por
                // cada slave nao é o mesmo)
                if (excedent > 0 && (initialwordindex + intervalSize + excedent) == dictionaryLength){
                    finalwordindex = dictionaryLength - 1;
                }

                SubAttack subAttack = new SubAttack(a.getAttackNumber(), new Range(initialwordindex, finalwordindex));

                // Adiciona um novo sub attack na lista de sub attacks
                a.addSubAttack(s.getKey(), subAttack);
                
                try {
                    // Inicia um novo sub attack
                    s.getValue().startSubAttack(
                        ciphertext, 
                        knowntext, 
                        initialwordindex, 
                        finalwordindex, 
                        attackNumber, 
                        (SlaveManager) this
                    );
                } catch (RemoteException e) {
                    Log.log("MASTER", "Slave nao encontrado.");
                    this.redirectSubAttack(a, subAttack);
                    this.removeSlave(s.getKey());
                    a.removeSubAttack(s.getKey());
                }

				i++;
			}

        }
        
        // Executa a thread responsável por administrar esse ataque
        this.executor.execute(new AttackManager(a));

		synchronized (a) {
			try {
				if(!a.isDone()) {
					a.wait();       // aguardando até que o ataque seja concluído
				}
			} catch(InterruptedException e) {
				Log.log("MASTER", "Ataque interrompido inesperadamente");
				return new Guess[0];
			}
		}
		
        Log.log("MASTER", "Finalizando ataque. Retornando resultados...");
        
        Guess[] ans = a.getGuesses();

        synchronized(atks) {
            atks.remove(a);   // removendo ataque da lista de ataques
        }

		return ans;
    }

    private class AttackManager implements Runnable {
        private Attack attack;
        
        public AttackManager(Attack attack) {
            this.attack = attack;
        }
        
        public void run() {
        	while(!attack.isDone()) {   				
                List<UUID> slavesToRemove = new ArrayList<UUID>();
                
                Map<UUID, Slave> slaves;
                Map<UUID, String> slavesNames;

                // Recuperando informações dos escravos
                synchronized (MasterImpl.this.slaves) {
                    slaves = MasterImpl.this.slaves;
                    slavesNames = MasterImpl.this.slavesNames;
                }

                // Recuperando lista de sub-ataques
        		Map<UUID, SubAttack> subAttacks = attack.getSubAttacks();

                // Analizando cada escravo individualmente
        		synchronized (subAttacks) {
        			for(Map.Entry<UUID, SubAttack> s : subAttacks.entrySet()) {
                        SubAttack subAttack = s.getValue();
                        UUID slaveKey = s.getKey();

                        // Atualizando intervalo de ataque caso um intervalo ja tenha sido concluído
        				if(subAttack.isDone()) {
                            Range next = subAttack.getNextRange();
                            if (next != null) {
                                try {
                                    // Iniciando novo sub-ataque com o próximo intervalo
                                    slaves.get(slaveKey).startSubAttack(
                                        this.attack.getCipherText(),
                                        this.attack.getKnownText(),
                                        next.getInit(),
                                        next.getLast(),
                                        this.attack.getAttackNumber(),
                                        (SlaveManager) MasterImpl.this
                                    );
                                } catch (RemoteException e) {
                                    Log.log("MASTER", "Remote exception");
                                    slavesToRemove.add(slaveKey);
                                    MasterImpl.this.redirectSubAttack(this.attack, subAttack);
                                }
                            }
                            continue;
                        }
                        
                        // verificando tempo desde o último checkpoint
                        long lastCheckpointTime = subAttack.getLastCheckpointTime();
                        long timeSinceLastCheckpoint = System.nanoTime() - lastCheckpointTime;
                        double seconds = (double) timeSinceLastCheckpoint / 1000000000.0;

                        // Marcando escravo para remoção caso tempo seja maior que 20 segundos
                        if(seconds > 20) {
                            Log.log("MASTER", "O Slave [" + slavesNames.get(slaveKey) + "] sera removido");

                            slavesToRemove.add(slaveKey);
                            
                            MasterImpl.this.redirectSubAttack(this.attack, subAttack);
                        }
        			}
        		}

                // Removendo escravos marcados
                try {
        		    for(UUID slaveKey : slavesToRemove) {
                        MasterImpl.this.removeSlave(slaveKey);
                        attack.removeSubAttack(slaveKey);
                    }
                } catch (RemoteException e) {
                    Log.log("MASTER", "Remote exception");
                    e.printStackTrace();
                }

                // Fazendo um intervalo de 5 segundos para não sobrecerregar a rede
        		try {
        			TimeUnit.MILLISECONDS.sleep(5 * 1000);
        		} catch (InterruptedException e) {
                    Log.log("MASTER", "Ataque interrompido");
                    e.printStackTrace();
        		} 
        	}
        }
    }
    
    private void redirectSubAttack(Attack attack, SubAttack subAttack) {
        try {
            int avaiableSlavesNumber;
            
            synchronized (slaves) {
                avaiableSlavesNumber = slaves.size();
            }

            if (avaiableSlavesNumber == 0) {
                Log.log("MASTER", "Nenhum escravo disponivel para redirecionamento");
                return;
            }

            Log.log("MASTER", "Redirecionando indices restantes do Slave...");

            // Recuperando intervaalo(s) ainda não avaliado(s) pelo escravo
            Range[] remainingRanges = subAttack.getRemainingRanges();

            // Enviando o(s) intervalo(s) para um escravo(s) aleatório(s)
            for(Range r : remainingRanges) {
                UUID slaveKey;

                synchronized (slaves) {
                    List<UUID> keys = new ArrayList<UUID>(this.slaves.keySet());
                    slaveKey = keys.get((new Random()).nextInt(keys.size()));
                }
                SubAttack chosenOne = attack.getSubAttack(slaveKey);
                chosenOne.addRange(r);
            }

            Log.log("MASTER", "Redirecionamento efetuado");
        } catch (Exception e) {
            Log.log("MASTER", "Redirecionamento falhou");
            Log.log("MASTER", "Encerrando programa");
            // e.printStackTrace();
            System.exit(0);
        }
    }
}