# 简介

什么是dom4j？

简单来说，dom4j 就是用来读写 xml 的。相比 JDK 的 JAXP，dom4j 的 API 更容易使用，所以，目前 dom4j 在国内还是比较受欢迎。

本文主要讲的是如何使用 dom4j 以及分析 dom4j 的源码，除此之外，我希望回答更多的问题，例如，什么是 DOM？什么是 SAX？要不要使用 dom4j？

本文的结构大致如下：

1. 先了解DOM和SAX？
4. 如何使用 dom4j？
5. 源码分析
4. 要不要使用 dom4j？

# 先了解DOM和SAX

为什么提到 DOM 和 SAX 呢？因为这是常识，提到 xml 解析，我们很难绕开它们。

DOM（Document Object Model） 和 SAX（Simple API for XML Parsing） 是处理 xml 节点的两种方式，注意，**它们只是方法论**。

下面说说它们是如何读 xml 的。首先，从抽象层面，整个 xml 可以看成是一棵树，而具体的 xml 节点可以看成是树的根、枝、叶等。

**DOM 是 一边读取 xml 文件，一边在内存中构建 xml 树，整棵树构建完了之后，我可以在树上面找我需要的节点**。

**SAX 是一边读取 xml 文件，一边处理节点，而不会在内存中构建树**。

为了更好理解，我举个例子。假如我要买橘子，DOM 是将所有橘子全部摆在你面前，你可以随便挑，而 SAX 则是一次只掏出一个橘子，然后问你要还是不要，如果你不要，那就把橘子收回，拿出下一个，如果你反悔了，想要上一个橘子，抱歉，那个已经不在了。

当然，这两种方式各有优缺点，没有绝对的好坏。DOM 支持随机访问节点，以及对节点进行增删改，但由于需要在内存中构建 xml 树，当树太大时容易出现内存溢出，而 SAX 不需要构建树，所以性能更高，但它不支持随机访问节点以及增删改。

通常情况下，我们更多的会使用 DOM，因为我们的 xml 并不大，而且经常需要随机访问节点，例如，读取配置文件一般就是用 DOM。在文件太大且不需要随机访问节点时，可以使用 SAX，例如，读取大型 xlsx 时就是用 SAX（没错，xlsx 本质也是 xml）。

本文说到的 **dom4j 就属于 DOM**。

# 如何使用dom4j

## 项目环境

JDK：1.8.0_231

maven：3.6.3

IDE：ideaIC-2021.1.win

dom4j：2.1.3

## maven依赖

项目类型 Maven Project，打包方式 jar。

注意：如果要使用 XPath，必须引入 jaxen 的 jar 包。

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
    <version>2.1.3/version>
</dependency>
<!-- dom4j使用XPath需要的jar包 -->
<dependency>
    <groupId>jaxen</groupId>
    <artifactId>jaxen</artifactId>
    <version>1.1.6</version>
</dependency>
```

# 写xml

## 需求

已有一个学生对象的集合，使用 java 代码把学生对象转换为 xml 节点，做出这么一个 xml 文件。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<students>
    <student name="zzf0" age="19" location="广州第0大道"/>
    <student name="zzf1" age="19" location="广州第1大道"/>
    <student name="zzf2" age="19" location="广州第2大道"/>
    <!--省略-->
    <!-- ······ -->
    <student name="zzf99" age="19" location="广州第99大道"/>
</students>
```
## 使用dom4j写xml

dom4j 添加节点时支持链式编程（JAXP 就不支持），所以写起来比较简洁一些。

```java
    public static void write(List<Student> students) throws Exception {
        // 创建Document对象
        Document document = DocumentHelper.createDocument();
        // 添加根节点
        Element root = document.addElement("students");
        // 添加一级节点并设置属性
        students.stream().forEach(student -> {
            root.addElement("student")
                    .addAttribute("name", student.getName())
                    .addAttribute("age", String.valueOf(student.getAge()))
                    .addAttribute("location", student.getLocation());
        });
        // 创建文件对象
        File file = new File("xml/students.xml");
        if (!file.exists()) {
            file.createNewFile();
        }
        // 创建输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();// 有换行和缩进效果
        // OutputFormat format = OutputFormat.createCompactFormat();// 无换行和缩进效果
        format.setEncoding("UTF-8");// 编码
        // 获得XMLWriter
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        // 输出xml
        writer.write(document);
        // 释放资源(这里会帮我们把FileOutputStream关闭)
        writer.close();
    }
    //zzs001
```

