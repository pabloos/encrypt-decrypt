package com.claytablet.cq5.ctctranslation.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class XMLUtil {
    private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);

    public static String removeBOM(final String xml_String)
    {
        String xmlString = xml_String;
        try
        {
            if (xmlString != null && xmlString.length() > 3)
            {
                byte[] xmlStringBytes = xmlString.getBytes("UTF-8");

                if ( (xmlStringBytes[0] == (byte) 0xEF) && (xmlStringBytes[1] == (byte) 0xBB) && (xmlStringBytes[2] == (byte) 0xBF) )
                {
                    //There is a BOM
                    byte[] xmlStringBytes_noBOM = new byte[xmlStringBytes.length - 3];
                    System.arraycopy(xmlStringBytes, 3, xmlStringBytes_noBOM, 0, xmlStringBytes_noBOM.length);
                    xmlString = new String(xmlStringBytes_noBOM, "UTF-8");
                }
            }
            else
                xmlString = xml_String;
        }
        catch (Exception e )
        {
            xmlString = xml_String;
        }

        return xmlString;
    }

    public static Document readXMLStringIntoDoc(String xmlString)
            throws ParserConfigurationException, SAXException, IOException {
        return readXMLStringIntoDoc(xmlString, true);
    }

    private static Document readXMLStringIntoDoc(String xmlString, boolean useClassLoaderHack)
            throws ParserConfigurationException, SAXException, IOException {

        if (useClassLoaderHack) {
            logger.debug("readXMLStringIntoDoc using CL hack");
            ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
            try {
                ClassLoader dbLoader = DocumentBuilderFactory.class.getClassLoader();
                Thread.currentThread().setContextClassLoader(dbLoader);
                return parseSourceIntoDoc(xmlString);
            } finally {
                Thread.currentThread().setContextClassLoader(originalLoader);
            }
        }
        else {
            return parseSourceIntoDoc(xmlString);
        }
    }

    private static Document parseSourceIntoDoc(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf;
        dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        return db.parse(is);
    }

    public static Document getNewXMLDoc() throws ParserConfigurationException {
        return getNewXMLDoc(true);
    }

    private static Document getNewXMLDoc(boolean useClassLoaderHack) throws ParserConfigurationException {
        if (useClassLoaderHack) {
            logger.debug("getNewXMLDoc using CL hack");
            ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
            try {
                ClassLoader dbLoader = DocumentBuilderFactory.class.getClassLoader();
                Thread.currentThread().setContextClassLoader(dbLoader);
                return getNewDocument();
            } finally {
                Thread.currentThread().setContextClassLoader(originalLoader);
            }
        }
        else {
            return getNewDocument();
        }
    }

    private static Document getNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf;
        dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        docBuilder = dbf.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    /*public static Object evalXPathFromStream(InputStream inputStream, String xpath_expression, boolean useClassLoaderHack) throws
            Exception {
        logger.debug("using CL hack:{}", useClassLoaderHack);
        Document doc = null;
        DocumentBuilderFactory dbf = null;
        ClassLoader originalLoader = useClassLoaderHack ? Thread.currentThread().getContextClassLoader() : null ;
        try {
            if (useClassLoaderHack) {
                ClassLoader dbLoader = DocumentBuilderFactory.class.getClassLoader();
                Thread.currentThread().setContextClassLoader(dbLoader);
            }
            dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(inputStream);

            if (useClassLoaderHack) {
                ClassLoader xpathLoader = XPathFactory.class.getClassLoader();
                Thread.currentThread().setContextClassLoader(xpathLoader);
            }
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            QName qName = XPathConstants.NODESET;
            return xpath.evaluate(xpath_expression, doc, qName);

        } catch (Exception e) {
            logger.error("Error evaluating xpath:" , e);
            logger.debug("full stack", org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(e));
            throw e;
        } finally {
            if (useClassLoaderHack) Thread.currentThread().setContextClassLoader(originalLoader);
        }


    }*/

    public static List<String> getTransUnitSourcesFromDoc (Document doc) {

        List<String> sources = new LinkedList<String>();



        NodeList nl = doc.getElementsByTagName("source");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            String s = node.getTextContent();
            s = s.replace("<![CDATA[", "").replace("]]>", "");
            sources.add(s);
        }

        return sources;
    }

    private static Logger logger = LoggerFactory.getLogger(XMLUtil.class);
}