package br.inf.ufes.ppd.master;

import java.util.LinkedList;
import java.util.List;

public class SubAttack {
    private int attackNumber;
	// private UUID attackerKey;
	private long startTime;
	private long lastCheckpointTime;
	private List<Range> ranges = new LinkedList<Range>();
	private int currentindex;
	private boolean done;
    // private Attack attack;
    
    public SubAttack(int attackNumber, Range range) {
		this.attackNumber = attackNumber;
		this.startTime = System.nanoTime();
        this.lastCheckpointTime = System.nanoTime();
        this.ranges.add(range);
		this.currentindex = range.getInit();
		this.done = false;
	}

	public void setCurrentIndex(int currentindex) {
		this.currentindex = currentindex;
		
		if(currentindex == ranges.get(0).getLast()) {
            this.ranges.remove(0);	

            if(this.ranges.isEmpty()) {
                this.done = true;
            }
		}
    }
    
    public void addRange(Range range) {
        ranges.add(range);
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

	public Range getRemainingRange() {
		return new Range(currentindex, ranges.get(0).getLast());
    }
    
	public boolean isDone() {
		return this.done;
    }
    
	public void setDone(boolean done) {
		this.done = done;
	}
}
