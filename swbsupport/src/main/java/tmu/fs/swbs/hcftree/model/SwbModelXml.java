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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tmu.fs.swbs.hcftree.SwbXml;
import tmu.fs.swbs.hcftree.modelnotation.NotationModel;

/**
 * model.stampファイルの解析を行う。
 *
 * @author Keiichi Tsumuta
 */
public class SwbModelXml extends SwbXml {

    public static final String UCA = "stamp_uca"; // UCA情報
    public static final String HCF = "stamp_hcf"; // HCF情報

    private List<SwbInfoModel> caList; // コントロールアクション情報リスト
    private List<SwbInfoModel> ucaList; // UCA情報リスト
    private List<SwbInfoModel> hcfList; // HCF情報リスト
    private Map<String, SwbInfoModel> saMap;  // 安全策情報マップ

    private List<NotationModel> notations;

    public SwbModelXml() {
        super();
    }

    public void parse(byte[] xmlBuf) throws Exception {
        caList = new ArrayList<>();
        ucaList = new ArrayList<>();
        hcfList = new ArrayList<>();
        saMap = new HashMap<>();

        Document doc = parseXml(xmlBuf);
        Element stampSTPAAnalysis = doc.getDocumentElement();
        // 安全対策情報マップ取出し
        safetyMeasuresParse(stampSTPAAnalysis);
        // コントロールアクション情報リスト取出し
        linkParse(stampSTPAAnalysis);
        // UCA情報リスト取出し
        ucaParse(stampSTPAAnalysis);
        // HCF情報リスト取出し
        hcfParse(stampSTPAAnalysis);

        System.out.println("List size: " + caList.size() + ", " + ucaList.size() + ", " + hcfList.size() + ", " + saMap.size());
    }

