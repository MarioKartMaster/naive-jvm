package runtime.memory;

import lombok.Getter;
import runtime.Obj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Memory {
    private MethodArea methodArea = new MethodArea();
    private Heap heap = new Heap();
    private HashMap<String, Obj> stringConstantPool = new HashMap<>();
}
