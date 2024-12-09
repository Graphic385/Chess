import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
public class ChessGame {
	private Board board;
	private ChessPanel panel;
	private Piece slectedPiece = null;
	private TimeSelection timeSelection = null;
	private boolean isWhiteTurn = true;
	protected int boardSize = 800;
	private JFrame frame;
	private JPanel settingsPanel;
	private JPanel timerPanel;
	private JPanel turnPanel;

	public void startGame() {
		board = new Board(this);
		board.initialize();
		createGUI();
	}

	public void restartGame() {
		isWhiteTurn = true;
		slectedPiece = null;
		timeSelection = null;
		board = new Board(this);
		board.initialize();
		frame.getContentPane().removeAll(); // Clear the frame
		frame.revalidate(); // Refresh the frame's layout
		frame.repaint(); //
		timeSelectionGUI();
	}

	public void handleClick(int mouseX, int mouseY) {
		int cellX = mouseX / (panel.getWidth() / 8);
		int cellY = mouseY / (panel.getWidth() / 8);
		if (!board.isInBounds(cellX, cellY)) {
			return;
		}
		Piece pieceOnCellMove = board.getPieceAt(cellX, cellY);
		int slectedPieceX = board.getPieceX(slectedPiece);
		int slectedPieceY = board.getPieceY(slectedPiece);

		if (slectedPiece != null) {
			if (board.isValidMove(board.getPieceX(slectedPiece), board.getPieceY(slectedPiece), cellX, cellY,
					board.getGrid(), false, isWhiteTurn)) {
				if (!(slectedPiece instanceof Pawn && ((Pawn) slectedPiece).didPromoteInThisTurn())) {
					board.movePiece(slectedPieceX, slectedPieceY, cellX, cellY, board.getGrid());
				}
				panel.drawBoard(panel.getGraphics());
				isWhiteTurn = !isWhiteTurn;
				((TimerPanel) timerPanel).newMove(isWhiteTurn);
				((TurnPanel) turnPanel).newMove(isWhiteTurn);
				if (isInCheckmate(board.getGrid(), isWhiteTurn)) {
					checkMateAction(isWhiteTurn);
				}

				// Reset en passant vulnerabilities after a move
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
		if (isInCheck(board.getGrid(), isWhiteTurn, board.findKing(board.getGrid(), isWhiteTurn))) {
			panel.drawBoard(panel.getGraphics());
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
		Piece[][] copyOfGrid = new Piece[grid.length][]; 
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
			restartGame();
		} else {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}

	private void renderPossibleMoves(Piece piece) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board.isValidMove(board.getPieceX(piece),
						board.getPieceY(piece), x, y, board.getGrid(), true, isWhiteTurn)) { 
					panel.drawPossibleMove(x, y);
				}
			}
		}
	}

	private void createGUI() {
		frame = new JFrame("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(boardSize, boardSize);
		frame.setVisible(isWhiteTurn);
		timeSelectionGUI();
	}

	private void timeSelectionGUI() {
		settingsPanel = new SettingsPanel(this);
		frame.add(settingsPanel);
		frame.setVisible(true);
	}

	protected void gamemodeSelected() {
		timeSelection = ((SettingsPanel) settingsPanel).getSelectedTime();
		frame.getContentPane().removeAll(); // Clear the frame
		gameGUI();
		frame.revalidate(); // Refresh the frame's layout
	}

	private void gameGUI() {
		frame.setLayout(new BorderLayout());
		

		// Chess panel (center)
		panel = new ChessPanel(board, this);
		frame.add(panel, BorderLayout.CENTER);

		// Turn indicator panel (top)
		turnPanel = new TurnPanel();
		frame.add(turnPanel, BorderLayout.NORTH);

		// Timer panel (right)
		if (!(timeSelection == TimeSelection.NoLimit)) {
			timerPanel = new TimerPanel(timeSelection);
			frame.add(timerPanel, BorderLayout.EAST);
		}
		
		frame.setMinimumSize(frame.getSize());
		frame.pack();
		frame.setVisible(true);
	}
	
	public boolean getIsWhiteTurn() {
		return isWhiteTurn;
	}
}