/**
 * Server for sending images that communicates over Stream Sockets.
 *
 * @author Maja Lund
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ImageServer implements Runnable{

    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private ServerSocket serverSocket;

    /**
     * Open new serverSocket
     * @param port socket port
     */
    public ImageServer(int port){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Couldn't connect. " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Add new client to client list
     * @param ch new client
     */
    public void addClient(ClientHandler ch){
        clientHandlers.add(ch);
    }

    /**
     * Removes client from client list
     * @param ch client to be deleted
     */
    public void removeClient(ClientHandler ch){
        clientHandlers.remove(ch);
    }

    /**
     * Send specified object to all connected clients
     * @param obj object to be sent
     * @throws IOException
     */
    public void broadCast(Object obj) throws IOException{
        for(ClientHandler ch : clientHandlers){
            ch.write(obj);
        }
    }

    /**
     * Listens after new clients that will connect. If anyone connects a broadcast message will be send
     * to all connected sockets with information that a new client has been connected.
     */
    @Override
    public void run() {
        try{
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Connected" + socket.getInetAddress());
                Thread clientThread = new Thread(new ClientHandler(socket, this));
                clientThread.start();
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Starts program. Socket port will be 2000 if the user doesn't speecify anything else.
     * @param args
     */
    public static void main(String args[]){
        int port = 2000;

        if(args.length > 0){
            port = Integer.parseInt(args[0].toString());
        }

        ImageServer imageServer = new ImageServer(port);
        Thread thread = new Thread(imageServer);
        thread.start();
    }
}
