package managers;

import entities.zombies.*;
import utils.Posicion;
import java.util.concurrent.atomic.AtomicBoolean;

public class GestorDeOleadas extends Thread {

    private final Mapa mapa;
    private final AtomicBoolean juegoCorriendo;
    private int numeroOleada = 0;
    private final long TIEMPO_ENTRE_OLEADAS = 15000;
    private final Posicion puntoInicioZombies;

    public GestorDeOleadas(Mapa mapa, AtomicBoolean juegoCorriendo) {
        super("GestorOleadas");
        this.mapa = mapa;
        this.juegoCorriendo = juegoCorriendo;
        this.puntoInicioZombies = new Posicion(0, mapa.getAlto() / 2);
    }

    @Override
    public void run() {
        System.out.println("Gestor de Oleadas iniciado.");
        try {
            Thread.sleep(5000);

            while (juegoCorriendo.get() && !Thread.currentThread().isInterrupted()) {
                numeroOleada++;
                System.out.println("--- Iniciando Oleada " + numeroOleada + " ---");
                generarOleada(numeroOleada);

                System.out.println("--- Oleada " + numeroOleada + " generada. Próxima oleada en " + TIEMPO_ENTRE_OLEADAS/1000 + " seg ---");
                Thread.sleep(TIEMPO_ENTRE_OLEADAS);
            }
        } catch (InterruptedException e) {
            System.out.println("Gestor de Oleadas interrumpido.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Error en Gestor de Oleadas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Gestor de Oleadas finalizado.");
        }
    }

    private void generarOleada(int numOleada) throws InterruptedException {
        int cantidadZombiesComunes = 5 + numOleada * 2;
        int cantidadZombiesBrutos = numOleada / 2;
        int cantidadZombiesCorredores = numOleada / 3;

        for (int i = 0; i < cantidadZombiesComunes; i++) {
            if (!juegoCorriendo.get()) return;
            Zombie z = new ZombiComun(puntoInicioZombies.x, puntoInicioZombies.y, juegoCorriendo, mapa);
            mapa.agregarZombie(z);
            z.start();
            Thread.sleep(500);
        }
    /*
        for (int i = 0; i < cantidadZombiesBrutos; i++) {
            if (!juegoCorriendo.get()) return;
            Zombie z = new ZombiBruto(puntoInicioZombies.x, puntoInicioZombies.y, juegoCorriendo, mapa);
            mapa.agregarZombie(z);
            z.start();
            Thread.sleep(1000); // Más delay para los brutos
        }

        for (int i = 0; i < cantidadZombiesCorredores; i++) {
            if (!juegoCorriendo.get()) return;
            Zombie z = new ZombiCorredor(puntoInicioZombies.x, puntoInicioZombies.y, juegoCorriendo, mapa);
            mapa.agregarZombie(z);
            z.start();
            Thread.sleep(300); // Menos delay para los corredores
        }
    */

    }
}