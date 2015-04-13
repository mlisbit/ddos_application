import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.text.SimpleDateFormat;

public class attacker {

  //internal states of attacker.
  private final int MONITORING = 0;
  private final int ATTACKING = 1;

  //configs
  private int number_of_connection_threads = 1;
  private String start_time = "";
  private String attack_length_seconds = "";
  public static String server_ip_string;

  private static int monitoring_port;
  private static int attacking_port;
  private static Socket socket;

  private static void parseConfigs() {

  }

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
      System.out.println(Calendar.ZONE_OFFSET / (60 * 1000));
      //System.out.println((now.get(Calendar.ZONE_OFFSET) + now.get(Calendar.DST_OFFSET)) / (60 * 1000));
    } catch (Exception e) {}
    return now;
  }

  private static String addSecondsToTime(String seconds) {
    return null;
  }

  private static void openConnection(String attacking_ip, int attacking_port) {
    Inet4Address server_ip_inet;

    try {
      server_ip_inet = (Inet4Address) Inet4Address.getByAddress(convertStringToByteArrayIP(attacking_ip));
    } catch (Exception e) {
      System.out.println("Invalid Host name");
      return;
    }

    try {
      socket = new Socket(server_ip_inet, attacking_port);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to " + attacking_ip + " on port " + attacking_port );
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

  public static void main(String[] args) throws IOException {
    //two modes, monitoring mode, and attacking mode.
    //monitoring mode
      //get the information from the coordinator
      //parse information, including converting the IP to a byte array.
    //attack mode.
    openConnection("127.0.0.1", 1337);
    while (socket.getInputStream().read() != -1 ){}
    socket.close();
    System.out.println("Socket was closed");
    System.out.println(convertStringToTime("07:07:07").getTime());
  }
}
