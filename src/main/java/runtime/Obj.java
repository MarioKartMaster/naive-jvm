package runtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Obj {
    private String type;
    private Map<String, Object> fields = new HashMap<>();

    public Obj(String type) {
        this.type = type;
    }

    public Object getField(String name) {
        return fields.get(name);
    }

    public void setField(String name, Object val) {
        fields.put(name, val);
    }

    @Override
    public String toString() {
        return "Obj{" +
                "type='" + type + '\'' +
                '}';
    }
}
