import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Server {

	private DatagramPacket sendPacket, recievePacket;
	private DatagramSocket sendSocket, recieveSocket;
	private final int SERVER_PORT_NUM = 69;
	private final int MAX_BYTE_ARRAY_SIZE = 100;
	private final byte[] VALID_READ_REQUEST_RESPONSE = { 0, 3, 0, 1 };
	private final byte[] VALID_WRITE_REQUEST_RESPONSE = { 0, 4, 0, 0 };

	public Server() {
		try {
			sendSocket = new DatagramSocket();
			recieveSocket = new DatagramSocket(SERVER_PORT_NUM);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method recieves a message from the Client via the Host. It then sends a
	 * response back to the Client via the Host.
	 * 
	 * @throws InvalidPacketException
	 */
	private void receiveAndSend() throws InvalidPacketException {

		// Get the message from the Host
		byte data[] = new byte[MAX_BYTE_ARRAY_SIZE];
		recievePacket = new DatagramPacket(data, data.length);
		try {
			System.out.println("Server: Waiting for packet from the Host...");
			recieveSocket.receive(recievePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server: Packet received: ");
		System.out.print("Containing (String): ");
		String received = new String(data, StandardCharsets.UTF_8);
		System.out.println(received);
		System.out.print("Containing (Byte): ");
		printPacketBytes(recievePacket);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Send the response to the Host
		byte[] responseData = parseData(data, recievePacket.getLength());
		sendPacket = new DatagramPacket(responseData, responseData.length, recievePacket.getAddress(),
				recievePacket.getPort());
		System.out.println("Server: Sending packet to Host:");
		System.out.print("Containing (String): ");
		System.out.println(new String(sendPacket.getData(), StandardCharsets.UTF_8));
		System.out.print("Containing (Byte): ");
		printPacketBytes(sendPacket);
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		sendSocket.close();
		recieveSocket.close();
	}

	/**
	 * This method is used to decode the data (in bytes) and determine if it was a
	 * read, write or invalid request. If it is a read/write request, the
	 * appropriate response is returned. If an invalid request is sent,
	 * InvalidPacketException s thrown.
	 * 
	 * @param data          The byte array that contains the message.
	 * @param messageLength The length of the message.
	 * @return A write/read response
	 * @throws InvalidPacketException Thrown if invalid message request sent.
	 */
	private byte[] parseData(byte[] data, int messageLength) throws InvalidPacketException {

		// Verify first 2 positions and last position
		if ((data[0] == 0 && (data[1] == 1 || data[1] == 2)) && data[messageLength - 1] == 0) {
			for (int a = 2; a < messageLength - 1; a++) {
				if (data[a] == 0) {
					for (int b = a + 1; b < messageLength - 1; b++) {
						if (isByteFromText(data[b])) {
							continue;
						} else {
							throw new InvalidPacketException("---INVALID REQUEST SENT TO THE SERVER---");
						}
					}
					if (data[1] == 1) {
						return VALID_READ_REQUEST_RESPONSE;
					}
					return VALID_WRITE_REQUEST_RESPONSE;
				} else if (isByteFromText(data[a])) {
					continue;
				} else {
					throw new InvalidPacketException("---INVALID REQUEST SENT TO THE SERVER---");
				}
			}
		}
		throw new InvalidPacketException("---INVALID REQUEST SENT TO THE SERVER---");

	}

	/**
	 * This method is used to determine if the specific byte is from text or not.
	 * 
	 * @param byteValue
	 * @return True if the byte value belongs to text, False otherwise.
	 */
	private boolean isByteFromText(byte byteValue) {
		if (byteValue >= 32 && byteValue <= 126) {
			return true;
		}
		return false;
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

	public static void main(String args[]) throws InvalidPacketException {
		while (true) {
			Server server = new Server();
			server.receiveAndSend();
		}

	}
}