# 读xml

## 需求

使用 java 代码读取上面生成的 xml 文件，将学生节点封装成学生对象。

## 使用dom4j读xml

dom4j 节点的遍历支持`Collection`（JAXP 就不支持），所以可以更简单地遍历。

```java
    public static List<Student> getStudentFromXml() throws Exception {
        // 创建SAXReader
        SAXReader saxReader = SAXReader.createDefault();
        // 将xml解析为树
        Document document = saxReader.read("xml/students.xml");
        // 获得根节点
        Element root = document.getRootElement();
        // 获取所有student节点并映射成学生对象(这里假设我不知道student节点在哪一级，或者哪几级)
        return mapElementsToStudents(root.elements());
    }

    private static List<Student> mapElementsToStudents(List<Element> elements) {
        List<Student> students = new ArrayList<>();
        elements.stream().forEach(element -> {
            // 转换当前节点
            if ("student".equals(element.getName())) {
                Optional.ofNullable(mapElementToStudent(element)).ifPresent(students::add);
            }
            // 递归转换子节点
            students.addAll(mapElementsToStudents(element.elements()));
        });
        return students;
    }

    private static Student mapElementToStudent(Element element) {
        Student student = new Student();
        Optional.ofNullable(element.attributeValue("name")).ifPresent(student::setName);
        Optional.ofNullable(element.attributeValue("age")).map(Integer::valueOf).ifPresent(student::setAge);
        Optional.ofNullable(element.attributeValue("location")).ifPresent(student::setLocation);
        return student;
    }
    //zzs001
```

## 使用XPath获取指定节点

在读取 xml 时，我们很少通过遍历递归来获取我们所需的节点，更多的希望通过路径来直接找到节点，dom4j 通过 XPath 来提供支持（需要额外引入 jaxen 包）。

```java
    public static List<Student> getStudentFromXmlByXpath() throws Exception {
        // 创建SAXReader
        SAXReader saxReader = SAXReader.createDefault();
        // 将xml解析为树
        Document document = saxReader.read("xml/students.xml");
        // 使用xpath随机获取节点(这里假设我不知道student节点在哪一级，或者哪几级)
        List<Node> list = document.selectNodes("//student");
        // 映射成学生对象
        return list.stream()
                .map(node -> mapElementToStudent((Element) node))
                .collect(Collectors.toList());
    }
    //zzs001
```

这里再补充下 XPath 的基本语法。  

| 表达式                             | 结果                                  |
| ---------------------------------- | ------------------------------------- |
| `/students`                        | 选取根节点下的所有students子节点      |
| `//students`                       | 选取根节点下的所有students节点        |
| `//students/student[1]`            | 选取students下第一个student子节点     |
| `//students/student[last()]`       | 选取students下的最后一个student子节点 |
| `//students/student[position()<3]` | 选取students下前两个student子节点     |
| `//student[@age]`                  | 选取所有具有age属性的student节点      |
| `//student[@age='18']`             | 选取所有age属性为18的student节点      |
| `//students/*`                     | 选取students下的所有节点              |
| `//*`                              | 选取文档中所有节点                    |
| `//student[@*]`                    | 选取所有具有属性的节点                |

# 源码分析 

本文会先介绍`dom4j`如何将 xml 元素抽象成具体的对象，再去分析`dom4j`读 xml 文件的过程（写的部分本文不扩展）。

注意，阅读以下内容最好先了解 JAXP SAX。 

## dom4j节点的类结构

先来看下一个完整 xml 的节点组成。可以看出，一个 xml 文件包含了`Document`、`Element`、`Comment`、`Attribute`、`DocumentType`、`Text`等等。 

