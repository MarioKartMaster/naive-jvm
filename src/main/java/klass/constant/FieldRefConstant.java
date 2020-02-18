package klass.constant;

import klass.Field;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FieldRefConstant extends RefConstant {

    @Setter
    private Field field;

    public FieldRefConstant(int tag, int classIndex, int nameAndTypeIndex) {
        super(tag, classIndex, nameAndTypeIndex);
    }
}
