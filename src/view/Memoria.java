package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Bloco;

public class Memoria {

    public static VBox displayBloco(Bloco bloco) {
        VBox b = new VBox();

        Label labelID = new Label("ID: " + bloco.getId());
        labelID.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelSize = new Label(bloco.getTamanho() + "kb");
        labelSize.setFont(Font.font("Verdana", 10));
        Label labelPid;
        if (bloco.isLivre()) {
            labelPid = new Label("LIVRE");
        } else {
            labelPid = new Label("PID: " + bloco.getProcesso().getId());
        }
        labelPid.setFont(Font.font("Verdana", 10));

        b.getChildren().addAll(labelID, labelSize, labelPid);

        b.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        b.setPadding(new Insets(2, 2, 2, 2));
        b.setPrefSize(60 + bloco.getTamanho() / 4, 50);
        return b;
    }

    public static VBox displayBlocoComCor(Bloco bloco, int R, int G, int B) {
        VBox b = new VBox();

        Label labelID = new Label("ID: " + bloco.getId());
        labelID.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelSize = new Label(bloco.getTamanho() + "kb");
        labelSize.setFont(Font.font("Verdana", 10));
        Label labelPid;
        if (bloco.isLivre()) {
            labelPid = new Label("LIVRE");
        } else {
            labelPid = new Label("PID: " + bloco.getProcesso().getId());
        }
        labelPid.setFont(Font.font("Verdana", 10));

        b.getChildren().addAll(labelID, labelSize, labelPid);

        Color myColor = Color.web("rgb("+R+","+G+","+B+")");// blue as an rgb web value, implicit alpha
//        Color.color(R, G, B);
        b.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        b.setPadding(new Insets(2, 2, 2, 2));
        b.setBackground(new Background(new BackgroundFill(myColor, CornerRadii.EMPTY, Insets.EMPTY)));
        b.setPrefSize(60 + bloco.getTamanho() / 4, 50);
        return b;
    }


    public static VBox displayMemoriaNaoAlocada(int tamanho) {
        VBox b = new VBox();

        Label labelID = new Label("M.N.A");
        labelID.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        Label labelSize = new Label(tamanho + "kb");
        labelSize.setFont(Font.font("Verdana", 10));

        b.getChildren().addAll(labelID, labelSize);

        b.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        b.setPadding(new Insets(2, 2, 2, 2));
        b.setPrefSize(60 + tamanho / 4, 50);
        return b;
    }
}
