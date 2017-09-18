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
    VBox coresSjfVbox;
    @FXML
    VBox aptosSjfVbox;
    @FXML
    VBox finalizadosSjfVbox;

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
                        Thread.sleep(500);
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
        HBox hbox;
        coresSjfVbox.getChildren().clear();
        for (int i = 0; i < coresSjf.length; i++) {
            if (coresSjf[i] != null) {
                coresSjfVbox.getChildren().add(Processo.display(coresSjf[i], i));
            } else {
                hbox = new HBox();
                hbox.getChildren().add(new Label("Core " + (i + 1) + " vazio."));

                if (i % 2 == 0)
                    hbox.setBackground(new Background(new BackgroundFill(Color.web("#dfe2dc"), CornerRadii.EMPTY, Insets.EMPTY)));
                else
                    hbox.setBackground(new Background(new BackgroundFill(Color.web("#b4b5b3"), CornerRadii.EMPTY, Insets.EMPTY)));

                hbox.prefWidth(Control.USE_COMPUTED_SIZE);
                coresSjfVbox.getChildren().add(hbox);
            }
        }

        aptosSjfVbox.getChildren().clear();
        for (int i = 0; i < aptosSjf.size(); i++) {
            aptosSjfVbox.getChildren().add(Processo.display(aptosSjf.get(i), i));
            aptosSjf.get(i).setNovo(false);
        }

        finalizadosSjfVbox.getChildren().clear();
        for (int i = 0; i < finalizadosSjf.size(); i++) {
            finalizadosSjfVbox.getChildren().add(Processo.display(finalizadosSjf.get(i), i));
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
                        Thread.sleep(500);
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
    VBox coresRrVbox;
    @FXML
    VBox aptosRrVbox;
    @FXML
    VBox finalizadosRrVbox;

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
        coresRrVbox.getChildren().clear();
        for (int i = 0; i < coresRr.length; i++) {
            if (coresRr[i] != null) {
                coresRrVbox.getChildren().add(new Label(coresRr[i].toString()));
            } else {
                coresRrVbox.getChildren().add(new Label("Core " + (i + 1) + " vazio."));
            }
        }

        aptosRrVbox.getChildren().clear();
        for (int i = 0; i < aptosRr.size(); i++) {
            aptosRrVbox.getChildren().add(new Label(aptosRr.get(i).toString()));
        }

        finalizadosRrVbox.getChildren().clear();
        for (int i = 0; i < finalizadosRr.size(); i++) {
            finalizadosRrVbox.getChildren().add(new Label(finalizadosRr.get(i).toString()));
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
