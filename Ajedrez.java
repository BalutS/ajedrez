import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Ajedrez extends JFrame {
    private JButton[][] squares = new JButton[8][8];
    private Tablero tablero;
    private Pieza piezaSeleccionada = null;
    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;
    private boolean juegoTerminado = false;

    public Ajedrez() {
        tablero = new Tablero();
        setTitle("Ajedrez");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8));

        initializeBoard();
        updateBoard();
        actualizarTitulo();

        setVisible(true);
    }

    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setOpaque(true);
                button.setBorderPainted(false);
                button.setFont(new Font("SansSerif", Font.BOLD, 40));
                button.setActionCommand(row + "," + col);
                button.addActionListener(new PieceClickListener());
                squares[row][col] = button;
                add(button);
            }
        }
        resetearColoresCasillas();
    }

    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Pieza pieza = tablero.getPieza(row, col);
                if (pieza != null) {
                    squares[row][col].setText(pieza.getUnicodeSymbol());
                    if (pieza.getColor() == ColorPieza.BLANCO) {
                        squares[row][col].setForeground(Color.LIGHT_GRAY);
                    } else {
                        squares[row][col].setForeground(Color.BLACK);
                    }
                } else {
                    squares[row][col].setText("");
                }
            }
        }
    }

    private void resetearColoresCasillas() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    squares[i][j].setBackground(Color.WHITE);
                } else {
                    squares[i][j].setBackground(Color.DARK_GRAY);
                }
            }
        }
    }

    private void resaltarMovimientosValidos(List<int[]> movimientos) {
        for (int[] mov : movimientos) {
            squares[mov[0]][mov[1]].setBackground(new Color(144, 238, 144)); // Verde claro
        }
    }

    private void actualizarTitulo() {
        if (juegoTerminado) return;
        ColorPieza turno = tablero.getTurnoActual();
        String titulo = "Ajedrez - Turno de " + (turno == ColorPieza.BLANCO ? "Blancas" : "Negras");
        if (tablero.estaEnJaque(turno)) {
            titulo += " (¡Jaque!)";
        }
        setTitle(titulo);
    }

    private void promocionarPeon(int fila, int col) {
        Object[] opciones = {"Reina", "Torre", "Alfil", "Caballo"};
        String piezaElegida = (String) JOptionPane.showInputDialog(this, "Elige una pieza para la promoción:", "Promoción de Peón", JOptionPane.PLAIN_MESSAGE, null, opciones, "Reina");
        if (piezaElegida != null) {
            tablero.promocionarPeon(fila, col, piezaElegida);
        } else {
            tablero.promocionarPeon(fila, col, "Reina");
        }
    }

    private void verificarFinDePartida() {
        ColorPieza turnoActual = tablero.getTurnoActual();
        if (!tablero.tieneMovimientosLegales(turnoActual)) {
            juegoTerminado = true;
            if (tablero.estaEnJaque(turnoActual)) {
                JOptionPane.showMessageDialog(this, "¡Jaque Mate! Ganan las " + (turnoActual == ColorPieza.BLANCO ? "Negras" : "Blancas") + ".");
                setTitle("Jaque Mate");
            } else {
                JOptionPane.showMessageDialog(this, "¡Tablas por Ahogado!");
                setTitle("Tablas por Ahogado");
            }
        }
    }

    private class PieceClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (juegoTerminado) return;

            String command = e.getActionCommand();
            int fila = Integer.parseInt(command.split(",")[0]);
            int col = Integer.parseInt(command.split(",")[1]);

            if (piezaSeleccionada == null) {
                Pieza pieza = tablero.getPieza(fila, col);
                if (pieza != null && pieza.getColor() == tablero.getTurnoActual()) {
                    piezaSeleccionada = pieza;
                    filaSeleccionada = fila;
                    colSeleccionada = col;
                    resetearColoresCasillas();
                    squares[fila][col].setBackground(new Color(173, 216, 230)); // Azul claro
                    resaltarMovimientosValidos(getMovimientosLegales(pieza, fila, col));
                }
            } else {
                if (fila == filaSeleccionada && col == colSeleccionada) {
                    // Deseleccionar
                    piezaSeleccionada = null;
                    resetearColoresCasillas();
                } else if (esMovimientoValido(fila, col)) {
                    Pieza piezaMovida = tablero.getPieza(filaSeleccionada, colSeleccionada);
                    tablero.moverPieza(filaSeleccionada, colSeleccionada, fila, col);
                    if (piezaMovida instanceof Peon && (fila == 0 || fila == 7)) {
                        promocionarPeon(fila, col);
                    }
                    tablero.cambiarTurno();
                    updateBoard();
                    actualizarTitulo();
                    verificarFinDePartida();
                    piezaSeleccionada = null;
                    resetearColoresCasillas();
                } else {
                    // Clic en casilla no válida, deseleccionar
                     piezaSeleccionada = null;
                     resetearColoresCasillas();
                }
            }
        }

        private List<int[]> getMovimientosLegales(Pieza pieza, int fila, int col) {
            List<int[]> movimientosLegales = new java.util.ArrayList<>();
            List<int[]> movimientosPotenciales = pieza.getMovimientosValidos(fila, col, tablero);
            for (int[] mov : movimientosPotenciales) {
                if (!tablero.simularMovimientoYVerificarJaque(fila, col, mov[0], mov[1], pieza.getColor())) {
                    movimientosLegales.add(mov);
                }
            }
            return movimientosLegales;
        }

        private boolean esMovimientoValido(int destFila, int destCol) {
            if (piezaSeleccionada == null) return false;
            List<int[]> movimientos = getMovimientosLegales(piezaSeleccionada, filaSeleccionada, colSeleccionada);
            for (int[] move : movimientos) {
                if (move[0] == destFila && move[1] == destCol) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ajedrez());
    }
}

