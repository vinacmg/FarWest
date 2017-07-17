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
    	}

    	for(int i=0; i<2; i++){
	        new Servindo(clientSocket).start();
    	}
        
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
    char c;

    Servindo(Socket[] clientSocket) {
    	this.clientSocket = clientSocket[cont];
    	if(cont == 0){
			ESSE = 0;
    		OPONENTE = 1;
    		jogador[ESSE] = new JogadorCliente();
    		c = 'A';
    	}
    	else{
			ESSE = 1;
    		OPONENTE = 0;
    		jogador[ESSE] = new JogadorCliente();
    		c = 'B';
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
        				jogador[ESSE].atirar(c);
            			break;
            		case "PodePular": //o cliente dessa thread está disponível em ter seu oponente pulando (por conta da animação)
            			os[OPONENTE].println("Pulou"); // "te libero! Pula!"
            			os[OPONENTE].flush();
            			os[ESSE].println("OponentePulou"); //avisa o proprio cliente que o oponente realmente pulou
            			os[ESSE].flush();
            			jogador[OPONENTE].pulou();

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
		int underSpace = 20;
		int delta = 266;
		BalaServer[] bala;
		
		
		JogadorCliente() {
			bala = new BalaServer[4];
			for(int i = 0; i < 4; i++) {
				bala[i] = new BalaServer();
			}
		}

		void pulou(){
			new Pulou().start();
		}
	    
		void atirar(char c){
			if(c == 'A') new AtirouA().start();
			else new AtirouB().start();
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
							bala[valor].xposicao = 964;
							if(estado != CROUCHING) bala[valor].yposicao = underSpace + 133;
							else bala[valor].yposicao = 95;
							while(bala[valor].xposicao > -10) {
								bala[valor].xposicao = bala[valor].xposicao - 10;
								sleep(10);
								

								int lado = 140;							
								if(jogador[OPONENTE].estado != CROUCHING){
									int topo = jogador[OPONENTE].delta + jogador[OPONENTE].underSpace;
									int baixo = topo - jogador[OPONENTE].delta;
									if((bala[valor].xposicao <= lado)&&(bala[valor].yposicao <= topo)&&(bala[valor].yposicao >= baixo)) {
										if(jogador[OPONENTE].vidas > 1){
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("PerdeuVida");
											System.out.println("B PerdeuVida");
											os[OPONENTE].flush();
										}
										else{
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("Morreu");
											System.out.println("B Morreu");
											os[OPONENTE].flush();
											os[ESSE].println("Venceu");
										}
										break;
									}
								}
								else {
									if((bala[valor].yposicao == 95) && (bala[valor].xposicao <= (lado+20)){
										if(jogador[OPONENTE].vidas > 1){
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("PerdeuVida");
											System.out.println("B PerdeuVida");
											os[OPONENTE].flush();
										}
										else{
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("Morreu");
											System.out.println("B Morreu");
											os[OPONENTE].flush();
											os[ESSE].println("Venceu");
										}
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

		class AtirouB extends Thread {

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
							bala[valor].xposicao = 236;
							if(estado != CROUCHING) bala[valor].yposicao = underSpace + 133;
							else bala[valor].yposicao = 95;
							while(bala[valor].xposicao < 1210) {
								bala[valor].xposicao = bala[valor].xposicao + 10;
								sleep(10);
								

								int lado = 970;							
								if(jogador[OPONENTE].estado != CROUCHING){
									int topo = jogador[OPONENTE].delta + jogador[OPONENTE].underSpace;
									int baixo = topo - jogador[OPONENTE].delta;
									if((bala[valor].xposicao >= lado)&&(bala[valor].yposicao <= topo)&&(bala[valor].yposicao >= baixo)) {
										if(jogador[OPONENTE].vidas > 1){
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("PerdeuVida");
											System.out.println("A PerdeuVida");
											os[OPONENTE].flush();
										}
										else{
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("Morreu");
											System.out.println("A Morreu");
											os[OPONENTE].flush();
											os[ESSE].println("Venceu");
										}
										break;
									}
								}
								else {
									if((bala[valor].yposicao == 95) && (bala[valor].xposicao >= (lado+20)){
										if(jogador[OPONENTE].vidas > 1){
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("PerdeuVida");
											System.out.println("A PerdeuVida");
											os[OPONENTE].flush();
										}
										else{
											jogador[OPONENTE].vidas--;
											os[OPONENTE].println("Morreu");
											System.out.println("A Morreu");
											os[OPONENTE].flush();
											os[ESSE].println("Venceu");
										}
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

