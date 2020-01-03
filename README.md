# 目录

* [简介](#简介)
* [DOM、SAX、JAXP和DOM4J](#domsaxjaxp和dom4j)
  * [xerces解释器](#xerces解释器)
  * [SAX](#sax)
  * [DOM](#dom)
  * [JAXP](#jaxp)
    * [DOM解析器](#dom解析器)
    * [获取SAX解析器](#获取sax解析器)
  * [DOM4j](#dom4j)
* [项目环境](#项目环境)
  * [工程环境](#工程环境)
  * [创建项目](#创建项目)
  * [引入依赖](#引入依赖)
* [使用例子--生成xml文件](#使用例子--生成xml文件)
  * [需求](#需求)
  * [生成xml文件--使用w3c的DOM接口](#生成xml文件--使用w3c的dom接口)
    * [主要步骤](#主要步骤)
    * [编写测试类](#编写测试类)
    * [测试结果](#测试结果)
  * [生成xml文件--使用dom4j的DOM接口](#生成xml文件--使用dom4j的dom接口)
    * [主要步骤](#主要步骤-1)
    * [编写测试类](#编写测试类-1)
    * [测试结果](#测试结果-1)
* [使用例子--解析xml文件](#使用例子--解析xml文件)
  * [需求](#需求-1)
  * [主要步骤](#主要步骤-2)
  * [测试遍历节点](#测试遍历节点)
  * [测试XPath获取指定节点](#测试xpath获取指定节点)
  * [XPath语法](#xpath语法)
* [源码分析](#源码分析)
  * [dom4j节点的类结构](#dom4j节点的类结构)
  * [SAXReader.read(File file)](#saxreaderreadfile-file)
  * [SAXReader.read(InputSource in)](#saxreaderreadinputsource-in)
  * [SAXContentHandler](#saxcontenthandler)
    * [startDocument()](#startdocument)
    * [startElement(String,String,String,Attributes)](#startelementstringstringstringattributes)
    * [endElement(String, String, String)](#endelementstring-string-string)
    * [endDocument()](#enddocument)

# 简介  
`dom4j`用于创建和解析XML文件，不是纯粹的`DOM`或`SAX`，而是两者的结合和改进，另外，`dom4j`支持`Xpath`来获取节点。目前，由于其出色的性能和易用性，目前`dom4j`已经得到广泛使用，例如`Spring`、`Hibernate`就是使用`dom4j`来解析xml配置。  

注意，`dom4j`使用`Xpath`需要额外引入`jaxen`的包。  

# DOM、SAX、JAXP和DOM4J
其实，JDK已经带有可以解析xml的api，如`DOM`、`SAX`、`JAXP`，但为什么`dom4j`会更受欢迎呢？它们有什么区别呢？在学习`dom4j`之前，需要先理解下`DOM`、`SAX`等概念，因为`dom4j`就是在此基础上改进而来。  

## xerces解释器
先介绍下`xerces`解释器，下面介绍的`SAX`、`DOM`和`JAXP`都只是接口，而`xerces`解释器就是它们的具体实现，在`com.sun.org.apache.xerces.internal`包。`xerces`被称为性能最好的解释器，除了`xerces`外，还有其他的第三方解释器，如`crimson`。 

## SAX
JDK针对解析xml提供的接口，不是具体实现，在`org.xml.sax`包。`SAX`是**基于事件处理**，解析过程中根据当前的XML元素类型，调用用户自己实现的回调方法，如：`startDocument()`;，`startElement()`。下面以例子说明，通过`SAX`解析xml并打印节点名：  

```java
	/*这里解释下四个的接口：
	EntityResolver：需要实现resolveEntity方法。当解析xml需要引入外部数据源时触发，通过这个方法可以重定向到本地数据源或进行其他操作。
	DTDHandler：需要实现notationDecl和unparsedEntityDecl方法。当解析到"NOTATION", "ENTITY"或 "ENTITIES"时触发。
	ContentHandler：最常用的一个接口，需要实现startDocument、endDocument、startElement、endElement等方法。当解析到指定元素类型时触发。
	ErrorHandler：需要实现warning、error或fatalError方法。当解析出现异常时会触发。
	*/
	@Test
	public void test04() throws Exception {
		//DefaultHandler实现了EntityResolver, DTDHandler, ContentHandler, ErrorHandler四个接口		
		DefaultHandler handler = new DefaultHandler() {
			@Override
			//当解析到Element时，触发打印该节点名
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				System.out.println(qName);
			}
		};
		//获取解析器实例
		XMLReader xr = XMLReaderFactory.createXMLReader();
		//设置处理类
		xr.setContentHandler(handler);
		/*
		 * xr.setErrorHandler(handler); 
		 * xr.setDTDHandler(handler); 
		 * xr.setEntityResolver(handler);
		 */
		xr.parse(new InputSource("members.xml"));
	}
```
因为`SAX`是基于事件处理的，不需要等到整个xml文件都解析完才执行我们的操作，所以效率较高。但`SAX`存在一个较大缺点，就是不能随机访问节点，因为`SAX`不会主动地去保存处理过的元素（优点就是内存占用小、效率高），如果想要保存读取的元素，开发人员先构建出一个xml树形结构，再手动往里面放入元素，非常麻烦（其实`dom4j`就是通过`SAX`来构建xml树）。  

## DOM
JDK针对解析xml提供的接口，不是具体实现，在`org.w3c.dom`包。`DOM`采用了解析方式是一次性加载整个XML文档，在内存中形成一个树形的数据结构，开发人员可以随机地操作元素。见以下例子：  

```java
	@SuppressWarnings("restriction")
	@Test
	public void test05() throws Exception {
		//获得DOMParser对象
		com.sun.org.apache.xerces.internal.parsers.DOMParser domParser = new com.sun.org.apache.xerces.internal.parsers.DOMParser();
		//解析文件
		domParser.parse(new InputSource("members.xml"));
		//获得Document对象
		Document document=domParser.getDocument();
		// 遍历节点
		printNodeList(document.getChildNodes());		
	}
```
通过DOM解析，我们可以获取任意节点进行操作。但是，`DOM`有两个缺点：  
1. 由于一次性加载整个XML文件到内存，当处理较大文件时，容易出现内存溢出。  
2. 节点的操作还是比较繁琐。  

以上两点，`dom4j`都进行了相应优化。  

## JAXP
封装了`SAX`、`DOM`两种接口，它并没有为JAVA解析XML提供任何新功能，只是对外提供更解耦、简便操作的API。如下：  

### DOM解析器  
```java
	@Test
	public void test02() throws Exception {
		// 获得DocumentBuilder对象
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 解析xml文件，获得Document对象
		Document document = builder.parse("members.xml");
		// 遍历节点
		printNodeList(document.getChildNodes());
	}
```

### 获取SAX解析器
```java
	@Test
	public void test03() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse("members.xml", new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				System.out.println(qName);
			}
		});
	}
```
其实，`JAXP`并没有很大程度提高DOM和SAX的易用性，更多地体现在获取解析器时实现解耦。完全没有解决`SAX`和`DOM`的缺点。   

## DOM4j
对比过`dom4j`和`JAXP`就会发现，`JAXP`本质上还是将`SAX`和`DOM`当成两套API来看待，而`dom4j`就不是，它将`SAX`和`DOM`结合在一起使用，取长补短，并对原有的api进行了改造，在使用简便性、性能、面向接口编程等方面都要优于JDK自带的`SAX`和`DOM`。  

以下通过使用例子和源码分析将作出说明。  

# 项目环境

## 工程环境
JDK：1.8  

maven：3.6.1  

IDE：sts4    

dom4j：2.1.1  

## 创建项目

项目类型Maven Project，打包方式jar。  

## 引入依赖
注意：`dom4j`使用`XPath`，必须引入`jaxen`的jar包。  

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
<!-- 配置BeanUtils的包，这个我自定义工具类用的，如果只是简单使用dom4j可以不引入 -->
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.3</version>
</dependency>
```

# 使用例子--生成xml文件
本例子将分别使用`dom4j`和JDK的`DOM`接口生成xml文件（使用JDK的`DOM`接口时会使用`JAXP`的API）。  

## 需求
构建xml树，添加节点，并生成xml文件。格式如下：  

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
## 生成xml文件--使用w3c的DOM接口
### 主要步骤

1. 通过`JAXP`的API获得`Document`对象，这个对象可以看成xml的树；  

2. 将对象转化为节点，并添加在`Document`这棵树上；  

3. 通过`Transformer`对象将树输出到文件中。  

### 编写测试类
路径：test目录下的`cn.zzs.dom4j`。  

注意：因为使用的是`w3c`的`DOM`接口，所以节点对象导的是`org.w3c.dom`包，而不是`org.dom4j`包。  

```java
	@Test
	public void test02() throws Exception {
		// 创建工厂对象
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		// 创建DocumentBuilder对象
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		// 创建Document对象
		Document document = documentBuilder.newDocument();

		// 创建根节点
		Element root = document.createElement("members");
		document.appendChild(root);

		// 添加一级节点
		Element studentsElement = (Element)root.appendChild(document.createElement("students"));
		Element teachersElement = (Element)root.appendChild(document.createElement("teachers"));

		// 添加二级节点并设置属性
		Element studentElement1 = (Element)studentsElement.appendChild(document.createElement("student"));
		studentElement1.setAttribute("name", "张三");
		studentElement1.setAttribute("age", "18");
		studentElement1.setAttribute("location", "河南");
		Element studentElement2 = (Element)studentsElement.appendChild(document.createElement("student"));
		studentElement2.setAttribute("name", "李四");
		studentElement2.setAttribute("age", "26");
		studentElement2.setAttribute("location", "新疆");	
		Element studentElement3 = (Element)studentsElement.appendChild(document.createElement("student"));
		studentElement3.setAttribute("name", "王五");
		studentElement3.setAttribute("age", "20");
		studentElement3.setAttribute("location", "北京");		
		Element teacherElement1 = (Element)teachersElement.appendChild(document.createElement("teacher"));
		teacherElement1.setAttribute("name", "zzs");
		teacherElement1.setAttribute("age", "18");
		teacherElement1.setAttribute("location", "河南");	
		Element teacherElement2 = (Element)teachersElement.appendChild(document.createElement("teacher"));
		teacherElement2.setAttribute("name", "zzf");
		teacherElement2.setAttribute("age", "26");
		teacherElement2.setAttribute("location", "新疆");		
		Element teacherElement3 = (Element)teachersElement.appendChild(document.createElement("teacher"));
		teacherElement3.setAttribute("name", "lt");
		teacherElement3.setAttribute("age", "20");
		teacherElement3.setAttribute("location", "北京");	
			
		// 获取文件对象
		File file = new File("members.xml");
		if(!file.exists()) {
			file.createNewFile();
		}
		// 获取Transformer对象
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		// 设置编码、美化格式
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// 创建DOMSource对象
		DOMSource domSource = new DOMSource(document);
		// 将document写出
		transformer.transform(domSource, new StreamResult(new PrintWriter(new FileOutputStream(file))));	
	}	
```

### 测试结果
此时，在项目路径下会生成`members.xml`，文件内容如下，可以看到，使用`w3c`的`DOM`接口输出的内容没有缩进格式。  

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<members>
<students>
<student age="18" location="河南" name="张三"/>
<student age="26" location="新疆" name="李四"/>
<student age="20" location="北京" name="王五"/>
</students>
<teachers>
<teacher age="18" location="河南" name="zzs"/>
<teacher age="26" location="新疆" name="zzf"/>
<teacher age="20" location="北京" name="lt"/>
</teachers>
</members>
```
## 生成xml文件--使用dom4j的DOM接口
### 主要步骤

1. 通过`DocumentHelper`获得`Document`对象，这个对象可以看成xml的树；  

2. 将对象转化为节点，并添加在`Document`这棵树上；  

3. 通过`XMLWriter`对象将树输出到文件中。  

### 编写测试类
路径：test目录下的`cn.zzs.dom4j`。通过对比，可以看出，`dom4j`的API相比JDK的还是要方便很多。  

注意：因为使用的是`dom4j`的`DOM`接口，所以节点对象导的是`org.dom4j`包，而不是`org.w3c.dom`包（`dom4j`一个很大的特点就是改造了`w3c`的`DOM`接口，极大地简化了我们对节点的操作）。  

```java
	@Test
	public void test02() throws Exception {
		// 创建Document对象
		Document document = DocumentHelper.createDocument();

		// 添加根节点
		Element root = document.addElement("members");

		// 添加一级节点
		Element studentsElement = root.addElement("students");
		Element teachersElement = root.addElement("teachers");

		// 添加二级节点并设置属性，dom4j改造了w3c的DOM接口，极大地简化了我们对节点的操作
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
```
### 测试结果
此时，在项目路径下会生成`members.xml`，文件内容如下，可以看出`dom4j`输出文件会进行缩进处理，而JDK的不会：  

```xml
<?xml version="1.0" encoding="UTF-8"?>

<members>
  <students>
    <student name="张三" age="18" location="河南"/>
    <student name="李四" age="26" location="新疆"/>
    <student name="王五" age="20" location="北京"/>
  </students>
  <teachers>
    <teacher name="zzs" age="18" location="河南"/>
    <teacher name="zzf" age="26" location="新疆"/>
    <teacher name="lt" age="20" location="北京"/>
  </teachers>
</members>
```
# 使用例子--解析xml文件
## 需求
1. 解析xml：解析上面生成的xml文件，将学生和老师节点按以下格式遍历打印出来（当然也可以再封装成对象返回给调用者，这里就不扩展了）。  

```
student:name=张三,location=河南,age=18
student:name=李四,location=新疆,age=26
student:name=王五,location=北京,age=20
teacher:name=zzs,location=河南,age=18
teacher:name=zzf,location=新疆,age=26
teacher:name=lt,location=北京,age=20
```

2. `dom4j`结合`XPath`查找指定节点

## 主要步骤
1. 通过`SAXReader`对象读取和解析xml文件，获得`Document`对象，即xml树；  

2. 调用`Node`的方法遍历打印xml树的节点；  

3. 使用`XPath`查询指定节点。  

## 测试遍历节点
考虑篇幅，这里仅给出一种节点遍历方式，项目源码中还给出了其他的几种。  

```java
	/**
	 *  测试解析xml
	 */
	@Test
	public void test03() throws Exception {
		// 创建指定文件的File对象
		File file = new File("members.xml");
		// 创建SAXReader
		SAXReader saxReader = new SAXReader();
		// 将xml文件读入成document
		Document document = saxReader.read(file);
		// 获得根元素
		Element root = document.getRootElement();
		// 递归遍历节点
		list1(root);
	}

	/**
	 * 递归遍历节点
	 */
	private void list1(Element parent) {
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
## 测试XPath获取指定节点

```java
	@Test
	public void test04() throws Exception {
		// 创建指定文件的File对象
		File file = new File("members.xml");
		// 创建SAXReader
		SAXReader saxReader = new SAXReader();
		// 将xml文件读入成document
		Document document = saxReader.read(file);
		// 使用xpath随机获取节点
		List<Node> list = document.selectNodes("//members//students/student");
		// List<Node> list = xmlParser.getDocument().selectSingleNode("students");
		// 遍历节点
		Iterator<Node> iterator = list.iterator();
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			printAttr(element);
		}
	}
```
测试结果如下：  
```java
student:age=18,location=河南,name=张三
student:age=26,location=新疆,name=李四
student:age=20,location=北京,name=王五
```
## XPath语法
利用`XPath`获取指定节点，平时用的比较多，这里列举下基本语法。  

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
本文会先介绍`dom4j`如何将xml元素抽象成具体的对象，再去分析`dom4j`解析xml文件的过程（注意，阅读以下内容前需要了解和使用过JDK自带的`DOM`和`SAX`）。  

## dom4j节点的类结构
先来看下一个完整xml的元素组成，可以看出，一个xml文件包含了`Document`、`Element`、`Comment`、`Attribute`、`DocumentType`、`Text`等等。  

![xml元素组成](https://github.com/ZhangZiSheng001/Dom4j-demo/blob/master/img/xml_structure.png)

`DOM`的思想就是将xml元素解析为具体对象，并构建树形数据结构。基于此，`w3c`提供了xml元素的接口规范，`dom4j`基本借用了这套规范（如下图），只是改造了接口的方法，使得我们操作时更加简便。  

![dom4j的节点接口继承图](https://github.com/ZhangZiSheng001/Dom4j-demo/blob/master/img/dom4j_structure.png)

## SAXReader.read(File file)
通过使用例子可知，我们解析xml文件的入口是`SAXReader`对象的`read`方法，入参可以是文件路径、url、字节流、字符流等，这里以传入文件路径为例。  

注意：考虑篇幅和可读性，以下代码经过删减，仅保留所需部分。  

```java
    public Document read(File file) throws DocumentException {
        //不管是URI，path，character stream还是byte stream，都会包装成InputSource对象
        InputSource source = new InputSource(new FileInputStream(file));
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }
        
        //下面这段代码是为了设置systemId，当传入URI且没有指定字符流和字节流时，可以通过systemId去连接URL并解析
        //如果一开始传入了字符流或字节流，这个systemId就是可选的
        String path = file.getAbsolutePath();
        if (path != null) {
            StringBuffer sb = new StringBuffer("file://");
            if (!path.startsWith(File.separator)) {
                sb.append("/");
            }
            path = path.replace('\\', '/');
            sb.append(path);
            source.setSystemId(sb.toString());
        }

        //这里调用重载方法解析InputSource对象
        return read(source);
    }
```
## SAXReader.read(InputSource in)
看到这个方法的代码时，使用过JDK的`SAX`的朋友应该很熟悉，没错，`dom4j`也是采用事件处理的机制来解析xml。其实，只是这里设置的`SAXContentHandler`已经实现好了相关的方法，这些方法共同完成一件事情：构建xml树。明白这一点，应该就能理解`dom4j`是如何解决`SAX`和`DOM`的缺点了。  

注意：考虑篇幅和可读性，以下代码经过删减，仅保留所需部分。  

```java
    public Document read(InputSource in) throws DocumentException {
        // 这里会调用JAXP接口获取XMLReader实现类对象
        XMLReader reader = getXMLReader();
        reader = installXMLFilter(reader);
        
        // 下面这些操作，是不是和使用JDK的SAX差不多，dom4j也是使用了事件处理机制。

        // EntityResolver：通过实现resolveEntity方法，当解析xml需要引入外部数据源时触发，可以重定向到本地数据源或进行其他操作。
        EntityResolver thatEntityResolver = this.entityResolver;
        if (thatEntityResolver == null) {
            thatEntityResolver = createDefaultEntityResolver(in
                    .getSystemId());
            this.entityResolver = thatEntityResolver;
        }
        reader.setEntityResolver(thatEntityResolver);
        
        // 下面的SAXContentHandler继承了DefaultHandler，即实现了EntityResolver, DTDHandler, ContentHandler, ErrorHandler等接口
        // 其中最重要的是ContentHandler接口，通过实现startDocument、endDocument、startElement、endElement等方法，当dom4j解析xml文件到指定元素类型时，可以触发我们自定义的方法。
        // 当然，dom4j已经实现了ContentHandler的方法。具体实现的方法内容为：在解析xml时构建xml树
        SAXContentHandler contentHandler = createContentHandler(reader);
        contentHandler.setEntityResolver(thatEntityResolver);
        contentHandler.setInputSource(in);
        boolean internal = isIncludeInternalDTDDeclarations();
        boolean external = isIncludeExternalDTDDeclarations();
        contentHandler.setIncludeInternalDTDDeclarations(internal);
        contentHandler.setIncludeExternalDTDDeclarations(external);
        contentHandler.setMergeAdjacentText(isMergeAdjacentText());
        contentHandler.setStripWhitespaceText(isStripWhitespaceText());
        contentHandler.setIgnoreComments(isIgnoreComments());
        reader.setContentHandler(contentHandler);

        configureReader(reader, contentHandler);
        
        // 使用事件处理机制解析xml，处理过程会构建xml树
        reader.parse(in);
        // 返回构建好的xml树
        return contentHandler.getDocument();
    }
```

## SAXContentHandler
通过上面的分析，可知`SAXContentHandler`是`dom4j`构建xml树的关键。这里看下它的几个重要方法和属性。  

### startDocument()
```java
    // xml树
    private Document document;

    // 节点栈，栈顶存放当前解析节点(节点解析结束)、或当前解析节点的父节点（节点解析开始）
    private ElementStack elementStack;

    // 节点处理器，可以看成节点开始解析或结束解析的标志
    private ElementHandler elementHandler;
    
    // 当前解析节点(节点解析结束)、或当前解析节点的父节点（节点解析开始）
    private Element currentElement;
    public void startDocument() throws SAXException {
        document = null;
        currentElement = null;
        
        // 清空节点栈
        elementStack.clear();
        // 初始化节点处理器
        if ((elementHandler != null)
                && (elementHandler instanceof DispatchHandler)) {
            elementStack.setDispatchHandler((DispatchHandler) elementHandler);
        }

        namespaceStack.clear();
        declaredNamespaceIndex = 0;

        if (mergeAdjacentText && (textBuffer == null)) {
            textBuffer = new StringBuffer();
        }

        textInTextBuffer = false;
    }
```
### startElement(String,String,String,Attributes)
```java
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes attributes) throws SAXException {
        if (mergeAdjacentText && textInTextBuffer) {
            completeCurrentTextNode();
        }

        QName qName = namespaceStack.getQName(namespaceURI, localName,
                qualifiedName);
        // 获取当前解析节点的父节点
        Branch branch = currentElement;

        if (branch == null) {
            branch = getDocument();
        }
        // 创建当前解析节点
        Element element = branch.addElement(qName);
        addDeclaredNamespaces(element);

        // 添加节点属性
        addAttributes(element, attributes);
        
        //将当前节点压入节点栈
        elementStack.pushElement(element);
        currentElement = element;
        entity = null; // fixes bug527062

        //标记节点解析开始
        if (elementHandler != null) {
            elementHandler.onStart(elementStack);
        }
    }
```
### endElement(String, String, String)
```java
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (mergeAdjacentText && textInTextBuffer) {
            completeCurrentTextNode();
        }
        // 标记节点解析结束
        if ((elementHandler != null) && (currentElement != null)) {
            elementHandler.onEnd(elementStack);
        }
        // 当前解析节点从节点栈中弹出
        elementStack.popElement();
        // 指定为栈顶节点
        currentElement = elementStack.peekElement();
    }
```
### endDocument()
```java
    public void endDocument() throws SAXException {
        namespaceStack.clear();
        // 清空节点栈
        elementStack.clear();
        currentElement = null;
        textBuffer = null;
    }
```
以上，`dom4j`的源码分析基本已经分析完，其他具体细节后续再做补充。  

# 参考资料

[浅析SAX,DOM,JAXP,JDOM与DOM4J之间的关系](https://blog.csdn.net/xiongqi215/article/details/10125281)

> 相关源码请移步：https://github.com/ZhangZiSheng001/dom4j-demo.git

> 本文为原创文章，转载请附上原文出处链接：https://www.cnblogs.com/ZhangZiSheng001/p/11917301.html
