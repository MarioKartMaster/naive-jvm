package klass;

import klass.attribute.Attribute;
import lombok.Getter;
import runtime.ClassLoader;

@Getter
public class ArrayKlass extends Klass {

    private String type;

    public ArrayKlass(String type, ClassLoader classLoader) {
        super(0, 0, 0, null, 0, 0, 0, new int[]{}, new Field[]{}, new Method[]{}, new Attribute[]{}, classLoader);
        this.type = type;
    }
}
