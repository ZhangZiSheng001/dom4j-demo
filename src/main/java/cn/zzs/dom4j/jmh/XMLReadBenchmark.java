package cn.zzs.dom4j.jmh;

import cn.zzs.dom4j.Dom4jReader;
import cn.zzs.dom4j.JaxpReader;
import cn.zzs.dom4j.entity.Student;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
@State(Scope.Thread)
public class XMLReadBenchmark {

    @Benchmark
    public List<Student> dom4jRead() throws Exception {
        return Dom4jReader.getStudentFromXml();
    }

    @Benchmark
    public List<Student> dom4jReadByXpath() throws Exception {
        return Dom4jReader.getStudentFromXmlByXpath();
    }

    @Benchmark
    public List<Student> jaxpDomRead() throws Exception {
        return JaxpReader.getStudentFromXmlDom();
    }

    @Benchmark
    public List<Student> jaxpSaxRead() throws Exception {
        return JaxpReader.getStudentFromXmlSax();
    }

    @Benchmark
    public List<Student> jaxpReadByXpth() throws Exception {
        return JaxpReader.getStudentFromXmlByXPath();
    }

    @Benchmark
    public List<Student> jaxpUnmarshallRead() throws Exception {
        return JaxpReader.getStudentFromXmlByUnmarshall();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(XMLReadBenchmark.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .detectJvmArgs()
                .build();
        new Runner(opt).run();
    }
}
