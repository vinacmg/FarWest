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
		Bala bala[];
		Image img[] = null;
		int municao = 0;
		int vidas = 5;
		int estado = STANDING;
		
		
		int underSpace = 20;
		boolean atirando = false;
		boolean agindo = false;
		
		
		Jogador(int numero) {
			img = new Image[2];
			bala = new Bala[4];
			for(int i = 0; i < 4; i++) {
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
				int valor = municao;
				try {
					if(valor >= 4) {
						municao = 0;
						repaint();
					}
					else {
						if(!bala[valor].atirando) {
							bala[valor].atirando = true;
							municao++;
							while(bala[valor].xposicao > -10) {
								bala[valor].xposicao = bala[valor].xposicao - 10;
								sleep(10);
								repaint();
								Toolkit.getDefaultToolkit().sync();
								
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
					underSpace+= 14;
					repaint();
					Toolkit.getDefaultToolkit().sync();
					try{
						sleep(30);
					} catch (InterruptedException e) {};
				}
				while(underSpace > y){
					underSpace-= 14;
					repaint();
					Toolkit.getDefaultToolkit().sync();
					try{
						sleep(30);
					} catch (InterruptedException e) {};
				}

				agindo = false;
			}
		}
	}

	class JogadorB extends Jogador {

		JogadorB(int numero) {
			super(numero);
		}

		void atirar(){ new AtirarB().start();}

		class AtirarB extends Thread{
			public void run() {
				int valor = municao;
				try {
					if(valor >= 4) {
						municao = 0;
					}
					else {
						if(!bala[valor].atirando) {
							bala[valor].atirando = true;
							municao++;
							while(bala[valor].xposicao < 1210) {
								bala[valor].xposicao = bala[valor].xposicao + 10;
								sleep(10);
								repaint();
								Toolkit.getDefaultToolkit().sync();
								
								int topo = getSize().height - jogadorA.img[jogadorA.estado].getHeight(tela) - jogadorA.underSpace;
								int baixo = getSize().height - jogadorA.underSpace;
								int lado = jogadorA.img[jogadorA.estado].getWidth(tela) - 190; //compensa o tamanho da bala em relação ao A
								if((bala[valor].xposicao >= 1200 - lado)&&(bala[valor].yposicao >= topo)&&(bala[valor].yposicao <= baixo)) {
									break;
								}
									
								
							}
							bala[valor].atirando = false;
						}
					}
				}
				catch (InterruptedException e) {}
			}
		}

		class Pular extends Thread{
			int y = underSpace;
			final int MAX = img[STANDING].getHeight(tela);
			public void run(){
				estado = STANDING;
				while(underSpace < MAX){
					underSpace+= 14;
					repaint();
					Toolkit.getDefaultToolkit().sync();
					try{
						sleep(30);
					} catch (InterruptedException e) {};
				}
				while(underSpace > y){
					underSpace-= 14;
					repaint();
					Toolkit.getDefaultToolkit().sync();
					try{
						sleep(30);
					} catch (InterruptedException e) {};
				}
			
				agindo = false;
			}
		}
	}

	Image fundo = null, life = null, municao = null, perdeu = null, venceu = null;
	Jogador jogadorA = new Jogador(1);
	JogadorB jogadorB = new JogadorB(2);
	Desenho tela = new Desenho();
	static Scanner is = null;
	static PrintStream os = null;
    static Thread t;
    String inputLine, outputLine;
    static boolean gameOn = true;
    boolean keyPressed = false;
    int xLife;
	boolean perdeuBool = false, venceuBool = false;
	int height, width;


	class Desenho extends JPanel{

		Desenho(){
			try {
				fundo = ImageIO.read(new File("background.png"));
				life = ImageIO.read(new File("life.png"));
				municao = ImageIO.read(new File("municao.png"));
				perdeu = ImageIO.read(new File("perdeu.png"));
				venceu = ImageIO.read(new File("venceu.png"));
	      	} catch (IOException e) {
		        JOptionPane.showMessageDialog(this, "A imagem não pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
		        System.exit(1);
	      	}

	    }
		
		public void paintComponent(Graphics g) {
	    	super.paintComponent(g);
	    	g.drawImage(fundo, 0, 0, getSize().width, getSize().height, this);
	    	
	    	xLife = 1140;
	    	for(int i=0; i<jogadorA.vidas; i++){
	    		g.drawImage(life, xLife, 10, life.getWidth(this), life.getHeight(this), this);
	    		xLife -= 50;
	    	}

	    	

	    	xLife = 1140;
	    	for(int i=4; i>jogadorA.municao; i--){
	    		g.drawImage(municao, xLife, 70, municao.getWidth(this), municao.getHeight(this), this);
	    		xLife -= 20;
	    	}
	    	calcula_posicao();
	    	//BALAS JOGADOR A
			g.drawImage(jogadorA.bala[0].img, jogadorA.bala[0].xposicao, jogadorA.bala[0].yposicao, 10, 4, this);
			g.drawImage(jogadorA.bala[1].img, jogadorA.bala[1].xposicao, jogadorA.bala[1].yposicao, 10, 4, this);
			g.drawImage(jogadorA.bala[2].img, jogadorA.bala[2].xposicao, jogadorA.bala[2].yposicao, 10, 4, this);
			g.drawImage(jogadorA.bala[3].img, jogadorA.bala[3].xposicao, jogadorA.bala[3].yposicao, 10, 4, this);
			//BALAS JOGADOR B
			g.drawImage(jogadorB.bala[0].img, jogadorB.bala[0].xposicao, jogadorB.bala[0].yposicao, -10, 4, this);
			g.drawImage(jogadorB.bala[1].img, jogadorB.bala[1].xposicao, jogadorB.bala[1].yposicao, -10, 4, this);
			g.drawImage(jogadorB.bala[2].img, jogadorB.bala[2].xposicao, jogadorB.bala[2].yposicao, -10, 4, this);
			g.drawImage(jogadorB.bala[3].img, jogadorB.bala[3].xposicao, jogadorB.bala[3].yposicao, -10, 4, this);

			g.drawImage(jogadorA.img[jogadorA.estado], getSize().width - jogadorA.img[jogadorA.estado].getWidth(this) - 10,
		 		getSize().height - jogadorA.img[jogadorA.estado].getHeight(this) - jogadorA.underSpace, 
		 		jogadorA.img[jogadorA.estado].getWidth(this),
		 		jogadorA.img[jogadorA.estado].getHeight(this), this);
			g.drawImage(jogadorB.img[jogadorB.estado], jogadorB.img[jogadorB.estado].getWidth(this), 
				getSize().height - jogadorB.img[jogadorB.estado].getHeight(this) - jogadorB.underSpace, 
				-jogadorB.img[jogadorB.estado].getWidth(this), 
				jogadorB.img[jogadorB.estado].getHeight(this), this);

			if(venceuBool) g.drawImage(venceu, getSize().width/2 - 250, getSize().height/2 - 175, 500, 350, this);
			if(perdeuBool) g.drawImage(perdeu, getSize().width/2 - 250, getSize().height/2 - 175, 500, 350, this);
		}
		
		public Dimension getPreferredSize() {
      		return new Dimension(1200, 700);
    	}

	}
	
	public void calcula_posicao() {
		int i;
		width = tela.getSize().width;
		height = tela.getSize().height;
		
			if(jogadorA.estado == jogadorA.CROUCHING) {
				for(i = 0; i < 4; i++) {
					if(!jogadorA.bala[i].atirando) {
						jogadorA.bala[i].yposicao = height - jogadorA.img[jogadorA.CROUCHING].getHeight(this) + 205;
						jogadorA.bala[i].xposicao = width - jogadorA.img[jogadorA.CROUCHING].getWidth(this) + 33;
					}
				}
				
				
			}
			else {
				if(!jogadorA.agindo) {
					if(jogadorA.estado == jogadorA.STANDING) {
						for(i = 0; i < 4; i++) {
							if(!jogadorA.bala[i].atirando) {
								jogadorA.bala[i].xposicao = width - jogadorA.img[jogadorA.STANDING].getWidth(this)/2 - 76;
								jogadorA.bala[i].yposicao = height - jogadorA.img[jogadorA.STANDING].getHeight(this) + 113;
							}
						}
						
						
					}
				}
				else {
					for(i = 0; i < 4; i++) {
						if(!jogadorA.bala[i].atirando) {
							jogadorA.bala[i].xposicao = width - jogadorA.img[jogadorA.STANDING].getWidth(this)/2 - 76;
							jogadorA.bala[i].yposicao = height - jogadorA.img[jogadorA.estado].getHeight(this) - jogadorA.underSpace + 133;
						}
					}
					
					
				}
			}
		
		if(jogadorB.estado == jogadorB.CROUCHING) {
			for(i = 0; i < 4; i++) {
				if(!jogadorB.bala[i].atirando) {
					jogadorB.bala[i].yposicao = height - jogadorB.img[jogadorB.CROUCHING].getHeight(this) + 205;
					jogadorB.bala[i].xposicao = jogadorB.img[jogadorB.CROUCHING].getWidth(this) - 43;
				}
			}
			
			
		}
		else {
			if(!jogadorB.agindo) {
				if(jogadorB.estado == jogadorB.STANDING) {
					for(i = 0; i < 4; i++) {
						if(!jogadorB.bala[i].atirando) {
							jogadorB.bala[i].xposicao = jogadorB.img[jogadorB.STANDING].getWidth(this)/2 + 66;
							jogadorB.bala[i].yposicao = height - jogadorB.img[jogadorB.STANDING].getHeight(this) + 113;
						}
					}
					
					
				}
			}
			else {
				for(i = 0; i < 4; i++) {
					if(!jogadorB.bala[i].atirando) {
						jogadorB.bala[i].xposicao = jogadorB.img[jogadorB.STANDING].getWidth(this)/2 + 66;
						jogadorB.bala[i].yposicao = height - jogadorB.img[jogadorB.estado].getHeight(this) - jogadorB.underSpace + 133;
					}
				}
				
				
			}
		}
	}

	Client() {
	    super("FarWest");
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						if(!jogadorA.agindo){
							outputLine = "Abaixar";
							os.println(outputLine);
							os.flush();
						}
						break;
					case KeyEvent.VK_UP:
						if(!jogadorA.agindo){
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
	    add(tela);
	    pack();
	    setResizable(false);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
  	}

  	public void run() {
        do {
            inputLine = is.nextLine();
            switch (inputLine) {
					case "Pulou":
						jogadorA.agindo = true;
						jogadorA.pular();
						break;
					case "Abaixou":
						jogadorA.agindo = true;
						jogadorA.abaixar();
						break;
					case "Atirou":
						jogadorA.atirar();
						break;
					case "Levantou":
						jogadorA.agindo = false;
						jogadorA.iniciar();
						break;
					case "PossoPular?":
						if(!jogadorB.agindo){
							os.println("PodePular");
						}
						break;
					case "OponentePulou":
						jogadorB.agindo = true;
						jogadorB.pular();
						break;
					case "OponenteAbaixou":
						jogadorB.agindo = true;
						jogadorB.abaixar();
						break;
					case "OponenteAtirou":
						jogadorB.atirar();
	        			break;
					case "OponenteLevantou":
						jogadorB.agindo = false;
						jogadorB.iniciar();
						break;
					case "PerdeuVida":
						jogadorA.vidas--;
						repaint();
						break;
					case "Morreu":
						jogadorA.vidas--;
						perdeuBool = true;
						repaint();
						gameOn = false;
						inputLine = "";
						os.println(inputLine);
						break;
					case "Venceu":
						venceuBool = true;
						repaint();
						gameOn = false;
						inputLine = "";
						os.println(inputLine);
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