package cn.zzs.dom4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

/**
 * @ClassName: XMLParser
 * @Description: 解析XML文件--使用dom4j
 * @author: zzs
 * @date: 2019年9月1日 上午12:17:06
 */
public class XMLParser {

	// 文档模型
	private Document document = null;

	// 元素的根节点<root>
	private Element root = null;

	public XMLParser(String fileName) {
		super();
		init(fileName);
	}

	public XMLParser() {
		super();
	}

	/**
	 * @Title: list1
	 * @Description: 第一种遍历节点的方法：Iterator+递归
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:33:16
	 * @param parent
	 * @return: void
	 */
	public void list1(Element parent) {
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
	 * @Title: list2
	 * @Description: 第二种遍历节点的方法：node(i)+递归
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:37:17
	 * @param parent
	 * @return: void
	 */
	public void list2(Element parent) {
		if(parent == null) {
			return;
		}
		// 遍历节点中的Node
		for(int i = 0, size = parent.nodeCount(); i < size; i++) {
			// 获得当前Node
			Node node = parent.node(i);
			// 获得node的类型
			int nodeType = node.getNodeType();
			if(Node.ELEMENT_NODE != nodeType) {
				continue;
			}
			Element element = (Element)node;
			// 遍历当前节点属性并输出
			printAttr(element);
			// 递归
			list2(element);
		}
	}

	/**
	 * 
	 * @Title: list3
	 * @Description: 第三种遍历节点的方法：VisitorSupport
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:40:54
	 * @param root
	 * @return: void
	 */
	public void list3(Element root) {
		if(root == null) {
			return;
		}
		root.accept(new VisitorSupport() {

			public void visit(Element element) {
				printAttr(element);
			}
		});
	}

	/**
	 * 
	 * @Title: init
	 * @Description: 解析指定文件，并初始化Document和根节点
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:27:38
	 * @param fileName
	 * @return: void
	 */
	private void init(String fileName) {
		// 创建指定文件的File对象
		File file = new File(fileName);
		// 创建SAXReader
		SAXReader saxReader = new SAXReader();
		try {
			// 将xml文件读入成document
			document = saxReader.read(file);
			// 获得根元素
			root = document.getRootElement();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Element getRoot() {
		return root;
	}

	public Document getDocument() {
		return document;
	}

	/**
	 * 
	 * @Title: printAttr
	 * @Description: 遍历指定节点的属性
	 * @author: zzs
	 * @date: 2019年11月3日 下午10:20:09
	 * @param element
	 * @return: void
	 */
	public void printAttr(Element element) {
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
}
