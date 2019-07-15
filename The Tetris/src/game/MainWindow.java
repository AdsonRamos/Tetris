package game;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	protected MenuPanel menu;
	protected GamePanel game;
	
	public MainWindow() {
		super("Tetris");
		
		menu = new MenuPanel(this);
		
		try {
			setIconImage(ImageIO.read(new File("res/tetris_ico.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setSize(600, 700);
		add(menu);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}
}
