import java.net.*;
import java.io.*;
import java.util.*;

public class coordinator {
  public static int my_port = 2337;
  public static String victim_ip = "127.0.0.1";
  public static int victim_port = 1337;
  public static String victim_time = "07:07:07";

  public static Runnable spawnAttackServer(Socket socket) {
    Runnable attackerHandler = new Runnable() {
      public void run() {
        System.out.println("attack node connected.");
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine;
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


  public static void main(String[] args) throws IOException {
    try {
      ServerSocket serverSocket = new ServerSocket(my_port);
       while (true) {
         Runnable clientHandler = spawnAttackServer(serverSocket.accept());
         new Thread(clientHandler).start();
       }
     } catch (Exception e) {
       System.out.println("Error.");
     }

    //show the current number of attackers you have access to.
    //get the input from the user (target IP address, attack time range, attack time)
    //send that information out in a broadcast for attackers to get.
  }
}
