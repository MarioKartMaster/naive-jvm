package runtime.memory;

import clazz.Clazz;
import clazz.Method;
import clazz.attribute.CodeAttribute;
import lombok.Getter;

import java.util.Stack;

@Getter
public class Frame {

    private Method method;
    private Clazz clazz;

    private Object[] localVariable;
    private byte[] codes;
    private Stack<Object> operandStack = new Stack<>();

    public Frame(Clazz clazz, Method method) {
        this.clazz = clazz;
        this.method = method;

        CodeAttribute codeAttribute = method.getCodeAttribute();
        int maxLocals = codeAttribute.getMaxLocals();
        codes = codeAttribute.getCode();
        localVariable = new Object[maxLocals];
    }
}
