package WebBrowser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.Scene;
import javafx.animation.*;
import javafx.scene.input.KeyCodeCombination;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.control.Tooltip;
import javafx.geometry.Point3D;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle.Control;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.ContextMenu;
import javafx.scene.web.WebHistory.Entry;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;

/**
 * CST8284 Assignment 3: Web Browser in Javafx section: 310 Professors: Eric
 * Torunski, Dave Houtman
 * 
 * @author Jean
 *
 */
public class MyGUI extends Application {
	/**
	 * This is a WebEngine object
	 */
	protected WebEngine engine;
	/**
	 * This is a WebHistory object
	 */
	protected WebHistory history;
	/**
	 * This is a String array of file extensions
	 */
	protected String[] fTypes = { ".EXE", ".PDF", ".ZIP", ".DOC", ".DOCX", ".XLS", ".XLSX", ".ISO", ".IMG", ".DMG",
			".TAR", ".TGZ", ".JAR" };
	/**
	 * This is a boolean variable as flag
	 */
	protected boolean flag = false;
	/**
	 * This is a button object on browser bar
	 */
	protected Button bBack, bAddBookmark, bForward;
	/**
	 * This is the Scene object
	 */
	protected Scene mainScene;
	/**
	 * This is a MenuItem object for download and homepage
	 */
	protected MenuItem downMI, homeMI;
	/**
	 * This is a Stage object
	 */
	protected Stage stage;
	/**
	 * This a double variable for stage dimension settings
	 */
	protected double stageX, stageY, stageHeight, stageWidth;
	/**
	 * This is a String object for homepage url
	 */
	protected String homePage;
	/**
	 * This is a String object for download folder
	 */
	protected String dirDownLoad;

	/**
	 * This is a MenuBar object
	 */
	protected MenuBar mb;
	/**
	 * This is a Menu object for menu bar
	 */
	protected Menu fMenu, hMenu, setMenu;
	/**
	 * This is a Menu object for bookmark menu
	 */
	protected Menu bmMenu = new Menu("Bookmarks");
	/**
	 * This is a TextField object for browser bar
	 */
	protected TextField tURL;

	/**
	 * This is an ObservableList<T> object with WebHistory.Entry type
	 */
	protected ObservableList<WebHistory.Entry> entryList;

	/**
	 * This is a constructor to initialize the browser with the saved settings
	 */
	public MyGUI() {
		try {
			readSettings();
		} catch (Exception e) {
		}
	}

	/**
	 * This is a static void main function
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * This is a public void start function
	 * 
	 * @param primaryStage
	 *            Stage
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		WebView browserView = new WebView();
		engine = browserView.getEngine();

		history = engine.getHistory();
		entryList = history.getEntries();

		/**
		 * This is a ListView<T> object with WebHistory.Entry type
		 */
		ListView<WebHistory.Entry> historyView = new ListView<>();

		/**
		 * This is a boolean variable
		 */
		boolean flag = false;
		/**
		 * This is a ClickHandlerClass object
		 */
		ClickHandlerClass responder = new ClickHandlerClass();

