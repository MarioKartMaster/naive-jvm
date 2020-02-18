package runtime.memory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrayObj {
    private String type;
    private int length;

    public ArrayObj(String type, int length) {
        this.type = type;
        this.length = length;
    }
}
