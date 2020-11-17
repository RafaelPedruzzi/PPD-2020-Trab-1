package br.inf.ufes.ppd.slave;

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

    public SubAttackManager(SlaveManager masterRef, UUID slaveKey, byte[] ciphertext, byte[] knowntext, long initialwordindex, long finalwordindex, int attackNumber) {
        this.masterRef = masterRef;
        this.ciphertext = ciphertext;
        this.knowntext = knowntext;
        this.initialwordindex = initialwordindex;
        this.currentindex = initialwordindex;
        this.finalwordindex = finalwordindex;
        this.attackNumber = attackNumber;
        this.slaveKey = slaveKey;

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new CheckpointTask(), 0, 10 * 1000);
    }

    @Override
    public void run() {
        for (long i = initialwordindex; i <= finalwordindex; i++) {
            // pegar palavra i em dictionary
            // decryptografar cipher com i
            // procurar knowtext
            // se achou -> chama foundGuess
        }
        //chamar checkpoint final
    }

    public void setCurrentIndex(long currentindex) {
        this.currentindex = currentindex;
    }

    private class CheckpointTask extends TimerTask {
        @Override
        public void run() {
            SubAttackManager cp = SubAttackManager.this;
            try {
                Log.log("SLAVE", "Realizando Checkpoint...");
                cp.masterRef.checkpoint(cp.slaveKey, cp.attackNumber, cp.currentindex);
                Log.log("SLAVE", "Checkpoint concluido.");

            } catch (Exception e) {
                Log.log("SLAVE", "Erro: Checkpoint falhou");
                // e.printStackTrace();

                cp.timer.cancel();
            }
        }
    }

}