enum ColorPieza {
    BLANCO,
    NEGRO
}

abstract class Pieza {
    protected ColorPieza color;
    protected boolean haMovido;

    public Pieza(ColorPieza color) {
        this.color = color;
        this.haMovido = false;
    }

    public boolean haMovido() {
        return haMovido;
    }

    public void setHaMovido(boolean haMovido) {
        this.haMovido = haMovido;
    }

    public ColorPieza getColor() {
        return color;
    }

    public abstract String getUnicodeSymbol();

    public abstract List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero);

    @Override
    public String toString() {
        return getUnicodeSymbol();
    }
}

class Torre extends Pieza {
    public Torre(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2656" : "\u265C";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : direcciones) {
            int nextFila = fila + d[0];
            int nextCol = col + d[1];
            while (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null) {
                    movimientos.add(new int[]{nextFila, nextCol});
                } else {
                    if (piezaEnCasilla.getColor() != this.color) {
                        movimientos.add(new int[]{nextFila, nextCol});
                    }
                    break;
                }
                nextFila += d[0];
                nextCol += d[1];
            }
        }
        return movimientos;
    }
}

class Caballo extends Pieza {
    public Caballo(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2658" : "\u265E";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();
        int[][] lMovimientos = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        for (int[] m : lMovimientos) {
            int nextFila = fila + m[0];
            int nextCol = col + m[1];
            if (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null || piezaEnCasilla.getColor() != this.color) {
                    movimientos.add(new int[]{nextFila, nextCol});
                }
            }
        }
        return movimientos;
    }
}

class Alfil extends Pieza {
    public Alfil(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2657" : "\u265D";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();
        int[][] direcciones = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] d : direcciones) {
            int nextFila = fila + d[0];
            int nextCol = col + d[1];
            while (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null) {
                    movimientos.add(new int[]{nextFila, nextCol});
                } else {
                    if (piezaEnCasilla.getColor() != this.color) {
                        movimientos.add(new int[]{nextFila, nextCol});
                    }
                    break;
                }
                nextFila += d[0];
                nextCol += d[1];
            }
        }
        return movimientos;
    }
}

class Reina extends Pieza {
    public Reina(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2655" : "\u265B";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();
        int[][] direcciones = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        for (int[] d : direcciones) {
            int nextFila = fila + d[0];
            int nextCol = col + d[1];
            while (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null) {
                    movimientos.add(new int[]{nextFila, nextCol});
                } else {
                    if (piezaEnCasilla.getColor() != this.color) {
                        movimientos.add(new int[]{nextFila, nextCol});
                    }
                    break;
                }
                nextFila += d[0];
                nextCol += d[1];
            }
        }
        return movimientos;
    }
}

