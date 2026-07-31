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
package tmu.fs.swbs.hcftree.model;

import java.util.ArrayList;
import java.util.List;

/**
 * STAMP Workbenchのデータモデル情報オブジェクト
 *
 * @author Keiichi Tsumuta
 */
public class SwbInfoModel {

    public static final String ACTION = "action";     // コントロール　アクション
    public static final String UCA = "uca";           // UCA
    public static final String HCF = "hcf";           // HCF
    public static final String SCENARIO = "scenario"; // シナリオ
    public static final String SAFTY_ME = "safety measures";   // 安全対策

    private final String type;
    private final String xmiId;
    private final String id;
    private String description = null;
    private String att = null;
    private String att2 = null;
    private List<SwbInfoModel> children;
    private int sortValue;

    public SwbInfoModel(String type, String xmiId, String id) {
        this.type = type;
        this.xmiId = xmiId;
        this.id = id;
        this.children = new ArrayList<>();
    }

    public SwbInfoModel(String type, String xmiId, String id, String description) {
        this(type, xmiId, id);
        this.description = description;
    }

    public SwbInfoModel(String type, String xmiId, String id, String description, String att) {
        this(type, xmiId, id, description);
        this.att = att;
    }

    public SwbInfoModel(String type, String xmiId, String id, String description, String att, String att2) {
        this(type, xmiId, id, description, att);
        this.att2 = att2;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the xmiId
     */
    public String getXmiId() {
        return xmiId;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the att
     */
    public String getAtt() {
        return att;
    }

    /**
     * @param att the att to set
     */
    public void setAtt(String att) {
        this.att = att;
    }

    /**
     * @return the att2
     */
    public String getAtt2() {
        return att2;
    }

    /**
     * @param att2 the att2 to set
     */
    public void setAtt2(String att2) {
        this.att2 = att2;
    }

    /**
     * @return the children
     */
    public List<SwbInfoModel> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<SwbInfoModel> children) {
        this.children = children;
    }

    public void clearChildren() {
        children = new ArrayList<>();
    }

    /**
     * @param children the children to set
     */
    public void addChild(SwbInfoModel child) {
        this.children.add(child);
    }

    /**
     * @return the sortValue
     */
    public int getSortValue() {
        return sortValue;
    }

    /**
     * @param sortValue the sortValue to set
     */
    public void setSortValue(int sortValue) {
        this.sortValue = sortValue;
    }

    @Override
    public SwbInfoModel clone() {
        SwbInfoModel sm = new SwbInfoModel(type, xmiId, id, description, att, att2);
        sm.setSortValue(sortValue);
        for (SwbInfoModel child : children) {
            sm.addChild(child.clone());
        }
        return sm;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case ACTION ->
                sb.append(type).append(" : ").append(getId());
            case UCA -> {
                sb.append(type).append(" : ").append(getId()).append(":");
                if (description != null) {
                    sb.append(description);
                }
            }
            case HCF -> {
                sb.append(type).append(" : ").append(getId()).append(":");
                if (description != null) {
                    sb.append(description);
                }
            }
            case SCENARIO -> {
                sb.append("シナリオ").append(" : ").append(getId()).append(":");
                if (description != null) {
                    sb.append(description);
                }
            }
            case SAFTY_ME -> {
                sb.append("安全対策").append(" : ").append(getId()).append(":");
                if (description != null) {
                    sb.append(description);
                }
                if (getAtt() != null && getAtt().length() > 0) {
                    sb.append("\n注）").append(getAtt());
                }
            }
            default -> {
                sb.append(type);
                if (description != null) {
                    sb.append(description);
                }
            }
        }
        return sb.toString();
    }

    public String toTreeString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(":");
        sb.append(id).append(":");
        sb.append(description).append(":");
        sb.append(att).append(", ").append(att2).append("\n");
        for (SwbInfoModel child : children) {
            sb.append("\t");
            sb.append(child.toTreeString());
        }
        sb.append("\n");
        return sb.toString();
    }

}
