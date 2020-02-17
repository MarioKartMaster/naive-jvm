package clazz.attribute;

import lombok.Getter;

@Getter
public class ConstantValueAttribute extends Attribute {

    private int constantValueIndex;

    public ConstantValueAttribute(int nameIndex, int length, int constantValueIndex) {
        super(nameIndex, length);
        this.constantValueIndex = constantValueIndex;
    }
}
