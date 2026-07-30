package com.example.demo;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Represents one JavaFX board cell and its displayed tile value
 *
 * <p>The class manages the cell rectangle, text node, color and
 * legacy merge state used by the graphical interface.</p>
 */
public class Cell {
    private Rectangle rectangle;
    private Group root;
    private Text textClass;
    private boolean modify = false;

    /**
     * Changes whether this cell has already been modified during a move
     *
     * @param modify {@code true} when the cell is marked as modified
     */
    void setModify(boolean modify) {
        this.modify = modify;
    }

    /**
     * Returns whether this cell is marked as modified.
     *
     * @return {@code true} when the cell has already been modified
     */
    boolean getModify() {
        return modify;
    }

    /**
     * Creates and displays an empty board cell
     *
     * @param x horizontal position of the cell
     * @param y vertical position of the cell
     * @param scale width and height of the cell
     * @param root JavaFX group that contains the cell
     */
    Cell(double x, double y, double scale, Group root) {
        rectangle = new Rectangle();
        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setHeight(scale);
        rectangle.setWidth(scale);
        this.root = root;
        rectangle.setFill(Color.rgb(224, 226, 226, 0.5));
        this.textClass = TextMaker.getSingleInstance().madeText("0", x, y, root);
        root.getChildren().add(rectangle);
    }

    /**
     * Sets the text node used to display the tile value
     *
     * @param textClass text node associated with this cell
     */
    void setTextClass(Text textClass) {
        this.textClass = textClass;
    }

    /**
     * Exchanges the displayed values and text positions of two cells
     *
     * @param cell other cell involved in the exchange
     */
    void changeCell(Cell cell) {
        TextMaker.changeTwoText(textClass, cell.getTextClass());
        root.getChildren().remove(cell.getTextClass());
        root.getChildren().remove(textClass);

        if (!cell.getTextClass().getText().equals("0")) {
            root.getChildren().add(cell.getTextClass());
        }
        if (!textClass.getText().equals("0")) {
            root.getChildren().add(textClass);
        }
        setColorByNumber(getNumber());
        cell.setColorByNumber(cell.getNumber());
    }

    /**
     * Merges the value of this cell into the supplied destination cell
     *
     * @param cell destination cell that receives the combined value
     */
    void adder(Cell cell) {
        cell.getTextClass().setText((cell.getNumber() + this.getNumber()) + "");
        textClass.setText("0");
        root.getChildren().remove(textClass);
        cell.setColorByNumber(cell.getNumber());
        setColorByNumber(getNumber());
    }

    /**
     * Updates the cell color to match a tile value
     *
     * @param number tile value displayed by the cell
     */
    void setColorByNumber(int number) {
        switch (number) {
            case 0:
                rectangle.setFill(Color.rgb(224, 226, 226, 0.5));
                break;
            case 2:
                rectangle.setFill(Color.rgb(232, 255, 100, 0.5));
                break;
            case 4:
                rectangle.setFill(Color.rgb(232, 220, 50, 0.5));
                break;
            case 8:
                rectangle.setFill(Color.rgb(232, 200, 44, 0.8));
                break;
            case 16:
                rectangle.setFill(Color.rgb(232, 170, 44, 0.8));
                break;
            case 32:
                rectangle.setFill(Color.rgb(180, 120, 44, 0.7));
                break;
            case 64:
                rectangle.setFill(Color.rgb(180, 100, 44, 0.7));
                break;
            case 128:
                rectangle.setFill(Color.rgb(180, 80, 44, 0.7));
                break;
            case 256:
                rectangle.setFill(Color.rgb(180, 60, 44, 0.8));
                break;
            case 512:
                rectangle.setFill(Color.rgb(180, 30, 44, 0.8));
                break;
            case 1024:
                rectangle.setFill(Color.rgb(250, 0, 44, 0.8));
                break;
            case 2048:
                rectangle.setFill(Color.rgb(250,0,0,1));


        }

    }

    /**
     * Returns the horizontal position of the cell
     *
     * @return horizontal rectangle position
     */
    double getX() {
        return rectangle.getX();
    }

    /**
     * Returns the vertical position of the cell
     *
     * @return vertical rectangle position
     */
    double getY() {
        return rectangle.getY();
    }

    /**
     * Returns the numerical value displayed by the cell
     *
     * @return displayed tile value
     */
    int getNumber() {
        return Integer.parseInt(textClass.getText());
    }

    private Text getTextClass() {
        return textClass;
    }

}
