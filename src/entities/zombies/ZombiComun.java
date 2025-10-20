package entities.zombies;

import managers.Casilla;
import managers.Mapa;
import utils.Posicion;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZombiComun extends Zombie {

    private static final int VIDA_INICIAL = 100;
    private static final float VELOCIDAD = 1.0f;

    public ZombiComun(int posX, int posY, AtomicBoolean juegoCorriendo, Mapa mapa) {
        super("ZombiComun", posX, posY, juegoCorriendo, VIDA_INICIAL, VELOCIDAD, mapa);
    }

    @Override
    protected void moverse() throws InterruptedException {
        int nuevaPosX = posicion.x + 1;
        int nuevaPosY = posicion.y; // Asume camino horizontal

        Casilla casillaSiguiente = mapa.getCasilla(nuevaPosX, nuevaPosY);

        if (casillaSiguiente != null && casillaSiguiente.esTransitable()) {

            mapa.mapaLock.lock();
            try {
            posicion.x = nuevaPosX;
            posicion.y = nuevaPosY;
            System.out.println(getName() + " avanza a " + posicion);
            } finally {
               mapa.mapaLock.unlock();
             }

            if (casillaSiguiente.estaRalentizada()) {
                System.out.println(getName() + " ralentizado por cortafuegos!");
                Thread.sleep(1000); // Dormir 1 segundo extra
            }

        } else {
            System.out.println(getName() + " bloqueado en " + posicion + ". No puede avanzar a ("+nuevaPosX+","+nuevaPosY+")");
            Thread.sleep(500);
        }
    }
}