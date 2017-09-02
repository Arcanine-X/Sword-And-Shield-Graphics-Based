package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class InfoPanel extends JPanel{
	InformationFrame frame;
	JTextArea information;
	StringBuilder stringBuilder = new StringBuilder();

	public InfoPanel(InformationFrame frame) {
		this.frame = frame;
		JTextPane text = new JTextPane();
		text.setContentType("text/html");
		displayPage();
		text.setText(stringBuilder.toString());
		this.add(text);
	}


	private String readFile(String file) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
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

	public void displayPage() {
		try{
			readFile(InfoPanel.class.getResource("/Resources/info.txt").getFile());
		} catch(IOException e){
			e.printStackTrace();
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
}
