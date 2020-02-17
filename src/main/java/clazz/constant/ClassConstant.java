package clazz.constant;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ClassConstant extends Constant {
    private int nameIndex;
    @Setter
    private boolean isResolved;
    @Setter
    private String name;

    public ClassConstant(int tag, int nameIndex) {
        super(tag);
        this.nameIndex = nameIndex;
    }
}
