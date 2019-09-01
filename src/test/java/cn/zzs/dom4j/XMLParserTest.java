package cn.zzs.dom4j;

import org.dom4j.Element;
import org.junit.Test;

/**
 * @ClassName: XMLParserTest
 * @Description: 测试使用dom4j解析xml
 * @author: zzs
 * @date: 2019年9月1日 上午12:44:38
 */
public class XMLParserTest {
	/**
	 * 测试dom4j解析xml
	 */
	@Test
	public void test01() {
		XMLParser xmlParser = new XMLParser("members.xml");
		Element root = xmlParser.getRoot();
		System.out.println("-------第一种遍历方式：Iterator+递归--------");
		xmlParser.list1(root);
		System.out.println("-------第二种遍历方式：node(i)+递归--------");
		xmlParser.list2(root);
		System.out.println("-------第三种遍历方式：VisitorSupport--------");
		xmlParser.list3(root, "student");
		System.out.println("-------第四种遍历方式：XPath方式查找节点--------");
		xmlParser.list4("student");
	}

}
