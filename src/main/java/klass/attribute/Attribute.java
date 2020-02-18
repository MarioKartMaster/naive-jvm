package klass.attribute;

import lombok.Getter;

@Getter
public abstract class Attribute {

    private int nameIndex;
    private int length;

    public Attribute(int nameIndex, int length) {
        this.nameIndex = nameIndex;
        this.length = length;
    }
}
