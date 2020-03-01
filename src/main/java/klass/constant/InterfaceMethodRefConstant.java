package klass.constant;

import klass.Method;
import lombok.Getter;
import lombok.Setter;

@Getter
public class InterfaceMethodRefConstant extends RefConstant {

    @Setter
    private Method method;

    public InterfaceMethodRefConstant(int tag, int classIndex, int nameAndTypeIndex) {
        super(tag, classIndex, nameAndTypeIndex);
    }
}
