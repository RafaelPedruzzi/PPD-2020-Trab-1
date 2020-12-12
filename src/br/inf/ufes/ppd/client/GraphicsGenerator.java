package br.inf.ufes.ppd.client;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import br.inf.ufes.ppd.Guess;
import br.inf.ufes.ppd.methods.Log;

public class GraphicsGenerator {
    
    public static void main(String[] args) {
        // Arquvos de teste
        String[] files = { "../tests/test1.txt.cipher", // 50 kb
                           "../tests/test2.txt.cipher", // 100 kb
                           "../tests/test3.txt.cipher", // 150 kb
                           "../tests/test4.txt.cipher"  // 200 kb
                         };
        
        // Palavras conhecidas dos arquivos
        String[] knowntext = { "sit", "est", "non", "sem"};

        Log.log("GG", "Iniciando procedimento...");
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("dados.txt"), "utf-8"))) { // arquivo para gravar os resultados
            for(int i = 0; i < files.length ; i++) {
            // for(int i = 3; i < 4 ; i++) {
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
            Log.log("GG", "Exception");
            e.printStackTrace();
        }

        
    }
}
