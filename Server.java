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

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
        System.out.println("Accept failed: " + 80 + ", " + e);
        System.exit(1);
        }

        System.out.println("Accept Funcionou!");

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
    static PrintStream os;
    JogadorCliente jogador = new JogadorCliente();

    Servindo(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
        Scanner is = new Scanner(clientSocket.getInputStream());
        os = new PrintStream(clientSocket.getOutputStream());
        String inputLine, outputLine;

        do {
            inputLine = is.nextLine();
            switch (inputLine) {
					case "Pular":
						//verificar aqui se já não ta pulando ou no cliente?
						os.println("Pulou");
						new Pulou().start();
            			os.flush();
						break;
					case "Abaixar":
						os.println("Abaixou");
            			os.flush();
						break;
					case "Estado":
					    inputLine = is.nextLine();

				}
        } while (!inputLine.equals(""));

        os.close();
        is.close();
        clientSocket.close();

        } catch (IOException e) {
        e.printStackTrace();
        } catch (NoSuchElementException e) {
        System.out.println("Conexacao terminada pelo cliente");
        }
    }

    class Abaixou extends Thread {
		public void run() {
			jogadorA.estado = jogadorA.CROUCHING;
		}
	}
    
    class Pulou extends Thread{
		int y = jogador.underSpace;
		final int MAX = jogador.DELTA;
		public void run(){
			jogador.estado = jogador.STANDING;
			while(jogador.underSpace < MAX){
				jogador.underSpace++;
				try{
					sleep(1);
				} catch (InterruptedException e) {};
			}
			while(jogador.underSpace > y){
				jogador.underSpace--;
				try{
					sleep(1);
				} catch (InterruptedException e) {};
			}
		}
	}
	
}



class JogadorCliente {
	
	final int STANDING    = 0;
	final int CROUCHING   = 1;
	int vidas = 5;
	int estado = STANDING;
	int xposicao = 972;
	int yposicao = 548;
	int underSpace = 20;
	final int DELTA = 262;
	
	
	JogadorCliente() {
	}
}

