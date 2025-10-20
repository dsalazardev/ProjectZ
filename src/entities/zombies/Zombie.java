package entities.zombies;

import entities.EntidadActiva;
import managers.Mapa;
import utils.Posicion;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Zombie extends EntidadActiva {

    protected AtomicInteger vida;
    protected float velocidad;
    protected Mapa mapa;

    public Zombie(String nombre, int posX, int posY, AtomicBoolean juegoCorriendo, int vidaInicial, float velocidad, Mapa mapa) {
        super(nombre + "-" + System.nanoTime() % 1000, posX, posY, juegoCorriendo);
        this.vida = new AtomicInteger(vidaInicial);
        this.velocidad = velocidad;
        this.mapa = mapa;
    }

    @Override
    public void run() {
        System.out.println("Zombie " + getName() + " aparece en " + posicion);
        try {
            while (juegoCorriendo.get() && !isMuerto() && !Thread.currentThread().isInterrupted()) {
                moverse();

                long sleepTime = (long) (1000 / velocidad);
                Thread.sleep(sleepTime > 0 ? sleepTime : 100);

                if (haLlegadoAlFinal()) {
                    System.out.println("¡¡¡Zombie " + getName() + " ha llegado al núcleo!!!");
                    mapa.eliminarZombie(this);
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Zombie " + getName() + " interrumpido.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Error inesperado en Zombie " + getName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (isMuerto()) {
                System.out.println("Zombie " + getName() + " eliminado en " + posicion);
            } else if (!juegoCorriendo.get()) {
                System.out.println("Zombie " + getName() + " detenido por fin de juego.");
            }
        }
    }

    // Método seguro para recibir daño
    public void recibirDano(int cantidad) {
        int vidaRestante = vida.addAndGet(-cantidad);
        System.out.println(getName() + " recibe " + cantidad + " daño. Vida: " + vidaRestante);
        if (vidaRestante <= 0) {
            System.out.println(getName() + " ha sido derrotado.");
        }
    }

    public boolean isMuerto() {
        return vida.get() <= 0;
    }

    protected abstract void moverse() throws InterruptedException;

    // Método para comprobar si llegó al destino
    protected boolean haLlegadoAlFinal() {
        return mapa.esPosicionFinal(this.posicion);
    }

    public int getVidaActual() {
        return vida.get();
    }
}