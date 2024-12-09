public class King extends Piece {
	protected boolean hasMoved;

	public King(boolean isWhite) {
		super(isWhite);
		hasMoved = false;
	}

	@Override
	public King copy() {
		King king = new King(this.isWhite());
		king.hasMoved = this.hasMoved;
		return king;
	}
	@Override
	public boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering) {
		int dx = Math.abs(toX - fromX);
		int dy = Math.abs(toY - fromY);

		// Single-step move in any direction
		if ((dx <= 1 && dy <= 1) && (board[toX][toY] == null || board[toX][toY].isWhite() != isWhite())) {
			if (!forRendering) {
				hasMoved = true;
			}
			return true;
		}

		// Castling move
		if (!hasMoved && dy == 0 && (dx == 2)) {
			int rookX = (toX > fromX) ? 7 : 0; // Rook on the right (king-side) or left (queen-side)
			Piece rook = board[rookX][fromY];

			if (rook instanceof Rook && !((Rook) rook).hasMoved()) {
				int step = (toX > fromX) ? 1 : -1; // Direction of movement
				for (int x = fromX + step; x != rookX; x += step) {
					if (board[x][fromY] != null) { // Path between king and rook must be clear
						return false;
					}
				}

				// Castling is valid; move the rook
				if (!forRendering) {
					board[fromX + step][fromY] = rook; // Place rook next to king
					board[rookX][fromY] = null; // Remove rook from its original position
					hasMoved = true;
				}
				return true;
			}
		}

		return false;
	}

	public boolean hasMoved() {
		return hasMoved;
	}
}
