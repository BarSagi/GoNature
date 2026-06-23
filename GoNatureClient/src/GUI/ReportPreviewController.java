package GUI;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

public class ReportPreviewController {

    @FXML
    private ImageView imageView;

    public void setImage(byte[] imageBytes) {
        Image image = new Image(new ByteArrayInputStream(imageBytes));
        imageView.setImage(image);
    }

    @FXML
    private void closeWindow() {
        ((Stage) imageView.getScene().getWindow()).close();
    }
}