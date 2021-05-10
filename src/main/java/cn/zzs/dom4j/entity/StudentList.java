package cn.zzs.dom4j.entity;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * xml根节点
 * @author zzs
 * @version 1.0.0
 * @date 2021-05-04 15:04
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "students")
public class StudentList {
    @XmlElement(name = "student")
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
