/**
 * Menu bar class
 *
 * @author (Piper Inns Hall)
 * @version (16/07/2023)
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Menu extends JFrame implements ActionListener {
    static final int BOARD_SIZE = Board.SQR_SIZE * Board.SQR_AMOUNT;
    public static Board panel = new Board();
    private static JTextArea textArea; // Declare the JTextArea
    public Menu() {
        setTitle("Chess");
        this.getContentPane().setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        contents();
        this.add(panel);

        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
    public void contents() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu;

        menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem menuItem;

        menuItem = new JMenuItem("New");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Load");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Clear");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        this.setJMenuBar(menuBar);

        menu = new JMenu("Move Notation");
        menuBar.add(menu);

        int menuWidth = menu.getPreferredSize().width;
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(menuWidth, 200));
        menu.add(scrollPane);
    }
    public static void updateTextArea(String newText) {
        textArea.append(newText);
    }
    public static void clearTextArea() {
        textArea.setText(""); // Clear the content of the textArea
    }
    public void actionPerformed(ActionEvent e) {
        String cmd=e.getActionCommand();
        switch (cmd) {
            case "New" -> { new newGame(); clearTextArea(); Board.selectedPiece = null; }
            case "Save" -> { if (!Piece.checkmated) new Save(Board.ps); else System.out.println("can't save checkmate"); }
            case "Load" -> { new Load(); clearTextArea(); Board.selectedPiece = null; }
            case "Clear" -> { newGame.clearing(); panel.repaint(); clearTextArea(); Board.selectedPiece = null; }
            default -> System.out.println("Invalid input");
        }
    }
}