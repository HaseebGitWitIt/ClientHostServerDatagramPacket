public class InvalidPacketException extends Exception {
	/**
	 * This exception gets thrown by the Server if it recieves an invalid message
	 * request.
	 */
	private static final long serialVersionUID = -4320129700546064238L;

	public InvalidPacketException(String errorMessage) {
		super(errorMessage);
	}
}