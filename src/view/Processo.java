
package view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.IbsProcesso;
import model.LtgProcesso;
import model.RrProcesso;
import model.SjfProcesso;
import model.bases.BaseProcesso;
import model.enums.Estado;
import util.Config;

import java.text.SimpleDateFormat;

public class Processo {

    private static final Background focusBackground = new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY));
    private static Background unfocusBackground;

    public static VBox displaySjfProcesso(SjfProcesso p, int i) {
        VBox processo;

        processo = new VBox();

        Label labelCore = new Label("CORE " + i);
        labelCore.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelPid = new Label("PID: " + p.getId());
        labelPid.setFont(Font.font("Verdana", 10));
        Label labelTempoRestanteDuracao = new Label("TEMPO: " + p.getTempoRestante() + "/" + p.getDuracao());
        labelTempoRestanteDuracao.setFont(Font.font("Verdana", 10));

        if (p.getEstado() == Estado.EXECUTANDO.getValor())
            processo.getChildren().addAll(labelCore, labelPid, new Label(""), labelTempoRestanteDuracao);
        else
            processo.getChildren().addAll(labelPid, new Label(""), new Label(""), labelTempoRestanteDuracao);

        displayProcesso(processo, p, i);

        return processo;
    }

    public static VBox displayRrProcesso(RrProcesso p, int i) {
        VBox processo;

        processo = new VBox();

        Label labelCore = new Label("CORE " + i);
        labelCore.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelPid = new Label("PID: " + p.getId());
        labelPid.setFont(Font.font("Verdana", 10));
        Label labelTempoRestanteDuracao = new Label("TEMPO: " + p.getTempoRestante() + "/" + p.getDuracao());
        labelTempoRestanteDuracao.setFont(Font.font("Verdana", 10));
        Label labelQuantum = new Label("QUANTUM: " + p.getQuantumRestante() + "/" + p.getQuantum());
        labelQuantum.setFont(Font.font("Verdana", 10));


        if (p.getEstado() == Estado.EXECUTANDO.getValor())
            processo.getChildren().addAll(labelCore, labelPid, labelQuantum, labelTempoRestanteDuracao);
        else
            processo.getChildren().addAll(labelPid, new Label(), labelQuantum, labelTempoRestanteDuracao);

        displayProcesso(processo, p, i);

        return processo;
    }

    public static VBox displayLtgProcesso(LtgProcesso p, int i) {
        VBox processo;

        processo = new VBox();
        Label labelCore = new Label("CORE " + i);
        labelCore.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelPid = new Label("PID: " + p.getId());
        labelPid.setFont(Font.font("Verdana", 10));
        Label labelTempoRestanteDuracao = new Label("TEMPO: " + p.getTempoRestante() + "/" + p.getDuracao());
        labelTempoRestanteDuracao.setFont(Font.font("Verdana", 10));
        Label labelDeadline = new Label("DEADLINE: " + new SimpleDateFormat("HH:mm:ss").format(p.getDeadline()));
        labelDeadline.setFont(Font.font("Verdana", 10));

        if (p.getEstado() == Estado.EXECUTANDO.getValor())
            processo.getChildren().addAll(labelCore, new Label(), labelPid, labelTempoRestanteDuracao);
        else
            processo.getChildren().addAll(labelPid, new Label(), labelDeadline, labelTempoRestanteDuracao);

        displayProcesso(processo, p, i);

        return processo;
    }

    public static VBox displayIbsProcesso(IbsProcesso p, int i) {
        VBox processo;

        processo = new VBox();
        Label labelCore = new Label("CORE " + i);
        labelCore.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelPid = new Label("PID: " + p.getId());
        labelPid.setFont(Font.font("Verdana", 10));
        Label labelTempoRestanteDuracao = new Label("TEMPO: " + p.getTempoRestante() + "/" + p.getDuracao());
        labelTempoRestanteDuracao.setFont(Font.font("Verdana", 10));
        Label labelStart = new Label("START: " + p.getStart());
        labelStart.setFont(Font.font("Verdana", 10));
        Label labelEnd = new Label("END: " + p.getEnd());
        labelEnd.setFont(Font.font("Verdana", 10));

        if (p.getEstado() == Estado.EXECUTANDO.getValor())
            processo.getChildren().addAll(labelCore, labelPid, labelStart, labelEnd, labelTempoRestanteDuracao);
        else
            processo.getChildren().addAll(labelPid, new Label(), labelStart, labelEnd, labelTempoRestanteDuracao);

        displayProcesso(processo, p, i);

        return processo;
    }

    public static VBox displayCores(int i) {
        VBox processo;

        processo = new VBox();

        Label labelCore = new Label("CORE " + i);
        labelCore.setFont(Font.font("Verdana", FontWeight.BOLD, 10));

        Label labelVazio = new Label("vazio");
        labelVazio.setFont(Font.font("Verdana", 10));

        processo.getChildren().addAll(labelCore, new Label(""), new Label(""), labelVazio);


        if (i % 2 == 0) //verdes
            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_CLARO), CornerRadii.EMPTY, Insets.EMPTY)));
        else
            processo.setBackground(new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_ESCURO), CornerRadii.EMPTY, Insets.EMPTY)));


        processo.setBorder(new

                Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        processo.setPadding(new

                Insets(2, 2, 2, 2));
        processo.setPrefSize(120, 50);
        return processo;
    }

    private static void displayProcesso(VBox vb, BaseProcesso p, int i) {
        if (p.isNovo()) {

            unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_NOVO_PROCESSO), CornerRadii.EMPTY, Insets.EMPTY));
            p.setNovo(false);
        } else {
            if (p.getEstado() == Estado.EXECUTANDO.getValor()) {
                if (i % 2 == 0)
                    unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_CLARO), CornerRadii.EMPTY, Insets.EMPTY));
                else
                    unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_CORE_ESCURO), CornerRadii.EMPTY, Insets.EMPTY));
            } else {
                if (p.getEstado() == Estado.FINALIZADO.getValor()) {
                    if (i % 2 == 0)
                        unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_CLARO), CornerRadii.EMPTY, Insets.EMPTY));
                    else
                        unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_FINALIZADO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY));
                } else {
                    if (p.getEstado() == Estado.ABORTADO.getValor()) {
                        if (i % 2 == 0)
                            unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_ABORTADO_CLARO), CornerRadii.EMPTY, Insets.EMPTY));
                        else
                            unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_ABORTADO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY));

                    } else {
                        if (i % 2 == 0)
                            unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_CLARO), CornerRadii.EMPTY, Insets.EMPTY));
                        else
                            unfocusBackground = new Background(new BackgroundFill(Color.web(Config.COLOR_APTO_ESCURO), CornerRadii.EMPTY, Insets.EMPTY));

                    }
                }

            }

        }

        vb.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        vb.setPadding(new Insets(2, 2, 2, 2));
        vb.setPrefSize(120, 50);
        vb.setOnMouseClicked(e -> {
            vb.requestFocus();
            System.out.println(p.toString());
        });

        vb.backgroundProperty().bind(Bindings
                .when(vb.focusedProperty())
                .then(focusBackground)
                .otherwise(unfocusBackground)
        );
    }
}
