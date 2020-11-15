package br.inf.ufes.ppd.master;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.inf.ufes.ppd.interfaces.Guess;

public class Attack {
    private Map<UUID, SubAttack> subAttacks = new HashMap<UUID, SubAttack>();
	private List<Guess> guesses = new LinkedList<Guess>();
    private int attackNumber;
	private byte[] ciphertext;
	private byte[] knowntext;
    private boolean done;
    
    public Attack(int attackNumber, byte[] ciphertext, byte[] knowntext) {
		this.attackNumber = attackNumber;
		this.ciphertext = ciphertext;
		this.knowntext = knowntext;
		this.done = false;
	}

    public void addSubAttack(UUID slave, SubAttack subAttack) {
		synchronized (subAttacks) {
			this.subAttacks.put(slave, subAttack);
		}
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
}
