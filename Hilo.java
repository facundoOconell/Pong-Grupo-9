package principal;


import java.util.logging.Level;
import java.util.logging.Logger;


public class Hilo extends Thread { // la clase Thread se usa para aplicaciones multitarea
    
    private final TableroJuego lamina;
   
    public Hilo (TableroJuego lamina){
        
        this.lamina = lamina;
    }
    
    
    @Override
    public void run (){
        
        // le agregamos la condicion de que cuando en Pelota.finJuego == false, entonces se termine
        while(!Pelota.finJuego){
            lamina.repaint(); // LO PONEMOS ACA EN VEZ DE ABAJO
            try {
                Thread.sleep(4);  // Esto controla la velocidad de la pelota, a mayor nro, mas lento
            } catch (InterruptedException ex) {
                System.out.println("error in graphics engine: " + ex.getMessage());
            }
            
        }
        
        
    }
    
    
}
