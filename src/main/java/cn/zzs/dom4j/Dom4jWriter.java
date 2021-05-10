package cn.zzs.dom4j;

import cn.zzs.dom4j.entity.Student;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

/**
 * dom4j写xml工具类
 *
 * @author zzs
 * @version 1.0.0
 * @date 2021/5/3
 */
public class Dom4jWriter {

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
}
