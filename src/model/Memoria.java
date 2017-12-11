package model;

import model.Bloco;

import java.util.ArrayList;
import java.util.Arrays;

public class Memoria {

    private final int tamanho;
    private int memoriaLivre;

    private ArrayList<Bloco> blocos = new ArrayList<>();

    public Memoria(final int tamanho) {
        this.tamanho = tamanho;
        this.memoriaLivre = tamanho;
    }

    private int qtdeRequisicoes;
    private int getQtdeRequisicoesFeitas;
    public ListasQF[] listas;

    public Memoria(int tamanho, int qtdeRequisicoes, int qtdeListas) {
        this.tamanho = tamanho;
        this.memoriaLivre = this.tamanho;
        this.qtdeRequisicoes = qtdeRequisicoes;
        this.getQtdeRequisicoesFeitas = 0;
        listas = new ListasQF[qtdeListas];
        print();
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

    public ArrayList<Bloco> getBlocosPraExibicao() {
        ArrayList<Bloco> todos = blocos;

        if(listas!=null)
        for (int i = 0; i < listas.length; i++) {
            if(listas[i]!=null)
            todos.addAll(listas[i].getBloco());
        }

        return todos;
    }

    public void setBlocos(ArrayList<Bloco> blocos) {
        this.blocos = blocos;
    }

    private void print(){
        System.out.println(toString());
    }

//    @Override
//    public String toString() {
//        return "Memoria{" +
//                "tamanho=" + tamanho +
//                ", memoriaLivre=" + memoriaLivre +
//                ", blocos=" + blocos +
//                '}';
//    }


    public void incrementarQtdeRequisicoesFeitas() {
        this.getQtdeRequisicoesFeitas++;
    }

    public int getQtdeRequisicoesFeitas (){
        return getQtdeRequisicoesFeitas;
    }

    public int getQtdeRequisicoes() {
        return qtdeRequisicoes;
    }

    public void configLista(int id, int parametro){
        listas[id] = new ListasQF(parametro);
    }

    public int getQtdeListas(){
        return listas.length;
    }

    public void printListas(){
        for (ListasQF l:
                listas) {
            if(l!=null)
                System.out.println(l.toString());
        }
    }

    public ArrayList<Bloco> getBlocosGerais() {
        return blocos;
    }

    @Override
    public String toString() {
        return "MemoriaQF{" +
                "tamanho=" + tamanho +
                ", memoriaLivre=" + memoriaLivre +
                ", qtdeRequisicoes=" + qtdeRequisicoes +
                ", getQtdeRequisicoesFeitas=" + getQtdeRequisicoesFeitas +
                ", blocosGerais=" + blocos +
                ", listas=" + Arrays.toString(listas) +
                '}';
    }
}
