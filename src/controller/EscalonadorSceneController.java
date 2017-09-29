package controller;

import controller.algoritmos.Ibs;
import controller.algoritmos.Ltg;
import controller.algoritmos.Rr;
import controller.algoritmos.Sjf;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import model.IbsProcesso;
import model.LtgProcesso;
import model.RrProcesso;
import model.SjfProcesso;
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
        ltg = new Ltg((int) coresLtgSlider.getValue(), (int) piLtgSlider.getValue());

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
    Button iniciarRrButton;
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
                            atualizarInterfaceRr();
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
    }

    public void adicionarProcessoAptosRr() {
        rr.adicionarProcesso(new RrProcesso());
    }

    private void verificarStatusBotaoIniciarRr() {
        if (!Config.RR_IS_RUNNING)
            iniciarRrButton.setDisable(false);
    }

    //IBS Algoritmo
   /* private Ibs ibs;
    private ArrayList<IbsProcesso> esperandoIbs;


    @FXML
    Slider coresIbsSlider;
    @FXML
    Slider piIbsSlider;
    @FXML
    Button iniciarIbsButton;
    @FXML
    Button addProcessoIbsButton;

    @FXML
    HBox esperandoIbsHbox;

    @FXML
    TitledPane paneIbs;

    public void iniciarRr() {

        iniciarIbsButton.setDisable(true);
        paneIbs.setExpanded(true);
        addProcessoIbsButton.setDisable(false);
        ibs = new Ibs((int) coresIbsSlider.getValue(), (int) piIbsSlider.getValue());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Config.IBS_IS_RUNNING) {
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
                            atualizarInterfaceRr();
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
    }

    public void adicionarProcessoAptosRr() {
        rr.adicionarProcesso(new RrProcesso());
    }

    private void verificarStatusBotaoIniciarRr() {
        if (!Config.RR_IS_RUNNING)
            iniciarRrButton.setDisable(false);
    }

*/
}


