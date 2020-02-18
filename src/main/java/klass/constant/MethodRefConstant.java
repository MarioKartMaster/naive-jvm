package klass.constant;

import klass.Method;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodRefConstant extends RefConstant {

    private Method method;

    public MethodRefConstant(int tag, int classIndex, int nameAndTypeIndex) {
        super(tag, classIndex, nameAndTypeIndex);
    }
}
