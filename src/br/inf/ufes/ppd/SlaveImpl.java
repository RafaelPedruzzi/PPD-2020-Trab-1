package br.inf.ufes.ppd;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
            Registry registry = LocateRegistry.getRegistry("localhost");
            Master master = (Master) registry.lookup("mestre");

            slave.setMaster(master);
            slave.setName(slaveName);
            slave.setKey(java.util.UUID.randomUUID());

            System.out.println("Mestre nulo? " + (master == null));

            System.out.println("Registrando-se no mestre...");
            master.addSlave(slave, slaveName, slaveKey);
            System.out.println("Registro completo.");
        } catch (Exception e) {
            System.out.println("Erro: Falha de comunicação com o mestre: ");
            e.printStackTrace();
        }
    }

    @Override
    public void startSubAttack(byte[] ciphertext, byte[] knowntext, long initialwordindex, long finalwordindex,
            int attackNumber, SlaveManager callbackinterface) throws RemoteException {
        // TODO Auto-generated method stub

    }

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
