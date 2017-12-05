package model;

import controller.algoritmosEscalonamento.Sjf;
import model.bases.BaseProcesso;

public class Bloco {

    private int id;
    private int tamanho;
    private boolean livre;
    private BaseProcesso processo;

    public int getId() {
        return id;
    }

    public Bloco(int id, int tamanho, BaseProcesso processo) {
        this.id = id;
        this.tamanho = tamanho;
        this.processo = processo;
        this.livre = false;
    }

    public void addProcesso(BaseProcesso processo) {
        this.processo = processo;
    }

    public BaseProcesso removerProcesso() {
        if (processo != null) {
            BaseProcesso novo = processo;
            processo = null;
            return novo;
        } else {
            return null;
        }
    }

    public int getTamanho() {
        return tamanho;
    }

    public boolean isLivre() {
        return livre;
    }

    public void setLivre(boolean livre) {
        this.livre = livre;
    }

    public BaseProcesso getProcesso() {
        return processo;
    }

    public boolean isEmpty() {
        if (processo == null)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "Bloco{" +
                "id=" + id +
                ", tamanho=" + tamanho +
                ", livre=" + livre +
                '}';
    }


}
