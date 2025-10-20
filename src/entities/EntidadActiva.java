package entities;

import utils.Posicion;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class EntidadActiva extends Thread {

    protected Posicion posicion;
    protected AtomicBoolean juegoCorriendo;

    public EntidadActiva(String nombre, int posX, int posY, AtomicBoolean juegoCorriendo) {
        super(nombre);
        this.posicion = new Posicion(posX, posY);
        this.juegoCorriendo = juegoCorriendo;
    }

    @Override
    public abstract void run();

    public Posicion getPosicion() {
        return posicion;
    }

    public int getPosicionX() {
        return posicion.x;
    }

    public int getPosicionY() {
        return posicion.y;
    }

    public void detener() {
        this.interrupt();
    }
}