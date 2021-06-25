package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
* Implements a sprite representing a human.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Human extends Sprite{
	
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
	* whether it has been infected or not
	*/
	boolean infected;
	/**
	* whether it completed the map
	*/
	boolean arrived;
	/**
	* unique number affecting how often it jump compared to others
	*/
	int n;
	/**
	* speed
	*/
	float speed;
	Random rand;
	
	public Human(float x, float y, int difficulty, int mode) {
		super(x, y, 40, 40);
		rand = new Random();
		n = rand.nextInt(25) + 5;
		notWorking = 0;
		arrived = false;
		
		if (difficulty == 0) {
			speed = (float) ((rand.nextGaussian() * 30 + 180) / 100);
		} else if (difficulty == 1) {
			speed = (float) ((rand.nextGaussian() * 30 + 190) / 100);
		} else if (difficulty == 2) {
			speed = (float) ((rand.nextGaussian() * 30 + 200) / 100);
		} else if (difficulty == 3) {
			speed = (float) ((rand.nextGaussian() * 30 + 210) / 100);
		} else if (difficulty == 4) {
			speed = (float) ((rand.nextGaussian() * 30 + 230) / 100);
		}
		
		speed -= mode * 0.2;
		
		infected = false;
	}
	
	/**
	* Display the sprite on window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		if (infected) {
			g2d.setColor(new Color(255, 26, 26));			
		} else {
			g2d.setColor(new Color(51, 100, 255));						
		}
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
		if (notWorking == 0) {
			jumpProb = n;	
		} else if (notWorking < 10) {
			jumpProb = 100; // jump
		} else if (notWorking < 30) {
			jumpProb = 0; // wait
		} else if (notWorking < 60) {
			acceleration.x -= 4;
		} else if (notWorking < 65) {
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
			if (o instanceof SafeZone) {
				arrived = true;
			}
		}
		
		//move
		int onPlat = onPlatform(map, scr_width);
		acceleration.x += speed;
		
		if (onPlat > 0 && c <= 0.005f) {
				int r = rand.nextInt(100);
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