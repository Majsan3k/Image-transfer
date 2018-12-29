/**
 * A tread that always is listening for new messages from a server.
 *
 * @author Maja Lund
 */

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private ImageServer server;
    private Socket socket;
    private ObjectOutputStream objOut;

    /**
     * Creates a new ClientHandler
     * @param clientSocket
     * @param server
     */
    public ClientHandler(Socket clientSocket, ImageServer server){
        this.server = server;
        this.socket = clientSocket;
    }

    public void write(Object obj) throws IOException {
        objOut.writeObject(obj);
        objOut.flush();
    }

    /**
     * Listen after new messages from the ObjectInputStream and send it to
     * the server to broadcast if anything is received.
     */
    @Override
    public void run() {
        ObjectInputStream inDataFromClient = null;
        try {
            inDataFromClient = new ObjectInputStream(socket.getInputStream());
            objOut = new ObjectOutputStream(socket.getOutputStream());
            server.addClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object obj;
        try {
            while ((obj = inDataFromClient.readObject()) != null) {
                server.broadCast(obj);
            }
            inDataFromClient.close();
            close();
        } catch (IOException e) {
            System.out.println(socket.getInetAddress() + " disconnected");
            close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            close();
        }
    }

    /**
     * Closes socket and ObjectOutPutStream.
     * Removes client from server.
     */
    private void close(){
        try {
            socket.close();
            objOut.close();
            server.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
