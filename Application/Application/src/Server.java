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

	public Server() {
		try {
			sendSocket = new DatagramSocket();

			receiveSocket = new DatagramSocket(5000);

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	public void receiveAndEcho() {

		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Server: Waiting for Packet.\n");

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

		sendPacket = new DatagramPacket(data, receivePacket.getLength(), receivePacket.getAddress(),
				receivePacket.getPort());

		System.out.println("Server: Sending packet:");
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
		server.receiveAndEcho();
	}
}
