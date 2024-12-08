public class Board {
	private Piece[][] grid = new Piece[8][8];

	public void initialize() {
		// place pawns
		for (int i = 0; i < 8; i++) {
			grid[i][1] = new Pawn(false);
			grid[i][6] = new Pawn(true);
		}

		// place black rooks
		grid[0][0] = new Rook(false);
		grid[7][0] = new Rook(false);
		// place white rooks
		grid[0][7] = new Rook(true);
		grid[7][7] = new Rook(true);

		// place black knights
		grid[1][0] = new Knight(false);
		grid[6][0] = new Knight(false);
		// place white knights
		grid[1][7] = new Knight(true);
		grid[6][7] = new Knight(true);

		// place black bishops
		grid[2][0] = new Bishop(false);
		grid[5][0] = new Bishop(false);
		// place white bishops
		grid[2][7] = new Bishop(true);
		grid[5][7] = new Bishop(true);

		// place black queen and king
		grid[3][0] = new Queen(false);
		grid[4][0] = new King(false);

		// place white queen and king
		grid[3][7] = new Queen(true);
		grid[4][7] = new King(true);
	}

	public boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering, boolean isWhiteTurn) {
		Piece piece = board[fromX][fromY];
		if (piece == null || piece.isWhite() != isWhiteTurn)
			return false;
		if (!isInBounds(toX, toY)) {
			return false;
		}
		return piece.isValidMove(fromX, fromY, toX, toY, grid, forRendering);
	}

	public void movePiece(int fromX, int fromY, int toX, int toY, Piece[][] board) {
		board[toX][toY] = board[fromX][fromY];
		board[fromX][fromY] = null;
	}

	public int getPieceX(Piece piece) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j] == piece) {
					return i;
				}
			}
		}
		return -1;
	}

	public int getPieceY(Piece piece) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j] == piece) {
					return j;
				}
			}
		}
		return -1;
	}

	public int[] findKing(Piece[][] board, boolean whiteKing) {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				if (board[x][y] instanceof King && board[x][y].isWhite() == whiteKing) {
					return  new int[] {x, y};
				}
			}
		}
		return new int[] {-1, -1};
	}
	
	public boolean isInBounds(int x, int y) {
		return (x >= 0 && x < 8) && (y >= 0 && y < 8);
	}

	public Piece getPieceAt(int x, int y) {
		return grid[x][y];
	}

	public void setPieceAt(int x, int y, Piece piece) {
		grid[x][y] = piece;
	}

	public Piece[][] getGrid() {
		return grid;
	}

	public Piece[][] getCopyOfGrid() { 
		Piece[][] copyOfGrid = new Piece[grid.length][];
		for (int i = 0; i < grid.length; i++) {
			copyOfGrid[i] = grid[i].clone();
		}
		return copyOfGrid;
	}
}