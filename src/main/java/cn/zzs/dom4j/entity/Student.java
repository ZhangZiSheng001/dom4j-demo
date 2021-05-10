package cn.zzs.dom4j.entity;

import javax.xml.bind.annotation.*;
import java.util.Objects;

/**
 * 学生的实体类
 * @author: zzs
 * @date: 2019年8月31日 下午11:47:11
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Student {

    /**
     * 姓名
     */
    @XmlAttribute(name = "name")
    private String name;

    /**
     * 年龄
     */
	@XmlAttribute(name = "age")
    private Integer age;

    /**
     * 住址
     */
	@XmlAttribute(name = "location")
    private String location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		return Objects.equals(name, student.name) && Objects.equals(age, student.age) && Objects.equals(location, student.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, age, location);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Student{");
		sb.append("name='").append(name).append('\'');
		sb.append(", age=").append(age);
		sb.append(", location='").append(location).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
