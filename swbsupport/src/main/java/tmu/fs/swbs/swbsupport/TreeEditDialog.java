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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;

/**
 * ツリーのデータを付け替えるための操作を行うダイアログを表示する。
 *
 * @author Keiichi Tsumuta
 */
public class TreeEditDialog implements Initializable {

    @FXML
    private Button smUpMoveButton; // ダイアログ、UPボタン

    @FXML
    private Button smDownMoveButton; // ダイアログ、downボタン

    private Stage stage;

    private static TreeItem<SwbInfoModel> treeRoot;
    private static TreeItem<SwbInfoModel> selectedItem;

    /**
     * コンストラクタ
     */
    public TreeEditDialog() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        smUpMoveButton.setDisable(false);
        smDownMoveButton.setDisable(false);
        setButtonProperty();
    }

    /**
     * ツリーデータの設定
     *
     * @param rootObj　ツリーのルートオブジェクト
     * @param selItem　選択された安全対策項目のモデルオブジェクト
     */
    public void setObject(TreeItem<SwbInfoModel> rootObj, TreeItem<SwbInfoModel> selItem) {
        treeRoot = rootObj;
        selectedItem = selItem;
    }

    /**
     * ダイアログを表示する。
     *
     * @param event　マウスイベント
     * @throws IOException
     */
    public void show(MouseEvent event) throws IOException {
        stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/tmu/fs/swbs/swbsupport/treeEditDialog.fxml"));
        Scene scene = new Scene(root);
        //scene.getStylesheets().add("/styles/Styles.css");
        stage.setScene(scene);

        stage.setTitle("安全対策項目付け替え");
        stage.initModality(Modality.WINDOW_MODAL);

        stage.initOwner(App.getStage());
        stage.show();
    }

    private void setButtonProperty() {
        TreeItem<SwbInfoModel> parent = selectedItem.getParent();
        TreeItem<SwbInfoModel> previous = parent.previousSibling();
        if (smUpMoveButton != null) {
            if (previous != null) {
                smUpMoveButton.setDisable(false);
            } else {
                smUpMoveButton.setDisable(true);
            }
        }

        TreeItem<SwbInfoModel> next = parent.nextSibling();
        if (smDownMoveButton != null) {
            if (next != null) {
                smDownMoveButton.setDisable(false);
            } else {
                smDownMoveButton.setDisable(true);
            }
        }
    }

    /**
     * 安全対策項目付け替えダイアログ、上移動ボタンクリック
     *
     * @param event
     */
    @FXML
    public void smUpMoveAction(ActionEvent event) {
        //System.out.println("Up Clicked!!");
        TreeItem<SwbInfoModel> parent = selectedItem.getParent();
        parent.getChildren().remove(selectedItem);
        TreeItem<SwbInfoModel> previous = parent.previousSibling();
        previous.getChildren().add(selectedItem);
        previous.setExpanded(true);
        setButtonProperty();
    }

    /**
     * 安全対策項目付け替えダイアログ、下移動ボタンクリック
     *
     * @param event
     */
    @FXML
    public void smDownMoveAction(ActionEvent event) {
        //System.out.println("Down Clicked!!");
        TreeItem<SwbInfoModel> parent = selectedItem.getParent();
        parent.getChildren().remove(selectedItem);
        TreeItem<SwbInfoModel> next = parent.nextSibling();
        next.getChildren().add(selectedItem);
        next.setExpanded(true);
        setButtonProperty();
    }

    /**
     * 安全対策項目付け替えダイアログ消去
     *
     * @param event
     */
    @FXML
    public void smMoveDialogHideAction(ActionEvent event) {
        //System.out.println("Dialog Hide Clicked!!");
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
