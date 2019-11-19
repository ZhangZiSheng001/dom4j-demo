package cn.zzs.dom4j;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @ClassName: XMLCreater
 * @Description: 创建XML文件--使用dom4j
 * @author: zzs
 * @date: 2019年8月31日 下午11:38:11
 */
public class XMLCreater {

	/**
	 * 文档模型
	 */
	private Document document;

	/**
	 * 元素的根节点
	 */
	private Element root;

	public XMLCreater(String rootName) {
		super();
		init(rootName);
	}

	// 无参构造私有
	private XMLCreater() {}

	/**
	 * 
	 * @Title: addMember
	 * @Description: 在根节点下创建该类对象的父节点，并在该父节点下插入指定对象的节点
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:23:47
	 * @param obj
	 * @return: void
	 * @throws Exception 
	 */
	public void addMember(Object obj) throws Exception {
		// 获得对象的简单类名
		String name = obj.getClass().getSimpleName().toLowerCase();
		// 获取该类对象的父节点
		Element parent = getElementByName(name);
		// 在父节点下创建指定对象的节点
		Element element = parent.addElement(name);
		// 在当前节点下添加属性
		addAttribute(element, obj);
	}

	/**
	 * 
	 * @Title: create
	 * @Description: 将Document以xml文件形式输出到指定文件
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:25:35
	 * @param fileName
	 * @param useFormat
	 * @return: void
	 * @throws Exception 
	 */
	public void create(String fileName, boolean useFormat) throws Exception {
		File file = null;
		FileWriter out = null;
		XMLWriter writer = null;
		try {
			// 获取文件对象
			file = new File(fileName);
			if(!file.exists()) {
				file.createNewFile();
			}
			// 输出docunment到对应的文件中，out是定义的输出文件,也可以是控制台等
			out = new FileWriter(file);
			// 包装out,获得XMLWriter
			writer = new XMLWriter(out, getFormat(useFormat));
			// 输出xml文件
			writer.write(document);
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			// 释放资源
			if(writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 
	 * @Title: init
	 * @Description: 初始化Document和根节点
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:26:46
	 * @return: void
	 */
	private void init(String rootName) {
		// 创建一个文档模型
		document = DocumentHelper.createDocument();
		// 创建元素的根节点
		root = document.addElement(rootName);
	}

	/**
	 * @Title: addAttribute
	 * @Description: 将指定对象的属性放入element中
	 * @author: zzs
	 * @date: 2019年11月3日 下午9:34:55
	 * @param element
	 * @param obj
	 * @return: void
	 * @throws Exception 对象字段解析异常
	 */
	private void addAttribute(Element element, Object obj) throws Exception {
		Map<String, Object> map = PropertyUtils.describe(obj);
		for(Entry<String, Object> entry : map.entrySet()) {
			String fieldName = entry.getKey();
			if("class".equals(fieldName)) {
				continue;
			}
			element.addAttribute(fieldName, ConvertUtils.convert(entry.getValue()));
		}
	}

	/**
	 * @Title: getElementByName
	 * @Description: 获取指定节点
	 * @author: zzs
	 * @date: 2019年11月3日 下午8:55:51
	 * @param: name
	 * @return: Element
	 */
	private Element getElementByName(String typeName) {
		String name = typeName + "s";
		Element element = null;
		List<Node> list = document.selectNodes("//" + name);
		if(list != null && list.size() != 0) {
			element = (Element)list.get(0);
			return element;
		}
		element = root.addElement(name);
		return element;
	}

	/**
	 * 
	 * @Title: getFormat
	 * @Description: 获得xml文件的格式
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:26:21
	 * @param: useFormat
	 * @return: OutputFormat
	 */
	private OutputFormat getFormat(boolean useFormat) {
		OutputFormat format = null;
		if(useFormat) {
			// 美化格式，并设置编码格式
			format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			return format;
		}
		// 如果不指定美化格式，默认是缩减格式
		format = OutputFormat.createCompactFormat();
		return format;
	}
}
