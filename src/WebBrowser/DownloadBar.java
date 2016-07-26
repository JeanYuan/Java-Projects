package WebBrowser;

import javafx.scene.media.AudioClip;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * This is DownloadBar class to handle file download
 * 
 * @author Jean
 *
 */
public class DownloadBar extends HBox {

	/**
	 * This is a static Stage object
	 */
	private static Stage downloadWindow = null;
	/**
	 * This is a static VBox object
	 */
	private static VBox downloadTasks;
	/**
	 * This is a static TextArea object
	 */
	private static TextArea messageArea;
	/**
	 * This is a Text object to display the file name
	 */
	private Text fileName;
	/**
	 * This is a ProgressBar object to represent the progress of file download
	 */
	private ProgressBar barProgress;
	/**
	 * This is a Button object to cancel file download
	 */
	private Button bCancel = new Button("Cancel");
	/**
	 * They are String objects for target file name, download link, and saving
	 * parth
	 */
	private String url, fName, fbName, ext, sfName, dlDir, sfPath;
	/**
	 * This is a long variable for file size
	 */
	private long fLen = 0;

	/**
	 * This is a method to initialize the file download window Calling this
	 * function will guarantee that the downloadTasks VBox is created and
	 * visible.
	 * 
	 * @return A Stage that will show each downloadTask's progress Stage
	 */
	public Stage getDownloadWindow() {
		if (downloadWindow == null) {
			// Create a new borderPane for the download window
			BorderPane downloadRoot = new BorderPane();
			downloadTasks = new VBox();
			// downloadTasks will contain rows of DownloadTask objects, which
			// are HBoxes
			downloadRoot.setCenter(downloadTasks);

			// The bottom of the window will be the message box for download
			// tasks
			downloadRoot.setBottom(messageArea = new TextArea());
			downloadWindow = new Stage();
			downloadWindow.setScene(new Scene(downloadRoot, 400, 600));

			// When closing the window, set the variable downloadWindow to null
			downloadWindow.setOnCloseRequest(event -> downloadWindow = null);
		}
		return downloadWindow;
	}

	/**
	 * This is a constructor for a DownloadTask
	 * 
	 * @param newLocation
	 *            The String URL of a file to download
	 */
	public DownloadBar(String newLocation) {
		// See if the filename at the end of newLocation exists on your hard
		// drive.
		// If the file already exists, then add (1), (2), ... (n) until you find
		// a new filename that doesn't exist.
		String line;
		// load the download folder from saved setting file or set a default
		// folder
		try (BufferedReader tis = Files.newBufferedReader(Paths.get("setting.txt"), StandardCharsets.UTF_8);) {
			while ((line = tis.readLine()) != null) {
				if (line.contains("downloadDirectory") && !line.split("=")[1].contains("null")) {
					dlDir = line.split("=")[1];
				} else {
					dlDir = "Download folder";
				}
			}
		} catch (Exception e) {
			System.out.println("Default download folder is used.");
			dlDir = "Download folder";
		}
		url = newLocation;
		// extract file name from the download link
		fName = new String(newLocation.substring(newLocation.lastIndexOf("/") + 1));
		// set the target file name
		sfName = fName;

		int i = 0;
		// get all of the files in the download folder
		List<String> arrayFile = Arrays.asList(new File("Download folder").list());
		// update target file name if the file exists
		if (sfName != null) {
			while (arrayFile.contains(sfName)) {
				i++;
				sfName = fName.substring(0, fName.lastIndexOf(".")) + "(" + i + ")"
						+ fName.substring(fName.lastIndexOf("."));
			}
		}

		// Create the window if it doesn't exist. After this call, the VBox and
		// TextArea should exist.
		getDownloadWindow();

		DownloadTask aFileDownload = new DownloadTask(this);

		// Add a Text label for the filename
		fileName = new Text(sfName);

		// Add a ProgressBar to show the progress of the task
		barProgress = new ProgressBar(0);
		barProgress.progressProperty().unbind();

		// Add a cancel button that asks the user for confirmation, and cancel
		// the task if the user agrees
		bCancel.setOnAction(evt -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Cancel");
			alert.setHeaderText("Cancel Download");
			alert.setContentText("Do you really want to cancel?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				aFileDownload.cancel();
				barProgress.progressProperty().unbind();
				barProgress.setProgress(0);
			}
		});
		this.getChildren().add(fileName);
		this.getChildren().add(barProgress);
		this.getChildren().add(bCancel);
		downloadTasks.getChildren().add(this);
		// Start the download

