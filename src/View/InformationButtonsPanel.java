package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This class holds the buttons for the information frame
 * @author Chin Patel
 *
 */
public class InformationButtonsPanel extends JPanel{
	private InformationFrame infoFrame;
	public InformationButtonsPanel(InformationFrame infoFrame) {
		this.infoFrame = infoFrame;
		//Create buttons
		JButton menu = new JButton("Menu");
		JButton start = new JButton("Start Game");
		JButton quit = new JButton("Quit");
		//Add buttons to panel
		this.add(menu);
		this.add(start);
		this.add(quit);

		//Add action listeners for buttons
		menu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MenuFrame menuFrame = new MenuFrame();
				infoFrame.dispatchEvent(new WindowEvent(infoFrame, WindowEvent.WINDOW_CLOSING));
			}
		});
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameFrame mainGame = new GameFrame();
				infoFrame.dispatchEvent(new WindowEvent(infoFrame, WindowEvent.WINDOW_CLOSING));
			}
		});

		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
	}
}
