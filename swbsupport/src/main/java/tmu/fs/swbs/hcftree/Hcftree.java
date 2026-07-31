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
package tmu.fs.swbs.hcftree;

import tmu.fs.swbs.hcftree.writer.MakeExcelFile;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;
import tmu.fs.swbs.hcftree.model.SwbModelXml;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import tmu.fs.swbs.hcftree.modelnotation.NotationModel;
import tmu.fs.swbs.hcftree.modelnotation.NotationXml;
import tmu.fs.swbs.hcftree.writer.MakeCsvFile;
import tmu.fs.swbs.hcftree.writer.MakeMarkdown;

/**
 * STAMP　Workbenchプロジェクトデータからツリー状のデータを作成する処理。
 *
 * @author Keiichi Tsumuta
 */
public class Hcftree {

    public static final String STRAMP_MODEL = "model.stamp";
    public static final String STRAMP_NOTATION = "model.stampnotation";

    private static SwbInfoModel treeRoot = null;

    /**
     * プロジェクトデータからツリー状のデータを作成する
     *
     * @param stampFile 分析ファイル指定
     * @param orderType 出力順序の指定（0:UCA番号順 、1:UCA矢印の始点の表示座標順）
     * @return
     */
    public static void makeTree(String stampFile, int orderType) throws Exception {
        SwbModelXml modelXml = new SwbModelXml();
        NotationXml notaModelXml = new NotationXml();
        List<NotationModel> notations = new ArrayList<>();
        var zipfile = Paths.get(stampFile);
        //　.stmpファイルをZIP解凍する！
        try (var in = new ZipInputStream(Files.newInputStream(zipfile))) {
            ZipEntry e;
            while ((e = in.getNextEntry()) != null) {
                // ZIPファイルに含まれるファイルの選択
                String fileName = e.getName();
                if (fileName.equals(STRAMP_MODEL)) {
                    modelXml.parse(in.readAllBytes());
                } else if (fileName.equals(STRAMP_NOTATION)) {
                    notations = notaModelXml.parse(in.readAllBytes());
                }
            }
        }
        // UCAにHCFを関連付ける（階層化する）
        treeRoot = modelXml.getCaTree(orderType, notations);
        //System.out.println("*** CA - UCF - HCF ツリー start ***");
        //System.out.println(treeRoot.toTreeString());
        //System.out.println("*** CA - UCF - HCF ツリー end ***");
    }

    /**
     * カレントのツリーデータをリセットする。
     */
    public static void clearTreeData() {
        treeRoot = null;
    }

    /**
     * ツリーデータを取り出す。
     *
     * @return ツリーデータ
     */
    public static SwbInfoModel getTreeData() {
        return treeRoot;
    }

    /**
     * ツリーデータを設定する。
     *
     * @param sm ツリーデータ（ルート）
     */
    public static void setTreeData(SwbInfoModel sm) {
        treeRoot = sm;
    }

    /**
     * プロジェクトデータからツリー状のデータを作成する
     *
     * @param type 出力タイプ（md:マークダウン、csv：CSV形式、excel:Excel、txt:ツリー状タブ区切りテキスト、）
     * @param stampFile 分析ファイル指定
     * @return
     */
    public static void outputDoc(String type, String stampFile) throws Exception {
        if (treeRoot == null) {
            throw new SwbException("ツリーデータがありません。");
        }
        switch (type) {
            case "md" -> {
                // MarkDown形式のテキスト
                writeUTF8File(type, stampFile, MakeMarkdown.getMarkdownDoc(treeRoot));
            }
            case "csv" -> {
                // CSV形式のテキスト
                writeCSV(stampFile, treeRoot);
            }
            case "excel" ->
                // Excelファイル
                writeExcel(stampFile, treeRoot);
            default ->
                // タブ形式のテキストファイル
                writeTab(stampFile, treeRoot);
        }
    }

    private static void writeUTF8File(String type, String stampFile, String s) throws Exception {
        try {
            File f = new File(stampFile);
            String dirName = f.getPath() + "." + type;
            Path path = Paths.get(dirName);
            Files.writeString(path, s, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SwbException("ファイル書き込みに失敗しました。");
        }
    }

    private static void writeSJISFile(String type, String stampFile, String s) throws Exception {
        try {
            File f = new File(stampFile);
            String dirName = f.getPath() + "." + type;
            Path path = Paths.get(dirName);
            Files.writeString(path, s, Charset.forName("Shift_JIS"));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SwbException("ファイル書き込みに失敗しました。");
        }
    }

    private static void writeCSV(String stampFile, SwbInfoModel sModel) throws Exception {
        try {
            File f = new File(stampFile);
            String dirName = f.getPath() + ".csv";
            Path path = Paths.get(dirName);
            MakeCsvFile.writeCSV(path, sModel, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SwbException("ファイル書き込みに失敗しました。");
        }
    }
    
        private static void writeTab(String stampFile, SwbInfoModel sModel) throws Exception {
        try {
            File f = new File(stampFile);
            String dirName = f.getPath() + ".txt";
            Path path = Paths.get(dirName);
            MakeCsvFile.writeCSV(path, sModel, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SwbException("ファイル書き込みに失敗しました。");
        }
    }

    private static void writeExcel(String stampFile, SwbInfoModel sModel) throws Exception {
        try {
            File f = new File(stampFile);
            String dirName = f.getPath() + ".xlsx";
            Path path = Paths.get(dirName);
            MakeExcelFile.writeExcel(path, sModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SwbException("Excelファイルの生成に失敗しました。");
        }
    }

}
