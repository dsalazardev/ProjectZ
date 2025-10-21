package managers;

import entities.engineers.Ingeniero;
import entities.zombies.Zombie;
import utils.Posicion;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Mapa {

    private final int ancho;
    private final int alto;

    private final Casilla[][] cuadricula;
    private final List<Ingeniero> ingenierosActivos = new CopyOnWriteArrayList<>();
    private final List<Zombie> zombiesActivos = new CopyOnWriteArrayList<>();
    public final Lock mapaLock = new ReentrantLock();

    public Mapa(int ancho, int alto) {

        this.ancho = ancho;
        this.alto = alto;

        this.cuadricula = new Casilla[alto][ancho];
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                cuadricula[y][x] = new Casilla();
                if (y == alto / 2) {
                    cuadricula[y][x].setEsCamino(true);
                    cuadricula[y][x].setObstaculo(false);
                } else {
                    cuadricula[y][x].setEsCamino(false);
                    cuadricula[y][x].setObstaculo(true);
                }
            }
        }
        System.out.println("Mapa inicializado (" + ancho + "x" + alto + ")");
    }


    public void agregarIngeniero(Ingeniero ing) {
        if (ing != null) {
            ingenierosActivos.add(ing);
            System.out.println("Ingeniero " + ing.getName() + " añadido al mapa.");
        }
    }

    public void eliminarIngeniero(Ingeniero ing) {
        if (ing != null) {
            ingenierosActivos.remove(ing);
        }
    }

    public void agregarZombie(Zombie zom) {
        if (zom != null) {
            zombiesActivos.add(zom);
        }
    }

    public void eliminarZombie(Zombie zom) {
        if (zom != null) {
            zombiesActivos.remove(zom);
        }
    }


    public void colocarCortafuegos(int x, int y, long duracionMs) {
        mapaLock.lock();
        try {
            if (esPosicionValida(x, y) && cuadricula[y][x].esCamino()) {
                cuadricula[y][x].setCortafuegos(duracionMs);
                System.out.println("Mapa: Cortafuegos colocado en (" + x + "," + y + ")");
            } else {
                System.out.println("Mapa: Posición inválida para cortafuegos (" + x + "," + y + ")");
            }
        } finally {
            mapaLock.unlock();
        }
    }

    public void colocarMuro(int x, int y) {
        mapaLock.lock();
        try {
            if (esPosicionValida(x, y) && !cuadricula[y][x].esCamino()) {
                cuadricula[y][x].setObstaculo(true);
                System.out.println("Mapa: Muro colocado en (" + x + "," + y + ")");
            } else {
                System.out.println("Mapa: Posición inválida para muro (" + x + "," + y + ")");
            }
        } finally {
            mapaLock.unlock();
        }
    }


    public List<Zombie> getZombiesEnRango(Posicion centro, int rango) {
        return zombiesActivos.stream()
                .filter(z -> !z.isMuerto() && centro.distanciaA(z.getPosicion()) <= rango)
                .collect(Collectors.toList());
    }

    public Casilla getCasilla(int x, int y) {
        if (esPosicionValida(x, y)) {
            return cuadricula[y][x];
        }
        return null;
    }

    public boolean esPosicionValida(int x, int y) {
        return x >= 0 && x < ancho && y >= 0 && y < alto;
    }

    public boolean esCasillaValidaParaCortafuegos(int x, int y) {
        mapaLock.lock();
        try {
            return esPosicionValida(x, y) && cuadricula[y][x].esCamino() && !cuadricula[y][x].estaRalentizada();
        } finally {
            mapaLock.unlock();
        }
    }

    public boolean esPosicionFinal(Posicion pos) {
        return pos.x == ancho - 1 && pos.y == alto / 2;
    }

    public List<Ingeniero> getIngenierosActivos() {
        return ingenierosActivos;
    }

    public List<Zombie> getZombiesActivos() {
        return zombiesActivos;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}