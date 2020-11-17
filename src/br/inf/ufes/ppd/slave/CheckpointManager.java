package br.inf.ufes.ppd.slave;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import br.inf.ufes.ppd.interfaces.SlaveManager;

public class CheckpointManager {
    private SlaveManager masterRef;
    private UUID slaveKey;
    private int attackNumber;
    private long currentIndex;
    private Timer timer;

    public CheckpointManager(SlaveManager masterRef, UUID slaveKey, int attackNumber, long currentIndex) {
        this.masterRef = masterRef;
        this.slaveKey = slaveKey;
        this.attackNumber = attackNumber;
        this.currentIndex = currentIndex;

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new CheckpointTask(), 0, 10 * 1000);
    }

    private static class CheckpointTask extends TimerTask {
        @Override
        public void run() {
            try {
                Log.log("SLAVE", "Re-registrando " + this.n + "...");
                m.addSlave(s, n, k);
                Log.log("SLAVE", "Re-registro de " + this.n + " concluido.");

            } catch (Exception e) {
                Log.log("SLAVE", "Erro: Mestre não encontrado.");
                // e.printStackTrace();

                Log.log("SLAVE", "Buscando novo mestre...");
                while (!achaMestre(s)) {
                    try {
                        Log.log("SLAVE", "Mestre não encontrado. Esperando 15 segundos...");
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e1) {
                        Log.log("SLAVE", "Erro: Timer com insonia.");
                        e1.printStackTrace();
                    }
                }
                t.cancel();
            }
        }
    }
}
