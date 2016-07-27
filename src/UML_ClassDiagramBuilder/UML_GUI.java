package UML_ClassDiagramBuilder;


import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class UML_GUI extends Application {

	private VBox variablesBox, functionsBox;
	private Text className;

	/** This launches the GUI and calls start() */
	public static void main(String args[]) {
		launch(args);
	}

	/** 			 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		MenuBar mb = new MenuBar();
		Menu fileMenu = new Menu("File");

		MenuItem openItem = new MenuItem("Open");
		openItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open a Java file");

			// Tell the file chooser to only show Java files:
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Java Files", "*.java"));
			File chosen = fileChooser.showOpenDialog(primaryStage);
			if (chosen != null) {
				// After opening the first file, the open menuItem should open a
				// second window instead:
				openItem.setOnAction(event -> {
					// Create a new window:
					Stage newWindow = new Stage();
					UML_GUI newGui = new UML_GUI();
					try {
						newGui.start(newWindow);
						mb.getMenus().remove(fileMenu);
					} catch (Exception e1) {
						Alert alertBox = new Alert(AlertType.ERROR);
						alertBox.setTitle("Error!");
						alertBox.setHeaderText("Couldn't open new window");
						alertBox.showAndWait();
					}
				});

				// Read the file:
				readFile(chosen.toPath());
			}
		});
		MenuItem quitItem = new MenuItem("Quit");
		quitItem.setOnAction(evt -> {
			Alert confirmWindow = new Alert(AlertType.CONFIRMATION);
			confirmWindow.setTitle("Do you want to quit?");
			confirmWindow.setContentText("Are you sure?");
			Optional<ButtonType> answer = confirmWindow.showAndWait();
			if (answer.isPresent() && answer.get().equals(ButtonType.OK))
				Platform.exit();
		});
		fileMenu.getItems().addAll(openItem, quitItem);
		mb.getMenus().addAll(fileMenu);

		className = new Text("None");
		variablesBox = new VBox();
		variablesBox.setAlignment(Pos.CENTER_LEFT);
		variablesBox.setStyle("-fx-border-color: black;");
		functionsBox = new VBox();

		VBox topBox = new VBox(mb, className);
		topBox.setAlignment(Pos.CENTER);
		className.setFont(new Font("Times new Roman", 30));
		root.setTop(topBox);
		BorderPane.setAlignment(root.getTop(), Pos.CENTER);
		root.setCenter(variablesBox);
		root.setBottom(functionsBox);
		primaryStage.setScene(new Scene(root, 600, 400));
		primaryStage.show();
	}

	/**
	 * Do your lab 9 work here:
	 * 
	 * @param p
	 *            The path to a Java file
	 */
	private void readFile(Path p) {
		// Use Try-with-resources to open a buffered reader for the Java file
		try (BufferedReader reader = Files.newBufferedReader(p)) {
			// Remove the ".java" from this filename:
			String line = p.getFileName().toString().replace(".java", "");
			String declaration;
			String parameters;

			className.setText(line);
			while ((line= reader.readLine()) != null) {
				// reset your variables so that you don't accidentally use
				// strings from previous lines:
				declaration = parameters = null;

				line=line.trim();
				// Check if this is a variable or function declaration:
				if (!line.contains("class") && !line.contains("package") && !line.contains("import")
						&& line.length() > 0) {
					if (line.contains("public") || line.contains("private") || line.contains("protected")) {
						if (line.contains("(") && line.contains(")") && !line.contains("new")) {
							declaration = line.substring(0, line.indexOf("("));
							parameters = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
						} else if (line.contains("=")) {
							declaration = line.substring(0, line.indexOf("="));
						} else {
							declaration = line.substring(0, line.indexOf(";"));
						}
					} else
						continue;

				} else
					continue;

				// Add the umlEntry to the proper VBox depending on whether this
				// line is a variable or function declaration:
				Text umlEntry = getUMLString(declaration, parameters);
				if (parameters == null)
					variablesBox.getChildren().add(umlEntry);
				else
					functionsBox.getChildren().add(umlEntry);

			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Write this function to build the UML string that will be displayed as
	 * 
	 * @param declaration
	 *            A String containing the first part of a variable or function
	 *            declaration, before "(", "=" or ";"
	 * @param parameterString
	 *            The string that comes after "(" if this is a function
	 *            declaration. Otherwise it should be null.
	 * @return
	 */
	private Text getUMLString(String declaration, String parameterString) {
		String UMLstring = "";
		boolean foundStatic = false;
		boolean foundAbstract = false;

		String newArray[] = declaration.split(" ");

		// Write your algorithm here:
		
		
		
		
		UMLstring += newArray[newArray.length - 1];

		if (parameterString != null ) {
			String paraArray[] = parameterString.split(",");
			UMLstring += "(";
			if(parameterString.trim().length()>0){
				int i=0;
				for (String paraTerm : paraArray) {
					UMLstring += paraTerm.trim().split(" ")[1] + ": " + paraTerm.trim().split(" ")[0];
					if(i<paraArray.length-1){
						i++;
						UMLstring += ",";
					}
				}
			}
			
			UMLstring += ")";
		} 

		for (int i = newArray.length - 2; i >= 0; i--) {
			if(newArray[i].contains("public")){
				UMLstring = "+"+UMLstring;
				continue;
			}
			
			if(newArray[i].contains("private")){
				UMLstring = "-"+UMLstring;
				continue;
			}
			if(newArray[i].contains("protected")){
				UMLstring = "#"+UMLstring;
				continue;
			}
			if (newArray[i].contains("static")) {
				foundStatic = true;
				continue;
			}
			if (newArray[i].contains("abstract")) {
				foundAbstract = true;
				continue;
			}
			if(newArray[i].length()>0)
			UMLstring += ": "+newArray[i];
		}

		Text umlLine = new Text(UMLstring);
		// Modify the Text object depending on whether you find static, or
		// abstract:
		umlLine.setUnderline(foundStatic);
		if (foundAbstract)
			umlLine.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
		else
			umlLine.setFont(Font.font("Verdana", FontPosture.REGULAR, 20));

		return umlLine;
	}
}

