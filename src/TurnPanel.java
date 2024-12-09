import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TurnPanel extends JPanel {
	private JLabel whiteTurnLabel;
	private JLabel blackTurnLabel;

	public TurnPanel() {
		setLayout(new GridLayout(1, 2));
		whiteTurnLabel = new JLabel("White's Turn", SwingConstants.CENTER);
		whiteTurnLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		whiteTurnLabel.setOpaque(true);
		whiteTurnLabel.setBackground(Color.GREEN);
		blackTurnLabel = new JLabel("Black's Turn", SwingConstants.CENTER);
		blackTurnLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		blackTurnLabel.setOpaque(true);
		blackTurnLabel.setBackground(Color.RED);
		add(whiteTurnLabel);
		add(blackTurnLabel);
	}

	public void newMove(boolean isWhiteTurn) {
		if (isWhiteTurn) {
			whiteTurnLabel.setBackground(Color.GREEN);
			blackTurnLabel.setBackground(Color.RED);
		} else {
			whiteTurnLabel.setBackground(Color.RED);
			blackTurnLabel.setBackground(Color.GREEN);
		}
	}
}