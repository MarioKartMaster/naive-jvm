package classfile.view;

import clazz.constant.*;

public class ConstantPoolView {

    private ConstantPool constantPool;

    public ConstantPoolView(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public void show() {
        for (int i = 1; i < constantPool.size(); i++) {
            Constant constant = constantPool.get(i);
            System.out.format("#%d\t%-50s\t%s\n", i, constant.getClass().getName(), this.infoConvertToStringStrategy.convert(constant));
        }
    }

    public interface ConvertToStringStrategy {
        String convert(Constant constant);
    }

    public ConvertToStringStrategy refStrategy = (info) -> {
        RefConstant i = (RefConstant) info;
        int classIndex = i.getClassIndex();
        int nameAndTypeIndex = i.getNameAndTypeIndex();
        return java.lang.String.format("%s.%s",
                this.infoConvertToStringStrategy.convert(constantPool.get(classIndex)),
                this.infoConvertToStringStrategy.convert(constantPool.get(nameAndTypeIndex))
        );
    };

    public ConvertToStringStrategy utf8Strategy = (info) -> ((Utf8Constant) info).getBytes();

    public ConvertToStringStrategy classStrategy = info -> {
        ClassConstant i = (ClassConstant) info;
        int nameIndex = i.getNameIndex();
        return this.infoConvertToStringStrategy.convert(constantPool.get(nameIndex));
    };

    public ConvertToStringStrategy nameAndTypeStrategy = info -> {
        NameAndTypeConstant i = (NameAndTypeConstant) info;
        int nameIndex = i.getNameIndex();
        int descriptionIndex = i.getDescriptorIndex();
        return java.lang.String.format("%s:%s",
                this.infoConvertToStringStrategy.convert(constantPool.get(nameIndex)),
                this.infoConvertToStringStrategy.convert(constantPool.get(descriptionIndex))
        );
    };

    public ConvertToStringStrategy stringStrategy = info -> {
        int stringIndex = ((StringConstant) info).getStringIndex();
        return this.infoConvertToStringStrategy.convert(constantPool.get(stringIndex));
    };

    public String constantToString(int index) {
        return infoConvertToStringStrategy.convert(constantPool.get(index));
    }

    public ConvertToStringStrategy infoConvertToStringStrategy = (info) -> {
        int tag = info.getTag();
        switch (tag) {
            case Constant.CONSTANT_Utf8:
                return utf8Strategy.convert(info);
            case Constant.CONSTANT_Class:
                return classStrategy.convert(info);
            case Constant.CONSTANT_Methodref:
            case Constant.CONSTANT_Fieldref:
            case Constant.CONSTANT_InterfaceMethodref:
                return refStrategy.convert(info);
            case Constant.CONSTANT_NameAndType:
                return nameAndTypeStrategy.convert(info);
            case Constant.CONSTANT_String:
                return stringStrategy.convert(info);
            default:
                return "";
        }
    };
}
