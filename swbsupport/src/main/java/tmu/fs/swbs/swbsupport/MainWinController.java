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

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import tmu.fs.swbs.hcftree.Hcftree;
import tmu.fs.swbs.hcftree.SwbException;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;

/**
 * メイン画面コントローラー処理
 *
 * @author keiichi tsumuta
 */
public class MainWinController implements Initializable {

    @FXML
    private TextField stampPrijectDirFile; // STAMPファイル入力エリア

    @FXML
    private TreeView stampTreeDisplay;  // 内容のツリー表示

    @FXML
    private RadioButton excelSelection; // Excel選択ラジオボタン

    @FXML
    private RadioButton markdownSelection; // マークダウン選択ラジオボタン

    @FXML
    private RadioButton csvSelection; // CSV選択ラジオボタン

    @FXML
    private RadioButton tabTextSelection; // タブテキスト選択ラジオボタン

    @FXML
    private RadioButton ucaOrderSelection; // UCA番号順でのソート選択ラジオボタン

    @FXML
    private RadioButton coordinateOrderSelection; // 表示座標順でのソート選択ラジオボタン

    @FXML
    private Button executeButton; // 実行ボタン

    private TreeItem<SwbInfoModel> treeInfo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup tg = new ToggleGroup();
        excelSelection.setToggleGroup(tg);
        excelSelection.setSelected(true); // デフォルト選択設定
        markdownSelection.setToggleGroup(tg);
        csvSelection.setToggleGroup(tg);
        tabTextSelection.setToggleGroup(tg);

        ToggleGroup tg2 = new ToggleGroup();
        ucaOrderSelection.setToggleGroup(tg2);
        ucaOrderSelection.setSelected(true); // デフォルト選択設定
        coordinateOrderSelection.setToggleGroup(tg2);

        executeButton.setDisable(true); // 最初disableにしておく

        stampTreeDisplay.setOnMouseClicked(event -> selectTreeNode(event));
    }

    /**
     * 参照ボタンクリック
     *
     * @param event
     */
    @FXML
    public void fileSelectionAction(ActionEvent event) {
        refFileSelection();
    }

    // ダイアログによるファイル選択
    private void refFileSelection() {
        final FileChooser fc = new FileChooser();
        fc.setTitle("STAMP Workbenchプリジェクトファイル選択");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("stmp", "*.stmp"));
        File stampFile = fc.showOpenDialog(App.getStage());
        if (stampFile != null) {
            System.out.println("select STAMP PJファイル　：" + stampFile.getAbsolutePath());
            stampPrijectDirFile.setText(stampFile.getAbsolutePath());
        }
    }

    /**
     * ツリー生成ボタンクリック
     *
     * @param event
     */
    @FXML
    public void createTreeAction(ActionEvent event) {
        String stampPjFile = stampPrijectDirFile.getText();
        if (!(stampPjFile != null && stampPjFile.length() > 5)) {
            Alert alert = new Alert(AlertType.WARNING,
                    "STAMP Workbenchのプロジェクトファイルを指定してください。",
                    ButtonType.OK);
            Optional opt = alert.showAndWait();
            return;
        }
        try {
            int orderType = 0; // UCA番号順
            if (coordinateOrderSelection.isSelected()) {
                orderType = 1; // UCA矢印の始点の表示座標順
            }
            Hcftree.makeTree(stampPjFile, orderType);
            treeInfo = TreeDisplayData.getMakeTree(Hcftree.getTreeData());
            treeInfo.setExpanded(true);
            stampTreeDisplay.setRoot(treeInfo);
            executeButton.setDisable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 実行ボタンクリック
     *
     * @param event
     */
    @FXML
    public void executeAction(ActionEvent event) {
        System.out.println("!!! 実行ボタンクリック !!!");
        String stampPjFile = stampPrijectDirFile.getText();
        if (Hcftree.getTreeData() == null) {
            Alert alert = new Alert(AlertType.WARNING,
                    "ツリーデータが生成されていません。",
                    ButtonType.OK);
            Optional opt = alert.showAndWait();
            return;
        }
        // 表示されているツリーデータを設定する。
        Hcftree.setTreeData(TreeDisplayData.getTreeModel(treeInfo));
        String type = "";
        if (excelSelection.isSelected()) {
            type = "excel";
        } else if (markdownSelection.isSelected()) {
            type = "md";
        } else if (csvSelection.isSelected()) {
            type = "csv";
        } else if (tabTextSelection.isSelected()) {
            type = "txt";
        }
        // ファイル出力
        try {
            Hcftree.outputDoc(type, stampPjFile);
            Alert alert = new Alert(AlertType.INFORMATION,
                    "ファイル出力が完了しました。",
                    ButtonType.OK);
            alert.showAndWait();
        } catch (SwbException ex0) {
            Alert alert = new Alert(AlertType.WARNING,
                    ex0.getMessage(),
                    ButtonType.OK);
            alert.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(AlertType.WARNING,
                    "ファイル出力中に異常が発生しました。",
                    ButtonType.OK);
            alert.showAndWait();
        }
    }

    /**
     * クリアボタンクリック
     *
     * @param event
     */
    @FXML
    public void clearAction(ActionEvent event) {
        //System.out.println("!!! クリアボタンクリック !!!");
        stampPrijectDirFile.setText("");

        excelSelection.setSelected(true);
        markdownSelection.setSelected(false);
        csvSelection.setSelected(false);
        tabTextSelection.setSelected(false);

        ucaOrderSelection.setSelected(true);
        coordinateOrderSelection.setSelected(false);

        SwbInfoModel sw = new SwbInfoModel("STAMP/STPA", "", "");
        TreeItem<SwbInfoModel> item = new TreeItem(sw);
        stampTreeDisplay.setRoot(item);
        executeButton.setDisable(true);
    }

    /**
     * aboutボタンクリック
     */
    @FXML
    public void aboutAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("swbsupport");
        alert.setHeaderText("STAMP Workbench 応援団");
        alert.setContentText(
                "Version : " + SwbsVersion.version + "\n"
                + "Copyright (C) " + SwbsVersion.year + "  Keiichi Tsumuta\n"
                + "License : GNU General Public License version 3");
        alert.showAndWait();
    }

    private void selectTreeNode(MouseEvent event) {
        int index = stampTreeDisplay.getFocusModel().getFocusedIndex();
        TreeItem<SwbInfoModel> selectedItem = (TreeItem<SwbInfoModel>) stampTreeDisplay.getFocusModel().getFocusedItem();
        if (selectedItem == null) {
            return;
        }
        SwbInfoModel sm = (SwbInfoModel) selectedItem.getValue();
        //System.out.println("Tree node select：(" + index + "), type=" + sm.getType() + ", " + sm.getId());

        if (sm.getType().equals(SwbInfoModel.SAFTY_ME)) {
            // 安全対策を選択した場合
            try {
                TreeEditDialog treeEditDialog = new TreeEditDialog();
                treeEditDialog.setObject(treeInfo, selectedItem);
                treeEditDialog.show(event);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

}
