package gui;

import Serialize.WindowWithSerialize;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.*;

public class GameWindow extends WindowWithSerialize
{
    private final GameVisualizer m_visualizer;
    public GameWindow()
    {
        super("Игровое поле", true, true, true, true,  System.getProperty("user.home") + File.separator + "dataGameWindow.txt");
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
        restore();
    }
}