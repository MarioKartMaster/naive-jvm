package runtime.memory;

import klass.Klass;
import klass.Method;
import klass.attribute.CodeAttribute;
import lombok.Getter;

@Getter
public class Frame {

    private int lastPc;
    private Method method;
    private Klass klass;

    private Object[] localVariable;
    private byte[] codes;
    private OperandStack operandStack = new OperandStack();

    public Frame(Klass klass, Method method, int lastPc) {
        this.klass = klass;
        this.method = method;
        this.lastPc = lastPc;

        CodeAttribute codeAttribute = method.getCodeAttribute();
        int maxLocals = codeAttribute.getMaxLocals();
        codes = codeAttribute.getCode();
        localVariable = new Object[maxLocals];
    }
}
