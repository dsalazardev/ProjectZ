package game;

import entities.engineers.Ingeniero;
import entities.engineers.IngenieroSistemas; // Ejemplo
import entities.zombies.Zombie;
import managers.GestorDeOleadas;
import managers.GestorDeRecursos;
import managers.Mapa;
import ui.MainApp; // Importar la UI

import java.util.concurrent.atomic.AtomicBoolean;

// Clase principal que gestiona el ciclo de vida del juego
public class Juego {

    private MainApp mainApp; // Referencia a la UI
    private Mapa mapa;
    private GestorDeRecursos gestorRecursos;
    private GestorDeOleadas gestorOleadas;
    private final AtomicBoolean juegoCorriendo = new AtomicBoolean(false); // Flag para controlar el estado

    // ----- CORRECCIÓN -----
    // Este es AHORA el ÚNICO constructor.
    // Forza a que el Juego DEBE recibir la instancia de MainApp (la UI).
    public Juego(MainApp app) {
        // Inicializar componentes principales
        this.mainApp = app;
        mapa = new Mapa(20, 10); // Mapa de 20x10 casillas (ejemplo)
        gestorRecursos = new GestorDeRecursos(500); // Empezar con 500 de energía
        gestorOleadas = new GestorDeOleadas(mapa, juegoCorriendo);
    }

    // ----- CORRECCIÓN -----
    // Se eliminó el constructor public Juego() {}
    // que causaba el error de "dos instancias".

    public void iniciarJuego() {
        System.out.println("===================================");
        System.out.println("=== Iniciando Project Z: The Last Bastion ===");
        System.out.println("===================================");
        juegoCorriendo.set(true);

        // Iniciar el generador de oleadas
        gestorOleadas.start();

        // Ejemplo: Añadir un ingeniero inicial (esto podría venir de la interacción del usuario)
        agregarIngeniero(new IngenieroSistemas(3, mapa.getAlto() / 2 - 1, // Posición cercana al camino
                juegoCorriendo, mapa, gestorRecursos, mapa.getZombiesActivos()));


        // Iniciar el bucle principal del juego
        mainLoop();
    }

    // Método para añadir un ingeniero si hay suficientes recursos
    public void agregarIngeniero(Ingeniero nuevoIngeniero) {
        if (nuevoIngeniero != null) {
            // Intenta gastar la energía
            if (gestorRecursos.gastarEnergia(nuevoIngeniero.getCostoEnergia())) {
                mapa.agregarIngeniero(nuevoIngeniero);
                nuevoIngeniero.start(); // Iniciar el hilo del ingeniero
            } else {
                System.out.println("No se puede añadir " + nuevoIngeniero.getName() + ". Energía insuficiente.");
                // Aquí podrías enviar una notificación a la UI
                // mainApp.actualizarUI(() -> mainApp.mostrarNotificacion("Energía insuficiente"));
            }
        }
    }

    // ----- NUEVO MÉTODO AÑADIDO -----
    // Este método es llamado por GameCanvas (UI) cuando el usuario hace clic.
    public void intentarColocarIngeniero(int fila, int col, String tipo) {
        if (tipo == null) {
            System.out.println("Juego: Intento de colocar sin tipo seleccionado.");
            return;
        }

        // Lógica para crear el ingeniero basado en el 'tipo'
        Ingeniero nuevoIngeniero = null;

        // Aquí puedes añadir validaciones (ej. mapa.esCasillaLibre(fila, col))

        if (tipo.equals("SISTEMAS")) {
            nuevoIngeniero = new IngenieroSistemas(col, fila, // (col, fila) -> (x, y)
                    juegoCorriendo, mapa, gestorRecursos, mapa.getZombiesActivos());
        } else if (tipo.equals("CIVIL")) {
            // nuevoIngeniero = new IngenieroCivil(col, fila, ...);
            System.out.println("Juego: Tipo 'CIVIL' aún no implementado.");
        }

        if (nuevoIngeniero != null) {
            // Usar el método que ya tenías para añadirlo (que también gasta energía)
            agregarIngeniero(nuevoIngeniero);
        } else {
            System.out.println("Juego: No se pudo crear el ingeniero de tipo " + tipo);
        }
    }


    // Bucle principal del juego (simplificado)
    public void mainLoop() {
        long tiempoInicio = System.currentTimeMillis();
        long duracionJuegoMs = 600000; // Duración de 10 minutos (ejemplo)

        while (juegoCorriendo.get()) {

            // 1. Actualizar estado (los hilos de entidades lo hacen concurrentemente)

            // 2. Enviar actualizaciones a la UI
            int energiaActual = gestorRecursos.getEnergia();
            int oleadaActual = gestorOleadas.getNumeroOleada();
            int vidaNucleo = 100; // TODO: Implementar lógica de vida

            // ----- CORRECCIÓN -----
            // Esta comprobación ahora SIEMPRE será verdadera.
            if (mainApp != null) {
                // Usa el puente 'actualizarUI' para enviar la tarea al hilo de JavaFX
                mainApp.actualizarUI(() -> {
                    // Llama al método público de MainApp para obtener el componente
                    mainApp.getStatsDisplay().actualizarStats(energiaActual, oleadaActual, vidaNucleo);
                });
            }

            // 3. Imprimir estado en consola (opcional)
            // imprimirEstadoJuego(); // Puedes comentar esto si hay mucha salida

            // 4. Comprobar condiciones de fin de juego
            if (System.currentTimeMillis() - tiempoInicio > duracionJuegoMs) {
                System.out.println("\n--- Tiempo agotado ---");
                terminarJuego();
            }

            try {
                // Refresca el estado de la UI cada 500ms (2 veces por segundo)
                // No es necesario hacerlo 1000 veces por segundo.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Salir del bucle si el hilo es interrumpido
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
        System.out.println("---------------------------");
    }


    // Método para detener todos los hilos y finalizar
    public void terminarJuego() {
        juegoCorriendo.set(false); // Indicar a todos los hilos que deben parar
        System.out.println("===================================");
        System.out.println("====== Finalizando el Juego =======");
        System.out.println("===================================");

        // Interrumpir y esperar al gestor de oleadas
        System.out.println("Deteniendo Gestor de Oleadas...");
        if (gestorOleadas != null) {
            gestorOleadas.interrupt();
            try {
                gestorOleadas.join(1000); // Esperar máx 1 seg
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // Interrumpir y esperar a todos los ingenieros
        System.out.println("Deteniendo Ingenieros...");
        if (mapa != null) {
            for (Ingeniero ing : mapa.getIngenierosActivos()) {
                ing.interrupt();
            }
            for (Ingeniero ing : mapa.getIngenierosActivos()) {
                try {
                    ing.join(500);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }

        // Interrumpir y esperar a todos los zombies
        System.out.println("Deteniendo Zombies...");
        if (mapa != null) {
            for (Zombie zom : mapa.getZombiesActivos()) {
                zom.interrupt();
            }
            for (Zombie zom : mapa.getZombiesActivos()) {
                try {
                    zom.join(500);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }

        System.out.println("===================================");
        System.out.println("========= Juego Terminado =========");
        System.out.println("===================================");
    }

    // ----- CORRECCIÓN -----
    // Se eliminó el método main(String[] args) {}
    // que causaba el error de "dos instancias".
    // El único 'main' está en ui/MainApp.java

    // Getter para que el GameCanvas pueda acceder al mapa
    public Mapa getMapa() {
        return mapa;
    }
}