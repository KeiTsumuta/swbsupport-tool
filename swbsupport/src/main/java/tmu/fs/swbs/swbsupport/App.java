/*
 *	 swbsupport - Support tools for STAMP Workbench 
 *	 Copyright (C) 2026  Keiichi Tsumuta
 *
 *	 This program is free software: you can redistribute it and/or modify
 *	 it under the terms of the GNU General Public License as published by
 *	 the Free Software Foundation, either version 3 of the License, or
 *	 (at your option) any later version.
 *
 *	 This program is distributed in the hope that it will be useful,
 *	 but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	 GNU General Public License for more details.
 *
 *	 You should have received a copy of the GNU General Public License
 *	 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tmu.fs.swbs.swbsupport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.WindowEvent;

/**
 * JavaFX App
 *
 * @author Keiichi Tsumuta
 */
public class App extends Application {

    private static Scene scene;
    private static Stage mwStage;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("--- SWBSupport start ---");
        mwStage = stage;
        stage.setOnCloseRequest((WindowEvent t) -> {
            System.out.println("--- SWBSupport end ---");
        });
        scene = new Scene(loadFXML("mainWin"), 1000, 650);
        scene.getStylesheets().add(App.class.getResource("/styles/Styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Report tool for STAMP Workbench");
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Stage getStage() {
        return mwStage;
    }

    public static void main(String[] args) {
        launch();
    }

}
