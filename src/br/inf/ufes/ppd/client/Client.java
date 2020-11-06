package br.inf.ufes.ppd.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import br.inf.ufes.ppd.interfaces.Guess;
import br.inf.ufes.ppd.interfaces.Master;
import br.inf.ufes.ppd.methods.FileManager;
import br.inf.ufes.ppd.methods.Log;

public class Client {

    public static void main(String[] args) {
        Log.log("CLIENT", "Iniciando o programa cliente...");

		try {
            Log.log("CLIENT", "Procurando referencia do mestre no registry...");
			Registry registry = LocateRegistry.getRegistry("localhost");
			Master master = (Master) registry.lookup("mestre");
			
            Scanner s = new Scanner(System.in);
            
            Log.log("CLIENT", "Digite o nome do arquivo com cipher text: ");
			// String filename = s.nextLine();	
			// byte[] ciphertext = FileManager.readFile(filename);
			byte[] ciphertext = FileManager.readFile("message.txt.cipher");
			
            Log.log("CLIENT", "Digite o known text: ");
			// String tmp = s.nextLine();
			// byte[] knowntext = tmp.getBytes("utf-8");
			byte[] knowntext = "world".getBytes("utf-8");

            s.close();
            
            Log.log("CLIENT", "Chamando o mestre para iniciar o ataque...");
			Guess[] candidates = master.attack(ciphertext, knowntext);
			
			for(Guess g : candidates) {
				FileManager.saveFile(g.getKey() + ".msg", g.getMessage());
            }
			
            Log.log("CLIENT", "Ataque finalizado!");
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}

    }
}
