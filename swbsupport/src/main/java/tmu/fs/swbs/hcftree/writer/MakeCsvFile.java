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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Action-UCF-HCFのツリー状データをCSV形式ファイルに編集出力する。
 *
 * @author Keiichi Tsumuta
 */
public class MakeCsvFile {

    private static final String[] DATA_TITLE = {
        "Action", "UCA", "HCF", "シナリオ", "安全対策", "内容"
    };

    private static List<List<String>> list;

    private MakeCsvFile() {

    }

    /**
     * CSV形式のテキストを出力する。
     *
     * @param path　ファイルのパス指定
     * @param sm STAMP Workbenchのデータモデル情報
     * @param type フォーマットタイプ（0: CSV形式、1:　タブ形式）
     */
    public static void writeCSV(Path path, SwbInfoModel sm, int type) {
        list = new ArrayList<>();
        // タイトル部設定
        list.add(Arrays.asList(DATA_TITLE));
        setActionData(sm.getChildren());

        //CSV or タブフォーマット作成
        CSVFormat csvFormat = null;
        if (type == 0) {
            csvFormat = CSVFormat.DEFAULT.builder().get();
        } else {
            csvFormat = CSVFormat.TDF.builder().get();
        }
        // ファイルに書き込み
        try (FileOutputStream fos = new FileOutputStream(path.toFile().getAbsolutePath()); // 
                 OutputStreamWriter osw = new OutputStreamWriter(fos, "sjis"); //
                 BufferedWriter bw = new BufferedWriter(osw);//
                 CSVPrinter csvPrinter = new CSVPrinter(bw, csvFormat);) {   //
            csvPrinter.printRecords(list.stream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setActionData(List<SwbInfoModel> actions) {
        for (int i = 0; i < actions.size(); i++) {
            SwbInfoModel sm = actions.get(i);
            String[] ps = {sm.getType(), "", "", "", "", sm.getId()};
            list.add(Arrays.asList(ps));
            setUcaData(sm.getChildren());
        }
    }

    private static void setUcaData(List<SwbInfoModel> ucas) {
        for (int i = 0; i < ucas.size(); i++) {
            SwbInfoModel sm = ucas.get(i);
            String[] ps = {"", sm.getType(), sm.getId(), "", "", sm.getDescription()};
            list.add(Arrays.asList(ps));
            setHcfData(sm.getChildren());
        }
    }

    private static void setHcfData(List<SwbInfoModel> hcfs) {
        for (int i = 0; i < hcfs.size(); i++) {
            SwbInfoModel sm = hcfs.get(i);
            String[] ps = {"", "", sm.getType(), sm.getId(), "", sm.getDescription()};
            list.add(Arrays.asList(ps));
            setScenarioData(sm.getChildren());
        }
    }

    private static void setScenarioData(List<SwbInfoModel> scinas) {
        for (int i = 0; i < scinas.size(); i++) {
            SwbInfoModel sm = scinas.get(i);
            String type = sm.getType();
            String descri = sm.getDescription();
            String[] ps = {"", "", "", type + sm.getId(), "", descri};
            list.add(Arrays.asList(ps));
            setSaftyMeasuresData(sm.getChildren());
        }
    }

    private static void setSaftyMeasuresData(List<SwbInfoModel> scinas) {
        for (int i = 0; i < scinas.size(); i++) {
            SwbInfoModel sm = scinas.get(i);
            String type = sm.getType();
            String descri = sm.getDescription();
            type = "安全対策";
            if (sm.getAtt() != null && sm.getAtt().length() > 0) {
                descri = descri + "\n注）" + sm.getAtt();
            }
            String[] ps = {"", "", "", "", type + sm.getId(), descri};
            list.add(Arrays.asList(ps));
        }
    }
}
