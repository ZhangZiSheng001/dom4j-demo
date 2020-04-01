package cn.zzs.jdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @ClassName: JDKXMLTest
 * @Description: 测试使用JDK自带的API创建和解析xml文件
 * @author: zzs
 * @date: 2019年9月1日 上午12:09:54
 */
public class JDKXMLTest {

    /**
     * 创建XML文件--使用JDK的DOM接口
     * 
     */
    @Test
    public void test01() throws Exception {
        // 创建工厂对象
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // 创建DocumentBuilder对象
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        // 创建Document对象
        Document document = documentBuilder.newDocument();
        // 创建根节点
        Element root = document.createElement("members");
        document.appendChild(root);
        // 添加一级节点
        Element studentsElement = (Element)root.appendChild(document.createElement("students"));
        Element teachersElement = (Element)root.appendChild(document.createElement("teachers"));
        // 添加二级节点并设置属性
        Element studentElement1 = (Element)studentsElement.appendChild(document.createElement("student"));
        studentElement1.setAttribute("name", "张三");
        studentElement1.setAttribute("age", "18");
        studentElement1.setAttribute("location", "河南");
        Element studentElement2 = (Element)studentsElement.appendChild(document.createElement("student"));
        studentElement2.setAttribute("name", "李四");
        studentElement2.setAttribute("age", "26");
        studentElement2.setAttribute("location", "新疆");
        Element studentElement3 = (Element)studentsElement.appendChild(document.createElement("student"));
        studentElement3.setAttribute("name", "王五");
        studentElement3.setAttribute("age", "20");
        studentElement3.setAttribute("location", "北京");
        Element teacherElement1 = (Element)teachersElement.appendChild(document.createElement("teacher"));
        teacherElement1.setAttribute("name", "zzs");
        teacherElement1.setAttribute("age", "18");
        teacherElement1.setAttribute("location", "河南");
        Element teacherElement2 = (Element)teachersElement.appendChild(document.createElement("teacher"));
        teacherElement2.setAttribute("name", "zzf");
        teacherElement2.setAttribute("age", "26");
        teacherElement2.setAttribute("location", "新疆");
        Element teacherElement3 = (Element)teachersElement.appendChild(document.createElement("teacher"));
        teacherElement3.setAttribute("name", "lt");
        teacherElement3.setAttribute("age", "20");
        teacherElement3.setAttribute("location", "北京");
        // 获取文件对象
        File file = new File("members.xml");
        if(!file.exists()) {
            file.createNewFile();
        }
        // 获取Transformer对象
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // 设置编码、美化格式
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // 创建DOMSource对象
        DOMSource domSource = new DOMSource(document);
        // 将document写出
        transformer.transform(domSource, new StreamResult(new PrintWriter(new FileOutputStream(file))));
    }

    /**
     * 
     * @Title: test02
     * @Description: 解析xml文件-使用JDK的DOM接口,并使用JAXP
     * @author: zzs
     * @date: 2019年11月17日 下午2:21:12
     * @return: void
     */
    @Test
    public void test02() throws Exception {
        // 获得DocumentBuilder对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 解析xml文件，获得Document对象
        Document document = builder.parse("members.xml");
        // 遍历节点
        printNodeList(document.getChildNodes());
    }

    /**
     * 
     * @Title: test05
     * @Description: 解析xml文件-使用JDK的DOM接口
     * @author: zzs
     * @date: 2019年11月17日 下午2:21:12
     * @return: void
     */
    @SuppressWarnings("restriction")
    @Test
    public void test05() throws Exception {
        // 获得DOMParser对象
        com.sun.org.apache.xerces.internal.parsers.DOMParser domParser = new com.sun.org.apache.xerces.internal.parsers.DOMParser();
        // 解析文件
        domParser.parse(new InputSource("members.xml"));
        // 获得Document对象
        Document document = domParser.getDocument();
        // 递归遍历节点
        printNodeList(document.getChildNodes());
    }

