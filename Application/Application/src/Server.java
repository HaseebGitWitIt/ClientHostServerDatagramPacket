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

public class Server {

	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;

	private final int SERVER_PORT_NUM = 69;
	private final int MAX_BYTE_ARRAY_SIZE = 100;
	private final byte[] VALID_READ_REQUEST_RESPONSE = { 0, 3, 0, 1 };
	private final byte[] VALID_WRITE_REQUEST_RESPONSE = { 0, 4, 0, 0 };

	public Server() {
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(SERVER_PORT_NUM);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method recieves a message from the Client via the Host. It then sends a
	 * response back to the Client via the Host.
	 */
	private void receiveAndSend() {

		// Get the message from the Host
		byte data[] = new byte[MAX_BYTE_ARRAY_SIZE];
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
//		System.out.println("From host: " + receivePacket.getAddress());
//		System.out.println("Host port: " + receivePacket.getPort());
//		int len = receivePacket.getLength();
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

		// Send the response to the Host
		byte[] responseData = parseData(data);
		sendPacket = new DatagramPacket(responseData, responseData.length, receivePacket.getAddress(),
				receivePacket.getPort());
		System.out.println("Server: Sending packet to Host:");
//		System.out.println("To host: " + sendPacket.getAddress());
//		System.out.println("Destination host port: " + sendPacket.getPort());
//		len = sendPacket.getLength();
//		System.out.println("Length: " + len);
		System.out.print("Containing (String): ");
		System.out.println(new String(sendPacket.getData(), StandardCharsets.UTF_8));
		System.out.print("Containing (Byte): ");
		System.out.println(Arrays.toString(sendPacket.getData()));
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		date = new Date();
		dateFormatted = formatter.format(date);
		System.out.println("Server: packet sent at " + dateFormatted + "\n");
		sendSocket.close();
		receiveSocket.close();
	}

	private byte[] parseData(byte[] data) {
		if (data[0] == 0 && (data[1] == 1 || data[1] == 2)) {
			for (int a = 2; a < data.length; a++) {
				if (data[a] == 0) {
					// Filename complete
					for (int b = a; b < data.length; b++) {
						if (data[b] == 0) {
							// Mode complete, nothing else after that
							if (data[1] == 1) {
								return VALID_READ_REQUEST_RESPONSE;
							} else {
								return VALID_WRITE_REQUEST_RESPONSE;
							}
						}
					}

					System.out.println("\n\n---INVALID REQUEST SENT TO THE SERVER---\n");
					System.exit(0);
				}
			}
		}
		System.out.println("\n\n---INVALID REQUEST SENT TO THE SERVER---\n");
		System.exit(0);
		return null;
	}

	public static void main(String args[]) {
		while (true) {
			Server server = new Server();
			server.receiveAndSend();
		}

	}
}
