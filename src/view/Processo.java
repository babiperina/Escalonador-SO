package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.LtgProcesso;
import model.RrProcesso;
import model.SjfProcesso;
import model.enums.Estado;
import util.Config;

public class Processo {

    public static VBox displaySjfProcesso(SjfProcesso p, int i) {
        VBox processo;

        processo = new VBox();

        Label labelCore = new Label("CORE " + (i + 1));
        labelCore.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelTempoRestanteDuracao = new Label(p.getTempoRestante() + "/" + p.getDuracao());

        if (p.getEstado() == Estado.EXECUTANDO.getValor())
            processo.getChildren().addAll(labelCore, new Label("PID: " + p.getId()), new Label(""), labelTempoRestanteDuracao);
        else
            processo.getChildren().addAll(new Label("PID: " + p.getId()), new Label(""), new Label(""), labelTempoRestanteDuracao);


        if (p.isNovo()) {

            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_NOVO_PROCESSO), CornerRadii.EMPTY, Insets.EMPTY)));
            p.setNovo(false);
        } else {
            if (p.getEstado() == Estado.EXECUTANDO.getValor()) {
                if (i % 2 == 0)
                    processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                else
                    processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                if (p.getEstado() == Estado.FINALIZADO.getValor()) {
                    if (i % 2 == 0)
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                    else
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    if (i % 2 == 0)
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                    else
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
                }

            }

        }

        processo.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        processo.setPadding(new Insets(2, 2, 2, 2));
        processo.setPrefSize(100, 50);
        return processo;
    }

    public static VBox displayRrProcesso(RrProcesso p, int i) {
        VBox processo;

        processo = new VBox();

        Label labelTempoRestanteDuracao = new Label(p.getTempoRestante() + "/" + p.getDuracao());

        processo.getChildren().addAll(new Label("CORE " + (i + 1)), new Label("PID: " + p.getId()), new Label(""), labelTempoRestanteDuracao);


        if (p.isNovo()) {

            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_NOVO_PROCESSO), CornerRadii.EMPTY, Insets.EMPTY)));
            p.setNovo(false);
        } else {
            if (p.getEstado() == Estado.EXECUTANDO.getValor()) {
                if (i % 2 == 0)
                    processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                else
                    processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                if (p.getEstado() == Estado.FINALIZADO.getValor()) {
                    if (i % 2 == 0)
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                    else
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    if (i % 2 == 0)
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                    else
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
                }

            }

        }

        processo.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        processo.setPadding(new Insets(2, 2, 2, 2));
        processo.setPrefSize(100, 50);
        return processo;
    }

    public static VBox displayLtgProcesso(LtgProcesso p, int i) {
        VBox processo;

        processo = new VBox();

        Label labelTempoRestanteDuracao = new Label(p.getTempoRestante() + "/" + p.getDuracao());

        processo.getChildren().addAll(new Label("CORE " + (i + 1)), new Label("PID: " + p.getId()), new Label(""), labelTempoRestanteDuracao);


        if (p.isNovo()) {

            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_NOVO_PROCESSO), CornerRadii.EMPTY, Insets.EMPTY)));
            p.setNovo(false);
        } else {
            if (p.getEstado() == Estado.EXECUTANDO.getValor()) {
                if (i % 2 == 0)
                    processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                else
                    processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                if (p.getEstado() == Estado.FINALIZADO.getValor()) {
                    if (i % 2 == 0)
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                    else
                        processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    if (p.getEstado() == Estado.ABORTADO.getValor()) {
                        if (i % 2 == 0)
                            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_ABORTADO_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                        else
                            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_ABORTADO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        if (i % 2 == 0)
                            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
                        else
                            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }

            }

        }

        processo.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        processo.setPadding(new Insets(2, 2, 2, 2));
        processo.setPrefSize(100, 50);
        return processo;
    }


    public static VBox displayCores(int i) {
        VBox processo;

        processo = new VBox();

        Label labelCore = new Label("CORE " + (i + 1));
        labelCore.setFont(Font.font("Verdana", FontWeight.BOLD, 10));

        Label labelVazio = new Label("vazio");
        labelVazio.setFont(Font.font("Verdana", 10));

        processo.getChildren().addAll(labelCore, new Label(""), new Label(""), labelVazio);


        if (i % 2 == 0) //verdes
            processo.setBackground(new Background(new BackgroundFill(Color.web("#afc697"), CornerRadii.EMPTY, Insets.EMPTY)));
        else
            processo.setBackground(new Background(new BackgroundFill(Color.web("#adce8a"), CornerRadii.EMPTY, Insets.EMPTY)));


        processo.setBorder(new

                Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        processo.setPadding(new

                Insets(2, 2, 2, 2));
        processo.setPrefSize(100, 50);
        return processo;
    }

}
