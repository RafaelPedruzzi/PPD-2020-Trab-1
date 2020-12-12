package br.inf.ufes.ppd.slave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.inf.ufes.ppd.Master;
import br.inf.ufes.ppd.Slave;
import br.inf.ufes.ppd.SlaveManager;
import br.inf.ufes.ppd.methods.Log;

public class SlaveImpl implements Serializable, Slave {
    private UUID key;
    private String name;
    private String masterAddress;
    private Master masterRef;
    private ExecutorService executor = Executors.newFixedThreadPool(8);
    public Timer timer;
    private ArrayList<String> dictionary;

    public SlaveImpl(String slaveName, String masterAddress, ArrayList<String> dictionary) {
        this.key = java.util.UUID.randomUUID();
        this.name = slaveName;
        this.masterAddress = masterAddress;
        this.masterRef = null;
        this.dictionary = dictionary;
    }

    public static void main(String args[]) {
        Log.log("SLAVE", "Criando novo Slave...");

        Scanner s = new Scanner(System.in);

        Log.log("SLAVE", "Digite o master address: ");
        String masterAddress = s.next();

        Log.log("SLAVE", "Digite o nome do Slave: ");
        String slaveName = s.next();

        s.close();

        // Recuperando dicionário
        ArrayList<String> dictionary = null;
        try {
            Scanner s2 = new Scanner(new File("/tmp/dictionary.txt"));
            // Scanner s2 = new Scanner(new File("../dictionary.txt"));
            dictionary = new ArrayList<String>();
            while (s2.hasNextLine()){
                dictionary.add(s2.nextLine());
            }
            s2.close();
        } catch (FileNotFoundException e1) {
            Log.log("SLAVE", "Erro: Dicionario nao encontrado.");
            System.exit(0);
        }

        SlaveImpl slave = new SlaveImpl(slaveName, masterAddress, dictionary);

        try {
            Log.log("SLAVE", "Criando referencia remota do Slave \"" + slave.getName() + "\"...");
            UnicastRemoteObject.exportObject(slave, 0);

            // Buscando referência do mestre
            findMaster(slave);

        } catch (Exception e) {
            Log.log("SLAVE", "Erro: Falha de comunicação com o mestre.");
            // e.printStackTrace();
        }
    }

    // Metodo que encontra a referência para o mestre
    public static boolean findMaster(SlaveImpl s) {
        try {
            Log.log("SLAVE", "Buscando a referencia do mestre no registry...");
            Registry registry = LocateRegistry.getRegistry(s.getMasterAddress());
            Master master = (Master) registry.lookup("mestre");

            s.setMasterRef(master); // salvando/atualizando referência do mestre

            Log.log("SLAVE", "Registrando-se no mestre...");
            master.addSlave(s, s.getName(), s.getKey());
            Log.log("SLAVE", "Registro efetuado.");

            // Criando uma thread para realizar re-registro periódico
            s.timer = new Timer();
            s.timer.scheduleAtFixedRate(new SlaveRegister(s.getMasterRef(), s, s.getName(), s.getKey(), s.timer),
                    30 * 1000, 30 * 1000);

            return true;
        } catch (NotBoundException e) {
            Log.log("SLAVE", "Erro: Mestre nao encontrado.");
            // e.printStackTrace();
            return false;
        } catch (Exception e) {
            Log.log("SLAVE", "Erro");
            e.printStackTrace();
            return false;
        }
    }

    // Classe auxiliar que realiza o re-registro periódico
    private static class SlaveRegister extends TimerTask {
        private Master m;
        private SlaveImpl s;
        private String n;
        private UUID k;
        private Timer t;

        public SlaveRegister(Master m, SlaveImpl s, String n, UUID k, Timer t) {
            this.m = m;
            this.s = s;
            this.n = n;
            this.k = k;
            this.t = t;
        }

        @Override
        public void run() {
            try {
                Log.log("SLAVE", "Re-registrando " + this.n + "...");
                m.addSlave(s, n, k);
                Log.log("SLAVE", "Re-registro de " + this.n + " concluido.");

            } catch (Exception e) {
                Log.log("SLAVE", "Erro: Mestre não encontrado.");

                Log.log("SLAVE", "Buscando novo mestre...");
                while (!findMaster(s)) { // findMaster retorna False caso mestre não seja encontrado
                    try {
                        Log.log("SLAVE", "Mestre não encontrado. Esperando 15 segundos...");
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e1) {
                        Log.log("SLAVE", "Timer com insonia. Buscando mestre novamente...");
                    }
                }
                t.cancel();
            }
        }
    }

    @Override
    public void startSubAttack(byte[] ciphertext, byte[] knowntext, long initialwordindex, long finalwordindex,
            int attackNumber, SlaveManager callbackinterface) throws RemoteException {
        Log.log("SLAVE", "Iniciando novo sub-attack...");

        // Redirecionando sub attack para um SubAttackManager
        SubAttackManager sam = new SubAttackManager(
            callbackinterface, 
            this.key, 
            ciphertext, 
            knowntext, 
            initialwordindex, 
            finalwordindex, 
            attackNumber,
            this.dictionary
        );
		
		executor.execute(sam); // executando SubAttackManager
    }

    public UUID getKey() {
        return this.key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMasterRef(Master master) {
        this.masterRef = master;
    }

    public Master getMasterRef() {
        return this.masterRef;
    }

    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public String getMasterAddress() {
        return this.masterAddress;
    }
}
