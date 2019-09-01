
# dom4j

## 简介  
Dom4j用于创建和解析XML文件，采用了Java集合框架并完全支持`DOM`、`SAX`和`JAXP`。dom4j对xml文件的解析本质上是将xml内容转化为一棵多叉树。  
节点的获取和操作都是从`Document`对象开始，里面包含了根节点，即指向了这棵树。一般利用`SAXReader`的`read`方法解析XML文件获得`Document`对象。利用`Document`这个对象就可以获得指定元素的属性和值了。另外，通过Xpath可以高效地获得指定节点。  
注意，使用Xpath需要引入jaxen的包。  
这里再说下dom4j比较重要的节点结构，从图中可看出，这是非常巧妙的设计：
![dom4j的节点结构图](https://github.com/ZhangZiSheng001/Dom4j-demo/img/dom4j-node.cld.jpg)

## 使用例子
### 需求
1. 使用dom4j创建xml
2. 使用dom4j解析xml

### 工程环境
JDK：1.8.0_201  
maven：3.6.1  
IDE：Spring Tool Suites4 for Eclipse  

### 主要步骤
#### 创建xml文件
1. 通过`DocumentHelper`获得`Document`对象，这个对象可以看成xml的树；
2. 在`Document`这棵树上添加节点；
3. 通过`XMLWriter`对象将树输出到文件中，这个对象负责将xml树按xml语法转换为字符流并输出；

### 解析xml文件
1. 通过`SAXReader`对象读取和解析xml文件，获得`Document`对象；
2. 利用Node节点的`*Iterator()`，`node(index)`,`accept(visitor)`,`selectNodes(xpathExpression)`等方法查找指定节点。推荐使用XPath的方式。

### 创建项目
项目类型Maven Project，打包方式jar

### 引入依赖
注意：dom4j使用XPath，必须引入jaxen的jar包。
```xml
<!-- junit -->
<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.12</version>
	<scope>test</scope>
</dependency>
<!-- dom4j的jar包 -->
<dependency>
	<groupId>org.dom4j</groupId>
	<artifactId>dom4j</artifactId>
	<version>2.1.1</version>
</dependency>
<!-- dom4j使用XPath需要的jar包 -->
<dependency>
	<groupId>jaxen</groupId>
	<artifactId>jaxen</artifactId>
	<version>1.1.6</version>
</dependency>
```

## 编写XMLCreater
这里我创建了一个类，可以自由地增加Student节点，并将xml文件生成到指定路径。
路径：`cn.zzs.dom4j`
```java
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
			//输出xml文件
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
```
### 编写XMLCreaterTest
路径：test目录下的cn.zzs.dom4j
```java
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
```
### 测试XMLCreaterTest
路径：test目录下的`cn.zzs.dom4j`
运行测试方法，可以看到在项目路径下生成members.xml的文件，内容如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>

<students>
  <student age="18" location="河南">张三</student>
  <student age="26" location="新疆">李四</student>
</students>
```
### 编写XMLParser
现在是使用dom4j来解析生成的这个xml。
这里先定义一个构造方法。
路径：`cn.zzs.dom4j`
```java
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

}
```
### 编写遍历xml节点的方法
在XmlParser的类中，定义以下四个遍历节点的方法。

#### Iterator+递归
```java
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
	//获取当前节点Text并输出
	String text = parent.getTextTrim();
	if (text != null && !"".equals(text)) {
		System.out.println(text);
	}
	//遍历当前节点属性并输出
	Iterator<Attribute> iterator1 = parent.attributeIterator();
	while (iterator1.hasNext()) {
		Attribute attribute = iterator1.next();
		System.out.println(attribute.getName() + "=" + attribute.getText());
	}
	//递归打印子节点
	Iterator<Element> iterator2 = parent.elementIterator();
	while (iterator2.hasNext()) {
		Element son = (Element) iterator2.next();
		list1(son);
	}
}
```
#### node(i)+递归
```java
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
	//遍历节点中的Node
	for (int i = 0, size = parent.nodeCount(); i < size; i++) {
		//获得当前Node
		Node node = parent.node(i);
		//获得node的类型
		int nodeType = node.getNodeType();
		switch (nodeType) {
		//如果是Text类型
		case Node.TEXT_NODE:
			if (node != null && !"".equals(node.getText().trim())) {
				System.out.println(node.getText());
			}
			break;
		//如果是Element类型
		case Node.ELEMENT_NODE:
			//遍历当前节点属性并输出
			Iterator<Attribute> iterator1 = ((Element)node).attributeIterator();
			while (iterator1.hasNext()) {
				Attribute attribute = iterator1.next();
				System.out.println(attribute.getName() + "=" + attribute.getText());
			}
			//递归
			list2((Element) node); 
			break;
		//case Node.ATTRIBUTE_NODE://这种方式不能遍历Attribute
			//System.out.println(node.getName() + "：" + node.getText());
			//break;
		default:
			System.out.println("其他类型" + node);
			break;
		}
	}
}
```
#### VisitorSupport
```java
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
	if(root == null) {
		return;
	}
	root.accept(new VisitorSupport() {
		public void visit(Element element) {
			if (elementName.equals(element.getName())) {
				System.out.println(element.getTextTrim() + ":");
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
```
#### 使用XPath方式来指定节点
```java
	/**
	 * 
	 * @Title: list4
	 * @Description: 第四种遍历节点的方法：使用XPath方式来指定节点
	 * @author: zzs
	 * @date: 2019年9月1日 上午9:10:01
	 * @return: void
	 */
	public void list4(String elementName) {
		if(elementName == null) {
			return;
		}
		//获得根节点下的所有符合指定节点名的节点
		List<Node> list = document.selectNodes("//"+elementName.trim());
		//遍历节点
		Iterator<Node> iterator = list.iterator();
	    while (iterator.hasNext()) {
	    	Node node = (Node)iterator.next();
	    	//打印Text
	    	String text = node.getText().trim();
	    	if(!"".equals(text)) {
	    		System.out.println(text+":");
	    	}
	    	//打印Attribute
	    	if(node.getNodeType() == Node.ELEMENT_NODE) {
	    		Iterator<Attribute> iterator1 = ((Element)node).attributeIterator();
	    		while (iterator1.hasNext()) {
	    			Attribute attribute = iterator1.next();
	    			System.out.println(attribute.getName() + "=" + attribute.getText());
	    		}
	    	}
	    }	 
	}
```

### 编写XMLParserTest
路径：test目录下的`cn.zzs.dom4j`
```java
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
```

### 测试XmlParser
	-------第一种遍历方式：Iterator+递归--------
	张三
	age=18
	location=河南
	李四
	age=26
	location=新疆
	-------第二种遍历方式：node(i)+递归--------
	age=18
	location=河南
	张三
	age=26
	location=新疆
	李四
	-------第三种遍历方式：VisitorSupport--------
	张三:
	age=18
	location=河南
	李四:
	age=26
	location=新疆
	-------第四种遍历方式：XPath方式查找节点--------
	张三:
	age=18
	location=河南
	李四:
	age=26
	location=新疆

> 学习使我快乐！！
