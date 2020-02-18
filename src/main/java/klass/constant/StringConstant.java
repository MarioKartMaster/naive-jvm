package klass.constant;

import lombok.Getter;

@Getter
public class StringConstant extends Constant {
    private int stringIndex;

    public StringConstant(int tag, int stringIndex) {
        super(tag);
        this.stringIndex = stringIndex;
    }
}
