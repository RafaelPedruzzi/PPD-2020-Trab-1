package br.inf.ufes.ppd;





import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
// import java.security.*;

public class Encrypt {

	private static byte[] readFile(String filename) throws IOException
	{
		File file = new File(filename);
	    InputStream is = new FileInputStream(file);
        long length = file.length();
        // creates array (assumes file length<Integer.MAX_VALUE)
        byte[] data = new byte[(int)length];
        int offset = 0; int count = 0;
        while ((offset < data.length) &&
        		(count=is.read(data, offset, data.length-offset)) >= 0) {
            offset += count;
        }
        is.close();
        return data;
	}

	private static void saveFile(String filename, byte[] data) throws IOException
	{
		FileOutputStream out = new FileOutputStream(filename);
	    out.write(data);
	    out.close();
	}

	// public static void main(String[] args) {
	public static void encrypt(String encrKey, String filename){
		// args[0] È a chave a ser usada
		// args[1] È o nome do arquivo de entrada

    	try {
			byte[] key = encrKey.getBytes();
			SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");

			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			byte [] message = readFile(filename);
			System.out.println("message size (bytes) = "+message.length);

			byte[] encrypted = cipher.doFinal(message);

			saveFile(filename+".cipher", encrypted);

		} catch (Exception e) {
			// don't try this at home
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		encrypt("faena", "message.txt");
	}
}
