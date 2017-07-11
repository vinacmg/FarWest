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
        
    	new Servindo(clientSocket).start();


        try {
            serverSocket.close();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}

class Servindo extends Thread {
    Socket clientSocket;
    Socket oponentSocket;
    PrintStream os, osOponent;
    JogadorCliente jogador;
    static int cont = 0;

    Servindo(Socket[] clientSocket) {
    	this.clientSocket = clientSocket[cont];
    	if(cont == 0){
    		jogador = new JogadorCliente1();
    		//oponentSocket = clientSocket[1];
    	}
    	else{
    		jogador = new JogadorCliente2();
    		//oponentSocket = clientSocket[0];
    	}
    	cont++;
    }

    public void run() {
        try {
        Scanner is = new Scanner(clientSocket.getInputStream());
        //osOponent = new PrintStream(oponentSocket.getOutputStream());
        os = new PrintStream(clientSocket.getOutputStream());
        String inputLine, outputLine;

        do {
            inputLine = is.nextLine();
            switch (inputLine) {
					case "Pular":
						os.println("Pulou");
            			os.flush();
            			//new Oponente(inputLine).start();
						jogador.pulou();
						break;
					case "Abaixar":
						os.println("Abaixou");
            			os.flush();
            			//new Oponente(inputLine).start();
						jogador.estado = jogador.CROUCHING;
						break;
					case "Levantou":
						jogador.estado = jogador.STANDING;
						break;
					case "Estado":
					    inputLine = is.nextLine();
					case "Atirar":
						os.println("Atirou"); //colocar rotina atirar
            			os.flush();
            			//new Oponente(inputLine).start();
            			break;
				}
        } while (!inputLine.equals(""));

        os.close();
        is.close();
        //osOponent.close();
        clientSocket.close();
        //oponentSocket.close();

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
					osOponent.println("OponentePulou");
        			osOponent.flush();
					break;
				case "Abaixar":
					osOponent.println("OponenteAbaixou");
        			osOponent.flush();
					break;
				case "Levantou":
					osOponent.println("OponenteLevantou");
        			osOponent.flush();
					break;
				case "Estado":
				case "Atirar":
					osOponent.println("OponenteAtirou");
        			osOponent.flush();
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

