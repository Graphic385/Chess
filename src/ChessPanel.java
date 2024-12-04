import java.awt.Color;
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

	public ChessPanel(Board board, ChessGame game) {
		this.board = board;
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
		g.drawOval((int)(cellX * cellSize + cellSize * 0.1), (int)(cellY * cellSize + cellSize * 0.1), (int)(cellSize * 0.85), (int)(cellSize * 0.85));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBoard(g);
	}

	protected void drawBoard(Graphics g) {
		int cellSize = getWidth() / 8;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if ((x + y) % 2 == 0) {
					g.setColor(new Color(241, 217, 182)); // light brown
				} else {
					g.setColor(new Color(181, 137, 99)); // dark brown
				}
				g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
				Piece piece = board.getPieceAt(x, y);
				if (piece != null) {
					drawPiece(g, piece, x * cellSize, y * cellSize, cellSize);
				}
			}
		}
	}

	private void drawPiece(Graphics g, Piece piece, int x, int y, int cellSize) {
		try {
			String imagePath = "images/" + piece.getClass().getSimpleName().toLowerCase()
					+ (piece.isWhite() ? "White" : "Black") + ".png";

			Image img = ImageIO.read(new File(imagePath));

			img = img.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
			g.drawImage(img, x, y, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
