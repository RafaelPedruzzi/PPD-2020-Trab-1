package br.inf.ufes.ppd.slave;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import br.inf.ufes.ppd.interfaces.SlaveManager;
import br.inf.ufes.ppd.methods.Log;

public class SubAttackManager implements Runnable {
    private SlaveManager masterRef;
    private UUID slaveKey;
    private byte[] ciphertext;
    private byte[] knowntext;
    private long currentindex;
    private long initialwordindex;
    private long finalwordindex;
    private int attackNumber;
    private Timer timer;
    private ArrayList<String> dictionary;
    
    public SubAttackManager(SlaveManager masterRef, UUID slaveKey, byte[] ciphertext, byte[] knowntext, long initialwordindex, long finalwordindex, int attackNumber, ArrayList<String> dictionary) {
        this.masterRef = masterRef;
        this.ciphertext = ciphertext;
        this.knowntext = knowntext;
        this.initialwordindex = initialwordindex;
        this.currentindex = initialwordindex;
        this.finalwordindex = finalwordindex;
        this.attackNumber = attackNumber;
        this.slaveKey = slaveKey;
        this.dictionary = dictionary;
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new CheckpointTask(), 0, 10 * 1000);
    }

    @Override
    public void run() {
        try {
            System.out.println("initial: "+initialwordindex+" final: "+finalwordindex);
;            for (long i = initialwordindex; i <= finalwordindex; i++) {
                // pegar palavra i em dictionary
                if (i == initialwordindex || i == finalwordindex) {
                    System.out.println(this.dictionary.get((int)i));
                }
                // decryptografar cipher com i
                // procurar knowtext
                // se achou -> chama foundGuess
                this.currentindex = i;
            }
            this.timer.cancel();
            Log.log("SLAVE", "SubAttack concluido");
            synchronized(this.masterRef) {
                this.masterRef.checkpoint(this.slaveKey, this.attackNumber, this.currentindex);
            }
        } catch (Exception e) {
            Log.log("SLAVE", "Erro: SubAttack falhou");
            this.timer.cancel();
            System.out.println(e);
            // e.printStackTrace();
        }
    }

    public void setCurrentIndex(long currentindex) {
        this.currentindex = currentindex;
    }

    private class CheckpointTask extends TimerTask {
        @Override
        public void run() {
            SubAttackManager cp = SubAttackManager.this;
            try {
                synchronized(cp.masterRef) {
                    Log.log("SLAVE", "Realizando Checkpoint...");
                    cp.masterRef.checkpoint(cp.slaveKey, cp.attackNumber, cp.currentindex);
                    Log.log("SLAVE", "Checkpoint concluido.");
                }

            } catch (Exception e) {
                Log.log("SLAVE", "Erro: Checkpoint falhou");
                // e.printStackTrace();

                cp.timer.cancel();
            }
        }
    }

}

