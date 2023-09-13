import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/*-------|---------|---------|---------|---------|---------|---------|---------|
 Prof. Boothe - Based on Reges & Stepp Drawing Panel
 Revised 8/9/2020

 In 2018 with Java 10, the Reges & Stepp drawing panel sometimes has severe
 pixelation problems. This is due to the new point based scaling Java added for
 high dpi screens. There is now a mismatch between their internal image's 
 resolution and the screen. Scaling of the image occurs when it is mapped to the
 screen. Sometimes, such as on my desktop where that scaling factor is 1.25, it
 looks very bad. 

 Their code is 5000 lines long and tries to do lots of things. This drawing
 panel is much simpler and just does one thing well: providing an easy to use
 drawing panel. The basic idea is that of the original DrawingPanel: to avoid
 the normal call back mechanism of the Java awt and instead have students draw
 to an image, and then the drawing panel periodically updates that to the
 screen.

 The change to my drawing panel is that rather than having an image of the
 specified pixel size and scaling it to fit the larger actual jpanel on the
 screen. I scale the drawing of the image onto a larger image panel that then
 exactly fits the jpanel. This allows the higher resolution of the screen to be
 used for drawing, but without the distorting effects of the scaling. 

 The drawing panel has public methods:
   setBackground(color)
   getGraphics() - which return the Graphics2D of the image used for drawing
   sleep(millis) - sleep milliseconds
   clear()
   addKeyListener() - so I can get keyboard events for some demos
   repaint() - used for animation, the first use disables automatic repainting
               so that repaininting is done manually by calling this method.
               Hopefully this will eliminate flicker problems.

 It sets the panel to stay on top because the old drawing panel would sometimes
 end up hidden behind eclipse.

 It also writes a hidden file .DrawingPanelDefaults that remembers where a
 user moves the drawing panel. Future panels appear "near" that location, but
 randomly moved a bit so that repeated panels aren't exactly on top of each 
 other.

 Notes:
 A user might expect a fillRect(0,0,400,400) to just fit inside a DrawingPanel
 of size 400x400, but it does not because the DrawingPanel coordinates are
 only [0,399]
 
 Bugs: 
 10/2018 On Windows OS it appears that the frame title bar has a minimum size
 that is rather large so that small drawing panels get extended horizontally. 
 Especially so on large scaling factor displays.
 >>> I have not figured out how to work around that.
 10/2018 On windows the actual panel seems to be 1 point smaller in width than
 asked for.
 >>> I could add an extra pixel, put that makes other machines panels too large.
 10/2018 For one student in the lab the drawing panel was always created as
 minimized. 
 >>> I could not recreate problem.
 10/2018 On macs there is a several second delay when fonts are first used. This
 is a known problem with Java 10, strangely it does not occur for the old drawing
 panel and does not occur on windows.
*/
public class DrawingPanel implements ComponentListener {
    private JFrame frame;
    private JPanel panel;           // panel for showing the drawing 
    private BufferedImage image;    // drawing done on this image
    private Graphics2D image_g2;    // graphics context for drawing 
    private double xScale, yScale;  // the scaling being applied from points to pixels
    private Timer repaintTimer;     // repaint timer
    private static final int DELAY = 100; // delay between repaints in millis
    private Timer movedTimer;       // for updating defaults after moved

    // values from defaults file
    private static final String DEFAULTS_FILE = ".DrawingPanelDefaults"; 
    private boolean hasDefaults = false;    
    private int windowX, windowY;
    /**
     * Constructs a drawing panel of given width and height enclosed in a window.
     * @param width panel's width in points
     * @param height panel's height in points
     */
    public DrawingPanel(int width, int height) {
        frame = new JFrame("Drawing Panel");
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        readDefaultsFile();

        panel = new ImagePanel();
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(Color.WHITE);
        frame.getContentPane().add(panel);
        frame.pack();  // fits frame to panel's size
        setLocation(frame); 
        frame.addComponentListener(this);

        // find out real pixel size of panel
        Graphics2D jpanel_g2 = (Graphics2D)panel.getGraphics();
        AffineTransform transform = jpanel_g2.getTransform();
        xScale = transform.getScaleX();  
        yScale = transform.getScaleY();
        // calculate image size in pixels
        int imageWidth = (int)Math.ceil(width*xScale);
        int imageHeight = (int)Math.ceil(height*yScale);

        // make image size the real pixel size
        // and apply the scaling to the image's g2
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        image_g2 = (Graphics2D) image.getGraphics();
        image_g2.setTransform(transform);  // instead use the scaling transform on the image
        image_g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        image_g2.setColor(Color.BLACK);   // otherwise defaults to white ?

        frame.setVisible(true);
        frame.repaint();  // to get initial panel up immediately

        // set up a timer for repeated repainting
        repaintTimer = new Timer(DELAY, new PeriodicRepaint());
        repaintTimer.start();
    }

    /**
     * set the background color for the panel
     * @param color
     */
    public void setBackground(Color color) {
        panel.setBackground(color);
    }

    /**
     * get the graphics context for drawing operations
     * @return
     */
    public Graphics2D getGraphics() {
        return image_g2;
    }

