package cn.zzs.dom4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @ClassName: XMLCreater
 * @Description: 用于创建XML文件
 * @author: zzs
 * @date: 2019年8月31日 下午11:38:11
 */
public class XMLCreater {
	//文档模型
	private Document document = null;
	//元素的根节点<root>
	private Element root = null;
	
	public XMLCreater(String rootName){
		super();
		init(rootName);
	}
	
	/**
	 * 
	 * @Title: addMember
	 * @Description: 在根节点下添加指定的Student节点
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:23:47
	 * @param student
	 * @return: void
	 */
	public void addMember(Student student) {
		//创建节点、属性和文本等 		
		root
			.addElement("student")
			.addAttribute("age",student.getAge().toString())
			.addAttribute("location", student.getLocation())
			.addText(student.getName());
	}
	
	/**
	 * 
	 * @Title: create
	 * @Description: 将Document以xml文件形式输出到指定文件
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:25:35
	 * @param fileName
	 * @return: void
	 */
	public void create(String fileName){
		File file = null;
		FileWriter out = null;
		XMLWriter writer = null;
		//获得xml的路径
		try {
			//获取文件对象
			file = new File(fileName);
			if(!file.exists()) {
				file.createNewFile();
			}
			//输出docunment到对应的文件中，out是定义的输出文件,也可以是控制台等
			out = new FileWriter(file);
			//包装out,获得XMLWriter
			writer = new XMLWriter(out, createFormat());
			writer.write(document);
		} catch (Exception e) {
			System.err.println("输出XML文件失败");
			e.printStackTrace();
		} finally{
			//释放资源
			try {
				writer.close();
			} catch (IOException e) {
				System.err.println("XMLWriter释放资源失败");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @Title: createFormat
	 * @Description: 设置xml文件的格式
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:26:21
	 * @return: OutputFormat
	 */
	private OutputFormat createFormat(){
		//美化格式，并设置编码格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		//如果不指定美化格式，默认是缩减格式
		//OutputFormat format = OutputFormat.createCompactFormat();
		return format;
	}
	
	/**
	 * 
	 * @Title: init
	 * @Description: 初始化Document和根节点
	 * @author: zzs
	 * @date: 2019年9月1日 上午12:26:46
	 * @param rootName
	 * @return: void
	 */
	private void init(String rootName) {
		//创建一个文档模型
		document = DocumentHelper.createDocument();
		//创建元素的根节点<root>
		root = document.addElement(rootName);
	}
}
