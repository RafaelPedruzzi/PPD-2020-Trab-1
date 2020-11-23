package br.inf.ufes.ppd.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import br.inf.ufes.ppd.Guess;
import br.inf.ufes.ppd.methods.Decrypt;
import br.inf.ufes.ppd.methods.FileManager;
import br.inf.ufes.ppd.methods.Log;

public class Sequencial {
    public static void main(String[] args) {
        String[] files = { "../tests/test1.txt.cipher", 
                           "../tests/test2.txt.cipher", 
                           "../tests/test3.txt.cipher", 
                           "../tests/test4.txt.cipher"
                         };
                         
        String[] kts = { "sit", "est", "non", "sem"};

        ArrayList<String> dictionary = null;
        try {
            Scanner s2 = new Scanner(new File("/tmp/dictionary.txt"));
            dictionary = new ArrayList<String>();
            while (s2.hasNextLine()){
                dictionary.add(s2.nextLine());
            }
            s2.close();
        } catch (FileNotFoundException e1) {
            Log.log("Seq", "Erro: Dicionario nao encontrado.");
            System.exit(0);
        }
        
        long dicLen = 80368;
        String word;
        byte[] decrypted;

        Log.log("Seq", "Iniciando caso sequencial...");
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("dadosSeq.txt"), "utf-8"))) {

            for(int i = 3; i < files.length ; i++) {
                long start = System.nanoTime();

                List<Guess> guesses = new LinkedList<Guess>();

                File f = new File(files[i]);
                Boolean exists = f.exists() && !f.isDirectory();
                if (!exists) {
                    Log.log("Seq", "Erro: Arquivo não encontrado");
                    System.exit(0);
                }

                byte[] ciphertext = FileManager.readFile(files[i]);
                byte[] knowntext = kts[i].getBytes("utf-8");

                for (long j = 0; j <= dicLen-1; j++) {
                    word = dictionary.get((int) i);
                    decrypted = Decrypt.decrypt(word, ciphertext);

                    if (Search(decrypted, knowntext)) {
                        Log.log("Seq", "Eu found um guess");
                        Guess guess = new Guess();
                        guess.setKey(word);
                        guess.setMessage(decrypted);
                        guesses.add(guess);
                    }
                    if( j % 10000 == 0) {
                        System.out.print(".");
                    }
                }

                long end = System.nanoTime();

                long time = end - start;
                double seconds = (double) time / 1000000000.0;

                String result = files[i] + " - seconds: " + seconds;
                Log.log("Seq", "Ataque concluido.");
                Log.log("Seq", result);
                writer.write(result + "\n");

                Log.log("Seq", "Chaves candidatas encontradas:");
                for(Guess g : guesses) {
                    Log.log("Seq", " - " + g.getKey());
                    // FileManager.saveFile(g.getKey() + ".msg", g.getMessage());
                }
            }
        
        } catch (Exception e) {
            Log.log("Seq", "deu pau...");
        }
        
    }

    private static boolean Search(byte[] src, byte[] pattern) {
        int c = src.length - pattern.length + 1;
        int j;
        for (int i = 0; i < c; i++) {
            if (src[i] != pattern[0])
                continue;
            for (j = pattern.length - 1; j >= 1 && src[i + j] == pattern[j]; j--)
                ;
            if (j == 0)
                return true;
        }
        return false;
    }
}
