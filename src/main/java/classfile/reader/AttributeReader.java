package classfile.reader;

import klass.attribute.*;
import klass.constant.Constant;
import klass.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AttributeReader extends Reader {

    private static Map<String, Class> nameClassMap = new HashMap<>();
    private static Class<?>[] parameters = new Class[]{
            ClassFileReader.class,
            int.class,
            int.class,
    };

    static {
        nameClassMap.put("ConstantValue", ConstantValueAttribute.class);
        nameClassMap.put("SourceFile", SourceFileAttribute.class);
    }

    private Constant[] constants;

    public AttributeReader(InputStream in) {
        super(in);
    }

    public Attribute read(ConstantPool constantPool) throws IOException {
        int nameIndex = readU2();
        int length = readU4();

        String name = constantPool.constantToString(nameIndex);
        // TODO: support other attribute type
        switch (name) {
            case "ConstantValue":
                int constantValueIndex = readU2();
                return new ConstantValueAttribute(nameIndex, length, constantValueIndex);
            case "SourceFile":
                int sourceFileIndex = readU2();
                return new SourceFileAttribute(nameIndex, length, sourceFileIndex);
            case "Code":
                int maxStack = readU2();
                int maxLocals = readU2();

                int codeLength = readU4();
                byte[] code = new byte[codeLength];
                readFully(code);

                int exceptionTableLength = readU2();
                CodeAttribute.ExceptionTable[] exceptionTables = new CodeAttribute.ExceptionTable[exceptionTableLength];
                for (int i = 0; i < exceptionTableLength; i++) {
                    int startPC = readU2();
                    int endPC = readU2();
                    int handlerPC = readU2();
                    int catchType = readU2();
                    exceptionTables[i] = new CodeAttribute.ExceptionTable(startPC, endPC, handlerPC, catchType);
                }

                int attributeCount = readU2();
                Attribute[] attributes = new Attribute[attributeCount];
                for (int i = 0; i < attributeCount; i++) {
                    attributes[i] = this.read(constantPool);
                }

                return new CodeAttribute(nameIndex, length, maxStack, maxLocals, code, exceptionTables, attributes);
            default:
                byte[] data = new byte[length];
                readFully(data);
                return new UnknowAttribute(nameIndex, length, name, data);
        }
    }
}
