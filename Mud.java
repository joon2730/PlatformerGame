package game;

import java.awt.Color;
import java.awt.Graphics2D;

/**
* Implements an object representing mud.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Mud extends Object {
	
	/**
	* Drag constant of mud
	*/
	final static float CD = 0.05f; 
	
	public Mud(float x, float y, int width, int height) {
		super(x, y, width, height);
	}
	
	/**
	* Display the object on the window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		g2d.setColor(new Color(51, 153, 153));
		g2d.fillRect((int) (position.x - cm.view.x), (int) (position.y - cm.view.y), width, height);	    			
	}
	
	public String toString() {
		return "Mud (x: " + position.x + " y: " + position.y + ")";
	}
}
