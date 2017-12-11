package controller.algoritmosEscalonamento;

import model.*;
import model.enums.Estado;
import util.Config;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Ltg {

    LtgProcesso[] cores;
    ArrayList<LtgProcesso> aptos = new ArrayList<>();
    ArrayList<LtgProcesso> finalizados = new ArrayList<>();
    ArrayList<LtgProcesso> abortados = new ArrayList<>();
    private ArrayList<Tamanho> tamanhos;
    int gerenciadorDeMemoria;

    private Memoria memoria;

    public Ltg(int qtdeCores, int qtdeProcessosIniciais, int memoriaSize) {
        Config.LTG_IS_RUNNING = true;
        gerenciadorDeMemoria = 1;
        tamanhos = new ArrayList<>();
        memoria = new Memoria(memoriaSize);
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

    public Ltg(int qtdeCores, int qtdeProcessosIniciais, int memoriaSize, int qtdeRequisicoes, int qtdeListas) {
        Config.LTG_IS_RUNNING = true;
        tamanhos = new ArrayList<>();
        gerenciadorDeMemoria = 2;
        memoria = new Memoria(memoriaSize, qtdeRequisicoes, qtdeListas);
        cores = new LtgProcesso[qtdeCores];
        for (int i = 0; i < qtdeProcessosIniciais; i++) {
            adicionarProcesso(new LtgProcesso());
        }
        for (int i = 0; i < cores.length && !aptos.isEmpty(); i++) {
//            cores[i] = aptos.remove(0);
//            cores[i].setEstado(Estado.EXECUTANDO.getValor());
            LtgProcesso processoRequisicao = aptos.remove(0);
            if (requisicaoQuickFit(processoRequisicao)) {
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

    public boolean requisicaoQuickFit(LtgProcesso processo) {
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

    public boolean alocarProcessoBlocosGerais(LtgProcesso processo) {
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

    private void criarListas() {
        for (int i = 0; i < memoria.getQtdeListas(); i++) {
            memoria.configLista(i, tamanhos.get(tamanhos.size() - 1).getTamanho());
            tamanhos.remove(tamanhos.size() - 1);
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

    public boolean existeFilaToHim(LtgProcesso processo) {
        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                return true;
            }
        }
        return false;
    }

    public boolean alocarComQuickFit(LtgProcesso processo) {
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

    public void criarBlocoNaFilaCorrespondente(LtgProcesso processo) {
        memoria.getBlocosGerais().add(new Bloco(memoria.getBlocosGerais().size() + 1, processo.getSize(), processo));

        for (ListasQF l :
                memoria.listas) {
            if (processo.getSize() == l.getParametro()) {
                l.getBloco().add(new Bloco(l.getBloco().size() + 1, processo.getSize(), processo));
            }
        }
    }

    public void alocarNoPrimeiroBlocoLivreDaFilaCorrespondente(LtgProcesso processo) {
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

    public boolean existeBlocoLivreNaLista(LtgProcesso processo) {
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

    private void printTamanhos() {
        for (Tamanho tamanho :
                tamanhos) {
            System.out.println(tamanho.toString());
        }
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

    private void mudarProcessosDeFilaUsandoQuickFit() {
        for (int i = 0; i < cores.length; i++) {
            LtgProcesso processoAtual = cores[i];

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


    private void mudarProcessosDeFilaUsandoBestFit() {
        for (int i = 0; i < cores.length; i++) {
            LtgProcesso processoAtual = cores[i];

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
                    abortados.add(processoRequisicao);
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
        if (gerenciadorDeMemoria == 1)
            mudarProcessosDeFilaUsandoBestFit();
        else
            mudarProcessosDeFilaUsandoQuickFit();
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
