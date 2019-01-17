Client.java: Where the initial message is generated and sent to the Host. The client eventually recieves a response for its iniital message.
Host.java: The messenger between the client and server. The client and server are unaware of eachother, and only know of the host.
Server.java: Where the initial message is recieved and response is created. The response is dependent on whether the initial message was a read request or a write request. If it was an invalid request, InvalidPacketException is thrown.
InvalidPacketException.java: The exception that gets thrown if the server recieves an invalid request.

HOW TO RUN
1. Import the source files into your Eclipse Java project
2. Run the Server.java file as a Java application
3. Run the Host.java file as a Java application
4. Run the Client.java file as a Java application

NOTE: The last message (11th) sent from the client (packet #10 because start counting from 0) is an invalid request. Therefore:
Server.java: InvalidPacketException is thrown.
Host.java: After about 30 seconds, SocketTimeoutException is thrown.
Client.java: After about 30 seconds, SocketTimeoutException is thrown.
