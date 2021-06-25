package game;

import java.awt.Graphics2D;

/**
* Implements an object.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public abstract class Object {
	
	/**
	* Current position of the object in pixels
	*/
	Vector position;
	/**
	* Width of the object in pixels
	*/
	int width;
	/**
	* Height of the object in pixels
	*/
	int height;
	
	public Object(float x, float y, int width, int height) {
		this.width = width;
		this.height = height;
		position = new Vector(x, y);
	}
	
	/**
	* Display the object on the window.
	*/
	public abstract void display(Graphics2D g2d, Camera cm);
	
	public abstract String toString();
}
