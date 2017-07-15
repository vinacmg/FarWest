import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.net.*;

class Client extends JFrame implements Runnable{
	
	class Bala {
		Image img = null;
		int xposicao;
		int yposicao;
		boolean atirando = false;
		
		Bala() {
			try {
				img = ImageIO.read(new File("bala.png"));
			} catch (IOException e) {}
		}
	}

	class Jogador {
		final int STANDING    = 0;
		final int CROUCHING   = 1;
		final int SHOOTING    = 2;
		Bala bala[];
		Image img[] = null;
		int municao = 0;
		int vidas = 5;
		int estado = STANDING;
		int xinicial;
		int yinicial;
		int underSpace = 20;
		boolean atirando = false;
		
		
		Jogador(int numero) {
			img = new Image[3];
			bala = new Bala[5];
			for(int i = 0; i < 5; i++) {
				bala[i] = new Bala();
			}
			try {
				img[STANDING] = ImageIO.read(new File("standing" + numero + ".png"));
				img[CROUCHING] = ImageIO.read(new File("agachado" + numero + ".png"));
			}
			catch (IOException e) {}
		}


		void abaixar(){ new Abaixar().start();}
		void atirar(){ new Atirar().start();}
		void iniciar(){ new Iniciar().start();}
		void pular(){ new Pular().start();}

		class Abaixar extends Thread {
			public void run() {
				estado = jogadorA.CROUCHING;
				repaint();
			}
		}
		
		class Atirar extends Thread {
			public void run() {
			int valor = jogadorA.municao;
			try {
				if(valor > 4) {
					jogadorA.municao = 0;
				}
				else {
					if(!jogadorA.bala[valor].atirando) {
						jogadorA.bala[valor].atirando = true;
						jogadorA.municao++;
						while(jogadorA.bala[valor].xposicao > -10) {
							jogadorA.bala[valor].xposicao = jogadorA.bala[valor].xposicao - 10;
							sleep(10);
							repaint();
							
							int topo = getSize().height - jogadorB.img[jogadorB.estado].getHeight(tela) - jogadorB.underSpace;
							int baixo = getSize().height - jogadorB.underSpace;
							int lado = jogadorB.img[jogadorA.estado].getWidth(tela) - 200;
							if((jogadorA.bala[valor].xposicao <= lado)&&(jogadorA.bala[valor].yposicao >= topo)&&(jogadorA.bala[valor].yposicao <= baixo)) {
								break;
							}
								
							
						}
						jogadorA.bala[valor].atirando = false;
					}
				}
			}
			catch (InterruptedException e) {}
		}
		}
		
		class Iniciar extends Thread {
			public void run() {
				estado = jogadorA.STANDING;
				calcula_posicao();
				repaint();
			}
		}

		
		class Pular extends Thread{
			int y = underSpace;
			final int MAX = img[STANDING].getHeight(tela);
			public void run(){
				estado = STANDING;
				while(underSpace < MAX){
					underSpace++;
					repaint();
					Toolkit.getDefaultToolkit().sync();
					try{
						sleep(1);
					} catch (InterruptedException e) {};
				}
				while(underSpace > y){
					underSpace--;
					repaint();
					Toolkit.getDefaultToolkit().sync();
					try{
						sleep(1);
					} catch (InterruptedException e) {};
				}
				agindo = false;
			}
		}
	}


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
				getSize().height - jogadorB.img[jogadorB.estado].getHeight(this) - jogadorB.underSpace, 
				-jogadorB.img[jogadorB.estado].getWidth(this), 
				jogadorB.img[jogadorB.estado].getHeight(this), this);
			calcula_posicao();
			g.drawImage(jogadorA.bala[0].img, jogadorA.bala[0].xposicao, jogadorA.bala[0].yposicao, 10, 4, this);
			g.drawImage(jogadorA.bala[1].img, jogadorA.bala[1].xposicao, jogadorA.bala[1].yposicao, 10, 4, this);
			g.drawImage(jogadorA.bala[2].img, jogadorA.bala[2].xposicao, jogadorA.bala[2].yposicao, 10, 4, this);
			g.drawImage(jogadorA.bala[3].img, jogadorA.bala[3].xposicao, jogadorA.bala[3].yposicao, 10, 4, this);
			g.drawImage(jogadorA.bala[4].img, jogadorA.bala[4].xposicao, jogadorA.bala[4].yposicao, 10, 4, this);
		}
		
		public Dimension getPreferredSize() {
      		return new Dimension(1200, 700);
    	}
	}
	
	public void calcula_posicao() {
		int i;
		//if(!agindo) {
			if(jogadorA.estado == jogadorA.CROUCHING) {
				for(i = 0; i < 4; i++) {
					if(!jogadorA.bala[i].atirando) {
						jogadorA.bala[i].yposicao = getSize().height - jogadorA.img[jogadorA.CROUCHING].getHeight(this) + 167;
						jogadorA.bala[i].xposicao = getSize().width - jogadorA.img[jogadorA.CROUCHING].getWidth(this) + 20;
					}
				}
				jogadorA.xinicial = jogadorA.bala[i - 1].xposicao;
				jogadorA.yinicial = jogadorA.bala[i - 1].yposicao;
			}
			else {
				if(!agindo) {
					if(jogadorA.estado == jogadorA.STANDING) {
						for(i = 0; i < 4; i++) {
							if(!jogadorA.bala[i].atirando) {
								jogadorA.bala[i].xposicao = getSize().width - jogadorA.img[jogadorA.STANDING].getWidth(this)/2 - 86;
								jogadorA.bala[i].yposicao = getSize().height - jogadorA.img[jogadorA.STANDING].getHeight(this) + 75;
							}
						}
						jogadorA.xinicial = jogadorA.bala[i - 1].xposicao;
						jogadorA.yinicial = jogadorA.bala[i - 1].yposicao;
					}
				}
				else {
					for(i = 0; i < 4; i++) {
						if(!jogadorA.bala[i].atirando) {
							jogadorA.bala[i].xposicao = getSize().width - jogadorA.img[jogadorA.STANDING].getWidth(this)/2 - 86;
							jogadorA.bala[i].yposicao = getSize().height - jogadorA.img[jogadorA.estado].getHeight(this) - jogadorA.underSpace + 100;
						}
					}
					jogadorA.xinicial = jogadorA.bala[i - 1].xposicao;
					jogadorA.yinicial = jogadorA.bala[i - 1].yposicao;
				}
			}
		//}
	}
	

	Client() {
	    super("FarWest");
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
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
						//calcula_posicao();
						outputLine = "Atirar";
						os.println(outputLine);
						os.flush();
						break;
				}
			}
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						outputLine = "Levantar";
						os.println(outputLine);
						os.flush();
						break;
				}
			}
		});
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    add(new Desenho());
	    pack();
	    setVisible(true);
  	}

  	public void run() {
        do {
            inputLine = is.nextLine();
            switch (inputLine) {
					case "Pulou":
						agindo = true;
						jogadorA.pular();
						break;
					case "Abaixou":
						agindo = true;
						jogadorA.abaixar();
						break;
					case "Atirou":
						jogadorA.atirar();
						break;
					case "Levantou":
						agindo = false;
						jogadorA.iniciar();
						break;
					case "OponentePulou":
						jogadorB.pular();
						break;
					case "OponenteAbaixou":
						jogadorB.abaixar();
						break;
					case "OponenteAtirou":
						jogadorB.atirar();
	        			break;
					case "OponenteLevantou":
						jogadorB.iniciar();
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