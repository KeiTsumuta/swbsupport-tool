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
package tmu.fs.swbs.hctree.writer;

import java.util.List;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;

/**
 *  Action-UCF-HCFのツリー状データをタブ形式で編集出力する。
 * 
 * @author Keiichi Tsumuta
 */
public class MakeTabFile {
    
    private static final String DATA_TITLE = 
        "Action \tUCA \tHCF \tScenario \t内容";

    
    /**
     * タブ形式のテキストデータを作成する。
     *
     * @param sm　STAMP Workbenchデータモデル情報
     * @return マークダウンテキスト
     */
    public static String getTabDoc(SwbInfoModel sm) {
        StringBuilder sb = new StringBuilder();
        sb.append(DATA_TITLE).append("\n");
        setActionData(sm.getChildren(), sb);
        System.out.println("\n\n" + sb.toString() + "\n");
        return sb.toString();
    }

    //　Actionデータの出力
    private static void setActionData(List<SwbInfoModel> actions, StringBuilder sb) {
        for (int i = 0; i < actions.size(); i++) {
            SwbInfoModel sm = actions.get(i);
            sb.append(sm.getType()).append(" \t\t\t\t");
            sb.append(sm.getId()).append("\n");
            setUcaData(sm.getChildren(), sb);
        }
    }

    // UCAデータの出力
    private static void setUcaData(List<SwbInfoModel> ucas, StringBuilder sb) {
        for (int i = 0; i < ucas.size(); i++) {
            SwbInfoModel sm = ucas.get(i);
            sb.append("\t\t ").append(sm.getType()).append("\t");
            sb.append(sm.getId()).append("\t");
            sb.append(sm.getDescription()).append("\n");
            setHcfData(sm.getChildren(), sb);
        }
    }

    // HCFデータの出力
    private static void setHcfData(List<SwbInfoModel> hcfs, StringBuilder sb) {
        for (int i = 0; i < hcfs.size(); i++) {
            SwbInfoModel sm = hcfs.get(i);
            sb.append("\t\t\t ").append(sm.getType()).append("\t");
            sb.append(sm.getId()).append("\t");
            sb.append(sm.getDescription()).append("\n");
            setScenarioAndCountmeData(sm.getChildren(), sb);
        }
    }

    // シナリオと対策データの出力
    private static void setScenarioAndCountmeData(List<SwbInfoModel> scinas, StringBuilder sb) {
        for (int i = 0; i < scinas.size(); i++) {
            SwbInfoModel sm = scinas.get(i);
            String type = sm.getType();
            String descri = sm.getDescription();
            if (type.equals("countme")) {
                type = "対策";
                if (sm.getAtt() != null && sm.getAtt().length() > 0) {
                    descri = descri + "\t" + sm.getAtt();
                }
            }
            sb.append("\t\t\t\t ").append(type).append("\t");
            sb.append(sm.getId()).append("\t");
            sb.append(descri).append("\n");
        }
    }

}

