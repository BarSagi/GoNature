package Client;

import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.Parent;
import Common.Message;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import GUI.ConnectionController;
import javafx.application.Application;
import javafx.application.Platform;

/**
 * The main entry point for the client-side application.
 * This class handles the JavaFX lifecycle, manages the primary stage,
 * establishes the connection to the server, and tracks application idle time.
 */
public class ClientUI extends Application {

	/**
	 * The controller managing the initial connection screen.
	 */
	public static ConnectionController connectionController;

	// public static OrderClient aaaclient;

	/**
	 * The client instance responsible for communicating with the server.
	 */
	public static GoNatureClient client;

	/**
	 * The primary JavaFX stage for the application.
	 */
	private static Stage mainStage;

	/**
	 * A flag indicating whether the user interface is fully loaded and ready.
	 */
	public static volatile boolean uiReady = false;

	/**
	 * The IP address of the currently connected server.
	 */
	public static String serverIP; // current client

	/**
	 * The port number of the currently connected server.
	 */
	public static int serverPort; // current server port

	/**
	 * Timestamp of the last recorded user or system activity.
	 * Marked as volatile to ensure thread safety between the UI thread and timer thread.
	 */
	private static volatile long lastActivityTime = System.currentTimeMillis();

	/**
	 * The allowed timeout duration in milliseconds before the client is disconnected.
	 * Set to 120,000 milliseconds (2 minutes).
	 */
	private static final long TIMEOUT = 120_000;

	/**
	 * Timer used to monitor user inactivity.
	 */
	private static Timer idleTimer;
	
	/**
	 * The specific timer task tracking inactivity, saved so it can be canceled to prevent duplicates.
	 */
	private static TimerTask currentTask;

	/**
	 * The main method that launches the JavaFX application.
	 * * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		launch(); // call start method
	}

	/**
	 * The starting point of the JavaFX application. Loads the initial Connection
	 * FXML screen and displays it.
	 * * @param primaryStage The primary stage for this application.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// load the graphical file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Connection.fxml"));

			Scene scene = new Scene(loader.load()); // create a new scene

			connectionController = loader.getController(); // get the class that runs the FXML

			primaryStage.setTitle("Connect to Server");
			primaryStage.setScene(scene);
			primaryStage.show();
			mainStage = primaryStage; // save the stage for later use

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connects the client to the server using the provided IP and port, starts the
	 * idle monitor, and loads the LoginRoute UI.
	 * * @param ip   The IP address of the server.
	 * @param port The port number of the server.
	 * @throws Exception If the connection fails or if there is an error loading the FXML.
	 */
	public static void startClient(String ip, int port) throws Exception {
		serverIP = ip;
		serverPort = port;

		uiReady = false;

		if (client != null) {
			try {
				client.closeConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// try to connect
		client = new GoNatureClient(ip, port);
		client.openConnection();

		// if connection failed
		if (!client.isConnected()) {
			throw new Exception("Connection failed");
		}

		updateActivity();
		startIdleMonitor();

		// load the UI
		try {
			FXMLLoader loader = new FXMLLoader(ClientUI.class.getResource("/GUI/LoginRoute.fxml"));

			Scene scene = new Scene(loader.load());

			uiReady = true;

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					mainStage.setTitle("Order Client");
					mainStage.setScene(scene);
				}
			});
		} catch (Exception e) {
			System.out.println("Error loading LoginRoute.fxml");
			e.printStackTrace();
		}
	}

	/**
	 * A generic method to switch screens in the application.
	 * * @param fxmlPath The path to the FXML file
	 * @param title    The title to display at the top of the window
	 */
	public static void changeScreen(String fxmlPath, String title) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					FXMLLoader loader = new FXMLLoader(ClientUI.class.getResource(fxmlPath));
					Parent root = loader.load();

					mainStage.setTitle(title);
					// If this is the first time the app loads and there is no Scene yet
					if (mainStage.getScene() == null) {
						mainStage.setScene(new Scene(root));
					} else {
						// If a Scene already exists, smoothly swap its internal content (Root)
						mainStage.getScene().setRoot(root);
					}

					// Ensure the window remains maximized across all screen changes
					mainStage.setMaximized(true);
					mainStage.show();
				} catch (Exception e) {
					System.out.println("Error loading screen: " + fxmlPath);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Disconnects the client from the server gracefully.
	 */
	public static void disconnect() {
		if (client != null) {
			client.disconnectClient();
		}
	}

	/**
	 * Called when the application is closing. Ensures that a disconnect message is
	 * sent to the server and the connection is closed.
	 */
	@Override
	public void stop() {
		try {
			if (client != null && client.isConnected()) {
				client.sendToServer(new Message("DISCONNECT", null));
				client.closeConnection();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Client application stopped");
	}

	/**
	 * Updates the last activity timestamp to the current time, preventing idle timeout.
	 */
	public static void updateActivity() {
		lastActivityTime = System.currentTimeMillis();
	}

	/**
	 * Starts a background timer task that periodically checks if the client has
	 * been idle longer than the defined TIMEOUT. If so, it closes the connection.
	 * Ensures previous tasks are canceled to avoid memory leaks.
	 */
	public static void startIdleMonitor() {
		// If there is an old task running, cancel it so we don't get duplicates!
		if (currentTask != null) {
			currentTask.cancel();
		}

		// If we don't have a timer yet, make one
		if (idleTimer == null) {
			idleTimer = new Timer(true);
		}

		currentTask = new TimerTask() {
			@Override
			public void run() {
				if (client == null)
					return;

				if (client.isConnected()) {
					long now = System.currentTimeMillis();
					if (now - lastActivityTime > TIMEOUT) {
						try {
							System.out.println("Idle timeout - closing connection");
							client.closeConnection();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		// Schedule the clean, newly created task
		idleTimer.scheduleAtFixedRate(currentTask, 1000, 1000);
	}

	/**
	 * Sends a message to the server synchronously. Updates the activity timer and
	 * attempts to reconnect if the client is not connected.
	 * * @param msg The message object to be sent.
	 * @throws Exception If an error occurs during sending or reconnection.
	 */
	public static synchronized void send(Message msg) throws Exception {

		updateActivity();

		if (client == null || !client.isConnected()) {
			reconnect();
		}

		client.sendToServer(msg);
	}

	/**
	 * Attempts to reconnect to the server using the previously saved IP and port.
	 * * @throws Exception If there is no saved connection info or if the connection fails.
	 */
	public static void reconnect() throws Exception {
		if (serverIP == null || serverPort == 0) {
			throw new Exception("No saved server connection info");
		}

		if (client != null) {
			try {
				client.closeConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		client = new GoNatureClient(serverIP, serverPort);
		client.openConnection();
	}

}