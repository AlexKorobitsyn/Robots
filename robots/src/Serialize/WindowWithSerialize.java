package Serialize;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.swing.JInternalFrame;

public class WindowWithSerialize extends JInternalFrame implements ObjectWithState {

    private String path;
    private int x;
    private int y;
    private boolean isIcon = false;
    private Dimension dimension;

    public WindowWithSerialize(String title, boolean b, boolean b1, boolean b2, boolean b3,
                               String path) {
        super(title, b, b1, b2, b3);
        setPath(path);
    }

    public void setLocation() {
        setLocation(this.x, this.y);
    }

    public boolean getStateIcon() {
        return this.isIcon;
    }

    public void setSize() {
        setSize(getDimension());
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public static boolean isFileExists(File file) {
        return file.isFile();
    }

    @Override
    public void save() {
        try (OutputStream os = new FileOutputStream(getPath())) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os))) {
                Point point = this.getLocation();
                oos.writeObject(point.x);
                oos.writeObject(point.y);
                oos.writeObject(this.isIcon());
                oos.writeObject(this.getSize());
                oos.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void restore() {
        String path = getPath();
        File file = new File(path);

        if (!isFileExists(file)) {
            this.x = 50;
            this.y = 50;
            this.isIcon = false;
            this.dimension = new Dimension(500, 300);
        } else {
            System.out.println("FileExist");
            try (InputStream is = new FileInputStream(path)) {
                try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is))) {
                    this.x = (int) ois.readObject();
                    this.y = (int) ois.readObject();
                    this.isIcon = (boolean) ois.readObject();
                    this.dimension = (Dimension) ois.readObject();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}