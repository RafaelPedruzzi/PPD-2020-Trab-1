package br.inf.ufes.ppd.master;

import java.util.LinkedList;
import java.util.List;

public class SubAttack {
    private int attackNumber;
	// private UUID attackerKey;
	private long startTime;
	private long lastCheckpointTime;
	private List<Range> ranges = new LinkedList<Range>(); // listas de intervalos de índices a serem avaliados pelo escravo em questão
	private long currentindex;
	private boolean done;
    // private Attack attack;
    
    public SubAttack(int attackNumber, Range range) {
		this.attackNumber = attackNumber;
		this.startTime = System.nanoTime();
        this.lastCheckpointTime = System.nanoTime();
        this.ranges.add(range);
		this.currentindex = range.getInit() -1;
		this.done = false;
	}

	public void setCurrentIndex(long currentindex) {
		this.currentindex = currentindex;
		
		// Verificando se o sub-ataque atual foi concluído
		synchronized (this.ranges) {
			if(currentindex == ranges.get(0).getLast()) { // recuperando índice final do intervalo atual
            	this.ranges.remove(0); // removendo intervalo atual

				// Marcando sub-ataque como concluído caso não tenham mais intervalos
            	if(this.ranges.isEmpty()) {
                	this.done = true;
            	}
			}
		}
	}
	
	// Método para recuperar o próximo intervalo a ser avaliado
	public Range getNextRange() {
		synchronized (this.ranges) {
			if (this.ranges.isEmpty()) return null;

			// Removendo último intervalo (já avaliado)
			this.ranges.remove(0);
			if (this.ranges.isEmpty()) return null;

			// Se lista de intervalos não está vazia, marcando sub-ataque como não encerrado
			this.done = false;

			// Retornando próximo intervalo
			return this.ranges.get(0);
		}
	}

	// Método para recuperar índices ainda não avaliados
	// Utilizado para redirecionar sub-ataque
	public Range[] getRemainingRanges() {
		Range[] remaining;
		synchronized (this.ranges) {
			// Obtendo índices ainda não avaliados do sub-ataque atual (desde o último checkpoint)
			int size = this.ranges.size();
			remaining = new Range[size];
			remaining[0] = new Range(currentindex + 1, this.ranges.get(0).getLast()) ;

			// Adicionando demais sub-intervalos, se existirem
			if (size > 1) {
				int i = 1;
				for(Range r : this.ranges) {
					remaining[i] = r;
					i++;
				}
			}
		}
		return remaining;
    }
	
	// Adiciona intervalo para ser avaliado
	// Normalmente usado para receber um redirecionamento
    public void addRange(Range range) {
		synchronized (this.ranges) {
			ranges.add(range);
		}
		this.done = false;
    }
    
    public int getAttackerNumber() {
		return this.attackNumber;
    }
    
	public void setAttackerNumber(int attackNumber) {
		this.attackNumber = attackNumber;
    }
    
	public long getStartTime() {
		return this.startTime;
    }
    
	public long getLastCheckpointTime() {
		return this.lastCheckpointTime;
    }

	public void setLastCheckpointTime(long lastCheckpointTime) {
		this.lastCheckpointTime = lastCheckpointTime;
    }

	public boolean isDone() {
		return this.done;
    }
    
	public void setDone(boolean done) {
		this.done = done;
	}
}
