package controller;

import controller.algoritmos.Ltg;
import controller.algoritmos.Rr;
import controller.algoritmos.Sjf;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.LtgProcesso;
import model.RrProcesso;
import model.SjfProcesso;
import util.Config;
import view.Processo;

import java.util.ArrayList;


public class EscalonadorSceneController {


    //SJF Algoritmo
    private Sjf sjf;
    private SjfProcesso[] coresSjf;
    private ArrayList<SjfProcesso> aptosSjf;
    private ArrayList<SjfProcesso> finalizadosSjf;

    @FXML
    Slider coresSjfSlider;
    @FXML
    Slider piSjfSlider;
    @FXML
    Button iniciarSjfButton;
    @FXML
    Button addProcessoSjfButton;

    @FXML
    HBox coresSjfHbox;
    @FXML
    HBox aptosSjfHbox;
    @FXML
    HBox finalizadosSjfHbox;

    @FXML
    TitledPane paneSjf;


    public void iniciarSjf() {

        iniciarSjfButton.setDisable(true);
        paneSjf.setExpanded(true);
        addProcessoSjfButton.setDisable(false);
        sjf = new Sjf((int) coresSjfSlider.getValue(), (int) piSjfSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.SJF_IS_RUNNING) {
                    sjf.atualizarAlgoritmo();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresSjf = sjf.getCores();
                            aptosSjf = sjf.getAptos();
                            finalizadosSjf = sjf.getFinalizados();
                            atualizarInterfaceSjf();
                        }
                    });
                    verificarStatusBotaoIniciar();
                }
            }
        });

        thread.start();

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
    }

    public void adicionarProcessoAptosSjf() {
        sjf.adicionarProcesso(new SjfProcesso());
    }

    private void verificarStatusBotaoIniciar() {
        if (!Config.SJF_IS_RUNNING)
            iniciarSjfButton.setDisable(false);
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
    Button iniciarLtgButton;
    @FXML
    Button addProcessoLtgButton;

    @FXML
    VBox coresLtgVbox;
    @FXML
    VBox aptosLtgVbox;
    @FXML
    VBox finalizadosLtgVbox;
    @FXML
    VBox abortadosLtgVbox;

    @FXML
    TitledPane paneLtg;

    public void iniciarLtg() {

        iniciarLtgButton.setDisable(true);
        paneLtg.setExpanded(true);
        addProcessoLtgButton.setDisable(false);
        ltg = new Ltg((int) coresLtgSlider.getValue(), (int) piLtgSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.LTG_IS_RUNNING) {
                    ltg.atualizarAlgoritmo();
                    try {
                        Thread.sleep(Config.SEGUNDO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresLtg = ltg.getCores();
                            aptosLtg = ltg.getAptos();
                            finalizadosLtg = ltg.getFinalizados();
                            abortadosLtg = ltg.getAbortados();
                            atualizarInterfaceLtg();
                        }
                    });
                    verificarStatusBotaoIniciarLtg();
                }
            }
        });

        thread.start();

    }

    private void atualizarInterfaceLtg() {
        coresLtgVbox.getChildren().clear();
        for (int i = 0; i < coresLtg.length; i++) {
            if (coresLtg[i] != null) {
                coresLtgVbox.getChildren().add(new Label(coresLtg[i].toString()));
            } else {
                coresLtgVbox.getChildren().add(new Label("Core " + (i + 1) + " vazio."));
            }
        }

        aptosLtgVbox.getChildren().clear();
        for (int i = 0; i < aptosLtg.size(); i++) {
            aptosLtgVbox.getChildren().add(new Label(aptosLtg.get(i).toString()));
        }

        finalizadosLtgVbox.getChildren().clear();
        for (int i = 0; i < finalizadosLtg.size(); i++) {
            finalizadosLtgVbox.getChildren().add(new Label(finalizadosLtg.get(i).toString()));
        }

        abortadosLtgVbox.getChildren().clear();
        for (int i = 0; i < abortadosLtg.size(); i++) {
            abortadosLtgVbox.getChildren().add(new Label(abortadosLtg.get(i).toString()));
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
    private ArrayList<RrProcesso> finalizadosRr;
    private ArrayList<RrProcesso> abortadosRr;


    @FXML
    Slider coresRrSlider;
    @FXML
    Slider piRrSlider;
    @FXML
    Slider quantumRrSlider;
    @FXML
    Button iniciarRrButton;
    @FXML
    Button addProcessoRrButton;

    @FXML
    HBox coresRrHbox;
    @FXML
    HBox aptosRrHbox;
    @FXML
    HBox finalizadosRrHbox;

    @FXML
    TitledPane paneRr;

    public void iniciarRr() {

        iniciarRrButton.setDisable(true);
        paneRr.setExpanded(true);
        addProcessoRrButton.setDisable(false);
        rr = new Rr((int) coresRrSlider.getValue(), (int) piRrSlider.getValue(), (int) quantumRrSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.RR_IS_RUNNING) {
                    rr.atualizarAlgoritmo();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            coresRr = rr.getCores();
                            aptosRr = rr.getAptos();
                            finalizadosRr = rr.getFinalizados();
                            atualizarInterfaceRr();
                        }
                    });
                    verificarStatusBotaoIniciarRr();
                }
            }
        });

        thread.start();

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

        finalizadosRrHbox.getChildren().clear();
        for (int i = 0; i < finalizadosRr.size(); i++) {
            finalizadosRrHbox.getChildren().add(Processo.displayRrProcesso(finalizadosRr.get(i), i));
        }
    }

    public void adicionarProcessoAptosRr() {
        rr.adicionarProcesso(new RrProcesso());
    }

    private void verificarStatusBotaoIniciarRr() {
        if (!Config.LTG_IS_RUNNING)
            iniciarLtgButton.setDisable(false);
    }


}
