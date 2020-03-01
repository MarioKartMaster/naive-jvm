package klass.constant;

import klass.Klass;
import lombok.Getter;
import lombok.Setter;

@Getter
public class KlassConstant extends Constant {

    private int nameIndex;
    @Setter
    private boolean isResolved;
    @Setter
    private String name;
    @Setter
    private Klass klass;

    public KlassConstant(int tag, int nameIndex) {
        super(tag);
        this.nameIndex = nameIndex;
    }
}
