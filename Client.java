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
	int height;
	Desenho tela = new Desenho();

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
	    	g.drawImage(img[STANDING1], getSize().width - img[STANDING1].getWidth(this) - 10,
	    		getSize().height - img[STANDING1].getHeight(this) - height, 
	    		(int)Math.ceil((getSize().width/1200.0)*img[STANDING1].getWidth(this)), 
	    		(int)Math.ceil((getSize().height/699.0)*img[STANDING1].getHeight(this)), this);
    	}
		
		public Dimension getPreferredSize() {
      		return new Dimension(1200, 699);
    	}

    }

	public class Pular extends Thread{
		int y = height;
		final int MAX = img[STANDING1].getHeight(tela);
		public void run(){
			while(height < MAX){
				height++;
				repaint();
				try{
					sleep(2);
				} catch (InterruptedException e) {};
			}
			while(height > y){
				height--;
				repaint();
				try{
					sleep(2);
				} catch (InterruptedException e) {};
			}
		}
	}

	Client() {
	    super("FarWest");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    add(tela);
	    pack();
	    setVisible(true);
	    height = (int)Math.ceil(tela.getSize().height/37.0);
	    while(true){
		    try{
		    	new Pular().start();
				Thread.sleep(1500);
			} catch (InterruptedException e) {};
		}
  	}

	public static void main(String[] args) {
		Client c = new Client();
	}
}