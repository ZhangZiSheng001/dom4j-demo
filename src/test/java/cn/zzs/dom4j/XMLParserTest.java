package cn.zzs.dom4j;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;

/**
 * @ClassName: XMLParserTest
 * @Description: 测试使用dom4j解析xml
 * @author: zzs
 * @date: 2019年9月1日 上午12:44:38
 */
public class XMLParserTest {

    /**
     * 测试三种不同的遍历方式--使用自定义工具类
     */
    @Test
    public void test01() {
        XMLParser xmlParser = new XMLParser("members.xml");
        Element root = xmlParser.getRoot();
        System.out.println("-------第一种遍历方式：Iterator+递归--------");
        xmlParser.list1(root);
        // System.out.println("-------第二种遍历方式：node(i)+递归--------");
        // xmlParser.list2(root);
        // System.out.println("-------第三种遍历方式：VisitorSupport--------");
        // xmlParser.list3(root);
    }

    /**
     *  测试XPath获取指定节点
     */
    @Test
    public void test02() {
        XMLParser xmlParser = new XMLParser("members.xml");
        List<Node> list = xmlParser.getDocument().selectNodes("//members/students");
        // List<Node> list = xmlParser.getDocument().selectSingleNode("students");
        // 遍历节点
        Iterator<Node> iterator = list.iterator();
        while(iterator.hasNext()) {
            Element element = (Element)iterator.next();
            System.out.println(element);
            // xmlParser.printAttr(element);
        }
    }

    /**
     *  测试遍历节点
     */
    @Test
    public void test03() throws Exception {
        // 创建指定文件的File对象
        File file = new File("members.xml");
        // 创建SAXReader
        SAXReader saxReader = new SAXReader();
        // 将xml文件读入成document
        Document document = saxReader.read(file);
        // 获得根元素
        Element root = document.getRootElement();
        // 递归遍历节点
        list1(root);
    }

    /**
     * 递归遍历节点
     */
    private void list1(Element parent) {
        if(parent == null) {
            return;
        }
        // 遍历当前节点属性并输出
        printAttr(parent);
        // 递归打印子节点
        Iterator<Element> iterator2 = parent.elementIterator();
        while(iterator2.hasNext()) {
            Element son = (Element)iterator2.next();
            list1(son);
        }
    }

    /**
     * 打印节点
     */
    private void printAttr(Element element) {
        // 遍历当前节点属性并输出
        Iterator<Attribute> iterator1 = element.attributeIterator();
        StringBuffer buffer = new StringBuffer("");
        while(iterator1.hasNext()) {
            Attribute attribute = iterator1.next();
            buffer.append(attribute.getName() + "=" + attribute.getText() + ",");
        }
        String str = buffer.toString();
        if(!"".equals(str)) {
            int lastIndexOfComma = str.lastIndexOf(",");
            StringBuffer outputStr = new StringBuffer();
            if(lastIndexOfComma != -1) {
                outputStr.append(element.getName()).append(":").append(str.substring(0, lastIndexOfComma));
                System.out.println(outputStr);
            }
        }
    }

    /**
     *  测试XPath获取指定节点
     */
    @Test
    public void test04() throws Exception {
        // 创建指定文件的File对象
        File file = new File("members.xml");
        // 创建SAXReader
        SAXReader saxReader = new SAXReader();
        // 将xml文件读入成document
        Document document = saxReader.read(file);
        // 使用xpath随机获取节点
        List<Node> list = document.selectNodes("//members//students/student");
        // List<Node> list = xmlParser.getDocument().selectSingleNode("students");
        // 遍历节点
        Iterator<Node> iterator = list.iterator();
        while(iterator.hasNext()) {
            Element element = (Element)iterator.next();
            printAttr(element);
        }
    }
}