<img src="https://img2018.cnblogs.com/blog/1731892/201911/1731892-20191123120233242-1363075546.png" alt="xml元素组成" style="zoom:67%;" />

DOM 的思想就是将 xml 节点解析为具体的对象，并构建树形数据结构。基于此，`w3c`提供了 xml 元素的接口规范，`dom4j`基本借用了这套规范（如下图），只是改造了接口的方法，使得我们操作时更加简便。 

<img src="https://img2018.cnblogs.com/blog/1731892/201911/1731892-20191123120357524-2028719224.png" alt="dom4j的节点接口继承图" style="zoom:67%;" />

## 如何读取xml的节点

通过使用例子可知，我们解析xml文件的入口是`SAXReader`对象的`read`方法，入参可以是文件路径、url、字节流、字符流等，这些入参都会被包装成`InputSource`对象，最终调用`org.dom4j.io.SAXReader#read(org.xml.sax.InputSource)`方法。 

看到这个方法的代码时，使用过 JAXP SAX 的朋友应该很熟悉。没错，dom4j 直接调用了 JAXP SAX 的 API 来读取节点，一边读节点，一边构建 xml 树。这么看来，**dom4j 认同 JAXP SAX，但不认同 JAXP DOM**。

注意：考虑篇幅和可读性，以下代码经过删减，仅保留所需部分。 

```java
    public Document read(InputSource in) throws DocumentException {
        // 这里会调用JAXP接口获取XMLReader实现类对象
        XMLReader reader = getXMLReader();
        reader = installXMLFilter(reader);

        // EntityResolver：通过实现resolveEntity方法，当解析xml需要引入外部数据源时触发，可以重定向到本地数据源或进行其他操作。
        EntityResolver thatEntityResolver = this.entityResolver;
        if (thatEntityResolver == null) {
            thatEntityResolver = createDefaultEntityResolver(in
                    .getSystemId());
            this.entityResolver = thatEntityResolver;
        }
        reader.setEntityResolver(thatEntityResolver);
        
        // 下面的SAXContentHandler继承了DefaultHandler，即实现了EntityResolver, DTDHandler, ContentHandler, ErrorHandler等接口
        // 其中最重要的是ContentHandler接口，SAXContentHandler通过实现ContentHandler接口的startDocument、endDocument、startElement、endElement等方法来构建 xml 树。
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
        
        // 一边读取节点，一边构建树
        reader.parse(in);
        return contentHandler.getDocument();
    }
```

## 构建xml树

通过上面的代码，可以知道，构建 xml 树的逻辑在`SAXContentHandler`里。这里看下它的几个重要方法和属性。 

### 树的开始和结束

```java
    // xml树
    private Document document;

    // 节点栈，栈顶存放当前解析节点(节点解析结束)、或当前解析节点的父节点（节点解析开始）
    private ElementStack elementStack;

    // 节点处理器，可以看成节点开始解析或结束解析的标志
    private ElementHandler elementHandler;
    
    // 当前解析节点(节点解析结束)、或当前解析节点的父节点（节点解析开始），一般等于elementStack的栈顶元素
    private Element currentElement;

    public void startDocument() throws SAXException {
        // 为构建xml树进行的初始化工作
        document = null;
        currentElement = null;
        elementStack.clear();
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
    public void endDocument() throws SAXException {
        // 构建完xml树后释放某些资源
        namespaceStack.clear();
        elementStack.clear();
        currentElement = null;
        textBuffer = null;
    }
    //zzs001
```
### 节点的开始和结束

