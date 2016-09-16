package uploadLidarIndex;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;

import UML_ClassDiagramBuilder.UML_GUI;
import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;

public class uploadGUI extends Application {
	private HBox srcBox, destBox, runBox;

	private Text srcText, destText;
	private TextField srcField, destField;
	private Button srcBtn, destBtn, uploadBtn;

	/** This launches the GUI and calls start() */
	public static void main(String args[]) {
		launch(args);
	}

	/** 			 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox();

		srcText = new Text("Source Folder: ");
		destText = new Text("Destination Folder: ");

		srcField = new TextField();
		HBox.setHgrow(srcField, Priority.ALWAYS);

		destField = new TextField();
		HBox.setHgrow(destField, Priority.ALWAYS);

		srcBtn = new Button("Choose...");
		srcBtn.setOnAction(e -> {
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle("Choose a source folder...");

			File chosen = dirChooser.showDialog(primaryStage);
			if (chosen != null) {
				srcField.setText(chosen.getAbsolutePath());
			}
			;
		});

		destBtn = new Button("Choose...");
		destBtn.setOnAction(e -> {
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Choose a source folder...");

			File chosen = fileChooser.showDialog(primaryStage);
			if (chosen != null) {
				destField.setText(chosen.getAbsolutePath());
			}
			;
		});

		uploadBtn = new Button("Upload");
		uploadBtn.setOnAction(e -> {
			File srcFolder = new File(srcField.getText());
			File destFolder = new File(destField.getText());

			File[] srcdList = srcFolder.listFiles(File::isDirectory);

			for (File srcd : srcdList) {
				String srcName = srcd.getName();
				String destName = new String();
				if (srcName.contains("UTM")) {
					destName = srcName.substring(0, srcName.indexOf("- UTM"));
					File srcdPath=Paths.get(srcFolder.getAbsolutePath(),srcName,"LidarIndex").toFile();

					destName = destName.replace(' ', '_');
					destName = destName.replaceAll("_-_", "_");
					destName = destName.replace('(', '_');
					destName = destName.replace(')', '_');
					destName = destName.replaceAll("__", "_");
					destName = destName.trim();
					if (destName.charAt(destName.length() - 1) == ')' || destName.charAt(destName.length() - 1) == '_')
						destName = destName.substring(0, destName.length() - 1);

					File destdPath=Paths.get(destFolder.getAbsolutePath(),destName).toFile();
					if (!destdPath.exists()) {
		                destdPath.mkdir();
		            }
					
					
					
					File[] srcfiles = srcdPath.listFiles();
		            for (File srcf:srcfiles){
		            	Path destfPath=Paths.get(destdPath.getAbsolutePath(), srcf.getName());
		            	try {
							Files.copy(srcf.toPath(),destfPath,
							StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            }

					// System.out.println(destName);
					// System.out.println(srcfile.getName());
				}

			}
		});

		srcBox = new HBox(srcText, srcField, srcBtn);
		destBox = new HBox(destText, destField, destBtn);
		runBox = new HBox(uploadBtn);
		runBox.setAlignment(Pos.CENTER);

		root.getChildren().addAll(srcBox, destBox, runBox);

		primaryStage.setScene(new Scene(root, 600, 400));
		primaryStage.show();
	}

	/**
	 * 
	 * @param p
	 *            The path to a Java file
	 */

}
