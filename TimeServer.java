import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class TimeServer implements Runnable {

private DatagramSocket socket;
        
 public TimeServer(int port) throws SocketException {
  socket = new DatagramSocket(port);
  socket.setSoTimeout(1000); // 1000ms = 1s
 }
 
 public void run() {
  DatagramPacket answer;
  byte[] buffer = new byte[1024]; // creazione di un array di byte della dimensione specificata
  ByteBuffer data;
  DatagramPacket request = new DatagramPacket(buffer, buffer.length); // creazione di un datagram UDP
 
  while (!Thread.interrupted()) {
   try {
    socket.receive(request); // attesa ricezione datagram di richiesta
    // determinazione timestamp
    Date now = new Date();
    long timestamp = now.getTime()/1000;
    // costruzione datagram di risposta
    data = ByteBuffer.wrap(buffer, 0, 8);
    data.putLong(timestamp);
    answer = new DatagramPacket(data.array(), 8, request.getAddress(), request.getPort());
    socket.send(answer); // trasmissione datagram di risposta
   }
   catch (IOException exception) {}
  }
 }

 public static void main(String[] args) throws IOException {
  int c;
  Thread thread;
        
  try {
   TimeServer udp_echo = new TimeServer(12345);
   thread = new Thread(udp_echo);
   thread.start();
   c = System.in.read();
   thread.interrupt();
   thread.join();
  }
  catch (SocketException exception) {
   System.err.println("Errore!");
  }
  catch (InterruptedException exception) {
   System.err.println("Fine.");
  }
 }    
}
