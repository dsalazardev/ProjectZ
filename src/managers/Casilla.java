package managers;

import entities.EntidadActiva;

// Representa una celda en la cuadrícula del mapa
public class Casilla {
    private boolean esObstaculo = false;
    private boolean esCamino = true; // Por defecto es camino
    private boolean tieneCortafuegos = false;
    private long tiempoFinCortafuegos = 0;

    private EntidadActiva ocupante = null;

    public boolean esTransitable() {
        return esCamino && !esObstaculo;
    }

    public boolean estaRalentizada() {
        if (tieneCortafuegos && System.currentTimeMillis() < tiempoFinCortafuegos) {
            return true;
        } else if (tieneCortafuegos) {
            // El efecto expiró
            tieneCortafuegos = false;
            tiempoFinCortafuegos = 0;
            return false;
        }
        return false;
    }

    public void setObstaculo(boolean esObstaculo) {
        this.esObstaculo = esObstaculo;
        if(esObstaculo) this.esCamino = false;
    }

    public void setCortafuegos(long duracionMs) {
        this.tieneCortafuegos = true;
        this.tiempoFinCortafuegos = System.currentTimeMillis() + duracionMs;
        System.out.println("Cortafuegos activado hasta: " + tiempoFinCortafuegos);
    }
    public boolean esCamino() {
        return esCamino;
    }
    public void setEsCamino(boolean esCamino) {
        this.esCamino = esCamino;
    }
    // Getters y setters según necesidad
}