package runtime;

import instruction.Instruction;
import klass.Klass;
import klass.Method;
import runtime.cpu.CPU;
import runtime.memory.Frame;
import runtime.memory.Memory;
import runtime.memory.Thread;

import java.util.Stack;

public class NaiveJVM {

    private CPU cpu;
    private Memory memory;
    private ClassLoader classLoader;

    public NaiveJVM(CPU cpu, Memory memory, ClassLoader classLoader) {
        this.cpu = cpu;
        this.memory = memory;
        this.classLoader = classLoader;
    }

    public void run(String[] args) {
        if (args.length < 1) {
            return;
        }

        // get main class
        String mainClassName = args[0];
        Klass mainClass = classLoader.loadClass(mainClassName);

        // get main method
        Klass.KlassMethodSearchResult klassMethodSearchResult = mainClass.getMainMethod();

        Thread thread = new Thread(classLoader, memory);
        init(thread);

        Frame frame = new Frame(klassMethodSearchResult.getKlass(), klassMethodSearchResult.getMethod(), 0);
        thread.push(frame);
        cpu.run(thread);
    }

    private void init(Thread thread) {
        Klass klass = classLoader.loadClass("java/lang/System");
        Method method = klass.getMethod("initializeSystemClass", "()V");
        Instruction.doInvoke(thread, klass, method, new Object[0]);
        cpu.run(thread);
    }

    public static void main(String[] args) {
        // TODO: support class path
        String[] classpath = new String[]{
                "/Users/binyang/Documents/naive-jvm/src/test/"
        };

        CPU cpu = new CPU();
        Memory memory = new Memory();
        ClassLoader classLoader = new ClassLoader(classpath, memory.getMethodArea());

        NaiveJVM naiveJVM = new NaiveJVM(cpu, memory, classLoader);
        naiveJVM.run(args);
    }
}
