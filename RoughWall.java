package game;

import java.awt.Color;
import java.awt.Graphics2D;

/**
* Implements an object representing rough wall.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class RoughWall extends Object {
	
	/**
	* Friction constant of rough wall
	*/
	final static float CF = 8f; 
	
	public RoughWall(float x, float y, int width, int height) {
		super(x, y, width, height);
	}
	
	/**
	* Display the object on the window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		g2d.setColor(new Color(96, 96, 96));
		g2d.fillRect((int) (position.x - cm.view.x), (int) (position.y - cm.view.y), width, height);	    			
	}
	
	public String toString() {
		return "RoughWall (x: " + position.x + " y: " + position.y + ")";
	}
}
