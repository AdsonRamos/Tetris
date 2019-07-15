package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import game.Shape.Tetrominos;

public class GamePanel extends JPanel implements ActionListener, KeyListener{

	/**
	 * @author Adson Ramos
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 600;
	public static final int HEIGHT = 700;

	private static final Color[] COLORS = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
			new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
			new Color(218, 170, 0) };

	private int gridWidth = 10;
	private int gridHeight = 20;
	private int startGridX = 15, startGridY = 15;

	private int squareLength = 32;
	
	private boolean pause = false;

	private boolean showGrid = false;
	
	private boolean gameOver = false;

	private Timer timer;

	private Tetrominos[] board;

	private Shape currentShape;
	
	private Shape nextPiece;
	
	private int score = 0, level = 1;
	
	private int initialDelay = 400;

	private int curY = 1;
	private int curX = 4;
	
	
	private int currentRecord;
	
	private File recordFile;
	
	public GamePanel() {
		timer = new Timer(initialDelay, this);
		
		addKeyListener(this);
		setFocusable(true);

		
		startGame();
		
		try {
			checkRecord();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkRecord() throws IOException {
		recordFile = new File("res/record.dat");
		if(!recordFile.exists()) {
			recordFile.createNewFile();
		}
		Scanner scanner = new Scanner(recordFile);
		if(scanner.hasNextLine()) {
			currentRecord = scanner.nextInt();
		} else {
			currentRecord = 0;
		}
		scanner.close();
	}

	private Tetrominos shapeAt(int x, int y) {
		return board[y * gridWidth + x];
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A && !gameOver) {
			tryMove(curX - 1, curY, currentShape);
		} else if (e.getKeyCode() == KeyEvent.VK_D && !gameOver) {
			tryMove(curX + 1, curY, currentShape);
		} else if (e.getKeyCode() == KeyEvent.VK_E  && !gameOver) {
			tryMove(curX, curY, currentShape.rotateRight());
			repaint();		
		} else if (e.getKeyCode() == KeyEvent.VK_Q  && !gameOver) {
			tryMove(curX, curY, currentShape.rotateLeft());
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
			dropDown();
		} else if(e.getKeyCode() == KeyEvent.VK_S && !gameOver) {
			oneLineDown();
		} else if(e.getKeyCode() == KeyEvent.VK_G) {
			showGrid = !showGrid;
			repaint();
		} else if(e.getKeyCode() == KeyEvent.VK_P && !gameOver) {
			pause = !pause;
			if(pause) {
				timer.stop();
			} else {
				timer.restart();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(gameOver) {
				startGame();
			}
		}

	}

	private void startGame() {
		board = new Tetrominos[gridWidth * gridHeight];

		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				board[gridWidth * i + j] = Tetrominos.NoShape;
			}
		}


		nextPiece = new Shape();
		nextPiece.setRandomShape();
		
		currentShape = new Shape();
		currentShape.setRandomShape();
		
		score = 0;
		gameOver = false;
		initialDelay = 400;
		level = 1;
		curX = 4;
		curY = 1;
		timer.setDelay(initialDelay);
		timer.start();
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		update();
		oneLineDown();
	}

	private void oneLineDown() {
		if (!tryMove(curX, curY + 1, currentShape)) {
			// colocar outra peça e salvar a peca atual
			savePiece();
		}
	}

	private void update() {
		
		if(gameOver) {
			timer.stop();
			if(score > currentRecord) {
				try {
					FileWriter writer = new FileWriter(recordFile);
					writer.write(""+score);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Object[] options = { "Sair", "Jogar novamente" };
				this.repaint();
				int dialog = JOptionPane.showOptionDialog(null, "O que você deseja fazer?", "Novo recorde!",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (dialog == 1) {
					currentRecord = score;
					startGame();
				} else {
					System.exit(0);
				}
				
				
			}
		}
		
		if(score % 100 == 0 && score != 0) {
			levelUp();
		}
		
	}

	private void levelUp() {
		// condição para não entrar em loop infinito
		if(score / 100 == level) return;
		level++;
		if(timer.getDelay() == 50) return;
		timer.setDelay(timer.getDelay() - 50*(level-1));
	}

	private void savePiece() {
		for (int i = 0; i < 4; i++) {
			int x = curX + currentShape.x(i);
			int y = curY + currentShape.y(i);
			board[y * gridWidth + x] = currentShape.getShape();
		}

		removeFullLines();

		newPiece();
	}
	
	private void dropDown() {
		int newY = curY;

		while (newY > 0) {
			if (!tryMove(curX, newY + 1, currentShape))
				break;
			newY++;
		}

		savePiece();
	}

	private void removeFullLines() {

		int numFullLines = 0;

		for (int i = 0; i <= gridHeight - 1; i++) {

			boolean lineIsFull = true;

			for (int j = 0; j < gridWidth; j++) {
				if (shapeAt(j, i) == Tetrominos.NoShape) {
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {

				numFullLines++;
				for (int k = i; k > 0; k--) {
					for (int x = 0; x < gridWidth; x++) {
						board[k * gridWidth + x] = shapeAt(x, k - 1);
					}
				}
			}
			
			if (numFullLines > 0) {
				currentShape.setShape(Tetrominos.NoShape);
				repaint();
			}
		}
		score += numFullLines;

	}

	private void newPiece() {
		currentShape.setShape(nextPiece.getShape());
		nextPiece.setRandomShape();
		
		if(curY - 1 <= 0) {
			gameOver = true;
			repaint();
		}
		
		curX = 4;
		curY = 1;
	}

	private void render(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw grid
		g.setColor(Color.BLACK);
		if (showGrid) {
			for (int i = 0; i < gridHeight; i++) {
				for (int j = 0; j < gridWidth; j++) {
					g.drawRect(startGridX + j * squareLength, startGridY + i * squareLength, squareLength,
							squareLength);
				}
			}
		} else {
			g.drawLine(startGridX, startGridY, startGridX + 10 *squareLength, startGridY);
			g.drawLine(startGridX, startGridY, startGridX, startGridY + 20*squareLength);
			g.drawLine(startGridX + 10*squareLength, startGridY, startGridX + 10*squareLength, startGridY + 20*squareLength);
			g.drawLine(startGridX, startGridY + 20*squareLength, startGridX + 10*squareLength, startGridY + 20*squareLength);
		}
		
		// draw stats
		g.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		g.drawString("Estatísticas: ", 360, 300);
		g.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		if(!gameOver) {
			g.drawString("Pontos: "+score, 360, 320);
			g.drawString("Nível: "+level, 360, 340);
			if(currentRecord > score) {
				g.drawString("Recorde: "+currentRecord, 360, 360);				
			} else {
				g.drawString("Recorde: "+score, 360, 360);
			}
		} else {
			g.drawString("Fim de jogo!", 360, 320);
			g.drawString("Pressione ENTER para jogar.", 360, 340);
		}
		
		// draw instructions
		g.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		g.drawString("Controles: ", 360, 400);
		g.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		g.drawString("A - Mover para a esquerda", 360, 420);
		g.drawString("D - Mover para a direita", 360, 440);
		g.drawString("S - Descer mais rápido", 360, 460);
		g.drawString("ESPAÇO - Derrubar peça", 360, 480);
		g.drawString("E - Girar no sentido horário", 360, 500);
		g.drawString("Q - Girar no sentido anti-horário", 360, 520);
		g.drawString("P - Pausar jogo", 360, 540);
		g.drawString("G - Mostrar/Ocultar grid", 360, 560);
		
		// draw next piece
		g.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
		g.drawString("Próxima peça:", 400, 50);
		g.drawRect(370, 70, 200, 200);
		
		for(int i = 0; i < 4; i++) {
			int x = 5 + gridWidth + (13 + nextPiece.x(i)) * squareLength;
			int y = -5 + gridHeight + (4 + nextPiece.y(i)) * squareLength;

			drawSquare(g, x, y, nextPiece.getShape());
		}
		
		// draw shapes
		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				Tetrominos shape = shapeAt(j, i);

				if (shape != Tetrominos.NoShape) {
					drawSquare(g, 5 + gridWidth + j * squareLength, -5 + gridHeight + i * squareLength, shape);
				}
			}
		}

		// draw current shape
		if (currentShape.getShape() != Tetrominos.NoShape) {
			for (int i = 0; i < 4; i++) {
				int x = 5 + gridWidth + (curX + currentShape.x(i)) * squareLength;
				int y = -5 + gridHeight + (curY + currentShape.y(i)) * squareLength;

				drawSquare(g, x, y, currentShape.getShape());
			}
		}
		
	}

	private boolean tryMove(int newX, int newY, Shape shape) {
		for (int i = 0; i < 4; i++) {
			int x = newX + shape.x(i);
			int y = newY + shape.y(i);

			// colisão com as paredes e com o teto
			if (x < 0 || x >= gridWidth || y >= gridHeight) {
				return false;
			}

			// colisão com as outras peças
			if (shapeAt(x, y) != Tetrominos.NoShape) {
				return false;
			}

		}
		currentShape = shape;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void drawSquare(Graphics2D g, int x, int y, Tetrominos s) {
		Color color = COLORS[s.ordinal()];
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareLength - 2, squareLength - 2);
		g.setColor(color.brighter());
		g.drawLine(x, y + squareLength - 1, x, y);
		g.drawLine(x, y, x + squareLength - 1, y);
		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareLength - 1, x + squareLength - 1, y + squareLength - 1);
		g.drawLine(x + squareLength - 1, y + squareLength - 1, x + squareLength - 1, y + 1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		render((Graphics2D) g);
	}

}
