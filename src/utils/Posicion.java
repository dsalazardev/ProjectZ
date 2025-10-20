package utils;

public class Posicion {
    public int x;
    public int y;

    public Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double distanciaA(Posicion otra) {
        int dx = this.x - otra.x;
        int dy = this.y - otra.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}