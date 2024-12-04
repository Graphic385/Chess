public class Pawn extends Piece {
	private boolean hasMoved;
	private boolean vulnerableToEnPassant;
	private boolean didPromoteInThisTurn;

	public Pawn(boolean isWhite) {
		super(isWhite);
		hasMoved = false;
		vulnerableToEnPassant = false;
		didPromoteInThisTurn = false;
	}

	@Override
	public boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering) {
		int direction;
		direction = (isWhite()) ? 1 : -1;

		// Normal forward move
		if (fromY == toY + direction && fromX == toX && board[toX][toY] == null) {
			if (!forRendering) {
				hasMoved = true;
				vulnerableToEnPassant = false;
			}

			// for promotion to queen if at end of board
			if ((isWhite() && toY == 0 || !isWhite() && toY == 7) && !forRendering && board[toX][toY]
					.isWhite() != isWhite()) {
				board[fromX][fromY] = null;
				// TODO make popup with options for piece
				board[toX][toY] = new Queen(isWhite()); // Promote to a queen
				didPromoteInThisTurn = true;
			}
			return true;
		}

		// double on first move
		if (!hasMoved && fromX == toX && fromY == toY + (direction * 2) && board[toX][toY] == null
				&& board[fromX][toY + direction] == null) {
			if (!forRendering) {
				hasMoved = true;
				vulnerableToEnPassant = true;
			}
			return true;
		}

		// capture move
		if (fromY == toY + direction && Math.abs(toX - fromX) == 1 && board[toX][toY] != null) {
			if (!forRendering) {
				hasMoved = true;
				vulnerableToEnPassant = false;
			}
			// for promotion to queen if at end of board
			if ((isWhite() && toY == 0 || !isWhite() && toY == 7) && !forRendering && board[toX][toY]
					.isWhite() != isWhite()) {
				board[fromX][fromY] = null;
				// TODO make popup with options for piece
				board[toX][toY] = new Queen(isWhite()); // Promote to a queen
				didPromoteInThisTurn = true;
				return true;
			}
			return board[toX][toY].isWhite() != isWhite();
		}

		if (fromY == toY + direction && Math.abs(toX - fromX) == 1 && board[toX][toY] == null) {
			Piece adjacentPawn = board[toX][fromY]; // Pawn to be captured en passant

			if (adjacentPawn instanceof Pawn && ((Pawn) adjacentPawn).isVulnerableToEnPassant()) {
				if (!forRendering) {
					board[toX][fromY] = null;
					hasMoved = true;
					vulnerableToEnPassant = false;
				}
				return true;
			}
		}
		return false;
	}

	public boolean didPromoteInThisTurn() {
		return didPromoteInThisTurn;
	}

	public void resetDidPromateInThisTurn() {
		didPromoteInThisTurn = false;
	}

	public boolean isVulnerableToEnPassant() {
		return vulnerableToEnPassant;
	}

	public void resetEnPassantVulnerability() {
		vulnerableToEnPassant = false;
	}
}
