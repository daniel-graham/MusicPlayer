package app.musicplayer.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import app.musicplayer.MusicPlayer;
import app.musicplayer.model.Artist;
import app.musicplayer.model.Library;
import app.musicplayer.util.Refreshable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class ArtistsController implements Initializable, Refreshable {

    @FXML private FlowPane grid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<Artist> artists = FXCollections.observableArrayList(Library.getArtists());
        Collections.sort(artists);

        int limit = (artists.size() < 25) ? artists.size() : 25;

        for (int i = 0; i < limit; i++) {

            Artist artist = artists.get(i);
            grid.getChildren().add(createCell(artist));
        }

        int rows = (artists.size() % 5 == 0) ? artists.size() / 5 : artists.size() / 5 + 1;
        grid.prefHeightProperty().bind(grid.widthProperty().divide(5).add(16).multiply(rows));

        new Thread(() -> {

            ArrayList<VBox> cells = new ArrayList<VBox>();

            for (int j = 25; j < artists.size(); j++) {
                Artist artist = artists.get(j);
                cells.add(createCell(artist));
            }

            Platform.runLater(() -> {
                grid.getChildren().addAll(cells);
            });

        }).start();
    }

    @Override
    public void refresh() {

    }

    private VBox createCell(Artist artist) {

        VBox cell = new VBox();
        Label title = new Label(artist.getTitle());
        ImageView image = new ImageView(artist.getArtistImage());
        VBox imageBox = new VBox();

        title.setTextOverrun(OverrunStyle.CLIP);
        title.setWrapText(true);
        title.setPadding(new Insets(10, 0, 10, 0));
        title.setAlignment(Pos.TOP_LEFT);
        title.setPrefHeight(66);
        title.prefWidthProperty().bind(grid.widthProperty().divide(5).subtract(21));

        image.fitWidthProperty().bind(grid.widthProperty().divide(5).subtract(21));
        image.fitHeightProperty().bind(grid.widthProperty().divide(5).subtract(21));
        image.setPreserveRatio(true);
        image.setSmooth(true);

        imageBox.prefWidthProperty().bind(grid.widthProperty().divide(5).subtract(21));
        imageBox.prefHeightProperty().bind(grid.widthProperty().divide(5).subtract(21));
        imageBox.setAlignment(Pos.CENTER);
        imageBox.getChildren().add(image);

        cell.getChildren().addAll(imageBox, title);
        cell.setPadding(new Insets(10, 10, 0, 10));
        cell.getStyleClass().add("artist-cell");
        cell.setAlignment(Pos.CENTER);
        cell.setOnMouseClicked(event -> {

            MainController mainController = MusicPlayer.getMainController();
            ArtistsMainController artistsMainController = (ArtistsMainController) mainController.loadView("ArtistsMain");

            VBox artistCell = (VBox) event.getSource();
            String artistTitle = ((Label) artistCell.getChildren().get(1)).getText();
            Artist a = Library.getArtists().stream().filter(x -> artistTitle.equals(x.getTitle())).findFirst().get();
            artistsMainController.selectArtist(a);
        });

        return cell;
    }
}