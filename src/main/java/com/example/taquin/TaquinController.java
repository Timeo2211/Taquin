/**
 * Ce logiciel est distribué à des fins éducatives.
 *
 * Il est fourni "tel quel", sans garantie d’aucune sorte, explicite
 * ou implicite, notamment sans garantie de qualité marchande, d’adéquation
 * à un usage particulier et d’absence de contrefaçon.
 * En aucun cas, les auteurs ou titulaires du droit d’auteur ne seront
 * responsables de tout dommage, réclamation ou autre responsabilité, que ce
 * soit dans le cadre d’un contrat, d’un délit ou autre, en provenance de,
 * consécutif à ou en relation avec le logiciel ou son utilisation, ou avec
 * d’autres éléments du logiciel.
 *
 * (c) 2022-2023 Romain Wallon - Université d'Artois.
 * Tous droits réservés.
 */

package com.example.taquin;

import java.net.URL;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;

/**
 * La classe TaquinController propose un contrôleur permettant de gérer un jeu du Taquin
 * présenté à l'utilisateur sous la forme d'une interface graphique JavaFX.
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class TaquinController {

    /**
     * Le label affichant le nombre de déplacements réalisés par l'utilisateur.
     */
    @FXML
    private Label nbMoves;

    @FXML
    private Scene scene;

    /**
     * La grille affichant les boutons permettant de jouer au Taquin.
     */
    @FXML
    private GridPane gridPane;

    /**
     * Les boutons représentant les tuiles du jeu du Taquin.
     */
    private Button[][] buttons ;




    /**
     * Le modèle du Taquin avec lequel ce contrôleur interagit.
     */
    private Taquin taquin;

    /**
     * Associe à ce contrôleur la partie du jeu du Taquin en cours.
     *
     * @param taquin La partie du Taquin avec laquelle interagir.
     */
    public void setModel(Taquin taquin) {
        this.taquin = taquin;
    }

    /**
     * Initialise la grille du Taquin affichée par ce contrôleur.
     *
     * @param grid La grille du jeu.
     */



    public void  initGrid(Grid grid) {
        int gridSize = grid.size();
        buttons = new Button[gridSize][gridSize];

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                extracted(grid, i, j);
            }
        }
    }

    private void extracted(Grid grid, int i, int j) {
        Button button = new Button();
        int tileValue = grid.getTile(i, j);
        button.setText(String.valueOf(tileValue));
        buttons[i][j] = button;
        gridPane.add(button, j, i);
        buttons[i][j].textProperty().bind(grid.get(i, j).getProperty().asString());
        buttons[i][j].visibleProperty().bind(grid.get(i, j).getProperty().isNotEqualTo(0));
        grid.get(i, j).getProperty().addListener(
                (p, o, n) -> buttons[i][j].setBackground(createBackground(n)));
        buttons[i][j].setPrefHeight(100);
        buttons[i][j].setPrefWidth(100);
        final int row = i;
        final int column = j;
        buttons[i][j].setOnAction(e -> taquin.push(row, column));

        // Définition de l'arrière-plan du bouton en fonction de la valeur de la grille
        Background background = createBackground(tileValue);
        buttons[i][j].setBackground(background);
    }

// Méthode pour créer un arrière-plan en fonction de la valeur de la


    @FunctionalInterface
    public interface ChangeListener<T> {
        void changed(ObservableValue<? extends T> observable,
                     T oldValue, T newValue);
    }


    public void setScene(Scene scene) {
        this.scene = scene;
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode keyCode = e.getCode();
            if (keyCode == KeyCode.UP) {
                taquin.pushUp();
            } else if (keyCode == KeyCode.DOWN) {
                taquin.pushDown();
            } else if (keyCode == KeyCode.LEFT) {
                taquin.pushLeft();
            } else if (keyCode == KeyCode.RIGHT) {
                taquin.pushRight();
            }
        });
    }




    /**
     * Met à jour l'affichage du nombre de déplacements.
     *
     * @param nb Le nombre de déplacements.
     */
    public void updateMoves(IntegerProperty nb ) {
        this.nbMoves.textProperty().bind(nb.asString());
    }


    /**
     * Prépare une nouvelle partie sur la vue.
     */
    public void startGame() {
        int size = buttons.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setDisable(false);
            }
        }
    }

    /**
     * Redémarre le jeu affiché sur la vue.
     */
    @FXML
    public void restart() {
        taquin.restartGame();
    }

    /**
     * Termine la partie en cours sur la vue.
     */
    public void endGame() {
        int size = buttons.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }

    /**
     * Détermine l'arrière-plan de la tuile ayant l'indice donné.
     *
     * @param index L'indice de la tuile.
     *
     * @return L'arrière-plan associé à la tuile.
     */
    private Background createBackground(Number index) {
        String imageName = "iut-" + index;
        String imagePath = "/com/example/taquin/images/" + imageName + ".jpg";
        java.io.InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream != null) {
            Image image = new Image(imageStream, 100, 100, false, true);
            BackgroundImage backgroundImage = new BackgroundImage(
                    image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT
            );
            return new Background(backgroundImage);
        } else {
            System.err.println("Image not found: " + imageName);
            Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/taquin/images/default.jpg"), 100, 100, false, true);
            BackgroundImage defaultBackgroundImage = new BackgroundImage(
                    defaultImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT
            );
            return new Background(defaultBackgroundImage);
        }
    }

}


