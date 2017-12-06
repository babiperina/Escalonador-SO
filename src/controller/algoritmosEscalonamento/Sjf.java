package controller.algoritmosEscalonamento;

import model.Bloco;
import model.Memoria;
import model.SjfProcesso;
import model.enums.Estado;
import util.Config;

import java.util.ArrayList;

public class Sjf {

    private SjfProcesso[] cores;
    private ArrayList<SjfProcesso> aptos = new ArrayList<>();
    private ArrayList<SjfProcesso> finalizados = new ArrayList<>();
    private Memoria memoria;

    public Sjf(int qtdeCores, int qtdeProcessosIniciais) {
        Config.SJF_IS_RUNNING = true;
        memoria = new Memoria(2000);
        cores = new SjfProcesso[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new SjfProcesso());
        }
        for (int i = 0; i < cores.length && !aptos.isEmpty(); i++) {
//            cores[i] = aptos.remove(0);
//            cores[i].setEstado(Estado.EXECUTANDO.getValor());
            SjfProcesso processoRequisicao = aptos.remove(0);
            if (bestFit(processoRequisicao)) {
                cores[i] = processoRequisicao;
                cores[i].setEstado(Estado.EXECUTANDO.getValor());
            } else {
                System.out.println("OutOfMemory:: " + processoRequisicao.getId() + ", " + processoRequisicao.getSize());
                processoRequisicao.setEstado(Estado.ABORTADO.getValor());
            }
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

    private void mudarProcessosDeFilaUsandoBestFit() {
        for (int i = 0; i < cores.length; i++) {
            SjfProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
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
            SjfProcesso processoAtual = cores[i];

            if (processoAtual == null && !aptos.isEmpty()) {
                SjfProcesso processoRequisicao = aptos.remove(0);
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

    public boolean bestFit(SjfProcesso processo) {
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

    public boolean auxBestFit(SjfProcesso processo) {
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
        System.out.println(memoria.toString());
        decrementarTempoRestanteProcessosExecutando();
        mudarProcessosDeFilaUsandoBestFit();
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

    public Memoria getMemoria() {
        return memoria;
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
