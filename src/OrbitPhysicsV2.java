import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class OrbitPhysicsV2 extends Application {

	/* 
	 * IDEAS
	 * 1. Multiple windows?
	 * 		- Would be cool to have a summary window showing data of all space objects
	 * 2. Add background, stars
	 * 3. Ability to increase mass of a planet while program is running to show what effect it hass on gravity
	 */
	
	// Window Dimensions
	int WIDTH = 1000;
	int HEIGHT = 900;

	// Useful global variables
	Group root; // Stores objects to be drawn on the screen
	Canvas canvas;
	GraphicsContext gc; // Drawing object
	AnimationTimer animator; // Allows for animation to occur via a continuously updated handle method
	GameCamera gameCamera = new GameCamera(WIDTH, HEIGHT, 0, 0);
	
	double rocketVelocity = 10;
	double rocketVelocityDirectionDegrees = 0;
		
	ArrayList<SpaceObject> spaceObjects = new ArrayList<SpaceObject>();
	SpaceObject planet;
	SpaceObject rocket;
	
	double planetMass = 20000;
	double rocketmass = 200; // making this smaller decreases the rocket's gravitational pull on the planet
	
	boolean updatePositions = true;
	
	int focusBodyIndex = 0; // The index of object to focus on of the spaceObjects ArrayList
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage gameStage) throws Exception {
		
		Stage theStage = gameStage;
		theStage.setTitle("OrbitPhysicsV2");

		root = new Group();

		Scene gameScene = new Scene(root, WIDTH, HEIGHT);

		theStage.setWidth(WIDTH);
		theStage.setHeight(HEIGHT);

		canvas = new Canvas(theStage.getWidth(), theStage.getHeight());

		root.getChildren().add(canvas);

		gc = canvas.getGraphicsContext2D();
		
		addMouseHandling(gameScene);
		addKeyboardhandling(gameScene);
		
		planet = new SpaceObject("Earth", Color.BLUE, getWidth() / 2, getHeight() / 2, 10, planetMass);
		
		rocket = new SpaceObject("Rocket", Color.GREEN, planet.getCenterX(), planet.getCenterY() / 2, 5, rocketmass, rocketVelocity, rocketVelocityDirectionDegrees);
		
		spaceObjects.add(planet);
		spaceObjects.add(rocket);
		
		animator = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				
				for (SpaceObject object : spaceObjects) {
					
					object.setSpaceObjects(spaceObjects);
					
					object.setNumPointsToSave(175);
								
				}
				
				gameCamera.centerOn(spaceObjects.get(focusBodyIndex));
				
				gc.clearRect(0, 0, WIDTH, HEIGHT);
				
				// Update SpaceObject positions and add dots at their previous positions
				for (SpaceObject object : spaceObjects) {

					if (updatePositions) {
						object.updatePosition();
					}
					
					object.draw(gc, gameCamera);
					
					for (int i = 1; i < object.getTrailPoints().size(); i++) {
						
						Point2D lastPoint = object.getTrailPoints().get(i - 1);
						Point2D currentPoint = object.getTrailPoints().get(i);
						
						double x1 = lastPoint.getX() - gameCamera.getxOffset();
						double y1 = lastPoint.getY() - gameCamera.getyOffset();
						double x2 = currentPoint.getX() - gameCamera.getxOffset();
						double y2 = currentPoint.getY() - gameCamera.getyOffset();
						
						gc.strokeLine(x1, y1, x2, y2);
						
					}
					
				}
				
				for (int i = 0; i < spaceObjects.size(); i++) {

					for (int j = 0; j < spaceObjects.size(); j++) {

						if (j != i) {

							SpaceObject currentObject = spaceObjects.get(j);
							SpaceObject otherObject = spaceObjects.get(i);
							if (currentObject.isCollisionPresent(otherObject)) {

								currentObject.setColor(Color.BROWN);
								otherObject.setColor(Color.BROWN);
								//updatePositions = false;

							}
						}

					}
				}
			}
				
		};
		animator.start();
		
		theStage.setScene(gameScene);
		theStage.show();
	}
	
	
	
	/**
	 * Adds mouse event(ex. clicks/presses) handling to a scene
	 * @param scene scene to add mouse event handling to
	 */
	public void addMouseHandling(Scene scene) {
		
		// adds mouse event handling to the given scene
		MouseHandler mouseHandler = new MouseHandler();
		scene.setOnMouseEntered(mouseHandler);
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
	
	/**
	 * @return the width of the window
	 */
	public int getWidth() {
		return WIDTH;
	}

	/**
	 * @return the height of the window
	 */
	public int getHeight() {
		return HEIGHT;
	}

	/**
	 * Turns the requestedIndex of an indexed list(Array, ArrayList, etc.) into
	 * a valid index of that list. The valid index returned depends on whether the
	 * requested index was a valid index. The requested index is invalid if it is 
	 * less than 0 or greater than the length of the list - 1 (both of those cases
	 * would result in an OutOfBoundsException if the requestedIndex was used). 
	 * 
	 * If requestedIndex was invalid and less than 0, the valid index is the 
	 * requestedIndex plus the requestedIndex. For example, getValidIndex(-1, 5)
	 * would calculate and return a valid index of (5 + -1) = 4.
	 * 
	 * If requestedIndex was invalid and greater than the last valid index of the array
	 * (length - 1), then the valid index is requestedIndex - the length of the list. If
	 * that index is still too big for the list(validIndex > length - 1), then the 
	 * getValidIndex method will be used again on that index. For example, getValidIndex(7, 3) 
	 * would calculate a valid index of (7 - 3) = 4, which is still too big for the list,
	 * so getValidIndex(4, 3) will be used, which would return 1. 
	 * 
	 * @param requestedIndex
	 * @param length
	 * @return
	 */
	public int getValidIndex(int requestedIndex, int length) {
		// converts a requested index to one that is in the bounds of an array with a
		// given length
		// ex. getValidIndex(-1, 6) returns 5

		int validIndex;
		int finalIndex = length - 1;
		if (requestedIndex < 0) {
			
			validIndex = length + requestedIndex;
			
		} else if (requestedIndex > finalIndex) {
			
			validIndex = requestedIndex - length;
			
			if (validIndex > finalIndex) {
				
				getValidIndex(validIndex, length);
				
			}
			
		} else {
			// if the index was valid to begin with, don't change it
			validIndex = requestedIndex;
			
		}

		return validIndex;
	}
	
	/**
	 * A class for handling keyboard input
	 *
	 */
	private class KeyboardHandler implements EventHandler<KeyEvent> {

		KeyboardHandler() {
		}

		public void handle(KeyEvent arg0) {
			if (arg0.getEventType() == KeyEvent.KEY_PRESSED) {
				String code = arg0.getCode().toString().toUpperCase();
				if (code.equals("UP")) {
					focusBodyIndex = getValidIndex(focusBodyIndex + 1, spaceObjects.size());
				}
				if (code.equals("DOWN")) {
					focusBodyIndex = getValidIndex(focusBodyIndex - 1, spaceObjects.size());
				}
				if (code.equals("SPACE")) {
					updatePositions = !updatePositions;
				}
				
			} 
		}

	}
	
	/**
	 * A class for handling mouse events
	 */
	private class MouseHandler implements EventHandler<MouseEvent> {
		
		private Point2D mouseStartPoint;
				
		private boolean isSettingVelocity = false;
		
		private SpaceObject tempObject;
		
		private Line velocityLine = new Line();
		
		MouseHandler() {
			
			// Add the velocityLine to the root group 
			// Its start and end points will be set while the user
			// sets the velocity of the SpaceObject
			root.getChildren().add(velocityLine); 
			
		}

		public void handle(MouseEvent arg0) {
			
			
			if (arg0.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				
				// if the left click button is pressed
				if (arg0.getButton() == MouseButton.PRIMARY) {
					
					// If the user isn't updating the velocity of the spaceObject
					// (they will press again when they set the velocity, but
					// that should not create a new SpaceObject, as is done
					// in the below if-block)
					if (!isSettingVelocity) {
						
						// Pause the updating of spaceObjects
						updatePositions = false;
						
						// register the current mouse click point as the start click point
						mouseStartPoint = new Point2D(arg0.getX(), arg0.getY());
						
						
						// Create the SpaceObject centered on the click position
						tempObject = new SpaceObject();
						
						tempObject.setCenterX(mouseStartPoint.getX());
						tempObject.setCenterY(mouseStartPoint.getY());
						tempObject.setColor(Color.RED);
						
						spaceObjects.add(tempObject);
						
					}
				}
			}
			
			// Set the radius of the new SpaceObject to the drag distance
			if (arg0.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {

				isSettingVelocity = false;

				Point2D mouseCurrentPoint = new Point2D(arg0.getX(), arg0.getY());

				double dragDistance = calculateDistance(mouseStartPoint, mouseCurrentPoint);

				tempObject.setVisible(true);
				tempObject.setRadius(dragDistance);
				tempObject.setCenterX(mouseStartPoint.getX() + gameCamera.getxOffset());
				tempObject.setCenterY(mouseStartPoint.getY() + gameCamera.getyOffset());
								
			}
			
			if (isSettingVelocity) {

				// If the user is moving the mouse around after setting the initial
				// and radius of the SpaceObject, draw a line from the SpaceObject's
				// center to the mouse position. The user uses this line to set the velocity
				// of the SpaceObject: a longer line indicates a greater magnitude,
				// and the direction of the line will be used as the direction of the 
				// SpaceObject's velocity. When the user presses(and then releases)
				// the mouse, these calculations for the SpaceObject's velocities
				// will be made
				
				
				// Draw the velocity line from the SpaceObject's center to the 
				// mouse position
				if (!arg0.getEventType().equals(MouseEvent.MOUSE_EXITED)) {

					Point2D mouseCurrentPoint = new Point2D(arg0.getX(), arg0.getY());
					velocityLine.setStartX(tempObject.getCenterX() - gameCamera.getxOffset());
					velocityLine.setStartY(tempObject.getCenterY() - gameCamera.getyOffset());
					velocityLine.setEndX(mouseCurrentPoint.getX());
					velocityLine.setEndY(mouseCurrentPoint.getY());

				}
				
				// If the user releases the mouse(after pressing it),
				// use the velocity line to set the velocity and
				// direction of the SpaceObject
				if (arg0.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {

					Point2D currentPoint = new Point2D(arg0.getX(), arg0.getY());

					// Calculate velocity and direction of the SpaceObject
					double velocity = calculateDistance(mouseStartPoint, currentPoint) / 10;
					double direction = calculateAngleBetween(currentPoint, mouseStartPoint);

					// Set the velocities of the SpaceObjects
					tempObject.setxVelocity(velocity * Math.cos(direction));
					tempObject.setyVelocity(velocity * Math.sin(direction) * -1);

					// Resume updating of SpaceObjects and reset this process
					// by setting isSettingVelocity to false
					updatePositions = true;
					isSettingVelocity = false;
					
					velocityLine.setVisible(false);

				}
				
			} else {
				// if the user released the mouse but wasn't setting the velocity,
				// they were setting the initial radius and position of the
				// SpaceObject. In that case, make the velocity line visible,
				// and set isSettingVelocity to true so the user can set
				// the SpaceObject's velocity
				if (arg0.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {

					velocityLine.setVisible(true);
					isSettingVelocity = true;

				}
			}

		}
		
		/**
		 * Calculates the distance between two points using the Pythagorean theorem
		 * @param startPoint one of the points to calculate the distance between
		 * @param endPoint the other point to calculate distance between
		 * @return the distance between two points
		 */
		double calculateDistance(Point2D startPoint, Point2D endPoint) {
			
			double xDistance = endPoint.getX() - startPoint.getX();
			double yDistance = endPoint.getY() - startPoint.getY();
			
			double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
			
			return distance;
			
		}
		
		/**
		 * Calculates the angle between two points and returns that angle in radians
		 * @param point1 one of the points to calculate the angle between
		 * @param point2 one of the points to calculate the angle between
		 * @return the angle between the two points in radians
		 */
		public double calculateAngleBetween(Point2D point1, Point2D point2) {
			
			double angle;
			
			double xDiff = point1.getX() - point2.getX();
			double yDiff = point1.getY() - point2.getY();
			
			angle = Math.atan2(xDiff, yDiff);
			// Subtract Pi/2 to translate the angle(this way 0 degrees is on the right of the planet, 90 degrees is directly above, etc.)
			angle -= Math.PI / 2;
			
			return angle;
			
		}
		
	}

}