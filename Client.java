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
	int underSpace;
	
	
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
	Desenho tela = new Desenho();

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
	    	g.drawImage(jogadorA.bala, jogadorA.xposicao, jogadorA.yposicao, 15, 8, this);
			g.drawImage(jogadorA.img[jogadorA.estado], getSize().width - jogadorA.img[jogadorA.estado].getWidth(this) - 10,
		 		getSize().height - jogadorA.img[jogadorA.estado].getHeight(this) - jogadorA.underSpace, 
		 		jogadorA.img[jogadorA.estado].getWidth(this),
		 		jogadorA.img[jogadorA.estado].getHeight(this), this);

    	}
		
		public Dimension getPreferredSize() {
      		return new Dimension(1200, 699);
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

	public class Pular extends Thread{
		int y = jogadorA.underSpace;
		final int MAX = jogadorA.img[jogadorA.STANDING].getHeight(tela);
		public void run(){
			while(jogadorA.underSpace < MAX){
				jogadorA.underSpace++;
				repaint();
				try{
					sleep(2);
				} catch (InterruptedException e) {};
			}
			while(jogadorA.underSpace > y){
				jogadorA.underSpace--;
				repaint();
				try{
					sleep(2);
				} catch (InterruptedException e) {};
			}
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
	    add(tela);
	    pack();
	    setVisible(true);
	    jogadorA.underSpace = 20;
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