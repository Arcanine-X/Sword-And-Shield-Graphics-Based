package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class just draws the image onto the menu frame. It exists to make the layout easier.
 * @author patelchin1
 *
 */
public class MenuPanel extends JPanel{

	public MenuPanel() {
		this.setBackground(Color.WHITE);
		URL url = MenuFrame.class.getResource("/Resources/Logo.jpg");
		ImageIcon icon = new ImageIcon(url);
		this.add(new JLabel(icon));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension((int) dimension.getWidth(), 1000);
	}
}
