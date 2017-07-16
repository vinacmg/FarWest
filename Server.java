import java.net.*;
import java.io.*;
import java.util.*;

class Server {
    public static void main(String[] args) {
        ServerSocket serverSocket=null;

        try {
            serverSocket = new ServerSocket(80);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + 80 + ", " + e);
            System.exit(1);
        }

        Socket[] clientSocket = new Socket[2];

        for(int i=0;i<2;i++){	
	        try {
	            clientSocket[i] = serverSocket.accept();
	        } catch (IOException e) {
	        System.out.println("Accept failed: " + 80 + ", " + e);
	        System.exit(1);
	        }

	        System.out.println("Accept "+ (i+1) +" Funcionou!");

    		new Servindo(clientSocket).start();

    	}
        
   		//colocar servindo ora do for depois de pronto

        try {
            serverSocket.close();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}

class Servindo extends Thread {
    Socket clientSocket;    
    static PrintStream[] os = new PrintStream[2];
    static JogadorCliente[] jogador = new JogadorCliente[2];
    static int cont = 0;
    final int ESSE, OPONENTE;

    Servindo(Socket[] clientSocket) {
    	this.clientSocket = clientSocket[cont];
    	if(cont == 0){
			ESSE = 0;
    		OPONENTE = 1;
    		jogador[ESSE] = new JogadorCliente(890,460);
    	}
    	else{
			ESSE = 1;
    		OPONENTE = 0;
    		jogador[ESSE] = new JogadorCliente(0,460);
    	}
		
    	cont++;
    }

    public void run() {
        try {
        Scanner is = new Scanner(clientSocket.getInputStream());
        os[ESSE] = new PrintStream(clientSocket.getOutputStream());
        String inputLine, outputLine;

        do {
            inputLine = is.nextLine();
            switch (inputLine) {
					case "Pular":
						os[OPONENTE].println("PossoPular?");  //verifica com o oponente se pode pular (por conta da animação)
        				os[OPONENTE].flush();
						break;
					case "Abaixar":
						os[OPONENTE].println("OponenteAbaixou");
        				os[OPONENTE].flush();
						os[ESSE].println("Abaixou");
            			os[ESSE].flush();
            			
						jogador[ESSE].estado = jogador[ESSE].CROUCHING;
						break;
					case "Levantar":
						os[ESSE].println("Levantou");
            			os[ESSE].flush();
            			os[OPONENTE].println("OponenteLevantou");
        				os[OPONENTE].flush();
						jogador[ESSE].estado = jogador[ESSE].STANDING;
						break;
					case "Estado":
					    inputLine = is.nextLine();
					case "Atirar":
						os[ESSE].println("Atirou");
            			os[ESSE].flush();
            			os[OPONENTE].println("OponenteAtirou");
        				os[OPONENTE].flush();
        				jogador[ESSE].atirar('A');
            			break;
            		case "PodePular": //o cliente dessa thread está disponível em ter seu oponente pulando
            			os[OPONENTE].println("Pulou"); // "te libero! Pula!"
            			os[OPONENTE].flush();
            			os[ESSE].println("OponentePulou"); //avisa o proprio cliente que o oponente realmente pulou
            			os[ESSE].flush();

				}
        } while (!inputLine.equals(""));

        os[ESSE].close();
        is.close();
        clientSocket.close();

        } catch (IOException e) {
        e.printStackTrace();
        } catch (NoSuchElementException e) {
        System.out.println("Conexacao terminada pelo cliente");
        }
    }

	class JogadorCliente {
	
		final int STANDING    = 0;
		final int CROUCHING   = 1;
		int vidas = 5;
		int municao = 0;
		int estado = STANDING;
		int x;
		int y;
		int underSpace = 20;
		int delta = 266;
		int yarma;
		BalaServer[] bala;
		
		
		JogadorCliente(int x, int y) {
			this.x = x;
			this.y = y;
			bala = new BalaServer[4];
			for(int i = 0; i < 4; i++) {
				bala[i] = new BalaServer();
			}
		}

		void pulou(){
			new Pulou().start();
		}
	    
		void atirar(char op){
			if(op == 'A') new AtirouA().start();
			//else new AtirouB().start();
		}

	    class Pulou extends Thread{
			int y = underSpace;
			final int MAX = delta;
			public void run(){
				estado = STANDING;
				while(underSpace < MAX){
					underSpace+=14;
					try{
						sleep(30);
					} catch (InterruptedException e) {};
				}
				while(underSpace > y){
					underSpace-=14;
					try{
						sleep(30);
					} catch (InterruptedException e) {};
				}
			}
		}

		class AtirouA extends Thread {
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
								if(estado != CROUCHING) bala[valor].yposicao = underSpace + 133;
								else bala[valor].yposicao = 100;
								while(bala[valor].xposicao > -10) {
									bala[valor].xposicao = bala[valor].xposicao - 10;
									sleep(10);
									

									int lado = 100;							
									if(jogador[OPONENTE].estado != CROUCHING){
										int topo = jogador[OPONENTE].delta + jogador[OPONENTE].underSpace;
										int baixo = topo - jogador[OPONENTE].delta;
										if((bala[valor].xposicao <= lado)&&(bala[valor].yposicao <= topo)&&(bala[valor].yposicao >= baixo)) {
											if(jogador[OPONENTE].vidas > 1){
												jogador[OPONENTE].vidas--;
												os[OPONENTE].println("PerdeuVida");
												os[OPONENTE].flush();
											}
											else{
												jogador[OPONENTE].vidas--;
												os[OPONENTE].println("Morreu");
												os[OPONENTE].flush();
											}
											break;
										}
									}
									else {
										if((bala[valor].yposicao == 100) && (bala[valor].xposicao <= lado)){
											break;
										}
									}
									
								}
								bala[valor].atirando = false;
							}
						}
					}
					catch (InterruptedException e) {}
				}
		}

		class BalaServer {

			int xposicao;
			int yposicao;
			boolean atirando = false;
		}
	}
}

class JogadorCliente1 extends JogadorCliente{
	int x = 890;
	int y = 460;
}

class JogadorCliente2 extends JogadorCliente{
	int x = 0; //300 de largura
	int y = 460;
}

