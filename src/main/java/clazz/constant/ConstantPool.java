package clazz.constant;

import java.util.ArrayList;

public class ConstantPool extends ArrayList<Constant> {
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
