import java.net.*;
import java.io.*;
import java.util.*;

public class CoordinatingProtocol {

  private static final int WAITING = 0;
  private static final int GETTING_VICTIM_IP = 1;
  private static final int GETTING_VICTIM_PORT = 2;
  private static final int GETTING_VICTIM_TIME = 3;
  private static final int DONE = 4;

  private static String victim_ip = "";
  private static int victim_port = 0;
  private static String victim_time = "";

  private int state = WAITING; //current state the user is on.

  public CoordinatingProtocol(String victim_ip, int victim_port, String victim_time) {
    this.victim_ip = victim_ip;
    this.victim_port = victim_port;
    this.victim_time = victim_time;
  }


  public String processInput() {
    String theOutput = null;
    if (state == WAITING) {
        theOutput = "Hello.";
        state = GETTING_VICTIM_IP;
    } else if (state == GETTING_VICTIM_IP) {
      theOutput = victim_ip;
      state = GETTING_VICTIM_PORT;
    } else if (state == GETTING_VICTIM_PORT) {
      theOutput = Integer.toString(victim_port);
      state = GETTING_VICTIM_TIME;
    } else if (state == GETTING_VICTIM_TIME) {
      theOutput = victim_time;
      state = DONE;
    } else if (state == DONE) {
      theOutput = "Bye.";
      state = WAITING;
    }
    return theOutput;
  } //end process input.
}
