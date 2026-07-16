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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tmu.fs.swbs.hcftree.SwbXml;

/**
 * model.stampnotationファイルの解析を行う。
 *
 * @author Keiichi Tsumuta
 */
public class NotationXml extends SwbXml {

    public static final String CSD = "stampn:STAMPControlStructureDiagram";
    public static final String OE = "ownedElement";
    public static final String WAYPOINT = "waypoint";
    public static final String BOUNDS = "bounds";
    public static final String MODEL = "modelElement";
    private final List<NotationModel> notations = new ArrayList<>();

    public NotationXml() {
        super();
    }

    public List<NotationModel> parse(byte[] xmlBuf) throws Exception {
        //System.out.println("NotationXml start : --- ");
        Document doc = parseXml(xmlBuf);
        Element xmlRoot = doc.getDocumentElement();
        parseCSD(xmlRoot);
        sortList();
        return notations;
    }

    private void parseCSD(Element xmlRoot) {
        NodeList nodes = xmlRoot.getElementsByTagName(CSD);
        if (nodes.getLength() == 0) {
            return;
        }
        Element elem = (Element) nodes.item(0);
        NodeList owneds = elem.getElementsByTagName(OE);
        for (int k = 0; k < owneds.getLength(); k++) {
            Element owned = (Element) owneds.item(k);
            if (!(owned.getAttribute("xmi:type")).equals("stampn:STAMPEdge")) {
                continue;
            }

            NodeList ways = owned.getElementsByTagName(WAYPOINT);
            if (ways.getLength() <= 0) {
                continue;
            }
            Element way = (Element) ways.item(0);
            int x = getInt(way.getAttribute("x"));
            int y = getInt(way.getAttribute("y"));

            NodeList owned2s = owned.getElementsByTagName(OE);
            String href = "";
            String text = "";
            for (int n = 0; n < owned2s.getLength(); n++) {
                Element owned2 = (Element) owned2s.item(n);
                text = owned2.getAttribute("text");
                // 対応するActionの情報を求める。
                NodeList models = owned2.getElementsByTagName(MODEL);
                if (models.getLength() > 0) {
                    Element model = (Element) models.item(0);
                    href = model.getAttribute("href");
                }
            }
            NotationModel model = new NotationModel(href, x, y, text);
            notations.add(model);
            //System.out.println("** NotationModel : " + model.toString());
        }
    }

    private int getInt(String val) {
        try {
            double xx = Double.parseDouble(val);
            return (int) xx;
        } catch (Exception ex) {
        }
        return 0;
    }

    private void sortList() {
        Collections.sort(notations,
                (NotationModel obj1, NotationModel obj2)
                -> (obj1.getSortValue() - obj2.getSortValue()));
    }

}
