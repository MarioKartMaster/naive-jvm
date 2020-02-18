package klass;

import klass.attribute.Attribute;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Field {
    private int accessFlag;
    private int nameIndex;
    private int descriptorIndex;
    private Attribute[] attributes;

    @Setter
    private String name;
    @Setter
    private String descriptor;
    @Setter
    private boolean isResolved;

    public static final int ACC_PUBLIC	    = 0x0001;
    public static final int ACC_PRIVATE	    = 0x0002;
    public static final int ACC_PROTECTED	= 0x0004;
    public static final int ACC_STATIC	    = 0x0008;
    public static final int ACC_FINAL	    = 0x0010;
    public static final int ACC_VOLATILE	= 0x0040;
    public static final int ACC_TRANSIENT	= 0x0080;
    public static final int ACC_SYNTHETIC	= 0x1000;
    public static final int ACC_ENUM	    = 0x4000;

    public Field(int accessFlag, int nameIndex, int descriptorIndex, Attribute[] attributes) {
        this.accessFlag = accessFlag;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributes = attributes;
    }

    public boolean isStatic() {
        return (accessFlag & ACC_STATIC) == ACC_STATIC;
    }
}
