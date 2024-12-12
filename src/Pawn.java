import javax.swing.JOptionPane;

public class Pawn extends Piece {
	protected boolean hasMoved;
	protected boolean vulnerableToEnPassant;
	protected boolean didPromoteInThisTurn;

	public Pawn(boolean isWhite) {
		super(isWhite);
		hasMoved = false;
		vulnerableToEnPassant = false;
		didPromoteInThisTurn = false;
	}

	@Override
	public Pawn copy() {
		Pawn pawn = new Pawn(this.isWhite());
		pawn.hasMoved = this.hasMoved;
		pawn.vulnerableToEnPassant = this.vulnerableToEnPassant;
		pawn.didPromoteInThisTurn = this.didPromoteInThisTurn;
		return pawn;
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
			if ((isWhite() && toY == 0 || !isWhite() && toY == 7) && !forRendering) {
				board[fromX][fromY] = null;
				promotionPopUp(toX, toY, board);
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
				promotionPopUp(toX, toY, board);
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

	private void promotionPopUp(int toX, int toY, Piece[][] board) {
		// Custom text for buttons
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};

        // Show a dialog with 4 buttons
        int choice = JOptionPane.showOptionDialog(
            null, // Parent component (null for default)
            "Choose one of the options below to promote to:", // Message
            "Promotion Selection", // Title
            JOptionPane.DEFAULT_OPTION, // Option type
            JOptionPane.INFORMATION_MESSAGE, // Message type
            null, // Icon (null for default)
            options, // Options to display
            options[0] // Default option
        );

        // Output the user's choice
		if (choice == 0) {
			board[toX][toY] = new Queen(isWhite());
		} else if (choice == 1) {
			board[toX][toY] = new Rook(isWhite());
		} else if (choice == 2) {
			board[toX][toY] = new Bishop(isWhite());
		} else if (choice == 3) {
			board[toX][toY] = new Knight(isWhite());
		}
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
