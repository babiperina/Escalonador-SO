package view;

import com.sun.xml.internal.rngom.parse.host.Base;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.bases.BaseProcesso;

public class Processo {

    public static HBox display(BaseProcesso p, int i) {
        HBox processo = new HBox();

        processo = new HBox();
        processo.getChildren().add(new Label(p.toString()));

        if (p.isNovo()) {
            processo.setBackground(new Background(new BackgroundFill(Color.web("#ff0000"), CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            if (i % 2 == 0)
                processo.setBackground(new Background(new BackgroundFill(Color.web("#dfe2dc"), CornerRadii.EMPTY, Insets.EMPTY)));
            else
                processo.setBackground(new Background(new BackgroundFill(Color.web("#b4b5b3"), CornerRadii.EMPTY, Insets.EMPTY)));

            processo.prefWidth(Control.USE_COMPUTED_SIZE);
        }

        return processo;
    }
}
