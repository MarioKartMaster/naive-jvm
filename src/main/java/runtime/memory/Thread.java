package runtime.memory;

import klass.Klass;
import klass.Method;
import klass.constant.*;
import lombok.Getter;
import runtime.ClassLoader;

import java.util.Stack;

@Getter
public class Thread {
    private int pc;
    private Memory memory;
    private Stack<Frame> stack = new Stack();
    private ClassLoader classLoader;

    private Frame currentFrame;
    private Klass currentKlass;
    private Method currentMethod;

    public Thread(ClassLoader classLoader, Memory memory) {
        this.classLoader = classLoader;
        this.memory = memory;
    }

    public Frame pop() {
        Frame frame = stack.pop();
        updateCurrent();
        pc = frame.getLastPc();
        return frame;
    }

    public void push(Frame frame) {
        stack.push(frame);
        updateCurrent();
    }

    private void updateCurrent() {
        if (stack.isEmpty()) {
            currentFrame = null;
            return;
        }

        currentFrame = stack.peek();
        currentKlass = currentFrame.getKlass();
        currentMethod = currentFrame.getMethod();
    }

    public byte readCode() {
        byte[] codes = currentFrame.getCodes();
        return codes[pc++];
    }

    public int readConstantIndex() {
        int indexByte1 = readCode() & 0xFF;
        int indexByte2 = readCode() & 0xFF;
        return (indexByte1 << 8) + (indexByte2 << 0);
    }

    public Constant getConstant(int index) {
        return currentKlass.getConstantPool().get(index);
    }

    public KlassConstant getClassConstant(int index) {
        return (KlassConstant) getConstant(index);
    }

    public RefConstant getRefConstant(int intdex) {
        return (RefConstant) getConstant(intdex);
    }

    public FieldRefConstant getFieldRefConstant(int index) {
        return (FieldRefConstant) getConstant(index);
    }

    public MethodRefConstant getMethodRefConstant(int index) {
        return (MethodRefConstant) getConstant(index);
    }

    public InterfaceMethodRefConstant getInterfaceMethodRefConstant(int index) {
        return ((InterfaceMethodRefConstant) getConstant(index));
    }

    public NameAndTypeConstant getNameAndTypeConstant(int index) {
        return (NameAndTypeConstant) getConstant(index);
    }

    public Utf8Constant getUtf8Constant(int index) {
        return (Utf8Constant) getConstant(index);
    }

    public Heap getHeap() {
        return memory.getHeap();
    }

    public void setPc(int newPc) {
        pc = newPc;
    }

    public void incrPc(int offset) {
        pc += offset;
    }

    public void resetPc() {
        setPc(0);
    }
}
