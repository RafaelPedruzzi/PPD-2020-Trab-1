package br.inf.ufes.ppd.methods;

import java.io.IOException;

import javax.crypto.*;
import javax.crypto.spec.*;
// import java.io.*;
// import java.security.*;

public class Encrypt {

	// public static void main(String[] args) {
	public static byte[] encrypt(String encrKey, byte[] message) {
		// args[0] È a chave a ser usada
		// args[1] È o nome do arquivo de entrada

		try {
			byte[] key = encrKey.getBytes();
			SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			System.out.println("message size (bytes) = " + message.length);

			byte[] encrypted = cipher.doFinal(message);

			return encrypted;

		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	public static void main(String[] args) {
		String filename = "message.txt";
		
		try {
			byte[] message = FileManager.readFile(filename);
	
			byte[] encrypted = encrypt("faena", message);
			FileManager.saveFile(filename + ".cipher", encrypted);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}