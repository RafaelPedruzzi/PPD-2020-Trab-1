package br.inf.ufes.ppd.methods;

import javax.crypto.*;
import javax.crypto.spec.*;
// import java.io.*;
// import java.security.*;

public class Encrypt {

	// public static void main(String[] args) {
	public static void encrypt(String encrKey, String filename){
		// args[0] È a chave a ser usada
		// args[1] È o nome do arquivo de entrada

    	try {
			byte[] key = encrKey.getBytes();
			SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			byte [] message = FileManager.readFile(filename);
			System.out.println("message size (bytes) = "+message.length);

			byte[] encrypted = cipher.doFinal(message);

			FileManager.saveFile(filename+".cipher", encrypted);

		} catch (Exception e) {
			// don't try this at home
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		encrypt("faena", "message.txt");
	}
}