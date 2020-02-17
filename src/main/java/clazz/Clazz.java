package clazz;

import clazz.attribute.Attribute;
import clazz.constant.*;
import lombok.Data;

import java.util.Arrays;
import java.util.Optional;

@Data
public class Clazz {

    public static final int ACC_PUBLIC	= 0x0001;
    public static final int ACC_FINAL	= 0x0010;
    public static final int ACC_SUPER	= 0x0020;
    public static final int ACC_INTERFACE	= 0x0200;
    public static final int ACC_ABSTRACT	= 0x0400;
    public static final int ACC_SYNTHETIC	= 0x1000;
    public static final int ACC_ANNOTATION	= 0x2000;
    public static final int ACC_ENUM = 0x4000;

    private final int magic;
    private final int minorVersion;
    private final int majorVersion;
    private final ConstantPool constantPool;
    private final int accessFlags;
    private final int thisClass;
    private final int superClass;
    private final int[] interfaces;
    private final Field[] fields;
    private final Method[] methods;
    private final Attribute[] attributes;

    private String name;

    public Clazz(int magic, int minorVersion, int majorVersion, ConstantPool constantPool, int accessFlags, int thisClass, int superClass, int[] interfaces, Field[] fields, Method[] methods, Attribute[] attributes) {
        this.magic = magic;
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.constantPool = constantPool;
        this.accessFlags = accessFlags;
        this.thisClass = thisClass;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.attributes = attributes;
    }

    public String getThisClassName() {
        return constantPool.constantToString(thisClass);
    }

    public String getSuperClassName() {
        return constantPool.constantToString(superClass);
    }

    public Method getMainMethod() {
        return getMethod("main", "([Ljava/lang/String;)V");
    }

    public Method getMethod(String name, String descriptor) {
        return Arrays.stream(methods).filter(m -> {
            int nameIndex = m.getNameIndex();
            int descriptorIndex = m.getDescriptorIndex();
            return name.equals(constantPool.constantToString(nameIndex))
                    && descriptor.equals(constantPool.constantToString(descriptorIndex));
        }).findFirst().get();
    }

    public void resolveMethodRefConstant(MethodRefConstant methodRefConstant) {
        if (methodRefConstant.isResolved()) {
            return;
        }

        int classIndex = methodRefConstant.getClassIndex();
        int nameAndTypeIndex = methodRefConstant.getNameAndTypeIndex();

        ClassConstant classConstant = (ClassConstant) constantPool.get(classIndex);
        resolveClassConstant(classConstant);

        NameAndTypeConstant nameAndTypeConstant = (NameAndTypeConstant) constantPool.get(nameAndTypeIndex);
        resolveNameAndTypeConstant(nameAndTypeConstant);

        methodRefConstant.setClassName(classConstant.getName());
        methodRefConstant.setName(nameAndTypeConstant.getName());
        methodRefConstant.setType(nameAndTypeConstant.getDescriptor());
        methodRefConstant.setResolved(true);
    }

    public void resolveClassConstant(ClassConstant classConstant) {
        if (classConstant.isResolved()) {
            return;
        }

        int nameIndex = classConstant.getNameIndex();
        Utf8Constant utf8Constant = (Utf8Constant) constantPool.get(nameIndex);
        String className = utf8Constant.getBytes();
        classConstant.setName(className);
        classConstant.setResolved(true);
    }

    public void resolveNameAndTypeConstant(NameAndTypeConstant nameAndTypeConstant) {
        if (nameAndTypeConstant.isResolved()) {
            return;
        }

        int nameIndex = nameAndTypeConstant.getNameIndex();
        int descriptorIndex = nameAndTypeConstant.getDescriptorIndex();

        nameAndTypeConstant.setName(getUtf8ConstantBytes(nameIndex));
        nameAndTypeConstant.setDescriptor(getUtf8ConstantBytes(descriptorIndex));
        nameAndTypeConstant.setResolved(true);
    }

    public void resolveMethod(Method method) {
        if (method.isResolved()) {
            return;
        }

        int nameIndex = method.getNameIndex();
        int descriptorIndex = method.getDescriptorIndex();

        method.setName(getUtf8ConstantBytes(nameIndex));
        method.setDescriptor(getUtf8ConstantBytes(descriptorIndex));
        method.setResolved(true);
    }

    public String getUtf8ConstantBytes(int index) {
        return ((Utf8Constant) constantPool.get(index)).getBytes();
    }
}
