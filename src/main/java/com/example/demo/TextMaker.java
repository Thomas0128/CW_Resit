package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Creates and updates JavaFX text nodes used by board cells
 *
 * <p>The class is implemented as a singleton because tile text uses
 * the shared dimensions configured by {@link GameScene}.</p>
 */
class TextMaker {
    private static TextMaker singleInstance = null;

    private TextMaker() {

    }

    /**
     * Returns the shared text factory
     *
     * @return singleton {@code TextMaker} instance
     */
    static TextMaker getSingleInstance() {
        if (singleInstance == null)
            singleInstance = new TextMaker();
        return singleInstance;
    }


    /**
     * Creates a text node for a displayed tile value
     *
     * @param input text displayed inside the tile
     * @param xCell horizontal position of the related cell
     * @param yCell vertical position of the related cell
     * @param root board group associated with the text node
     * @return configured JavaFX text node
     */
    Text madeText(String input, double xCell, double yCell, Group root) {
        double length = GameScene.getLENGTH();
        double fontSize = (3 * length) / 7.0;
        Text text = new Text(input);
        text.setFont(Font.font(fontSize));
        text.relocate((xCell + (1.2)* length / 7.0), (yCell + 2 * length / 7.0));
        text.setFill(Color.WHITE);

        return text;
    }


    /**
     * Exchanges the contents and positions of two text nodes
     *
     * @param first first text node
     * @param second second text node
     */
    static void changeTwoText(Text first, Text second) {
        String temp;
        temp = first.getText();
        first.setText(second.getText());
        second.setText(temp);

        double tempNumber;
        tempNumber = first.getX();
        first.setX(second.getX());
        second.setX(tempNumber);

        tempNumber = first.getY();
        first.setY(second.getY());
        second.setY(tempNumber);

    }

}
