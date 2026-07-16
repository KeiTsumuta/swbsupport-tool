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
package tmu.fs.swbs.hcftree.modelnotation;

/**
 * コントロールストラクチャ図の表示座標とそのエレメント情報を保持する。
 *
 * @author Keiichi Tsumuta
 */
public class NotationModel {

    private final String modelId; // モデル側のxml:id
    private final int x; // X座標
    private final int y; // Y座標
    private final String text;
    private final int sortValue;

    public NotationModel(String href, int x, int y, String text) {
        String[] tks = href.split("#");
        this.modelId = tks[1];
        this.x = x;
        this.y = y;
        this.text = text;
        this.sortValue = y * 10000 + x;
    }

    /**
     * @return the modelId
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    public int getSortValue() {
        return sortValue;
    }

    public String toString() {
        return "(" + x + "," + y + ") " + sortValue + "," + modelId + ", " + text + "," + sortValue;
    }
}
