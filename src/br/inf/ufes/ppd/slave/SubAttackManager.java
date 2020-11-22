package br.inf.ufes.ppd.slave;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import br.inf.ufes.ppd.Guess;
import br.inf.ufes.ppd.SlaveManager;
import br.inf.ufes.ppd.methods.Log;
import br.inf.ufes.ppd.methods.Decrypt;

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
    }

    @Override
    public void run() {
        try {
            Log.log("SLAVE", "initial index: "+initialwordindex+" final index: "+finalwordindex);

            String word = dictionary.get((int) initialwordindex);
            byte[] decrypted = Decrypt.decrypt(word, this.ciphertext);
            
            if (this.Search(decrypted, this.knowntext)) {
                Guess guess = new Guess();
                guess.setKey(word);
                guess.setMessage(decrypted);
                this.masterRef.foundGuess(slaveKey,attackNumber,initialwordindex,guess);
            }

            this.currentindex = finalwordindex;

            this.timer.scheduleAtFixedRate(new CheckpointTask(), 0, 10 * 1000);

            for (long i = initialwordindex + 1; i <= finalwordindex; i++) {
                word = dictionary.get((int) i);
                decrypted = Decrypt.decrypt(word, this.ciphertext);

                if (this.Search(decrypted, this.knowntext)) {
                    Guess guess = new Guess();
                    guess.setKey(word);
                    guess.setMessage(decrypted);
                    this.masterRef.foundGuess(slaveKey, attackNumber, i, guess);
                }

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

    private boolean Search(byte[] src, byte[] pattern) {
        int c = src.length - pattern.length + 1;
        int j;
        for (int i = 0; i < c; i++) {
            if (src[i] != pattern[0])
                continue;
            for (j = pattern.length - 1; j >= 1 && src[i + j] == pattern[j]; j--)
                ;
            if (j == 0)
                return true;
        }
        return false;
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

