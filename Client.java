import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.net.*;
import java.util.*;

class Jogador {
	final int STANDING    = 0;
	final int CROUCHING   = 1;
	final int SHOOTING    = 2;
	Image img[] = null;
	Image bala = null;
	int vidas = 5;
	int estado = STANDING;
	int underSpace;
	int xposicao;
	int yposicao;
	int xinicial;
	int yinicial;
	boolean atirando = false;
	
	
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


class Client extends JFrame implements Runnable{

	Image fundo = null, life = null;
	Jogador jogadorA = new Jogador(1);
	Jogador jogadorB = new Jogador(1);
	Desenho tela = new Desenho();
	boolean agindo = false;
	static Scanner is = null;
	static PrintStream os = null;
    static Thread t;
    String inputLine, outputLine;
    static boolean gameOn = true;
    boolean keyPressed = false;
    int vidas = 5, xLife;

	class Desenho extends JPanel{


		Desenho(){
			try {
				fundo = ImageIO.read(new File("background.png"));
				life = ImageIO.read(new File("life.png"));
	      	} catch (IOException e) {
		        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
		        System.exit(1);
	      	}
	    }
		
		public void paintComponent(Graphics g) {
	    	super.paintComponent(g);

	    	g.drawImage(fundo, 0, 0, getSize().width, getSize().height, this);

	    	xLife = 1140;
	    	for(int i=0; i<vidas; i++){
	    		g.drawImage(life, xLife, 10, life.getWidth(this), life.getHeight(this), this);
	    		xLife -= 50;
	    	}

			g.drawImage(jogadorA.img[jogadorA.estado], getSize().width - jogadorA.img[jogadorA.estado].getWidth(this) - 10,
		 		getSize().height - jogadorA.img[jogadorA.estado].getHeight(this) - jogadorA.underSpace, 
		 		jogadorA.img[jogadorA.estado].getWidth(this),
		 		jogadorA.img[jogadorA.estado].getHeight(this), this);
			g.drawImage(jogadorB.img[jogadorB.estado], jogadorB.img[jogadorB.estado].getWidth(this), 
				getSize().height - jogadorB.img[jogadorB.estado].getHeight(this) - 20, 
				-jogadorB.img[jogadorB.estado].getWidth(this), 
				jogadorB.img[jogadorB.estado].getHeight(this), this);
			calcula_posicao();
			g.drawImage(jogadorA.bala, jogadorA.xposicao, jogadorA.yposicao, 10, 4, this);
			
		}
		
		public Dimension getPreferredSize() {
      		return new Dimension(1200, 699);
    	}

	}
	
	public void calcula_posicao() {
		if(!jogadorA.atirando) {
			if(jogadorA.estado == jogadorA.CROUCHING) {
				jogadorA.yposicao = getSize().height - jogadorA.img[jogadorA.CROUCHING].getHeight(this) + 167;
				jogadorA.xposicao = getSize().width - jogadorA.img[jogadorA.CROUCHING].getWidth(this) + 20;
				jogadorA.xinicial = jogadorA.xposicao;
				jogadorA.yinicial = jogadorA.yposicao;
			}
		
			if(jogadorA.estado == jogadorA.STANDING) {
				jogadorA.xposicao = getSize().width - jogadorA.img[jogadorA.STANDING].getWidth(this)/2 - 86;
				jogadorA.yposicao = getSize().height - jogadorA.img[jogadorA.STANDING].getHeight(this) + 75;
				jogadorA.xinicial = jogadorA.xposicao;
				jogadorA.yinicial = jogadorA.yposicao;
			}
		}
	}
	
	class Abaixar extends Thread {
		public void run() {

			jogadorA.estado = jogadorA.CROUCHING;
			repaint();
			try{
				sleep(1000);
			} catch (InterruptedException e) {};
			agindo = false;
			jogadorA.estado = jogadorA.STANDING;
			repaint();
			outputLine = "Levantou";
		    os.println(outputLine);
		}
	}
	
	class Atirar extends Thread {
		public void run() {
			try {
				if(!jogadorA.atirando) {
					jogadorA.atirando = true;
					while(jogadorA.xposicao > -10) {
						jogadorA.xposicao = jogadorA.xposicao - 10;
						sleep(5);
						repaint();
					}
					jogadorA.atirando = false;
					jogadorA.xposicao = jogadorA.xinicial;
				}
			}
			catch (InterruptedException e) {}
		}
	}
	
	class Iniciar extends Thread {
		public void run() {
			jogadorA.estado = jogadorA.STANDING;
			calcula_posicao();
			repaint();
		}
	}

	class Pular extends Thread{
		int y = jogadorA.underSpace;
		final int MAX = jogadorA.img[jogadorA.STANDING].getHeight(tela);
		public void run(){
			jogadorA.estado = jogadorA.STANDING;
			while(jogadorA.underSpace < MAX){
				jogadorA.underSpace++;
				repaint();
				Toolkit.getDefaultToolkit().sync();
				try{
					sleep(1);
				} catch (InterruptedException e) {};
			}
			while(jogadorA.underSpace > y){
				jogadorA.underSpace--;
				repaint();
				Toolkit.getDefaultToolkit().sync();
				try{
					sleep(1);
				} catch (InterruptedException e) {};
			}
			agindo = false;
		}
	}

	Client() {
	    super("FarWest");
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				keyPressed = true;
				switch (e.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						if(!agindo){
							outputLine = "Abaixar";
							os.println(outputLine);
							os.flush();
						}
						break;
					case KeyEvent.VK_UP:
						if(!agindo){
							outputLine = "Pular";
							os.println(outputLine);
							os.flush();
						}
						break;
					case KeyEvent.VK_SPACE:
						calcula_posicao();
						outputLine = "Atirar";
						os.println(outputLine);
						os.flush();
						break;
				}
			}
			public void keyReleased(KeyEvent e) {
				keyPressed = false;
				switch (e.getKeyCode()) {
				}
			}
		});
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setResizable(false);
	    add(tela);
	    pack();
	    setVisible(true);
	    jogadorA.underSpace = 20;
  	}

  	public void run() {
        do {
            inputLine = is.nextLine();
            switch (inputLine) {
					case "Pulou":
						agindo = true;
						new Pular().start();
						break;
					case "Abaixou":
						agindo = true;
						new Abaixar().start();
						break;
					case "Atirou":
						new Atirar().start();
						break;
				}
        } while (!inputLine.equals(""));
    }

	public static void main(String[] args) {
		Client c = new Client();
		Socket socket = null;
		Thread t = new Thread(c);

        try {
            socket = new Socket("127.0.0.1", 80);
            os = new PrintStream(socket.getOutputStream(), true);
            is = new Scanner(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to host");
        }

        t.start();

        try{
			while(gameOn) Thread.sleep(10000);
		} catch (InterruptedException e) {};
        

        try {
            os.close();
            is.close();
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
	}
}