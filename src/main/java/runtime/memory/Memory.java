package runtime.memory;

import lombok.Getter;

@Getter
public class Memory {
    private MethodArea methodArea = new MethodArea();
    private Heap heap = new Heap();
}
