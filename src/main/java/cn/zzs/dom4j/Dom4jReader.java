package cn.zzs.dom4j;

import cn.zzs.dom4j.entity.Student;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * dom4j 读xml工具类
 *
 * @author zzs
 * @version 1.0.0
 * @date 2021/5/3
 */
public class Dom4jReader {

    public static List<Student> getStudentFromXml() throws Exception {
        // 创建SAXReader
        SAXReader saxReader = SAXReader.createDefault();
        // 将xml解析为树
        Document document = saxReader.read("xml/students.xml");
        // 获得根节点
        Element root = document.getRootElement();
        // 获取所有student节点并映射成学生对象(这里假设我不知道student节点在哪一级，或者哪几级)
        return mapElementsToStudents(root.elements());
    }

    public static List<Student> getStudentFromXmlByXpath() throws Exception {
        // 创建SAXReader
        SAXReader saxReader = SAXReader.createDefault();
        // 将xml解析为树
        Document document = saxReader.read("xml/students.xml");
        // 使用xpath随机获取节点(这里假设我不知道student节点在哪一级，或者哪几级)
        List<Node> list = document.selectNodes("//student");
        // 映射成学生对象
        return list.stream()
                .map(node -> mapElementToStudent((Element) node))
                .collect(Collectors.toList());
    }

    private static List<Student> mapElementsToStudents(List<Element> elements) {
        List<Student> students = new ArrayList<>();
        elements.stream().forEach(element -> {
            // 转换当前节点
            if ("student".equals(element.getName())) {
                Optional.ofNullable(mapElementToStudent(element)).ifPresent(students::add);
            }
            // 递归转换子节点
            students.addAll(mapElementsToStudents(element.elements()));
        });
        return students;
    }

    private static Student mapElementToStudent(Element element) {
        Student student = new Student();
        Optional.ofNullable(element.attributeValue("name")).ifPresent(student::setName);
        Optional.ofNullable(element.attributeValue("age")).map(Integer::valueOf).ifPresent(student::setAge);
        Optional.ofNullable(element.attributeValue("location")).ifPresent(student::setLocation);
        return student;
    }
}
