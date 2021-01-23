import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A class for storing information for objects used in the OrbitPhysics program
 */
public class SpaceObject extends Circle {
	
	String name;
	
	Color color;
	
	double xVelocity;
	double yVelocity;
	
	double mass;
	
	// An ArrayList of all SpaceObjects that apply gravitational attraction on this SpaceObject
	ArrayList<SpaceObject> spaceObjects;
	
	// An ArrayList storing the past coordinate points of the SpaceObject
	ArrayList<Point2D> trailPoints = new ArrayList<>(); 
	
	// Specifies how many elements the trailPoints ArrayList should hold throughout the program
	int numPointsToSave;
	
	
	public SpaceObject() {
		super();
	}
	/**
	 * Creates a named SpaceObject with a specified color centered on (x, y) with a given radius and mass 
	 * and an initial velocity of 0
	 * @param x the center x coordinate of the SpaceObject
	 * @param y the center y coordinate of the SpaceObject
	 * @param radius the radius of the SpaceObject
	 * @param mass the mass of the SpaceObject
	 * @param velocity the initial magnitude of the SpaceObject's velocity
	 * @param direction the initial direction of the SpaceObject's velocity(in degrees)
	 */
	public SpaceObject(String name, Color color, double x, double y, double radius, double mass) {
		
		super(x, y, radius);
		
		this.name = name;
		this.color = color;
		
		this.mass = mass;
		
		this.xVelocity = 0;
		this.yVelocity = 0;
		
	}
	
	/**
	 * Creates a named SpaceObject with a specified color centered on (x, y) with a given radius and mass
	 * The x and y velocities of the SpaceObject are computed based on the velocity given and its direction
	 * @param x the center x coordinate of the SpaceObject
	 * @param y the center y coordinate of the SpaceObject
	 * @param radius the radius of the SpaceObject
	 * @param mass the mass of the SpaceObject
	 * @param velocity the initial magnitude of the SpaceObject's velocity
	 * @param direction the initial direction of the SpaceObject's velocity(in degrees)
	 */
	public SpaceObject(String name, Color color, double x, double y, double radius, double mass, double velocity, double direction) {
		
		super(x, y, radius);
		
		this.name = name;
		
		this.color = color;
		
		this.mass = mass;
		
		double directionInRadians = Math.toRadians(direction);
		this.xVelocity = velocity * Math.cos(directionInRadians);
		this.yVelocity = velocity * Math.sin(directionInRadians);
		
	}

	/**
	 * Update the SpaceObject's x and y velocities by calculating the weight forces acting upon it and 
	 * adding/subtracting those forces to its velocities 
	 * (Note: This method does not update the x and y coordinates of the SpaceObject, only velocities)
	 */
	public void applyForces() {
		
		
		for (SpaceObject object : spaceObjects) {
			

			if (object != this) {
				
				double angle = calculateAngleBetween(object, this);
												
				double weightForce = calculateWeightForce(object);
				
				setxVelocity(getxVelocity() + weightForce * Math.cos(angle));
				setyVelocity(getyVelocity() - weightForce * Math.sin(angle));
				
			}
		}
		
		
	}
	
	/**
	 * Updates the SpaceObject's location in the coordinate plane by adding
	 * its x and y velocities to their respective coordinates and adds the new
	 * (x, y) position to the trailPoints ArrayList
	 */
	public void updatePosition() {
		
		applyForces();
		
		setCenterX(getCenterX() + xVelocity);
		setCenterY(getCenterY() + yVelocity);
		
		trailPoints.add(new Point2D(getCenterX(), getCenterY()));
		if (trailPoints.size() > numPointsToSave) {
			
			trailPoints.remove(0);
			
		}
		
	}
	
