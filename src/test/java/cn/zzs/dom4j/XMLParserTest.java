package cn.zzs.dom4j;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Test;

/**
 * @ClassName: XMLParserTest
 * @Description: 测试使用dom4j解析xml
 * @author: zzs
 * @date: 2019年9月1日 上午12:44:38
 */
public class XMLParserTest {

	/**
	 * 测试三种不同的遍历方式
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
		//List<Node> list = xmlParser.getDocument().selectSingleNode("students");
		// 遍历节点
		Iterator<Node> iterator = list.iterator();
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			System.out.println(element);
			// xmlParser.printAttr(element);
		}
	}
}