    /**
     * Causes the program to pause for the given amount of time in milliseconds.
     * This allows for animation by calling pause in a loop.
     * The idea for animation is that the user draws something, calls sleep, and
     * then quickly redraws the panel.
     * @param millis number of milliseconds to sleep
     */
    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            // ignore InterruptedException
        }
    }
    
    /**
     * Used for animation to specify repainting times.
     * The first call turns of the repaint timer
     * From then on repainting only happens by calling repaint
     * Hopefully using this will eliminate flickering issues.
     */
    public void repaint() {
       repaintTimer.stop();  // stop repaint timer, from now on must call this method
       panel.repaint();      // repaint their drawing to the screen
    }

    /**
     * clear the drawing panel image
     */
    public void clear() {
        image_g2.setBackground(new Color(0, 0, 0, 0));
        image_g2.clearRect(0, 0, image.getWidth(), image.getHeight());
    }

    // this is the jpanel that just shows the image with no scaling
    // this gets a fresh graphics context every time it is called
    // Ideally we could just set the transform to the Identity transform,
    // but on partial redraws it appears they are using a clipping region
    // and then using the transform to offset the drawing so that (0,0)
    // is the upper corner of the clipping region. 
    // I thus have to reverse the scaling of their transform
    // (This is a nested class so it can access DrawingPanel fields)
    // overrides paint() no double buffering
    // rather than paintComponent() that does double buffering
    @SuppressWarnings("serial")
    private class ImagePanel extends JPanel {
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D)g;
            AffineTransform t = g2.getTransform();
            t.scale(1.0/xScale, 1.0/yScale);
            g2.setTransform(t);
            g2.drawImage(image, 0, 0, this);
        }
    }

    // handler for timer events to repaint (update) the jpanel
    // with anything new drawn to the image
    private class PeriodicRepaint implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            panel.repaint();
        }
    }

    // Moves the given JFrame to the location from the
    // defaults file, or if none, to the center of the screen.
    private void setLocation(Window frame) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screen = tk.getScreenSize();
        if (!hasDefaults) { // if no defaults file, center it on the screen
            windowX = (screen.width - frame.getWidth()) / 2;
            windowY = (screen.height - frame.getHeight()) / 2;
        }
        // randomly perturb it a little bit so that multiple panels aren't
        // all on top of each other
        Random rand = new Random();
        windowX += rand.nextInt(20);
        windowY += rand.nextInt(20);
        // assure it is on screen
        windowX = Math.min(windowX, screen.width - frame.getWidth());
        windowY = Math.min(windowY, screen.height - frame.getHeight());         
        windowX = Math.max(0, windowX);
        windowY = Math.max(0, windowY);

        //System.out.printf("initial (%d, %d)\n", windowX, windowY);
        frame.setLocation(windowX, windowY);
    }

    // To remember where the user repositions the drawing panel to,
    // I create a file .DrawingPanelDefaults with content:
    // location xxx yyy
    // this method reads that file
    private void readDefaultsFile() {
        File file = new File(DEFAULTS_FILE);
        Scanner fileScnr = null, lineScnr = null;
        if (file.exists()) {
            try {
                fileScnr = new Scanner(file);
                String line = fileScnr.nextLine();
                lineScnr = new Scanner(line);
                String param = lineScnr.next(); 
                switch (param) {
                case "location":
                    windowX = lineScnr.nextInt();
                    windowY = lineScnr.nextInt();
                    hasDefaults = true;
                    break;
                default:
                    System.err.printf("unexpected parameter (%s) in defaults file\n",
                            param);
                }
            } catch (Exception e) {
                System.err.println("Error reading defaults file");
            } finally {
                if (fileScnr != null) fileScnr.close();
                if (lineScnr != null) lineScnr.close();
            }
        }
    }

    // write defaults file
    private void writeDefaultsFile() {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(DEFAULTS_FILE,false);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            Point p = frame.getLocationOnScreen();
            pw.printf("location %d %d\n", p.x, p.y);
        } catch (Exception e) {
            System.err.println("Error while writing defaults file");
        } finally {
            pw.close();
        }
    }

    /**
     * Adds the given event listener to respond to key events on this panel.
     * by Reges & Stepp
     * @param listener the key event listener to attach
     */
    public void addKeyListener(KeyListener listener) {
        ensureNotNull("listener", listener);
        frame.addKeyListener(listener);
        panel.setFocusable(false);
        frame.requestFocusInWindow();
        frame.requestFocus();
    }

    /*
     * Helper that throws a NullPointerException if the given value is null 
     * Reges & Stepp
     */
    private static void ensureNotNull(String name, Object value) {
        if (value == null) {
            throw new NullPointerException("null value was passed for " + name);
        }
    }

    // When the user moves the drawing panel, I want to remember where they moved it to,
    // so that next time I can open it at that preferred spot.
    // The plan is to catch a componentMoved event, wait a couple seconds until it
    // is probably done moving, and then write the new location to the defaults file.
    // I start a timer and set a boolean flag to inhibit future timer until the movement
    // has been recorded.
    //
    // It gets a moved event when it is first drawn. Once even before it was drawn!
    // To be a ComponentListener,
    // I need to have all these methods, but the only one I use is "Moved:
    private boolean movementInProgress = false; // set when movement starts, reset when recorded

    @Override
    public void componentMoved(ComponentEvent e) {
        if (movementInProgress) // already started the timer
            return;
        if (!frame.isShowing()) // because I once got an exception before it was drawn
            return;
        Point p = frame.getLocationOnScreen();
        if (p.x == windowX && p.y == windowY)
            return;  // it hasn't actually moved

        //System.out.printf("moved (%d, %d)\n", p.x, p.y);

        // set up a timer for delayed writing after 2 seconds
        movementInProgress = true;
        movedTimer = new Timer(2000, new UpdateDefaults());
        movedTimer.setRepeats(false);
        movedTimer.start();
    }

    @Override
    public void componentResized(ComponentEvent e) { return; }
    @Override
    public void componentShown(ComponentEvent e) { return; }
    @Override
    public void componentHidden(ComponentEvent e) { return; }

    // handler for timer events to repaint (update) the jpanel
    // with anything new drawn to the image
    private class UpdateDefaults implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            writeDefaultsFile();
            movementInProgress = false;
        }
    }
}