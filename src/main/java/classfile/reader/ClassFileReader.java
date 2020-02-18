package classfile.reader;

import klass.Klass;
import klass.constant.*;
import klass.Field;
import klass.Method;
import klass.attribute.Attribute;
import runtime.ClassLoader;

import java.io.IOException;
import java.io.InputStream;

public class ClassFileReader extends Reader {

    private AttributeReader attributeReader;

    public ClassFileReader(InputStream in) {
        super(in);
        this.attributeReader = new AttributeReader(in);
    }

    public int readInterface() throws IOException {
        return readU2();
    }

    public Constant readConstant() throws IOException {
        return ConstantFactory.create(this);
    }

    public Field readField(ConstantPool constants) throws IOException {
        int accessFlag = readU2();
        int nameIndex = readU2();
        int descriptorIndex = readU2();
        int attributeCount = readU2();
        Attribute[] attributes = new Attribute[attributeCount];
        for (int i = 0; i < attributeCount; i++) {
            attributes[i] = readAttribute(constants);
        }

        return new Field(accessFlag, nameIndex, descriptorIndex, attributes);
    }

    public Method readMethod(ConstantPool constants) throws IOException {
        int accessFlag = readU2();
        int nameIndex = readU2();
        int descriptorIndex = readU2();
        int attributeCount = readU2();
        Attribute[] attributes = new Attribute[attributeCount];
        for (int i = 0; i < attributeCount; i++) {
            attributes[i] = readAttribute(constants);
        }
        return new Method(accessFlag, nameIndex, descriptorIndex, attributes);
    }

    public Klass readClass(ClassLoader classLoader) throws IOException {
        int magic = readU4();
        int minorVersion = readU2();
        int majorVersion = readU2();

        int constantPoolCount = readU2();
        ConstantPool constantPool = new ConstantPool();
        constantPool.add(null);
        for (int i = 1; i < constantPoolCount; i++) {
            Constant constant = readConstant();
            constantPool.add(constant);
            if (constant instanceof LongConstant || constant instanceof DoubleConstant) {
                constantPool.add(null);
                i++;
            }
        }

        int accessFlags = readU2();
        int thisClass = readU2();
        int superClass = readU2();

        int interfacesCount = readU2();
        int[] interfaces = new int[interfacesCount];
        for (int i = 0; i < interfacesCount; i++) {
            interfaces[i] = readInterface();
        }

        int fieldsCount = readU2();
        Field[] fields = new Field[fieldsCount];
        for (int i = 0; i < fieldsCount; i++) {
            fields[i] = readField(constantPool);
        }

        int methodsCount = readU2();
        Method[] methods = new Method[methodsCount];
        for (int i = 0; i < methodsCount; i++) {
            methods[i] = readMethod(constantPool);
        }

        int attributesCount = readU2();
        Attribute[] attributes = new Attribute[attributesCount];
        for (int i = 0; i < attributesCount; i++) {
            attributes[i] = readAttribute(constantPool);
        }

        return new Klass(magic, minorVersion, majorVersion, constantPool, accessFlags, thisClass, superClass, interfaces, fields, methods, attributes, classLoader);
    }

    public Attribute readAttribute(ConstantPool constants) throws IOException {
        return attributeReader.read(constants);
    }

}
