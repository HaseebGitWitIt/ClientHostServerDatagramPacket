import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client {

	private DatagramPacket sendPacket, recievePacket;
	private DatagramSocket sendRecieveSocket;
	private final int HOST_PORT_NUM = 23;
	private final int MAX_BYTE_ARRAY_SIZE = 100;
	private final String READ_REQUEST = "READ";
	private final String WRITE_REQUEST = "WRITE";
	private final int TIMEOUT_TIME = 30000;
	private final int CANCEL_TIMEOUT = 0;

	public Client() {
		try {
			sendRecieveSocket = new DatagramSocket();
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

		for (int a = 0; a <= 10; a++) {
			// Send the message to the Host
			byte msg[] = null;
			if (a == 10) {
				msg = getRequestMessage("Invalid request");
			} else if (a % 2 == 0) {
				msg = getRequestMessage(READ_REQUEST);
			} else {
				msg = getRequestMessage(WRITE_REQUEST);
			}
			try {
				sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), HOST_PORT_NUM);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Client: Sending packet #" + a + " to the Host:");
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

			// Recieve the response from the Host
			byte data[] = new byte[MAX_BYTE_ARRAY_SIZE];
			recievePacket = new DatagramPacket(data, data.length);
			try {
				System.out.println("Client: Waiting for response from Host...");
				sendRecieveSocket.setSoTimeout(TIMEOUT_TIME);
				sendRecieveSocket.receive(recievePacket);
				sendRecieveSocket.setSoTimeout(CANCEL_TIMEOUT);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Client: Packet received for message #" + a + " from Host:");
			System.out.print("Containing (String): ");
			String received = new String(data, StandardCharsets.UTF_8);
			System.out.println(received);
			System.out.print("Containing (Byte): ");
			printPacketBytes(recievePacket);
		}

		sendRecieveSocket.close();
	}

	/**
	 * This method is used to generate the message request to be sent to the Server
	 * via the Host.
	 * 
	 * @param readOrWrite If we are creating a read requiest or a write request
	 * @return The byte array of the message to be send to the Host
	 */
	private byte[] getRequestMessage(String readOrWrite) {
		String filename = "test.txt";
		String mode = "ocTEt";
		byte readBytes[] = { 0, 1 };
		byte writeBytes[] = { 0, 2 };
		byte corruptBytes[] = { 1, 2 };
		byte filenameBytes[] = filename.getBytes();
		byte modeBytes[] = mode.getBytes();
		byte zero[] = { 0 };

		if (readOrWrite.equalsIgnoreCase(READ_REQUEST)) {
			return combineFiveByteArrays(readBytes, filenameBytes, zero, modeBytes, zero);
		} else if (readOrWrite.equalsIgnoreCase(WRITE_REQUEST)) {
			return combineFiveByteArrays(writeBytes, filenameBytes, zero, modeBytes, zero);
		} else {
			return combineFiveByteArrays(corruptBytes, filenameBytes, zero, modeBytes, zero);
		}

	}

	/**
	 * This method is used to combine 5 byte arrays to create one byte array.
	 * 
	 * @param array1
	 * @param array2
	 * @param array3
	 * @param array4
	 * @param array5
	 * @return The byte array composed of all 5 arrays.
	 */
	private byte[] combineFiveByteArrays(byte[] array1, byte[] array2, byte[] array3, byte[] array4, byte[] array5) {
		int length = array1.length + array2.length + array3.length + array4.length + array5.length;
		byte[] byteArray = new byte[length];
		System.arraycopy(array1, 0, byteArray, 0, array1.length);
		System.arraycopy(array2, 0, byteArray, array1.length, array2.length);
		System.arraycopy(array3, 0, byteArray, array1.length + array2.length, array3.length);
		System.arraycopy(array4, 0, byteArray, array1.length + array2.length + array3.length, array4.length);
		System.arraycopy(array5, 0, byteArray, array1.length + array2.length + array3.length + array4.length,
				array5.length);
		return byteArray;
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
		Client client = new Client();
		client.sendAndReceive();
	}
}
