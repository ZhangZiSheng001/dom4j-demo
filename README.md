# 目录

* [简介](#简介)
* [使用例子](#使用例子)
  * [需求](#需求)
  * [工程环境](#工程环境)
  * [主要步骤](#主要步骤)
    * [生成xml文件](#生成xml文件)
    * [解析xml文件](#解析xml文件)
  * [创建项目](#创建项目)
  * [引入依赖](#引入依赖)
  * [生成xml文件](#生成xml文件-1)
    * [编写XMLCreater](#编写xmlcreater)
    * [编写XMLCreaterTest](#编写xmlcreatertest)
    * [测试结果](#测试结果)
  * [解析xml文件](#解析xml文件-1)
    * [编写XMLParser](#编写xmlparser)
    * [测试遍历节点](#测试遍历节点)
    * [测试XPath获取指定节点](#测试xpath获取指定节点)
  * [XPath语法](#xpath语法)
* [源码分析](#源码分析)
  * [dom4j节点的类结构](#dom4j节点的类结构)



# 简介  
dom4j用于创建和解析XML文件，本质上是将xml的节点看成一棵多叉树。  

针对xml元素的结构，dom4j设计非常巧妙，如下：  

![dom4j的节点结构图](https://github.com/ZhangZiSheng001/Dom4j-demo/blob/master/img/dom4j_structure.png)

另外，dom4j支持Xpath来获取节点。注意，使用Xpath需要引入jaxen的包。  


# 使用例子
## 需求
1. 生成xml：添加学生或老师对象，可生成对应的xml节点，最终生成xml文件。格式如下：  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<members>
  <students>
    <student name="张三" location="河南" age="18"/>
    <student name="李四" location="新疆" age="26"/>
    <student name="王五" location="北京" age="20"/>
  </students>
  <teachers>
    <teacher name="zzs" location="河南" age="18"/>
    <teacher name="zzf" location="新疆" age="26"/>
    <teacher name="lt" location="北京" age="20"/>
  </teachers>
</members>
```
2. 解析xml：解析上面生成的xml文件，将学生和老师节点按以下格式打印出来（当然也可以再封装成对象返回给调用者，这里就不扩展了）。  

```
student:name=张三,location=河南,age=18
student:name=李四,location=新疆,age=26
student:name=王五,location=北京,age=20
teacher:name=zzs,location=河南,age=18
teacher:name=zzf,location=新疆,age=26
teacher:name=lt,location=北京,age=20
```

3. dom4j结合XPath查找指定节点

## 工程环境
JDK：1.8  

maven：3.6.1  

IDE：sts4    

dom4j：2.1.1  

## 主要步骤
### 生成xml文件

1. 通过`DocumentHelper`获得`Document`对象，这个对象可以看成xml的树；  

2. 将对象转化为节点，并添加在`Document`这棵树上；  

3. 通过`XMLWriter`对象将树输出到文件中。  

### 解析xml文件

1. 通过`SAXReader`对象读取和解析xml文件，获得`Document`对象，即xml树；  

2. 调用`Node`的方法遍历打印xml树的节点；  

3. 使用`XPath`查询指定节点。  

## 创建项目

项目类型Maven Project，打包方式jar。  

## 引入依赖
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
<!-- 配置BeanUtils的包 -->
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.3</version>
</dependency>
```

## 生成xml文件
### 编写XMLCreater
这里我编写了一个xml生成器，通过构造指定根节点名并初始化Document树，接着就可以调用`addMember`添加节点，最后调用`create`并指定文件路径就能生成所需的xml文件了。  

路径：`cn.zzs.dom4j`  

```java
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
	//无参构造私有
	private XMLCreater() {
	}
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
	 * @throws Exception 
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

```
### 编写XMLCreaterTest
路径：test目录下的`cn.zzs.dom4j`  

```java
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
```

### 测试结果
此时，在项目路径下会生成members.xml，文件内容如下：  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<members>
  <students>
    <student name="张三" location="河南" age="18"/>
    <student name="李四" location="新疆" age="26"/>
    <student name="王五" location="北京" age="20"/>
  </students>
  <teachers>
    <teacher name="zzs" location="河南" age="18"/>
    <teacher name="zzf" location="新疆" age="26"/>
    <teacher name="lt" location="北京" age="20"/>
  </teachers>
</members>
```
## 解析xml文件
### 编写XMLParser
这里我编写了一个解析器，提供了三种方式用来遍历xml节点（这里仅列出一种，具体见源码），另外还提供了通过XPath获得指定节点的方法。  

路径：`cn.zzs.dom4j`

```java
public class XMLParser {
	// 文档模型
	private Document document = null;

	// 元素的根节点<root>
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
```
### 测试遍历节点

```java
	@Test
	public void test01() {
		XMLParser xmlParser = new XMLParser("members.xml");
		Element root = xmlParser.getRoot();
		System.out.println("-------第一种遍历方式：Iterator+递归--------");
		xmlParser.list1(root);
		//System.out.println("-------第二种遍历方式：node(i)+递归--------");
		//xmlParser.list2(root);
		//System.out.println("-------第三种遍历方式：VisitorSupport--------");
		//xmlParser.list3(root);
	}
```
测试结果如下：  

```
-------第一种遍历方式：Iterator+递归--------
student:name=张三,location=河南,age=18
student:name=李四,location=新疆,age=26
student:name=王五,location=北京,age=20
teacher:name=zzs,location=河南,age=18
teacher:name=zzf,location=新疆,age=26
teacher:name=lt,location=北京,age=20
```
### 测试XPath获取指定节点

```java
	@Test
	public void test02() {
		XMLParser xmlParser = new XMLParser("members.xml");
		List<Node> list = xmlParser.getDocument().selectNodes("//students/student");
		//List<Node> list = xmlParser.getDocument().selectSingleNode("students");
		// 遍历节点
		Iterator<Node> iterator = list.iterator();
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			System.out.println(element);
			// xmlParser.printAttr(element);
		}
	}
```
测试结果如下：  
```java
org.dom4j.tree.DefaultElement@1c53fd30 [Element: <student attributes: [org.dom4j.tree.DefaultAttribute@61443d8f [Attribute: name name value "张三"], org.dom4j.tree.DefaultAttribute@445b84c0 [Attribute: name location value "河南"], org.dom4j.tree.DefaultAttribute@61a52fbd [Attribute: name age value "18"]]/>]
org.dom4j.tree.DefaultElement@75412c2f [Element: <student attributes: [org.dom4j.tree.DefaultAttribute@233c0b17 [Attribute: name name value "李四"], org.dom4j.tree.DefaultAttribute@63d4e2ba [Attribute: name location value "新疆"], org.dom4j.tree.DefaultAttribute@7bb11784 [Attribute: name age value "26"]]/>]
org.dom4j.tree.DefaultElement@13b6d03 [Element: <student attributes: [org.dom4j.tree.DefaultAttribute@33a10788 [Attribute: name name value "王五"], org.dom4j.tree.DefaultAttribute@7006c658 [Attribute: name location value "北京"], org.dom4j.tree.DefaultAttribute@34033bd0 [Attribute: name age value "20"]]/>]

```
## XPath语法
利用XPath获取指定节点，平时用的比较多，这里列举下基本语法。  

表达式|结果
-|-
/members|选取根节点下的所有members子节点
//members|选取根节点下的所有members节点
//students/student[1]|选取students下第一个student子节点
//students/student[last()]|选取students下的最后一个student子节点
//students/student[position()<3]|选取students下前两个student子节点
//student[@age]|选取所有具有age属性的student节点
//student[@age='18']|选取所有age属性为18的student节点
//students/*|选取students下的所有节点
//*|选取文档中所有节点
//student[@*]|选取所有具有属性的节点
//members/students\\|//members/teachers|选取members下的students子节点和teachers子节点

# 源码分析
dom4j本质上是建立在xml规范上的IO操作。大概看了下解析的过程，还是比较复杂，所以，这里暂时不对代码进行解读，重点关注下dom4j的节点类结构。  

## dom4j节点的类结构
先来看下xml文件的元素组成，可以看出，一个xml文件包含了`Document`、`Element`、`Comment`、`Attribute`、`DocumentType`、`Text`等等。  

![xml元素组成](https://github.com/ZhangZiSheng001/Dom4j-demo/blob/master/img/xml_structure.png)

如果想要解析xml，就必须将xml的元素抽象成对象。下面看看dom4j的做法：  

![dom4j的节点接口继承图](https://github.com/ZhangZiSheng001/Dom4j-demo/blob/master/img/dom4j_structure.png)


> 学习使我快乐！！
