package game;

import entities.engineers.Ingeniero;
import entities.engineers.IngenieroSistemas;
import entities.zombies.Zombie;
import managers.GestorDeOleadas;
import managers.GestorDeRecursos;
import managers.Mapa;

import java.util.concurrent.atomic.AtomicBoolean;

public class Juego {

    private Mapa mapa;
    private GestorDeRecursos gestorRecursos;
    private GestorDeOleadas gestorOleadas;
    private final AtomicBoolean juegoCorriendo = new AtomicBoolean(false);

    public Juego() {
        mapa = new Mapa(20, 10);
        gestorRecursos = new GestorDeRecursos(500);
        gestorOleadas = new GestorDeOleadas(mapa, juegoCorriendo);
    }

    public void iniciarJuego() {
        System.out.println("===================================");
        System.out.println("=== Iniciando Project Z: The Last Bastion ===");
        System.out.println("===================================");
        juegoCorriendo.set(true);

        gestorOleadas.start();

        agregarIngeniero(new IngenieroSistemas(3, mapa.getAlto() / 2 -1, // Posición cercana al camino
                juegoCorriendo, mapa, gestorRecursos, mapa.getZombiesActivos()));

        mainLoop();
    }

    // Método para añadir un ingeniero si hay suficientes recursos
    public void agregarIngeniero(Ingeniero nuevoIngeniero) {
        if (nuevoIngeniero != null) {
            if (gestorRecursos.gastarEnergia(nuevoIngeniero.getCostoEnergia())) {
                mapa.agregarIngeniero(nuevoIngeniero);
                nuevoIngeniero.start();
            } else {
                System.out.println("No se puede añadir " + nuevoIngeniero.getName() + ". Energía insuficiente.");
            }
        }
    }


    public void mainLoop() {
        long tiempoInicio = System.currentTimeMillis();
        long duracionJuegoMs = 60000;

        while (juegoCorriendo.get()) {
            imprimirEstadoJuego();
            if (System.currentTimeMillis() - tiempoInicio > duracionJuegoMs) {
                System.out.println("\n--- Tiempo agotado ---");
                terminarJuego();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Bucle principal terminado.");
    }

    private void imprimirEstadoJuego() {
        System.out.println("\n----- Estado del Juego -----");
        System.out.println("Energía: " + gestorRecursos.getEnergia());
        System.out.println("Ingenieros Activos: " + mapa.getIngenierosActivos().size());
        System.out.println("Zombies Activos: " + mapa.getZombiesActivos().size());
        System.out.println("---------------------------");
    }


    // Método para detener todos los hilos y finalizar
    public void terminarJuego() {
        System.out.println("===================================");
        System.out.println("====== Finalizando el Juego =======");
        System.out.println("===================================");
        juegoCorriendo.set(false);

        System.out.println("Deteniendo Gestor de Oleadas...");
        gestorOleadas.interrupt();
        try {
            gestorOleadas.join(2000);
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        if (gestorOleadas.isAlive()) System.out.println("Gestor de oleadas no terminó correctamente.");

        System.out.println("Deteniendo Ingenieros...");
        for (Ingeniero ing : mapa.getIngenierosActivos()) {
            ing.interrupt();
        }
        for (Ingeniero ing : mapa.getIngenierosActivos()) {
            try {
                ing.join(1000);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            if (ing.isAlive()) System.out.println("Ingeniero " + ing.getName() + " no terminó correctamente.");
        }

        // Interrumpir y esperar a todos los zombies
        System.out.println("Deteniendo Zombies...");
        for (Zombie zom : mapa.getZombiesActivos()) {
            zom.interrupt();
        }
        for (Zombie zom : mapa.getZombiesActivos()) {
            try {
                zom.join(1000);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            if (zom.isAlive()) System.out.println("Zombie " + zom.getName() + " no terminó correctamente.");
        }

        System.out.println("===================================");
        System.out.println("========= Juego Terminado =========");
        System.out.println("===================================");
    }

    public static void main(String[] args) {
        Juego projectZ = new Juego();
        projectZ.iniciarJuego();
    }
}