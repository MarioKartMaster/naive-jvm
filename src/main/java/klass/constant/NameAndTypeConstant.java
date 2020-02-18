package klass.constant;

import lombok.Getter;
import lombok.Setter;

@Getter
public class NameAndTypeConstant extends Constant {
    private int nameIndex;
    private int descriptorIndex;
    @Setter
    private String name;
    @Setter
    private String descriptor;
    @Setter
    private boolean isResolved;

    public NameAndTypeConstant(int tag, int nameIndex, int descriptorIndex) {
        super(tag);
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }
}
