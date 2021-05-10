package cn.zzs.dom4j;

import cn.zzs.dom4j.entity.Student;
import cn.zzs.dom4j.entity.StudentList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JAXP读xml工具类
 *
 * @author zzs
 * @version 1.0.0
 * @date 2021/5/3
 */
public class JaxpReader {

    public static List<Student> getStudentFromXmlDom() throws Exception {
        // 创建DocumentBuilder对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 将xml解析为树
        Document document = builder.parse("xml/students.xml");
        // 获取所有Student节点并映射成对象(这里假设我不知道student节点在哪一级，或者哪几级)
        return mapElementsToStudents(document.getChildNodes());
    }


    public static List<Student> getStudentFromXmlSax() throws Exception {
        List<Student> students = new ArrayList<>();
        // 创建SAXParser对象
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        // 注册处理器，并进行解析
        saxParser.parse("xml/students.xml", new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if ("student".equals(qName)) {
                    Student student = new Student();
                    Optional.ofNullable(attributes.getValue("name")).ifPresent(student::setName);
                    Optional.ofNullable(attributes.getValue("age")).map(Integer::valueOf).ifPresent(student::setAge);
                    Optional.ofNullable(attributes.getValue("location")).ifPresent(student::setLocation);
                    students.add(student);
                }
            }
        });
        return students;
    }


    public static List<Student> getStudentFromXmlByXPath() throws Exception {
        // 创建DocumentBuilder对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 将xml解析为树
        Document document = builder.parse("xml/students.xml");
        // 使用xpath随机获取节点(这里假设我不知道student节点在哪一级，或者哪几级)
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("//student");
        NodeList nl = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        return mapElementsToStudents(nl);
    }

    public static List<Student> getStudentFromXmlByUnmarshall() throws Exception {
        // return JAXB.unmarshal("xml/students.xml", Student.class);
        JAXBContext jc = JAXBContext.newInstance(StudentList.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        StudentList studentList = (StudentList) unmarshaller.unmarshal(new File("xml/students.xml"));
        return studentList.getStudents();
    }

    private static List<Student> mapElementsToStudents(NodeList childNodes) {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            // 转换当前节点
            if ("student".equals(node.getNodeName())) {
                Optional.ofNullable(mapElementToStudent((Element) node)).ifPresent(students::add);
            }
            // 递归转换子节点
            students.addAll(mapElementsToStudents(node.getChildNodes()));
        }
        return students;
    }

    private static Student mapElementToStudent(Element element) {
        Student student = new Student();
        Optional.ofNullable(element.getAttribute("name")).ifPresent(student::setName);
        Optional.ofNullable(element.getAttribute("age")).map(Integer::valueOf).ifPresent(student::setAge);
        Optional.ofNullable(element.getAttribute("location")).ifPresent(student::setLocation);
        return student;
    }
}