	/**
	 * Draws a circle at the SpaceObject's (x, y) coordinates
	 * The color and radius of the circle are determined by their local variables in the SpaceObject
	 * @param gc the {@link GraphicsContext2D} object of the canvas
	 * @param gameCamera the GameCamera instance for the game which stores x and y drawing offsets
	 */
	public void draw(GraphicsContext gc, GameCamera gameCamera) {
		
		// Store x and y draw offsets
		double xOffset = gameCamera.getxOffset();
		double yOffset = gameCamera.getyOffset();
		
		// Fill the SpaceObject's circle, centered around its (x, y) coordinates
		
		gc.save();
		
		gc.setFill(color);
		
		gc.fillOval(getCenterX() - getRadius() - xOffset, getCenterY() - getRadius() - yOffset, getRadius() * 2, getRadius() * 2);
		
		gc.restore();
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
	 * Calculates the weight force being applied on this object by another object
	 * @param otherObject the SpaceObject applying a gravitational force on this object
	 * @return the weight force the other object is applying on this object
	 */
	public double calculateWeightForce(SpaceObject otherObject) {
		
		// Newton's equation for universal gravitation: F = (G * mass1 * mass2) / distance^2
		// mass2 is omitted to calculate weight force in one direction(from this object towards otherObject)
		
		final double G = 1;
		
		double mass = otherObject.mass;
		
		double distance = calculateDistance(this, otherObject);
		
		double weightForce = (G * mass) / (distance * distance);
		
		return weightForce;
		
	}
	
	/**
	 * Calculates the distance between two circles using the Pythagorean Theorem
	 * @param body1 a circle on the (x, y) coordinate plane
	 * @param body2 another circle on the (x, y) coordinate plane
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
	 * Tests if this SpaceObject is colliding with another SpaceObject by testing
	 * if the distance between them is less than the sum of their radii.
	 * If this method is used to check for a collision of a SpaceObject with itself,
	 * it will return false.
	 * @param otherObject the other SpaceObject to check for a collision with
	 * @return true if there is a collision between the two SpaceObjects, false otherwise
	 */
	public boolean isCollisionPresent(SpaceObject otherObject) {
		
		if (otherObject != this) {
			if (calculateDistance(this, otherObject) < getRadius() + otherObject.getRadius()) {

				return true;

			} else {

				return false;

			}
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * Gets the name associated with the SpaceObject(like "Earth" or "Moon")
	 * @return the name of the SpaceObject
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the SpaceObject
	 * @param name the new name of the SpaceObject
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the SpaceObject's color
	 * @return the color of the SpaceObject
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets a new color to be used when drawing the SpaceObject
	 * @param color the new color for the SpaceObject
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Gets the x velocity of the SpaceObject
	 * @return the x velocity of the SpaceObject
	 */
	public double getxVelocity() {
		return xVelocity;
	}

	/**
	 * @param xVelocity the new x velocity
	 */
	public void setxVelocity(double xVelocity) {
		this.xVelocity = xVelocity;
	}

	/**
	 * @return the y velocity of the SpaceObject
	 */
	public double getyVelocity() {
		return yVelocity;
	}

	/**
	 * @param yVelocity the new y velocity
	 */
	public void setyVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}

	/**
	 * @return the mass of the SpaceObject
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass the new mass of the SpaceObject
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}

	/**
	 * @param spaceObjects the ArrayList of all SpaceObjects
	 */
	public void setSpaceObjects(ArrayList<SpaceObject> spaceObjects) {
		this.spaceObjects = spaceObjects;
	}

	/**
	 * Gets the specified max size of the trailPoints ArrayList
	 * @return the max size of the trailPoints ArrayList
	 */
	public int getNumPointsToSave() {
		return numPointsToSave;
	}

	/**
	 * Sets the maximum size of the SpaceObject's ArrayList of past coordinates
	 * @param numPointsToSave the numPointsToSave to set
	 */
	public void setNumPointsToSave(int numPointsToSave) {
		this.numPointsToSave = numPointsToSave;
	}

	/**
	 * Gets the trailPoints ArrayList, which contains the preceding (x, y) coordinates the SpaceObject has been at
	 * The size of the ArrayList is determined by the numPointsToSave integer stored in the SpaceObject
	 * @return an ArrayList of {@link Point2D} objects
	 * 
	 */
	public ArrayList<Point2D> getTrailPoints() {
		return trailPoints;
	}

}
