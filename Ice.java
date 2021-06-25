package game;

import java.awt.Color;
import java.awt.Graphics2D;

/**
* Implements an object representing ice.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Ice extends Object {
	
	/**
	* Friction constant of ice
	*/
	final static float CF = 0.1f; 
	
	public Ice(float x, float y, int width, int height) {
		super(x, y, width, height);
	}
	
	/**
	* Display the object on the window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		g2d.setColor(new Color(153, 255, 255));
		g2d.fillRect((int) (position.x - cm.view.x), (int) (position.y - cm.view.y), width, height);	    			
	}
	
	public String toString() {
		return "Ice (x: " + position.x + " y: " + position.y + ")";
	}
}
