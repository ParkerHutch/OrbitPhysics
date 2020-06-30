import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class OrbitPhysicsV1 extends Application {

	// Window Dimensions
	int WIDTH = 1000;
	int HEIGHT = 680;

	// Useful global variables
	Group root;
	
	GraphicsContext gc;

	AnimationTimer animator;

	double xVelocity = 8;
	double yVelocity = 0;
	
	double weightForce;
	
	KeyboardHandler keyboardHandler;	
	
	Circle planet;
	Circle rocket;
	
	double planetMass = 10000;
	double rocketmass = .2;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage gameStage) throws Exception {
		
		Stage theStage = gameStage;
		theStage.setTitle("OrbitPhysicsV1");

		root = new Group();

		Scene gameScene = new Scene(root, WIDTH, HEIGHT);

		theStage.setWidth(WIDTH); // adding these 2 lines fixes drawing errors
		theStage.setHeight(HEIGHT);

		Canvas canvas = new Canvas(theStage.getWidth(), theStage.getHeight());

		root.getChildren().add(canvas);

		gc = canvas.getGraphicsContext2D();
		
		addMouseHandling(gameScene);
		addKeyboardhandling(gameScene);
		
		planet = new Circle(getWidth() / 2, getHeight() / 2, 20);
		
		rocket = new Circle(planet.getCenterX(), planet.getCenterY() / 2, 10);
		
		root.getChildren().addAll(planet, rocket);
		
		animator = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				
				double angle = calculateAngleBetween(planet, rocket);		
				
				double distance = calculateDistance(planet, rocket);
								
				double weightForce = calculateWeightForce(rocketmass, planetMass, distance);
				
				// Update the rocket velocities
				xVelocity += weightForce * Math.cos(angle);
				yVelocity -= weightForce * Math.sin(angle);
				
				// Update the rocket position
				rocket.setCenterX(rocket.getCenterX() + xVelocity);
				rocket.setCenterY(rocket.getCenterY() + yVelocity);
				
				// add a dot where the rocket was
				gc.fillOval(rocket.getCenterX(), rocket.getCenterY(), 1, 3);
				
				if (isCollisionPresent(planet, rocket)) {
					
					planet.setFill(Color.GREEN);
					gc.fillText("Collision occured", planet.getCenterX() + 20, planet.getCenterY() + 20);
					animator.stop();
					
				}
				
			}
		};
		animator.start();
		
		theStage.setScene(gameScene); // stuff won't show up without this
		theStage.show();
	}
	
	/**
	 * Tests if a collision between the two Nodes given
	 * @param body1 one of the bodies to check for a collision between
	 * @param body2 one of the bodies to check for a collision between
	 * @return true if there is a collision between the two bodies, false otherwise
	 */
	public boolean isCollisionPresent(Node body1, Node body2) {
		
		boolean collisionStatus;
		
		collisionStatus = body1.getLayoutBounds().intersects(body2.getLayoutBounds());
		
		return collisionStatus;
		
	}
	
	/**
	 * Calculates the angle between two bodies and gives it in radians
	 * @param body1 one of the bodies to calculate the angle between
	 * @param body2 one of the bodies to calculate the angle between
	 * @return the angle between the two bodies in radians
	 */
	public double calculateAngleBetween(Circle body1, Circle body2) {
		
		double angle;
		
		double xDiff = body1.getCenterX() - body2.getCenterX();
		double yDiff = body1.getCenterY() - body2.getCenterY();
		
		angle = Math.atan2(xDiff, yDiff);
		// Subtract Pi/2 to translate the angle(this way 0 degrees is on the right of the planet, 90 degrees is directly above, etc.)
		angle -= Math.PI / 2;
		
		return angle;
	}
	
	/**
	 * Calculates the weight force between two bodies using Newton's equation for universal gravitation
	 * @param mass1 mass of one of the bodies involved in the gravitational attraction
	 * @param mass2 mass of the other body involved in the gravitational attraction
	 * @param distance the distance between the two bodies
	 * @return the weight force between the two bodies
	 */
	public double calculateWeightForce(double mass1, double mass2, double distance) {
		
		// Newton's equation for universal gravitation: F = (G * m1 * m2) / d^2
		
		final double G = 6.0; // NOTE: This is not accurate to Earth
		
		double weightForce = (G * mass1 * mass2) / (distance * distance);
		
		System.out.println(weightForce);
		return weightForce;
		
	}
	
	/**
	 * Calculates the distance between two circles using the Pythagorean Theorem
	 * @param body1
	 * @param body2
	 * @return the distance between the two circles
	 */
	double calculateDistance(Circle body1, Circle body2) {
		
		// Pythagorean Theorem: c^2 = a^2 + b^2
		// So distance = sqrt((x distance)^2 + (y distance)^2)
		
		double xDistance = body1.getCenterX() - body2.getCenterX();
		double yDistance = body1.getCenterY() - body2.getCenterY();
		
		double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
		
		return distance;
		
	}
	
	/**
	 * Adds mouse event(ex. clicks/presses) handling to a scene
	 * @param scene scene to add mouse event handling to
	 */
	public void addMouseHandling(Scene scene) {
		
		// adds mouse event handling to the given scene
		MouseHandler mouseHandler = new MouseHandler();
		scene.setOnMouseMoved(mouseHandler);
		scene.setOnMouseDragged(mouseHandler);
		scene.setOnMousePressed(mouseHandler);
		scene.setOnMouseClicked(mouseHandler);
		scene.setOnMouseReleased(mouseHandler);
		
	}
	
	/**
	 * Adds keyboard event(ex. key press) handling to a scene
	 * @param scene scene to add keyboard event handling to
	 */
	public void addKeyboardhandling(Scene scene) {
		
		KeyboardHandler keyboardHandler = new KeyboardHandler();
		scene.setOnKeyPressed(keyboardHandler);
		scene.setOnKeyReleased(keyboardHandler);
	}
	
	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	class KeyboardHandler implements EventHandler<KeyEvent> {
		// A class for handling keyboard input

		private String currentKey = "";

		KeyboardHandler() {
		}

		public void handle(KeyEvent arg0) {
			if (arg0.getEventType() == KeyEvent.KEY_PRESSED) {
				setCurrentKey(arg0.getCode().toString());
			} else if (arg0.getEventType() == KeyEvent.KEY_RELEASED) {
				clearFromCurrentKey(arg0);
			}
			
			applyInput(getCurrentKey());
		}

		public void applyInput(String keys) {
			
			System.out.println(keys);
			if (keys.contains("W")) {
			}
			if (keys.contains("S")) {
			}
			if (keys.contains("A")) {
			}
			if (keys.contains("D")) {
			}
			if (keys.contains("I")) {
			}
			if (keys.contains("N")) {
			}
		}

		private void setCurrentKey(String key) {
			if (currentKey.equalsIgnoreCase("None")) {
				currentKey = key; // if no key was pressed, the current key is the one given
			} else if (!(currentKey.contains(key))) {
				// if the currentKey already contained given key, don't keep adding it
				currentKey += key; // if another key is also being pressed, add the input to it
			}
		}

		private void clearFromCurrentKey(KeyEvent ke) {
			// removes the key from the current key String
			// NOTE: This does not work with longer keyEvent characters, such as semicolon

			StringBuilder modifiableString = new StringBuilder(currentKey);

			String letterPressed = ke.getText().toUpperCase(); // currentKey uses upper case values

			for (int i = 0; i < modifiableString.length(); i++) {
				String currentCharAsString = Character.toString(modifiableString.charAt(i));

				if (currentCharAsString.equals(letterPressed)) {
					modifiableString.deleteCharAt(i);
				}
			}

			currentKey = modifiableString.toString();
		}

		private String getCurrentKey() {
			return currentKey;
		}

	}
	
	class MouseHandler implements EventHandler<MouseEvent> {
		// A class for handling mouse input

		MouseHandler() {
		}

		public void handle(MouseEvent arg0) {
			
			if (arg0.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
				
				if (arg0.getButton() == MouseButton.PRIMARY) {
					// if the left click button is pressed, add a dot and calculate the angle between it and the planet
					Circle dot = new Circle(arg0.getX(), arg0.getY(), 2);
					dot.setFill(Color.RED);
					
					root.getChildren().add(dot);
					
					double tempAngle = calculateAngleBetween(dot, planet);
					
					gc.fillText("" + Math.round(Math.toDegrees(tempAngle)), dot.getCenterX(), dot.getCenterY());
					
				}
			}
		}

	}

}