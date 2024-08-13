package principal;

import java.applet.AudioClip;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/*
import obtenerRecursos.Audio;*/


public class Pelota {
   private int x;
   private int y;
   private final int ANCHO=15, ALTO=15;
   private int dx=1, dy=1;
   
   
   
   
   private Integer puntaje1=0, puntaje2=0;
   public static boolean finJuego = false;
   
   /*Audio audio = new Audio();
   AudioClip rebote_1=audio.getAudio("/recursos/rebote_pelota1.wav");
   AudioClip rebote_2=audio.getAudio("/recursos/rebote_pelota2.wav");
   AudioClip falta=audio.getAudio("/recursos/falta.wav");
   
   */
   /********************************************************************************/
   
   
   public Pelota(int x, int y){
       
       this.x = x;
       this.y = y;
       
   }
    
   public Rectangle2D getPelota(){
       return new Rectangle2D.Double(x,y,ANCHO,ALTO);
   }
    
    public void mover(Rectangle limites, boolean colisionR1, boolean colisionR2){
        x+=dx;
        y+=dy;
        
        if (colisionR1){ // si se detecta una colision con la raqueta 1, tonces la pelota cambia de direccion
          dx =-dx;  
          x=25; // Esto corrige el error de que no rebota automaticamente
        }
        
        if (colisionR2){ // lo mismo para la raqueta 2
          dx =-dx;  
           x=740; // Esto corrige el error de que no rebota automaticamente 
        }
        
        
  
        
        // Esto hace que si supera el limite max en x, cambie de direccion a la contraria
        if (x < limites.getMinX()) {
            puntaje2++; //el puntaje del jugador2 aumenta en uno
           
            x = (int) limites.getCenterX();
            y = (int) limites.getCenterY();
            dx = -dx;
            /*
           falta.play();*/
        }

        if (x + ANCHO >= limites.getMaxX()) {
            puntaje1++; //el puntaje del jugador1 aumenta en uno
            
            x = (int) limites.getCenterX();
            y = (int) limites.getCenterY();
            dx = -dx;
            /*
            audio.getAudio("/recursos/falta.wav").play();*/
        }

        if (y < limites.getMinY()) {

            y = (int) limites.getMinY();

            dy = -dy;
       //     rebote_2.play();
        }

        if (y + ALTO >= limites.getMaxY()) {

            y = (int) (limites.getMaxY() - ALTO);

            dy = -dy;
            //rebote_2.play();
        }
      
    }
    
    public int getScore1(){
        return puntaje1;
    }
    
     public int getScore2(){
        return puntaje2;
    }
    
     
  /********************************************************************************/  
}
