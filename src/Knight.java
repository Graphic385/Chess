public class Knight extends Piece {

	public Knight(boolean isWhite) {
		super(isWhite);
	}

	@Override
	public boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering) {
		int dx = Math.abs(toX - fromX);
		int dy = Math.abs(toY - fromY);

		// L shaped move 2 steps in one direction 1 in the other
		if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
			return board[toX][toY] == null || board[toX][toY].isWhite() != isWhite();
		}
		return false;
	}
}
