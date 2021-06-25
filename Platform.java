package game;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
//import java.util.Random;
import java.util.Random;
import java.util.Scanner;

/**
* Implements a platform made up of objects.
*
* @author  Yejoon Jung
* @since   2021-05-07
*/
public class Platform {
	
	/**
	* Width of the window in pixels
	*/
	static int WIDTH;
	/**
	* Height of the window in pixels
	*/
	static int HEIGHT;
	/**
	* Width of one block of an object (width / WIDTH_UNIT)
	*/
	static int X_UNIT;
	/**
	* Height of one block of an object (height / HEIGHT_UNIT)
	*/
	static int Y_UNIT;
	/**
	* constant value used to define X_UNIT
	*/
	final static int WIDTH_UNIT = 12;
	/**
	* constant value used to define Y_UNIT
	*/
	final static int HEIGHT_UNIT = 8;
	
	/**
	* objects in the platform
	*/
	ArrayList<Object> objects = new ArrayList<Object>();
	/**
	* Position of the platform (fixed)
	*/
	final Vector position;
	
	/**
	* A static method that generates map.
	* @throws FileNotFoundException 
	*/
	public static Platform[] generateMap(int width, int height) throws FileNotFoundException {
		WIDTH = width;
		HEIGHT = height;
		X_UNIT = WIDTH / WIDTH_UNIT;
		Y_UNIT = HEIGHT / HEIGHT_UNIT;
		Platform[] map = openMapFile();
		return map;
	}
	
	/**
	* A static method that reads a file and generates map.
	* @throws FileNotFoundException 
	*/
	public static Platform[] openMapFile() throws FileNotFoundException {

		try {			
			File f = new File("./map.csv");
			Scanner sc1, sc2;
			sc1 = new Scanner(f);
			sc1.useDelimiter(",");
			
			sc2 = new Scanner(sc1.nextLine());
			sc2.useDelimiter(",");
			int length = sc2.nextInt();
			Platform[] map = new Platform[length];
			
			for (int i=0; i < map.length; i++) {
				int[][] matrix = new int[HEIGHT_UNIT][WIDTH_UNIT * 2];
				
				for (int v=0; v < HEIGHT_UNIT; v++) {
					sc2 = new Scanner(sc1.nextLine());
					sc2.useDelimiter(",");
					
					for (int h=0; h < WIDTH_UNIT * 2; h++) {
						matrix[v][h] = sc2.nextInt();	
					}
				}

				map[i] = new Platform(WIDTH * 2 * i, 0, matrix);
			}
		        
		    return map;
			
		} catch (FileNotFoundException e) {
			throw(e);
		}
		
	}
	
	public Platform(float x, float y, int[][] matrix) {
		position = new Vector(x, y);
		
		translate(matrix);
	}
	
	/**
	* Translate matrix into an ArrayList of objects.
	*/
	public void translate(int[][] matrix) {
		   for (int i=0; i < matrix.length; i++) {
			   for (int j=0; j < matrix[0].length; j++) {
					switch(matrix[i][j]) {
					  case 1:
						//wall
					    objects.add(new Wall(j * X_UNIT + position.x, i * Y_UNIT + position.y, X_UNIT, Y_UNIT));
					    break;
					  case 2:
						//water
						objects.add(new Water(j * X_UNIT + position.x, i * Y_UNIT + position.y, X_UNIT, Y_UNIT));
					    break;
					  case 3:
						//Mud
					    objects.add(new Mud(j * X_UNIT + position.x, i * Y_UNIT + position.y, X_UNIT, Y_UNIT));
					    break;					  
					  case 4:
						  //Ice
						  objects.add(new Ice(j * X_UNIT + position.x, i * Y_UNIT + position.y, X_UNIT, Y_UNIT));
						  break;					  
					  case 5:
						  //JumpPad
						  objects.add(new JumpPad(j * X_UNIT + position.x, i * Y_UNIT + position.y, X_UNIT, Y_UNIT));
						  break;	
					  case 6:
						  //RoughWall
						  objects.add(new RoughWall(j * X_UNIT + position.x, i * Y_UNIT + position.y, X_UNIT, Y_UNIT));
						  break;
					  case 9:
						  //SafeZone
						  objects.add(new SafeZone(j * X_UNIT + position.x, i * Y_UNIT + position.y, X_UNIT, Y_UNIT));
						  break;
				}
		   }
		}
	}
	
	/**
	* Display the platform on the window.
	*/
	public void display(Graphics2D g2d, Camera cm) {
		for(Object o : objects) {
			o.display(g2d, cm);
		}
	}
	
	@Override
	public String toString( ) {
		String output = "";
		for (Object o :objects) {
			output += o + "\n";
		}
		return output; 
	}

}
