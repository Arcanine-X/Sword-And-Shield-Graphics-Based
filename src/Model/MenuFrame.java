package Model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class represents the frame for the menu. This menu consists of buttons, and when a button is pressed,
 * it will dispose of this frame and create the frame accordingly.
 * @author Chin Patel
 *
 */
public class MenuFrame extends JFrame {
	public MenuFrame() {
		//Create Buttons
		JPanel buttons = new JPanel();
		JButton start = new JButton("Start Game");
		JButton information = new JButton("Information");
		JButton quit = new JButton("Quit");
		buttons.setBackground(Color.white);
		//Add Buttons
		buttons.add(start);
		buttons.add(information);
		buttons.add(quit);
		this.setTitle("Menu");
		this.setFocusable(true);
		this.add(buttons, BorderLayout.NORTH);
		this.add(new JLabel(new ImageIcon("editied.jpg")));
		this.pack();
		this.setVisible(true);
		//Button action listeners
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameFrame mainGame = new GameFrame();
				MenuFrame.this.dispatchEvent(new WindowEvent(MenuFrame.this, WindowEvent.WINDOW_CLOSING));

			}
		});
		information.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InformationFrame info = new InformationFrame();
				MenuFrame.this.dispatchEvent(new WindowEvent(MenuFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});

		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
	}
	public Dimension getPreferredSize() {
		return new Dimension(800, 800);
	}

}
