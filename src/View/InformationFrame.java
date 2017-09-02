package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;


/**
 * This class represents the information frame.
 * @author patelchin1
 *
 */
public class InformationFrame extends JFrame{
	public InformationFrame() {
		this.setTitle("Information");
		this.setLayout(new BorderLayout());
		InfoPanel infoPanel = new InfoPanel(this);
		InformationButtonsPanel buttons = new InformationButtonsPanel(this);
		this.add(infoPanel,BorderLayout.CENTER);
		this.add(buttons, BorderLayout.NORTH);
		buttons.setBackground(Color.WHITE);
		this.pack();
		this.setVisible(true);
	}


	public Dimension getPreferredSize() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension((int) dimension.getWidth(), 1000);
	}

}
