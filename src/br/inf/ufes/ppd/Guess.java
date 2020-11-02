package br.inf.ufes.ppd;



/**
 * Guess.java
 */


import java.io.Serializable;

public class Guess implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -965380957676557066L;

	private String key;
	// chave candidata

	private byte[] message;
	// mensagem decriptografada com a chave candidata

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public byte[] getMessage() {
		return message;
	}
	public void setMessage(byte[] message) {
		this.message = message;
	}

}
