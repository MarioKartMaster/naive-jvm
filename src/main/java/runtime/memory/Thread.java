package runtime.memory;

import clazz.Clazz;
import clazz.constant.Constant;
import lombok.Getter;
import runtime.BootstrapClassLoader;

import java.util.Stack;

@Getter
public class Thread {
    private int pc;
    private Stack<Frame> stack = new Stack();

    public Thread(BootstrapClassLoader bootstrapClassLoader) {
        this.bootstrapClassLoader = bootstrapClassLoader;
    }

    private BootstrapClassLoader bootstrapClassLoader;

    public Frame getCurrentFrame() {
        return stack.peek();
    }

    public byte readCode() {
        Frame frame = getCurrentFrame();
        byte[] codes = frame.getCodes();
        return codes[pc++];
    }

    public Constant getConstant(int index) {
        Frame frame = getCurrentFrame();
        return frame.getClazz().getConstantPool().get(index);
    }

    public Clazz getClazz() {
        return getCurrentFrame().getClazz();
    }

    public void setPc(int newPc) {
        pc = newPc;
    }

    public void movePc(int offset) {
        pc += offset;
    }

    public void resetPc() {
        setPc(0);
    }
}
