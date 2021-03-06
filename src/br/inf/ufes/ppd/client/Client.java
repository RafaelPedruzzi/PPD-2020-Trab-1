package br.inf.ufes.ppd.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import br.inf.ufes.ppd.Guess;
import br.inf.ufes.ppd.Master;
import br.inf.ufes.ppd.methods.Encrypt;
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
            
            Log.log("CLIENT", "Digite o nome do arquivo com o texto criptografado: ");
			String filename = s.nextLine();	
			File f = new File(filename);
			Boolean exists = f.exists() && !f.isDirectory(); // verificando se o arquivo passado existe (ou, pelo menos, se foi encontrado)
			
            Log.log("CLIENT", "Digite o trecho conhecido do texto: ");
			String tmp = s.nextLine();
			byte[] knowntext = tmp.getBytes("utf-8");

			s.close();
			
			// Buscando dicionário 
			ArrayList<String> dictionary = null;
			try {
				Scanner s2 = new Scanner(new File("/tmp/dictionary.txt"));
				// Scanner s2 = new Scanner(new File("../dictionary.txt"));
				dictionary = new ArrayList<String>();
				while (s2.hasNextLine()){
					dictionary.add(s2.nextLine());
				}
				s2.close();
			} catch (FileNotFoundException e1) {
				Log.log("SLAVE", "Erro: Dicionario nao encontrado.");
				System.exit(0);
			}

			// Lendo arquivo com a mensagem ou gerando nova menságem aleatória, caso ele não exista
			byte[] ciphertext;
			if (exists) {
				ciphertext = FileManager.readFile(filename);
			} else {
				Log.log("CLIENT", "Arquivo nao encontrado.");
				Log.log("CLIENT", "Gerando um arquivo aleatório...");
				Random r = new Random();
				int result = r.nextInt(100000-1000) + 1000;
				byte[] bytes = new byte[result];
				SecureRandom.getInstanceStrong().nextBytes(bytes);
				String word = dictionary.get(r.nextInt(80368));
				ciphertext = Encrypt.encrypt(word, bytes);
			}
            
            Log.log("CLIENT", "Chamando o mestre e iniciando o ataque...");
			Guess[] candidates = master.attack(ciphertext, knowntext);
			
			Log.log("CLIENT", "Ataque concluido.");
			
			// Imprimindo chaves encontradas na saída padrão e em arquivos
            Log.log("CLIENT", "Chaves candidatas encontradas:");

			for(Guess g : candidates) {
				Log.log("CLIENT", " - " + g.getKey());
				FileManager.saveFile(g.getKey() + ".msg", g.getMessage());
            }
			
            Log.log("CLIENT", "Ataque finalizado com sucesso");
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}

	}
	
	// Método equivalente a main que abstrai comunicação com o usuário
	public static Guess[] orderAttack(String filename, String kt){
		Log.log("CLIENT", "Iniciando o programa cliente...");

		try {
            Log.log("CLIENT", "Procurando referencia do mestre no registry...");
			Registry registry = LocateRegistry.getRegistry("localhost");
			Master master = (Master) registry.lookup("mestre");
			
            
			File f = new File(filename);
			Boolean exists = f.exists() && !f.isDirectory();

			byte[] knowntext = kt.getBytes("utf-8");

			ArrayList<String> dictionary = null;
			try {
				Scanner s2 = new Scanner(new File("/tmp/dictionary.txt"));
				// Scanner s2 = new Scanner(new File("../dictionary.txt"));
				dictionary = new ArrayList<String>();
				while (s2.hasNextLine()){
					dictionary.add(s2.nextLine());
				}
				s2.close();
			} catch (FileNotFoundException e1) {
				Log.log("SLAVE", "Erro: Dicionario nao encontrado.");
				System.exit(0);
				return new Guess[0];
			}

			byte[] ciphertext;
			if (exists) {
				ciphertext = FileManager.readFile(filename);
			} else {
				Log.log("CLIENT", "Arquivo nao encontrado.");
				Log.log("CLIENT", "Gerando um arquivo aleatório...");
				Random r = new Random();
				int result = r.nextInt(100000-1000) + 1000;
				byte[] bytes = new byte[result];
				SecureRandom.getInstanceStrong().nextBytes(bytes);
				String word = dictionary.get(r.nextInt(80368));
				ciphertext = Encrypt.encrypt(word, bytes);
			}
            
            Log.log("CLIENT", "Chamando o mestre e iniciando o ataque...");
			Guess[] candidates = master.attack(ciphertext, knowntext);
			
            Log.log("CLIENT", "Ataque concluido.");
            Log.log("CLIENT", "Chaves candidatas encontradas:");

			for(Guess g : candidates) {
				Log.log("CLIENT", " - " + g.getKey());
				// FileManager.saveFile(g.getKey() + ".msg", g.getMessage());
            }
			
			Log.log("CLIENT", "Ataque finalizado com sucesso");
			
			return candidates;
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
			System.exit(0);
			return new Guess[0];
		}
	}
}
