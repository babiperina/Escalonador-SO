package controller.algoritmosEscalonamento;

import model.*;
import model.enums.Estado;
import util.Config;

import java.util.ArrayList;

public class Sjf {

    private SjfProcesso[] cores;
    private ArrayList<SjfProcesso> aptos = new ArrayList<>();
    private ArrayList<SjfProcesso> finalizados = new ArrayList<>();
    private ArrayList<SjfProcesso> abortados = new ArrayList<>();
    private Memoria memoria;
    private ArrayList<Tamanho> tamanhos;
    int gerenciadorDeMemoria;

    public Sjf(int qtdeCores, int qtdeProcessosIniciais, int memoriaSize) {
        gerenciadorDeMemoria = 1;
        Config.SJF_IS_RUNNING = true;
        tamanhos = new ArrayList<>();
        memoria = new Memoria(memoriaSize);
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
                abortados.add(processoRequisicao);
            }
        }
    }


    public Sjf(int qtdeCores, int qtdeProcessosIniciais, int memoriaSize, int qtdeRequisicoes, int qtdeListas) {
        gerenciadorDeMemoria = 2;
        Config.SJF_IS_RUNNING = true;
        tamanhos = new ArrayList<>();
        memoria = new Memoria(memoriaSize, qtdeRequisicoes, qtdeListas);
        cores = new SjfProcesso[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new SjfProcesso());
        }
        for (int i = 0; i < cores.length && !aptos.isEmpty(); i++) {
//            cores[i] = aptos.remove(0);
//            cores[i].setEstado(Estado.EXECUTANDO.getValor());
            SjfProcesso processoRequisicao = aptos.remove(0);
            if (requisicaoQuickFit(processoRequisicao)) {
                cores[i] = processoRequisicao;
                cores[i].setEstado(Estado.EXECUTANDO.getValor());
            } else {
                System.out.println("OutOfMemory:: " + processoRequisicao.getId() + ", " + processoRequisicao.getSize());
                processoRequisicao.setEstado(Estado.ABORTADO.getValor());
                abortados.add(processoRequisicao);
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
                        if (c.getProcesso() != null)
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
                    abortados.add(processoRequisicao);
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
                if (bloco == null && b.getTamanho() >= processo.getSize()) {
                    bloco = b;
                } else {
                    if (bloco != null)
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
        if (gerenciadorDeMemoria == 1)
            mudarProcessosDeFilaUsandoBestFit();
        else
            mudarProcessosDeFilaUsandoQuickFit();
        desligarAlgoritmo();
    }

    private void mudarProcessosDeFilaUsandoQuickFit() {
        for (int i = 0; i < cores.length; i++) {
            SjfProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
                    for (Bloco c :
                            memoria.getBlocos()) {
                        if (c.getProcesso() != null)
                            if (c.getProcesso().getId() == processoAtual.getId()) {
                                c.setLivre(true);
                                c.addProcesso(null);
                            }
                    }
                    for (ListasQF lista :
                            memoria.listas) {
                        if (lista != null) {
                            if (lista.getParametro() == processoAtual.getSize()) {
                                for (Bloco b :
                                        lista.getBloco()) {
                                    if (b.getProcesso() != null)
                                        if (b.getProcesso().getId() == processoAtual.getId()) {
                                            b.setLivre(true);
                                            b.addProcesso(null);
                                        }
                                }
                            }
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
                if (requisicaoQuickFit(processoRequisicao)) {
                    cores[i] = processoRequisicao;
                    cores[i].setEstado(Estado.EXECUTANDO.getValor());
                } else {
                    System.out.println("OutOfMemory:: " + processoRequisicao.getId() + ", " + processoRequisicao.getSize());
                    processoRequisicao.setEstado(Estado.ABORTADO.getValor());
                    abortados.add(processoRequisicao);
                }
            }
        }

    }

    public boolean requisicaoQuickFit(SjfProcesso processo) {
        System.out.println("Requisição Feita");
        memoria.incrementarQtdeRequisicoesFeitas();
        if (memoria.getQtdeRequisicoesFeitas() <= memoria.getQtdeRequisicoes()) {
            //adicionar o tamanho ou contar as  vezes que ele apareceu
            Tamanho t = isTamanhoExiste(processo.getSize());
            if (t != null) {
                t.incrementarVezes();
            } else {
                registrarNovoTamanho(processo.getSize());
            }

            //aqui, preciso alocar o processo em questão no seu devido bloco.
            if (!alocarProcessoBlocosGerais(processo)) {
                System.out.println("outOfBounds");
                return false;
            } else {
                return true;
            }

        } else if (memoria.getQtdeRequisicoesFeitas() == memoria.getQtdeRequisicoes() + 1) {
            // criar as listas, antes de qualquer coisa, dps alocar os processos que forem aparecendo nas devidas listas.
            printTamanhos();
            System.out.println("Aplicando sort nos tamanhos...");
            tamanhos.sort((t1, t2) -> t1.getVezes().compareTo(t2.getVezes()));
            printTamanhos();
            criarListas();
            memoria.printListas();

            //colocar os blocos corretos nas listas
            colocarBlocosNasListas();
            //alocar processo maneira quick fit
            return alocarComQuickFit(processo);
        } else {
            // alocar corretamente os processos levando em consideração as listas.
            return alocarComQuickFit(processo);

        }
    }

    private void printTamanhos() {
        for (Tamanho tamanho :
                tamanhos) {
            System.out.println(tamanho.toString());
        }
    }

    public boolean alocarComQuickFit(SjfProcesso processo) {
        if (existeFilaToHim(processo)) {
            if (existeMemoriaLivre() || existeBlocoLivreNaLista(processo)) {
                if (existeBlocoLivreNaLista(processo)) {
                    alocarNoPrimeiroBlocoLivreDaFilaCorrespondente(processo);
                    return true;
                } else {
                    if (existeMemoriaSuficienteLivre(processo.getSize())) {
                        criarBlocoNaFilaCorrespondente(processo);
                        memoria.setMemoriaLivre(memoria.getMemoriaLivre() - processo.getSize());
                        return true;
                    }
                }
            }
        } else {
            return alocarProcessoBlocosGerais(processo);
        }
        return false;
    }

    public void criarBlocoNaFilaCorrespondente(SjfProcesso processo) {
        memoria.getBlocosGerais().add(new Bloco(memoria.getBlocosGerais().size() + 1, processo.getSize(), processo));

        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                l.getBloco().add(new Bloco(l.getBloco().size() + 1, processo.getSize(), processo));
            }
        }
    }

    public void alocarNoPrimeiroBlocoLivreDaFilaCorrespondente(SjfProcesso processo) {
        boolean alocou = false;
        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                for (int i = 0; i < l.getBloco().size(); i++) {
                    if (l.getBloco().get(i).isLivre() && !alocou) {
                        l.getBloco().get(i).addProcesso(processo);
                        l.getBloco().get(i).setLivre(false);
                        alocou = true;
                    }
                }
            }
        }
    }

    public boolean existeBlocoLivreNaLista(SjfProcesso processo) {
        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                for (int i = 0; i < l.getBloco().size(); i++) {
                    if (l.getBloco().get(i).isLivre())
                        return true;
                }
            }
        }
        return false;
    }

    public boolean existeFilaToHim(SjfProcesso processo) {
        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                return true;
            }
        }
        return false;
    }


    public void colocarBlocosNasListas() {
        for (int i = 0; i < memoria.listas.length; i++) {
            int parametro = memoria.listas[i].getParametro();
            for (int b = 0; b < memoria.getBlocos().size(); b++) {
                if (memoria.getBlocos().get(b).getTamanho() == parametro) {
                    memoria.listas[i].getBloco().add(memoria.getBlocos().get(b));
                    memoria.getBlocosGerais().remove(b);
                    b--;
                }
            }
        }
    }

    private void criarListas() {
        for (int i = 0; i < memoria.getQtdeListas(); i++) {
            memoria.configLista(i, tamanhos.get(tamanhos.size() - 1).getTamanho());
            tamanhos.remove(tamanhos.size() - 1);
        }
    }

    public boolean alocarProcessoBlocosGerais(SjfProcesso processo) {
        boolean feedback = false;

        if (memoria.getBlocosGerais().size() == 0) {
            memoria.getBlocosGerais().add(new Bloco(1, processo.getSize(), processo));
            memoria.setMemoriaLivre(memoria.getMemoriaLivre() - processo.getSize());
            feedback = true;
        } else {
            if (existeMemoriaLivre() || existeBlocoLivre()) {
                if (existeMemoriaSuficienteLivre(processo.getSize())) {
                    memoria.getBlocosGerais().add(new Bloco(memoria.getBlocosGerais().size() + 1, processo.getSize(), processo));
                    memoria.setMemoriaLivre(memoria.getMemoriaLivre() - processo.getSize());
                    feedback = true;
                } else {
                    feedback = auxBestFit(processo);
                }
            }
        }

        return feedback;
    }

    public void registrarNovoTamanho(int t) {
        tamanhos.add(new Tamanho(t));
    }

    public Tamanho isTamanhoExiste(int t) {
        for (Tamanho tamanho :
                tamanhos) {
            if (tamanho.getTamanho() == t) {
                return tamanho;
            }
        }
        return null;
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

    public ArrayList<SjfProcesso> getAbortados() {
        return abortados;
    }
}
