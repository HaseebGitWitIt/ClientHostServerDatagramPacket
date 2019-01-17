import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Host {

	private DatagramPacket sendPacket, clientRecievePacket, serverRecievePacket;
	private DatagramSocket recieveSocket, sendRecieveSocket;
	private final int HOST_PORT_NUM = 23;
	private final int MAX_BYTE_ARRAY_SIZE = 100;
	private final int SERVER_PORT_NUM = 69;
	private final int TIMEOUT_TIME = 30000;
	private final int CANCEL_TIMEOUT = 0;

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
		try {
			System.out.println("Host: Waiting for packet from Client...");
			recieveSocket.receive(clientRecievePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Host: Packet received: ");
		System.out.print("Containing (String): ");
		String received = new String(data, StandardCharsets.UTF_8);
		System.out.println(received);
		System.out.print("Containing (Byte): ");
		printPacketBytes(clientRecievePacket);
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
		System.out.print("Containing (String): ");
		System.out.println(new String(sendPacket.getData(), StandardCharsets.UTF_8));
		System.out.print("Containing (Byte): ");
		printPacketBytes(sendPacket);
		try {
			sendRecieveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Recieve response from Server
		data = new byte[MAX_BYTE_ARRAY_SIZE];
		serverRecievePacket = new DatagramPacket(data, data.length);
		try {
			System.out.println("Host: Waiting for response from Server...");
			sendRecieveSocket.setSoTimeout(TIMEOUT_TIME);
			sendRecieveSocket.receive(serverRecievePacket);
			sendRecieveSocket.setSoTimeout(CANCEL_TIMEOUT);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Host: Packet received:");
		System.out.print("Containing (String): ");
		received = new String(data, StandardCharsets.UTF_8);
		System.out.println(received);
		System.out.print("Containing (Byte): ");
		printPacketBytes(serverRecievePacket);
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
		System.out.print("Containing (String): ");
		System.out.println(new String(sendPacket.getData(), StandardCharsets.UTF_8));
		System.out.print("Containing (Byte): ");
		printPacketBytes(sendPacket);
		try {
			sendRecieveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		recieveSocket.close();
		sendRecieveSocket.close();
	}

	/**
	 * This method is used to display the bytes of the message sent or recieved
	 * (only show message length).
	 * 
	 * @param bytes DatagramPacket containing the message data to be displayed
	 */
	private void printPacketBytes(DatagramPacket bytes) {
		byte bytesArray[] = bytes.getData();
		System.out.print("[");
		for (int a = 0; a < bytes.getLength(); a++) {
			if (a != (bytes.getLength() - 1)) {
				System.out.print(bytesArray[a] + ", ");
			} else {
				System.out.print(bytesArray[a]);
			}
		}
		System.out.println("] \n");
	}

	public static void main(String args[]) {
		while (true) {
			Host host = new Host();
			host.recieveAndSend();
		}

	}
}