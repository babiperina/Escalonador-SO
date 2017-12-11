package model;

public class Tamanho {
    private int tamanho;
    private int vezes;

    public Tamanho(int tamanho) {
        this.tamanho = tamanho;
        vezes = 1;
    }

    public void incrementarVezes(){
        vezes++;
    }

    public Integer getVezes() {
        return vezes;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    @Override
    public String toString() {
        return "Tamanho{" +
                "tamanho=" + tamanho +
                ", vezes=" + vezes +
                '}';
    }
}
