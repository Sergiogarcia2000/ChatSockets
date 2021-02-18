package Cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JTextField;

/**
 * Esta clase gestiona el envio de datos entre el cliente y el servidor.
 * 
 * @author Ivan Salas Corrales <http://programandoointentandolo.com>
 */

public class ConexionServidor {
    
    private DataOutputStream salidaDatos;
    
    public ConexionServidor(Socket socket, String tfMensaje, String usuario) {
        try {
            this.salidaDatos = new DataOutputStream(socket.getOutputStream());
            salidaDatos.writeUTF(tfMensaje);
        } catch (IOException ex) {
            System.out.println("Error al crear el stream de salida : " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("El socket no se creo correctamente. ");
        }
    }
    
    
    public void imprimirMensaje(String mensaje){
        try {
            salidaDatos.writeUTF(mensaje);
            //tfMensaje.setText("");
        } catch (IOException ex) {
            System.out.println("Error al intentar enviar un mensaje: " + ex.getMessage());
        }
    }
}