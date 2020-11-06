package br.inf.ufes.ppd.slave;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import br.inf.ufes.ppd.interfaces.Master;
import br.inf.ufes.ppd.interfaces.Slave;
import br.inf.ufes.ppd.interfaces.SlaveManager;
import br.inf.ufes.ppd.methods.Log;

public class SlaveImpl implements Serializable, Slave {
    private UUID key;
    private String name;
    private String masterAddress;
    private Master masterRef;

    public SlaveImpl(String slaveName, String masterAddress) {
        this.key = java.util.UUID.randomUUID();
        this.name = slaveName;
        this.masterAddress = masterAddress;
        this.masterRef = null;
    }

    public static void main(String args[]) {
        Log.log("SLAVE", "Criando novo Slave...");

        // String masterAddress = args[0];
        String masterAddress = "localhost";

        // String slaveName = args[1];
        String slaveName = "Slave01";
        
        SlaveImpl slave = new SlaveImpl(slaveName, masterAddress);

        try {
            Log.log("SLAVE", "Criando referencia remota do Slave \"" + slave.getName() + "\"...");
            Slave slaveRef = (Slave) UnicastRemoteObject.exportObject(slave, 0);
            
            Log.log("SLAVE", "Buscando a referencia do mestre no registry...");
            Registry registry = LocateRegistry.getRegistry("localhost");
            Master master = (Master) registry.lookup("mestre");

            slave.setMasterRef(master);

            Log.log("SLAVE", "Registrando-se no mestre...");
            master.addSlave(slaveRef, slave.getName(), slave.getKey());
            Log.log("SLAVE", "Registro efetuado.");

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new SlaveRegister(master, slaveRef, slave.getName(), slave.getKey()), 30*1000, 30*1000);
        } catch (Exception e) {
            Log.log("SLAVE", "Erro: Falha de comunicação com o mestre.");
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
                Log.log("SLAVE", "Re-registrando " + this.n + "...");
                m.addSlave(s, n, k);
                Log.log("SLAVE", "Re-registro de " + this.n + " concluido.");
                
            } catch (Exception e) {
                Log.log("SLAVE", "Erro: Mestre não encontrado.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startSubAttack(byte[] ciphertext, byte[] knowntext, long initialwordindex, long finalwordindex,
            int attackNumber, SlaveManager callbackinterface) throws RemoteException {
        System.out.println("Printei!");
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
        return masterRef;
    }
}
