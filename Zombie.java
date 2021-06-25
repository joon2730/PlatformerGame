package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
* Implements a sprite representing a zombie.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Zombie extends Sprite{
	
	/**
	* speed
	*/
	float speed;
	/**
	* records how many times it failed to move forward
	*/
	int notWorking;
	/**
	* previous x position
	*/
	float previousXpos;
	/**
	* jumping probability
	*/
	int jumpProb;
	/**
	* unique number affecting how often it jump compared to others
	*/
	int n;
	Random rand;
	
	public Zombie(float x, float y, int difficulty) {
		super(x, y, 40, 40);
		rand = new Random();
		n = rand.nextInt(80) + 20;
		notWorking = 0;
		if (difficulty == 0) {
			speed = 1.6f;			
		} else if (difficulty == 1) {
			speed = 1.8f;			
		} else if (difficulty == 2) {
			speed = 2.0f;			
		} else if (difficulty == 3) {
			speed = 2.2f;						
		} else if (difficulty == 4) {
			speed = 2.6f;						
		}
	}
	
	/**
	* Display the sprite on window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		g2d.setColor(new Color(255, 51, 51));
		g2d.fillRect((int) (position.x - cm.view.x), (int) (position.y - cm.view.y), width, height);	
	}
	
	/**
	* Move the sprite automatically and resolve collisions.
	*/
	public void move(boolean[] keyStates, Platform[] map, int scr_width) {
		
		if (position.x <= previousXpos) {
			notWorking++;
		} else {
			notWorking = 0;
		}
		if (notWorking < 10) {
			jumpProb = 100;
		} else if (notWorking < 50) {
			jumpProb = 0;
		} else if (notWorking < 80) {
			acceleration.x -= 4;
		} else if (notWorking < 85) {
			jumpProb = 10;
			acceleration.x -= 4;
		}
		
		float c = 0.003f;
		for (Object o : detectCollision(map, scr_width)) {
			if (o instanceof Water && Water.CD > c) {
				c = Water.CD;
			}
			if (o instanceof Mud && Mud.CD > c) {
				c = Mud.CD;
			}
		}
		
		//move
		
		int onPlat = onPlatform(map, scr_width);
		acceleration.x += speed;
		
		if (onPlat > 0 && c <= 0.005f) {
				int r = rand.nextInt(n);
				if (r < jumpProb) {
					if (onPlat == 5) {
						acceleration.y -= 80;    				
					} else {
						acceleration.y -= 40;
					}					
				}
		} else if (c > 0.005f) {
			int r = rand.nextInt(n);
			if (r < jumpProb * 3) {
				acceleration.y -= 6 + 0.04 / c;		
			}
		}
		
		//apply Gravity
		acceleration.y += 4;			
		
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
    		} else if (onPlat == 6) {
    			c = RoughWall.CF;
    		}
    		friction.mult(-1*c);
    		acceleration.add(friction);  
    	}
		
    	previousXpos = position.x;
		//update and resolve collision
		resolveCollision(map, scr_width, true);
		
    }
	
	/**
	* Detect whether the sprite was tagged by zombies.
	*/
	public boolean detectInfection(ArrayList<Sprite> zombies) {
		return true;
	}

}