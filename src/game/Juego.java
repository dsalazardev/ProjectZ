package game;

import entities.engineers.Ingeniero;
import entities.engineers.IngenieroSistemas; // Ejemplo
import entities.zombies.Zombie;
import managers.GestorDeOleadas;
import managers.GestorDeRecursos;
import managers.Mapa;
import ui.MainApp;

import java.util.concurrent.atomic.AtomicBoolean;

// Clase principal que gestiona el ciclo de vida del juego
public class Juego {

    private MainApp mainApp;

    private Mapa mapa;
    private GestorDeRecursos gestorRecursos;
    private GestorDeOleadas gestorOleadas;
    private final AtomicBoolean juegoCorriendo = new AtomicBoolean(false); // Flag para controlar el estado

    public Juego(MainApp app) {
        // Inicializar componentes principales
        this.mainApp = app;
        mapa = new Mapa(20, 10); // Mapa de 20x10 casillas (ejemplo)
        gestorRecursos = new GestorDeRecursos(500); // Empezar con 500 de energía
        gestorOleadas = new GestorDeOleadas(mapa, juegoCorriendo);
    }

    public void iniciarJuego() {
        System.out.println("===================================");
        System.out.println("=== Iniciando Project Z: The Last Bastion ===");
        System.out.println("===================================");
        juegoCorriendo.set(true);

        // Iniciar el generador de oleadas
        gestorOleadas.start();

        // Ejemplo: Añadir un ingeniero inicial (esto podría venir de la interacción del usuario)
        agregarIngeniero(new IngenieroSistemas(3, mapa.getAlto() / 2 -1, // Posición cercana al camino
                juegoCorriendo, mapa, gestorRecursos, mapa.getZombiesActivos()));


        // Iniciar el bucle principal del juego (podría ir en su propio hilo si la UI es compleja)
        mainLoop();
    }

    // Método para añadir un ingeniero si hay suficientes recursos
    public void agregarIngeniero(Ingeniero nuevoIngeniero) {
        if (nuevoIngeniero != null) {
            if (gestorRecursos.gastarEnergia(nuevoIngeniero.getCostoEnergia())) {
                mapa.agregarIngeniero(nuevoIngeniero);
                nuevoIngeniero.start(); // Iniciar el hilo del ingeniero
            } else {
                System.out.println("No se puede añadir " + nuevoIngeniero.getName() + ". Energía insuficiente.");
            }
        }
    }


    // Bucle principal del juego (simplificado)
    public void mainLoop() {
        long tiempoInicio = System.currentTimeMillis();
        long duracionJuegoMs = 60000; // Duración de 1 minuto (ejemplo)

        while (juegoCorriendo.get()) {
            // 1. Procesar entrada del usuario (si hubiera interfaz)

            // 2. Actualizar estado (los hilos de entidades lo hacen concurrentemente)

            // 3. Renderizar / Mostrar información (simplificado a consola)
            int energiaActual = gestorRecursos.getEnergia();
            int oleadaActual = gestorOleadas.getNumeroOleada(); // (Necesitarías añadir este getter en GestorDeOleadas)
            int vidaNucleo = 100; // (Tu lógica de vida)

            if (mainApp != null) {
                // Usa el puente 'actualizarUI' para enviar la tarea al hilo de JavaFX
                mainApp.actualizarUI(() -> {
                    mainApp.getStatsDisplay().actualizarStats(energiaActual, oleadaActual, vidaNucleo);
                });
            }

            imprimirEstadoJuego();

            // 4. Comprobar condiciones de fin de juego
            //    - ¿Ha llegado un zombi al final? (Implementar en Zombie.haLlegadoAlFinal y notificar aquí)
            //    - ¿Se ha superado el tiempo?
            //    - ¿Se han completado todas las oleadas? (Podría ser condición de victoria)

            if (System.currentTimeMillis() - tiempoInicio > duracionJuegoMs) {
                System.out.println("\n--- Tiempo agotado ---");
                terminarJuego();
            }

            try {
                Thread.sleep(1000); // Pausa del bucle principal (ej. actualizar cada segundo)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Bucle principal terminado.");
    }

    // Imprimir estado actual (ejemplo simple)
    private void imprimirEstadoJuego() {
        System.out.println("\n----- Estado del Juego -----");
        System.out.println("Energía: " + gestorRecursos.getEnergia());
        System.out.println("Ingenieros Activos: " + mapa.getIngenierosActivos().size());
        System.out.println("Zombies Activos: " + mapa.getZombiesActivos().size());
        // Podríamos imprimir una representación básica del mapa
        System.out.println("---------------------------");
    }


    // Método para detener todos los hilos y finalizar
    public void terminarJuego() {
        System.out.println("===================================");
        System.out.println("====== Finalizando el Juego =======");
        System.out.println("===================================");
        juegoCorriendo.set(false); // Indicar a todos los hilos que deben parar

        // Interrumpir y esperar al gestor de oleadas
        System.out.println("Deteniendo Gestor de Oleadas...");
        gestorOleadas.interrupt();
        try {
            gestorOleadas.join(2000); // Esperar máx 2 segundos
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        if (gestorOleadas.isAlive()) System.out.println("Gestor de oleadas no terminó correctamente.");


        // Interrumpir y esperar a todos los ingenieros
        System.out.println("Deteniendo Ingenieros...");
        for (Ingeniero ing : mapa.getIngenierosActivos()) {
            ing.interrupt();
        }
        for (Ingeniero ing : mapa.getIngenierosActivos()) {
            try {
                ing.join(1000); // Esperar máx 1 segundo por cada uno
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


    public Mapa getMapa() {
        return mapa;
    }
}