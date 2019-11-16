package cn.zzs.dom4j;

import org.junit.Test;

import cn.zzs.dom4j.entity.Student;
import cn.zzs.dom4j.entity.Teacher;

/**
 * @ClassName: XMLCreaterTest
 * @Description: 测试使用dom4j来创建XML文件
 * @author: zzs
 * @date: 2019年9月1日 上午12:09:54
 */
public class XMLCreaterTest {

	/**
	 * 测试使用dom4j创建XML文件
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
}
