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
        // Arquvos de teste
        String[] files = { "../tests/test1.txt.cipher", // 50 kb
                           "../tests/test2.txt.cipher", // 100 kb
                           "../tests/test3.txt.cipher", // 150 kb
                           "../tests/test4.txt.cipher"  // 200 kb
                         };
                         
        // Palavras conhecidas dos arquivos
        String[] kts = { "sit", "est", "non", "sem"};

        // Buscando dicionário
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
        
        long dicLen = 80368; // tamanho do dicionário
        String word;         // palavra do dicionário usada na tentativa de decriptação
        byte[] decrypted;    // vetor para armazenar a menságem decriptada

        Log.log("Seq", "Iniciando caso sequencial...");
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("dadosSeq.txt"), "utf-8"))) { // arquivo para impressão dos resultados

            // Executando para cada arquivo de teste
            for(int i = 0; i < files.length ; i++) {
                long start = System.nanoTime();

                List<Guess> guesses = new LinkedList<Guess>(); // candidatos encontrados

                // Verificando se arquivo foi encontrado
                File f = new File(files[i]);
                Boolean exists = f.exists() && !f.isDirectory();
                if (!exists) {
                    Log.log("Seq", "Erro: Arquivo não encontrado");
                    System.exit(0);
                }

                // Obtendo mensagem criptografada e convertendo palavra conhecida para byte[]
                byte[] ciphertext = FileManager.readFile(files[i]);
                byte[] knowntext = kts[i].getBytes("utf-8");

                // Aplicando método de decriptação
                for (long j = 0; j <= dicLen-1; j++) {
                    word = dictionary.get((int) i);
                    decrypted = Decrypt.decrypt(word, ciphertext);

                    // Buscando palavra conhecida no texto decriptografado
                    if (Search(decrypted, knowntext)) {
                        Log.log("Seq", "Eu found um guess");
                        Guess guess = new Guess();
                        guess.setKey(word);
                        guess.setMessage(decrypted);
                        guesses.add(guess);
                    }
                }

                long end = System.nanoTime();

                long time = end - start;
                double seconds = (double) time / 1000000000.0;

                // Imprimindo resultados desse caso de teste
                String result = files[i] + " - seconds: " + seconds;
                Log.log("Seq", "Ataque concluido.");
                Log.log("Seq", result);
                writer.write(result + "\n");

                Log.log("Seq", "Chaves candidatas encontradas:");
                for(Guess g : guesses) {
                    Log.log("Seq", " - " + g.getKey());
                    FileManager.saveFile(g.getKey() + ".msg", g.getMessage());
                }
            }
        
        } catch (Exception e) {
            Log.log("Seq", "Exception");
            e.printStackTrace();
        }
        
    }

    // Método para comparação de vetores de bytes
    // Fonte: stack overflow
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
