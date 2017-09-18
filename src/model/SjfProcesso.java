package model;

import model.bases.BaseProcesso;
import model.enums.Estado;

public class SjfProcesso extends BaseProcesso {

    public SjfProcesso() {
        super();
    }

    public void decrementarTempoRestante() {
        if (--this.tempoRestante == 0) {
            this.estado = Estado.FINALIZADO.getValor();
        }
    }

    @Override
    public String toString() {
        return "Processo [ PID:" + id + " \tDURAÇÃO:" + duracao + " \tTEMPO RESTANTE:" + tempoRestante + " \tESTADO:" + estado + " ]";
    }
}