    /**
     * 
     * @Title: test03
     * @Description: 解析xml文件-使用JDK的SAX接口,并使用JAXP
     * @author: zzs
     * @date: 2019年11月17日 下午2:21:12
     * @return: void
     */
    @Test
    public void test03() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse("members.xml", new DefaultHandler() {

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                StringBuffer buffer = new StringBuffer("");
                buffer.append(qName + ":");
                int attributeLength = -1;
                if(attributes != null && (attributeLength = attributes.getLength()) > 0) {
                    for(int j = 0; j < attributeLength; j++) {
                        buffer.append(attributes.getQName(j)).append("=").append(attributes.getValue(j));
                        if(j != attributeLength - 1) {
                            buffer.append(",");
                        }
                    }
                }
                System.out.println(buffer);
            }
        });
    }

    /**
     * 
     * @Title: test04
     * @Description: 解析xml文件-使用JDK的SAX接口
     * @author: zzs
     * @date: 2019年11月17日 下午2:21:12
     * @return: void
     */
    @Test
    public void test04() throws Exception {
        /*
         * 这里解释下四个的接口： EntityResolver：需要实现resolveEntity方法。当解析xml需要引入外部数据源时触发，通过这个方法可以重定向到本地数据源或进行其他操作。 DTDHandler：需要实现notationDecl和unparsedEntityDecl方法。当解析到"NOTATION", "ENTITY"或 "ENTITIES"时触发。 ContentHandler：最常用的一个接口，需要实现startDocument、endDocument、startElement、endElement等方法。当解析到指定元素类型时触发。 ErrorHandler：需要实现warning、error或fatalError方法。当解析出现异常时会触发。 DefaultHandler实现了EntityResolver, DTDHandler, ContentHandler, ErrorHandler四个接口
         */
        DefaultHandler handler = new DefaultHandler() {

            @Override
            // 当解析到Element时，触发打印该节点名
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                /*
                 * StringBuffer buffer = new StringBuffer(""); buffer.append(qName + ":"); int attributeLength = -1; if(attributes != null && (attributeLength = attributes.getLength()) > 0) { for(int j = 0; j < attributeLength; j++) { buffer.append(attributes.getQName(j)).append("=").append(attributes.getValue(j)); if(j != attributeLength - 1) { buffer.append(","); } } } System.out.println(buffer);
                 */
                System.out.println(qName);
            }
        };
        // 获取解析器实例
        XMLReader xr = XMLReaderFactory.createXMLReader();
        // 设置处理类
        xr.setContentHandler(handler);
        /*
         * xr.setErrorHandler(handler); xr.setDTDHandler(handler); xr.setEntityResolver(handler);
         */
        xr.parse(new InputSource("members.xml"));
    }

    /**
     * 
     * @Title: printNodeList
     * @Description: 递归遍历节点列表
     * @author: zzs
     * @date: 2019年11月17日 下午2:29:17
     * @param nodeList
     * @return: void
     */
    private void printNodeList(NodeList nodeList) {
        int length = -1;
        if(nodeList == null || (length = nodeList.getLength()) < 1) {
            return;
        }
        for(int i = 0; i < length; i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element)node;
            printNode(element);
            printNodeList(element.getChildNodes());
        }
    }

    /**
     * 
     * @Title: printNode
     * @Description: 打印节点
     * @author: zzs
     * @date: 2019年11月17日 下午3:27:25
     * @param element
     * @return: void
     */
    private void printNode(Element element) {
        StringBuffer buffer = new StringBuffer("");
        buffer.append(element.getNodeName() + ":");
        NamedNodeMap attributes = element.getAttributes();
        int attributeLength = -1;
        if(attributes != null && (attributeLength = attributes.getLength()) > 0) {
            for(int j = 0; j < attributeLength; j++) {
                Attr attr = (Attr)attributes.item(j);
                buffer.append(attr.getName()).append("=").append(attr.getValue());
                if(j != attributeLength - 1) {
                    buffer.append(",");
                }
            }
        }
        System.out.println(buffer);
    }
}
