package br.inf.ufes.ppd.interfaces;




/**
 * Master.java
 */


import java.rmi.Remote;

public interface Master extends Remote, SlaveManager, Attacker {
	// o mestre é um SlaveManager e um Attacker
}
