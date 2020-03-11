package klass;

import klass.attribute.Attribute;
import klass.attribute.CodeAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Method {

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

    public static final int ACC_PUBLIC	        = 0x0001;
    public static final int ACC_PRIVATE	        = 0x0002;
    public static final int ACC_PROTECTED	    = 0x0004;
    public static final int ACC_STATIC	        = 0x0008;
    public static final int ACC_FINAL	        = 0x0010;
    public static final int ACC_SYNCHRONIZED	= 0x0020;
    public static final int ACC_BRIDGE	        = 0x0040;
    public static final int ACC_VARARGS	        = 0x0080;
    public static final int ACC_NATIVE	        = 0x0100;
    public static final int ACC_ABSTRACT	    = 0x0400;
    public static final int ACC_STRICT	        = 0x0800;
    public static final int ACC_SYNTHETIC	    = 0x1000;

    public Method(int accessFlag, int nameIndex, int descriptorIndex, Attribute[] attributes) {
        this.accessFlag = accessFlag;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributes = attributes;
    }

    public CodeAttribute getCodeAttribute() {
        return (CodeAttribute) Arrays.stream(attributes)
                .filter(a -> a instanceof CodeAttribute)
                .findFirst()
                .get();
    }

    public List<String> getParameters() {
        if (! this.isResolved()) {
            throw new IllegalStateException("method is not resolved");
        }

        List<String> result = new ArrayList<>();
        String refPart = "";
        String arrayPart = "";
        boolean refFlag = false;
        boolean arrayFlag = false;
        for (int i = 0; i < descriptor.length(); i++) {
            char ch = descriptor.charAt(i);
            if (refFlag && ch != ';') {
                refPart += ch;
            } else if (ch == ';') {
                refPart += ch;
                refFlag = false;
                if (arrayFlag) {
                    arrayFlag = false;
                    arrayPart += refPart;
                    result.add(arrayPart);
                    arrayPart = "";
                } else {
                    result.add(refPart);
                }
                refPart = "";
            } else if (ch == 'B' || ch == 'C' || ch == 'D' || ch == 'F' || ch == 'I' || ch == 'J' || ch == 'S' || ch == 'Z') {
                if (arrayFlag) {
                    arrayFlag = false;
                    arrayPart += ch;
                    result.add(arrayPart);
                    arrayPart = "";
                } else {
                    result.add(String.valueOf(ch));
                }
            } else if (ch == 'L' ){
                refPart += ch;
                refFlag = true;
            } else if (ch == '[') {
                arrayPart += ch;
                arrayFlag = true;
            } else if (ch == ';') {
                refPart += ';';
            } else if (ch == '(') {
                continue;
            } else if (ch == ')') {
                break;
            } else {
                throw new IllegalStateException("unknown type: " + ch);
            }
        }

        return result;
    }

    public boolean isNative() {
        return (accessFlag & ACC_NATIVE) == ACC_NATIVE;
    }
}
