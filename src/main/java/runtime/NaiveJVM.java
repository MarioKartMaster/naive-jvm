package runtime;

import clazz.Clazz;
import clazz.Method;
import clazz.attribute.Attribute;
import clazz.attribute.CodeAttribute;
import runtime.cpu.CPU;
import runtime.memory.Frame;
import runtime.memory.Memory;
import runtime.memory.Thread;

import java.io.IOException;
import java.util.Stack;

public class NaiveJVM {

    private CPU cpu;
    private Memory memory;
    private BootstrapClassLoader classLoader;

    public NaiveJVM(CPU cpu, Memory memory, BootstrapClassLoader classLoader) {
        this.cpu = cpu;
        this.memory = memory;
        this.classLoader = classLoader;
    }

    public void run(String[] args) {
        if (args.length < 1) {
            return;
        }

        Clazz mainClass;
        String mainClassName = args[0];
        try {
            mainClass = classLoader.loadClass(mainClassName);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // run main method
        Method mainMethod = mainClass.getMainMethod();

        Thread thread = new Thread(classLoader);
        Stack<Frame> stack = thread.getStack();
        Frame frame = new Frame(mainClass, mainMethod);
        stack.push(frame);
        cpu.run(thread);
    }

    public static void main(String[] args) {
        // TODO: support class path
        String[] classpath = new String[]{
                "/Users/binyang/Documents/naive-jvm/src/test/"
        };

        CPU cpu = new CPU();
        Memory memory = new Memory();
        BootstrapClassLoader classLoader = new BootstrapClassLoader(classpath, memory.getMethodArea());

        NaiveJVM naiveJVM = new NaiveJVM(cpu, memory, classLoader);
        naiveJVM.run(args);
    }
}
