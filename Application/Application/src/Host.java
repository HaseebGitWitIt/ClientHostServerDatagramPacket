import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class Host {

	private DatagramPacket sendPacket, clientRecievePacket, serverRecievePacket;
	private DatagramSocket recieveSocket, sendRecieveSocket;

	private final int HOST_PORT_NUM = 23;
	private final int MAX_BYTE_ARRAY_SIZE = 100;
	private final int SERVER_PORT_NUM = 69;

	public Host() {
		try {
			recieveSocket = new DatagramSocket(HOST_PORT_NUM);
			sendRecieveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method recieves a message from the Client, and then sends it to the
	 * Server. The Server then sends a response, which is then sent to the Client.
	 */
	private void recieveAndSend() {

		// Get the message from the Client
		byte data[] = new byte[MAX_BYTE_ARRAY_SIZE];
		clientRecievePacket = new DatagramPacket(data, data.length);
		System.out.println("Host: Waiting for Packet from Client.");
		try {
			System.out.println("Waiting...");
			recieveSocket.receive(clientRecievePacket);
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
		System.out.println("Host: Packet received at " + dateFormatted + " :");
//		System.out.println("From host: " + clientRecievePacket.getAddress());
//		System.out.println("Host port: " + clientRecievePacket.getPort());
//		int len = clientRecievePacket.getLength();
//		System.out.println("Length: " + len);
		System.out.print("Containing (String): ");
		String received = new String(data, StandardCharsets.UTF_8);
		System.out.println(received);
		System.out.print("Containing (Byte): ");
		System.out.println(Arrays.toString(data) + "\n");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Send the message from the Client to the Server
		sendPacket = new DatagramPacket(data, clientRecievePacket.getLength(), clientRecievePacket.getAddress(),
				SERVER_PORT_NUM);
		System.out.println("Host: Sending packet to Server:");
//		System.out.println("To host: " + sendPacket.getAddress());
//		System.out.println("Destination host port: " + sendPacket.getPort());
//		len = sendPacket.getLength();
//		System.out.println("Length: " + len);
		System.out.print("Containing (String): ");
		System.out.println(new String(sendPacket.getData(), StandardCharsets.UTF_8));
		System.out.print("Containing (Byte): ");
		System.out.println(Arrays.toString(sendPacket.getData()));
		try {

			sendRecieveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		date = new Date();
		dateFormatted = formatter.format(date);
		System.out.println("Server: packet sent at " + dateFormatted + "\n");

		// Recieve response from Server
		data = new byte[MAX_BYTE_ARRAY_SIZE];
		serverRecievePacket = new DatagramPacket(data, data.length);
		System.out.println("Host: Waiting for Packet from Server.");
		try {
			System.out.println("Waiting...");
			sendRecieveSocket.receive(serverRecievePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		date = new Date();
		formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		dateFormatted = formatter.format(date);
		System.out.println("Host: Packet received at " + dateFormatted + " :");
//		System.out.println("From host: " + serverRecievePacket.getAddress());
//		System.out.println("Host port: " + serverRecievePacket.getPort());
//		len = serverRecievePacket.getLength();
//		System.out.println("Length: " + len);
		System.out.print("Containing (String): ");
		received = new String(data, StandardCharsets.UTF_8);
		System.out.println(received);
		System.out.print("Containing (Byte): ");
		System.out.println(Arrays.toString(data) + "\n");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Send the response from the Server to the Client
		sendPacket = new DatagramPacket(data, serverRecievePacket.getLength(), serverRecievePacket.getAddress(),
				clientRecievePacket.getPort());
		System.out.println("Host: Sending packet to Client:");
//		System.out.println("To host: " + sendPacket.getAddress());
//		System.out.println("Destination host port: " + sendPacket.getPort());
//		len = sendPacket.getLength();
//		System.out.println("Length: " + len);
		System.out.print("Containing (String): ");
		System.out.println(new String(sendPacket.getData(), StandardCharsets.UTF_8));
		System.out.print("Containing (Byte): ");
		System.out.println(Arrays.toString(sendPacket.getData()));
		try {
			sendRecieveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		date = new Date();
		dateFormatted = formatter.format(date);
		System.out.println("Server: packet sent at " + dateFormatted + "\n");

		recieveSocket.close();
		sendRecieveSocket.close();
	}

	public static void main(String args[]) {
		while (true) {
			Host host = new Host();
			host.recieveAndSend();
		}

	}
}
