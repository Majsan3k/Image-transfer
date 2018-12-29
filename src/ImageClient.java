//Då jag inte använder mig av klassen Storage i denna uppgift har jag byggt en egen server. 
//Den ligger i mappen Server och behöver startas innan programmet körs.

/**
 * A program for sending and receiving images
 *
 * @author Maja Lund
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ImageClient extends JFrame implements Runnable{

    private ObjectOutputStream objOut;
    private Socket socket;
    private JLabel imageView;

    /**
     * Creates a GUI and a socket and opens a ObjectOutputStream on the socket.
     *
     * @param host socket host
     * @param port socket port
     */
    public ImageClient(String host, int port){

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem getImage = new JMenuItem("Choose Image");
        getImage.addActionListener(e -> sendImage());
        menu.add(getImage);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        imageView = new JLabel();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 300));
        setVisible(true);
        pack();

        try {
            socket = new Socket(host, port);
            objOut = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Couldn't connect. " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Show the image for the user
     * @param image
     */
    private void showImage(ImageIcon image){
        imageView.setIcon(image);
        add(imageView);
        pack();
        repaint();
    }

    /**
     * Get image from JFileChooser and send in throw ObjectOutPutStream
     */
    private synchronized void sendImage(){
        JFileChooser selectImage = new JFileChooser("");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Pictures (png, jpg)", "png", "jpg");
        selectImage.setFileFilter(filter);

        int newImage = selectImage.showOpenDialog(this);
        if (newImage != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File imageFile = selectImage.getSelectedFile();
        ImageIcon image = new ImageIcon(imageFile.getAbsolutePath());

        try{
            objOut.writeObject(image);
            objOut.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Listens for incoming data throw ObjectInputStream. If any data receives it while casts to a ImageIcon
     * and shows for the user.
     */
    @Override
    public void run() {
        ObjectInputStream objIn = null;
        try {
            objIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object image;
        try {
            while((image = objIn.readObject()) != null){
                ImageIcon receivedImage = (ImageIcon) image;
                showImage(receivedImage);
            }
        }catch(SocketException s){
            close();
            System.out.println("You lost connection");
            System.exit(1);
        }catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        close();
    }

    /**
     * Close socket and ObjectOutPutStream
     */
    private void close(){
        try {
            socket.close();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the program. Uses host 127.0.0.1 and port 2000 if nothing else is specified by user.
     * Host should be specified at index 0 and host at index 1.
     * Closes program if more than 2 arguments is specified
     * @param args
     */
    public static void main(String[] args){
        String host = "127.0.0.1";
        int port = 2000;

        if(args.length > 2){
            System.out.println("To many arguments");
            System.exit(1);
        }
        if(args.length > 0){
            host = args[0];
        }
        if(args.length > 1){
            port = Integer.parseInt(args[1].toString());
        }

        ImageClient imageClient = new ImageClient(host, port);
        Thread thread = new Thread(imageClient);
        thread.start();
    }
}

