package com.mogobiz.mockserver.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by smanciot on 18/04/16.
 */
public final class XPathHelper {

    private XPathHelper(){}

    public static String getValue(byte[] bytes, String xpath) throws UnsupportedEncodingException {
        return getValue(new InputStreamReader(new ByteArrayInputStream(bytes)), xpath);
    }

    public static String getValue(String xml, String xpath){
        return getValue(new StringReader(xml), xpath);
    }

    private static String getValue(Reader reader, String xpath){
        try {
            Document dDoc = parseDocument(reader);
            XPathExpression xPathExpression = computeXPath(xpath);
            Node n = (Node) xPathExpression.evaluate(dDoc, XPathConstants.NODE);
            if(n != null){
                return n.getNodeValue();
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] getValues(byte[] bytes, String xpath) throws UnsupportedEncodingException {
        return getValues(new InputStreamReader(new ByteArrayInputStream(bytes)), xpath);
    }

    public static String[] getValues(String xml, String xpath){
        return getValues(new StringReader(xml), xpath);
    }

    private static String[] getValues(Reader reader, String xpath){
        try {
            Document dDoc = parseDocument(reader);
            XPathExpression xPathExpression = computeXPath(xpath);
            NodeList nl = (NodeList) xPathExpression.evaluate(dDoc, XPathConstants.NODESET);
            if(nl != null){
                List<String> ret = new ArrayList<>();
                final int length = nl.getLength();
                for(int i = 0; i < length; i++){
                    ret.add(i, nl.item(i).getNodeValue());
                }
                return ret.toArray(new String[length]);
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private static Document parseDocument(Reader reader) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        return builder.parse(new InputSource(reader));
    }

    private static XPathExpression computeXPath(String xpath) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.compile(xpath);
    }
}
