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
package tmu.fs.swbs.hcftree.writer;

import java.util.List;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;

/**
 * Action-UCF-HCFのツリー状データをマークダウン形式に編集出力する。
 *
 * @author Keiichi Tsumuta
 */
public class MakeMarkdown {

    /**
     * マークダウン形式のテキストデータを作成する。
     *
     * @param sm　STAMP Workbenchデータモデル情報
     * @return マークダウンテキスト
     */
    public static String getMarkdownDoc(SwbInfoModel sm) {
        StringBuilder sb = new StringBuilder();
        setActionData(sm.getChildren(), sb);
        return sb.toString();
    }

    //　Actionデータの出力
    private static void setActionData(List<SwbInfoModel> actions, StringBuilder sb) {
        for (int i = 0; i < actions.size(); i++) {
            SwbInfoModel sm = actions.get(i);
            sb.append("# ").append(sm.getType()).append(" : ");
            sb.append(sm.getId()).append("\n");
            setUcaData(sm.getChildren(), sb);
        }
    }

    // UCAデータの出力
    private static void setUcaData(List<SwbInfoModel> ucas, StringBuilder sb) {
        for (int i = 0; i < ucas.size(); i++) {
            SwbInfoModel sm = ucas.get(i);
            sb.append("## ").append(sm.getType()).append(":");
            sb.append(sm.getId()).append("\n");
            sb.append(sm.getDescription()).append("\n");
            setHcfData(sm.getChildren(), sb);
        }
    }

    // HCFデータの出力
    private static void setHcfData(List<SwbInfoModel> hcfs, StringBuilder sb) {
        for (int i = 0; i < hcfs.size(); i++) {
            SwbInfoModel sm = hcfs.get(i);
            sb.append("### ").append(sm.getType()).append(":");
            sb.append(sm.getId()).append("\n");
            sb.append(sm.getDescription()).append("\n");
            setScenarioData(sm.getChildren(), sb);
        }
    }

    // シナリオと対策データの出力
    private static void setScenarioData(List<SwbInfoModel> scinas, StringBuilder sb) {
        for (int i = 0; i < scinas.size(); i++) {
            SwbInfoModel sm = scinas.get(i);
            String type = sm.getType();
            String descri = sm.getDescription();
            sb.append("#### ").append("シナリオ ");
            sb.append(sm.getId()).append("\n");
            sb.append("```\n");
            sb.append(descri).append("\n");
            sb.append("```\n");
            setSaftyMeasuresData(sm.getChildren(), sb);
        }
    }

    // シナリオと安全対策データの出力
    private static void setSaftyMeasuresData(List<SwbInfoModel> scinas, StringBuilder sb) {
        for (int i = 0; i < scinas.size(); i++) {
            SwbInfoModel sm = scinas.get(i);
            String type = sm.getType();
            String descri = sm.getDescription();
            type = "安全対策";
            if (sm.getAtt() != null && sm.getAtt().length() > 0) {
                descri = descri + "\n注）" + sm.getAtt();
            }
            sb.append("##### ").append(type).append(" : ");
            sb.append(sm.getId()).append("\n");
            sb.append("```\n");
            sb.append(descri).append("\n");
            sb.append("```\n");
        }
    }

}