		new Thread(aFileDownload).start();
		downloadWindow.show();
	}

	/**
	 * This is an inner class to handle download task This class represents a
	 * task that will be run in a separate thread. It will run call(), and then
	 * call succeeded, cancelled, or failed depending on whether the task was
	 * cancelled or failed. If it was not, then it will call succeeded() after
	 * call() finishes.
	 */
	private class DownloadTask extends Task<String> {
		/**
		 * This is a DownloadBar object
		 */
		private DownloadBar myBar;

		/**
		 * This is a constructor
		 * 
		 * @param downloadBar
		 *            DownloadBar
		 */
		public DownloadTask(DownloadBar downloadBar) {
			myBar = downloadBar;
		}

		// This should start the download. Look at the downloadFile() function
		// at:
		// http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url
		// Take that function but change it so that it updates the progress bar
		// as it iterates through the while loop.
		// Here is a tutorial on how to upgrade a progress bar:
		// https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/progress.htm

		/**
		 * This is a method to start file download and monitor the downloading
		 * progress
		 * 
		 * @return String
		 */
		@Override
		protected String call() throws Exception {
			URL dURL = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) dURL.openConnection();
			int responseCode = httpConn.getResponseCode();

			// check connection of the download link
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// read file object
				InputStream inStream = httpConn.getInputStream();
				// get file size
				long flen = httpConn.getContentLength();
				// set target file path
				sfPath = dlDir + File.separator + sfName;

				// write file object
				FileOutputStream outStream = new FileOutputStream(sfPath);

				int bRead = -1;
				int i = 0;
				/**
				 * This is a byte array to control the iteration of read/write
				 * file step
				 */
				byte[] buffer = new byte[4096];
				long dsize = 0;

				// read and write file
				while ((bRead = inStream.read(buffer)) >= 0) {
					// check if the task is cancelled
					if (isCancelled()) {
						updateMessage("Cancelled");
						break;
					}

					outStream.write(buffer, 0, bRead);
					dsize += bRead;
					float st = (float) dsize / (float) flen;

					// update progressbar progress
					barProgress.setProgress(st);
				}
				// close both files
				outStream.close();
				inStream.close();

				// remove partially downloaded file from the disk if the task
				// cancelled
				// (file cannot be removed before file close)
				if (isCancelled()) {
					try {
						Files.delete(Paths.get(sfPath));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						messageArea.appendText("Could not delete " + sfPath);
					}
				}
			} else {
				messageArea.appendText("No file to download. Server replied HTTP code: " + responseCode);
			}
			httpConn.disconnect();// disconnect the download link			
			return "Finished";
		}

		/**
		 * This is a method to handle the actions after file downloaded
		 * successfully
		 */
		@Override
		protected void succeeded() {
			super.succeeded();
			barProgress.progressProperty().unbind();
			
			/**
			 * Bonus d): animation of downloadtask
			 */
			// add animations
			TranslateTransition tt = new TranslateTransition(Duration.millis(1500), myBar);
			tt.setToY(downloadWindow.getY()+downloadWindow.getHeight());
			tt.setAutoReverse(false);
			tt.setCycleCount(1);

			FadeTransition ft = new FadeTransition(Duration.millis(1500), myBar);
			ft.setFromValue(myBar.getOpacity());
			ft.setToValue(0);
			ft.setCycleCount(1);
			ft.setAutoReverse(false);

			// This is a ParallelTransition with TranslateTransition and FadeTransition
			ParallelTransition pt = new ParallelTransition();

			pt.getChildren().addAll(tt, ft);
			//stt.setAutoReverse(true);
			pt.play();
			
			// Play an audio clip as a notification
			URL res = getClass().getResource("button-25.wav");
			AudioClip clip = new AudioClip(res.toString());
			clip.play(1.0);//End of Bonus d)
			
			// remove download bar from download tasks after the animation
			pt.setOnFinished(event -> downloadTasks.getChildren().remove(myBar));
			messageArea.appendText(sfName + " was successfully downloaded!\n");

			

			/**
			 * Bonus b) Launch program upon file downloaded
			 */
			if (Desktop.isDesktopSupported()) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Open");
				alert.setHeaderText("Open file");
				alert.setContentText("Do you want to open file " + sfName + " ?");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					try {
						File f = new File(sfPath);
						Desktop.getDesktop().open(f);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}//End of Bonus b)
		}

		/**
		 * This is a method to handle the actions if the task is cancelled
		 */
		@Override
		protected void cancelled() {
			super.cancelled();

			downloadTasks.getChildren().remove(myBar);
			messageArea.appendText(sfName + " download was cancelled!\n");
		}

		/**
		 * This is a method to handle the actions if the task failed
		 */
		@Override
		protected void failed() {
			super.failed();
			try {
				Files.delete(Paths.get(sfPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				messageArea.appendText("Could not delete " + sfPath);
			}
			downloadTasks.getChildren().remove(myBar);
			messageArea.appendText(sfName + " download failed!\n");
		}
	}
}

