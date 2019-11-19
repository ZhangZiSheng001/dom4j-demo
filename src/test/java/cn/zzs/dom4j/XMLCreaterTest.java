package cn.zzs.dom4j;

import java.io.File;
import java.io.FileWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import cn.zzs.dom4j.entity.Student;
import cn.zzs.dom4j.entity.Teacher;

/**
 * @ClassName: XMLCreaterTest
 * @Description: 测试创建XML文件--使用dom4j原生DOM接口
 * @author: zzs
 * @date: 2019年9月1日 上午12:09:54
 */
public class XMLCreaterTest {

	/**
	 * 测试自定义工具类创建XML文件
	 * 
	 */
	@Test
	public void test01() throws Exception {
		// 创建XMLCreater的对象，并指定根节点名
		XMLCreater xmlCreater = new XMLCreater("members");
		// 添加节点
		xmlCreater.addMember(new Student("张三", 18, "河南"));
		xmlCreater.addMember(new Student("李四", 26, "新疆"));
		xmlCreater.addMember(new Student("王五", 20, "北京"));
		xmlCreater.addMember(new Teacher("zzs", 18, "河南"));
		xmlCreater.addMember(new Teacher("zzf", 26, "新疆"));
		xmlCreater.addMember(new Teacher("lt", 20, "北京"));
		// 指定路径和格式，生成xml文件
		xmlCreater.create("members.xml", true);
	}

	/**
	 * 测试创建XML文件
	 * 
	 */
	@Test
	public void test02() throws Exception {
		// 创建Document对象
		Document document = DocumentHelper.createDocument();
		// 添加根节点
		Element root = document.addElement("members");
		// 添加一级节点
		Element studentsElement = root.addElement("students");
		Element teachersElement = root.addElement("teachers");
		// 添加二级节点并设置属性
		studentsElement.addElement("student").addAttribute("name", "张三").addAttribute("age", "18").addAttribute("location", "河南");
		studentsElement.addElement("student").addAttribute("name", "李四").addAttribute("age", "26").addAttribute("location", "新疆");
		studentsElement.addElement("student").addAttribute("name", "王五").addAttribute("age", "20").addAttribute("location", "北京");
		teachersElement.addElement("teacher").addAttribute("name", "zzs").addAttribute("age", "18").addAttribute("location", "河南");
		teachersElement.addElement("teacher").addAttribute("name", "zzf").addAttribute("age", "26").addAttribute("location", "新疆");
		teachersElement.addElement("teacher").addAttribute("name", "lt").addAttribute("age", "20").addAttribute("location", "北京");
		// 获取文件对象
		File file = new File("members.xml");
		if(!file.exists()) {
			file.createNewFile();
		}
		// 创建输出格式，不设置的话不会有缩进效果
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		// 获得XMLWriter
		XMLWriter writer = new XMLWriter(new FileWriter(file), format);
		// 打印Document
		writer.write(document);
		// 释放资源
		writer.close();
	}
}
