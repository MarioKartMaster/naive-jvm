package clazz.constant;

public class IntegerConstant extends Constant {
    private int bytes;

    public IntegerConstant(int tag, int bytes) {
        super(tag);
        this.bytes = bytes;
    }
}
