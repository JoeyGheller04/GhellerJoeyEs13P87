import java.io.*;
import java.net.*;
import java.nio.*;

public class TimeClient {
 private DatagramSocket socket;

 public TimeClient() throws SocketException {
  socket = new DatagramSocket();
  socket.setSoTimeout(1000); // 1000ms = 1s
 }

 public void closeSocket() { socket.close(); }
 
 public long getTime(String host, int port) throws UnknownHostException, IOException, SocketTimeoutException {
  ByteBuffer input, output;
  byte[] buffer = new byte[1024];
  DatagramPacket datagram;
  InetAddress address = InetAddress.getByName(host); // indirizzo IP del destinatario del datagram
  
  if (socket.isClosed()) { throw new IOException(); } // verifica chiusura socket
  output = ByteBuffer.allocate(2);
  output.putChar('?');
  datagram = new DatagramPacket(output.array(), 2, address, port); // costruzione datagram di richiesta
  socket.send(datagram); // trasmissione datagram di richiesta
  datagram = new DatagramPacket(buffer, buffer.length); // costruzione datagram di risposta
  socket.receive(datagram); // attesa ricezione datagram di richiesta (tempo massimo di attesa: 1s)
  // verifica indirizzo/porta provenienza datagram di risposta
  if (datagram.getAddress().equals(address) && datagram.getPort() == port) {
      // estrazione di 1 valore long dal datagram di risposta
      input = ByteBuffer.wrap(datagram.getData());
      return input.getLong();
  }
  else { throw new SocketTimeoutException(); }
 }

 public static void main(String args[]) {
  String IP_address;
  int UDP_port = 12345;
  long timestamp;
  TimeClient udp_request;
    
  if (args.length != 1) {
   System.err.println("Errore parametri forniti!");
   return;
  }

  IP_address = args[0];
    
  try {
   udp_request = new TimeClient();
   timestamp = udp_request.getTime(IP_address, UDP_port);
   System.out.println("Risposta: " + timestamp);
   udp_request.closeSocket();
  }
  catch(SocketException exception) { System.err.println("Errore creazione socket!"); }
  catch (UnknownHostException exception) { System.err.println("Indirizzo IP errato!"); }
  catch (SocketTimeoutException exception) { System.err.println("Nessuna risposta dal server!"); }
  catch (IOException exception) { System.err.println("Errore generico di comunicazione!"); }
 }
}
