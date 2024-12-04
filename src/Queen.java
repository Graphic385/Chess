public class Queen extends Piece {

	public Queen(boolean isWhite) {
		super(isWhite);
	}

	@Override
	public boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering) {
		int dx = Math.abs(toX - fromX);
		int dy = Math.abs(toY - fromY);

		// moving like rook
		if (fromX == toX || fromY == toY) {
			return new Rook(isWhite()).isValidMove(fromX, fromY, toX, toY, board, forRendering);
		}
		// moving like bishop
		if (dx == dy) {
			return new Bishop(isWhite()).isValidMove(fromX, fromY, toX, toY, board, forRendering);
		}
		return false;
	}
}
