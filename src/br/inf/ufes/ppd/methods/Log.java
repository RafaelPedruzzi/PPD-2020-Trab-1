package br.inf.ufes.ppd.methods;

public class Log {
    // Método auxiliar para fazer os logs de execução do programa
    public static void log(String author, String message) {
        System.out.println("[" + author + "] " + message);
    }
}
