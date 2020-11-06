package br.inf.ufes.ppd;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class SlaveImpl implements Serializable, Slave {
    /**
     *
     */
    private static final long serialVersionUID = -1460948498035788191L;
    private Master master;
    private String name;
    private java.util.UUID key;

    public static void main(String[] args){
        SlaveImpl slave = new SlaveImpl();

        String slaveName = "Slave01";
        java.util.UUID slaveKey = java.util.UUID.randomUUID();

        System.out.println("Nome: " + slaveName);
        System.out.println("UUID: " + slaveKey);
        
        try {
            Slave slvref = (Slave) UnicastRemoteObject.exportObject(slave, 0);

            Registry registry = LocateRegistry.getRegistry("localhost");
            Master master = (Master) registry.lookup("mestre");

            slave.setMaster(master);
            slave.setName(slaveName);
            slave.setKey(java.util.UUID.randomUUID());

            System.out.println("Mestre nulo? " + (master == null));

            System.out.println("Registrando-se no mestre...");
            master.addSlave(slvref, slaveName, slaveKey);
            System.out.println("Registro completo.");

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new SlaveRegister(master, (Slave)slvref, slaveName, slaveKey), 30*1000, 30*1000);
        } catch (Exception e) {
            System.out.println("Erro: Falha de comunicação com o mestre: ");
            e.printStackTrace();
        }
    }

    private static class SlaveRegister extends TimerTask {
        private Master m;
        private Slave s;
        private String n;
        private UUID k;

        public SlaveRegister(Master m, Slave s, String n, UUID k) {
            this.m = m;
            this.s = s;
            this.n = n;
            this.k = k;
        }
        @Override
        public void run() {
            try {
                System.out.println("Re-registrando...");
                m.addSlave(s, n, k);
                System.out.println("Re-registro concluido.");
            } catch (Exception e) {
                System.out.println("Erro: Mestre nao encontrado: ");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startSubAttack(byte[] ciphertext, byte[] knowntext, long initialwordindex, long finalwordindex,
            int attackNumber, SlaveManager callbackinterface) throws RemoteException {
        System.out.println("Printei!");

    }

    // class SendCheckpoint extends TimerTask {
    //     @Override
    //     public void run() {
    //         try {
    //             master.checkpoint(key,0,1234);
    //         } catch (Exception e) {
    //             System.out.println("Erro: Falha ao enviar checkpoint: ");
    //             e.printStackTrace();
    //         }
    //     }
    // }

    public java.util.UUID getKey() {
		return this.key;
	}

	public void setKey(java.util.UUID key) {
		this.key = key;
    }

	public String getName() {
		return name;
	}
    
	public void setName(String name) {
		this.name = name;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public Master getMaster() {
        return master;
    }
}
