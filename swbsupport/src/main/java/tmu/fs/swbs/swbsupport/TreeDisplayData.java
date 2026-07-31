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

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;

/**
 * STAMP Workbenchのデータを元にしてツリー表示を行う。
 *
 * @author Keiichi Tsumuta
 */
public class TreeDisplayData {

    /**
     * ツリー表示のデータを作成する。. 元となるSTAMP Workbenchでは、シナリオと安全対策は階層が同一のため、ここでは、まず、
     * HCF下の先頭シナリオについて、その下の階層として安全対策を配置する。
     * HCFに属するシナリオが複数ある場合、その後のGUI操作で目的のシナリオ下に安全対策データ を付け替えることで対応する。
     *
     * @param rootSm SwbInfoModelモデルのTOPオブジェクト
     * @return TreeItemオブジェクト
     */
    public static TreeItem<SwbInfoModel> getMakeTree(SwbInfoModel rootSm) {
        SwbInfoModel sw = new SwbInfoModel("STAMP/STPA", "", "");
        TreeItem<SwbInfoModel> item = new TreeItem(sw);
        item.setExpanded(true);
        setActionData(rootSm.getChildren(), item);
        return item;
    }

    //　Control Actionデータの出力
    private static void setActionData(List<SwbInfoModel> actions, TreeItem<SwbInfoModel> item) {
        for (int i = 0; i < actions.size(); i++) {
            SwbInfoModel sm = actions.get(i);
            TreeItem<SwbInfoModel> child = new TreeItem(sm);
            item.getChildren().add(child);
            item.setExpanded(true);
            setUcaData(sm.getChildren(), child);
        }
    }

    // UCAデータの出力
    private static void setUcaData(List<SwbInfoModel> ucas, TreeItem<SwbInfoModel> item) {
        for (int i = 0; i < ucas.size(); i++) {
            SwbInfoModel sm = ucas.get(i);
            TreeItem<SwbInfoModel> child = new TreeItem(sm);
            item.getChildren().add(child);
            item.setExpanded(true);
            setHcfData(sm.getChildren(), child);
        }
    }

    // HCFデータの出力
    private static void setHcfData(List<SwbInfoModel> hcfs, TreeItem<SwbInfoModel> item) {
        for (int i = 0; i < hcfs.size(); i++) {
            SwbInfoModel sm = hcfs.get(i);
            TreeItem<SwbInfoModel> child = new TreeItem(sm);
            item.getChildren().add(child);
            item.setExpanded(true);
            setScenarioAndCountmeData(sm.getChildren(), child);
        }
    }

    // シナリオと対策データの出力
    private static void setScenarioAndCountmeData(List<SwbInfoModel> scinas, TreeItem<SwbInfoModel> item) {
        List<TreeItem<SwbInfoModel>> scs = new ArrayList<>(); // シナリオ
        List<TreeItem<SwbInfoModel>> cts = new ArrayList<>(); // 安全対策
        for (int i = 0; i < scinas.size(); i++) {
            SwbInfoModel sm = scinas.get(i);
            String type = sm.getType();
            String descri = sm.getDescription();
            if (type.equals(SwbInfoModel.SAFTY_ME)) { // 安全対策に関するデータの場合
                if (sm.getAtt() != null && sm.getAtt().length() > 0) {
                    descri = descri + "\n注）" + sm.getAtt();
                }
                //String type, String xmiId, String id, String description
                SwbInfoModel sw = new SwbInfoModel(SwbInfoModel.SAFTY_ME, "", sm.getId(), descri);
                cts.add(new TreeItem(sw));
            } else { // シナリオ
                TreeItem<SwbInfoModel> child = new TreeItem(sm);
                scs.add(child);
                item.getChildren().add(child);
                item.setExpanded(true);
            }
        }
        if (!scs.isEmpty()) {
            for (TreeItem<SwbInfoModel> ti : cts) {
                int index = scs.size() - 1; // とりあえず、一番後ろのシナリオに付けておく。
                scs.get(index).getChildren().add(ti);
                scs.get(index).setExpanded(true);
            }
        }
    }

    /**
     * 表示されているツリーのデータを取り出す。
     *
     * @param root TOPデータ
     * @return SwbInfoModelデータ
     */
    public static SwbInfoModel getTreeModel(TreeItem<SwbInfoModel> root) {
        System.out.println("getTreeModel : start ++++");
        SwbInfoModel rt = root.getValue();
        rt.clearChildren();
        // CA
        for (TreeItem<SwbInfoModel> ti2 : root.getChildren()) {
            SwbInfoModel rt2 = ti2.getValue();
            rt2.clearChildren();
            rt.addChild(rt2);
            // UCA
            for (TreeItem<SwbInfoModel> ti3 : ti2.getChildren()) {
                SwbInfoModel rt3 = ti3.getValue();
                rt3.clearChildren();
                rt2.addChild(rt3);
                // HCF
                for (TreeItem<SwbInfoModel> ti4 : ti3.getChildren()) {
                    SwbInfoModel rt4 = ti4.getValue();
                    rt4.clearChildren();
                    rt3.addChild(rt4);
                    // シナリオ
                    for (TreeItem<SwbInfoModel> ti5 : ti4.getChildren()) {
                        SwbInfoModel rt5 = ti5.getValue();
                        rt5.clearChildren();
                        rt4.addChild(rt5);
                        // 安全対策
                        for (TreeItem<SwbInfoModel> ti6 : ti5.getChildren()) {
                            SwbInfoModel rt6 = ti6.getValue();
                            rt5.addChild(rt6);
                        }
                    }
                }
            }
        }
        System.out.println("getTreeModel : end ---");
        return rt;
    }
}
