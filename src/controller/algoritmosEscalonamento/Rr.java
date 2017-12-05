package controller.algoritmosEscalonamento;

import model.RrProcesso;
import model.enums.Estado;
import util.Config;

import java.util.ArrayList;

public class Rr {

    private int quantum;
    private int controlePrioridade = 0;

    RrProcesso[] cores;
    ArrayList<RrProcesso> P0 = new ArrayList<>();
    ArrayList<RrProcesso> P1 = new ArrayList<>();
    ArrayList<RrProcesso> P2 = new ArrayList<>();
    ArrayList<RrProcesso> P3 = new ArrayList<>();
    ArrayList<RrProcesso> finalizados = new ArrayList<>();

    public Rr(int qtdeCores, int qtdeProcessosIniciais, int quantum) {
        Config.RR_IS_RUNNING = true;
        this.quantum = quantum;
        cores = new RrProcesso[qtdeCores];

        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            RrProcesso novoProcesso = new RrProcesso();
            alocarProcessoByPrioridade(novoProcesso);
        }

        for (int i = 0; i < cores.length && (!P0.isEmpty() || !P1.isEmpty() || !P2.isEmpty() || !P3.isEmpty()); i++) {
            cores[i] = pegarProcessoNaFilaCorreta();
            cores[i].setEstado(Estado.EXECUTANDO.getValor());
        }
    }

    public void adicionarProcesso(RrProcesso processo){
        RrProcesso novoProcesso = new RrProcesso();
        alocarProcessoByPrioridade(novoProcesso);
    }

    private void alocarProcessoByPrioridade(RrProcesso processo) {
        switch (processo.getPrioridade()) {
            case 0:
                processo.setQuantum(this.quantum * 4);
                P0.add(processo);
                P0.sort((p1, p2) -> p1.getDuracao().compareTo(p2.getDuracao()));
                break;
            case 1:
                processo.setQuantum(this.quantum * 3);
                P1.add(processo);
                P1.sort((p1, p2) -> p1.getDuracao().compareTo(p2.getDuracao()));
                break;
            case 2:
                processo.setQuantum(this.quantum * 2);
                P2.add(processo);
                P2.sort((p1, p2) -> p1.getDuracao().compareTo(p2.getDuracao()));
                break;
            case 3:
                processo.setQuantum(this.quantum);
                P3.add(processo);
                P3.sort((p1, p2) -> p1.getDuracao().compareTo(p2.getDuracao()));
                break;
        }
    }

    private RrProcesso pegarProcessoNaFilaCorreta() {
        while (true) {
            switch (controlePrioridade) {
                case 0:
                    incrementarControlePrioridade();
                    if (!P0.isEmpty())
                        return P0.remove(0);
                    break;
                case 1:
                    incrementarControlePrioridade();
                    if (!P1.isEmpty())
                        return P1.remove(0);
                    break;
                case 2:
                    incrementarControlePrioridade();
                    if (!P2.isEmpty())
                        return P2.remove(0);
                    break;
                case 3:
                    incrementarControlePrioridade();
                    if (!P3.isEmpty())
                        return P3.remove(0);
                    break;
            }
        }
    }

    private void incrementarControlePrioridade() {
        if (++this.controlePrioridade > 3)
            controlePrioridade = 0;
    }

    private void decrementarTempoRestanteProcessoExecutando() {
        for (RrProcesso processo : cores) {
            if (processo != null)
                processo.decrementarTempoEQuantumRestante();
        }
    }

    private void mudarProcessosDeFila() {
        for (int i = 0; i < cores.length; i++) {
            RrProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
                    finalizados.add(processoAtual);
                    cores[i] = null;
                } else if (processoAtual.getEstado() == Estado.APTO.getValor()) {
                    alocarProcessoByPrioridade(processoAtual);
                    cores[i] = null;
                }
            }
        }

        for (int i = 0; i < cores.length; i++) {
            RrProcesso processoAtual = cores[i];

            if (processoAtual == null && (!P0.isEmpty() || !P1.isEmpty() || !P2.isEmpty() || !P3.isEmpty())) {
                cores[i] = pegarProcessoNaFilaCorreta();
                cores[i].setEstado(Estado.EXECUTANDO.getValor());
            }
        }

    }

    public void desligarAlgoritmo() {
        boolean coreIsEmpty = true;
        for (int i = 0; i < cores.length; i++) {
            RrProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                coreIsEmpty = false;
            }
        }

        if (coreIsEmpty && P0.isEmpty() && P1.isEmpty() && P2.isEmpty() && P3.isEmpty()) {
            Config.RR_IS_RUNNING = false;
        }
    }


    public void atualizarAlgoritmo() {
        decrementarTempoRestanteProcessoExecutando();
        mudarProcessosDeFila();
        desligarAlgoritmo();
    }

    public void print() {
        System.out.println();
        System.out.println("**********************");
        System.out.println("--------CORES--------");
        for (RrProcesso processo : cores) {
            if (processo != null)
                System.out.println(processo.toString());
        }
        System.out.println("--------P0--------");
        for (RrProcesso processo : P0) {
            System.out.println(processo.toString());
        }
        System.out.println("-----P1-----");
        for (RrProcesso processo : P1) {
            System.out.println(processo.toString());
        }
        System.out.println("--------P2--------");
        for (RrProcesso processo : P2) {
            System.out.println(processo.toString());
        }
        System.out.println("--------P3--------");
        for (RrProcesso processo : P3) {
            System.out.println(processo.toString());
        }
        System.out.println("--------FINALIZADOS--------");
        for (RrProcesso processo : finalizados) {
            System.out.println(processo.toString());
        }
        System.out.println("**********************");
        System.out.println();
    }


    public RrProcesso[] getCores() {
        return cores;
    }

    public ArrayList<RrProcesso> getAptos() {
        ArrayList<RrProcesso> rrs = new ArrayList<>();
        rrs.addAll(P0);
        rrs.addAll(P1);
        rrs.addAll(P2);
        rrs.addAll(P3);
        return rrs;
    }

    public ArrayList<RrProcesso> getP0() {
        return P0;
    }

    public ArrayList<RrProcesso> getP1() {
        return P1;
    }

    public ArrayList<RrProcesso> getP2() {
        return P2;
    }

    public ArrayList<RrProcesso> getP3() {
        return P3;
    }

    public ArrayList<RrProcesso> getFinalizados() {
        return finalizados;
    }
}
