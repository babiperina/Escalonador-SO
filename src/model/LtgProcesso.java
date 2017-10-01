package model;

import model.bases.BaseProcesso;
import model.enums.Estado;

import java.sql.Timestamp;
import java.util.Random;

public class LtgProcesso extends BaseProcesso {

    private Timestamp deadline;

    public LtgProcesso() {
        super();
        setDeadline();
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void decrementarTempoRestante() {
        if (--this.tempoRestante == 0) {
            this.estado = Estado.FINALIZADO.getValor();
        }
    }

    public void setDeadline() {
        int deadline;
        do {
            deadline = new Random().nextInt(21);
        } while (!(deadline >= 4 && deadline <= 20));
        deadline *= 1000;
        this.deadline = new Timestamp(System.currentTimeMillis() + deadline);
    }

    @Override
    public String toString() {
        return "Processo [ PID:" + id + " \tDURAÇÃO:" + duracao + " \tTEMPO RESTANTE:" + tempoRestante + " \tESTADO:" + estado + " \tDEADLINE:" + deadline + " ]";
    }

}
