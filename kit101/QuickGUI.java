package kit101;

import java.awt.*;
import javax.swing.*;

/**
 * A base class for creating simple GUIs. Implemented as an extension of
 * {@code JPanel} that creates a {@code JFrame} for whom it will be the content
 * pane. This means it can be subclassed to create a simple GUI application
 * without having to separately create a JFrame.
 * <p>
 * Usage: Extend this class, and in your new class:
 * <ol>
 *   <li>If you want to draw a customised image then override the
 *   {@link #paintComponent(Graphics)} method to draw the GUI as you would like
 *   it to be. Make sure that the first command in this method is
 *   {@code super.paintComponent(g)}.</li>
 *   <li>If you want any "widgets" (JLabels, JTextFields, JButtons, etc.) in
 *   the GUI then declare these as instance variables, write a constructor for
 *   the class that instantiates the widgets and {@linkplain #add(Component)
 *   adds} them  to the GUI.</li>
 * </ol>
 * <p>
 * Then create an object of the new class in a {@code main()} method somewhere
 * to start the GUI application.
 * <p>
 * Note that objects of this class:
 * <ul>
 *   <li>will close when the close window icon in clicked (indeed, will cause the
 *   current program to exit when the window is closed);</li>
 *   <li>have a default size of 500 x 500 pixels; and</li>
 *   <li>use the basic {@link FlowLayout} layout manager</li>
 * </ul>
 * @author James Montgomery
 * @author Robyn Gibson
 * @version 2.1: April 2014
 */
public class QuickGUI extends JPanel {
    /**
     * A requirement of later versions of Java; used when converting the in-memory
     * object to a binary format for writing to disk or sending over a network.
     */
    private static final long serialVersionUID = 1L;

    /** The default width of the window the QuickGUI creates. */
    protected static final int DEFAULT_WIDTH = 500;
    /** The default height of the window the QuickGUI creates. */
    protected static final int DEFAULT_HEIGHT = 500;

    /** The top level container for this panel. */
    protected JFrame home;
    /** Simple control over debugging messages, during development. */
    protected boolean debugging;


    /**
     * Creates a new QuickGUI with a default size and title.
     * @see #QuickGUI(String)
     */
    public QuickGUI() {
        this("New KIT101 Viewing Frame");
    }

    /**
     * Creates a new QuickGUI with its default size of 500x500 and the given
     * title. Also creates a new JFrame (window) that will have this panel as its
     * content pane, and which will quit the program when the window's close icon
     * is clicked.
     */
    public QuickGUI(String title) {
        home = new JFrame(title);

        debugging = false;
        setPreferredSize( new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT) );
        setVisible(true);
        home.setContentPane(this);
        home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        home.pack();
        home.setVisible(true);
    }

    /**
     * Sets the width of this panel and repaints
     * @param w the new width
     */
    public void setWidth(int w)    {
        debug("setting width to " + w);
        Dimension size = getSize();
        size.width = w;
        setSize(size);
        setPreferredSize(size);
        home.pack();
        repaint();
    }

    /**
     * Sets the height of this panel and repaints
     * @param h the new height
     */
    public void setHeight(int h) {
        debug("setting height to " + h);
        Dimension size = getSize();
        size.height = h;
        setSize(size);
        setPreferredSize(size);
        home.pack();
        repaint();
    }

    /**
     * Sets the window title. Actually a pass-through to the panel's frame's
     * setTitle().
     * @param title the new window title
     */
    public void setTitle(String title) {
        home.setTitle(title);
    }

    /**
     * Prints a debug message (preceded by the class's name) to standard output
     * if debugging is enabled.
     * @param message  the debugging message to output
     */
    public void debug(String message)     {
        if(debugging) {
            System.out.println(getClass().getSimpleName() + ": " + message);
        }
    }

    /**
     * Turns debugging messages on or off.
     * @param on if true then debugging messages will be output
     */
    public void setDebugging(boolean on) {
        debugging = on;
    }

}
