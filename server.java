/*
  A server used in the testing of the ddos client.
  accepts connections on port 1337,and keeps them open for 30 seconds.
  It will record all connections (opening and closing) to a file.
*/

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.*;
import java.text.SimpleDateFormat;

public class Server {
    public static String connection_log = "connection_log.txt";
    public static Thread thr1 = new Thread();
    public static int socketHandleTime = 30 * 1000;


    /*
      writes events in the conneciton_log file.
    */
    //open_close_flag == open: 1, close: 0
    private static void logToFile(String connected_client_IP, boolean open_close_flag) {
      //current date and time.
      String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
      String open_close = ((open_close_flag) ? "opened" : "closed");

      try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(connection_log, true)))) {
        out.println(connected_client_IP + " : " + open_close + " : " + date + "");
      }catch (IOException e) {
        System.out.println("Error writing to file.");
      }
    }

    /*
      creates a thread that deals with a clients socket connection.
      This is what allows for multiple clients to connect to the server.
    */
    public static Runnable spawnClientThread(Socket socket) {
      Runnable connectionHandler = new Runnable() {
        public void run() {
          System.out.println("received a connection from: " + socket.getInetAddress());
          logToFile(socket.getInetAddress().getHostAddress(), true);
          try {
            Thread.sleep(socketHandleTime);
            socket.close();
          } catch (Exception e) {}
          if (socket.isClosed()) {
            System.out.println("closed connection from: " + socket.getInetAddress());
            logToFile(socket.getInetAddress().getHostAddress(), false);
          } else {
            System.out.println("WARN: connection remained open.");
          }
        }
      };
      return connectionHandler;
    }

    public static void main(String[] args) throws IOException{
      int portNumber = 1337;
      //start the server on the designated port. 
      ServerSocket serverSocket = new ServerSocket(portNumber);
      System.out.println("Starting the server.");
      while (true) {
        Runnable clientHandler = spawnClientThread(serverSocket.accept());
        new Thread(clientHandler).start();
      }
    }
} //end class.
