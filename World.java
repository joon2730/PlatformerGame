package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
* Initiate and manage entire game, user inputs, and GUI window.
*
* @author  Yejoon Jung
* @since   2021-04-23
*/
public class World extends JFrame implements ActionListener, KeyListener{

	private static final long serialVersionUID = 1L;
	
	
	final int FPS;
	/**
	* Width of the window in pixels
	*/
    public int WIDTH;
    /**
	* Height of the window in pixels
	*/
    public int HEIGHT;
    
    /**
	* Timer for event
	*/
    Timer tm;
    /**
	* Camera defining player's view
	*/
    Camera cm;
    
    /**
	* Save keyboard inputs
	*/
    boolean keyStates[];
    
    /**
	* Player in gmae
	*/
    Player player;
    /**
	* Zombies in game 
	*/
    ArrayList<Sprite> zombies;
    /**
	* Humans in game
	*/
    ArrayList<Sprite> humans;
    
    /**
	* ArrayList for ScreenEffect instances
	*/
    ArrayList<ScreenEffect> effects;
    
    /**
	* Entire map 
	*/
    Platform[] map;
    
    /**
	* Warning signal (0: off, 1-: on)
	*/
    int warning;

    /**
	* Current screen (0-9: menu, 10-19: game, 20-: result) 
	*/
    int screen;
    /**
	* Mode of the game (0: human, 1: zombie)
	*/
    int mode;
    /**
	* Difficulty of the gmae (0: easy, 1: normal, 2: hard, 3: very hard, 4: ridiculous)
	*/
    int difficulty;
    
    /**
	* Result of the game (0: none, < 0: lose, > 0: win)
	*/
    int result;
    
    Font font;
    Random rand;
    
    /**
	* Counts total time the game played in frame
	*/
    long cnt;
    /**
	* Counts the total sum of the player's velocity
	*/
    long velocity_cnt;
    /**
	* Remaining distance 
	*/
    int rem_dist;
    /**
	* Counts the number of zombies and humans alive in the game.
	*/
    int zombie_cnt, human_cnt;
    
