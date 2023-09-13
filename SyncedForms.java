import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

public class SyncedForms {         // public class
    static Scanner scnr = new Scanner(System.in);
    static char formChar;
    static final int WIDTH = 50;
    static final int HEIGHT = 50;
    static final int deltaX = 25;
    static final int deltaY = 25;
    static int[] XArray;
    static int[] YArray;

    // Color[] colors;

    static Color[] colors = new Color[9];
    // static String[] formColor = new String[9];
    static Graphics2D g;
    static DrawingPanel panel;

    public static void initialPosition(int[] XArray, int[] YArray, int numForms) { //determines x and y coordinates of inital positions of forms

        initialColor(colors, numForms);

        SyncedForms.g = panel.getGraphics();

        SyncedForms.XArray = new int[] { 50, 250, 250, 50, 150, 50, 250, 150, 150 };
        SyncedForms.YArray = new int[] { 50, 50, 250, 250, 150, 150, 150, 50, 250 };
        // System.out.println("this is xarray length " + XArray.length);
        /*
         * if (formChar == 'C') { // if statemnt for either circle or square
         * for (int j = 0; j < numForms; j++) { // j is index for drawing the number of
         * shapes
         * g.setColor(colors[j]);
         * g.fillOval(XArray[j], YArray[j], WIDTH, HEIGHT);
         * // System.out.println(j + "this is j");
         * // System.out.println(numForms + "this is numForms");
         * 
         * }
         * } else if (formChar == 'S') {
         * for (int j = 0; j < numForms; j++) { // j is index for drawing the number of
         * shapes
         * g.setColor(colors[j]);
         * g.fillRect(XArray[j], YArray[j], WIDTH, HEIGHT);
         * }
         * }
         */

    }

    public static void drawSyncedForms() { //gets input , draws panel

        int numForms = 0;
        int numOfMoves = 0;
        // int XArray[] = new int[9];
        // int YArray[] = new int[9];
        // int[] XArray = { 50, 150, 150, 50, 100, 50, 150, 100, 100 };
        // int[] YArray = { 50, 50, 150, 150, 100, 100, 100, 50, 150 };

        System.out.println("What form will be shown (C-ircle or S-quare)?");
        formChar = scnr.next().charAt(0);
        System.out.println("How many forms you want to show (max 9)?");
        numForms = scnr.nextInt();
        System.out.println("How many times you want the forms to move? (max 500)?");
        numOfMoves = scnr.nextInt();
        // System.out.println("Please, input the different moves");

        SyncedForms.panel = new DrawingPanel(400, 400);

        initialPosition(XArray, YArray, numForms);
        // System.out.println("this is Xarray length " + XArray.length);
        moveForms(panel, formChar, XArray, YArray, colors, numForms, numOfMoves);

    }

    public static void initialColor(Color[] colors, int numForms) { //sets colors for each form

        for (int i = 0; i < numForms; i++) {
            if (i == 0) {
                colors[i] = Color.GREEN;
            }
            if (i == 1) {
                colors[i] = Color.GRAY;

            }
            if (i == 2) {
                colors[i] = Color.YELLOW;
            }
            if (i == 3) {
                colors[i] = Color.RED;
            }
            if (i == 4) {
                colors[i] = Color.ORANGE;
            }
            if (i == 5) {
                colors[i] = Color.PINK;
            }
            if (i == 6) {
                colors[i] = Color.DARK_GRAY;
            }
            if (i == 7) {
                colors[i] = Color.BLUE;
            }
            if (i == 8) {
                colors[i] = Color.BLACK;
            }

        }

    }

    public static void moveForms(DrawingPanel panel, char formChar, int[] XArray, int[] YArray, Color[] colors,
            int numForms,
            int numOfMoves) {
        int orientationVal;
        // System.out.println("this is Xarray length " + XArray.length);
        for (int i = 0; i < numForms; i++) {
            showForm(panel, formChar, XArray[i], YArray[i], colors[i], 50);

        }

        System.out.println("Please, input the different moves");
        // System.out.println(numOfMoves + " this is num of moves");

        for (int i = 0; i < numOfMoves; i++) { // more movements for loop

            orientationVal = scnr.nextInt();
            for (int j = 0; j < numForms; j++) {
                moveForm(XArray, YArray, j, orientationVal); // i coressponds to the form being displayed
                showForm(panel, formChar, XArray[j], YArray[j], colors[j], 50);

            }
        }
    }

