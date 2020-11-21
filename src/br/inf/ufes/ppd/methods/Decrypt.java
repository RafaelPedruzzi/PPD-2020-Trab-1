package br.inf.ufes.ppd.methods;

import java.io.IOException;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.*;

public class Decrypt {

	// public static void main(String[] args) {
	public static byte[] decrypt(String encrKey, byte[] message) {
		// args[0] e a chave a ser usada
		// args[1] e o nome do arquivo de entrada

		try {

			byte[] key = encrKey.getBytes();
			SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

			byte[] decrypted = cipher.doFinal(message);

			return decrypted;

		} catch (javax.crypto.BadPaddingException e) {
			// essa excecao e jogada quando a senha esta incorreta
			// porem nao quer dizer que a senha esta correta se nao jogar essa excecao
			// System.out.println("Senha invalida.");
			
			return new byte[0];

		} catch (Exception e) {
			// dont try this at home
			e.printStackTrace();

			return new byte[0];
		}
	}

	public static void main(String[] args) {
		try {
			byte[] message = FileManager.readFile("message.txt.cipher");
			System.out.println("message size (bytes) = " + message.length);

			byte[] dec = decrypt("faena", message);

			FileManager.saveFile("faena" + ".msg", dec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}