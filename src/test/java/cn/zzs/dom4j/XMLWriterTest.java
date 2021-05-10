package cn.zzs.dom4j;

import cn.zzs.dom4j.entity.Student;
import cn.zzs.dom4j.entity.StudentList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试写xml
 *
 * @author: zzs
 * @date: 2019年9月1日 上午12:09:54
 */
public class XMLWriterTest {

    private List<Student> students;

    @Before
    public void setup() {
        students = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Student student = new Student();
            student.setName("zzf" + i);
            student.setAge(19);
            student.setLocation("广州第" + i + "大道");
            students.add(student);
        }
    }

    @Test
    public void testDom4jWrite() throws Exception {
        Dom4jWriter.write(students);
    }

    @Test
    public void testJaxpWrite() throws Exception {
        JaxpWriter.write(students);
    }

    @Test
    public void testJaxpWriteByMarshaller() throws Exception {
        StudentList studentList = new StudentList();
        studentList.setStudents(students);
        JaxpWriter.writeByMarshaller(studentList);
    }

}
