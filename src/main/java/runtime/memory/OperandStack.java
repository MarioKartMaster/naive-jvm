package runtime.memory;

import runtime.Obj;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class OperandStack extends Stack<Object> {

//    public Integer popInteger() {
//        return (Integer) pop();
//    }
//
//    public Long popLong() {
//        Integer high = (Integer) pop();
//        Integer low = (Integer) pop();
//
//        long l1 = (high & 0x000000ffffffffL) << 32;
//        long l2 = low & 0x00000000ffffffffL;
//        return l1 | l2;
//    }
//
//    public Float popFloat() {
//        return Float.intBitsToFloat((Integer) pop());
//    }
//
//    public Double popDouble() {
//        Long tmp = this.popLong();
//        return Double.longBitsToDouble(tmp);
//    }
//
//    public Obj popRef() {
//        return (Obj) pop();
//    }

    public Object[] popByParameters(List<String> parameters) {
        return popByParametersHelper(parameters).toArray();
    }

    public Object[] popByParametersWithThis(List<String> parameters) {
        LinkedList<Object> result = popByParametersHelper(parameters);
        result.addFirst(pop());
        return result.toArray();
    }

    public LinkedList popByParametersHelper(List<String> parameters) {
        LinkedList<Object> result = new LinkedList<>();
        for (int i = parameters.size() - 1; i >= 0; i--) {
            String p = parameters.get(i);
            if (p.equals("J") || p.equals("D")) {
                result.addFirst(null);
            }
            result.addFirst(pop());
        }
        return result;
    }
}
