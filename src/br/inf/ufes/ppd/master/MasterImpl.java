package br.inf.ufes.ppd.master;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import br.inf.ufes.ppd.interfaces.*;
import br.inf.ufes.ppd.methods.Log;

public class MasterImpl implements Master {

    private List<Attack> atks = new ArrayList<Attack>();
    private Map<UUID, Slave> slaves = new HashMap<UUID, Slave>();
    private ExecutorService executor = Executors.newFixedThreadPool(8);
	private int dictionaryLength = 80368;
	// private SlaveManager slaveCallback;

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            Registry registry = LocateRegistry.createRegistry(1099);

            Log.log("MASTER", "Criando referencia remota do mestre...");
            MasterImpl master = new MasterImpl();
            Master masterRef = (Master) UnicastRemoteObject.exportObject(master, 0);

            Log.log("MASTER", "Fazendo o binding do mestre no registry...");
            registry.rebind("mestre", masterRef);
            Log.log("MASTER", "Binding concluido. Master's ready!");

        } catch (Exception e) {
            Log.log("MASTER", "Erro: Mestre não pode ser registrado: ");
            e.printStackTrace();
        }
    }

    @Override
    public void addSlave(Slave s, String slaveName, UUID slaveKey) throws RemoteException {
        Log.log("MASTER", "Pedido de (re)registro de \"" + slaveName + "\" UUID: " + slaveKey);

        synchronized (slaves) {
            slaves.put(slaveKey, s);
        }

        // s.startSubAttack(new byte[5], new byte[5], 0, 0, 0, this);

        Log.log("MASTER", "Registro de " + slaveName + " concluido com sucesso.");
    }

    @Override
    public void removeSlave(UUID slaveKey) throws RemoteException {
        synchronized (slaves) {
            slaves.remove(slaveKey);
        }
    }

    @Override
    public void foundGuess(UUID slaveKey, int attackNumber, long currentindex, Guess currentguess) throws RemoteException {
        // TODO
    }

    @Override
    public void checkpoint(UUID slaveKey, int attackNumber, long currentindex) throws RemoteException {
        // TODO
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
			for (Map.Entry<UUID, Slave> s : slaves.entrySet()) {

				// nextSubAttackID++;

				int initialwordindex = (dictionaryLength / avaiableSlavesNumber) * i;
                int finalwordindex   = (dictionaryLength / avaiableSlavesNumber) * (i + 1);

                SubAttack subAttack = new SubAttack(a.getAttackNumber(), new Range(initialwordindex, finalwordindex));

                a.addSubAttack(s.getKey(), subAttack);

                s.getValue().startSubAttack(
                    ciphertext, 
                    knowntext, 
                    initialwordindex, 
                    finalwordindex, 
                    attackNumber, 
                    this // (SlaveImpl) s.getMasterRef()
                );

				// s.getValue().addCurrentSubAttackJob(nextSubAttackID);
				i++;
			}

        }
        
        this.executor.execute(new AttackManager(a));

		// synchronized (attack) {
		// 	try
		// 	{
		// 		if(!attack.isDone())
		// 		{
		// 			attack.wait();
		// 		}
				
		// 	}
		// 	catch(InterruptedException e)
		// 	{
		// 		System.err.println("[M] Attack main thread interrupted. Returning 0 guess...");
		// 		return new Guess[0];
		// 	}
		// }
		
		
		// System.err.println("[M] Client request has finished. Returning " + attack.getFoundGuesses().size() + " found guess(es)");
		
		// Guess[] foundGuesses = new Guess[attack.getFoundGuesses().size()];
		// return attack.getFoundGuesses().toArray(foundGuesses);

        Log.log("MASTER", "Finalizando ataque. Retornando resultados...");

        return ;
    }

    private class AttackManager implements Runnable {
        private Attack attack;
        
        public AttackManager(Attack attack) {
        	this.attack = attack;
        }
        
        public void run() {
        	while(!attack.isDone()) {   				
        		List<UUID> attackersToRemove = new ArrayList<UUID>();

        		Map<UUID, SubAttack> subAttacks = attack.getSubAttacks();

        		synchronized (subAttacks) {
        			for(Map.Entry<UUID, SubAttack> s : subAttacks.entrySet()) {
                        SubAttack subAttack = s.getValue();
                        UUID slave = s.getKey();

        				if(subAttack.isDone()) continue;
        				
                        long lastCheckpointTime = subAttack.getLastCheckpointTime();
                        long timeSinceLastCheckpoint = System.nanoTime() - lastCheckpointTime;
                        double seconds = (double) timeSinceLastCheckpoint / 1000000000.0;

                        if(seconds > 20) {
                            Log.log("MASTER", "O Slave [" + slave + "] sera removido");

                            
                            attackersToRemove.add(slave);
                            // attack.redirect(subAttack);
                        }
        				
        			}
        		}

        		// removing attackers marked for removal
        		for(UUID slaveKey : attackersToRemove) {
        			removeSlave(slaveKey);
        		}

        		try {
        			TimeUnit.MILLISECONDS.sleep(5 * 1000);
        		} catch (InterruptedException e) {
                    Log.log("MASTER", "Ataque interrompido");
                    e.printStackTrace();
        		} 
        	}
        }
	}

}