package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * This panel displays the information on the information frame. It reads a file containing
 * all the text in a styled manner, and reads it to a string builder.
 * @author Chin Patel
 *
 */
public class InfoPanel extends JPanel{
	private InformationFrame frame;
	private StringBuilder stringBuilder = new StringBuilder();

	public InfoPanel(InformationFrame frame) {
		this.frame = frame;
		JTextPane text = new JTextPane();
		text.setContentType("text/html");
		try {
			readFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		text.setText(stringBuilder.toString());
		text.setEditable(false);
		this.add(text);
	}

	/**
	 * Reads the information file from the resources folder. It is read as a input stream
	 * as this will allow it to work in jar files were files don't exist independently.
	 * @return
	 * @throws IOException
	 */
	private String readFile() throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/Resources/info.txt");
	    BufferedReader reader = new BufferedReader(new InputStreamReader (is));
	    String         line = null;
	    stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
	        while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	public Dimension getPreferredSize() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension((int) dimension.getWidth(), 1000);
	}
	/**
	 * Modified the readFile() method from :
	 * 	https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
	 */
}
