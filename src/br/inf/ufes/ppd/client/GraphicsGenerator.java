package br.inf.ufes.ppd.client;

import br.inf.ufes.ppd.interfaces.Guess;
import br.inf.ufes.ppd.methods.Log;

public class GraphicsGenerator {
    
    public static void main(String[] args) {
        String[] files = { "tests/text1.txt.chipher", 
                           "tests/text2.txt.chipher", 
                           "tests/text3.txt.chipher", 
                           "tests/text4.txt.chipher"
                         };
    
        String[] knowntext = { "ac", "est", "non", "eu"};

        Log.log("GG", "Iniciando procedimento...");
        
        for(int i = 0; i < files.length; i++) {
            long start = System.nanoTime();
            Guess[] ans = Client.orderAttack(files[i], knowntext[i]);
            long end = System.nanoTime();

            long time = end - start;
            double seconds = (double) time / 1000000000.0;
        }



        
    }
}
