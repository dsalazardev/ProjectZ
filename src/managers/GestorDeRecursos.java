package managers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GestorDeRecursos {
    private int energiaActual;
    private final Lock recursoLock = new ReentrantLock();

    public GestorDeRecursos(int energiaInicial) {
        this.energiaActual = energiaInicial;
    }

    public void agregarEnergia(int cantidad) {
        recursoLock.lock(); // Adquirir lock
        try {
            if (cantidad > 0) {
                this.energiaActual += cantidad;
                System.out.println("Energía añadida: +" + cantidad + ". Total: " + this.energiaActual);
            }
        } finally {
            recursoLock.unlock();
        }
    }

    // Método sincronizado para gastar energía
    public boolean gastarEnergia(int cantidad) {
        recursoLock.lock();
        try {
            if (cantidad > 0 && this.energiaActual >= cantidad) {
                this.energiaActual -= cantidad;
                System.out.println("Energía gastada: -" + cantidad + ". Restante: " + this.energiaActual);
                return true;
            }
            System.out.println("Energía insuficiente para gastar " + cantidad + ". Actual: " + this.energiaActual);
            return false;
        } finally {
            recursoLock.unlock();
        }
    }

    public int getEnergia() {
        recursoLock.lock();
        try {
            return energiaActual;
        } finally {
            recursoLock.unlock();
        }
    }
}