可以看出，这个 xml 树是一棵多叉树。

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
        // 添加当前节点到currentElement作为子节点
        Element element = branch.addElement(qName);
        addDeclaredNamespaces(element);

        // 添加当前节点属性
        addAttributes(element, attributes);
        
        // 将当前节点压入节点栈
        elementStack.pushElement(element);
        currentElement = element;
        entity = null; // fixes bug527062

        //标记节点解析开始
        if (elementHandler != null) {
            elementHandler.onStart(elementStack);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (mergeAdjacentText && textInTextBuffer) {
            completeCurrentTextNode();
        }
        // 标记节点解析结束
        if ((elementHandler != null) && (currentElement != null)) {
            elementHandler.onEnd(elementStack);
        }
        // 当前节点从节点栈中弹出
        elementStack.popElement();
        // 指定为栈顶节点，它是下一个要解析节点的父节点
        currentElement = elementStack.peekElement();
    }
```

以上，dom4j 的源码基本已经分析完，其他具体细节后续再做补充。 

# 要不要使用dom4j

为什么要讨论这样的问题呢？首先，你的项目选择使用哪种技术，不是一拍脑袋就能决定的，需要谨慎评估，不能大家都说好，你就直接拿去用。其次，你觉得某个技术已经够好了，可能是因为你不知道还有更好的选择。

下面从易用性、性能、代码解耦等方面对比 dom4j 和  JAXP（实现用的是 JDK 默认的实现）。

## 易用性

在本项目中，也有使用 JAXP 直接操作节点来实现上述读写例子的代码（文末有源码链接），通过对比可以发现，dom4j 的 API 确实更加简洁，这一点不得不承认。

注意，这里对比的是直接操作节点来实现读写，而不是使用 JAXB（JAXB 可以用注解来自动完成 xml 文件和 java 对象的映射），如果用 JAXB 来处理本文中的例子，会更简单一些。

## 性能

再说说性能，我用 jmh 做了个测试（JDK 版本 1.8.0_231，dom4j 版本 2.1.3），**在读方面，JAXP DOM 稍快于 dom4j，在写方面，dom4j 更快**。

```powershell
# 写
Benchmark                                                      Mode  Cnt       Score      Error   Units
XMLWriteBenchmark.dom4jWrite                                   avgt    5     162.411 ±   22.892   us/op
XMLWriteBenchmark.jaxpWrite                                    avgt    5     277.598 ±   51.781   us/op
XMLWriteBenchmark.jaxpWriteByMarshaller                        avgt    5     349.465 ±   67.729   us/op
# 读
Benchmark                                                      Mode  Cnt       Score      Error   Units
XMLReadBenchmark.jaxpSaxRead                                   avgt    5     157.919 ±    0.685   us/op
XMLReadBenchmark.dom4jRead                                     avgt    5     223.989 ±    1.399   us/op
XMLReadBenchmark.jaxpDomRead                                   avgt    5     205.436 ±    2.287   us/op
XMLReadBenchmark.dom4jReadByXpath                              avgt    5     341.399 ±    1.666   us/op
XMLReadBenchmark.jaxpReadByXpth                                avgt    5     309.582 ±    2.056   us/op
XMLReadBenchmark.jaxpUnmarshallRead                            avgt    5     466.121 ±    5.761   us/op
```

## 代码耦合

项目中我们经常遇到需要更换类库的问题，为了更少改动代码，我们的代码中往往只会使用到标准接口，而不会使用到具体实现，例如，使用 JDBC 来访问数据库。

JAXP 就属于标准接口，并且 JDK 自带了一套实现。在项目中使用 dom4j，就好比在项目中直接调用 mysql-connector 的 API，这样做可能会简单一些，但是，哪天我需要更换 xml 类库时，需要修改大量的代码。

## 我的建议

经过以上对比，我的建议就是不要使用 dom4j，而是直接使用 JAXP。你会发现，更多的第三方类库使用的是 JAXP，例如，Spring、Mybatis、POI 等等。

当然，如果你觉得以后不会更改 xml 类库，可以考虑使用 dom4j。

以上，基本讲完 dom4j，不足的地方欢迎指正。

最后，感谢阅读。

# 参考资料 

[dom4j官方文档](https://dom4j.github.io/)

> 2021-09-24 更改

> 相关源码请移步：https://github.com/ZhangZiSheng001/dom4j-demo

> 本文为原创文章，转载请附上原文出处链接：https://www.cnblogs.com/ZhangZiSheng001/p/11917301.html