		// monitor the location url, and if newLoc ends with one of the download
		// file endings, create a new DownloadTask.
		engine.locationProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldLoc, String newLocation) {
				// to check if the url contains any file extension as defined in
				// the fTypes array
				for (String suffix : fTypes) {
					if (newLocation.toUpperCase().endsWith(suffix)) {
						DownloadBar newDownload = new DownloadBar(newLocation);
					}
				}
			}
		});

		// This is a lambda function to handle shortcuts of browserView
		browserView.setOnKeyPressed(evt -> {
			KeyCode kc = evt.getCode();
			if (evt.getCode() == KeyCode.LEFT && evt.isControlDown()) {
				goBack();
			}
			if (evt.getCode() == KeyCode.RIGHT && evt.isControlDown()) {
				goForward();
			}

		});
		
		/**
		 * This is a 3-parameter Lambda function for listening for changes of
		 * state when the web page loaded
		 * 
		 * @param ov
		 * @param oldState
		 * @param neState
		 */

		engine.getLoadWorker().stateProperty().addListener((o, oldState, newState) -> {
			// This if statement gets run if the new page load succeeded.
			if (newState == State.SUCCEEDED) {

				// control the Back button disabled status
				if (history.getCurrentIndex() == 0)
					bBack.setDisable(true);
				else
					bBack.setDisable(false);

				// control the Forward button disabled status
				if (history.getCurrentIndex() + 1 < entryList.size())
					bForward.setDisable(false);
				else
					bForward.setDisable(true);

				/**
				 * This is a local boolean variable
				 */
				boolean flg = true;

				// search current page in Bookmark MenuItem
				for (MenuItem items : bmMenu.getItems()) {
					if (items.getText().equalsIgnoreCase(engine.getLocation()))
						flg = false;
				}
				// control the AddBookmark button disabled status
				if (flg)
					bAddBookmark.setDisable(false);
				else
					bAddBookmark.setDisable(true);
			}
		});

		/**
		 * Bonus c): Tabbed browsing
		 */

		/**
		 * This is a TabPane object
		 */
		TabPane tPane = new TabPane();
		/**
		 * This is a Tab object for TabPane
		 */
		Tab iTab = new Tab("Browser Tab");
		/**
		 * This is a WebView object for new tab
		 */
		/*
		 * WebView aBView=new WebView(); WebEngine newEngine =
		 * aBView.getEngine();
		 */

		iTab.setContent(browserView);
		iTab.setClosable(false);
		/*
		 * Tab aTab=new Tab("Additional Tab"); aTab.setContent(aBView);
		 */
		tPane.getTabs().addAll(iTab);

		/**
		 * This is a MenuItem to add new tab
		 */
		MenuItem addTab = new MenuItem("New Tab");
		addTab.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));

		// Add new tab
		addTab.setOnAction(evt -> {
			WebView newView = new WebView();
			Tab newTab = new Tab("New Tab");
			newTab.setContent(newView);
			tPane.getTabs().add(newTab);

			WebEngine tempEngine = newView.getEngine();
			// check if the url link is a file for download for new tab
			tempEngine.locationProperty().addListener((observableValue, oldLoc, newLocation) -> {
				// to check if the url contains any file extension as defined in
				// the fTypes array
				for (String suffix : fTypes) {
					if (newLocation.toUpperCase().endsWith(suffix)) {
						DownloadBar newDownload = new DownloadBar(newLocation);
					}
				}
			});
			// This is a WebEngine alert dialog for new tab
			tempEngine.setOnAlert(otherName -> {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("WebEngine Alert Dialogy");
				alert.setHeaderText("WebEngine Alert");
				alert.setContentText(otherName.getData());
				alert.showAndWait();
			});
			/*
			 * engine=((WebView)newTab.getContent()).getEngine();
			 * history=engine.getHistory();
			 * historyView.setItems(history.getEntries()); entryList =
			 * history.getEntries();
			 */
			newView.setOnKeyPressed(e -> {
				KeyCode kc = e.getCode();
				if (e.getCode() == KeyCode.LEFT && e.isControlDown()) {
					goBack();
				}
				if (e.getCode() == KeyCode.RIGHT && e.isControlDown()) {
					goForward();
				}

			});
			
			// A listener for web page change of status in the new tab
			tempEngine.getLoadWorker().stateProperty().addListener((o, oldState, newState) -> {
				// This if statement gets run if the new page load succeeded.
				if (newState == State.SUCCEEDED) {

					// control the Back button disabled status
					if (history.getCurrentIndex() == 0)
						bBack.setDisable(true);
					else
						bBack.setDisable(false);

					// control the Forward button disabled status
					if (history.getCurrentIndex() + 1 < entryList.size())
						bForward.setDisable(false);
					else
						bForward.setDisable(true);

					/**
					 * This is a local boolean variable
					 */
					boolean flg = true;

					// search current page in Bookmark MenuItem
					for (MenuItem items : bmMenu.getItems()) {
						if (items.getText().equalsIgnoreCase(engine.getLocation()))
							flg = false;
					}
					// control the AddBookmark button disabled status
					if (flg)
						bAddBookmark.setDisable(false);
					else
						bAddBookmark.setDisable(true);
				}
			});
			
			// WebEngine Alert for new tab
			tempEngine.setOnAlert(ev -> {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("WebEngine Alert Dialogy");
				alert.setHeaderText("WebEngine Alert");
				alert.setContentText(ev.getData());
				alert.showAndWait();
			});

		});

		// A listener for tab change status
		tPane.getSelectionModel().selectedItemProperty().addListener((ov, oTab, nTab) -> {
			engine = ((WebView) nTab.getContent()).getEngine();

			tURL.setText(engine.getLocation());
			history = engine.getHistory();
			historyView.setItems(history.getEntries());
			entryList = history.getEntries();
		});//End of Bonus c)

		

		// MenuBars will hold Menu Objects
		mb = new MenuBar();

		// Create a file menu to hold items
		fMenu = new Menu("File");

		// Add quit menu item to fileMenu
		MenuItem quitMI = new MenuItem("Quit");
		quitMI.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

		/**
		 * This is a Lambda function to perform exit()
		 */
		quitMI.setOnAction(evt -> {
			saveSettings();
			Platform.exit();
		});

		fMenu.getItems().add(addTab);
		fMenu.getItems().add(quitMI);

		// My file menu is finished, now add it to the menu bar
		mb.getMenus().add(fMenu);

		// Add the bookmark Menu to the menuBar
		for (MenuItem items : bmMenu.getItems()) {
			/**
			 * This is a Lambda function to load selected page
			 */
			items.setOnAction(et -> {
				engine.load(items.getText());
				tURL.setText(engine.getLocation());
			});
			
		}
		mb.getMenus().add(bmMenu);

		// Create help menu
		hMenu = new Menu("Help");
		/**
		 * This is a MenuItem for help menu
		 */
		MenuItem getHelpMI = new MenuItem("Get help for Java class");
		getHelpMI.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));

		/**
		 * This is a Lambda function for TextInputDialog
		 */
		getHelpMI.setOnAction(evt -> {
			/**
			 * This is a local TextInputDialog object
			 */
			TextInputDialog dialog = new TextInputDialog("Java class");
			dialog.setTitle("Get help for Java class");
			dialog.setHeaderText("Search for Java Class Documentation");
			dialog.setContentText("Which Java class do you want to search?");

			// Traditional way to get the response value.
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				engine.load("https://www.google.ca/search?q=java+" + result.get() + "&sourceid=chrome&ie=UTF-8");
			}
		});

		/**
		 * This is a CheckMenuItem for Show History CheckMenuItem
		 */
		CheckMenuItem historyMI = new CheckMenuItem("Show Historys");
		historyMI.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

		/**
		 * This is a Lambda function to hide/display Web History based on Show
		 * History CheckMenuItem selected status
		 */
		historyMI.setOnAction(evt -> {
			if (historyMI.isSelected()) {
				historyView.setMinWidth(200);
				/**
				 * This is a ScaleTransition object
				 */
				ScaleTransition st = new ScaleTransition(Duration.millis(1500), historyView);
				st.setFromX(1);
				st.setFromY(1);
				st.setByX(0.4);
				st.setByY(0.4);
				st.setCycleCount(2);
				st.setAutoReverse(true);
				/**
				 * This is a RotateTransition object
				 */
				RotateTransition rt = new RotateTransition(Duration.millis(1500), historyView);
				rt.setFromAngle(0);
				rt.setToAngle(360);
				rt.setCycleCount(2);
				rt.setAutoReverse(true);

				/**
				 * This is a ParallelTransition object with ScaleTransition and
				 * RotateTransition
				 */
				ParallelTransition pt = new ParallelTransition(st, rt);
				pt.setAutoReverse(true);
				pt.play();
			} else {

				/**
				 * This is a TranslateTransition object
				 */
				TranslateTransition tt = new TranslateTransition(Duration.millis(1500), historyView);
				tt.setFromX(0);
				tt.setToX(100f);
				tt.setAutoReverse(true);
				tt.setCycleCount(2);

				/**
				 * This is a FadeTransition object
				 */
				FadeTransition ft = new FadeTransition(Duration.millis(1500), historyView);
				ft.setFromValue(historyView.getOpacity());
				ft.setToValue(1 - historyView.getOpacity());
				ft.setCycleCount(2);
				ft.setAutoReverse(true);

				/**
				 * This is a sequentialTransition with TranslateTransition and
				 * FadeTransition
				 */
				SequentialTransition stt = new SequentialTransition();

				stt.getChildren().addAll(tt, ft);
				stt.setAutoReverse(true);
				stt.play();
				/**
				 * This is a Lambda function to set historyView size based on
				 * SequentialTransition onFinished action
				 */
				stt.setOnFinished(event -> historyView.setMinWidth(0));
			}
		});

		/**
		 * This is a MenuItem for help menu
		 */
		MenuItem aboutMI = new MenuItem("About");
		aboutMI.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));

		/**
		 * This is a Lambda function to respond About MenuItem action
		 */
		aboutMI.setOnAction(evt -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("About");
			alert.setContentText("Jean's browser, v1.0. March 14, 2016");

			alert.showAndWait();
		});

		hMenu.getItems().add(getHelpMI);
		hMenu.getItems().add(historyMI);
		hMenu.getItems().add(aboutMI);

		mb.getMenus().add(hMenu);

		/**
		 * This is a menu of Settings including homepage and download folder
		 */
		setMenu = new Menu("Settings");
		homeMI = new MenuItem("HomePage");
		/**
		 * This is a lambda function to handle homepage MenuItem action
		 */
		homeMI.setOnAction(evt -> {
			TextInputDialog dialog = new TextInputDialog("Default Homepage");
			dialog.setTitle("Default Homepage");
			dialog.setHeaderText("Set the homepage");
			dialog.setContentText("Defalult Homepage:");

			// Traditional way to get the response value.
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				homePage = result.get();
				engine.load(result.get());
				tURL.setText(engine.getLocation());
			}
		});

		downMI = new MenuItem("Downloads");
		/**
		 * This is a lambda function to handle download MenuItem action
		 */
		downMI.setOnAction(evt -> {
			TextInputDialog dialog = new TextInputDialog("Download folder");
			dialog.setTitle("Download Directory");
			dialog.setHeaderText("Set the download directory");
			dialog.setContentText("Download Directory:");

			// Traditional way to get the response value.
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				File file = new File(result.get());
				if (file.exists()) {
					if (!file.canWrite()) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Write Permission");
						alert.setHeaderText("Write Permission Denied!");
						alert.setContentText("The selection is not valid");

						alert.showAndWait();
					} else {
						dirDownLoad = result.get();
					}
				} else {
					try {
						file.mkdir();
						dirDownLoad = result.get();
					} catch (SecurityException se) {
						System.out.println("Create directory failed.");
					}
				}
			}
		});
		setMenu.getItems().add(homeMI);
		setMenu.getItems().add(downMI);

		mb.getMenus().add(setMenu);

		/**
		 * Bonus a): inject javaScript code
		 */
		// a lambda function to handle engine setOnAlert
		engine.setOnAlert(evt -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("WebEngine Alert Dialog");
			alert.setHeaderText("WebEngine Alert");
			alert.setContentText(evt.getData());
			alert.showAndWait();
		});

		Menu jsMenu = new Menu("Javascript");
		MenuItem eCodeMI = new MenuItem("Execute code");
		jsMenu.getItems().add(eCodeMI);
		// a lambda function to inject Javascript code
		eCodeMI.setOnAction(evt -> {
			TextInputDialog dialog = new TextInputDialog("Javascript code");
			dialog.setTitle("JScode execute dialog");
			dialog.setHeaderText("Input Javascript code");
			dialog.setContentText("Javascript: ");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				try{				engine.executeScript(result.get());}
				catch(Exception e){
					System.out.println("Wrong JavaScript.");
				}
			}
		});
		mb.getMenus().add(jsMenu);//End of Bonus a)

		/**
		 * This is a HBox object for browser bar
		 */
		HBox barBrowser = new HBox();
		bBack = new Button("Back");
		bBack.setDisable(true);
		bBack.setOnMouseClicked(responder);
		/**
		 * This is a Tooltip object to display information on back button
		 */
		Tooltip ttBack = new Tooltip();
		ttBack.setText("Go back to the previous page.");
		bBack.setTooltip(ttBack);

		tURL = new TextField();
		HBox.setHgrow(tURL, Priority.ALWAYS);

		/**
		 * This is an anonymous class as a key pressed listener on text field
		 */
		tURL.setOnKeyPressed(new EventHandler<KeyEvent>() {
			// Inner anonymous classes
			@Override
			public void handle(KeyEvent event) {
				KeyCode kc = event.getCode();
				if (event.getCode() == KeyCode.ENTER) {
					try {
						URL testURL = new URL(tURL.getText());

						URLConnection connection = testURL.openConnection();
						if (connection != null) {
							engine.load(tURL.getText());
							boolean flg = true;
							for (MenuItem items : bmMenu.getItems()) {
								if (items.getText().toLowerCase() == tURL.getText().toLowerCase())
									flg = false;
							}

							if (flg)
								bAddBookmark.setDisable(false);
							else
								bAddBookmark.setDisable(true);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		tURL.setOnMouseClicked(responder);

		bAddBookmark = new Button("Add Bookmark");
		bAddBookmark.setDisable(true);
		bAddBookmark.setOnMouseClicked(responder);
		/**
		 * This is a Tooltip object to display information on Add Bookmark
		 * button
		 */
		Tooltip ttBmark = new Tooltip();
		ttBmark.setText("Add current page to the book mark.");
		bAddBookmark.setTooltip(ttBmark);

		bForward = new Button("Forward");
		bForward.setDisable(true);
		bForward.setOnMouseClicked(responder);
		/**
		 * This is a Tooltip object to display information on Forward button
		 */
		Tooltip ttForward = new Tooltip();
		ttForward.setText("Go to the next page.");
		bForward.setTooltip(ttForward);

		barBrowser.getChildren().add(bBack);
		barBrowser.getChildren().add(tURL);
		barBrowser.getChildren().add(bAddBookmark);
		barBrowser.getChildren().add(bForward);

		historyView.setItems(history.getEntries());
		historyView.setPrefWidth(0);
		/**
		 * This is a Lambda function to respond historyView entry selection
		 */
		historyView.setOnMouseClicked(evt -> {
			Platform.runLater(() -> {
				int offset = historyView.getSelectionModel().getSelectedIndex() - history.getCurrentIndex();
				history.go(offset);
				final String nextAddress = history.getEntries().get(history.getCurrentIndex()).getUrl();
			});
		});

		/**
		 * This is a VBox object to contain both menu bar and browser bar
		 */
		VBox topBars = new VBox(mb, barBrowser);

		/**
		 * This is a BorderPane object as the root layout
		 */
		// BorderPane rootLayout = new BorderPane(browserView, topBars,
		// historyView, null, null);
		BorderPane rootLayout = new BorderPane(tPane, topBars, historyView, null, null);

		/**
		 * This is a Scene object with 800x640
		 */
		mainScene = new Scene(rootLayout, 800, 640);

		// if stage settings available, restore the stage settings
		if (stage != null) {
			primaryStage = stage;
		}

		// Add the scene to the stage:
		primaryStage.setScene(mainScene);

		// store the initial settings of the stage
		stageX = primaryStage.getX();
		stageY = primaryStage.getY();
		stageWidth = primaryStage.getWidth();
		stageHeight = primaryStage.getHeight();

		// user property listener to capture coordinations change of the stage
		primaryStage.xProperty().addListener((observable, oldvalue, newvalue) -> stageX = (Double) newvalue);
		primaryStage.yProperty().addListener((observable, oldvalue, newvalue) -> stageY = (Double) newvalue);
		primaryStage.widthProperty().addListener((observable, oldvalue, newvalue) -> stageWidth = (Double) newvalue);
		primaryStage.heightProperty().addListener((observable, oldvalue, newvalue) -> stageHeight = (Double) newvalue);

		/**
		 * This is a setOnCloseRequest of stage to save settings
		 */
		primaryStage.setOnCloseRequest(evt -> saveSettings());
		// Show the stage:
		primaryStage.show();

	}

	/**
	 * This is an inner class to handler all mouse clicks on browser bar items
	 * 
	 * @author Jean
	 *
	 */
	private class ClickHandlerClass implements EventHandler<MouseEvent> {
		/**
		 * This is a void method to respond all mouse clicks
		 * 
		 * @param event
		 *            MouseEvent
		 */
		@Override
		public void handle(MouseEvent event) {
			Object clicked = event.getSource();

			if (clicked == bForward)
				goForward();
			else if (clicked == bBack)
				goBack();
			else if (clicked == bAddBookmark) {
				addBookmark();
			} else if (clicked == tURL) {
				// click once, highlight the textfield
				if (event.getClickCount() == 1) {
					tURL.requestFocus();
					tURL.selectAll();
				}
				// click twice, clear the textfield
				if (event.getClickCount() == 2)
					tURL.clear();
			}
		}
	}

	/**
	 * This is a method to add current page url to Bookmark menu
	 */
	public void addBookmark() {
		bmMenu.getItems().add(new MenuItem(engine.getLocation()));
		bAddBookmark.setDisable(true);
		for (MenuItem items : bmMenu.getItems()) {
			/**
			 * This is a Lambda function to load selected page
			 */
			items.setOnAction(et -> {
				engine.load(items.getText());
				tURL.setText(engine.getLocation());
			});
			
		}
	}

	/**
	 * This is a method to go to previous web page
	 */
	public void goBack() {
		final WebHistory history = engine.getHistory();
		ObservableList<WebHistory.Entry> entryList = history.getEntries();
		int currentIndex = history.getCurrentIndex();

		if (currentIndex > 0) {
			/**
			 * This is a no-parameter Lambda function to tell the engine to go
			 * back 1 page in the history
			 */
			Platform.runLater(() -> {
				history.go(-1);
				final String nextAddress = history.getEntries().get(currentIndex - 1).getUrl();
			});
		}
	}

	/**
	 * This is a method to go to next web page in the history
	 */
	public void goForward() {
		final WebHistory history = engine.getHistory();
		ObservableList<WebHistory.Entry> entryList = history.getEntries();
		int currentIndex = history.getCurrentIndex();

		if (currentIndex + 1 < entryList.size()) {
			/**
			 * This is a no-parameter Lambda function to tell the engine to go
			 * forward 1 page in the history
			 */
			Platform.runLater(() -> {
				history.go(1);
				final String nextAddress = history.getEntries().get(currentIndex + 1).getUrl();
			});
		}
	}

	/**
	 * This is a method to save the urls in the Bookmark and the stage settings
	 */
	public void saveSettings() {
		ArrayList<String> bmURL = new ArrayList<String>();
		for (MenuItem items : bmMenu.getItems()) {
			bmURL.add(items.getText());
		}

		// urls in the Bookmark are stored in a file as object
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get("urls.txt")));) {
			oos.writeObject(bmURL);
		} catch (Exception e) {
			System.out.println("Object writing error");
		}

		// stage settings are save in a text file
		try (BufferedWriter dos = Files.newBufferedWriter(Paths.get("setting.txt"), StandardCharsets.UTF_8);) {
			dos.write("ScreenX=" + stageX);
			dos.write(System.getProperty("line.separator"));
			dos.write("ScreenY=" + stageY);
			dos.write(System.getProperty("line.separator"));
			dos.write("height=" + stageHeight);
			dos.write(System.getProperty("line.separator"));
			dos.write("width=" + stageWidth);
			dos.write(System.getProperty("line.separator"));
			dos.write("downloadDirectory=" + dirDownLoad);
			dos.write(System.getProperty("line.separator"));
			dos.write("homepage=" + homePage);
		} catch (Exception e) {
			System.out.println("Data writing error.");
		}
	}

	/**
	 * This is a method to restore Bookmark menu and the stage settings when the
	 * browser open again
	 */
	public void readSettings() {
		ArrayList<String> bmURL = new ArrayList<String>();
		// load the bookmark items
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get("urls.txt")));) {
			bmURL = (ArrayList<String>) ois.readObject();
		} catch (Exception e) {
			System.out.println("Object reading failed");
		}
		for (String urls : bmURL) {
			MenuItem thisItem = new MenuItem(urls);
			thisItem.setOnAction(e -> engine.load(urls));
			bmMenu.getItems().add(thisItem);
		}

		String line;
		// load the stage setting
		try (BufferedReader tis = Files.newBufferedReader(Paths.get("setting.txt"), StandardCharsets.UTF_8);) {
			stage = new Stage();
			while ((line = tis.readLine()) != null) {
				if (line.contains("ScreenX") && line.split("=")[1] != "null") {
					stage.setX(Double.parseDouble(line.split("=")[1]));
				}
				if (line.contains("ScreenY") && line.split("=")[1] != "null") {
					stage.setY(Double.parseDouble(line.split("=")[1]));
				}
				if (line.contains("height") && !line.split("=")[1].contains("null")) {
					stage.setHeight(Double.parseDouble(line.split("=")[1]));
				}
				if (line.contains("width") && !line.split("=")[1].contains("null")) {
					stage.setWidth(Double.parseDouble(line.split("=")[1]));
				}
				if (line.contains("downloadDirectory") && !line.split("=")[1].contains("null")) {

					dirDownLoad = line.split("=")[1];
				}
				if (line.contains("homepage") && !line.split("=")[1].contains("null")) {
					String s = line.split("=")[1];
					homePage = line.split("=")[1];
				}
			}
		} catch (Exception e) {
			System.out.println("Data reading failed");
		}
	}
}

