package klass.constant;

import java.util.ArrayList;

public class ConstantPool extends ArrayList<Constant> {

    public Constant getConstant(int index) {
        return get(index);
    }

    public ClassConstant getClassConstant(int index) {
        return (ClassConstant) getConstant(index);
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

    public NameAndTypeConstant getNameAndTypeConstant(int index) {
        return (NameAndTypeConstant) getConstant(index);
    }

    public Utf8Constant getUtf8Constant(int index) {
        return (Utf8Constant) getConstant(index);
    }

    public String constantToString(int index) {
        if (index == 0) {
            return null;
        }

        Constant constant = get(index);

        if (constant instanceof Utf8Constant) {
            return ((Utf8Constant) constant).getBytes();
        }

        if (constant instanceof StringConstant) {
            index = ((StringConstant) constant).getStringIndex();
            return constantToString(index);
        }

        if (constant instanceof ClassConstant) {
            index = ((ClassConstant) constant).getNameIndex();
            return constantToString(index);
        }

        return constant.toString();
    }
}
