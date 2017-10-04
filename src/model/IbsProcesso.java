package model;

import model.bases.BaseProcesso;
import util.Config;

import java.util.Random;

public class IbsProcesso extends BaseProcesso {

    private Integer start;
    private Integer end;


    public IbsProcesso() {
        super();
        setStart();
        setEnd();
    }

    public Integer getStart() {
        return start;
    }

    public void setStart() {
        int random = new Random().nextInt(10) + 2;
        this.start = Config.timeIBS + random;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd() {
        int random = new Random().nextInt(20) + 1;
        this.end = start + random;
    }


    @Override
    public String toString() {
        return "IbsProcesso{" +
                "start=" + start +
                ", end=" + end +
                ", id=" + id +
                ", duracao=" + duracao +
                ", tempoRestante=" + tempoRestante +
                ", estado=" + estado +
                ", isNovo=" + isNovo +
                '}';
    }
}
