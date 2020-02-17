package clazz.constant;

public class DoubleConstant extends Constant{
    private int highBytes;
    private int lowBytes;

    public DoubleConstant(int tag, int highBytes, int lowBytes) {
        super(tag);
        this.highBytes = highBytes;
        this.lowBytes = lowBytes;
    }
}
