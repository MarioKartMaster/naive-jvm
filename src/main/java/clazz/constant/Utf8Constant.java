package clazz.constant;

public class Utf8Constant extends Constant{
    private int length;
    private String bytes;

    public Utf8Constant(int tag, int length, String bytes) {
        super(tag);
        this.length = length;
        this.bytes = bytes;
    }

    public String getBytes() {
        return bytes;
    }
}