    public static void moveForm(int[] XArray, int[] YArray, int indexForm, int orientationVal) { //method which controls where each form moves
        /*
         * System.out
         * .println(XArray[indexForm] + " this is x array with index form BEFORE   " +
         * indexForm + " :indexform");
         * System.out.println(YArray[indexForm] +
         * " this is yS array with index form BEFORE");
         */
        if (orientationVal == 0) { // orientation = 0 means that form moves to position 0
            YArray[indexForm] = YArray[indexForm] - deltaY;
        } else if (orientationVal == 1) {
            YArray[indexForm] = YArray[indexForm] - deltaY;
            XArray[indexForm] = XArray[indexForm] + deltaX;
        } else if (orientationVal == 2) {
            XArray[indexForm] = XArray[indexForm] + deltaX;
        } else if (orientationVal == 3) {
            XArray[indexForm] = XArray[indexForm] + deltaX;
            YArray[indexForm] = YArray[indexForm] + deltaY;
        } else if (orientationVal == 4) {
            YArray[indexForm] = YArray[indexForm] + deltaY;
        } else if (orientationVal == 5) {
            XArray[indexForm] = XArray[indexForm] - deltaX;
            YArray[indexForm] = YArray[indexForm] + deltaY;
        } else if (orientationVal == 6) {
            XArray[indexForm] = XArray[indexForm] - deltaX;
        } else if (orientationVal == 7) {
            XArray[indexForm] = XArray[indexForm] - deltaX;
            YArray[indexForm] = YArray[indexForm] - deltaY;
        } else {
            // do nothing
        }
        // System.out.println(XArray[indexForm] + " this is x array with index form");
        // ystem.out.println(YArray[indexForm] + " this is yS array with index form");

    }

    public static void showForm(DrawingPanel panel, char formChar, int xPos, int yPos, Color color,
            int sizeForm) {

        if (formChar == 'C') { // if statemnt for either circle or square

            g.setColor(color);
            g.fillOval(xPos, yPos, sizeForm, sizeForm);
            g.setColor(Color.BLACK);
            g.drawOval(xPos - 1, yPos - 1, sizeForm + 1, sizeForm + 1);
        } else if (formChar == 'S') {
            g.setColor(color);
            g.fillRect(xPos, yPos, sizeForm, sizeForm);
            g.setColor(Color.BLACK);
            g.drawRect(xPos - 1, yPos - 1, sizeForm + 1, sizeForm + 1);
        }
    }

    public static void main(String[] args) { //main method which calls drawSyncedForms which in turn calls all the other methods
        System.out.println("UTSA – Fall 2022 - CS1083 - Section 001 - Project 3 - SyncedForms - written by Omar Mian");

        drawSyncedForms();

        // panel.setBackground(Color.BLACK);

        g.setColor(Color.BLACK);
        g.fillRect(20, 200, 360, 40);
        g.setColor(Color.GREEN);
        g.drawRect(20, 200, 360, 40);

        g.setColor(Color.GREEN);
        g.drawString("UTSA - Fall 2022 - CS1083 - Section 001 - Project 3 –", 22, 215);
        g.drawString("SyncedForms - written by Omar Mian", 22, 235);

    }
}import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

public class SyncedForms {         // public class
    static Scanner scnr = new Scanner(System.in);
    static char formChar;
    static final int WIDTH = 50;
    static final int HEIGHT = 50;
    static final int deltaX = 25;
    static final int deltaY = 25;
    static int[] XArray;
    static int[] YArray;

    // Color[] colors;

    static Color[] colors = new Color[9];
    // static String[] formColor = new String[9];
    static Graphics2D g;
    static DrawingPanel panel;

    public static void initialPosition(int[] XArray, int[] YArray, int numForms) { //determines x and y coordinates of inital positions of forms

        initialColor(colors, numForms);

        SyncedForms.g = panel.getGraphics();

        SyncedForms.XArray = new int[] { 50, 250, 250, 50, 150, 50, 250, 150, 150 };
        SyncedForms.YArray = new int[] { 50, 50, 250, 250, 150, 150, 150, 50, 250 };
        // System.out.println("this is xarray length " + XArray.length);
        /*
         * if (formChar == 'C') { // if statemnt for either circle or square
         * for (int j = 0; j < numForms; j++) { // j is index for drawing the number of
         * shapes
         * g.setColor(colors[j]);
         * g.fillOval(XArray[j], YArray[j], WIDTH, HEIGHT);
         * // System.out.println(j + "this is j");
         * // System.out.println(numForms + "this is numForms");
         * 
         * }
         * } else if (formChar == 'S') {
         * for (int j = 0; j < numForms; j++) { // j is index for drawing the number of
         * shapes
         * g.setColor(colors[j]);
         * g.fillRect(XArray[j], YArray[j], WIDTH, HEIGHT);
         * }
         * }
         */

    }

    public static void drawSyncedForms() { //gets input , draws panel

        int numForms = 0;
        int numOfMoves = 0;
        // int XArray[] = new int[9];
        // int YArray[] = new int[9];
        // int[] XArray = { 50, 150, 150, 50, 100, 50, 150, 100, 100 };
        // int[] YArray = { 50, 50, 150, 150, 100, 100, 100, 50, 150 };

        System.out.println("What form will be shown (C-ircle or S-quare)?");
        formChar = scnr.next().charAt(0);
        System.out.println("How many forms you want to show (max 9)?");
        numForms = scnr.nextInt();
        System.out.println("How many times you want the forms to move? (max 500)?");
        numOfMoves = scnr.nextInt();
        // System.out.println("Please, input the different moves");

        SyncedForms.panel = new DrawingPanel(400, 400);

        initialPosition(XArray, YArray, numForms);
        // System.out.println("this is Xarray length " + XArray.length);
        moveForms(panel, formChar, XArray, YArray, colors, numForms, numOfMoves);

    }

