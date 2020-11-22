package br.inf.ufes.ppd.master;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.inf.ufes.ppd.Guess;

public class Attack {
    private Map<UUID, SubAttack> subAttacks = new HashMap<UUID, SubAttack>();
	private List<Guess> guesses = new LinkedList<Guess>();
    private int attackNumber;
	private byte[] ciphertext;
	private byte[] knowntext;
    private boolean done;
    
    public Attack(byte[] ciphertext, byte[] knowntext) {
		this.ciphertext = ciphertext;
		this.knowntext = knowntext;
		this.done = false;
	}

    public void addSubAttack(UUID slave, SubAttack subAttack) {
		synchronized (subAttacks) {
			this.subAttacks.put(slave, subAttack);
		}
    }

	public void removeSubAttack(UUID slave) {
		synchronized (subAttacks) {
			this.subAttacks.remove(slave);
		}
	}

	public SubAttack getSubAttack(UUID slave){
		return this.subAttacks.get(slave);
	}

	public void addGuess(Guess g) {
		this.guesses.add(g);
	}

	public Guess[] getGuesses() {
		return guesses.toArray(new Guess[guesses.size()]);
	}

	public boolean isDone() {
		if (this.done) return true;
		
		synchronized (this.subAttacks) {
			for(SubAttack subAttack : this.subAttacks.values()) {
				if(subAttack.isDone() == false) {
					return false;
				}
			}
        }

        this.done = true;
        return true;
	}

	public Map<UUID, SubAttack> getSubAttacks(){
		return this.subAttacks;
	}

	public void setAttackNumber(int attackNumber) {
		this.attackNumber = attackNumber;
	}

	public int getAttackNumber() { 
		return this.attackNumber;
	}

	public byte[] getKnownText() {
		return this.knowntext;
	}

	public byte[] getCipherText() {
		return this.ciphertext;
	}

}
