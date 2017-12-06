package controller.algoritmosEscalonamento;

import model.Bloco;
import model.LtgProcesso;
import model.Memoria;
import model.enums.Estado;
import util.Config;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Ltg {

    LtgProcesso[] cores;
    ArrayList<LtgProcesso> aptos = new ArrayList<>();
    ArrayList<LtgProcesso> finalizados = new ArrayList<>();
    ArrayList<LtgProcesso> abortados = new ArrayList<>();

    private Memoria memoria;

    public Ltg(int qtdeCores, int qtdeProcessosIniciais) {
        Config.LTG_IS_RUNNING = true;
        memoria = new Memoria(2000);
        cores = new LtgProcesso[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new LtgProcesso());
        }
        for (int i = 0; i < cores.length && !aptos.isEmpty(); i++) {
//            cores[i] = aptos.remove(0);
//            cores[i].setEstado(Estado.EXECUTANDO.getValor());
            LtgProcesso processoRequisicao = aptos.remove(0);
            if (bestFit(processoRequisicao)) {
                cores[i] = processoRequisicao;
                cores[i].setEstado(Estado.EXECUTANDO.getValor());
            } else {
                System.out.println("OutOfMemory:: " + processoRequisicao.getId() + ", " + processoRequisicao.getSize());
                processoRequisicao.setEstado(Estado.ABORTADO.getValor());
            }
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

    private void mudarProcessosDeFilaUsandoBestFit() {
        for (int i = 0; i < cores.length; i++) {
            LtgProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
//                    finalizados.add(processoAtual);
//                    cores[i] = null;
                    for (Bloco c :
                            memoria.getBlocos()) {
                        if(c.getProcesso() != null)
                            if (c.getProcesso().getId() == processoAtual.getId()) {
                                c.setLivre(true);
                                c.addProcesso(null);
                            }
                    }
                    finalizados.add(processoAtual);
                    cores[i] = null;
                }
            }
        }

        for (int i = 0; i < cores.length; i++) {
            LtgProcesso processoAtual = cores[i];

            if (processoAtual == null && !aptos.isEmpty()) {
//                cores[i] = aptos.remove(0);
//                cores[i].setEstado(Estado.EXECUTANDO.getValor());
                LtgProcesso processoRequisicao = aptos.remove(0);
                if (bestFit(processoRequisicao)) {
                    cores[i] = processoRequisicao;
                    cores[i].setEstado(Estado.EXECUTANDO.getValor());
                } else {
                    System.out.println("OutOfMemory:: " + processoRequisicao.getId() + ", " + processoRequisicao.getSize());
                    processoRequisicao.setEstado(Estado.ABORTADO.getValor());
                }
            }
        }

    }

    public boolean bestFit(LtgProcesso processo) {
        boolean success = false;

        if (memoria.getBlocos().size() == 0) {
            memoria.getBlocos().add(new Bloco(1, processo.getSize(), processo));
            memoria.setMemoriaLivre(memoria.getMemoriaLivre() - processo.getSize());
            success = true;
        } else {
            if (existeMemoriaLivre() || existeBlocoLivre()) {
                if (existeMemoriaSuficienteLivre(processo.getSize())) {
                    memoria.getBlocos().add(new Bloco(memoria.getBlocos().size() + 1, processo.getSize(), processo));
                    memoria.setMemoriaLivre(memoria.getMemoriaLivre() - processo.getSize());
                    success = true;
                } else {
                    success = auxBestFit(processo);
                }
            }
        }

        return success;
    }

    public boolean existeMemoriaLivre() {
        if (memoria.getMemoriaLivre() > 0)
            return true;
        return false;
    }

    public boolean existeMemoriaSuficienteLivre(int tamanhoProcesso) {
        if (memoria.getMemoriaLivre() >= tamanhoProcesso)
            return true;
        return false;
    }

    public boolean existeBlocoLivre() {
        for (Bloco b :
                memoria.getBlocos()) {
            if (b.isLivre()) {
                return true;
            }
        }
        return false;
    }

    public boolean auxBestFit(LtgProcesso processo) {
        Bloco bloco = null;
        for (Bloco b :
                memoria.getBlocos()) {
            if (b.isLivre()) {
                if (bloco == null  && b.getTamanho() >= processo.getSize()) {
                    bloco = b;
                } else {
                    if(bloco != null)
                        if (b.getTamanho() < bloco.getTamanho() && b.getTamanho() >= processo.getSize()) {
                            bloco = b;
                        }
                }
            }
        }
        if (bloco != null) {
            bloco.addProcesso(processo);
            bloco.setLivre(false);
            return true;
        } else
            return false;

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
        mudarProcessosDeFilaUsandoBestFit();
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

    public Memoria getMemoria() {
        return memoria;
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
