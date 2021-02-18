package Server;

import Cliente.ConexionServidor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;

/**
 * Servidor para el chat.
 * 
 * @author Ivan Salas Corrales <http://programandoointentandolo.com>
 */
 
public class ServidorChat extends JFrame{

    /**
     * @param args the command line arguments
     */

    private static ArrayList<String> admitedUsers;
    
    private static int puerto = 1234;
    private static int maximoConexiones = 10; // Maximo de conexiones simultaneas
    private static ServerSocket servidor = null;
    private static Socket socket = null;
    private static MensajesChat mensajes = new MensajesChat();
    private static boolean blackList = true;
    private static String message;
    
    public static void main(String[] args) {
        
        
        
        VentanaConfiguracionServer vcs = new VentanaConfiguracionServer();
        
        try {
            // Se crea el serverSocket
            servidor = new ServerSocket(puerto, maximoConexiones);
            
            
            
            // Bucle infinito para esperar conexiones
            while (true) {
                socket = servidor.accept();
                
                // get the input stream from the connected socket
                InputStream inputStream = socket.getInputStream();
                // create a DataInputStream so we can read data from it.
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                // read the message from the socket
                message = dataInputStream.readUTF();
                System.out.println(message);
                
                if (vcs.getBlackList()){
                    if (admitedUsers.contains(message)){
                        giveAccess(socket);
                    }else{
                        denyAccess(socket);
                    }
                }else{
                    giveAccess(socket);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally{
            try {
                socket.close();
                servidor.close();
            } catch (IOException ex) {
                System.out.println("Error al cerrar el servidor: " + ex.getMessage());
            }
        }
    }
    
    private static void giveAccess(Socket socket) throws IOException{
        System.out.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " conectado.");

        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        // write the message we want to send
        dataOutputStream.writeUTF("ALL/,/Servidor: Bienvenid@ al chat " + message + "!");
        dataOutputStream.flush(); // send the message

        ConexionCliente cc = new ConexionCliente(socket, mensajes);
        cc.start();
    }
    
    private static void denyAccess(Socket socket) throws IOException{
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        // write the message we want to send
        dataOutputStream.writeUTF("ALL/,/Servidor: No tienes permiso para conectarte!");
        dataOutputStream.flush(); // send the message
        socket.close();
    }
    
    public static void setUsers(String[] users){
        admitedUsers = new ArrayList<>(Arrays.asList(users));
        
        for (String s : admitedUsers){
            System.out.println(s);
        }
    }
    
    public static ArrayList<String> getUsers(){
        return admitedUsers;
    }
}