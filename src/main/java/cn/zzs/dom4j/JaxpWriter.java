package cn.zzs.dom4j;

import cn.zzs.dom4j.entity.Student;
import cn.zzs.dom4j.entity.StudentList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * JAXP写xml工具类
 *
 * @author zzs
 * @version 1.0.0
 * @date 2021/5/3
 */
public class JaxpWriter {

    public static void write(List<Student> students) throws ParserConfigurationException, IOException, TransformerException {
        // 创建DocumentBuilder对象
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        // 创建Document对象
        Document document = documentBuilder.newDocument();
        // 创建根节点
        Element root = document.createElement("students");
        document.appendChild(root);
        // 添加一级并设置属性
        students.stream().forEach(student -> {
            Element studentElement = (Element) root.appendChild(document.createElement("student"));
            studentElement.setAttribute("name", student.getName());
            studentElement.setAttribute("age", String.valueOf(student.getAge()));
            studentElement.setAttribute("location", student.getLocation());
        });
        // 获取文件对象
        File file = new File("xml/students.xml");
        if (!file.exists()) {
            file.createNewFile();
        }
        // 创建Transformer对象
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // 设置输出格式
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // 创建DOMSource对象
        DOMSource domSource = new DOMSource(document);
        // 输出xml
        OutputStream outputStream = new FileOutputStream(file);
        transformer.transform(domSource, new StreamResult(outputStream));
        outputStream.close();
    }

    public static void writeByMarshaller(StudentList studentList) throws JAXBException, FileNotFoundException {
        JAXBContext jc = JAXBContext.newInstance(StudentList.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(studentList, new FileOutputStream("xml/students.xml"));
    }
}
