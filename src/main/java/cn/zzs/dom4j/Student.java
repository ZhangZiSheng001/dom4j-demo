package cn.zzs.dom4j;
/**
 * @ClassName: Student
 * @Description: 学生的实体类
 * @author: zzs
 * @date: 2019年8月31日 下午11:47:11
 */
public class Student{

	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 年龄
	 */
	private Integer age;
	/**
	 * 住址
	 */
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
	public String toString() {
		return "Student [name=" + name + ", age=" + age + ", location=" + location + "]";
	}
	public Student(String name, Integer age, String location) {
		super();
		this.name = name;
		this.age = age;
		this.location = location;
	}
	public Student() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}