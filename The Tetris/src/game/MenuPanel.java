package game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MenuPanel extends JPanel implements ActionListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 600;
	public static final int HEIGHT = 700;
	
	private BufferedImage background;
	private BufferedImage title;
	private BufferedImage start;
	private BufferedImage exit;
	private BufferedImage selector;
	private BufferedImage[] shapes;
	
	private int menuChoice;
	
	private Timer timer;
	
	private int count;
	
	private Random r;
	
	private int a, b, c;
	
	private MainWindow window;
	
	public MenuPanel(MainWindow window) {
		
		this.window = window;
		
		setFocusable(true);
		addKeyListener(this);
		
		r = new Random();
		a = r.nextInt(7);
		b = r.nextInt(7);
		c = r.nextInt(7);
		
		menuChoice = 0;
		shapes = new BufferedImage[7];
		
		count = 0;
		
		try {
			background = ImageIO.read(new File("res/background.png"));
			title = ImageIO.read(new File("res/title.png"));
			start = ImageIO.read(new File("res/start.png"));
			exit = ImageIO.read(new File("res/exit.png"));
			selector = ImageIO.read(new File("res/logo.png"));
			
			shapes[0] = ImageIO.read(new File("res/shapes/LineShape.png"));
			shapes[1] = ImageIO.read(new File("res/shapes/LShape.png"));
			shapes[2] = ImageIO.read(new File("res/shapes/MirroredLShape.png"));
			shapes[3] = ImageIO.read(new File("res/shapes/SquareShape.png"));
			shapes[4] = ImageIO.read(new File("res/shapes/SShape.png"));
			shapes[5] = ImageIO.read(new File("res/shapes/TShape.png"));
			shapes[6] = ImageIO.read(new File("res/shapes/ZShape.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		timer = new Timer(20, this);
		timer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		render((Graphics2D) g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		update();
		repaint();
		
		
		count++;
		if(count == 50) {
			// trocar as imagens
			count = 0;
			a = r.nextInt(7);
			b = r.nextInt(7);
			c = r.nextInt(7);
		}
	}
	
	private void render(Graphics2D g) {
		g.drawImage(background, 0, 0, null);
		g.drawImage(title, 90, 97, null);
		g.drawImage(start, 118, 311, null);
		g.drawImage(exit, 140, 346, null);
		
		g.drawImage(shapes[a], 410, 60, null);
		g.drawImage(shapes[b], 410, 280, null);
		g.drawImage(shapes[c], 410, 470, null);
		
		if(menuChoice == 0) {
			g.drawImage(selector, 80, 306, null);
		} else if(menuChoice == 1) {
			g.drawImage(selector, 103, 340, null);
		}
		
	}
	
	private void update() {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			menuChoice++;
			if(menuChoice > 1) {
				menuChoice = 0;
			}
		} else if(e.getKeyCode() == KeyEvent.VK_UP) {
			menuChoice--;
			if(menuChoice < 0) {
				menuChoice = 1;
			}
		} else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (menuChoice == 0) {
				start();
			} else	if(menuChoice == 1) {
				System.exit(JFrame.EXIT_ON_CLOSE);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	private void start() {
		window.removeKeyListener(window.menu);
		window.remove(window.menu);
		
		GamePanel game = new GamePanel();
		window.setSize(WIDTH + 1, HEIGHT);
		window.setContentPane(game);
		window.game = game;
		window.game.requestFocus();
		window.addKeyListener(game);
	}
	
}
