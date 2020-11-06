package br.inf.ufes.ppd.methods;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.*;

public class Decrypt {

	// public static void main(String[] args) {
	public static void decrypt(String encrKey, String fileName) {
		// args[0] e a chave a ser usada
		// args[1] e o nome do arquivo de entrada

		try {

			byte[] key = encrKey.getBytes();
			SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

			byte[] message = FileManager.readFile(fileName);
			System.out.println("message size (bytes) = "+ message.length);

			byte[] decrypted = cipher.doFinal(message);

			FileManager.saveFile(encrKey+".msg", decrypted);

		} catch (javax.crypto.BadPaddingException e) {
			// essa excecao e jogada quando a senha esta incorreta
			// porem nao quer dizer que a senha esta correta se nao jogar essa excecao
			System.out.println("Senha invalida.");

		} catch (Exception e) {
			//dont try this at home
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		decrypt("faena", "message.txt.cipher");
	}
}