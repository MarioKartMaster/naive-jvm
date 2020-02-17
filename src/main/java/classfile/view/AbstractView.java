package classfile.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractView {

    public abstract void show();

    public static List<String> getFlagsName(Map<Integer, String> flagDefinition, int flag) {
        List<String> result = new LinkedList<>();
        for (Map.Entry<Integer, String> entry: flagDefinition.entrySet()) {
            Integer testFlag = entry.getKey();
            if (testFlag == (testFlag & flag)) {
                result.add(entry.getValue());
            }
        }

        return result;
    }

    public void showInteger(String key, Integer val) {
        System.out.format("%s: %d\n", key, val);
    }

    public void showHex(String key, Integer val) {
        System.out.format("%s: 0x%X\n", key, val);
    }

    public void showString(String key, String val) {
        System.out.format("%s: %s\n", key, val);
    }

    public void showSplitter() {
        System.out.println();
    }
}
