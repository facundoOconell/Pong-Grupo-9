package principal;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;



public class TableroJuego extends JPanel {
    
    Pelota pelota = new Pelota(0,0);
    
    Raqueta r1 = new Raqueta(10,200);
    Raqueta r2 = new Raqueta(786-10-Raqueta.ANCHO,200);
    
    
    public TableroJuego(){
        
        setBackground(Color.black);
        
        
        
        
    }
    
    
    @Override
    public void paintComponent(Graphics g){ // este metodo es el que nos va a dibujar las dos raquetas y la pelota
      
        super.paintComponent(g); // esto ejecuta el metodo de la clase padre, como si fuera un activador o un "ON"
        Graphics2D g2 = (Graphics2D)g; // Castear a g hace que se convierta a Graphics2D, que tiene mas funciones que Graphics solo
        g2.setColor(Color.white);
       
          //***************************************************************//
           dibujarPuntaje(g2);
        
          //***************************************************************//
          
        dibujar(g2);
        actualizar();
        
    }
    
   
    public void dibujar(Graphics2D g){
       //*********** COSO QUE NO ESTA EN ESTE TUTORIAL****************//
       
        //Dibuja la linea del medio
        Line2D.Double linea = new Line2D.Double(getBounds().getCenterX(), 0, getBounds().getCenterX(), getBounds().getMaxY());
        g.draw(linea); //imprime la linea del medio
        
       //***************************************************************//
        
        
        g.fill(pelota.getPelota()); // get Pelota nos retorna un objeto en 2D con las dimensiones que les dimos
        g.fill (r1.getRaqueta());
        g.fill (r2.getRaqueta());
        
       // System.out.println("x"+getBounds().getMaxX()); // esto nos sirve para ver las dimensiones reales del panel para establecer los limites
      //  System.out.println("x"+getBounds().getMaxY());
    } 
    
    public void actualizar(){
        
        
        pelota.mover(getBounds(), colision(r1.getRaqueta()),colision(r2.getRaqueta()) );
        r1.moverR1(getBounds());
        r2.moverR2(getBounds());
    }
    
    public void iterarJuego(){
        while(true){
            repaint();
           
            try {
                Thread.sleep(6);
            } catch (InterruptedException ex) {
                Logger.getLogger(TableroJuego.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean colision (Rectangle2D r){// metodo para poder detectar la colision
        return pelota.getPelota().intersects(r); // intersects detecta si la pelota se cruza con la raqueta
        // este metodo intesect detecta si dos objetos de tipo rectagulo se estan cruzando o no, si lo detecta, devuelve un true
    }
    
  
  //*********************** COSO QUE NO ESTA EN ESTE TUTORIAL ********************************************//
    
    private void dibujarPuntaje (Graphics2D g){
        
        Graphics2D g1=g, g2=g;
        
        Font puntaje = new Font("Arial", Font.BOLD, 30);
        g.setFont(puntaje);
        
         g1.drawString(Integer.toString(pelota.getScore1()), (float) getBounds().getCenterX() - 50, 30);
        g2.drawString(Integer.toString(pelota.getScore2()), (float) getBounds().getCenterX() + 25, 30);
        if (pelota.getScore1() == 5) {
            g.drawString("GANÓ El JUGADOR 1", (float) getBounds().getCenterX() - 155, (float) getBounds().getCenterY() - 100);
            Pelota.finJuego = false;
        }
        if (pelota.getScore2() == 5) {
            g.drawString("GANÓ EL JUGADOR 2", (float) getBounds().getCenterX() - 155, (float) getBounds().getCenterY() - 100);
            Pelota.finJuego = false;
        }
        
    }
    
    //***************************************************************//
    
    
}//FINAL