package klass.constant;

public class Utf8Constant extends Constant{
    private String bytes;

    public Utf8Constant(int tag, String bytes) {
        super(tag);
        this.bytes = bytes;
    }

    public String getBytes() {
        return bytes;
    }
}
