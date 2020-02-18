package klass.constant;

import lombok.Getter;

@Getter
public class LongConstant extends Constant {

    private int highBytes;
    private int lowBytes;
    private long value;

    public LongConstant(int tag, int highBytes, int lowBytes) {
        super(tag);
        this.highBytes = highBytes;
        this.lowBytes = lowBytes;
        value = highBytes << 32 + lowBytes;
    }
}
