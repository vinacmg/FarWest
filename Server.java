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
    JogadorCliente jogador;
    static int cont = 0;
    final int ESSE, OPONENTE;

    Servindo(Socket[] clientSocket) {
    	this.clientSocket = clientSocket[cont];
    	if(cont == 0){
    		jogador = new JogadorCliente1();
			ESSE = 0;
    		OPONENTE = 1;
    	}
    	else{
    		jogador = new JogadorCliente2();
			ESSE = 1;
    		OPONENTE = 0;
    	}
		
    	cont++;
    }

    public void run() {
        try {
        Scanner is = new Scanner(clientSocket.getInputStream());
        os[ESSE] = new PrintStream(clientSocket.getOutputStream());
        String inputLine, outputLine, oponentLine;

        do {
            inputLine = is.nextLine();
            switch (inputLine) {
					case "Pular":
						os[ESSE].println("Pulou");
            			os[ESSE].flush();
            			os[OPONENTE].println("OponentePulou");
        				os[OPONENTE].flush();
						jogador.pulou();
						break;
					case "Abaixar":
						os[ESSE].println("Abaixou");
            			os[ESSE].flush();
            			os[OPONENTE].println("OponenteAbaixou");
        				os[OPONENTE].flush();
						jogador.estado = jogador.CROUCHING;
						break;
					case "Levantar":
						os[ESSE].println("Levantou");
            			os[ESSE].flush();
            			os[OPONENTE].println("OponenteLevantou");
        				os[OPONENTE].flush();
						jogador.estado = jogador.STANDING;
						break;
					case "Estado":
					    inputLine = is.nextLine();
					case "Atirar":
						os[ESSE].println("Atirou"); //colocar rotina atirar
            			os[ESSE].flush();
            			os[OPONENTE].println("OponenteAtirou");
        				os[OPONENTE].flush();
            			break;
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
	
	/*
	class Oponente extends Thread{
		String inputLine;

		Oponente(String s){
			inputLine = s;
		}

		public void run(){
        	switch (inputLine) {
				case "Pular":
					os[OPONENTE].println("OponentePulou");
        			os[OPONENTE].flush();
					break;
				case "Abaixar":
					os[OPONENTE].println("OponenteAbaixou");
        			os[OPONENTE].flush();
					break;
				case "Levantou":
					os[OPONENTE].println("OponenteLevantou");
        			os[OPONENTE].flush();
					break;
				case "Estado":
				case "Atirar":
					os[OPONENTE].println("OponenteAtirou");
        			os[OPONENTE].flush();
        			break;
			}
		}
	}*/
}


class JogadorCliente {
	
	final int STANDING    = 0;
	final int CROUCHING   = 1;
	int vidas = 5;
	int estado = STANDING;
	int x;
	int y;
	int underSpace = 20;
	int delta = 262;
	
	
	JogadorCliente() {
	}

	void pulou(){
		new Pulou().start();
	}
    
    class Pulou extends Thread{
		int y = underSpace;
		final int MAX = delta;
		public void run(){
			estado = STANDING;
			while(underSpace < MAX){
				underSpace++;
				try{
					sleep(1);
				} catch (InterruptedException e) {};
			}
			while(underSpace > y){
				underSpace--;
				try{
					sleep(1);
				} catch (InterruptedException e) {};
			}
		}
	}
}

class JogadorCliente1 extends JogadorCliente{
	int x = 890;
	int y = 413;
}

class JogadorCliente2 extends JogadorCliente{
	int x = 0; //300 de largura
	int y = 413;
}

