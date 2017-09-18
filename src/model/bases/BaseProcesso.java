package model.bases;

import model.enums.Estado;
import util.Config;

import java.util.Random;

public abstract class BaseProcesso {

    protected int id;
    protected Integer duracao;
    protected Integer tempoRestante;
    protected int estado;
    protected boolean isNovo;

    public BaseProcesso() {
        setId();
        setDuracao();
        setTempoRestante();
        setEstado(this.estado = Estado.APTO.getValor());
        this.isNovo = true;
    }

    public int getId() {
        return id;
    }

    private void setId() {
        this.id = ++Config.IDS;
    }

    public Integer getDuracao() {
        return duracao;
    }

    private void setDuracao() {
        do {
            this.duracao = new Random().nextInt(21);
        } while (!(this.duracao >= 4 && this.duracao <= 20));
    }

    public Integer getTempoRestante() {
        return tempoRestante;
    }

    private void setTempoRestante() {
        this.tempoRestante = this.duracao;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public boolean isNovo() {
        return isNovo;
    }

    public void setNovo(boolean novo) {
        isNovo = novo;
    }
}
