package clazz.constant;

public class MethodHandleConstant extends Constant {
    private int referenceKind;
    private int referenceIndex;

    public MethodHandleConstant(int tag, int referenceKind, int referenceIndex) {
        super(tag);
        this.referenceKind = referenceKind;
        this.referenceIndex = referenceIndex;
    }
}