    //　コントロールアクション情報リスト取出し
    private void linkParse(Element stampSTPAAnalysis) {
        NodeList nodes = stampSTPAAnalysis.getElementsByTagName("controlStructure");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element elem = (Element) nodes.item(i);
            NodeList links = elem.getElementsByTagName("link");
            for (int k = 0; k < links.getLength(); k++) {
                Element link = (Element) links.item(k);
                String xmiType = link.getAttribute("xmi:type");
                if (!xmiType.equals("stamp:ControlLink")) {
                    continue;
                }
                NodeList acs = link.getElementsByTagName("action");
                for (int m = 0; m < acs.getLength(); m++) {
                    Element action = (Element) acs.item(m);
                    String name = action.getAttribute("name");
                    String xmiId = action.getAttribute("xmi:id");
                    SwbInfoModel sm = new SwbInfoModel(SwbInfoModel.ACTION, xmiId, name, null);
                    caList.add(sm);
                    //System.out.println("action:" + xmiId + ", " + name + ", " + i + ", " + k + ", " + m);
                }
            }
        }
    }

    // UCA情報リストを取り出す。
    private void ucaParse(Element stampSTPAAnalysis) {
        NodeList nodes = stampSTPAAnalysis.getElementsByTagName("unsafeControlAction");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element uca = (Element) nodes.item(i);
            String xmild = uca.getAttribute("xmi:id");
            String id = uca.getAttribute("id");
            String desc = uca.getAttribute("description");
            String att = uca.getAttribute("controlAction");
            SwbInfoModel sm = new SwbInfoModel(SwbInfoModel.UCA, xmild, id, desc, att);
            if (id.startsWith("UCA")) {
                try {
                    String[] tk = id.split("-");
                    if (tk.length > 0) {
                        String ucaIdNum = tk[0].substring(3);
                        int num = Integer.parseInt(ucaIdNum) * 100000;
                        num = switch (tk[1]) {
                            case "N" ->
                                num + 1000;
                            case "P" ->
                                num + 2000;
                            case "T" ->
                                num + 3000;
                            default ->
                                num + 4000;
                        };
                        num = num + Integer.parseInt(tk[2]);
                        sm.setSortValue(num);
                        //System.out.println("UCA id=" + sm.getId() + ", " + sm.getSortValue());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ucaList.add(sm);
            }
        }
    }

    // HCF情報リストを取り出す。
    private void hcfParse(Element stampSTPAAnalysis) {
        NodeList nodes = stampSTPAAnalysis.getElementsByTagName("hazardCausalFactor");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element hcf = (Element) nodes.item(i);
            String xmild = hcf.getAttribute("xmi:id");
            String id = hcf.getAttribute("id");
            String desc = hcf.getAttribute("description");
            String ucaRef = hcf.getAttribute("unsafeControlAction");
            String counterMes = hcf.getAttribute("countermeasure");
            String[] counterMeIds = {};
            if (counterMes != null && counterMes.length() > 0) {
                counterMeIds = counterMes.split(" ");
            }
            SwbInfoModel sm = new SwbInfoModel(SwbInfoModel.HCF, xmild, id, desc, ucaRef, counterMes);
            try {
                String[] tk = id.split("-");
                if (tk.length > 0) {
                    String hcfIdNum = tk[0].substring(3);
                    int num = Integer.parseInt(hcfIdNum) * 100000;
                    num = switch (tk[1]) {
                        case "N" ->
                            num + 1000;
                        case "P" ->
                            num + 2000;
                        case "T" ->
                            num + 3000;
                        default ->
                            num + 4000;
                    };
                    num = (num + Integer.parseInt(tk[2])) * 100 + Integer.parseInt(tk[3]);
                    sm.setSortValue(num);
                    //System.out.println("hcf id=" + sm.getId() + ", " + sm.getSortValue());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            hcfList.add(sm);
            NodeList hazardScenarios = hcf.getElementsByTagName("hazardScenario");
            for (int k = 0; k < hazardScenarios.getLength(); k++) {
                Element hs = (Element) hazardScenarios.item(k);
                String xmlid2 = hs.getAttribute("xmi:id");
                String desc2 = hs.getAttribute("description"); // シナリオ
                SwbInfoModel sm2 = new SwbInfoModel(SwbInfoModel.SCENARIO, xmlid2, "(" + (k + 1) + ")", desc2);
                sm.addChild(sm2);
            }

            for (int k = 0; k < counterMeIds.length; k++) {
                SwbInfoModel safty = saMap.get(counterMeIds[k]);
                if (safty != null) {
                    sm.addChild(safty);
                }
            }
            //System.out.println("HCF:"+id+", "+desc);
        }
    }

    // 安全対策情報リスト取出し
    private void safetyMeasuresParse(Element stampSTPAAnalysis) {
        NodeList nodes = stampSTPAAnalysis.getElementsByTagName("countermeasure");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element cm = (Element) nodes.item(i);
            String xmiId = cm.getAttribute("xmi:id"); // hazardCausalFactorのcountermeasureアトリビュートに対応
            String id = cm.getAttribute("id");
            String desc = cm.getAttribute("description"); // 安全対策
            String att = cm.getAttribute("remarks"); // 備考
            SwbInfoModel sm = new SwbInfoModel(SwbInfoModel.SAFTY_ME, xmiId, id, desc, att);
            saMap.put(xmiId, sm);
        }
    }

    // ツリー状構成の作成
    public SwbInfoModel getCaTree(int orderType, List<NotationModel> notations) {
        this.notations = notations;
        SwbInfoModel tree = new SwbInfoModel("tree", "tree", "stamp:STPAAnalysis", "", "");
        // CA(Controle Action)情報の取出し
        String name = "";
        SwbInfoModel sim = null;
        // CA(Controle Action)情報
        for (SwbInfoModel ca : getCaList()) {
            if (!name.equals(ca.getId())) {
                name = ca.getId();
                sim = ca;
            }
            if (sim == null) {
                continue;
            }
            // UCAツリー状構成
            setUcaHcfTree(ca.getXmiId(), sim);
            if (!sim.getChildren().isEmpty()) {
                tree.addChild(sim);
            }
        }

        //System.out.println("--------");
        // UCAのIDでソートする場合の値を算出する。
        if (orderType == 0) { // ID番号順
            for (SwbInfoModel sm : caList) {
                List<SwbInfoModel> cdList = sm.getChildren();
                if(!cdList.isEmpty()){
                    sm.setSortValue(cdList.get(0).getSortValue());
                }
                //System.out.println("CA id=" + sm.getId() + ", " + sm.getSortValue());
            }
        } else { // 座標順
            for (SwbInfoModel sm : caList) {
                sm.setSortValue(getSortValueByAction(sm.getXmiId()));
                //System.out.println("CA id=" + sm.getId() + ", " + sm.getSortValue());
            }
        }

        // ツリー構造をソートする。
        // CAリストをソート
        Collections.sort(tree.getChildren(),
                (SwbInfoModel obj1, SwbInfoModel obj2)
                -> (obj1.getSortValue() - obj2.getSortValue()));
        // CA中のUCAリストをソート
        for (int i = 0; i < tree.getChildren().size(); i++) {
            SwbInfoModel ucf = tree.getChildren().get(i);
            Collections.sort(ucf.getChildren(),
                    (SwbInfoModel obj1, SwbInfoModel obj2)
                    -> (obj1.getSortValue() - obj2.getSortValue()));
        }

        return tree;
    }

    // CA(Controle Action)情報の取出し
    private List<SwbInfoModel> getCaList() {
        List<SwbInfoModel> arr = new ArrayList<>();
        for (SwbInfoModel sm : caList) {
            if (sm.getType().equals(SwbInfoModel.ACTION)) {
                arr.add(sm);
            }
        }
        return arr;
    }

    private int getSortValueByAction(String id) {
        for (NotationModel nota : notations) {
            if (id.equals(nota.getModelId())) {
                return nota.getSortValue();
            }
        }
        return 0;
    }

    // UCAツリー状構成の作成
    private void setUcaHcfTree(String controlActionId, SwbInfoModel ca) {
        //System.out.println("setUcaHcfTree:" + controlActionId + ": " + ca.toString());
        // UCA情報の取出し
        Collections.sort(ucaList,
                (SwbInfoModel obj1, SwbInfoModel obj2)
                -> (obj1.getSortValue() - obj2.getSortValue()));
        for (SwbInfoModel sm : ucaList) {
            if (sm.getType().equals(SwbInfoModel.UCA) && sm.getAtt().equals(controlActionId)) {
                SwbInfoModel smc = sm.clone();
                ca.addChild(smc);
                String ucaXmlId = smc.getXmiId();
                List<SwbInfoModel> hcfs = getHcfObjs(ucaXmlId);
                for (SwbInfoModel hcf : hcfs) {
                    smc.addChild(hcf);
                }
            }
        }
    }

    // HCF情報に於いて、指定したunsafeControlActionに記されたSwbInfoModelを取り出す。
    private List<SwbInfoModel> getHcfObjs(String xmlId) {
        List<SwbInfoModel> hcfs = new ArrayList<>();
        for (SwbInfoModel sm : hcfList) {
            if (sm.getType().equals(SwbInfoModel.HCF)) {
                if (sm.getAtt().equals(xmlId)) {
                    hcfs.add(sm.clone());
                }
            }
        }
        return hcfs;
    }

}
