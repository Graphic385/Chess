public class Rook extends Piece {
	protected boolean hasMoved;

	public Rook(boolean isWhite) {
		super(isWhite);
		hasMoved = false;
	}

	@Override
	public Rook copy() {
		Rook rook = new Rook(this.isWhite());
		rook.hasMoved = this.hasMoved;
		return rook;
	}
	@Override
	public boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering) {
		//must move in straight line
		if (fromX != toX && fromY != toY) {
			return false;
		}
		// vertical move
		if (fromX == toX) {
			for (int y = Math.min(fromY, toY) + 1;	y < Math.max(fromY, toY); y++){
				if (board[fromX][y] != null) {
					return false;
				}
			}
		} else { //horizontal move
			for (int x = Math.min(fromX, toX) + 1; x < Math.max(fromX, toX); x++) {
				if (board[x][fromY] != null) {
					return false;
				}
			}
		}

		if (board[toX][toY] == null || board[toX][toY].isWhite() != isWhite()) {
			if (!forRendering) {
				hasMoved = true; // Mark rook as having moved
			}
			return true;
		}
		
		return false;
	}

	public boolean hasMoved() {
		return hasMoved;
	}
}