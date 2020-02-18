package klass.constant;

import classfile.reader.ClassFileReader;
import lombok.Getter;

import java.io.IOException;

@Getter
public class ConstantFactory {

    public static Constant create(ClassFileReader is) throws IOException {
        int tag = is.readUnsignedByte();
        switch (tag) {
            case Constant.CONSTANT_Class:
                int nameIndex = is.readU2();
                return new ClassConstant(tag, nameIndex);
            case Constant.CONSTANT_Fieldref:
                int classIndex = is.readU2();
                int nameAndTypeIndex = is.readU2();
                return new FieldRefConstant(tag, classIndex, nameAndTypeIndex);
            case Constant.CONSTANT_Methodref:
                classIndex = is.readU2();
                nameAndTypeIndex = is.readU2();
                return new MethodRefConstant(tag, classIndex, nameAndTypeIndex);
            case Constant.CONSTANT_InterfaceMethodref:
                classIndex = is.readU2();
                nameAndTypeIndex = is.readU2();
                return new InterfaceMethodRefConstant(tag, classIndex, nameAndTypeIndex);
            case Constant.CONSTANT_String:
                int stringIndex = is.readU2();
                return new StringConstant(tag, stringIndex);
            case Constant.CONSTANT_Integer:
                int bytes = is.readU4();
                return new IntegerConstant(tag, bytes);
            case Constant.CONSTANT_Float:
                bytes = is.readU4();
                return new FloatConstant(tag, bytes);
            case Constant.CONSTANT_Long:
                int lowBytes = is.readU4();
                int highBytes = is.readU4();
                return new LongConstant(tag, lowBytes, highBytes);
            case Constant.CONSTANT_Double:
                lowBytes = is.readU4();
                highBytes = is.readU4();
                return new DoubleConstant(tag, lowBytes, highBytes);
            case Constant.CONSTANT_NameAndType:
                nameIndex = is.readU2();
                int descriptorIndex = is.readU2();
                return new NameAndTypeConstant(tag, nameIndex, descriptorIndex);
            case Constant.CONSTANT_Utf8:
                String value = is.readUTF();
                return new Utf8Constant(tag, value);
            case Constant.CONSTANT_MethodHandle:
                int referenceKind = is.readUnsignedByte();
                int referenceIndex = is.readU2();
                return new MethodHandleConstant(tag, referenceKind, referenceIndex);
            case Constant.CONSTANT_MethodType:
                descriptorIndex = is.readU2();
                return new MethodTypeConstant(tag, descriptorIndex);
            case Constant.CONSTANT_InvokeDynamic:
                int bootstrapMethodAttrIndex = is.readU2();
                nameAndTypeIndex = is.readU2();
                return new InvokeDynamicConstant(tag, bootstrapMethodAttrIndex, nameAndTypeIndex);
            default:
                throw new IllegalStateException("Unexpected constant tag: " + tag);
        }
    }

}
