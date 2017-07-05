import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

class Jogador {
	final int STANDING    = 0;
	final int CROUCHING   = 1;
	final int SHOOTING    = 2;
	Image img[] = null;
	Image bala = null;
	int vidas = 5;
	int estado = STANDING;
	int xposicao = 972;
	int yposicao = 548;
	
	
	Jogador(int numero) {
		img = new Image[3];
		try {
			bala = ImageIO.read(new File("bala.png"));
			img[STANDING] = ImageIO.read(new File("standing" + numero + ".png"));
			img[CROUCHING] = ImageIO.read(new File("agachado" + numero + ".png"));
		}
		catch (IOException e) {}
	}
}


class Client extends JFrame {

	Image fundo = null;
	Jogador jogadorA = new Jogador(1);
	//Jogador jogadorB = new Jogador();

	class Desenho extends JPanel{

		Desenho(){
			try {
				fundo = ImageIO.read(new File("background.png"));
	      	} catch (IOException e) {
		        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
		        System.exit(1);
	      	}
	    }
		
		public void paintComponent(Graphics g) {
	    	super.paintComponent(g);
	    	g.drawImage(fundo, 0, 0, getSize().width, getSize().height, this);
	    	g.drawImage(jogadorA.bala, jogadorA.xposicao, jogadorA.yposicao, 10, 4, this);
			g.drawImage(jogadorA.img[jogadorA.estado], getSize().width - jogadorA.img[jogadorA.estado].getWidth(this) - 10, getSize().height - jogadorA.img[jogadorA.estado].getHeight(this) - 20, jogadorA.img[jogadorA.estado].getWidth(this), jogadorA.img[jogadorA.estado].getHeight(this), this);
    	}
		
		public Dimension getPreferredSize() {
      		return new Dimension(1200, 700);
    	}
	}
	
	class Abaixar extends Thread {
		public void run() {
			jogadorA.estado = jogadorA.CROUCHING;
			repaint();
		}
	}
	
	class Atirar extends Thread {
		public void run() {
		}
	}
	
	class Iniciar extends Thread {
		public void run() {
			jogadorA.estado = jogadorA.STANDING;
			repaint();
		}
	}


	Client() {
	    super("FarWest");
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						new Abaixar().start();
						break;
				}
			}
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						new Iniciar().start();
						break;
				}
			}
		});
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    add(new Desenho());
	    pack();
	    setVisible(true);
  	}

	public static void main(String[] args) {
		new Client();
	}
}