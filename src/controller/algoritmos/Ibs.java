package controller.algoritmos;

import model.IbsCore;
import model.IbsProcesso;
import model.enums.Estado;
import util.Config;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Ibs {
    private IbsCore[] cores;
    private ArrayList<IbsProcesso> esperando = new ArrayList<>();
    private ArrayList<IbsProcesso> finalizados = new ArrayList<>();
    private ArrayList<IbsProcesso> abortados = new ArrayList<>();

    public Ibs(int qtdeCores, int qtdeProcessosIniciais) {
        Config.IBS_IS_RUNNING = true;
        cores = new IbsCore[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new IbsProcesso());
        }
    }

    public void atualizarAlgoritmo() {
        verificarDeadline();
        desligarAlgoritmo();
    }


    public void adicionarProcesso(IbsProcesso processo) {
        esperando.add(processo);
        esperando.sort((p1, p2) -> p1.getEndTs().compareTo(p2.getEndTs()));
    }

    private void verificarDeadline() {
        Timestamp horaAtual = new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < esperando.size(); i++) {
            IbsProcesso processoAtual = esperando.get(i);

            if (processoAtual.getStartTs().getTime() <= horaAtual.getTime()) {
                processoAtual.setEstado(Estado.ABORTADO.getValor());
                abortados.add(processoAtual);
                esperando.remove(i);
                i--;
            }
        }
    }

    public void desligarAlgoritmo() {
        boolean coreIsEmpty = true;
        for (int i = 0; i < cores.length; i++) {
            IbsCore coreAtual = cores[i];

            if (coreAtual != null) {
                coreIsEmpty = false;
            }
        }

        if (coreIsEmpty && esperando.isEmpty()) {
            Config.IBS_IS_RUNNING = false;
        }
    }
    public IbsCore[] getCores() {
        return cores;
    }

    public ArrayList<IbsProcesso> getEsperando() {
        return esperando;
    }

    public ArrayList<IbsProcesso> getFinalizados() {
        return finalizados;
    }

    public ArrayList<IbsProcesso> getAbortados() {
        return abortados;
    }
}
