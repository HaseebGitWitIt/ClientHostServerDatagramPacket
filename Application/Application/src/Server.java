import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Server {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;

	final int serverPortNum = 69;
	final int maxByteArraySize = 100;

	public Server() {
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(serverPortNum);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method recieves a message from the Client via the Host. It then sends a
	 * response back to the Client via the Host.
	 */
	public void receiveAndSend() {

		// Get the message from the Host
		byte data[] = new byte[maxByteArraySize];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Server: Waiting for Packet from the Host.");
		try {
			System.out.println("Waiting...");
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String dateFormatted = formatter.format(date);
		System.out.println("Server: Packet received at " + dateFormatted + " :");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");
		String received = new String(data, 0, len);
		System.out.println(received + "\n");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Send the response to the Host
		sendPacket = new DatagramPacket(data, receivePacket.getLength(), receivePacket.getAddress(),
				receivePacket.getPort());
		System.out.println("Server: Sending packet to Host:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");
		System.out.println(new String(sendPacket.getData(), 0, len));
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		date = new Date();
		dateFormatted = formatter.format(date);
		System.out.println("Server: packet sent at " + dateFormatted);
		sendSocket.close();
		receiveSocket.close();
	}

	public static void main(String args[]) {
		Server server = new Server();
		server.receiveAndSend();
	}
}