    public static void initialColor(Color[] colors, int numForms) { //sets colors for each form

        for (int i = 0; i < numForms; i++) {
            if (i == 0) {
                colors[i] = Color.GREEN;
            }
            if (i == 1) {
                colors[i] = Color.GRAY;

            }
            if (i == 2) {
                colors[i] = Color.YELLOW;
            }
            if (i == 3) {
                colors[i] = Color.RED;
            }
            if (i == 4) {
                colors[i] = Color.ORANGE;
            }
            if (i == 5) {
                colors[i] = Color.PINK;
            }
            if (i == 6) {
                colors[i] = Color.DARK_GRAY;
            }
            if (i == 7) {
                colors[i] = Color.BLUE;
            }
            if (i == 8) {
                colors[i] = Color.BLACK;
            }

        }

    }

    public static void moveForms(DrawingPanel panel, char formChar, int[] XArray, int[] YArray, Color[] colors,
            int numForms,
            int numOfMoves) {
        int orientationVal;
        // System.out.println("this is Xarray length " + XArray.length);
        for (int i = 0; i < numForms; i++) {
            showForm(panel, formChar, XArray[i], YArray[i], colors[i], 50);

        }

        System.out.println("Please, input the different moves");
        // System.out.println(numOfMoves + " this is num of moves");

        for (int i = 0; i < numOfMoves; i++) { // more movements for loop

            orientationVal = scnr.nextInt();
            for (int j = 0; j < numForms; j++) {
                moveForm(XArray, YArray, j, orientationVal); // i coressponds to the form being displayed
                showForm(panel, formChar, XArray[j], YArray[j], colors[j], 50);

            }
        }
    }

    public static void moveForm(int[] XArray, int[] YArray, int indexForm, int orientationVal) { //method which controls where each form moves
        /*
         * System.out
         * .println(XArray[indexForm] + " this is x array with index form BEFORE   " +
         * indexForm + " :indexform");
         * System.out.println(YArray[indexForm] +
         * " this is yS array with index form BEFORE");
         */
        if (orientationVal == 0) { // orientation = 0 means that form moves to position 0
            YArray[indexForm] = YArray[indexForm] - deltaY;
        } else if (orientationVal == 1) {
            YArray[indexForm] = YArray[indexForm] - deltaY;
            XArray[indexForm] = XArray[indexForm] + deltaX;
        } else if (orientationVal == 2) {
            XArray[indexForm] = XArray[indexForm] + deltaX;
        } else if (orientationVal == 3) {
            XArray[indexForm] = XArray[indexForm] + deltaX;
            YArray[indexForm] = YArray[indexForm] + deltaY;
        } else if (orientationVal == 4) {
            YArray[indexForm] = YArray[indexForm] + deltaY;
        } else if (orientationVal == 5) {
            XArray[indexForm] = XArray[indexForm] - deltaX;
            YArray[indexForm] = YArray[indexForm] + deltaY;
        } else if (orientationVal == 6) {
            XArray[indexForm] = XArray[indexForm] - deltaX;
        } else if (orientationVal == 7) {
            XArray[indexForm] = XArray[indexForm] - deltaX;
            YArray[indexForm] = YArray[indexForm] - deltaY;
        } else {
            // do nothing
        }
        // System.out.println(XArray[indexForm] + " this is x array with index form");
        // ystem.out.println(YArray[indexForm] + " this is yS array with index form");

    }

    public static void showForm(DrawingPanel panel, char formChar, int xPos, int yPos, Color color,
            int sizeForm) {

        if (formChar == 'C') { // if statemnt for either circle or square

            g.setColor(color);
            g.fillOval(xPos, yPos, sizeForm, sizeForm);
            g.setColor(Color.BLACK);
            g.drawOval(xPos - 1, yPos - 1, sizeForm + 1, sizeForm + 1);
        } else if (formChar == 'S') {
            g.setColor(color);
            g.fillRect(xPos, yPos, sizeForm, sizeForm);
            g.setColor(Color.BLACK);
            g.drawRect(xPos - 1, yPos - 1, sizeForm + 1, sizeForm + 1);
        }
    }

    public static void main(String[] args) { //main method which calls drawSyncedForms which in turn calls all the other methods
        System.out.println("UTSA – Fall 2022 - CS1083 - Section 001 - Project 3 - SyncedForms - written by Omar Mian");

        drawSyncedForms();

        // panel.setBackground(Color.BLACK);

        g.setColor(Color.BLACK);
        g.fillRect(20, 200, 360, 40);
        g.setColor(Color.GREEN);
        g.drawRect(20, 200, 360, 40);

        g.setColor(Color.GREEN);
        g.drawString("UTSA - Fall 2022 - CS1083 - Section 001 - Project 3 –", 22, 215);
        g.drawString("SyncedForms - written by Omar Mian", 22, 235);

    }
}