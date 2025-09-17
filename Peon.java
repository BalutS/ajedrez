package ajedrez;

import java.util.ArrayList;
import java.util.List;

public class Peon extends Pieza {
    public Peon(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2659" : "\u265F";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();
        int direccion = (this.color == ColorPieza.BLANCO) ? -1 : 1;
        int unaCasillaAdelante = fila + direccion;
        if (unaCasillaAdelante >= 0 && unaCasillaAdelante < 8 && tablero.getPieza(unaCasillaAdelante, col) == null) {
            movimientos.add(new int[]{unaCasillaAdelante, col});
            boolean esPrimerMovimiento = (this.color == ColorPieza.BLANCO && fila == 6) || (this.color == ColorPieza.NEGRO && fila == 1);
            int dosCasillasAdelante = fila + 2 * direccion;
            if (esPrimerMovimiento && tablero.getPieza(dosCasillasAdelante, col) == null) {
                movimientos.add(new int[]{dosCasillasAdelante, col});
            }
        }
        int[] colsDeCaptura = {col - 1, col + 1};
        for (int nextCol : colsDeCaptura) {
            if (unaCasillaAdelante >= 0 && unaCasillaAdelante < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnDiagonal = tablero.getPieza(unaCasillaAdelante, nextCol);
                if (piezaEnDiagonal != null && piezaEnDiagonal.getColor() != this.color) {
                    movimientos.add(new int[]{unaCasillaAdelante, nextCol});
                }
            }
        }
        int[] ultimoDoblePaso = tablero.getUltimoMovimientoDoblePeon();
        if (ultimoDoblePaso != null) {
            int peonFila = ultimoDoblePaso[0];
            int peonCol = ultimoDoblePaso[1];
            if (peonFila == fila && Math.abs(peonCol - col) == 1) {
                boolean filaCorrecta = (this.color == ColorPieza.BLANCO && fila == 3) || (this.color == ColorPieza.NEGRO && fila == 4);
                if (filaCorrecta) {
                    movimientos.add(new int[]{fila + direccion, peonCol});
                }
            }
        }
        return movimientos;
    }
}
