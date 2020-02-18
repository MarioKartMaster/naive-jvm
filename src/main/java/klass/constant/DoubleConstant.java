package klass.constant;

import lombok.Getter;

@Getter
public class DoubleConstant extends Constant{

    private int highBytes;
    private int lowBytes;
    private double value;

    public DoubleConstant(int tag, int highBytes, int lowBytes) {
        super(tag);
        this.highBytes = highBytes;
        this.lowBytes = lowBytes;
        value = highBytes << 32 + lowBytes;
    }
}
