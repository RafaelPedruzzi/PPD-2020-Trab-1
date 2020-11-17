package br.inf.ufes.ppd.master;

import java.util.LinkedList;
import java.util.List;

public class SubAttack {
    private int attackNumber;
	// private UUID attackerKey;
	private long startTime;
	private long lastCheckpointTime;
	private List<Range> ranges = new LinkedList<Range>();
	private long currentindex;
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

	public void setCurrentIndex(long currentindex) {
		this.currentindex = currentindex;
		
		synchronized (this.ranges) {
			if(currentindex == ranges.get(0).getLast()) {
            	this.ranges.remove(0);	

            	if(this.ranges.isEmpty()) {
                	this.done = true;
            	}
			}
		}
	}
	
	public Range getNextRange() {
		synchronized (this.ranges) {
			if (this.ranges.isEmpty()) return null;
			this.ranges.remove(0);
			if (this.ranges.isEmpty()) return null;
			this.done = false;
			return this.ranges.get(0);
		}
	}

	public Range[] getRemainingRanges() {
		Range[] remaining;
		synchronized (this.ranges) {
			remaining = new Range[this.ranges.size()];
			remaining[0] = new Range(currentindex +1, this.ranges.get(0).getLast()) ;
			int i = 1;
			for(Range r : this.ranges) {
				remaining[i] = r;
				i++;
			}
		}
		return remaining;
    }
    
    public void addRange(Range range) {
		synchronized (this.ranges) {
			ranges.add(range);
		}
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
