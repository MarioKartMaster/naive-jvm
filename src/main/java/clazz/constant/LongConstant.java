package clazz.constant;

public class LongConstant extends Constant {
    private int highBytes;
    private int lowBytes;

    public LongConstant(int tag, int highBytes, int lowBytes) {
        super(tag);
        this.highBytes = highBytes;
        this.lowBytes = lowBytes;
    }
}
