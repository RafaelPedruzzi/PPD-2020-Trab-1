package br.inf.ufes.ppd;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MasterImpl implements Master {

    private List<Slave> slaves = new java.util.ArrayList<Slave>();

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            Registry registry = LocateRegistry.createRegistry(1099);

            MasterImpl master = new MasterImpl();
            Master objref = (Master) UnicastRemoteObject.exportObject(master, 0);
            // Registry registry = LocateRegistry.getRegistry();

            System.err.println("Master server binding...");

            registry.rebind("mestre", objref);
            System.err.println("Master server ready");

        } catch (Exception e) {
            System.err.println("Erro: Mestre não pode ser registrado: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void addSlave(Slave s, String slaveName, UUID slavekey) throws RemoteException {
        System.out.println("Pedido de registro de " + slaveName);
        System.out.println("UUID = " + slavekey);
        synchronized (slaves) {
            slaves.add(s);
        }
        System.out.println("Registro concluido com sucesso.");
    }

    @Override
    public void removeSlave(UUID slaveKey) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void foundGuess(UUID slaveKey, int attackNumber, long currentindex, Guess currentguess)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkpoint(UUID slaveKey, int attackNumber, long currentindex) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public Guess[] attack(byte[] ciphertext, byte[] knowntext) throws RemoteException {
        List<Guess> candidates = new LinkedList<Guess>();

        Guess g = new Guess();
        g.setKey("1-biscoito");
        
        Guess g2 = new Guess();
        g2.setKey("2-zambon");
        
        try {
            g.setMessage("Eh biscoito e nao bolacha!".getBytes("utf-8"));
            g2.setMessage("zambon god".getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        candidates.add(g);
        candidates.add(g2);

        return candidates.toArray(new Guess[candidates.size()]);
    }
}
