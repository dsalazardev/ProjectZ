package entities.engineers;

import entities.EntidadActiva;
import entities.zombies.Zombie;
import managers.GestorDeRecursos;
import managers.Mapa;
import utils.Posicion;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;


public abstract class Ingeniero extends EntidadActiva {

    protected int costoEnergia;
    protected int areaDeAtaque;
    protected int cooldownHabilidadMillis;
    protected long ultimoUsoHabilidad;

    protected Mapa mapa;
    protected GestorDeRecursos gestorRecursos;
    protected List<Zombie> zombiesActivos;


    public Ingeniero(String nombre, int posX, int posY, AtomicBoolean juegoCorriendo,
                     Mapa mapa, GestorDeRecursos gestorRecursos, List<Zombie> zombies) {
        super(nombre + "-" + System.nanoTime() % 1000, posX, posY, juegoCorriendo);
        this.mapa = mapa;
        this.gestorRecursos = gestorRecursos;
        this.zombiesActivos = zombies;
        this.ultimoUsoHabilidad = 0;
    }

    // Lógica principal del hilo del ingeniero
    @Override
    public void run() {
        System.out.println("Ingeniero " + getName() + " desplegado en " + posicion);
        try {
            while (juegoCorriendo.get() && !Thread.currentThread().isInterrupted()) {
                Zombie objetivo = buscarObjetivo();

                if (objetivo != null && !objetivo.isMuerto()) {
                    atacar(objetivo);
                }

                long tiempoActual = System.currentTimeMillis();
                if (tiempoActual - ultimoUsoHabilidad >= cooldownHabilidadMillis) {
                    if (puedeUsarHabilidad()) {
                        usarHabilidad();
                        ultimoUsoHabilidad = tiempoActual;
                    }
                }

                Thread.sleep(getCooldownAtaqueBase());

            }
        } catch (InterruptedException e) {
            System.out.println("Ingeniero " + getName() + " interrumpido.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Error inesperado en Ingeniero " + getName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Ingeniero " + getName() + " retirado de " + posicion);
        }
    }

    protected abstract Zombie buscarObjetivo();

    protected abstract void atacar(Zombie objetivo) throws InterruptedException;

    protected abstract boolean puedeUsarHabilidad();

    protected abstract void usarHabilidad() throws InterruptedException;

    protected abstract int getCooldownAtaqueBase();

    public int getCostoEnergia() {
        return costoEnergia;
    }
}