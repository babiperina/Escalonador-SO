package controller.algoritmosEscalonamento;

import model.*;
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
    ArrayList<RrProcesso> abortados = new ArrayList<>();
    private ArrayList<Tamanho> tamanhos;
    int gerenciadorMemoria;
    private Memoria memoria;

    public Rr(int qtdeCores, int qtdeProcessosIniciais, int quantum, int memoriaSize) {
        Config.RR_IS_RUNNING = true;
        gerenciadorMemoria = 1;
        memoria = new Memoria(memoriaSize);
        tamanhos = new ArrayList<>();
        this.quantum = quantum;
        cores = new RrProcesso[qtdeCores];

        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            RrProcesso novoProcesso = new RrProcesso();
            alocarProcessoByPrioridade(novoProcesso);
        }

        for (int i = 0; i < cores.length && (!P0.isEmpty() || !P1.isEmpty() || !P2.isEmpty() || !P3.isEmpty()); i++) {
//            cores[i] = pegarProcessoNaFilaCorreta();
//            cores[i].setEstado(Estado.EXECUTANDO.getValor());
            RrProcesso processoRequisicao = pegarProcessoNaFilaCorreta();
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

    public Rr(int qtdeCores, int qtdeProcessosIniciais, int quantum, int memoriaSize, int qtdeRequisicoes, int qtdeListas) {
        Config.RR_IS_RUNNING = true;
        memoria = new Memoria(memoriaSize, qtdeRequisicoes, qtdeListas);
        gerenciadorMemoria = 2;
        tamanhos = new ArrayList<>();
        this.quantum = quantum;
        cores = new RrProcesso[qtdeCores];

        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            RrProcesso novoProcesso = new RrProcesso();
            alocarProcessoByPrioridade(novoProcesso);
        }

        for (int i = 0; i < cores.length && (!P0.isEmpty() || !P1.isEmpty() || !P2.isEmpty() || !P3.isEmpty()); i++) {
//            cores[i] = pegarProcessoNaFilaCorreta();
//            cores[i].setEstado(Estado.EXECUTANDO.getValor());
            RrProcesso processoRequisicao = pegarProcessoNaFilaCorreta();
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


    public void adicionarProcesso(RrProcesso processo) {
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

    private void mudarProcessosDeFilaUsandoQuickFit() {
        for (int i = 0; i < cores.length; i++) {
            RrProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
//                    finalizados.add(processoAtual);
//                    cores[i] = null;
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
                        finalizados.add(processoAtual);
                        cores[i] = null;
                    }
                } else if (processoAtual.getEstado() == Estado.APTO.getValor()) {
                    alocarProcessoByPrioridade(processoAtual);
                    cores[i] = null;
                }

            }
        }

        for (int i = 0; i < cores.length; i++) {
            RrProcesso processoAtual = cores[i];

            if (processoAtual == null && (!P0.isEmpty() || !P1.isEmpty() || !P2.isEmpty() || !P3.isEmpty())) {
//                cores[i] = pegarProcessoNaFilaCorreta();
//                cores[i].setEstado(Estado.EXECUTANDO.getValor());
                RrProcesso processoRequisicao = pegarProcessoNaFilaCorreta();
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


    public boolean requisicaoQuickFit(RrProcesso processo) {
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

    public boolean existeFilaToHim(RrProcesso processo) {
        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                return true;
            }
        }
        return false;
    }


    public boolean alocarComQuickFit(RrProcesso processo) {
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

    public void criarBlocoNaFilaCorrespondente(RrProcesso processo) {
        memoria.getBlocosGerais().add(new Bloco(memoria.getBlocosGerais().size() + 1, processo.getSize(), processo));

        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                l.getBloco().add(new Bloco(l.getBloco().size() + 1, processo.getSize(), processo));
            }
        }
    }

    public void alocarNoPrimeiroBlocoLivreDaFilaCorrespondente(RrProcesso processo) {
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

    public boolean existeBlocoLivreNaLista(RrProcesso processo) {
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

    private void criarListas() {
        for (int i = 0; i < memoria.getQtdeListas(); i++) {
            memoria.configLista(i, tamanhos.get(tamanhos.size() - 1).getTamanho());
            tamanhos.remove(tamanhos.size() - 1);
        }
    }

    public boolean alocarProcessoBlocosGerais(RrProcesso processo) {
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

    private void mudarProcessosDeFilaUsandoBestFit() {
        for (int i = 0; i < cores.length; i++) {
            RrProcesso processoAtual = cores[i];

            if (processoAtual != null) {
                if (processoAtual.getEstado() == Estado.FINALIZADO.getValor()) {
//                    finalizados.add(processoAtual);
//                    cores[i] = null;
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
                } else if (processoAtual.getEstado() == Estado.APTO.getValor()) {
                    alocarProcessoByPrioridade(processoAtual);
                    cores[i] = null;
                }
            }
        }

        for (int i = 0; i < cores.length; i++) {
            RrProcesso processoAtual = cores[i];

            if (processoAtual == null && (!P0.isEmpty() || !P1.isEmpty() || !P2.isEmpty() || !P3.isEmpty())) {
//                cores[i] = pegarProcessoNaFilaCorreta();
//                cores[i].setEstado(Estado.EXECUTANDO.getValor());
                RrProcesso processoRequisicao = pegarProcessoNaFilaCorreta();
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

    public boolean bestFit(RrProcesso processo) {
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

    public boolean auxBestFit(RrProcesso processo) {
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
        System.out.println(memoria.toString());
        decrementarTempoRestanteProcessoExecutando();
        if (gerenciadorMemoria == 1)
            mudarProcessosDeFilaUsandoBestFit();
        else
            mudarProcessosDeFilaUsandoQuickFit();
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


    public Memoria getMemoria() {
        return memoria;
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

    public ArrayList<RrProcesso> getAbortados() {
        return abortados;
    }
}
