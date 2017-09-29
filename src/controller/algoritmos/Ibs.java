package controller.algoritmos;

import model.IbsCore;
import model.IbsProcesso;
import util.Config;

import java.util.ArrayList;

public class Ibs {
    private IbsCore[] cores;
    private ArrayList<IbsProcesso> esperando = new ArrayList<>();
    ArrayList<IbsProcesso> finalizados = new ArrayList<>();
    ArrayList<IbsProcesso> abortados = new ArrayList<>();

    public Ibs(int qtdeCores, int qtdeProcessosIniciais) {
        Config.IBS_IS_RUNNING = true;
        cores = new IbsCore[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new IbsProcesso());
        }
    }

    private void adicionarProcesso(IbsProcesso processo) {
        esperando.add(processo);
        esperando.sort((p1, p2) -> p1.getStartTs().compareTo(p2.getStartTs()));
    }

    public IbsCore[] getCores() {
        return cores;
    }

    public ArrayList<IbsProcesso> getEsperando() {
        return esperando;
    }
}
