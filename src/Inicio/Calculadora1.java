package Inicio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class Calculadora1 extends JFrame {
    private JTextField display;
    private JTextField historial;
    private double resultado = 0;
    private double memoria = 0;
    private String operacionPendiente = "";
    private boolean iniciandoNumero = true;
    private boolean hayPunto = false;
    private StringBuilder numeroActual = new StringBuilder();
    private StringBuilder expresionCompleta = new StringBuilder();
    
    
    private final Color COLOR_FONDO = new Color(18,18,18);
    private final Color COLOR_DISPLAY = new Color(30, 30, 30);
    private final Color COLOR_BOTON_NUMERO = new Color(40, 40, 40);
    private final Color COLOR_BOTON_OPERACION = new Color(50, 120, 120);
    private final Color COLOR_BOTON_FUNCION = new Color(60, 60, 60);
    private final Color COLOR_BOTON_IGUAL = new Color(0, 150, 136);
    private final Color COLOR_TEXTO = new Color(225, 225, 225);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(180, 180, 180);

    public Calculadora1() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Calculadora Profesional");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setPreferredSize(new Dimension(400, 600));
    }

    private void inicializarComponentes() {
       
        JPanel panelDisplays = new JPanel(new GridLayout(2, 1));
        panelDisplays.setBackground(COLOR_FONDO);
        panelDisplays.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        
        historial = new JTextField();
        historial.setPreferredSize(new Dimension(380, 30));
        historial.setBackground(COLOR_DISPLAY);
        historial.setForeground(COLOR_TEXTO_SECUNDARIO);
        historial.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        historial.setHorizontalAlignment(JTextField.RIGHT);
        historial.setEditable(false);
        historial.setBorder(null);

        
        display = new JTextField("0");
        display.setPreferredSize(new Dimension(380, 50));
        display.setBackground(COLOR_DISPLAY);
        display.setForeground(COLOR_TEXTO);
        display.setFont(new Font("Segoe UI", Font.BOLD, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBorder(null);

        panelDisplays.add(historial);
        panelDisplays.add(display);
        add(panelDisplays, BorderLayout.NORTH);

        
        JPanel panelBotones = new JPanel(new GridLayout(7, 4, 5, 5));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        
        String[][] botones = {
            {"-", "-", "-", "-"},
            {"C", "⌫", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"±", "0", ".", "="}
        };

        
        for (String[] fila : botones) {
            for (String textoBoton : fila) {
                JButton boton = crearBoton(textoBoton);
                panelBotones.add(boton);
            }
        }

        add(panelBotones, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(80, 60));

        
        if (texto.matches("[0-9]")) {
            configurarEstiloBoton(boton, COLOR_BOTON_NUMERO, COLOR_TEXTO);
        } else if (texto.matches("[+\\-×÷]")) {
            configurarEstiloBoton(boton, COLOR_BOTON_OPERACION, COLOR_TEXTO);
        } else if (texto.equals("=")) {
            configurarEstiloBoton(boton, COLOR_BOTON_IGUAL, COLOR_TEXTO);
        } else {
            configurarEstiloBoton(boton, COLOR_BOTON_FUNCION, COLOR_TEXTO);
        }

        
        boton.addActionListener(e -> procesarBoton(texto));

        return boton;
    }

    private void configurarEstiloBoton(JButton boton, Color colorFondo, Color colorTexto) {
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        
        
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorFondo.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorFondo);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                boton.setBackground(colorFondo.darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boton.setBackground(colorFondo);
            }
        });
    }

    private void procesarBoton(String boton) {
        switch (boton) {
            case "C":
                limpiarTodo();
                break;
            case "⌫":
                borrarUltimo();
                break;
            case "=":
                calcular();
                break;
            case "±":
                cambiarSigno();
                break;
            case "%":
                calcularPorcentaje();
                break;
            case "MC":
                memoria = 0;
                break;
            case "MR":
                mostrarMemoria();
                break;
            case "M+":
                sumarAMemoria();
                break;
            case "M-":
                restarAMemoria();
                break;
            case "+":
            case "-":
            case "×":
            case "÷":
                procesarOperador(boton);
                break;
            case ".":
                agregarPuntoDecimal();
                break;
            default:
                procesarNumero(boton);
                break;
        }
    }

    private void limpiarTodo() {
        numeroActual.setLength(0);
        expresionCompleta.setLength(0);
        resultado = 0;
        operacionPendiente = "";
        iniciandoNumero = true;
        hayPunto = false;
        display.setText("0");
        historial.setText("");
    }

    private void borrarUltimo() {
        if (numeroActual.length() > 0) {
            char ultimoCaracter = numeroActual.charAt(numeroActual.length() - 1);
            numeroActual.setLength(numeroActual.length() - 1);
            if (ultimoCaracter == '.') {
                hayPunto = false;
            }
            if (numeroActual.length() == 0) {
                display.setText("0");
                iniciandoNumero = true;
            } else {
                display.setText(numeroActual.toString());
            }
        }
    }

    private void calcular() {
        if (!operacionPendiente.isEmpty() && !iniciandoNumero) {
            double numActual = Double.parseDouble(numeroActual.toString());
            switch (operacionPendiente) {
                case "+":
                    resultado += numActual;
                    break;
                case "-":
                    resultado -= numActual;
                    break;
                case "×":
                    resultado *= numActual;
                    break;
                case "÷":
                    if (numActual != 0) {
                        resultado /= numActual;
                    } else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }
            expresionCompleta.append(numeroActual).append(" = ");
            historial.setText(expresionCompleta.toString());
            display.setText(formatearResultado(resultado));
            numeroActual.setLength(0);
            numeroActual.append(resultado);
            operacionPendiente = "";
            iniciandoNumero = true;
        }
    }

    private void cambiarSigno() {
        if (numeroActual.length() > 0) {
            if (numeroActual.charAt(0) == '-') {
                numeroActual.deleteCharAt(0);
            } else {
                numeroActual.insert(0, '-');
            }
            display.setText(numeroActual.toString());
        }
    }

    private void calcularPorcentaje() {
        if (numeroActual.length() > 0) {
            double valor = Double.parseDouble(numeroActual.toString());
            valor = resultado * (valor / 100.0);
            numeroActual.setLength(0);
            numeroActual.append(formatearResultado(valor));
            display.setText(numeroActual.toString());
        }
    }

    private void mostrarMemoria() {
        numeroActual.setLength(0);
        numeroActual.append(formatearResultado(memoria));
        display.setText(numeroActual.toString());
        iniciandoNumero = true;
    }

    private void sumarAMemoria() {
        if (numeroActual.length() > 0) {
            memoria += Double.parseDouble(numeroActual.toString());
        }
    }

    private void restarAMemoria() {
        if (numeroActual.length() > 0) {
            memoria -= Double.parseDouble(numeroActual.toString());
        }
    }

    private void procesarOperador(String operador) {
        if (numeroActual.length() > 0) {
            if (!iniciandoNumero) {
                calcular();
            }
            resultado = Double.parseDouble(numeroActual.toString());
            operacionPendiente = operador;
            expresionCompleta.setLength(0);
            expresionCompleta.append(resultado).append(" ").append(operador).append(" ");
            historial.setText(expresionCompleta.toString());
            iniciandoNumero = true;
            hayPunto = false;
        }
    }

    private void agregarPuntoDecimal() {
        if (!hayPunto) {
            if (iniciandoNumero) {
                numeroActual.setLength(0);
                numeroActual.append("0");
            }
            numeroActual.append(".");
            display.setText(numeroActual.toString());
            hayPunto = true;
            iniciandoNumero = false;
        }
    }

    private void procesarNumero(String numero) {
        if (iniciandoNumero) {
            numeroActual.setLength(0);
            iniciandoNumero = false;
        }
        numeroActual.append(numero);
        display.setText(numeroActual.toString());
    }

    private String formatearResultado(double valor) {
        if (valor == (long) valor) {
            return String.format("%d", (long) valor);
        } else {
            return String.format("%.8f", valor).replaceAll("0*$", "").replaceAll("\\.$", "");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new Calculadora1().setVisible(true);
        });
    }
}