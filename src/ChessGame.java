import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChessGame {
	private Board board;
	private ChessPanel panel;
	private Piece selectedPiece = null;
	private TimeSelection timeSelection = null;
	private boolean isWhiteTurn = true;
	private JFrame frame;
	private JPanel settingsPanel;
	private JPanel blackPlayer;
	private JPanel whitePlayer;

	public void startGame() {
		board = new Board(this);
		board.initialize();
		createGUI();
	}

	public void restartGame() {
		isWhiteTurn = true;
		selectedPiece = null;
		timeSelection = null;
		board = new Board(this);
		board.initialize();
		frame.getContentPane().removeAll(); // Clear the frame
		frame.revalidate(); // Refresh the frame's layout
		frame.repaint();
		timeSelectionGUI();
	}

	public void handleClick(int mouseX, int mouseY) {
		int cellX = mouseX / (panel.getWidth() / 8);
		int cellY = mouseY / (panel.getWidth() / 8);
		if (!board.isInBounds(cellX, cellY)) {
			return;
		}

		Piece pieceOnCellMove = board.getPieceAt(cellX, cellY);
		if (selectedPiece != null) {
			int selectedPieceX = board.getPieceX(selectedPiece);
			int selectedPieceY = board.getPieceY(selectedPiece);

			if (board.isValidMove(selectedPieceX, selectedPieceY, cellX, cellY, board.getGrid(), false, isWhiteTurn)) {
				if (!(selectedPiece instanceof Pawn && ((Pawn) selectedPiece).didPromoteInThisTurn())) {
					board.movePiece(selectedPieceX, selectedPieceY, cellX, cellY, board.getGrid());
				}
				panel.drawBoard(panel.getGraphics());
				isWhiteTurn = !isWhiteTurn;
				updateTurnPanels();
				resetEnPassantVulnerability(selectedPiece);

				if (isInCheckmate(board.getGrid(), isWhiteTurn)) {
					gameOver(isWhiteTurn);
				}

				selectedPiece = null;
			} else if (pieceOnCellMove != null && selectedPiece.isWhite() == pieceOnCellMove.isWhite()) {
				selectedPiece = pieceOnCellMove;
				panel.drawBoard(panel.getGraphics());
				renderPossibleMoves(selectedPiece);
			} else {
				selectedPiece = null;
			}
		} else {
			selectedPiece = pieceOnCellMove;
			panel.drawBoard(panel.getGraphics());
			if (selectedPiece != null) {
				renderPossibleMoves(selectedPiece);
			}
		}

		if (isInCheck(board.getGrid(), isWhiteTurn, board.findKing(board.getGrid(), isWhiteTurn))) {
			panel.drawBoard(panel.getGraphics());
		}
	}

	private void updateTurnPanels() {
		if (isWhiteTurn) {
			((PlayerTurnPanel) whitePlayer).playerTurn();
			((PlayerTurnPanel) blackPlayer).notPlayerTurn();
		} else {
			((PlayerTurnPanel) blackPlayer).playerTurn();
			((PlayerTurnPanel) whitePlayer).notPlayerTurn();
		}
	}

	private void resetEnPassantVulnerability(Piece peiceThatMoved) {
		boolean pawnMoved = (peiceThatMoved instanceof Pawn) ? true : false;
		for (Piece[] row : board.getGrid()) {
			for (Piece piece : row) {
				if (piece instanceof Pawn) {
					if (!(pawnMoved && piece.equals(peiceThatMoved))) {
						((Pawn) piece).resetEnPassantVulnerability();
					}
				}
			}
		}
	}

	public boolean isInCheck(Piece[][] grid, boolean whiteCheck, int[] kingLocation) {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				if (grid[x][y] == null || grid[x][y].isWhite() == whiteCheck) {
					continue;
				}
				if (grid[x][y].isValidMove(x, y, kingLocation[0], kingLocation[1], grid, true)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isInCheckmate(Piece[][] grid, boolean whiteCheck) {
		if (!isInCheck(grid, whiteCheck, board.findKing(grid, whiteCheck))) {
			return false;
		}

		for (Piece[] row : grid) {
			for (Piece piece : row) {
				if (piece == null || piece.isWhite() != whiteCheck) {
					continue;
				}
				for (int x = 0; x < grid.length; x++) {
					for (int y = 0; y < grid[x].length; y++) {
						Piece[][] copyOfGrid = board.getCopyOfGrid();
						if (piece.isValidMove(board.getPieceX(piece), board.getPieceY(piece), x, y, copyOfGrid,
								false)) {
							board.movePiece(board.getPieceX(piece), board.getPieceY(piece), x, y, copyOfGrid);
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

	private void renderPossibleMoves(Piece piece) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board.isValidMove(board.getPieceX(piece), board.getPieceY(piece), x, y, board.getGrid(), true,
						isWhiteTurn)) {
					panel.drawPossibleMove(x, y);
				}
			}
		}
	}

	private void createGUI() {
		frame = new JFrame("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		timeSelectionGUI();
	}

	private void timeSelectionGUI() {
		frame.setLayout(new BorderLayout());
		frame.setMinimumSize(new Dimension(380, 620));
		frame.setSize(380, 620);
		settingsPanel = new SettingsPanel(this);
		frame.add(settingsPanel);
		frame.setVisible(true);
	}

	protected void gamemodeSelected() {
		timeSelection = ((SettingsPanel) settingsPanel).getSelectedTime();
		frame.getContentPane().removeAll(); // Clear the frame
		gameGUI();
		frame.revalidate();
	}

	private void gameGUI() {
		frame.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Chess panel (center)
		panel = new ChessPanel(board, this);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1.0;
		frame.add(panel, gbc);

		// Black Panel
		blackPlayer = new PlayerTurnPanel(timeSelection, false, this);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		frame.add(blackPlayer, gbc);

		// White Panel
		whitePlayer = new PlayerTurnPanel(timeSelection, true, this);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		frame.add(whitePlayer, gbc);

		frame.setResizable(false);
		frame.setMinimumSize(new Dimension(600, 670));
		frame.pack();
		frame.setVisible(true);
	}

	public boolean getIsWhiteTurn() {
		return isWhiteTurn;
	}

	public void gameOver(boolean whiteWon) {
		if (JOptionPane.showConfirmDialog(null, "Do you want to play again?",
				(whiteWon ? "White" : "Black") + " won!!!",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			restartGame();
		} else {
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}
}
