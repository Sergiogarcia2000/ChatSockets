package Cliente;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

public class ClienteChat extends JFrame {
    
    private JTextArea mensajesChat;
    private Socket socket;
    
    private int puerto;
    private String host;
    private String usuario;
    private VentanaGrupos vg;
    private JCheckBox chGrupo;
    
    public ClienteChat(){
        super("Cliente Chat");
        
        // Elementos de la ventana
        mensajesChat = new JTextArea();
        mensajesChat.setEnabled(false); // El area de mensajes del chat no se debe de poder editar
        mensajesChat.setLineWrap(true); // Las lineas se parten al llegar al ancho del textArea
        mensajesChat.setWrapStyleWord(true); // Las lineas se parten entre palabras (por los espacios blancos)
        JScrollPane scrollMensajesChat = new JScrollPane(mensajesChat);
        JTextField tfMensaje = new JTextField("");
        JButton btEnviar = new JButton("Enviar");
        chGrupo = new JCheckBox("Grupo");
        JButton btnGrupo = new JButton("Modificar Grupo");
        vg = new VentanaGrupos();
        vg.setVisible(false);
        
        // Colocacion de los componentes en la ventana
        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(20, 20, 20, 20);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        c.add(scrollMensajesChat, gbc);
        // Restaura valores por defecto
        gbc.gridwidth = 1;        
        gbc.weighty = 0;
        
        gbc.fill = GridBagConstraints.HORIZONTAL;        
        gbc.insets = new Insets(0, 20, 20, 20);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        c.add(tfMensaje, gbc);
        // Restaura valores por defecto
        gbc.weightx = 0;
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        c.add(btEnviar, gbc);
        
        ///
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        c.add(chGrupo, gbc);
        // Restaura valores por defecto
        gbc.weightx = 0;
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        c.add(btnGrupo, gbc);
        
        this.setBounds(400, 100, 400, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     
        
        // Ventana de configuracion inicial
        VentanaConfiguracion vc = new VentanaConfiguracion(this);
        host = vc.getHost();
        puerto = vc.getPuerto();
        usuario = vc.getUsuario();
        
        System.out.println("Quieres conectarte a " + host + " en el puerto " + puerto + " con el nombre de ususario: " + usuario + ".");
        
        // Se crea el socket para conectar con el Sevidor del Chat
        try {
            socket = new Socket(host, puerto);
            
            // ENVÍO AL SERVIDOR EL USUARIO
            // get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Sending string to the ServerSocket");

            // write the message we want to send
            dataOutputStream.writeUTF(usuario);
            dataOutputStream.flush(); // send the message

            
        } catch (UnknownHostException ex) {
            System.out.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
        } catch (IOException ex) {
            System.out.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
        }
        
        // Accion para el boton enviar getMessage(tfMensaje.getText())new ConexionServidor(socket, this.getMessage(tfMensaje.getText()), usuario)
        btEnviar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = getMessage(tfMensaje.getText());
                new ConexionServidor(socket, msg, usuario);
                tfMensaje.setText("");
            }
        }); 
        btnGrupo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               vg.setVisible(true);
            }
        }); 
    }
    
    /**
     * Recibe los mensajes del chat reenviados por el servidor
     */

    private String getMessage(String msg){
        String message = "";
        
        String[] msgSplitted = msg.split(" ");
        
        if (msgSplitted[0].equalsIgnoreCase("/msg")){
            message += "PRIVATE/"+usuario + ","+msgSplitted[1]+"/";
            message += usuario + "[P]:";
            for (int i = 2; i < msgSplitted.length; i++){
                message += msgSplitted[i] + " ";
            }
        }else{
            if (chGrupo.isSelected()){
            message += "GROUP/";
            }else{
                message += "ALL/";
            }
            for (String s : vg.getUsuarios()){
            message += s + ",";
            message += usuario + "/" + usuario + ": " + msg;
            }
        }
        return message;
    }
    
    public void recibirMensajesServidor(){
        // Obtiene el flujo de entrada del socket
        DataInputStream entradaDatos = null;
        String mensaje;
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Error al crear el stream de entrada: " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("El socket no se creo correctamente. ");
        }
        
        // Bucle infinito que recibe mensajes del servidor
        boolean conectado = true;
        while (conectado) {
            try {
                mensaje = entradaDatos.readUTF();
                
                String[] mensajes = mensaje.split("/");
          
                if (mensajes.length >2){ 
                    if (mensajes[0].equalsIgnoreCase("ALL")){
                        mensajesChat.append(mensajes[2] + System.lineSeparator());
                    }else if (mensajes[0].equalsIgnoreCase("PRIVATE")){
                        ArrayList<String> usersAdmited = new ArrayList<>(Arrays.asList(mensajes[1].split(",")));
                        if (usersAdmited.contains(usuario)){
                            mensajesChat.append(mensajes[2] + System.lineSeparator());
                        }
                    }else{
                        ArrayList<String> usersAdmited = new ArrayList<>(Arrays.asList(mensajes[1].split(",")));
                        for (String s : usersAdmited){
                            System.out.println(s);
                        }
                        if (usersAdmited.contains(usuario)){
                            mensajesChat.append("[G]" + mensajes[2] + System.lineSeparator());
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error al leer del stream de entrada: " + ex.getMessage());
                conectado = false;
            } catch (NullPointerException ex) {
                System.out.println("El socket no se creo correctamente. ");
                conectado = false;
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        // Carga el archivo de configuracion de log4J   
        ClienteChat c = new ClienteChat();
        c.recibirMensajesServidor();
    }
}  