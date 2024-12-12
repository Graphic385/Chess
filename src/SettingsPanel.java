import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SettingsPanel extends JPanel {
	private JRadioButton[] timeButtons;
	private JButton confirmButton;
	private TimeSelection selectedTime;

	private final int cellSize = 73; // Size of each square on the chessboard
	private final int boardSize = 8; // 8x8 chessboard

	public SettingsPanel(ChessGame game) {
		setLayout(new GridBagLayout()); // Main layout
		setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding around the panel
		setOpaque(false); // Allow custom painting

		GridBagConstraints gbc = new GridBagConstraints();

		// Title label
		JLabel titleLabel = new JLabel("Chess Game Settings", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Serif", Font.BOLD, 34));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2; // Span across both columns
		gbc.insets = new Insets(10, 0, 20, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(titleLabel, gbc);

		// Time selection buttons
		JPanel radioButtonPanel = new JPanel(new GridBagLayout());
		radioButtonPanel.setOpaque(false); // Transparent panel
		timeButtons = new JRadioButton[TimeSelection.values().length];
		ButtonGroup buttonGroup = new ButtonGroup();

		GridBagConstraints rbGbc = new GridBagConstraints();
		rbGbc.gridx = 0;
		rbGbc.gridy = 0;
		rbGbc.anchor = GridBagConstraints.WEST;
		rbGbc.insets = new Insets(5, 10, 5, 10);

		for (TimeSelection timeSelection : TimeSelection.values()) {
			JRadioButton button = new JRadioButton(timeSelection.name().replace("_", " "));
			button.setFont(new Font("Serif", Font.PLAIN, 16));
			button.setOpaque(false);
			buttonGroup.add(button);
			timeButtons[rbGbc.gridy] = button;
			radioButtonPanel.add(button, rbGbc);
			rbGbc.gridy++;
			if (timeSelection == TimeSelection.NoLimit) {
				button.setSelected(true);
			}
		}

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 0, 20, 20);
		add(radioButtonPanel, gbc);

		// Confirm button spanning across the bottom
		confirmButton = new JButton("Confirm");
		confirmButton.setFont(new Font("Serif", Font.BOLD, 26));
		confirmButton.setForeground(Color.WHITE);
		confirmButton.setBackground(new Color(80, 50, 20)); // Dark brown button
		confirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2; // Span across both columns
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(confirmButton, gbc);

		// Event listener for the confirm button
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (JRadioButton button : timeButtons) {
					if (button.isSelected()) {
						selectedTime = TimeSelection.valueOf(button.getText().replace(" ", "_"));
						game.gamemodeSelected();
						break;
					}
				}
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Draw chessboard background spanning the entire panel
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				boolean isLightSquare = (row + col) % 2 == 0;
				g.setColor(isLightSquare ? new Color(240, 217, 181) : new Color(181, 136, 99));
				g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
			}
		}

		// Overlay chess pieces
		drawPieces(g);
	}

	private void drawPieces(Graphics g) {
		// Example positions for a few pieces
		String[][] pieces = {
				{ "pawnWhite", "5", "4" },
				{ "knightBlack", "6", "2" },
				{ "bishopBlack", "2", "0" },
				{ "rookWhite", "2", "4" },
				{ "queenWhite", "4", "2" },
				{ "kingBlack", "6", "4" }
		};

		for (String[] piece : pieces) {
			String pieceName = piece[0]; // Name of the piece (e.g., pawnWhite)
			int row = Integer.parseInt(piece[1]); // Row on the chessboard
			int col = Integer.parseInt(piece[2]); // Column on the chessboard

			try {
				String imagePath = "images/" + pieceName + ".png"; // Construct image path
				Image img = ImageIO.read(new File(imagePath));
				img = img.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
				int x = col * cellSize;
				int y = row * cellSize;
				g.drawImage(img, x, y, null);
			} catch (IOException e) {
				System.err.println("Error loading image: " + pieceName);
				e.printStackTrace();
			}
		}
	}

	public TimeSelection getSelectedTime() {
		return selectedTime;
	}
}
