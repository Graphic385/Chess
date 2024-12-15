import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ChessPanel extends JPanel {
	private Board board;
	private String pieceStyle;

	public ChessPanel(Board board, ChessGame game) {
		this.board = board;
		// pieceStyle = game.getPieceStyle(); //TODO make word
		pieceStyle = "cburnett"; // TODO make word


		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				game.handleClick(e.getX(), e.getY());
			}
		});
	}

	protected void drawPossibleMove(int cellX, int cellY) {
		Graphics g = getGraphics();
		if (g == null)
			return;
		int cellSize = getWidth() / 8;
		g.drawOval((int) (cellX * cellSize + cellSize * 0.1), (int) (cellY * cellSize + cellSize * 0.1),
				(int) (cellSize * 0.85), (int) (cellSize * 0.85));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBoard(g);
	}

	protected void drawBoard(Graphics g) {
		int cellSize = getWidth() / 8;

		boolean isWhiteTurn = board.getGame().getIsWhiteTurn();
		int[] kingLocation = board.findKing(board.getGrid(), isWhiteTurn);
		boolean kingInCheck = board.getGame().isInCheck(board.getGrid(), isWhiteTurn, kingLocation);

		// Draw the board squares
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (x == kingLocation[0] && y == kingLocation[1] && kingInCheck) {
					g.setColor(new Color(255, 0, 0)); // Red background for the king in check
				} else if ((x + y) % 2 == 0) {
					g.setColor(new Color(241, 217, 182)); // Light brown
				} else {
					g.setColor(new Color(181, 137, 99)); // Dark brown
				}
				g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

				Piece piece = board.getPieceAt(x, y);
				if (piece != null) {
					drawPiece(g, piece, x * cellSize, y * cellSize, cellSize);
				}
			}
		}

		// Draw column letters (A-H) at the top of the board
		g.setFont(new Font("Dialog", Font.BOLD, (int) (cellSize * 0.2)));
		for (int x = 0; x < 8; x++) {
			if (x % 2 == 0) {
				g.setColor(new Color(241, 217, 182)); // light brown
			} else {
				g.setColor(new Color(181, 137, 99)); // dark brown
			}
			int letterX = x * cellSize + cellSize - (int) (cellSize * 0.18); // right side of each column
			int letterY = (int) (7 * cellSize + cellSize * 0.92); // Position at the bottom row
			g.drawString(String.valueOf((char) ('a' + x)), letterX, letterY);
		}

		// Draw row numbers (1-8) along the left side of the board
		for (int y = 0; y < 8; y++) {
			if (y % 2 == 0) {
				g.setColor(new Color(181, 137, 99)); // dark brown
			} else {
				g.setColor(new Color(241, 217, 182)); // light brown
			}
			int numberX = (int) (cellSize * 0.1);
			int numberY = y * cellSize + (int) (cellSize * 0.3);
			g.drawString(String.valueOf(8 - y), numberX, numberY);
		}
	}

	private void drawPiece(Graphics g, Piece piece, int x, int y, int cellSize) {
		try {
			String imagePath = "images/" + pieceStyle + "/" + (piece.isWhite() ? "w" : "b");
			if (piece instanceof King) {
				imagePath += "K";
			} else if (piece instanceof Knight) {
				imagePath += "N";
			} else if (piece instanceof Pawn) {
				imagePath += "P";
			} else if (piece instanceof Bishop) {
				imagePath += "B";
			} else if (piece instanceof Queen) {
				imagePath += "Q";
			} else if (piece instanceof Rook) {
				imagePath += "R";
			}
			imagePath += ".png";

			Image img = ImageIO.read(new File(imagePath));

			img = img.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
			g.drawImage(img, x, y, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
