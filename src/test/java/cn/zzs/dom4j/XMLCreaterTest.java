package cn.zzs.dom4j;

import org.junit.Test;

/**
 * @ClassName: XMLCreaterTest
 * @Description: 测试使用dom4j来创建XML文件
 * @author: zzs
 * @date: 2019年9月1日 上午12:09:54
 */
public class XMLCreaterTest {
	/**
	 * 测试使用dom4j创建XML文件
	 */
	@Test
	public void test01() {
		//创建XMLCreater的对象
		XMLCreater xmlCreater = new XMLCreater("students");
		//添加节点
		xmlCreater.addMember(new Student("张三", 18, "河南"));
		xmlCreater.addMember(new Student("李四", 26, "新疆"));
		//生成xml文件
		xmlCreater.create("members.xml");
	}
}
