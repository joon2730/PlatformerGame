package game;

import java.awt.Color;
import java.awt.Graphics2D;

/**
* Implements an object representing wall.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Wall extends Object {
	
	/**
	* Friction constant of wall
	*/
	final static float CF = 0.8f; 
	
	public Wall(float x, float y, int width, int height) {
		super(x, y, width, height);
	}
	
	/**
	* Display the object on the window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		g2d.setColor(new Color(0, 0, 0));
		g2d.fillRect((int) (position.x - cm.view.x), (int) (position.y - cm.view.y), width, height);	    			
	}
	
	public String toString() {
		return "Wall (x: " + position.x + " y: " + position.y + ")";
	}
}
