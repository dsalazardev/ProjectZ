package entities.engineers;

import entities.engineers.Ingeniero;
import entities.zombies.Zombie;
import managers.GestorDeRecursos;
import managers.Mapa;
import utils.Posicion;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class IngenieroSistemas extends Ingeniero {

    private static final int COSTO = 75;
    private static final int RANGO = 4;
    private static final int COOLDOWN_HABILIDAD = 12000;
    private static final int COOLDOWN_ATAQUE = 300;
    private static final int DAMAGE_ATAQUE = 8;
    private static final long DURACION_CORTAFUEGOS = 6000;
    private static final int TIEMPO_COMPILACION = 1000;

    public IngenieroSistemas(int posX, int posY, AtomicBoolean juegoCorriendo, Mapa mapa, GestorDeRecursos gestorRecursos, List<Zombie> zombies) {
        super("Sistemas", posX, posY, juegoCorriendo, mapa, gestorRecursos, zombies);
        this.costoEnergia = COSTO;
        this.areaDeAtaque = RANGO;
        this.cooldownHabilidadMillis = COOLDOWN_HABILIDAD;
    }

    @Override
    protected Zombie buscarObjetivo() {
        Optional<Zombie> objetivoOpt = zombiesActivos.stream()
                .filter(z -> !z.isMuerto() && posicion.distanciaA(z.getPosicion()) <= areaDeAtaque)
                .min(Comparator.comparingDouble(z -> posicion.distanciaA(z.getPosicion())));

        return objetivoOpt.orElse(null);
    }

    @Override
    protected void atacar(Zombie objetivo) throws InterruptedException {
        System.out.println(getName() + " [" + posicion + "] lanza paquete de datos a " + objetivo.getName() + " ["+ objetivo.getPosicion()+"]");
        objetivo.recibirDano(DAMAGE_ATAQUE);
    }

    @Override
    protected boolean puedeUsarHabilidad() {
        return true;
    }

    @Override
    protected void usarHabilidad() throws InterruptedException {
        System.out.println(getName() + " [" + posicion + "] compilando Cortafuegos Lógico...");
        Thread.sleep(TIEMPO_COMPILACION);


        int targetX = posicion.x + 1;
        int targetY = posicion.y;

        System.out.println(getName() + " intenta desplegar Cortafuegos en (" + targetX + "," + targetY + ").");
        mapa.colocarCortafuegos(targetX, targetY, DURACION_CORTAFUEGOS);

        System.out.println(getName() + " terminó intento de despliegue de Cortafuegos.");
    }

    @Override
    protected int getCooldownAtaqueBase() {
        return COOLDOWN_ATAQUE;
    }
}