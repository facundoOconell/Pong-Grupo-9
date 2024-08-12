package principal;

import java.awt.Toolkit;
import javax.swing.JFrame;

public class Ventana extends JFrame {
    
  // final era una constante, o sea, no se puede modificar  
   private final int ANCHO=800, ALTO=500;
  
   private TableroJuego lamina;
   
   private Hilo hilo;
   
    public Ventana (){
        
        setTitle("PONG");
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);
        
        lamina = new TableroJuego();
        add(lamina);
        addKeyListener(new EventoTeclado());
        
        
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        hilo = new Hilo(lamina);
        hilo.start();
      
        
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imagenes/PONG.png")));
     
    }
    
}