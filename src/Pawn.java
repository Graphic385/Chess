import javax.swing.JOptionPane;

public class Pawn extends Piece {
	protected boolean hasMoved;
	protected boolean vulnerableToEnPassant;
	protected boolean didPromoteInThisTurn;
	protected PieceToPromote pieceToPromote;

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
				board[fromX][fromY] = null;
				if (this.pieceToPromote == null) {
					promotionPopUp(toX, toY, board);
				} else {
					doPromotion(toX, toY, board, this.pieceToPromote);
				}
				didPromoteInThisTurn = true;
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
				if (this.pieceToPromote == null) {
					promotionPopUp(toX, toY, board);
				} else {
					doPromotion(toX, toY, board, this.pieceToPromote);
				}
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

	private void promotionPopUp(int toX, int toY, Piece[][] board) { //TODO makes popup for checkamte senarios
		PieceToPromote pieceToPromote = PieceToPromote.Queen;
		// Custom text for buttons
		String[] options = { "Queen", "Rook", "Bishop", "Knight" };
		int choice = -1; 

		// Keep showing the dialog until a valid choice is made
		while (choice == -1) {
				choice = JOptionPane.showOptionDialog(
					null, // Parent component (null for default)
					"Choose one of the options below to promote to:", // Message
					"Promotion Selection", // Title
					JOptionPane.DEFAULT_OPTION, // Option type
					JOptionPane.INFORMATION_MESSAGE, // Message type
					null, // Icon (null for default)
					options, // Options to display
					options[0] // Default option
			);
			if (choice == -1) {
				JOptionPane.showMessageDialog(null, "Please choose a piece", "Try Again", JOptionPane.ERROR_MESSAGE);
			}
		}
		// Set the promoted piece based on the user's choice
		if (choice == 0) {
			pieceToPromote = PieceToPromote.Queen;
		} else if (choice == 1) {
			pieceToPromote = PieceToPromote.Rook;
		} else if (choice == 2) {
			pieceToPromote = PieceToPromote.Bishop;
		} else if (choice == 3) {
			pieceToPromote = PieceToPromote.Knight;
		}
		doPromotion(toX, toY, board, pieceToPromote);
	}

	private void doPromotion(int toX, int toY, Piece[][] board, PieceToPromote pieceToPromote) {
		// Set the promoted piece based on the user's choice
		if (pieceToPromote == PieceToPromote.Queen) {
			board[toX][toY] = new Queen(isWhite());
		} else if (pieceToPromote == PieceToPromote.Rook) {
			board[toX][toY] = new Rook(isWhite());
		} else if (pieceToPromote == PieceToPromote.Bishop) {
			board[toX][toY] = new Bishop(isWhite());
		} else if (pieceToPromote == PieceToPromote.Knight) {
			board[toX][toY] = new Knight(isWhite());
		}
	}

	public void setPieceToPromote(PieceToPromote pieceToPromote) {
		if (pieceToPromote == PieceToPromote.Queen) {
			this.pieceToPromote = PieceToPromote.Queen;
		} else if (pieceToPromote == PieceToPromote.Rook) {
			this.pieceToPromote = PieceToPromote.Rook;
		} else if (pieceToPromote == PieceToPromote.Bishop) {
			this.pieceToPromote = PieceToPromote.Bishop;
		} else if (pieceToPromote == PieceToPromote.Knight) {
			this.pieceToPromote = PieceToPromote.Knight;
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
