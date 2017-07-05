import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

class Client extends JFrame {

	Image[] img = null;
	final int BACKGROUND = 0;
	final int STANDING1 = 1;
	final int CROUCHING = 2;
	int estado = STANDING1;

	class Desenho extends JPanel{

		Desenho(){
			img = new Image[3];
			try {
				img[BACKGROUND] = ImageIO.read(new File("background.png"));
				img[STANDING1] = ImageIO.read(new File("standing1.png"));
				img[CROUCHING] = ImageIO.read(new File("agachado1.png"));
	      	} catch (IOException e) {
		        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
		        System.exit(1);
	      	}
	    }
		
		public void paintComponent(Graphics g) {
	    	super.paintComponent(g);
	    	g.drawImage(img[BACKGROUND], 0, 0, getSize().width, getSize().height, this);
	    	g.drawImage(img[STANDING1], getSize().width - img[STANDING1].getWidth(this) - 10, getSize().height - img[STANDING1].getHeight(this) - 20, img[STANDING1].getWidth(this), img[STANDING1].getHeight(this), this);
    	}
		
		public Dimension getPreferredSize() {
      		return new Dimension(1200, 700);
    	}

	}


	Client() {
	    super("FarWest");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    add(new Desenho());
	    pack();
	    setVisible(true);
  	}

	public static void main(String[] args) {
		new Client();
	}
}