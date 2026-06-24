package GUI;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

/**
 * Controller for the report preview window. Displays a snapshot of a generated
 * report as an image.
 */
public class ReportPreviewController {

	@FXML
	private ImageView imageView;

	/**
	 * Sets the image to be displayed in the preview window.
	 *
	 * @param imageBytes The byte array representation of the report image.
	 */
	public void setImage(byte[] imageBytes) {
		Image image = new Image(new ByteArrayInputStream(imageBytes));
		imageView.setImage(image);
	}

	/**
	 * Closes the current report preview window.
	 */
	@FXML
	private void closeWindow() {
		((Stage) imageView.getScene().getWindow()).close();
	}
}