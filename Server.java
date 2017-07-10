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
        


        try {
            serverSocket.close();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}

class Servindo extends Thread {
    Socket clientSocket;
    PrintStream os;
    Jogador1 jogador = new Jogador1();
    static int cont = 0;

    Servindo(Socket[] clientSocket) {
    	final int ATUAL = cont;
    	cont++;
        this.clientSocket = clientSocket[ATUAL];
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
						os.println("Pulou");
						jogador.pulou();
            			os.flush();
						break;
					case "Abaixar":
						os.println("Abaixou");
            			os.flush();
						jogador.estado = jogador.CROUCHING;
						break;
					case "Levantou":
						jogador.estado = jogador.STANDING;
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
	
}



class Jogador1 {
	
	final int STANDING    = 0;
	final int CROUCHING   = 1;
	int vidas = 5;
	int estado = STANDING;
	int xposicao = 972;
	int yposicao = 548;
	int underSpace = 20;
	int delta = 262;
	
	
	Jogador1() {
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

class Jogador2 extends Jogador1{

}

