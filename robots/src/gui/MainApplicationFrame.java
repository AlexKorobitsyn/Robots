package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);
        Rectangle logRec = new Rectangle(10, 10, 300, 800);
        Rectangle gameRec = new Rectangle(0, 0, 400, 400);
        HashMap<String, Rectangle> mapCheck = new HashMap<>();
        mapCheck.put("\"Протокол работы\"", logRec);
        mapCheck.put("\"Игровое поле\"", gameRec);
        LogWindow logWindow = createLogWindow();
        GameWindow gameWindow = new GameWindow();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        try {
            String string = readUsingFiles(System.getenv("USERPROFILE") + "\\setting.txt");
            String[] arr = string.split("\n");
            if (arr[0].equals("1")) {
                for (String strSave : arr) {
                    String[] temp1 = strSave.split("(?<=[\"\\d]) ");
                    if (mapCheck.containsKey(temp1[0])) {
                        Rectangle tempRectangle = mapCheck.get(temp1[0]);
                        tempRectangle.x = Integer.parseInt(temp1[2].substring(2));
                        tempRectangle.y = Integer.parseInt(temp1[3].substring(2));
                        tempRectangle.width = Integer.parseInt(temp1[4].substring(6));
                        tempRectangle.height = Integer.parseInt(temp1[5].substring(7));
                    }
                }
            }
        } catch (IOException ignored) {
        }
        logWindow.setBounds(logRec);
        addWindow(logWindow);


        gameWindow.setBounds(gameRec);
        addWindow(gameWindow);
        setJMenuBar(generateMenuBar());
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed");
                try {
                    exit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private static String readUsingFiles(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }

    private JMenuBar generateMenuBar() {
        Locale current = new Locale("ru", "RU");
        ResourceBundle myResources =
                ResourceBundle.getBundle("text", current);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(generateLookAndFeelMenu());
        menuBar.add(generateTestMenu());
        menuBar.add(generateExitMenu());
        return menuBar;
    }
    private JMenu generateExitMenu(){
        JMenu exitMenu = new JMenu("Выход");
        exitMenu.setMnemonic(KeyEvent.VK_T);

        {

            JMenuItem standartExit = new JMenuItem("Выход", KeyEvent.VK_T);
            standartExit.addActionListener((event) -> {
                try {
                    exit();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            exitMenu.add(standartExit);
        }
        return exitMenu;
    }
    private JMenu generateTestMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }
    private JMenu generateLookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        return lookAndFeelMenu;
    }

    private void exit() throws IOException {
        Object[] options = {"Да",
                "Нет"};
        int s = JOptionPane.showOptionDialog(null, "Вы уверены что хотите выйти?", "Уведомление", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (s == JOptionPane.YES_OPTION) {
            saveData();
            System.exit(0);
        }
    }
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
    private void saveData() throws IOException {
        File file = new File(System.getenv("USERPROFILE") + "\\setting.txt");
        if (file.createNewFile()) {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
        }
        FileWriter writer = new FileWriter(file);
        writer.write("1\n");
        writer.write("Test data:\n");
        JInternalFrame[] arr = desktopPane.getAllFrames();
        for (JInternalFrame var : arr) {
            writer.write("\"" + var.getTitle() + "\" ");
            if (var.isIcon())
                writer.write("\"Не Свёрнуто\" ");
            else
                writer.write("\"Свёрнуто\" ");
            Rectangle a = var.getBounds();
            writer.write("x=" + a.x + " y=" + a.y + " width=" + a.width + " ");
            writer.write("height=" + a.height + "\n");
        }
        writer.close();
    }
}