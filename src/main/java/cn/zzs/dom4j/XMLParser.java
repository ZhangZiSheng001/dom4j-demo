package cn.zzs.dom4j;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

/**
 * @ClassName: XMLParser
 * @Description: 用于解析XML文件
 * @author: zzs
 * @date: 2019年9月1日 上午12:17:06
 */
public class XMLParser {
	//文档模型
	private Document document = null;
	//元素的根节点<root>
	private Element root = null;

	public XMLParser(String fileName) {
		super();
		init(fileName);
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
		if(parent==null) {
			return;
		}
		// 先输出当前节点名称
		//System.out.println(parent.getName());
		//遍历当前节点属性并输出
		Iterator<Attribute> iterator1 = parent.attributeIterator();
		while (iterator1.hasNext()) {
			Attribute attribute = (Attribute) iterator1.next();
			System.out.println(attribute.getName() + "=" + attribute.getText());
		}
		//获取当前节点Text并输出
		String text = parent.getTextTrim();
		if (text != null && !"".equals(text)) {
			System.out.println(text);
		}
		//递归打印子节点
		Iterator<Element> iterator2 = parent.elementIterator();
		while (iterator2.hasNext()) {
			Element son = (Element) iterator2.next();
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
		if(parent==null) {
			return;
		}
		//先输出当前节点
		String name = parent.getName().trim();
		if (name != null && !"".equals(name)) {
			System.out.println(name);
		}
		//遍历节点中的Node
		for (int i = 0, size = parent.nodeCount(); i < size; i++) {
			//获得当前Node
			Node node = parent.node(i);
			//获得node的类型
			int nodeType = node.getNodeType();
			switch (nodeType) {
			case Node.ELEMENT_NODE:
				list2((Element) node); //如果是Element类型，递归
				break;
			case Node.ATTRIBUTE_NODE://这种方式不能遍历Attribute
				System.out.println(node.getName() + "：" + node.getText());
				break;
			case Node.TEXT_NODE:
				if (node != null && !"".equals(node.getText())) {
					System.out.println(node.getText());
				}
				break;
			default:
				System.out.println("其他类型" + node);
				break;
			}
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
	public void list3(Element root,String elementName) {
		if(root==null) {
			return;
		}
		root.accept(new VisitorSupport() {
			public void visit(Element element) {
				if (elementName.equals(element.getName())) {
					System.out.print(element.getTextTrim() + ":");
				}
			}

			public void visit(Attribute attribute) {
				Element parent = attribute.getParent();
				if (elementName.equals(parent.getName())) {
					System.out.println(attribute.getName() + "=" + attribute.getText());
				}
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
		//创建指定文件的File对象
		File file = new File(fileName);
		//创建SAXReader
		SAXReader saxReader = new SAXReader();
		try {
			//将xml文件读入成document
			document = saxReader.read(file);
			//获得根元素
			root = document.getRootElement();
		} catch (Exception e) {
			System.err.println("根据文件名解析文件失败");
			e.printStackTrace();
		}
	}

	public Element getRoot() {
		return root;
	}

	public void setRoot(Element root) {
		this.root = root;
	}

}
