package br.inf.ufes.ppd;

import java.rmi.Remote;

/**
 * Master.java
 */


public interface Master extends Remote, SlaveManager, Attacker {
	// o mestre é um SlaveManager e um Attacker
}
