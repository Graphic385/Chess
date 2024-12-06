import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SettingsPanel extends JPanel {
	private JComboBox<TimeSelection> timeSelectionComboBox;
    private JButton confirmButton;
	private TimeSelection selectedTime;

	public SettingsPanel(ChessGame game) {
		setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("Select Time Settings", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
		add(titleLabel, BorderLayout.NORTH);

		// Time selection combo box
		timeSelectionComboBox = new JComboBox<>(TimeSelection.values());
		JPanel comboBoxPanel = new JPanel();
		comboBoxPanel.add(new JLabel("Time Control:"));
		comboBoxPanel.add(timeSelectionComboBox);
		add(comboBoxPanel, BorderLayout.CENTER);

		// Buttons for confirm 
		JPanel buttonPanel = new JPanel();
		confirmButton = new JButton("Confirm");
		buttonPanel.add(confirmButton);
		add(buttonPanel, BorderLayout.SOUTH);

		// Event listeners
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedTime = (TimeSelection)timeSelectionComboBox.getSelectedItem();
				game.gamemodeSelected();
			}
		});
	}

	public TimeSelection getSelectedTime() {
		return selectedTime;
	}
}