class Rey extends Pieza {
    public Rey(ColorPieza color) {
        super(color);
    }

    @Override
    public String getUnicodeSymbol() {
        return (color == ColorPieza.BLANCO) ? "\u2654" : "\u265A";
    }

    @Override
    public List<int[]> getMovimientosValidos(int fila, int col, Tablero tablero) {
        List<int[]> movimientos = new ArrayList<>();
        int[][] reyMovimientos = {
            {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
        };
        for (int[] m : reyMovimientos) {
            int nextFila = fila + m[0];
            int nextCol = col + m[1];
            if (nextFila >= 0 && nextFila < 8 && nextCol >= 0 && nextCol < 8) {
                Pieza piezaEnCasilla = tablero.getPieza(nextFila, nextCol);
                if (piezaEnCasilla == null || piezaEnCasilla.getColor() != this.color) {
                    movimientos.add(new int[]{nextFila, nextCol});
                }
            }
        }
        if (!haMovido && !tablero.estaEnJaque(this.color)) {
            Pieza torreDerecha = tablero.getPieza(fila, 7);
            if (torreDerecha instanceof Torre && !torreDerecha.haMovido()) {
                if (tablero.getPieza(fila, 5) == null && tablero.getPieza(fila, 6) == null) {
                    if (!tablero.simularMovimientoYVerificarJaque(fila, col, fila, 5, this.color) &&
                        !tablero.simularMovimientoYVerificarJaque(fila, col, fila, 6, this.color)) {
                        movimientos.add(new int[]{fila, 6});
                    }
                }
            }
            Pieza torreIzquierda = tablero.getPieza(fila, 0);
            if (torreIzquierda instanceof Torre && !torreIzquierda.haMovido()) {
                if (tablero.getPieza(fila, 1) == null && tablero.getPieza(fila, 2) == null && tablero.getPieza(fila, 3) == null) {
                    if (!tablero.simularMovimientoYVerificarJaque(fila, col, fila, 2, this.color) &&
                        !tablero.simularMovimientoYVerificarJaque(fila, col, fila, 3, this.color)) {
                        movimientos.add(new int[]{fila, 2});
                    }
                }
            }
        }
        return movimientos;
    }
}

class Peon extends Pieza {
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

class Tablero {
    private Pieza[][] tablero = new Pieza[8][8];
    private ColorPieza turnoActual;
    private int[] ultimoMovimientoDoblePeon = null;

    public Tablero() {
        setupInicial();
        this.turnoActual = ColorPieza.BLANCO;
    }

    public ColorPieza getTurnoActual() {
        return turnoActual;
    }

    public void cambiarTurno() {
        turnoActual = (turnoActual == ColorPieza.BLANCO) ? ColorPieza.NEGRO : ColorPieza.BLANCO;
    }

    public Pieza getPieza(int fila, int col) {
        return tablero[fila][col];
    }

    public int[] getUltimoMovimientoDoblePeon() {
        return ultimoMovimientoDoblePeon;
    }

    public void moverPieza(int fromFila, int fromCol, int toFila, int toCol) {
        Pieza pieza = getPieza(fromFila, fromCol);
        ultimoMovimientoDoblePeon = null;
        if (pieza instanceof Rey && Math.abs(fromCol - toCol) == 2) {
            tablero[toFila][toCol] = pieza;
            tablero[fromFila][fromCol] = null;
            pieza.setHaMovido(true);
            if (toCol == 6) {
                Pieza torre = getPieza(fromFila, 7);
                tablero[fromFila][5] = torre;
                tablero[fromFila][7] = null;
                torre.setHaMovido(true);
            } else {
                Pieza torre = getPieza(fromFila, 0);
                tablero[fromFila][3] = torre;
                tablero[fromFila][0] = null;
                torre.setHaMovido(true);
            }
        } else if (pieza instanceof Peon && fromCol != toCol && getPieza(toFila, toCol) == null) {
            tablero[toFila][toCol] = pieza;
            tablero[fromFila][fromCol] = null;
            tablero[fromFila][toCol] = null;
            pieza.setHaMovido(true);
        } else {
            if (pieza instanceof Peon && Math.abs(fromFila - toFila) == 2) {
                ultimoMovimientoDoblePeon = new int[]{toFila, toCol};
            }
            tablero[toFila][toCol] = pieza;
            tablero[fromFila][fromCol] = null;
            if (pieza != null) {
                pieza.setHaMovido(true);
            }
        }
    }

