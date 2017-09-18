package controller.algoritmos;

import model.SjfProcesso;
import model.enums.Estado;
import util.Config;

import java.util.ArrayList;

public class Sjf {

    private SjfProcesso[] cores;
    private ArrayList<SjfProcesso> aptos = new ArrayList<>();
    private ArrayList<SjfProcesso> finalizados = new ArrayList<>();

    public Sjf(int qtdeCores, int qtdeProcessosIniciais) {
        Config.SJF_IS_RUNNING = true;
        cores = new SjfProcesso[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new SjfProcesso());
        }
        for (int i = 0; i < cores.length && !aptos.isEmpty(); i++) {
            cores[i] = aptos.remove(0);
            cores[i].setEstado(Estado.EXECUTANDO.getValor());
        }
    }

    public void adicionarProcesso(SjfProcesso processo) {
        aptos.add(processo);
        aptos.sort((p1, p2) -> p1.getDuracao().compareTo(p2.getDuracao()));
    }

    private void decrementarTempoRestanteProcessosExecutando() {
        for (SjfProcesso processo : cores) {
            if (processo != null)
                processo.decrementarTempoRestante();
        }
    }

    private void mudarProcessosDeFila() {
        for (int i = 0; i < cores.length; i++) {
            SjfProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
                    finalizados.add(processoAtual);
                    cores[i] = null;
                }
            }
        }

        for (int i = 0; i < cores.length; i++) {
            SjfProcesso processoAtual = cores[i];

            if (processoAtual == null && !aptos.isEmpty()) {
                cores[i] = aptos.remove(0);
                cores[i].setEstado(Estado.EXECUTANDO.getValor());
            }
        }

    }

    public void desligarAlgoritmo() {
        boolean coreIsEmpty = true;
        for (int i = 0; i < cores.length; i++) {
            SjfProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                coreIsEmpty = false;
            }
        }

        if (coreIsEmpty && aptos.isEmpty()) {
            Config.SJF_IS_RUNNING = false;
        }
    }

    public void atualizarAlgoritmo() {
        decrementarTempoRestanteProcessosExecutando();
        mudarProcessosDeFila();
        desligarAlgoritmo();
    }


    public void print() {
        System.out.println();
        System.out.println("**********************");
        System.out.println("--------CORES--------");
        for (SjfProcesso processo : cores) {
            if (processo != null)
                System.out.println(processo.toString());
        }
        System.out.println("--------APTOS--------");
        for (SjfProcesso processo : aptos) {
            System.out.println(processo.toString());
        }
        System.out.println("-----FINALIZADOS-----");
        for (SjfProcesso processo : finalizados) {
            System.out.println(processo.toString());
        }
        System.out.println("**********************");
        System.out.println();

    }

    public SjfProcesso[] getCores() {
        return cores;
    }

    public ArrayList<SjfProcesso> getAptos() {
        return aptos;
    }

    public ArrayList<SjfProcesso> getFinalizados() {
        return finalizados;
    }
}
