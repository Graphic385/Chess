import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlayerTurnPanel extends JPanel {
	private boolean noTime = false;
	private boolean isPlayerWhite;
	private Timer playerTimer;
	private JLabel playerTurnLabel;
	private int playerTime;
	private int timeAddPerTurn;

	public PlayerTurnPanel(TimeSelection selectedTime, boolean isPlayerWhite, ChessGame game) {
		this.isPlayerWhite = isPlayerWhite;
		switch (selectedTime) {
			case OneMinute:
				playerTime = 60; // 1 minute
				timeAddPerTurn = 0; // No increment per turn
				break;
			case OneMinutePlusSecond:
				playerTime = 60; // 1 minute
				timeAddPerTurn = 1; // 1 second increment per move
				break;
			case TwoMinutePlusSecond:
				playerTime = 120; // 2 minutes each
				timeAddPerTurn = 1; // 1 second increment per move
				break;
			case ThreeMinute:
				playerTime = 180; // 3 minutes each
				timeAddPerTurn = 0; // No increment per turn
				break;
			case ThreeMinutePlusSecond:
				playerTime = 180; // 3 minutes each
				timeAddPerTurn = 1; // 1 second increment per move
				break;
			case FiveMinute:
				playerTime = 300; // 5 minutes each
				timeAddPerTurn = 0; // No increment per turn
				break;
			case TenMinute:
				playerTime = 600; // 10 minutes each
				timeAddPerTurn = 0; // No increment per turn
				break;
			case FifteenMinutePlusTenSecond:
				playerTime = 900; // 15 minutes each
				timeAddPerTurn = 10; // 10 seconds increment per move
				break;
			case ThirtyMinute:
				playerTime = 1800; // 30 minutes each
				timeAddPerTurn = 0; // No increment per turn
				break;
			case NoLimit:
				noTime = true;
				break;
			default:
				throw new UnsupportedOperationException("Error: No time mode selected");
		}
		setLayout(new GridLayout(1, 1));
		if (!noTime) {
			playerTurnLabel = new JLabel((isPlayerWhite ? "White" : "Black") + " Time: " + formatTime(playerTime),
					SwingConstants.CENTER);
		} else {
			playerTurnLabel = new JLabel((isPlayerWhite ? "White" : "Black") + " Turn ", SwingConstants.CENTER);
		}
		playerTurnLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		playerTurnLabel.setOpaque(true);
		if (isPlayerWhite) {
			playerTurnLabel.setBackground(Color.GREEN);
		} else {
			playerTurnLabel.setBackground(Color.RED);
		}
		add(playerTurnLabel);

		if (!noTime) {
			playerTimer = new javax.swing.Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (playerTime > 0) {
						playerTime--;
						playerTurnLabel
								.setText((isPlayerWhite ? "White" : "Black") + " Time: " + formatTime(playerTime));
					} else {
						game.gameOver(!isPlayerWhite);
						playerTimer.stop();
					}
				}
			});
		}
	}

	public void playerTurn() {
		playerTurnLabel.setBackground(Color.GREEN);
		if (!noTime) {
			playerTimer.restart();
		}
	}

	public void notPlayerTurn() {
		playerTurnLabel.setBackground(Color.RED);
		if (!noTime) {
			playerTime += timeAddPerTurn;
			playerTimer.stop();
			playerTurnLabel.setText((isPlayerWhite ? "White" : "Black") + " Time: " + formatTime(playerTime));
		}
	}

	private String formatTime(int timeInSeconds) {
		int minutes = timeInSeconds / 60;
		int seconds = timeInSeconds % 60;
		return minutes + ":" + new DecimalFormat("00").format(seconds);
	}

	public boolean isPlayerWhite() {
		return isPlayerWhite;
	}
}