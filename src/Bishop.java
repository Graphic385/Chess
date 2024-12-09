public class Bishop extends Piece {
	public Bishop(boolean isWhite) {
		super(isWhite);
	}

	@Override
	public Bishop copy() {
		Bishop bishop = new Bishop(this.isWhite());
		return bishop; 
	}
	@Override
	public boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering) {
		int dx = Math.abs(toX - fromX);
		int dy = Math.abs(toY - fromY);

		//not diagonal
		if (dx != dy) {
			return false;
		}

		int stepX = (toX > fromX) ? 1 : -1;
		int stepY = (toY > fromY) ? 1 : -1;

		// Check for obstacles in the path (excluding the destination square)
		for (int i = 1; i < dx; i++) { // Start at 1 to skip the `from` square
			int checkX = fromX + i * stepX;
			int checkY = fromY + i * stepY;
			if (board[checkX][checkY] != null) { // If any square is occupied
				return false;
			}
		}

		// Destination square must be either empty or occupied by an opponent's piece
		return (board[toX][toY] == null) || (board[toX][toY].isWhite() != isWhite());
	}
}