package cn.zzs.dom4j.jmh;

import cn.zzs.dom4j.Dom4jWriter;
import cn.zzs.dom4j.JaxpWriter;
import cn.zzs.dom4j.entity.Student;
import cn.zzs.dom4j.entity.StudentList;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 读xml性能测试
 *
 * @author zzs
 * @version 1.0.0
 * @date 2021-05-03 20:30
 */
@Fork(value = 1, jvmArgsPrepend = "-Xmx128m")
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class XMLWriteBenchmark {

    @State(Scope.Benchmark)
    public static class StudentState {
        StudentList studentList;

        @Setup(Level.Trial)
        public void setup() {
            studentList = new StudentList();
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Student student = new Student();
                student.setName("zzf" + i);
                student.setAge(19);
                student.setLocation("广州第" + i + "大道");
                students.add(student);
            }
            studentList.setStudents(students);
        }

    }

    @Benchmark
    public void dom4jWrite(StudentState studentState) throws Exception {
        Dom4jWriter.write(studentState.studentList.getStudents());
    }

    @Benchmark
    public void jaxpWrite(StudentState studentState) throws Exception {
        JaxpWriter.write(studentState.studentList.getStudents());
    }

    @Benchmark
    public void jaxpWriteByMarshaller(StudentState studentState) throws Exception {
        JaxpWriter.writeByMarshaller(studentState.studentList);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(XMLWriteBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .detectJvmArgs()
                .build();
        new Runner(opt).run();
    }
}
