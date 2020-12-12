package br.inf.ufes.ppd.master;

// Classe auxiliar utilizada para armazenar um intervalo de índices para avaliação
public class Range {
    private long init;
    private long last;

    public Range(long init, long last) {
        this.init = init;
        this.last = last;
    }

    public long getInit() {
        return this.init;
    }

    public long getLast() {
        return last;
    }
}
