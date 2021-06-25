package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
* Implements a visual effect decorating the menu.
*
* @author  Yejoon Jung
* @since   2021-05-28
*/
public class ScreenEffect {
	
	/**
	* Position of the effect
	*/
	Vector position;
	/**
	* velocity of the effect
	*/
	Vector velocity;
	/**
	* acceleration of the effect
	*/
	Vector acceleration;
	/**
	* speed of the effect
	*/
	float speed;

	Random rand;
	/**
	* records whether the effect represents zombie or humen
	*/
	boolean infected;
	
	public ScreenEffect(float x, float y, boolean infected) {
		position = new Vector(x, y);
		velocity = new Vector(0, 0);
		this.infected = infected;
		
		acceleration = new Vector(0, 0);
		rand = new Random();
		speed = (float) ((rand.nextGaussian() * 30 + 210) / 100);
		
	}
	
	
	public void display(Graphics2D g2d) {
		if (infected) {
			g2d.setColor(new Color(255, 26, 26));			
		} else {
			g2d.setColor(new Color(51, 100, 255));						
		}
		g2d.fillRect((int) (position.x), (int) (position.y), 40, 40);	
	}
	
	public void move() {
		
		boolean onPlat;
		
		if (position.y ==  480 - 160) {
			onPlat = true;
		} else {
			onPlat = false;			
		}
		
		acceleration.x += 4;
		
		if (onPlat) {
			int r = rand.nextInt(100);
			if (r < 40) {
				acceleration.y -= 80;
			}
		}
		
		//apply Gravity
		acceleration.y += 8;
		
		//apply Drag
		Vector drag = velocity.get();
		drag.normalize();
    	float speed = velocity.mag();
    	drag.mult(-1*0.003f*speed*speed);
    	acceleration.add(drag);

		velocity.add(acceleration);
		position.add(velocity);
		
		if (position.y > 480 - 160) {
			position.y = 480 - 160;
		}
		
		acceleration.mult(0);
    }
	
}