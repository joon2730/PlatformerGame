package game;

/**
* Implements a camera that determines player's view.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Camera {
	/**
	* Horizontal margin in pixels
	*/
	final float X_MARGIN;
	/**
	* Vertical margin in pixels
	*/
	final float Y_MARGIN;
	/**
	* Left boundary in pixels
	*/
	final float LEFT_BOUNDARY;
	/**
	* Right boundary in pixels
	*/
	final float RIGHT_BOUNDARY;
	/**
	* Top boundary in pixels
	*/
	final float TOP_BOUNDARY;
	/**
	* Bottom boundary in pixels
	*/
	final float BOTTOM_BOUNDARY;
	/**
	* Width of the window in pixels
	*/
	final int WIDTH;
	/**
	* Height of the window in pixels
	*/
	final int HEIGHT;
	
	/**
	* Player's current view in pixels
	*/
	Vector view;
	
	public Camera(Player player, int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		X_MARGIN = WIDTH / 2f;
		Y_MARGIN = HEIGHT / 4;
		LEFT_BOUNDARY = X_MARGIN;
		RIGHT_BOUNDARY = WIDTH - X_MARGIN;
		TOP_BOUNDARY = Y_MARGIN;
		BOTTOM_BOUNDARY = HEIGHT - Y_MARGIN;
		
		view = new Vector(0, 0);
		scroll(player);
	}
	
	/**
	* Scroll the camera and change the view according to player's position.
	*/
	public void scroll(Player player) {
		if (player.position.x + player.width > view.x + RIGHT_BOUNDARY) {
			view.x += (player.position.x + player.width) - (view.x + RIGHT_BOUNDARY);
		}
		if (player.position.x < view.x + LEFT_BOUNDARY) {
			view.x -= (view.x + LEFT_BOUNDARY) - (player.position.x);
		}
		if (player.position.y + player.height > view.y + BOTTOM_BOUNDARY) {
			view.y += (player.position.y + player.height) - (view.y + BOTTOM_BOUNDARY);
		}
		if (player.position.y < view.y + TOP_BOUNDARY) {
			view.y -= (view.y + TOP_BOUNDARY) - (player.position.y);
		}
	}
	
}
