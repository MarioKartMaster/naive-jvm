package klass;

import klass.attribute.Attribute;
import klass.constant.*;
import lombok.Data;
import runtime.ClassLoader;

import java.util.*;

@Data
public class Klass {

    public static final String classInitializeMethodName = "<clinit>";
    public static final String classInitializeMethodDescriptor = "()V";

    public static final int ACC_PUBLIC	= 0x0001;
    public static final int ACC_FINAL	= 0x0010;
    public static final int ACC_SUPER	= 0x0020;
    public static final int ACC_INTERFACE	= 0x0200;
    public static final int ACC_ABSTRACT	= 0x0400;
    public static final int ACC_SYNTHETIC	= 0x1000;
    public static final int ACC_ANNOTATION	= 0x2000;
    public static final int ACC_ENUM = 0x4000;

    public static final String mainMethodName = "main";
    public static final String mainMethodDescriptor = "([Ljava/lang/String;)V";

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

    private ClassLoader classLoader;
    private String thisClassName;
    private String superClassName;
    private boolean isResolved;

    private Map<String, Object> staticFields = new HashMap<>();
    private boolean isInitialized;

    public Klass(int magic, int minorVersion, int majorVersion, ConstantPool constantPool, int accessFlags, int thisClass, int superClass, int[] interfaces, Field[] fields, Method[] methods, Attribute[] attributes, ClassLoader classLoader) {
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
        this.classLoader = classLoader;

        prepare();

        ClassConstant thisClassConstant = constantPool.getClassConstant(thisClass);
        resolveClassConstant(thisClassConstant);
        thisClassName = thisClassConstant.getName();

        if (superClass != 0) {
            ClassConstant superClassConstant = constantPool.getClassConstant(superClass);
            resolveClassConstant(superClassConstant);
            superClassName = superClassConstant.getName();
        }

        Arrays.stream(methods).forEach((m) -> this.resolveMethod(m));
        Arrays.stream(fields).forEach((f) -> this.resolveField(f));
    }

    public Method getMainMethod() {
        return searchMethod(mainMethodName, mainMethodDescriptor);
    }

    public Method searchMethod(String name, String descriptor) {
        Method method = getMethod(name, descriptor);
        if (null == method && 0 != superClass) {
            Klass superKlass = classLoader.loadClass(superClassName);
            return superKlass.searchMethod(name, descriptor);
        }

        if (null == method) {
            throw new IllegalArgumentException("method " + thisClassName + "\t" + name  + "\t" + descriptor + " not found");
        }

        return method;
    }

    public Method getMethod(String name, String descriptor) {
        return Arrays.stream(methods)
                .filter(m -> m.getName().equals(name) && m.getDescriptor().equals(descriptor))
                .findFirst()
                .orElse(null);
    }

    public Method getClassInitializeMethod() {
        return getMethod(classInitializeMethodName, classInitializeMethodDescriptor);
    }

    public Field getField(String name, String descriptor) {
        Optional<Field> field = Arrays.stream(fields)
                .filter(m -> m.getName().equals(name) && m.getDescriptor().equals(descriptor))
                .findFirst();
        return field.orElseThrow(() -> new IllegalArgumentException("method " + thisClassName + name + descriptor + " not found"));
    }

    public void resolveRefConstant(RefConstant refConstant) {
        if (refConstant.isResolved()) {
            return;
        }

        int classIndex = refConstant.getClassIndex();
        int nameAndTypeIndex = refConstant.getNameAndTypeIndex();

        ClassConstant classConstant = constantPool.getClassConstant(classIndex);
        resolveClassConstant(classConstant);

        NameAndTypeConstant nameAndTypeConstant = constantPool.getNameAndTypeConstant(nameAndTypeIndex);
        resolveNameAndTypeConstant(nameAndTypeConstant);

        String className = classConstant.getName();
        String name = nameAndTypeConstant.getName();
        String descriptor = nameAndTypeConstant.getDescriptor();

        refConstant.setClazzName(className);
        refConstant.setName(name);
        refConstant.setType(descriptor);

        Klass targetClass = classLoader.loadClass(classConstant.getName());

        if (refConstant instanceof MethodRefConstant) {
            MethodRefConstant methodRefConstant = (MethodRefConstant) refConstant;
            methodRefConstant.setMethod(targetClass.searchMethod(name, descriptor));
        } else if (refConstant instanceof FieldRefConstant) {
            FieldRefConstant fieldRefConstant = (FieldRefConstant) refConstant;
            fieldRefConstant.setField(targetClass.getField(name, descriptor));
        } else if (refConstant instanceof InterfaceMethodRefConstant) {
            InterfaceMethodRefConstant interfaceMethodRefConstant = (InterfaceMethodRefConstant) refConstant;
            interfaceMethodRefConstant.setMethod(targetClass.searchMethod(name, descriptor));
        }

        refConstant.setKlass(targetClass);
        refConstant.setResolved(true);
    }

    public void resolveClassConstant(ClassConstant classConstant) {
        if (classConstant.isResolved()) {
            return;
        }

        int nameIndex = classConstant.getNameIndex();
        Utf8Constant utf8Constant = constantPool.getUtf8Constant(nameIndex);
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

    public void resolveField(Field field) {
        if (field.isResolved()) {
            return;
        }

        int nameIndex = field.getNameIndex();
        int descriptorIndex = field.getDescriptorIndex();

        field.setName(getUtf8ConstantBytes(nameIndex));
        field.setDescriptor(getUtf8ConstantBytes(descriptorIndex));
        field.setResolved(true);
    }

    public String getUtf8ConstantBytes(int index) {
        return constantPool.getUtf8Constant(index).getBytes();
    }

    public void prepare() {
        Arrays.stream(fields).filter((f) -> f.isStatic()).forEach((f) -> {
            resolveField(f);
            String name = f.getName();
            staticFields.put(name, null);
        });
    }

    public void initialize() {
        Arrays.stream(fields).filter((f) -> f.isStatic()).forEach((f) -> {
            resolveField(f);
            String name = f.getName();
            String descriptor = f.getDescriptor();
            if (descriptor.equals("B")) {
                staticFields.put(name, (byte) 0);
            } else if (descriptor.equals("C")) {
                staticFields.put(name, (char) 0);
            } else if (descriptor.equals("D")) {
                staticFields.put(name, (double) 0);
            } else if (descriptor.equals("F")) {
                staticFields.put(name, (float) 0);
            } else if (descriptor.equals("I")) {
                staticFields.put(name, 0);
            } else if (descriptor.equals("J")) {
                staticFields.put(name, (long) 0);
            } else if (descriptor.equals("S")) {
                staticFields.put(name, (short) 0);
            } else if (descriptor.equals("Z")) {
                staticFields.put(name, 0);
            }
        });
    }

    public Object getStaticField(String name) {
        return staticFields.get(name);
    }
}
