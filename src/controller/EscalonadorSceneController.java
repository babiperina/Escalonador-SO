package controller;

import controller.algoritmosEscalonamento.Ibs;
import controller.algoritmosEscalonamento.Ltg;
import controller.algoritmosEscalonamento.Rr;
import controller.algoritmosEscalonamento.Sjf;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import model.*;
import util.Config;
import view.Processo;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class EscalonadorSceneController implements Initializable {

    @FXML
    Label relogioIcon;
    @FXML
    Label relogio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        relogioIcon.setText("RELOGIO: ");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            relogio.setText(new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()));
                        }
                    });

                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }


    @FXML
    HBox memoria;

    @FXML
    TitledPane nameMemoria;

    //SJF Algoritmo
    private Sjf sjf;
    private SjfProcesso[] coresSjf;
    private ArrayList<SjfProcesso> aptosSjf;
    private ArrayList<SjfProcesso> finalizadosSjf;
    private ArrayList<SjfProcesso> abortadosSjf;

    @FXML
    Slider coresSjfSlider;
    @FXML
    Slider piSjfSlider;
    @FXML
    Slider memoriaSjfSlider;
    @FXML
    Slider requisicoesSjfSlider;
    @FXML
    Slider listasSjfSlider;
    @FXML
    Button iniciarSjfButton;
    @FXML
    Button iniciarSjfButtonQ;
    @FXML
    Button addProcessoSjfButton;

    @FXML
    HBox coresSjfHbox;
    @FXML
    HBox aptosSjfHbox;
    @FXML
    HBox finalizadosSjfHbox;
    @FXML
    HBox abortadosSjfHbox;

    @FXML
    TitledPane paneSjf;


    public void iniciarSjf() {

        iniciarSjfButton.setDisable(true);
        iniciarSjfButtonQ.setDisable(true);
        paneSjf.setExpanded(true);
        addProcessoSjfButton.setDisable(false);
        sjf = new Sjf((int) coresSjfSlider.getValue(), (int) piSjfSlider.getValue(), (int) memoriaSjfSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.SJF_IS_RUNNING) {

                    sjf.atualizarAlgoritmo();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresSjf = sjf.getCores();
                            aptosSjf = sjf.getAptos();
                            finalizadosSjf = sjf.getFinalizados();
                            abortadosSjf = sjf.getAbortados();
                            atualizarInterfaceSjf();
                            if (paneSjf.isExpanded()){
                                printMemoriaDoSjfBest();
                            }
                        }
                    });
                    verificarStatusBotaoIniciar();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }

    public void iniciarSjfQ() {

        iniciarSjfButton.setDisable(true);
        iniciarSjfButtonQ.setDisable(true);
        paneSjf.setExpanded(true);
        addProcessoSjfButton.setDisable(false);
        sjf = new Sjf((int) coresSjfSlider.getValue(), (int) piSjfSlider.getValue(), (int) memoriaSjfSlider.getValue(),(int) requisicoesSjfSlider.getValue(),(int) listasSjfSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.SJF_IS_RUNNING) {

                    sjf.atualizarAlgoritmo();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresSjf = sjf.getCores();
                            aptosSjf = sjf.getAptos();
                            finalizadosSjf = sjf.getFinalizados();
                            abortadosSjf = sjf.getAbortados();
                            atualizarInterfaceSjf();
                            if (paneSjf.isExpanded()){
                                printMemoriaDoSjfQuick();
                            }
                        }
                    });
                    verificarStatusBotaoIniciar();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }

    private void printMemoriaDoSjfQuick(){
        Memoria m = sjf.getMemoria();
        nameMemoria.setText("Memória SJF QuickFit (" + m.getTamanho() + "kb) Filas: " + m.listas.length + " Requisições Feitas: " + m.getQtdeRequisicoesFeitas());

        memoria.getChildren().clear();
        if(m.listas!= null)
            for(ListasQF lista: m.listas){
                if(lista!= null){
                    for (Bloco b :
                            lista.getBloco()) {
                        memoria.getChildren().add(view.Memoria.displayBlocoComCor(b, lista.getR(), lista.getG(), lista.getB()));
                    }
                }
            }

        for (Bloco b :
                m.getBlocos()) {
            memoria.getChildren().add(view.Memoria.displayBloco(b));
        }



        memoria.getChildren().add(view.Memoria.displayMemoriaNaoAlocada(m.getMemoriaLivre()));

    }

    private void printMemoriaDoSjfBest(){
        Memoria m = sjf.getMemoria();
        nameMemoria.setText("Memória SJF BestFit (" + m.getTamanho() + "kb)");

        memoria.getChildren().clear();
        if(m.listas!= null)
        for(ListasQF lista: m.listas){
            if(lista!= null){
                for (Bloco b :
                        lista.getBloco()) {
                    memoria.getChildren().add(view.Memoria.displayBlocoComCor(b, lista.getR(), lista.getG(), lista.getB()));
                }
            }
        }

        for (Bloco b :
                m.getBlocos()) {
            memoria.getChildren().add(view.Memoria.displayBloco(b));
        }



        memoria.getChildren().add(view.Memoria.displayMemoriaNaoAlocada(m.getMemoriaLivre()));

    }

    private void atualizarInterfaceSjf() {
        coresSjfHbox.getChildren().clear();
        for (int i = 0; i < coresSjf.length; i++) {
            if (coresSjf[i] != null) {
                coresSjfHbox.getChildren().add(Processo.displaySjfProcesso(coresSjf[i], i));
            } else {
                coresSjfHbox.getChildren().add(Processo.displayCores(i));
            }
        }

        aptosSjfHbox.getChildren().clear();
        for (int i = 0; i < aptosSjf.size(); i++) {
            aptosSjfHbox.getChildren().add(Processo.displaySjfProcesso(aptosSjf.get(i), i));
            aptosSjf.get(i).setNovo(false);
        }

        finalizadosSjfHbox.getChildren().clear();
        for (int i = 0; i < finalizadosSjf.size(); i++) {
            finalizadosSjfHbox.getChildren().add(Processo.displaySjfProcesso(finalizadosSjf.get(i), i));
            finalizadosSjf.get(i).setNovo(false);
        }

        abortadosSjfHbox.getChildren().clear();
        for (int i = 0; i < abortadosSjf.size(); i++) {
            abortadosSjfHbox.getChildren().add(Processo.displaySjfProcesso(abortadosSjf.get(i), i));
            abortadosSjf.get(i).setNovo(false);
        }
    }

    public void adicionarProcessoAptosSjf() {
        sjf.adicionarProcesso(new SjfProcesso());
    }

    private void verificarStatusBotaoIniciar() {
        if (!Config.SJF_IS_RUNNING) {
            iniciarSjfButton.setDisable(false);
            iniciarSjfButtonQ.setDisable(false);
        }
    }



    //LTG Algoritmo
    private Ltg ltg;
    private LtgProcesso[] coresLtg;
    private ArrayList<LtgProcesso> aptosLtg;
    private ArrayList<LtgProcesso> finalizadosLtg;
    private ArrayList<LtgProcesso> abortadosLtg;


    @FXML
    Slider coresLtgSlider;
    @FXML
    Slider piLtgSlider;
    @FXML
    Slider memoriaLtgSlider;
    @FXML
    Button iniciarLtgButton;
    @FXML
    Button addProcessoLtgButton;

    @FXML
    HBox coresLtgHbox;
    @FXML
    HBox aptosLtgHbox;
    @FXML
    HBox finalizadosLtgHbox;
    @FXML
    HBox abortadosLtgHbox;

    @FXML
    TitledPane paneLtg;

    public void iniciarLtg() {

        iniciarLtgButton.setDisable(true);
        paneLtg.setExpanded(true);
        addProcessoLtgButton.setDisable(false);
        ltg = new Ltg((int) coresLtgSlider.getValue(), (int) piLtgSlider.getValue(), (int) memoriaLtgSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.LTG_IS_RUNNING) {
                    ltg.atualizarAlgoritmo();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresLtg = ltg.getCores();
                            aptosLtg = ltg.getAptos();
                            finalizadosLtg = ltg.getFinalizados();
                            abortadosLtg = ltg.getAbortados();
                            atualizarInterfaceLtg();
                            if (paneLtg.isExpanded()){
                                printMemoriaDoLtg();
                            }
                        }
                    });
                    verificarStatusBotaoIniciarLtg();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }

    private void printMemoriaDoLtg(){
        Memoria m = ltg.getMemoria();
        nameMemoria.setText("Memória LTG BestFit (" + m.getTamanho() + "kb)");

        memoria.getChildren().clear();
        for (Bloco b :
                m.getBlocos()) {
            memoria.getChildren().add(view.Memoria.displayBloco(b));
        }
        memoria.getChildren().add(view.Memoria.displayMemoriaNaoAlocada(m.getMemoriaLivre()));

    }

    private void atualizarInterfaceLtg() {
        coresLtgHbox.getChildren().clear();
        for (int i = 0; i < coresLtg.length; i++) {
            if (coresLtg[i] != null) {
                coresLtgHbox.getChildren().add(Processo.displayLtgProcesso(coresLtg[i], i));
            } else {
                coresLtgHbox.getChildren().add(Processo.displayCores(i));
            }
        }

        aptosLtgHbox.getChildren().clear();
        for (int i = 0; i < aptosLtg.size(); i++) {
            aptosLtgHbox.getChildren().add(Processo.displayLtgProcesso(aptosLtg.get(i), i));
        }

        finalizadosLtgHbox.getChildren().clear();
        for (int i = 0; i < finalizadosLtg.size(); i++) {
            finalizadosLtgHbox.getChildren().add(Processo.displayLtgProcesso(finalizadosLtg.get(i), i));
        }

        abortadosLtgHbox.getChildren().clear();
        for (int i = 0; i < abortadosLtg.size(); i++) {
            abortadosLtgHbox.getChildren().add(Processo.displayLtgProcesso(abortadosLtg.get(i), i));
        }
    }

    public void adicionarProcessoAptosLtg() {
        ltg.adicionarProcesso(new LtgProcesso());
    }

    private void verificarStatusBotaoIniciarLtg() {
        if (!Config.LTG_IS_RUNNING)
            iniciarLtgButton.setDisable(false);
    }


    //RR Algoritmo
    private Rr rr;
    private RrProcesso[] coresRr;
    private ArrayList<RrProcesso> aptosRr;
    private ArrayList<RrProcesso> p0;
    private ArrayList<RrProcesso> p1;
    private ArrayList<RrProcesso> p2;
    private ArrayList<RrProcesso> p3;
    private ArrayList<RrProcesso> finalizadosRr;
    private ArrayList<RrProcesso> abortadosRr;


    @FXML
    Slider coresRrSlider;
    @FXML
    Slider piRrSlider;
    @FXML
    Slider quantumRrSlider;
    @FXML
    Slider memoriaRrSlider;
    @FXML
    Button iniciarRrButton;
    @FXML
    Button iniciarRrButtonQ;
    @FXML
    Button addProcessoRrButton;

    @FXML
    HBox coresRrHbox;
    @FXML
    HBox aptosRrHbox;
    @FXML
    HBox prioridadeZeroHbox;
    @FXML
    HBox prioridadeUmHbox;
    @FXML
    HBox prioridadeDoisHbox;
    @FXML
    HBox prioridadeTresHbox;
    @FXML
    HBox finalizadosRrHbox;
    @FXML
    HBox abortadosRrHbox;

    @FXML
    TitledPane paneRr;

    public void iniciarRr() {

        iniciarRrButton.setDisable(true);
        iniciarRrButtonQ.setDisable(true);
        paneRr.setExpanded(true);
        addProcessoRrButton.setDisable(false);
        rr = new Rr((int) coresRrSlider.getValue(), (int) piRrSlider.getValue(), (int) quantumRrSlider.getValue(), (int) memoriaRrSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.RR_IS_RUNNING) {
                    rr.atualizarAlgoritmo();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresRr = rr.getCores();
                            aptosRr = rr.getAptos();
                            p0 = rr.getP0();
                            p1 = rr.getP1();
                            p2 = rr.getP2();
                            p3 = rr.getP3();
                            finalizadosRr = rr.getFinalizados();
                            abortadosRr = rr.getAbortados();
                            atualizarInterfaceRr();
                            if (paneRr.isExpanded()){
                                printMemoriaDoRr();
                            }
                        }
                    });
                    verificarStatusBotaoIniciarRr();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }

    public void iniciarRrQ() {

        iniciarRrButton.setDisable(true);
        iniciarRrButtonQ.setDisable(true);
        paneRr.setExpanded(true);
        addProcessoRrButton.setDisable(false);
        rr = new Rr((int) coresRrSlider.getValue(), (int) piRrSlider.getValue(), (int) quantumRrSlider.getValue(), (int) memoriaRrSlider.getValue(), 20, 4);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.RR_IS_RUNNING) {
                    rr.atualizarAlgoritmo();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresRr = rr.getCores();
                            aptosRr = rr.getAptos();
                            p0 = rr.getP0();
                            p1 = rr.getP1();
                            p2 = rr.getP2();
                            p3 = rr.getP3();
                            finalizadosRr = rr.getFinalizados();
                            abortadosRr = rr.getAbortados();
                            atualizarInterfaceRr();
                            if (paneRr.isExpanded()){
                                printMemoriaDoRrQ();
                            }
                        }
                    });
                    verificarStatusBotaoIniciarRr();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }

    private void printMemoriaDoRr(){
        Memoria m = rr.getMemoria();
        nameMemoria.setText("Memória RR BestFit (" + m.getTamanho() + "kb)");

        memoria.getChildren().clear();
        for (Bloco b :
                m.getBlocos()) {
            memoria.getChildren().add(view.Memoria.displayBloco(b));
        }
        memoria.getChildren().add(view.Memoria.displayMemoriaNaoAlocada(m.getMemoriaLivre()));

    }

    private void printMemoriaDoRrQ(){
        Memoria m = rr.getMemoria();
        nameMemoria.setText("Memória RR QuickFit (" + m.getTamanho() + "kb)");

        memoria.getChildren().clear();
        if(m.listas!= null)
            for(ListasQF lista: m.listas){
                if(lista!= null){
                    for (Bloco b :
                            lista.getBloco()) {
                        memoria.getChildren().add(view.Memoria.displayBlocoComCor(b, lista.getR(), lista.getG(), lista.getB()));
                    }
                }
            }

        for (Bloco b :
                m.getBlocos()) {
            memoria.getChildren().add(view.Memoria.displayBloco(b));
        }
        memoria.getChildren().add(view.Memoria.displayMemoriaNaoAlocada(m.getMemoriaLivre()));

    }

    private void atualizarInterfaceRr() {
        coresRrHbox.getChildren().clear();
        for (int i = 0; i < coresRr.length; i++) {
            if (coresRr[i] != null) {
                coresRrHbox.getChildren().add(Processo.displayRrProcesso(coresRr[i], i));
            } else {
                coresRrHbox.getChildren().add(Processo.displayCores(i));
            }
        }

        aptosRrHbox.getChildren().clear();
        for (int i = 0; i < aptosRr.size(); i++) {
            aptosRrHbox.getChildren().add(Processo.displayRrProcesso(aptosRr.get(i), i));
        }

        prioridadeZeroHbox.getChildren().clear();
        for (int i = 0; i < p0.size(); i++) {
            prioridadeZeroHbox.getChildren().add(Processo.displayRrProcesso(p0.get(i), i));
        }

        prioridadeUmHbox.getChildren().clear();
        for (int i = 0; i < p1.size(); i++) {
            prioridadeUmHbox.getChildren().add(Processo.displayRrProcesso(p1.get(i), i));
        }

        prioridadeDoisHbox.getChildren().clear();
        for (int i = 0; i < p2.size(); i++) {
            prioridadeDoisHbox.getChildren().add(Processo.displayRrProcesso(p2.get(i), i));
        }

        prioridadeTresHbox.getChildren().clear();
        for (int i = 0; i < p3.size(); i++) {
            prioridadeTresHbox.getChildren().add(Processo.displayRrProcesso(p3.get(i), i));
        }


        finalizadosRrHbox.getChildren().clear();
        for (int i = 0; i < finalizadosRr.size(); i++) {
            finalizadosRrHbox.getChildren().add(Processo.displayRrProcesso(finalizadosRr.get(i), i));
        }

        abortadosRrHbox.getChildren().clear();
        for (int i = 0; i < abortadosRr.size(); i++) {
            abortadosRrHbox.getChildren().add(Processo.displayRrProcesso(abortadosRr.get(i), i));
        }
    }

    public void adicionarProcessoAptosRr() {
        rr.adicionarProcesso(new RrProcesso());
    }

    private void verificarStatusBotaoIniciarRr() {
        if (!Config.RR_IS_RUNNING) {
            iniciarRrButton.setDisable(false);
            iniciarRrButtonQ.setDisable(false);
        }
    }

    //IBS Algoritmo
    private Ibs ibs;
    private ArrayList<IbsProcesso> executandoIbs;
    private ArrayList<IbsProcesso> esperandoIbs;
    private ArrayList<IbsProcesso> abortadosIbs;
    private ArrayList<IbsProcesso> finalizadosIbs;


    @FXML
    Slider coresIbsSlider;
    @FXML
    Slider piIbsSlider;
    @FXML
    Button iniciarIbsButton;
    @FXML
    Button addProcessoIbsButton;

    @FXML
    HBox executandoIbsHbox;
    @FXML
    HBox esperandoIbsHbox;
    @FXML
    HBox abortadosIbsHbox;
    @FXML
    HBox finalizadosIbsHbox;

    @FXML
    TitledPane paneIbs;

    public void iniciarIbs() {

        iniciarIbsButton.setDisable(true);
        paneIbs.setExpanded(true);
        addProcessoIbsButton.setDisable(false);
        ibs = new Ibs((int) coresIbsSlider.getValue(), (int) piIbsSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.IBS_IS_RUNNING) {
                    ibs.atualizarAlgoritmo();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            executandoIbs = ibs.getExecutando();
                            esperandoIbs = ibs.getEsperando();
                            abortadosIbs = ibs.getAbortados();
                            finalizadosIbs = ibs.getFinalizados();
                            atualizarInterfaceIbs();
                        }
                    });
                    verificarStatusBotaoIniciarIbs();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }

    private void atualizarInterfaceIbs() {
//        coresRrHbox.getChildren().clear();
//        for (int i = 0; i < coresRr.length; i++) {
//            if (coresRr[i] != null) {
//                coresRrHbox.getChildren().add(Processo.displayRrProcesso(coresRr[i], i));
//            } else {
//                coresRrHbox.getChildren().add(Processo.displayCores(i));
//            }
//        }

        executandoIbsHbox.getChildren().clear();
        for (int i = 0; i < executandoIbs.size(); i++) {
            if (executandoIbs.get(i) != null)
                executandoIbsHbox.getChildren().add(Processo.displayIbsProcesso(executandoIbs.get(i), i));
            else
                executandoIbsHbox.getChildren().add(Processo.displayCores(i));

        }

        esperandoIbsHbox.getChildren().clear();
        for (int i = 0; i < esperandoIbs.size(); i++) {
            esperandoIbsHbox.getChildren().add(Processo.displayIbsProcesso(esperandoIbs.get(i), i));
        }
        abortadosIbsHbox.getChildren().clear();
        for (int i = 0; i < abortadosIbs.size(); i++) {
            abortadosIbsHbox.getChildren().add(Processo.displayIbsProcesso(abortadosIbs.get(i), i));
        }
        finalizadosIbsHbox.getChildren().clear();
        for (int i = 0; i < finalizadosIbs.size(); i++) {
            finalizadosIbsHbox.getChildren().add(Processo.displayIbsProcesso(finalizadosIbs.get(i), i));
        }

    }

    public void adicionarProcessoEsperandoIbs() {
        ibs.adicionarProcesso(new IbsProcesso());
    }

    private void verificarStatusBotaoIniciarIbs() {
        if (!Config.IBS_IS_RUNNING)
            iniciarIbsButton.setDisable(false);
    }



}


