import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
