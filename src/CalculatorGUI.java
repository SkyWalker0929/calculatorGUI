import parser.Parser;
import resources.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class CalculatorGUI {
    private JFrame frame = new JFrame();
    private JPanel panel = new JPanel();
    private JTextArea textArea;

    private String[] buttonNames = {
                            "/", "1", "2", "3",
                            "*", "4", "5", "6",
                            "-", "7", "8", "9",
                            "+", "(", "0", ")",
                            "^", "%", ".", " "};

    public CalculatorGUI() {
        frame.setLayout(new BorderLayout());
        frame.setSize(250, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Calculator");

        ImageIcon image = new ImageIcon(
                ResourceLoader.getInstance().getFileFromPath(
                        "resources/icons/calculatorIcon.png",
                        "calcIcon",
                        ".png"));
        frame.setIconImage(image.getImage());
        init();
    }

    public static void main(String[] args) {
        CalculatorGUI calculator = new CalculatorGUI();
        calculator.showGUI();
    }

    private void init() {
        frame.add(panel, BorderLayout.CENTER);

        panel.setLayout(new GridLayout(6, 4));

        initTextArea();
        frame.add(textArea, BorderLayout.NORTH);

        createOperationAndSymbolButtons();
        createSpecialButtons();
    }

    private void createOperationAndSymbolButtons() {
        for(String buttonName: buttonNames) {
            panel.add(getButton(buttonName));
        }
    }

    private void createSpecialButtons() {
        panel.add(getButton("C", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        }));

        panel.add(getButton("<", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textArea.getText().length() > 0)
                    textArea.setText(textArea.getText().substring(0, textArea.getText().length() - 1));
            }
        }));

        panel.add(getButton("≠", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAnswer();
            }
        }));

        panel.add(getButton("=", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAnswer();
                BigDecimal result = Parser.calculate(textArea.getText());
                textArea.setText(textArea.getText() + "=" + result);
            }
        }));
    }

    private JButton getButton(String name) {
        JButton button = new JButton(name);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.append(name);
            }
        });
        return button;
    }

    private JButton getButton(String name, ActionListener listener) {
        JButton button = new JButton(name);
        button.addActionListener(listener);
        return button;
    }

    private void initTextArea() {
        textArea = new JTextArea(3, 20);
        textArea.setFont(new Font("Arial", Font.PLAIN, 18));
        textArea.setLineWrap(true);
    }

    private void clearAnswer() {
        String text = textArea.getText();
        if(!text.isEmpty()) {
            String source = text.split("=")[0];
            textArea.setText(source);
        }
    }

    public void showGUI() {
        frame.setVisible(true);
    }
}
