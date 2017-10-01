package model;

import model.bases.BaseProcesso;

import java.sql.Timestamp;
import java.util.Random;

public class IbsProcesso extends BaseProcesso {

    private int start;
    private int end;
    private Timestamp startTs;
    private Timestamp endTs;


    public IbsProcesso() {
        super();
        setStartTs();
        setEndTs();
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public Timestamp getStartTs() {
        return startTs;
    }

    public void setStartTs() {
        start = new Random().nextInt(24);
        this.startTs = new Timestamp(System.currentTimeMillis() + start * 1000);
    }

    public Timestamp getEndTs() {
        return endTs;
    }

    public void setEndTs() {
        end = start + new Random().nextInt(20);
        if (end > 24) {
            end = 24;
        }
        this.endTs = new Timestamp(System.currentTimeMillis() + end * 1000);
    }

    @Override
    public String toString() {
        return "IbsProcesso{" +
                "start=" + start +
                ", end=" + end +
                ", startTs=" + startTs +
                ", endTs=" + endTs +
                ", id=" + id +
                ", duracao=" + duracao +
                ", tempoRestante=" + tempoRestante +
                ", estado=" + estado +
                ", isNovo=" + isNovo +
                '}';
    }
}
