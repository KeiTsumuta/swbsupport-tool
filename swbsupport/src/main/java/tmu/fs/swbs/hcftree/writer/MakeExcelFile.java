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

import java.io.File;
import tmu.fs.swbs.hcftree.model.SwbInfoModel;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Action-UCF-HCFのツリー状データをExcelに編集出力する。
 *
 * @author Keiichi Tsumuta
 */
public class MakeExcelFile {

    private static final String[] DATA_TITLE = {
        "  ", "Action", "UCA", "視点", "シナリオ", "安全対策", "内　　　　　容"
    };
    private static final int[] COLUMN_SIZE = {
        500, 1600, 1600, 3000, 3200, 3000, 20000
    };

    private static int currentRow = 0;
    private static CellStyle tStyle; // タイトル用
    private static CellStyle wStyle; // 内容用
    private static CellStyle ctStyle; // 可変用（タイプ）
    private static CellStyle chStyle; // 可変用（縦線）
    private static CellStyle cuStyle; // 可変用（上線）

    public static void writeExcel(Path path, SwbInfoModel sm) throws Exception {
        currentRow = 1;
        // 出力用ファイルの生成
        Workbook workbook = new XSSFWorkbook();
        createStyles(workbook);
        // Excelシートを生成
        Sheet sheet = workbook.createSheet("STAMP Tree");
        // タイトル部の生成
        Row title = sheet.createRow(currentRow);
        createCells(title, DATA_TITLE.length);
        for (int i = 0; i < DATA_TITLE.length; i++) {
            Cell cell = title.createCell(i, CellType.STRING);
            cell.setCellValue(DATA_TITLE[i]);
            if (i != 0) {
                cell.setCellStyle(tStyle);
            }
            sheet.setColumnWidth(i, COLUMN_SIZE[i]);
        }
        currentRow++;
        setActionData(sheet, sm.getChildren());

        File wFile = path.toFile();
        //if (!wFile.canWrite()) {
        //    throw new SwbException("Excelファイルを書き込む権限がありません。");
        //}

        // ファイルに書き込み
        try (FileOutputStream fos = new FileOutputStream(wFile.getAbsolutePath())) {
            workbook.write(fos);
        }
    }

    private static void createStyles(Workbook workbook) {
        // タイトルスタイル
        tStyle = workbook.createCellStyle();
        tStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorderStyle(tStyle, true, true, true, true);
        tStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        tStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 折り返し、罫線の設定
        wStyle = workbook.createCellStyle();
        wStyle.setWrapText(true);
        wStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderStyle(wStyle, true, true, true, true);

        // 可変用（タイプ）
        ctStyle = workbook.createCellStyle();
        ctStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderStyle(ctStyle, true, false, true, false);

        // 可変用（縦線）
        chStyle = workbook.createCellStyle();
        chStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderStyle(chStyle, false, false, true, false);

        // 可変用（上線）
        cuStyle = workbook.createCellStyle();
        cuStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderStyle(cuStyle, true, false, false, false);
    }

    private static void setBorderStyle(CellStyle style, boolean top, boolean bottom, boolean left, boolean right) {
        if (top) {
            style.setBorderTop(BorderStyle.THIN);
        } else {
            style.setBorderTop(BorderStyle.NONE);
        }
        if (bottom) {
            style.setBorderBottom(BorderStyle.THIN);
        } else {
            style.setBorderBottom(BorderStyle.NONE);
        }
        if (left) {
            style.setBorderLeft(BorderStyle.THIN);
        } else {
            style.setBorderLeft(BorderStyle.NONE);
        }
        if (right) {
            style.setBorderRight(BorderStyle.THIN);
        } else {
            style.setBorderRight(BorderStyle.NONE);
        }
    }

