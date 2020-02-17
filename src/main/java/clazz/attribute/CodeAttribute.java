package clazz.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CodeAttribute extends Attribute {

    private int maxStack;
    private int maxLocals;
    private byte[] code;
    private ExceptionTable[] exceptionTables;
    private Attribute[] attributes;

    public static class Code {

    }

    @Getter
    @AllArgsConstructor
    public static class ExceptionTable {
        private int startPc;
        private int endPc;
        private int handlerPc;
        private int catchType;
    }

    public CodeAttribute(int nameIndex, int length, int maxStack, int maxLocals, byte[] code, ExceptionTable[] exceptionTables, Attribute[] attributes) {
        super(nameIndex, length);
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.code = code;
        this.exceptionTables = exceptionTables;
        this.attributes = attributes;
    }
}
