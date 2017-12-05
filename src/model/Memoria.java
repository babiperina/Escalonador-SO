package model;

import model.Bloco;

import java.util.ArrayList;

public class Memoria {

    private final int tamanho;
    private int memoriaLivre;

    private ArrayList<Bloco> blocos = new ArrayList<>();

    public Memoria(final int tamanho) {
        this.tamanho = tamanho;
        this.memoriaLivre = tamanho;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getMemoriaLivre() {
        return memoriaLivre;
    }

    public void setMemoriaLivre(int memoriaLivre) {
        this.memoriaLivre = memoriaLivre;
    }

    public ArrayList<Bloco> getBlocos() {
        return blocos;
    }

    public void setBlocos(ArrayList<Bloco> blocos) {
        this.blocos = blocos;
    }


    @Override
    public String toString() {
        return "Memoria{" +
                "tamanho=" + tamanho +
                ", memoriaLivre=" + memoriaLivre +
                ", blocos=" + blocos +
                '}';
    }
}
