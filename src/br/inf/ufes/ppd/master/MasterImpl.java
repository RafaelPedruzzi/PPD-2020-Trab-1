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

import br.inf.ufes.ppd.interfaces.*;
import br.inf.ufes.ppd.methods.Log;

public class MasterImpl implements Master {

    private List<Attack> atks = new ArrayList<Attack>();
    private Map<UUID, Slave> slaves = new HashMap<UUID, Slave>();
    private ExecutorService executor = Executors.newFixedThreadPool(8);

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

    public static Runnable t1 = new Runnable() {
        public void run() {

        }
    };

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
        Log.log("MASTER", "Requisicao de ataque recebida. Iniciando ataque...");

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

        List<Guess> candidates = new ArrayList<Guess>();

        synchronized (guesses) {
            guesses.add(g);
            guesses.add(g2);
            candidates.addAll(guesses);
        }

        // System.out.println("FIREEEE!!!");
        Log.log("MASTER", "Finalizando ataque. Retornando resultados...");

        return candidates.toArray(new Guess[candidates.size()]);
    }
}