    private int[] encontrarRey(ColorPieza color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = getPieza(i, j);
                if (p instanceof Rey && p.getColor() == color) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public boolean estaEnJaque(ColorPieza colorRey) {
        int[] posRey = encontrarRey(colorRey);
        if (posRey == null) return false;
        ColorPieza colorAtacante = (colorRey == ColorPieza.BLANCO) ? ColorPieza.NEGRO : ColorPieza.BLANCO;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = getPieza(i, j);
                if (p != null && p.getColor() == colorAtacante) {
                    java.util.List<int[]> movimientos = p.getMovimientosValidos(i, j, this);
                    for (int[] mov : movimientos) {
                        if (mov[0] == posRey[0] && mov[1] == posRey[1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean simularMovimientoYVerificarJaque(int fromFila, int fromCol, int toFila, int toCol, ColorPieza colorRey) {
        Pieza piezaMovida = getPieza(fromFila, fromCol);
        Pieza piezaCapturada = getPieza(toFila, toCol);
        tablero[toFila][toCol] = piezaMovida;
        tablero[fromFila][fromCol] = null;
        boolean reyEnJaque = estaEnJaque(colorRey);
        tablero[fromFila][fromCol] = piezaMovida;
        tablero[toFila][toCol] = piezaCapturada;
        return reyEnJaque;
    }

    public boolean tieneMovimientosLegales(ColorPieza color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = getPieza(i, j);
                if (p != null && p.getColor() == color) {
                    java.util.List<int[]> movimientos = p.getMovimientosValidos(i, j, this);
                    for (int[] mov : movimientos) {
                        if (!simularMovimientoYVerificarJaque(i, j, mov[0], mov[1], color)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void promocionarPeon(int fila, int col, String tipoPieza) {
        Pieza peon = getPieza(fila, col);
        if (peon == null || !(peon instanceof Peon)) return;
        ColorPieza color = peon.getColor();
        Pieza nuevaPieza;
        switch (tipoPieza.toLowerCase()) {
            case "reina": nuevaPieza = new Reina(color); break;
            case "torre": nuevaPieza = new Torre(color); break;
            case "alfil": nuevaPieza = new Alfil(color); break;
            case "caballo": nuevaPieza = new Caballo(color); break;
            default: nuevaPieza = new Reina(color); break;
        }
        tablero[fila][col] = nuevaPieza;
    }

    private void setupInicial() {
        tablero[0][0] = new Torre(ColorPieza.NEGRO);
        tablero[0][1] = new Caballo(ColorPieza.NEGRO);
        tablero[0][2] = new Alfil(ColorPieza.NEGRO);
        tablero[0][3] = new Reina(ColorPieza.NEGRO);
        tablero[0][4] = new Rey(ColorPieza.NEGRO);
        tablero[0][5] = new Alfil(ColorPieza.NEGRO);
        tablero[0][6] = new Caballo(ColorPieza.NEGRO);
        tablero[0][7] = new Torre(ColorPieza.NEGRO);
        for (int i = 0; i < 8; i++) {
            tablero[1][i] = new Peon(ColorPieza.NEGRO);
        }
        tablero[7][0] = new Torre(ColorPieza.BLANCO);
        tablero[7][1] = new Caballo(ColorPieza.BLANCO);
        tablero[7][2] = new Alfil(ColorPieza.BLANCO);
        tablero[7][3] = new Reina(ColorPieza.BLANCO);
        tablero[7][4] = new Rey(ColorPieza.BLANCO);
        tablero[7][5] = new Alfil(ColorPieza.BLANCO);
        tablero[7][6] = new Caballo(ColorPieza.BLANCO);
        tablero[7][7] = new Torre(ColorPieza.BLANCO);
        for (int i = 0; i < 8; i++) {
            tablero[6][i] = new Peon(ColorPieza.BLANCO);
        }
    }
}
