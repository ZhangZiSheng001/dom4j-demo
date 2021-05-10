package cn.zzs.dom4j;

import org.junit.Test;

/**
 * 测试读xml
 *
 * @author zzs
 * @version 1.0.0
 * @date 2021/5/3
 */
public class XMLReaderTest {

    @Test
    public void testDom4jRead() throws Exception {
        Dom4jReader.getStudentFromXml().stream().forEach(System.err::println);
    }

    @Test
    public void testDom4jReadByXpath() throws Exception {
        Dom4jReader.getStudentFromXmlByXpath().stream().forEach(System.err::println);
    }

    @Test
    public void testJaxpDomRead() throws Exception {
        JaxpReader.getStudentFromXmlDom().stream().forEach(System.err::println);
    }

    @Test
    public void testJaxpSaxRead() throws Exception {
        JaxpReader.getStudentFromXmlSax().stream().forEach(System.err::println);
    }

    @Test
    public void testJaxpReadByXpath() throws Exception {
        JaxpReader.getStudentFromXmlByXPath().stream().forEach(System.err::println);
    }

    @Test
    public void testJaxpReadByUnmarshall() throws Exception {
        JaxpReader.getStudentFromXmlByUnmarshall().stream().forEach(System.err::println);
    }

}
