package game;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
* Implements a sprite.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public abstract class Sprite {
	/**
	* Current position of the sprite in pixels
	*/
	public Vector position;
	/**
	* Current velocity of the sprite in pixels
	*/
	public Vector velocity;
	/**
	* Current acceleration of the sprite in pixels
	*/
	public Vector acceleration;
	/**
	* Width of the sprite in pixels
	*/
	public int width;
	/**
	* Height of the sprite in pixels
	*/
	public int height;
	
	public Sprite(float x, float y, int width, int height) {
		position = new Vector(x, y);
		velocity = new Vector(0, 0);
		acceleration = new Vector(0, 0);
		this.width = width;
		this.height = height;
	}
	
	/**
	* Display the sprite on the window.
	*/
	public abstract void display(Graphics2D g2d, Camera cm);
	
	/**
	* Resolve collisions and update current position.
	*/
	public void resolveCollision(Platform[] map, int scr_width, boolean infected) {
		velocity.add(acceleration);
		
		position.x +=  Math.round(velocity.x * 10) / 10;
		ArrayList<Object> collisionList = detectCollision(map, scr_width);
		if (collisionList.size() > 0) {
			for (Object o : collisionList) {
				if (o instanceof Wall || o instanceof Ice || o instanceof RoughWall || o instanceof JumpPad || infected && o instanceof SafeZone) {
					if (velocity.x > 0) {
						position.x = o.position.x - width;							
					} else if (velocity.x < 0) {
						position.x = o.position.x + o.width;							
					}					
					velocity.x = 0;
				}
			}
		}
		position.y +=  Math.round(velocity.y * 10) / 10;
		collisionList = detectCollision(map, scr_width);
		if (collisionList.size() > 0) {
			for (Object o : collisionList) {
				if (o instanceof Wall || o instanceof Ice || o instanceof RoughWall || o instanceof JumpPad || infected && o instanceof SafeZone) {
					if (velocity.y > 0) {
						position.y = o.position.y - height;		
					} else if (velocity.y < 0) {
						position.y = o.position.y + o.height;							
					}									
					velocity.y = 0;
				}
			}
		}
		
		acceleration.mult(0);
	}
	
	/**
	* Detect whether the sprite collided with any objects.
	*/
	public ArrayList<Object> detectCollision(Platform[] map, int scr_width) {
		ArrayList<Object> collisionList = new ArrayList<Object>();
		int left = (int) (position.x / (scr_width * 2));
		int right = (int) ((position.x + width) / (scr_width * 2));
		if (left < map.length && left >= 0) {
			for (Object o : map[left].objects) {
				boolean noXOverlap = position.x + width <= o.position.x || position.x >= o.position.x + o.width;
				boolean noYOverlap = position.y + height <= o.position.y || position.y >= o.position.y + o.height;
				if (!(noXOverlap || noYOverlap)) {
					collisionList.add(o);
				}				
			}
			
		}
		if (left != right && right < map.length && right >= 0) {
			for (Object o : map[right].objects) {
				boolean noXOverlap = position.x + width <= o.position.x || position.x >= o.position.x + o.width;
				boolean noYOverlap = position.y + height <= o.position.y || position.y >= o.position.y + o.height;
				if (!(noXOverlap || noYOverlap)) {
					collisionList.add(o);
				}				
			}
		}
		return collisionList;
	}
	
	/**
	* Detect whether the sprite is on the platform.
	*/
	public int onPlatform(Platform[] map, int scr_width) {
		position.y += 1;
		for (Object o : detectCollision(map, scr_width)) {
			if (o instanceof RoughWall) {
				position.y -= 1;
				return 6;				
			} else if (o instanceof Wall) {
				position.y -= 1;
				return 1;				
			} else if (o instanceof Ice) {
				position.y -= 1;
				return 4;				
			} else if (o instanceof JumpPad) {
				position.y -= 1;
				return 5;				
			}
			
		}
		position.y -= 1;
		return 0;
	}
	
	/**
	* Move the sprite and resolve collision.
	*/
	public abstract void move(boolean[] keyStates, Platform[] map, int width);
	 
	/**
	* Detect whether the sprite has been tagged by zombies.
	*/
	public abstract boolean detectInfection(ArrayList<Sprite> zombies);
	 
}
