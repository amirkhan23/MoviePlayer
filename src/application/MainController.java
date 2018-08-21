package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class MainController implements Initializable {

	@FXML
	private MediaView mv;
	@FXML
	private Slider volumeSilder;
	@FXML
	private Slider seekSlider;
	@FXML
	// private VBox spec;
	private MediaPlayer mp;
	private Media me;
	private String filePath;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void play(ActionEvent event) {
		mp.play();
		mp.setRate(1);
	}

	public void pause(ActionEvent event) {
		mp.pause();
	}

	public void fast(ActionEvent event) {
		mp.setRate(2);
	}

	public void slow(ActionEvent event) {
		mp.setRate(.75);
	}

	public void reload(ActionEvent event) {
		mp.seek(mp.getStartTime());
		mp.play();
	}

	public void fileChooser(ActionEvent event) {
		if (mp != null) {
			// mp.stop();
			mp.dispose();
		}

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("(*.mp4)", "*.mp4"),
				new ExtensionFilter("(*.mkv)", "*.mkv"), new ExtensionFilter("(*.mp3)", "*.mp3"));

		File file = fc.showOpenDialog(null);
		filePath = file.getAbsolutePath().toString();
		if (filePath != null) {
			me = new Media(new File(filePath).toURI().toString());
			mp = new MediaPlayer(me);
			mv.setMediaPlayer(mp);
			// mp.audioSpectrumIntervalProperty().setValue(0.4);
			// mp.audioSpectrumIntervalProperty().getValue();
			// mp.setAudioSpectrumNumBands(50);
			System.out.println(mp.audioSpectrumIntervalProperty().getValue());
			System.out.println(mp.getAudioSpectrumNumBands());

			// mp.setAudioSpectrumNumBands(7);

			DoubleProperty width = mv.fitWidthProperty();
			DoubleProperty height = mv.fitHeightProperty();
			width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
			height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));
			final HBox hbox = new HBox(2);
			final int bands = mp.getAudioSpectrumNumBands();
			final Rectangle[] rect = new Rectangle[bands];
			for (int i = 0; i < rect.length; i++) {
				rect[i] = new Rectangle();
				rect[i].setFill(Color.CHARTREUSE);
				hbox.getChildren().add(rect[i]);
			}

			// spec.getChildren().add(hbox);
			mp.play();

			volumeSilder.setValue(mp.getVolume() * 100);
			volumeSilder.valueProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					mp.setVolume(volumeSilder.getValue() / 100);

				}
			});

			mp.setOnReady(new Runnable() {

				@Override
				public void run() {
					/*
					 * int w = mp.getMedia().getWidth(); int bandWidth = w /
					 * rect.length;
					 * 
					 * spec.setMinWidth(w); for (Rectangle r : rect) {
					 * r.setWidth(bandWidth); r.setHeight(2); }
					 */

					seekSlider.setMin(0.0);
					seekSlider.setMax(mp.getTotalDuration().toSeconds());
				}
			});

			/*
			 * mp.setAudioSpectrumListener(new AudioSpectrumListener() {
			 * 
			 * @Override public void spectrumDataUpdate(double timestamp, double
			 * duration, float[] magnitudes, float[] phases) { for (int i = 0; i
			 * < rect.length; i++) { double h = magnitudes[i] + 60; if (h > 2) {
			 * rect[i].setHeight(h); } }
			 * 
			 * } });
			 */
			mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {

				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration duration,
						Duration current) {

					seekSlider.setValue(current.toSeconds());

				}

			});

		}

		seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				mp.seek(Duration.seconds(seekSlider.getValue()));

			}
		});

	}

}