    private static void createCells(Row row, int size) {
        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue("");
        }
    }

    private static void setCellValue(Row row, int cellIndex, String value, CellStyle style) {
        Cell cell = row.getCell(cellIndex);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private static void setActionData(Sheet sheet, List<SwbInfoModel> actions) {
        for (int i = 0; i < actions.size(); i++) {
            Row row = sheet.createRow(currentRow);
            createCells(row, DATA_TITLE.length);
            SwbInfoModel sm = actions.get(i);

            setCellValue(row, 1, sm.getType(), ctStyle);
            setCellValue(row, 2, "", cuStyle);
            setCellValue(row, 3, "", cuStyle);
            setCellValue(row, 4, "", cuStyle);
            setCellValue(row, 5, "", cuStyle);
            setCellValue(row, 6, sm.getId(), wStyle);
            currentRow++;
            setUcaData(sheet, sm.getChildren());
        }
        Row last = sheet.createRow(currentRow);
        createCells(last, DATA_TITLE.length);
        setCellValue(last, 1, "", cuStyle);
        setCellValue(last, 2, "", cuStyle);
        setCellValue(last, 3, "", cuStyle);
        setCellValue(last, 4, "", cuStyle);
        setCellValue(last, 5, "", cuStyle);
        setCellValue(last, 6, "", cuStyle);
    }

    private static void setUcaData(Sheet sheet, List<SwbInfoModel> ucas) {
        for (int i = 0; i < ucas.size(); i++) {
            Row row = sheet.createRow(currentRow);
            createCells(row, DATA_TITLE.length);
            SwbInfoModel sm = ucas.get(i);
            setCellValue(row, 1, "", chStyle);
            setCellValue(row, 2, sm.getType(), ctStyle);
            setCellValue(row, 3, sm.getId(), cuStyle);
            setCellValue(row, 4, "", cuStyle);
            setCellValue(row, 5, "", cuStyle);
            setCellValue(row, 6, sm.getDescription(), wStyle);
            currentRow++;
            setHcfData(sheet, sm.getChildren());
        }
    }

    private static void setHcfData(Sheet sheet, List<SwbInfoModel> hcfs) {
        for (int i = 0; i < hcfs.size(); i++) {
            Row row = sheet.createRow(currentRow);
            createCells(row, DATA_TITLE.length);
            SwbInfoModel sm = hcfs.get(i);
            setCellValue(row, 1, "", chStyle);
            setCellValue(row, 2, "", chStyle);
            setCellValue(row, 3, "視点", ctStyle);
            setCellValue(row, 4, sm.getId(), cuStyle);
            setCellValue(row, 5, "", cuStyle);
            setCellValue(row, 6, sm.getDescription(), wStyle);
            currentRow++;
            setScenarioData(sheet, sm.getChildren());
        }
    }

    private static void setScenarioData(Sheet sheet, List<SwbInfoModel> scinas) {
        for (int i = 0; i < scinas.size(); i++) {
            Row row = sheet.createRow(currentRow);
            createCells(row, DATA_TITLE.length);
            SwbInfoModel sm = scinas.get(i);
            setCellValue(row, 1, "", chStyle);
            setCellValue(row, 2, "", chStyle);
            setCellValue(row, 3, "", chStyle);
            String type = sm.getType();
            String descri = sm.getDescription();
            setCellValue(row, 4, "シナリオ：" + sm.getId(), ctStyle);
            setCellValue(row, 5, "", cuStyle);
            setCellValue(row, 6, descri, wStyle);
            currentRow++;
            setSaftyMeasuresData(sheet, sm.getChildren());
        }
    }

    private static void setSaftyMeasuresData(Sheet sheet, List<SwbInfoModel> scinas) {
        for (int i = 0; i < scinas.size(); i++) {
            Row row = sheet.createRow(currentRow);
            createCells(row, DATA_TITLE.length);
            SwbInfoModel sm = scinas.get(i);
            setCellValue(row, 1, "", chStyle);
            setCellValue(row, 2, "", chStyle);
            setCellValue(row, 3, "", chStyle);
            setCellValue(row, 4, "", chStyle);
            String type = sm.getType();
            String descri = sm.getDescription();
            type = "安全対策";
            if (sm.getAtt() != null && sm.getAtt().length() > 0) {
                descri = descri + "\n注）" + sm.getAtt();
            }
            setCellValue(row, 5, type + sm.getId(), ctStyle);
            setCellValue(row, 6, descri, wStyle);
            currentRow++;
        }
    }
}
