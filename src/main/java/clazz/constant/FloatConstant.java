package clazz.constant;

public class FloatConstant extends Constant {
    private int bytes;

    public FloatConstant(int tag, int bytes) {
        super(tag);
        this.bytes = bytes;
    }
}