    public World(int fps, int width, int height) throws FileNotFoundException {
    	this.FPS = fps;
    	this.WIDTH = width;
    	this.HEIGHT = height;
    	
    	init();
    	
    	//main screen
    	screen = 0;
    	rand = new Random();
    	effects = new ArrayList<ScreenEffect>();
    	
    	while (true) {
    		result = 0;
    		mode = 0;
    		difficulty = 0;
    		warning = 0;
    		velocity_cnt = 0;
    		getContentPane().setBackground(new Color(240, 240, 255));
    		
    		for (int i=0; i < (int) ((rand.nextGaussian() * 2 + 2)); i++) {
    			effects.add(new ScreenEffect(-40, 480 - 160, false));
    		}
    		for (int i=0; i < (int) ((rand.nextGaussian() * 2 + 2)); i++) {
    			effects.add(new ScreenEffect(-280, 480 - 160, true));
    		}
    		while (screen != 10) {
    			try {
    				Thread.sleep(3000/FPS);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    			
    			if (1 == rand.nextInt(6)) {
    				for (int i=0; i < (int) ((rand.nextGaussian() * 2 + 2)); i++) {
    					effects.add(new ScreenEffect(-240, 480 - 160, false));
    				}
    				for (int i=0; i < (int) ((rand.nextGaussian() * 2 + 2)); i++) {
    					effects.add(new ScreenEffect(-480, 480 - 160, true));
    				}
    			}
    			
    			repaint();    	
    		}
    		
    		
    		effects.clear();
    		startGame();
    		
    		cnt = 4;
    		while (screen == 10) {
    			if (cnt <= 1) {
    				screen = 11;
    			}
    			repaint();
    			cnt--;
    			try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    		
			while (screen == 11 && result == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (mode == 0) {
					if (player.arrived) {
						rem_dist = 0;
			    		zombie_cnt = zombies.size();
			    		human_cnt = humans.size();
						result = 1;
					} else if (player.infected) {
						rem_dist = (int) (map.length * WIDTH * 2 - player.position.x) / 20;
			    		zombie_cnt = zombies.size();
			    		human_cnt = humans.size();
						result = -1;
					}
				} else if (mode == 1) {
					if (humans.size() == 0) {
						rem_dist = (int) (map.length * WIDTH * 2 - player.position.x) / 20;
			    		zombie_cnt = zombies.size();
			    		human_cnt = humans.size();
						result = 2;
					}
					Human h;
					for (Sprite s : humans) {
						h = (Human) s;
						if (h.arrived) {
							rem_dist = (int) (map.length * WIDTH * 2 - player.position.x) / 20;
				    		zombie_cnt = zombies.size();
				    		human_cnt = humans.size();
							result = -2;
							break;
						}
					}
				}
			}
    		
    		while (screen == 20 || screen == 11) {
    			//wait
    			try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		repaint();
    	}
        
    }

	void init() {
		setVisible(true);
    	setTitle("Engine");
    	getContentPane().setBackground(new Color(240, 240, 255));
        setVisible(true);
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        this.addKeyListener(this);        
        keyStates = new boolean[]{false, false, false, false};
        
        zombies = new ArrayList<Sprite>();
		humans = new ArrayList<Sprite>();

    }
	
	/**
	* Initiate variables and objects.
	*/
	public void startGame() throws FileNotFoundException {
		map = Platform.generateMap(WIDTH, HEIGHT);
		
		zombies.clear();
		humans.clear();
		
		if (mode == 0) {
			player = new Player(WIDTH / 2 + 100, 200, false, difficulty);			
			zombies.add((Sprite) new Zombie(120, 320, difficulty));
			for (int i=0; i < 50 - 1; i++) {
				humans.add((Sprite) new Human(WIDTH / 2 + rand.nextInt(240) + 120, 200, difficulty, mode));
			}
			humans.add(player);
		} else if (mode == 1) {
			player = new Player(120, 320, true, difficulty);						
			zombies.add(player);
			for (int i=0; i < 50; i++) {
				humans.add((Sprite) new Human((WIDTH / 2) + (difficulty * WIDTH / 8) + rand.nextInt(240) + 120, 200, difficulty, mode));
			}
		}
		
		cm = new Camera(player, WIDTH, HEIGHT);
		
		tm = new Timer(1000/FPS, this);
		repaint();
		
	}
    
	@Override
    public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		paintComponent(g2d);			
    }
    
    public void paintComponent(Graphics2D g2d) {
    	
    	if (screen == 0) {
	    	for (int i=0; i < effects.size(); i++) {
	    		effects.get(i).move();
	    		effects.get(i).display(g2d);
	    		if (effects.get(i).position.x > WIDTH) {
	    			effects.remove(i);
	    		}
	    	}
	    	
	    	font = new Font ("Impact", 1, 90);
	    	g2d.setColor(new Color(5, 5, 50));
	    	g2d.setFont (font);
	    	g2d.drawString ("The Infection", 100, HEIGHT / 3);
	    	
	    	font = new Font ("Courier New", 1, 20);
	    	g2d.setColor(new Color(100, 100, 100));
	    	g2d.setFont (font);
	    	g2d.drawString ("Press Enter to Continue...", 110, (int) (HEIGHT * 0.45));

	    	g2d.setColor(new Color(0, 0, 0));
	    	g2d.fillRect(0, HEIGHT - 120, WIDTH, HEIGHT);
	    
    	} else if (screen == 1) {
    		for (int i=0; i < effects.size(); i++) {
	    		effects.get(i).move();
	    		effects.get(i).display(g2d);
	    		if (effects.get(i).position.x > WIDTH) {
	    			effects.remove(i);
	    		}
	    	}
    		
	    	font = new Font ("Courier New", 1, 20);
	    	g2d.setColor(new Color(100, 100, 100));
	    	g2d.setFont (font);
	    	g2d.drawString ("Use Arrow Keys to Choose mode...", 110, (int) (HEIGHT * 0.45));
	    	
	    	font = new Font ("Impact", 1, 90);
	    	g2d.setFont (font);
	    	if (mode == 1) {
	    		g2d.setColor(new Color(135, 0, 0));	
		    	g2d.drawString ("Zombie", 100, HEIGHT / 3);
		    	g2d.setColor(new Color(200, 26, 26));	
		    	g2d.fillRect(WIDTH / 2 - 20, HEIGHT - 160, 40, 40);
			} else {
				g2d.setColor(new Color(0, 0, 135));	
				g2d.drawString ("Human", 100, HEIGHT / 3);
				g2d.setColor(new Color(26, 26, 200));	
				g2d.fillRect(WIDTH / 2 - 20, HEIGHT - 160, 40, 40);
			}
	    	
	    	g2d.setColor(new Color(0, 0, 0));
	    	g2d.fillRect(0, HEIGHT - 120, WIDTH, HEIGHT);
	    
    	} else if (screen == 2) {
    		for (int i=0; i < effects.size(); i++) {
	    		effects.get(i).move();
	    		effects.get(i).display(g2d);
	    		if (effects.get(i).position.x > WIDTH) {
	    			effects.remove(i);
	    		}
	    	}
    		
    		font = new Font ("Courier New", 1, 20);
	    	g2d.setColor(new Color(100, 100, 100));
	    	g2d.setFont (font);
	    	g2d.drawString ("Use Arrow Keys to Choose difficulty...", 110, (int) (HEIGHT * 0.45));
    		
	    	font = new Font ("Impact", 1, 90);
	    	g2d.setFont (font);
    		if (difficulty == 0) {
				g2d.setColor(new Color(52, 52, 200));	
		    	g2d.drawString ("Easy", 100, HEIGHT / 3);
			} else if (difficulty == 1) {
				g2d.setColor(new Color(26, 26, 200));	
				g2d.drawString ("Normal", 100, HEIGHT / 3);
			} else if (difficulty == 2) {
				g2d.setColor(new Color(200, 26, 26));	
				g2d.drawString ("Hard", 100, HEIGHT / 3);
			} else if (difficulty == 3) {
	    		g2d.setColor(new Color(170, 13, 13));	
	    		g2d.drawString ("Very Hard", 100, HEIGHT / 3);
			} else if (difficulty == 4) {
				g2d.setColor(new Color(150, 0, 0));	
				g2d.drawString ("Ridiculous", 100, HEIGHT / 3);
			}
    		
    		g2d.setColor(new Color(0, 0, 0));
	    	g2d.fillRect(0, HEIGHT - 120, WIDTH, HEIGHT);
	    	
    	} else if (screen >= 10 && screen < 20 && cm != null) {
	    	int left = (int) (cm.view.x / (WIDTH * 2));
			int right = (int) ((cm.view.x + WIDTH) / (WIDTH * 2));
			if (left < map.length && left >= 0) {
				map[left].display(g2d, cm);
			}
			if (left != right && right < map.length && right >= 0) {
				map[right].display(g2d, cm);				
			}
			
			for (Sprite h: humans) {
				h.display(g2d, cm);	
			}
			for (Sprite z: zombies) {
				z.display(g2d, cm);			
			}
			
			if (screen == 10) {
				font = new Font("Impact", 1, 60);
				g2d.setFont(font);
				g2d.setColor(new Color(100, 10, 10));
				g2d.drawString(String.valueOf(cnt), 350, HEIGHT / 3);	
			} else if (screen == 11) {
				Font font = new Font("Courier New", 1, 20);
				g2d.setFont(font);
				
				g2d.setColor(new Color(111, 41, 0));
				g2d.drawString ("Zombies " + zombies.size(), 20, 50);
				
				g2d.setColor(new Color(0, 41, 111));
				g2d.drawString("Humans " + humans.size(), 150, 50);
				
				g2d.setColor(new Color(0, 0, 0));
				
				int distance = (int) (map.length * WIDTH * 2 - player.position.x);
				int speed = (int) (player.velocity.x * FPS) / 20;
				cnt++;
				velocity_cnt += speed;
				if (distance > WIDTH) {
					g2d.drawString("Remaining Distance: " + distance/20 + "m", 400, 50);		
					g2d.drawString("Current Speed: " + speed + "m/s", 400, 80);			
				}
				
				if (warning >= 1 && warning < 15 && !player.infected && result == 0) {
					font = new Font("Impact", 1, 60);
					g2d.setFont(font);
					g2d.setColor(new Color(100, 10, 10));
					g2d.drawString("Warning!", 250, HEIGHT / 3);			
				}					
				
				if (result != 0) {
					if (result == 1) {
						font = new Font("Impact", 1, 60);
						g2d.setFont(font);
						g2d.setColor(new Color(0, 52, 0));
						g2d.drawString("Secured", 250, HEIGHT / 3);						
					} else if (result == -1) {
						getContentPane().setBackground(new Color(240, 160, 160));
						font = new Font("Impact", 1, 60);
						g2d.setFont(font);
						g2d.setColor(new Color(51, 0, 0));
						g2d.drawString("Infected", 250, HEIGHT / 3);
						
					} else if (result == 2) {
						font = new Font("Impact", 1, 60);
						g2d.setFont(font);
						g2d.setColor(new Color(0, 0, 0));
						g2d.drawString("Completed", 250, HEIGHT / 3);	
					} else if (result == -2) {
						font = new Font("Impact", 1, 60);
						g2d.setFont(font);
						g2d.setColor(new Color(52, 0, 0));
						g2d.drawString("Missed", 260, HEIGHT / 3);		
					}
					font = new Font ("Courier New", 1, 20);
			    	g2d.setColor(new Color(0, 0, 0));
			    	g2d.setFont (font);
			    	g2d.drawString ("Press Enter to Check Result...", 250, (int) (HEIGHT * 0.4));
				}
				
				tm.start();
			}
    	} else if (screen == 20) {
    		tm.stop();
    		font = new Font("Impact", 1, 80);
			g2d.setFont(font);
			
			if (result == 1) {
				g2d.setColor(new Color(13, 13, 52));	
				g2d.drawString("CLEAR", 290, HEIGHT / 4);		
				
			} else if (result == -1) {
				g2d.setColor(new Color(52, 13, 13));	
				g2d.drawString("FAIL", 300, HEIGHT / 4);		
				
			} else if (result == 2) {
	    		g2d.setColor(new Color(52, 13, 13));	
	    		g2d.drawString("CLEAR", 290, HEIGHT / 4);		
	    		
			} else if (result == -2) {
				g2d.setColor(new Color(13, 13, 52));	
	    		g2d.drawString("FAIL", 300, HEIGHT / 4);	
	    		
	    	}
		
			g2d.setColor(new Color(0, 0, 0));
			int score =  (int) (velocity_cnt / cnt);
			if (result > 0) {
				score += difficulty * 50;
				score *= 2;
			} else {
				score += difficulty * 20;
				score /= 2;
			}
			
			font = new Font ("Courier New", 1, 30);
	    	g2d.setFont (font);
	    	g2d.drawString ("Press Enter to Return to Main Screen...", 10, 460);
	    	
			font = new Font("Impact", 1, 40);
			g2d.setFont(font);
			
			if (mode == 0) {
				g2d.drawString("Mode:    Human", 10, 150);
				
				score += 100 - rem_dist / 10;
			} else if (mode == 1) {
				g2d.drawString("Mode:    Zombie", 10, 150);
				
				score += zombies.size();
			}
			
			if (difficulty == 0) {
				g2d.drawString("Difficulty:    Easy", 10, 180);							
			} else if (difficulty == 1) {
				g2d.drawString("Difficulty:    Normal", 10, 180);							
			} else if (difficulty == 2) {
				g2d.drawString("Difficulty:    Hard", 10, 180);							
			} else if (difficulty == 3) {
				g2d.drawString("Difficulty:    Very Hard", 10, 180);							
			} else if (difficulty == 4) {
				g2d.drawString("Difficulty:    Ridiculous", 10, 180);							
			}
			
			g2d.drawString("Zombie:    " + zombie_cnt + "    Human:    " + human_cnt, 10, 240);	
			g2d.drawString("Remaining Distance:    " + rem_dist, 10, 270);	
			g2d.drawString("Total Time Played:    " + cnt / FPS + " second", 10, 300);							
			g2d.drawString("Average Speed:    " + velocity_cnt / cnt + " m/s", 10, 330);	
			
			g2d.drawString("Score:    " + score, 10, 390);
			
			String grade = "Error";
			if (score < 0) {
				g2d.setColor(new Color(0, 80, 0));	
				grade = "Troll";
			} else if (score < 10) {
				grade = "F";
			} else if (score < 20) {
				grade = "D";
			} else if (score < 40) {
				grade = "C";
			} else if (score < 80) {
				grade = "B";
			} else if (score < 160) {
				grade = "A";
			} else if (score < 240) {
				grade = "A+";
			} else if (score < 280) {
				grade = "A++";
			} else if (score < 320) {
				g2d.setColor(new Color(80, 60, 0));	
				grade = "Superb";
			} else if (score < 520) {
				g2d.setColor(new Color(136, 163, 208));	
				grade = "Epic";
			} else {
				g2d.setColor(new Color(120, 121, 118));	
				grade = "Legendary";
			}
			g2d.drawString("Grade:    " + grade, 10, 420);
    	}
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {  
    	int min = 999999;
    	if (cnt / FPS > 5 - difficulty || mode == 1) {
	    	for (Sprite z: zombies) {
				z.move(keyStates, map, WIDTH);
				
				if (min > player.position.x - z.position.x) {
					min = (int) (player.position.x - z.position.x);
				}
			}
    	}
    	
    	for (int i=0; i < humans.size(); i++) {
    		humans.get(i).move(keyStates, map, WIDTH);
    		if (humans.get(i).detectInfection(zombies)) {
    			zombies.add(humans.get(i));
    			humans.remove(i);
    		}
    	}
    	
    	if (!player.infected) {
    		if (min < 0) {
    			min *= -1;
    		}
    		if (min < WIDTH * 1.5) {
    			warning += 1;
    			if (warning >= 30) {
    				warning = 1;
    			}
        	} else {
        		warning = 0;
        	}
    	}
    	    	
    	cm.scroll(player);
    	repaint();
    }
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (screen == 0) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	    		screen = 1; 
	    	}
		} else if (screen == 1) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
	    		if (mode == 1) {
	    			mode = 0;
    		    	getContentPane().setBackground(new Color(240, 240, 255));
	    		} else {
	    			mode = 1;
    		    	getContentPane().setBackground(new Color(240, 160, 160));
	    		}
	    	}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	    		screen = 2;
	    	}
		} else if (screen == 2) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {						
				if (difficulty < 4) {
					difficulty += 1;
				} else {
					difficulty = 0;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {						
				if (difficulty > 0) {
					difficulty -= 1;
				} else {
					difficulty = 4;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				screen = 10;
			}
		} else if (screen == 20) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				screen = 0;
			}
		} else {
			 if (result != 0) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					screen = 20;
				}
			 }
			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
	    		keyStates[0] = true;
	    	}
	    	if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
	    		keyStates[1] = true;
	    	}
	    	if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
	    		keyStates[2] = true;
	    	}
	    	if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
	    		keyStates[3] = true;
	    	}
			if (e.getKeyCode() == KeyEvent.VK_I) {
				System.out.println("Camera position " + cm.view);
				System.out.println("Player position " + player.position);
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
    		keyStates[0] = false;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
    		keyStates[1] = false;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
    		keyStates[2] = false;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
    		keyStates[3] = false;
    	}
		
	}

}
