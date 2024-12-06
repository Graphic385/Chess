import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ChessGame {
	private Board board;
	private ChessPanel panel;
	private Piece slectedPiece = null;
	private TimeSelection timeSelection;
	private boolean isWhiteTurn = true;
	protected int boardSize = 800;
	private JFrame frame;

	public void startGame() {
		board = new Board();
		board.initialize();
		createGUI();
	}

	public void handleClick(int mouseX, int mouseY) {
		int cellX = mouseX / (panel.getWidth() / 8);
		int cellY = mouseY / (panel.getWidth() / 8);
		Piece pieceOnCellMove = board.getPieceAt(cellX, cellY);
		int slectedPieceX = board.getPieceX(slectedPiece);
		int slectedPieceY = board.getPieceY(slectedPiece);
		if (slectedPiece != null) {
			if (board.isValidMove(board.getPieceX(slectedPiece), board.getPieceY(slectedPiece), cellX, cellY,
					board.getGrid(), false, isWhiteTurn)) { // TODO change last parameter to isWhiteTurn
				if (!(slectedPiece instanceof Pawn && ((Pawn) slectedPiece).didPromoteInThisTurn())) {
					board.movePiece(slectedPieceX, slectedPieceY, cellX, cellY, board.getGrid());
				}
				panel.drawBoard(panel.getGraphics());
				isWhiteTurn = !isWhiteTurn;
				if (isInCheckmate(board.getGrid(), isWhiteTurn)) {
					checkMateAction(isWhiteTurn);
				} else {
					System.out.println("not checkmate");
				}

				// After a pawn moves in the game:
				for (Piece[] row : board.getGrid()) {
					for (Piece piece : row) {
						if (piece instanceof Pawn) {
							((Pawn) piece).resetEnPassantVulnerability();
						}
					}
				}
				slectedPiece = null;
			} else if (pieceOnCellMove != null
					&& slectedPiece.isWhite() == pieceOnCellMove.isWhite()) {
				slectedPiece = pieceOnCellMove;
				panel.drawBoard(panel.getGraphics());

				renderPossibleMoves(slectedPiece);
			} else {
				slectedPiece = null;
			}
		} else {
			slectedPiece = pieceOnCellMove;
			panel.drawBoard(panel.getGraphics());
			if (slectedPiece != null) {
				renderPossibleMoves(slectedPiece);
			}
		}
	}

	public boolean isInCheck(Piece[][] grid, boolean whiteCheck, int[] kingLocation) {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				if (grid[x][y] == null || grid[x][y].isWhite() == whiteCheck) {
					continue;
				}
				if (grid[x][y].isValidMove(x, y, kingLocation[0],
						kingLocation[1], grid, true)) { // problem here with logic
					return true;
				}
			}
		}
		return false;
	}

	public int numOfPiecesCausingCheck(Piece[][] grid, boolean whiteCheck, int[] kingLocation) {
		int numPiecesCausingCheck = 0;
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				if (grid[x] == null || grid[x][y] == null || grid[x][y].isWhite() == whiteCheck) {
					continue;
				}
				if (grid[x][y].isValidMove(board.getPieceX(grid[x][y]), board.getPieceY(grid[x][y]), kingLocation[0],
						kingLocation[1], grid, true)) {
					numPiecesCausingCheck++;
				}
			}
		}
		return numPiecesCausingCheck;
	}

	public Piece pieceCasuingCheck(Piece[][] grid, boolean whiteCheck, int[] kingLocation) {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				if (grid[x] == null || grid[x][y] == null || grid[x][y].isWhite() == whiteCheck) {
					continue;
				}
				if (grid[x][y].isValidMove(board.getPieceX(grid[x][y]), board.getPieceY(grid[x][y]), kingLocation[0],
						kingLocation[1], grid, true)) {
					return grid[x][y];
				}
			}
		}
		return null;
	}

	public boolean isInCheckmate(Piece[][] grid, boolean whiteCheck) {
		Piece[][] copyOfGrid = new Piece[grid.length][]; // TODO maybe remove later if not used
		int[] kingLocation = board.findKing(grid, whiteCheck);
		int kingX = kingLocation[0];
		int kingY = kingLocation[1];
		Piece pieceCasuingCheck = pieceCasuingCheck(copyOfGrid, whiteCheck, kingLocation);
		int numPiecesCausingCheck = numOfPiecesCausingCheck(copyOfGrid, whiteCheck, kingLocation);
		// if not in check then not in checkmate
		if (!isInCheck(grid, whiteCheck, kingLocation)) {
			return false;
		}

		// if king can move to get out of check
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (x == 0 && y == 0) { // x 0 and y 0 means original king location
					continue;
				}
				if (kingX + x < 8 || kingX + x > 0 || kingY + y < 8 || kingY + y > 0) { // within the board dimensions
					continue;
				}
				if (!isInCheck(grid, whiteCheck, new int[] { kingX + x, kingY + y })) { // if it moved and is not in
																						// check anymore
					return false;
				}
			}
		}

		// seeing if checked player can eat the piece causing check and get out of check
		if (numPiecesCausingCheck > 1) { // only works if 1 piece is causing check
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid.length; y++) {
					if (grid[x][y] == null || grid[x][y].isWhite() != whiteCheck) {
						continue;
					}
					if (grid[x][y].isValidMove(board.getPieceX(grid[x][y]), board.getPieceY(grid[x][y]),
							board.getPieceX(pieceCasuingCheck), board.getPieceY(pieceCasuingCheck), grid, true)) {
						return false;
					}
				}
			}
		}

		// check every possible move and see if no longer in checkmate
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				if (grid[x][y] == null || grid[x][y].isWhite() != whiteCheck) {
					continue;
				}
				for (int xPosibblesMoves = 0; xPosibblesMoves < grid.length; xPosibblesMoves++) {
					for (int yPossibleMoves = 0; yPossibleMoves < grid.length; yPossibleMoves++) {
						copyOfGrid = board.getCopyOfGrid();
						if (copyOfGrid[x][y].isValidMove(x, y, xPosibblesMoves, yPossibleMoves, copyOfGrid, false)) {
							if (!(copyOfGrid[x][y] instanceof Pawn && ((Pawn) grid[x][y]).didPromoteInThisTurn())) {
								board.movePiece(x, y, xPosibblesMoves, yPossibleMoves, copyOfGrid);
							}
							if (!isInCheck(copyOfGrid, whiteCheck, board.findKing(copyOfGrid, whiteCheck))) {
								return false;
							}
						}
					}
				}
			}
		}

		return true;
	}

	private void checkMateAction(Boolean whiteWon) {
		if (JOptionPane.showConfirmDialog(null, "Do you want to play again?",
				(whiteWon ? "Black" : "White") + " won!!!",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			startGame();
		} else {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}

	private void renderPossibleMoves(Piece piece) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board.isValidMove(board.getPieceX(piece),
						board.getPieceY(piece), x, y, board.getGrid(), true, isWhiteTurn)) { // TODO change last
																								// parameter to
					// isWhiteTurn
					panel.drawPossibleMove(x, y);
				}
			}
		}
	}

	private void createGUI() {
		frame = new JFrame("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(boardSize, boardSize);

		JPanel settingsPanel = new SettingsPanel(this);
		frame.add(settingsPanel);
		frame.setVisible(true);
	}

	protected void gamemodeSelected() {
		frame.getContentPane().removeAll(); // Clear the frame
		gameGUI();
		frame.revalidate(); // Refresh the frame's layout
	}

	private void gameGUI() {
		frame.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Chess panel (center)
		panel = new ChessPanel(board, this);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 2; // Span vertically
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.8; // More space allocated
		gbc.weighty = 1.0;
		frame.add(panel, gbc);

		// Turn indicator panel (top)
		JPanel turnPanel = new JPanel();
		turnPanel.setLayout(new GridLayout(1, 2));
		JLabel whiteTurnLabel = new JLabel("White's Turn", SwingConstants.CENTER);
		JLabel blackTurnLabel = new JLabel("Black's Turn", SwingConstants.CENTER);
		turnPanel.add(whiteTurnLabel);
		turnPanel.add(blackTurnLabel);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1; // Only one row
		gbc.weightx = 0.8;
		gbc.weighty = 0.1;
		frame.add(turnPanel, gbc);

		// Timer panel (right)
		JPanel timerPanel = new JPanel();
		timerPanel.setLayout(new GridLayout(2, 1));
		JLabel whiteTimer = new JLabel("White: 10:00", SwingConstants.CENTER);
		JLabel blackTimer = new JLabel("Black: 10:00", SwingConstants.CENTER);
		timerPanel.add(whiteTimer);
		timerPanel.add(blackTimer);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 2; // Span vertically
		gbc.weightx = 0.2; // Less space
		gbc.weighty = 1.0;
		frame.add(timerPanel, gbc);
		frame.setVisible(true);
	}
}