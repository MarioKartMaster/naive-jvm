package clazz.constant;

import lombok.Getter;
import lombok.Setter;

@Getter
public class RefConstant extends Constant {
    private int classIndex;
    private int nameAndTypeIndex;

    @Setter
    private String className;
    @Setter
    private String name;
    @Setter
    private String type;
    @Setter
    private boolean isResolved;

    public RefConstant(int tag, int classIndex, int nameAndTypeIndex) {
        super(tag);
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }
}
