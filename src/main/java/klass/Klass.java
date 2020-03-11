package klass;

import klass.attribute.Attribute;
import klass.constant.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import runtime.ClassLoader;
import runtime.Obj;
import runtime.memory.Memory;

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

        if (thisClass != 0) {
            KlassConstant thisKlassConstant = constantPool.getClassConstant(thisClass);
            resolveClassConstant(thisKlassConstant);
            thisClassName = thisKlassConstant.getName();
        }

        if (superClass != 0) {
            KlassConstant superKlassConstant = constantPool.getClassConstant(superClass);
            resolveClassConstant(superKlassConstant);
            superClassName = superKlassConstant.getName();
        }

        Arrays.stream(methods).forEach((m) -> this.resolveMethod(m));
        Arrays.stream(fields).forEach((f) -> this.resolveField(f));
    }

    public KlassMethodSearchResult getMainMethod() {
        return searchMethod(mainMethodName, mainMethodDescriptor);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class KlassMethodSearchResult {
        Klass klass;
        Method method;
    }

    public KlassMethodSearchResult searchMethod(String name, String descriptor) {
        Method method = getMethod(name, descriptor);
        if (null == method && 0 != superClass) {
            Klass superKlass = classLoader.loadClass(superClassName);
            return superKlass.searchMethod(name, descriptor);
        }

        if (null == method) {
            throw new IllegalArgumentException("method " + thisClassName + "\t" + name  + "\t" + descriptor + " not found");
        }

        return new KlassMethodSearchResult(this, method);
    }

    public Method getMethod(String name, String descriptor) {
        return Arrays.stream(methods)
                .filter(m -> m.getName().equals(name) && m.getDescriptor().equals(descriptor))
                .findFirst()
                .orElse(null);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class KlassFieldSearchResult {
        Klass klass;
        Field field;
    }

    public KlassFieldSearchResult searchField(String name, String descriptor) {
        Field field = getField(name, descriptor);
        if (null == field && 0 != superClass) {
            Klass superKlass = classLoader.loadClass(superClassName);
            return superKlass.searchField(name, descriptor);
        }

        if (null == field) {
            throw new IllegalArgumentException("field " + thisClassName + "\t" + name  + "\t" + descriptor + " not found");
        }

        return new KlassFieldSearchResult(this, field);
    }

    public Field getField(String name, String descriptor) {
        return Arrays.stream(fields)
                .filter(m -> m.getName().equals(name) && m.getDescriptor().equals(descriptor))
                .findFirst()
                .orElse(null);
    }

    public Method getClassInitializeMethod() {
        return getMethod(classInitializeMethodName, classInitializeMethodDescriptor);
    }

    public void resolveRefConstant(RefConstant refConstant) {
        if (refConstant.isResolved()) {
            return;
        }

        int nameAndTypeIndex = refConstant.getNameAndTypeIndex();
        NameAndTypeConstant nameAndTypeConstant = constantPool.getNameAndTypeConstant(nameAndTypeIndex);
        resolveNameAndTypeConstant(nameAndTypeConstant);

        int classIndex = refConstant.getClassIndex();
        KlassConstant klassConstant = constantPool.getClassConstant(classIndex);
        resolveClassConstant(klassConstant);

        String name = nameAndTypeConstant.getName();
        String descriptor = nameAndTypeConstant.getDescriptor();
        refConstant.setName(name);
        refConstant.setType(descriptor);

        Klass klass = classLoader.loadClass(klassConstant.getName());

        if (refConstant instanceof MethodRefConstant) {
            KlassMethodSearchResult klassMethodSearchResult = klass.searchMethod(name, descriptor);
            MethodRefConstant methodRefConstant = (MethodRefConstant) refConstant;
            methodRefConstant.setMethod(klassMethodSearchResult.method);
            methodRefConstant.setKlass(klassMethodSearchResult.klass);
            methodRefConstant.setKlassName(klassMethodSearchResult.klass.getThisClassName());
        } else if (refConstant instanceof FieldRefConstant) {
            KlassFieldSearchResult klassFieldSearchResult = klass.searchField(name, descriptor);
            FieldRefConstant fieldRefConstant = (FieldRefConstant) refConstant;
            fieldRefConstant.setField(klassFieldSearchResult.field);
            fieldRefConstant.setKlass(klassFieldSearchResult.klass);
            fieldRefConstant.setKlassName(klassFieldSearchResult.klass.getThisClassName());
        } else if (refConstant instanceof InterfaceMethodRefConstant) {
            KlassMethodSearchResult klassMethodSearchResult = klass.searchMethod(name, descriptor);
            InterfaceMethodRefConstant interfaceMethodRefConstant = (InterfaceMethodRefConstant) refConstant;
            interfaceMethodRefConstant.setMethod(klassMethodSearchResult.method);
            interfaceMethodRefConstant.setKlass(klassMethodSearchResult.klass);
            interfaceMethodRefConstant.setKlassName(klassMethodSearchResult.klass.getThisClassName());
        }

        refConstant.setResolved(true);
    }

    public void resolveClassConstant(KlassConstant klassConstant) {
        if (klassConstant.isResolved()) {
            return;
        }

        int nameIndex = klassConstant.getNameIndex();
        Utf8Constant utf8Constant = constantPool.getUtf8Constant(nameIndex);
        String klassName = utf8Constant.getBytes();
        klassConstant.setName(klassName);
//        klassConstant.setKlass(classLoader.loadClass(klassName));
        klassConstant.setResolved(true);
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

    // TODO: link obj with constant
    public void resolveStringConstant(StringConstant stringConstant, Obj obj) {
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
