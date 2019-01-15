import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Client {

	private DatagramPacket sendPacket, recievePacket;
	private DatagramSocket sendReceiveSocket;
	private final int HOST_PORT_NUM = 23;
	private final int MAX_BYTE_ARRAY_SIZE = 100;

	public Client() {
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method generates a message to send to the Server via the Host. The
	 * Server then sends a response back via the Host.
	 */
	private void sendAndReceive() {

		// Send the message to the Host
		String s = "Anyone there?";
		System.out.println("Client: Sending packet to Host containing: " + s);
		byte msg[] = s.getBytes();
		try {
			sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), HOST_PORT_NUM);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Client: Sending packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");
		System.out.println(new String(sendPacket.getData(), 0, len));
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String dateFormatted = formatter.format(date);
		System.out.println("Client: Packet sent at: " + dateFormatted + "\n");

		// Recieve the response from the Host
		byte data[] = new byte[MAX_BYTE_ARRAY_SIZE];
		recievePacket = new DatagramPacket(data, data.length);
		try {
			sendReceiveSocket.receive(recievePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		date = new Date();
		dateFormatted = formatter.format(date);
		System.out.println("Client: Packet received at " + dateFormatted + " :");
		System.out.println("From host: " + recievePacket.getAddress());
		System.out.println("Host port: " + recievePacket.getPort());
		len = recievePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");
		String received = new String(data, 0, len);
		System.out.println(received);

		sendReceiveSocket.close();
	}

	public static void main(String args[]) {
		Client client = new Client();
		client.sendAndReceive();
	}
}
