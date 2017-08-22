package Model;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class MenuFrame extends JFrame {
	public MenuFrame() {
		JPanel menuPanel = new JPanel();
		JButton start = new JButton("Start Game");
		JButton information = new JButton("Information");
		JButton quit = new JButton("Quit");
		menuPanel.add(start);
		menuPanel.add(information);
		menuPanel.add(quit);
		this.setTitle("Menu");
		this.setFocusable(true);
		this.setLayout(new FlowLayout());
		this.add(menuPanel);
		this.pack();
		this.setVisible(true);

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
				//InformationFrame info = new InformationFrame();
				//MenuFrame.this.dispatchEvent(new WindowEvent(MenuFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});

		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
	}

	public Dimension getPreferredSize() {return new Dimension(600, 600);}


}
