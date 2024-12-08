import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class TimerPanel extends JPanel {
	private JLabel blackTimerLabel;
	private JLabel whiteTimerLabel;
	private Timer blackTimer;
	private Timer whiteTimer;
	private int blackTime; 
	private int whiteTime; 
	private int timeAddPerTurn;

	public TimerPanel(TimeSelection selectedTime) {
		switch (selectedTime) {
			case OneMinute:
				blackTime = 60; // 1 minute for black
				whiteTime = 60; // 1 minute for white
				timeAddPerTurn = 0; // No increment per turn
				break;
			case OneMinutePlusSecond:
				blackTime = 60; // 1 minute for black
				whiteTime = 60; // 1 minute for white
				timeAddPerTurn = 1; // 1 second increment per move
				break;
			case TwoMinutePlusSecond:
				blackTime = 120; // 2 minutes for black
				whiteTime = 120; // 2 minutes for white
				timeAddPerTurn = 1; // 1 second increment per move
				break;
			case ThreeMinute:
				blackTime = 180; // 3 minutes for black
				whiteTime = 180; // 3 minutes for white
				timeAddPerTurn = 0; // No increment per turn
				break;
			case ThreeMinutePlusSecond:
				blackTime = 180; // 3 minutes for black
				whiteTime = 180; // 3 minutes for white
				timeAddPerTurn = 1; // 1 second increment per move
				break;
			case FiveMinute:
				blackTime = 300; // 5 minutes for black
				whiteTime = 300; // 5 minutes for white
				timeAddPerTurn = 0; // No increment per turn
				break;
			case TenMinute:
				blackTime = 600; // 10 minutes for black
				whiteTime = 600; // 10 minutes for white
				timeAddPerTurn = 0; // No increment per turn
				break;
			case FifteenMinutePlusTenSecond:
				blackTime = 900; // 15 minutes for black
				whiteTime = 900; // 15 minutes for white
				timeAddPerTurn = 10; // 10 seconds increment per move
				break;
			case ThirtyMinute:
				blackTime = 1800; // 30 minutes for black
				whiteTime = 1800; // 30 minutes for white
				timeAddPerTurn = 0; // No increment per turn
				break;
			default:
				throw new UnsupportedOperationException("Error no timemode selected or unlimited time");
		}

		setLayout(new GridLayout(2, 1)); // 2 rows, one for each timer
		blackTimerLabel = new JLabel("Black Time: " + formatTime(blackTime), SwingConstants.CENTER);
		whiteTimerLabel = new JLabel("White Time:" + formatTime(whiteTime), SwingConstants.CENTER);
		add(blackTimerLabel);
		add(whiteTimerLabel);
		whiteTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (whiteTime > 0) {
					whiteTime--;
					whiteTimerLabel.setText("White Time: " + formatTime(whiteTime));
				}
			}
		});
		blackTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (blackTime > 0) {
					blackTime--;
					blackTimerLabel.setText("Black Time: " + formatTime(blackTime));
				}
			}
		});
	}

	public void newMove(boolean isWhiteTurn) {
		if (isWhiteTurn) {
			blackTime += timeAddPerTurn;
			blackTimer.stop();
			whiteTimer.restart();
		} else {
			whiteTime += timeAddPerTurn;
			whiteTimer.stop();
			blackTimer.restart();
		}

		blackTimerLabel.setText("Black Time: " + formatTime(blackTime));
		whiteTimerLabel.setText("White Time: " + formatTime(whiteTime));
	}

	private String formatTime(int timeInSeconds) {
		int minutes = timeInSeconds / 60;
		int seconds = timeInSeconds % 60;
		return minutes + ":" +  new DecimalFormat("00").format(seconds);
	}
}