package clazz.constant;

public class MethodTypeConstant extends Constant {
    private int descriptorIndex;

    public MethodTypeConstant(int tag, int descriptorIndex) {
        super(tag);
        this.descriptorIndex = descriptorIndex;
    }
}
