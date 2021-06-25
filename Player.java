package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
* Implements a sprite representing the player.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Player extends Sprite{
	
	/**
	* whether the player has been infected or not
	*/
	boolean infected;
	/**
	* whether the player completed the map or not
	*/
	boolean arrived;
	/**
	* previous x position of the player
	*/
	float previousXpos;
	/**
	* speed of the player
	*/
	float speed;
	
	public Player(float x, float y, boolean infected, int difficulty) {
		super(x, y, 40, 40);
		this.infected = infected;
		arrived = false;
		previousXpos = -1;
		speed = 2f;
		if (speed == 4) {
			speed += 0.1;
		}
	}
	
	/**
	* Display the sprite on window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		if (infected) {
			g2d.setColor(new Color(200, 26, 26));			
		} else {
			g2d.setColor(new Color(26, 26, 200));			
		}
		g2d.fillRect((int) (position.x - cm.view.x), (int) (position.y - cm.view.y), width, height);	    		
	}
	
	/**
	* Move the sprite according to user inputs and resolve collisions.
	*/
	public void move(boolean[] keyStates, Platform[] map, int scr_width) {
		
		float c = 0.003f;
		for (Object o : detectCollision(map, scr_width)) {
			if (o instanceof Water && Water.CD > c) {
				c = Water.CD;
			}
			if (o instanceof Mud && Mud.CD > c) {
				c = Mud.CD;
			}
			if (o instanceof SafeZone) {
				arrived = true;
			}
		}
		
		//move
		int onPlat = onPlatform(map, scr_width);
    	if (keyStates[0] == true) {
    		acceleration.x += speed;
    	} if (keyStates[1] == true) {
    		acceleration.x -= speed;
    	} if (keyStates[2] == true) {
    		if (onPlat > 0 && c <= 0.005f) {
    			if (onPlat == 5) {
    				acceleration.y -= 80;    				
    			} else {
    				acceleration.y -= 30;
    			}
    		} else if (c > 0.005f) {
    			acceleration.y -= 4 + 0.04 / c;
    		}
		} if (keyStates[3] == true) {
			acceleration.y += 4;
		}
		
		//apply Gravity
		acceleration.y += 4;			
		
		if (Math.abs(Math.round(velocity.x)) > 0 || Math.abs(Math.round(velocity.y)) > 0) {
			//apply Drag
			Vector drag = velocity.get();
			drag.normalize();
			float speed = velocity.mag();
			drag.mult(-1*c*speed*speed);
			acceleration.add(drag);
			
			//apply Friction
			if (onPlat > 0) {
				Vector friction = velocity.get();
				friction.normalize();
				c = Wall.CF;    			
				if (onPlat == 4) {
					c = Ice.CF;
				}
				friction.mult(-1*c);
				acceleration.add(friction);  
			}    	
		}
		
    	previousXpos = position.x;
    	
		//update and resolve collision
		resolveCollision(map, scr_width, infected);
    }

	/**
	* Detect whether the sprite was tagged by zombies.
	*/
	public boolean detectInfection(ArrayList<Sprite> zombies) {
		for (Sprite z : zombies) {
			boolean noXOverlap = position.x + width <= z.position.x || position.x >= z.position.x + z.width;
			boolean noYOverlap = position.y + height <= z.position.y || position.y >= z.position.y + z.height;
			if (!(noXOverlap || noYOverlap)) {
				infected = true;
				return true;
			}				
		}
		return false;
	}
	
}