package model;

import java.util.ArrayList;
import java.util.Random;

public class ListasQF {

    private int parametro;
    private int R;
    private int G;
    private int B;
    ArrayList<Bloco> bloco = new ArrayList<>();

    public ListasQF(int parametro) {
        this.parametro = parametro;

        R = new Random().nextInt(255);
        G = new Random().nextInt(255);
            B = new Random().nextInt(255);
    }

    public int getParametro() {
        return parametro;
    }

    public void setParametro(int parametro) {
        this.parametro = parametro;
    }

    public ArrayList<Bloco> getBloco() {
        return bloco;
    }

    public void setBloco(ArrayList<Bloco> bloco) {
        this.bloco = bloco;
    }

    public int getR() {
        return R;
    }

    public int getG() {
        return G;
    }

    public int getB() {
        return B;
    }

    @Override
    public String toString() {
        return "ListasQF{" +
                "parametro=" + parametro +
                ", bloco=" + bloco +
                '}';
    }
}
