package controller.algoritmosEscalonamento;

import model.IbsCore;
import model.IbsProcesso;
import model.enums.Estado;
import util.Config;

import java.util.ArrayList;

public class Ibs {
    private IbsCore[] cores;
    private ArrayList<IbsProcesso> esperando = new ArrayList<>();
    private ArrayList<IbsProcesso> finalizados = new ArrayList<>();
    private ArrayList<IbsProcesso> abortados = new ArrayList<>();

    public Ibs(int qtdeCores, int qtdeProcessosIniciais) {
        Config.IBS_IS_RUNNING = true;
        cores = new IbsCore[qtdeCores];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = new IbsCore();
        }
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new IbsProcesso());
        }
        ordenar();
        ibs();
    }

    public void atualizarAlgoritmo() {
        Config.timeIBS++;
        verificarDeadline();
        mudarProcessoDeFila();
        desligarAlgoritmo();
    }

    private void mudarProcessoDeFila() {
        for (IbsCore core :
                cores) {
            if (core.getFinalizado() != null) {
                finalizados.add(core.getFinalizado());
                core.setFinalizado(null);
                finalizados.get(finalizados.size() - 1).setEstado(Estado.FINALIZADO.getValor());
            }
            core.atualizar();
            System.out.println(core.getAptos());
        }


    }

    public void ibs() {
        for (IbsCore core :
                cores) {
            core.setAptos(getRunning(core));
        }

    }

    private ArrayList<IbsProcesso> getRunning(IbsCore core) {
        ArrayList running = new ArrayList();
        IbsProcesso ultimo;

        if (core.isProcessoExecutando()) {
            ultimo = core.getIbsProcesso();
        } else {
            ultimo = esperando.get(0);
            esperando.remove(0);
        }

        for (int i = 0; i < esperando.size(); i++) {
            if (esperando.get(i).getStart() >= ultimo.getEnd()) {
                running.add(esperando.get(i));
                ultimo = esperando.get(i);
                esperando.remove(i);
                i--;
            }
        }
        return running;
    }

    public void adicionarProcesso(IbsProcesso processo) {
        esperando.add(processo);
    }

    public void ordenar() {
        esperando.sort((p1, p2) -> p1.getEnd().compareTo(p2.getEnd()));

    }

    private void verificarDeadline() {
        for (int i = 0; i < esperando.size(); i++) {
            IbsProcesso processoAtual = esperando.get(i);

            if (processoAtual.getStart() <= Config.timeIBS) {
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

    public ArrayList<IbsProcesso> getExecutando() {
        ArrayList arrayList = new ArrayList();
        for (IbsCore core : cores) {
            if (core.isProcessoExecutando()) {
                arrayList.add(core.getIbsProcesso());
            } else {
                arrayList.add(null);
            }
        }

        return arrayList;
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
