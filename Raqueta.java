package principal;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;


public class Raqueta {
    
    
    private int x,y;
    static final int ANCHO=10, ALTO=40;
    
    
    public Raqueta(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public Rectangle2D getRaqueta(){
        return new Rectangle2D.Double(x, y, ANCHO, ALTO);
    }
    
    public void moverR1 (Rectangle limites){ // movimiento de la raqueta 1
        
        if (EventoTeclado.w && y > limites.getMinY()){
            y=y-2;
        }
        
        if (EventoTeclado.s && y < limites.getMaxY()-ALTO){
            y=y+2;
        }
        
    }
    
    public void moverR2 (Rectangle limites){// movimiento de la raqueta 2
        if (EventoTeclado.up && y > limites.getMinY()){
            y=y-2;
        }
        
        if (EventoTeclado.down && y < limites.getMaxY()-ALTO){
            y=y+2;
        }
    }
}