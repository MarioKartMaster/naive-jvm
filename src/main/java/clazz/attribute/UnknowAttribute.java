package clazz.attribute;

import lombok.Getter;

@Getter
public class UnknowAttribute extends Attribute {

    private String name;
    private byte[] data;

    public UnknowAttribute(int nameIndex, int length, String name, byte[] data) {
        super(nameIndex, length);
        this.name = name;
        this.data = data;
    }
}
