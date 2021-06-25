package game;

import java.awt.Color;
import java.awt.Graphics2D;

/**
* Implements an object representing safe zone.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class SafeZone extends Object {
	
	/**
	* Drag constant of safe zone
	*/
	final static float CD = 0.003f; 
	
	public SafeZone(float x, float y, int width, int height) {
		super(x, y, width, height);
	}
	
	/**
	* Display the object on the window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		g2d.setColor(new Color(200, 224, 200));
		g2d.fillRect((int) (position.x - cm.view.x), (int) (position.y - cm.view.y), width, height);	    			
	}
	
	public String toString() {
		return "SafeZone (x: " + position.x + " y: " + position.y + ")";
	}
}
