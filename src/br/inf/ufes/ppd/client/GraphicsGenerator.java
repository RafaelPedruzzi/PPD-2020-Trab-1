package br.inf.ufes.ppd.client;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import br.inf.ufes.ppd.interfaces.Guess;
import br.inf.ufes.ppd.methods.Log;

public class GraphicsGenerator {
    
    public static void main(String[] args) {
        String[] files = { "../tests/test1.txt.cipher", 
                           "../tests/test2.txt.cipher", 
                           "../tests/test3.txt.cipher", 
                           "../tests/test4.txt.cipher"
                         };
                         
        String[] knowntext = { "sit", "est", "non", "sem"};

        Log.log("GG", "Iniciando procedimento...");
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("dados.txt"), "utf-8"))) {
            for(int i = 0; i < files.length ; i++) {
                long start = System.nanoTime();
                Client.orderAttack(files[i], knowntext[i]);
                long end = System.nanoTime();

                long time = end - start;
                double seconds = (double) time / 1000000000.0;

                String result = files[i] + " - seconds: " + seconds;
                System.out.println(result);
                writer.write(result + "\n");
            }
        
        } catch (Exception e) {
            System.out.println("deu pau");
        }

        
    }
}
