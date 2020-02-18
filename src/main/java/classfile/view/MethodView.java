package classfile.view;

import klass.Method;
import klass.attribute.Attribute;

import java.util.LinkedHashMap;
import java.util.List;

public class MethodView extends AbstractView {

    private Method method;
    private ConstantPoolView constantPoolView;
    private static LinkedHashMap<Integer, String> flags = new LinkedHashMap<>();

    static {
        flags.put(Method.ACC_PUBLIC, "PUBLIC");
        flags.put(Method.ACC_PRIVATE, "PRIVATE");
        flags.put(Method.ACC_PROTECTED, "PROTECTED");
        flags.put(Method.ACC_STATIC, "STATIC");
        flags.put(Method.ACC_FINAL, "FINAL");
        flags.put(Method.ACC_SYNCHRONIZED, "SYCHRONIZED");
        flags.put(Method.ACC_BRIDGE, "BRIDGE");
        flags.put(Method.ACC_VARARGS, "VARGS");
        flags.put(Method.ACC_NATIVE, "NATIVE");
        flags.put(Method.ACC_ABSTRACT, "ABSTRACT");
        flags.put(Method.ACC_STRICT, "STRICT");
        flags.put(Method.ACC_SYNTHETIC, "SYNTHETIC");
    }

    public MethodView(Method method, ConstantPoolView constantPoolView) {
        this.method = method;
        this.constantPoolView = constantPoolView;
    }

    @Override
    public void show() {
        String flag = getFlag();
        String name = getName();
        String descriptor = getDescriptor();
        showString("method", String.join(" ", flag, descriptor, name));
        for (Attribute attribute: method.getAttributes()) {
            AttributeView attributeView = new AttributeView(attribute, constantPoolView);
            attributeView.show();
        }
    }

    public String getFlag() {
        int flag = method.getAccessFlag();
        List<String> flagNames = getFlagsName(flags, flag);
        return String.join(" ", flagNames);
    }

    public String getName() {
        int nameIndex = method.getNameIndex();
        return constantPoolView.constantToString(nameIndex);
    }

    public String getDescriptor() {
        int descriptorIndex = method.getDescriptorIndex();
        return constantPoolView.constantToString(descriptorIndex);
    }
}
