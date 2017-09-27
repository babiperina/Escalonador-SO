package model;

import model.bases.BaseProcesso;
import model.enums.Estado;

import java.util.Random;

public class RrProcesso extends BaseProcesso {

    private int prioridade;
    private int quantum;
    private int quantumRestante;

    public RrProcesso() {
        super();
        setPrioridade();
    }

    public void decrementarTempoEQuantumRestante() {
        --this.tempoRestante;
        --this.quantumRestante;
        if (this.tempoRestante == 0) {
            this.estado = Estado.FINALIZADO.getValor();
        } else if (this.quantumRestante == 0) {
            this.estado = Estado.APTO.getValor();
        }
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade() {
        this.prioridade = new Random().nextInt(4);
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
        this.quantumRestante = this.quantum;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getQuantumRestante() {
        return quantumRestante;
    }

    @Override
    public String toString() {
        return "Processo{" +
                "id=" + id +
                ", duracao=" + duracao +
                ", tempoRestante=" + tempoRestante +
                ", estado=" + estado +
                ", prioridade=" + prioridade +
                ", quantum=" + quantum +
                ", quantumRestante=" + quantumRestante +
                '}';
    }
}
