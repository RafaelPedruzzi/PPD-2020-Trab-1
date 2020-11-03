package br.inf.ufes.ppd;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

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
	
    public static void main(String[] args) {

		// String host = (args.length < 1) ? null : args[0];

		try {
			Registry registry = LocateRegistry.getRegistry("localhost");
			// 2804:7f2:595:6bfa:590f:4e52:d8cb:946f
			// 179.178.243.233
			Master master = (Master) registry.lookup("mestre");
			
			Scanner s = new Scanner(System.in);

			// System.out.println("Enter cipher text file name: ");
			// String filename = s.nextLine();	
			// byte[] ciphertext = readFile(filename);
			byte[] ciphertext = readFile("message.txt.cipher");
			
            // System.out.println("Enter known text: ");
			// String tmp = s.nextLine();
			// byte[] knowntext = tmp.getBytes("utf-8");
			byte[] knowntext = "world".getBytes("utf-8");

            s.close();
            
            // Master master = new MasterImpl();
            
			Guess[] candidates = master.attack(ciphertext, knowntext);
			
			for(Guess g : candidates) {
				saveFile(g.getKey() + ".msg", g.getMessage());
            }

		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
