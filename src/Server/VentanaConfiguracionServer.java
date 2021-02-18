
package Server;


import Cliente.*;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Una sencilla ventana para configurar el chat
 * 
 * @author Ivan Salas Corrales <http://programandoointentandolo.com/>
 */

public class VentanaConfiguracionServer extends JDialog{
    
    private JTextField tfUsuario;
    private JCheckBox blackListCh;
    /**
     * Constructor de la ventana de configuracion inicial
     * 
     * @param padre Ventana padre
     */

    public VentanaConfiguracionServer() {
        
        JLabel lbUsuario = new JLabel("Introduce los usuarios permitidos:");
        
        tfUsuario = new JTextField();
        
        blackListCh = new JCheckBox("BlackList");
        
        JButton btAceptar = new JButton("Aceptar");
        btAceptar.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                ServidorChat.setUsers(tfUsuario.getText().split(","));
            }
        });
        
        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(20, 20, 0, 20);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        c.add(lbUsuario, gbc);
        
        
        gbc.ipadx = 100;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        c.add(tfUsuario, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        c.add(blackListCh, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        c.add(btAceptar, gbc);
        
        this.pack(); // Le da a la ventana el minimo tamaño posible
        this.setLocation(450, 200); // Posicion de la ventana
        this.setResizable(false); // Evita que se pueda estirar la ventana
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Deshabilita el boton de cierre de la ventana 
        this.setVisible(true);
    }
    
    
    public String[] getUsuarios(){
        return this.tfUsuario.getText().split(",");
    }
    
    public boolean getBlackList(){
        return this.blackListCh.isSelected();
    }
    
}
