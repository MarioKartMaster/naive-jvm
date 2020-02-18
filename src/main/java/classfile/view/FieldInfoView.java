package classfile.view;

import klass.Field;
import klass.attribute.Attribute;

import java.util.LinkedHashMap;
import java.util.List;

public class FieldInfoView extends AbstractView {

    private Field field;
    private ConstantPoolView constantPoolView;
    private static LinkedHashMap<Integer, String> flags = new LinkedHashMap<>();
    static {
        flags.put(Field.ACC_PUBLIC, "PUBLIC");
        flags.put(Field.ACC_PRIVATE, "PRIVATE");
        flags.put(Field.ACC_PROTECTED, "PROTECTED");
        flags.put(Field.ACC_STATIC, "STATIC");
        flags.put(Field.ACC_FINAL, "FINAL");
        flags.put(Field.ACC_VOLATILE, "VOLATILE");
        flags.put(Field.ACC_TRANSIENT, "TRANSIENT");
        flags.put(Field.ACC_SYNTHETIC, "SYNTHETIC");
        flags.put(Field.ACC_ENUM, "ENUM");
    }

    public FieldInfoView(Field field, ConstantPoolView constantPoolView) {
        this.field = field;
        this.constantPoolView = constantPoolView;
    }

    @Override
    public void show() {
        String flag = getFlag();
        String name = getName();
        String descriptor = getDescriptor();
        showString("field", String.join(" ", flag, descriptor, name));
        for (Attribute attribute: field.getAttributes()) {
            AttributeView attributeView = new AttributeView(attribute, constantPoolView);
            attributeView.show();
        }
    }

    public String getFlag() {
        int flag = this.field.getAccessFlag();
        List<String> flagNames = getFlagsName(flags, flag);
            return String.join(" ", flagNames);
    }

    public String getName() {
        int nameIndex = field.getNameIndex();
        return constantPoolView.constantToString(nameIndex);
    }

    public String getDescriptor() {
        int descriptorIndex = field.getDescriptorIndex();
        return constantPoolView.constantToString(descriptorIndex);
    }
}
