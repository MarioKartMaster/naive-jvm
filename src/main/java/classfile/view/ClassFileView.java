package classfile.view;

import clazz.Clazz;
import clazz.Field;
import clazz.Method;
import clazz.attribute.Attribute;
import clazz.constant.ConstantPool;

import java.util.LinkedHashMap;
import java.util.List;

public class ClassFileView extends AbstractView {

    private Clazz classFile;
    private classfile.view.ConstantPoolView constantPoolView;
    private static LinkedHashMap<Integer, String> classFlags = new LinkedHashMap<>();
    static {
        classFlags.put(Clazz.ACC_PUBLIC, "PUBLIC");
        classFlags.put(Clazz.ACC_FINAL, "FINAL");
        classFlags.put(Clazz.ACC_SUPER, "SUPER");
        classFlags.put(Clazz.ACC_INTERFACE, "INTERFACE");
        classFlags.put(Clazz.ACC_ABSTRACT, "ABSTRACT");
        classFlags.put(Clazz.ACC_SYNTHETIC, "SYNTHETIC");
        classFlags.put(Clazz.ACC_ANNOTATION, "ANNOTATION");
        classFlags.put(Clazz.ACC_ENUM, "ENUM");
    }

    public ClassFileView(Clazz classFile) {

        this.classFile = classFile;
        ConstantPool constant = classFile.getConstantPool();
        this.constantPoolView = new classfile.view.ConstantPoolView(constant);
    }

    public void show() {
        showMagic();
        showMinorVersion();
        showMajorVersion();
        showFlags();
        showThisClass();
        showSuperClass();

        constantPoolView.show();

        showSplitter();

        for (Field field : classFile.getFields()) {
            FieldInfoView fieldInfoView = new FieldInfoView(field, constantPoolView);
            fieldInfoView.show();
        }

        showSplitter();

        for (Method method : classFile.getMethods()) {
            MethodView methodView = new MethodView(method, constantPoolView);
            methodView.show();
        }

        showSplitter();

        for (Attribute attribute : classFile.getAttributes()) {
            AttributeView attributeView = new AttributeView(attribute, constantPoolView);
            attributeView.show();
        }
    }

    public void showMagic() {
        showHex("magic", classFile.getMagic());
    }

    public void showMinorVersion() {
        showInteger("minor version", classFile.getMinorVersion());
    }

    public void showMajorVersion() {
        showInteger("major version", classFile.getMajorVersion());
    }

    public void showFlags() {
        int flag = this.classFile.getAccessFlags();
        List<String> flagNames = getFlagsName(classFlags, flag);
        String flagDisplay = String.join("\t", flagNames);
        showString("access flags", flagDisplay);
    }

    public void showThisClass() {
        ConstantPool cp = classFile.getConstantPool();
        int thisClass = classFile.getThisClass();
        showString("this class", constantPoolView.infoConvertToStringStrategy.convert(cp.get(thisClass)));
    }

    public void showSuperClass() {
        ConstantPool cp = classFile.getConstantPool();
        int superClass = classFile.getSuperClass();
        showString("this class", constantPoolView.infoConvertToStringStrategy.convert(cp.get(superClass)));
    }

}
