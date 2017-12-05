package controller.algoritmosEscalonamento;

import model.LtgProcesso;
import model.enums.Estado;
import util.Config;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Ltg {

    LtgProcesso[] cores;
    ArrayList<LtgProcesso> aptos = new ArrayList<>();
    ArrayList<LtgProcesso> finalizados = new ArrayList<>();
    ArrayList<LtgProcesso> abortados = new ArrayList<>();

    public Ltg(int qtdeCores, int qtdeProcessosIniciais) {
        Config.LTG_IS_RUNNING = true;
        cores = new LtgProcesso[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new LtgProcesso());
        }
        for (int i = 0; i < cores.length && !aptos.isEmpty(); i++) {
            cores[i] = aptos.remove(0);
            cores[i].setEstado(Estado.EXECUTANDO.getValor());
        }
    }

    public void adicionarProcesso(LtgProcesso processo) {
        aptos.add(processo);
        aptos.sort((p1, p2) -> p1.getDeadline().compareTo(p2.getDeadline()));
    }

    private void decrementarTempoRestanteProcessosExecutando() {
        for (LtgProcesso processo : cores) {
            if (processo != null)
                processo.decrementarTempoRestante();
        }
    }

    private void verificarDeadline() {
        Timestamp horaAtual = new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < aptos.size(); i++) {
            LtgProcesso processoAtual = aptos.get(i);

            if (processoAtual.getDeadline().getTime() <= horaAtual.getTime()) {
                processoAtual.setEstado(Estado.ABORTADO.getValor());
                abortados.add(processoAtual);
                aptos.remove(i);
                i--;
            }
        }
    }

    private void mudarProcessosDeFila() {
        for (int i = 0; i < cores.length; i++) {
            LtgProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
                    finalizados.add(processoAtual);
                    cores[i] = null;
                }
            }
        }

        for (int i = 0; i < cores.length; i++) {
            LtgProcesso processoAtual = cores[i];

            if (processoAtual == null && !aptos.isEmpty()) {
                cores[i] = aptos.remove(0);
                cores[i].setEstado(Estado.EXECUTANDO.getValor());
            }
        }

    }

    public void desligarAlgoritmo() {
        boolean coreIsEmpty = true;
        for (int i = 0; i < cores.length; i++) {
            LtgProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                coreIsEmpty = false;
            }
        }

        if (coreIsEmpty && aptos.isEmpty()) {
            Config.LTG_IS_RUNNING = false;
        }
    }

    public void atualizarAlgoritmo() {
        decrementarTempoRestanteProcessosExecutando();
        verificarDeadline();
        mudarProcessosDeFila();
        desligarAlgoritmo();
    }

    public void print() {
        System.out.println();
        System.out.println("**********************");
        System.out.println("--------CORES--------");
        for (LtgProcesso processo : cores) {
            if (processo != null)
                System.out.println(processo.toString());
        }
        System.out.println("--------APTOS--------");
        for (LtgProcesso processo : aptos) {
            System.out.println(processo.toString());
        }
        System.out.println("-----FINALIZADOS-----");
        for (LtgProcesso processo : finalizados) {
            System.out.println(processo.toString());
        }
        System.out.println("-----ABORTADOS-----");
        for (LtgProcesso processo : abortados) {
            System.out.println(processo.toString());
        }
        System.out.println("**********************");
        System.out.println();

    }

    public LtgProcesso[] getCores() {
        return cores;
    }

    public ArrayList<LtgProcesso> getAptos() {
        return aptos;
    }

    public ArrayList<LtgProcesso> getFinalizados() {
        return finalizados;
    }

    public ArrayList<LtgProcesso> getAbortados() {
        return abortados;
    }
}
