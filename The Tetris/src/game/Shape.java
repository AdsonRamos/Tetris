package game;

import java.util.Random;

public class Shape {
	
	enum Tetrominos {
		
		NoShape(new int[][] {{0,0}, {0,0}, {0,0}, {0,0}}),
		ZShape(new int[][] {{0,-1}, {0,0}, {-1,0}, {-1,1}}),
		SShape(new int[][] {{0,-1}, {0,0}, {1,0}, {1,1}}),
		LineShape(new int[][] {{0,-1}, {0,0}, {0,1}, {0,2}}),
		TShape(new int[][] {{-1,0}, {0,0}, {1,0}, {0,1}}),
		SquareShape(new int[][] {{0,0}, {1,0}, {0,1}, {1,1}}),
		LShape(new int[][] {{-1,-1}, {0,-1}, {0,0}, {0,1}}),
		MirroredLShape(new int[][] {{1,-1}, {0,-1}, {0,0}, {0,1}});
		
		
		public int[][] coords;
		
		private Tetrominos (int [][] coords) {
			this.coords = coords;
		}
		
	}
	
	private Tetrominos pieceShape;
	private int[][] coords;
	
	public Shape() {
		
		coords = new int[4][2];
		setShape(Tetrominos.NoShape);
		
	}
	
	
	public void setShape(Tetrominos shape) {
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 2; ++j) {
				coords[i][j] = shape.coords[i][j];
			}
		}
		
		pieceShape = shape;
	}
	
	private void setX(int index, int x) {
		coords[index][0] = x;
	}
	
	private void setY(int index, int y) {
		coords[index][1] = y;
		
	}
	
	public int x(int index) {
		return coords[index][0];
	}
	
	public int y(int index) {
		return coords[index][1];
	}
	
	public Tetrominos getShape() {
		return pieceShape;
	}
	
	public void setRandomShape() {
		// seleciona uma das 7 formas
		Random r = new Random();
		int x = Math.abs(r.nextInt()) % 7 + 1;
		Tetrominos[] values = Tetrominos.values();
		setShape(values[x]);
	}
	
	public int minX() {
		int m = coords[0][0];
		
		for(int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][0]);
		}
		return m;
	}
	
	public int minY() {
		int m = coords[0][1];
		
		for(int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][1]);
		}
		return m;
	}
	
	public int maxY() {
		int m = coords[0][1];
		
		for(int i = 0; i < 4; i++) {
			m = Math.max(m, coords[i][i]);
		}
		
		return m;
	}
	
	public Shape rotateLeft() {
		if(pieceShape == Tetrominos.SquareShape) return this;
		
		Shape result = new Shape();
		result.pieceShape = pieceShape;
		
		for(int i = 0; i < 4; i++) {
			result.setX(i, y(i));
			result.setY(i, -x(i));
			
		}
		
		return result;
	}
	
	public Shape rotateRight() {
		if(pieceShape == Tetrominos.SquareShape) {
			return this;
		}
		
		Shape result = new Shape();
		result.pieceShape = pieceShape;
		
		for(int i = 0; i < 4; i++) {
			result.setX(i, -y(i));
			result.setY(i, x(i));
		}
		
		return result;
	}
	
}
