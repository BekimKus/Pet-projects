import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CurrencyParser {
    public static void main(String[] args) throws Exception {
        String[][] rates = getRates();
        String[] columnsName = {"Валюта", "Код", "Стоимость"};
        JFrame frame = new JFrame("Currency Parser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTable table = new JTable(rates, columnsName);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Calibri", Font.BOLD, 20));

        table.setFont(new Font("Calibri", Font.PLAIN, 18));
        table.setRowHeight(table.getRowHeight() + 18);

        DefaultTableCellRenderer centreRenderer = new DefaultTableCellRenderer();
        centreRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centreRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centreRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centreRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(1);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static String[][] getRates() throws Exception {
        HashMap<String, NodeList> map = new HashMap<String, NodeList>();
        String[][] rates = null;

        Document document;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String url = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=";
        String input = JOptionPane.showInputDialog(null, "Enter the date by format dd/MM/yyyy");
        if (input != null) {
            url += input;
            document = loadXMLFile(url);
        }
        else {
            Date date = new Date();
            document = loadXMLFile(url + format.format(date));
        }

        NodeList nodeList = document.getElementsByTagName("Valute");

        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList childNodes = nodeList.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node temp = childNodes.item(j);
                if (temp.getNodeName().equals("CharCode")) {
                    map.put(temp.getTextContent(), childNodes);
                    //System.out.println(temp.getTextContent());
                }
            }
        }

        int k = 0;
        rates = new String[map.size()][3];

        for (Map.Entry<String, NodeList> entry : map.entrySet()) {
            NodeList temp = entry.getValue();
            double value = 0;
            int nominal = 0;
            String name = "";

            for (int i = 0; i < temp.getLength(); i++) {
                if (temp.item(i).getNodeName().equals("Value")) {
                    value = Double.parseDouble(temp.item(i).getTextContent().replace(',', '.'));
                }
                if (temp.item(i).getNodeName().equals("Nominal")) {
                    nominal = Integer.parseInt(temp.item(i).getTextContent());
                }
                if (temp.item(i).getNodeName().equals("Name")) {
                    name = temp.item(i).getTextContent();
                }
            }

            value = value / nominal;

            rates[k][0] = name;
            rates[k][1] = entry.getKey();
            rates[k][2] = (((double) Math.round(value * 10000)) / 10000) + " руб.";
            k++;
        }
         /*
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(rates[i][j] + " ");
            }
            System.out.println();
        }

          */

        System.out.println(document.getXmlVersion());

        return rates;
    }

    public static Document loadXMLFile(String url) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new URL(url).openStream());
        return document;
    }
}
