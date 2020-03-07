package runtime;

import klass.Klass;
import klass.Method;
import runtime.memory.Thread;

public class NativeMethod {
    public static void systemInitProperties(Thread t, Klass klass, Method method, Object[] args) {
        t.getCurrentFrame().getOperandStack().push(args[0]);
    }

    public static void classGetPrimitiveClass(Thread t, Klass klass, Method method, Object[] args) {
        t.getCurrentFrame().getOperandStack().push(new runtime.primitive.Float());
    }

    public static void classDesiredAssertionStatus0(Thread t, Klass klass, Method method, Object[] args) {
        t.getCurrentFrame().getOperandStack().push(1);
    }

    public static void classRegisterNatives(Thread t, Klass klass, Method method, Object[] args) {
    }

    public static void systemRegisterNatives(Thread t, Klass klass, Method method, Object[] args) {
    }

    public static void unsafeRegisterNatives(Thread t, Klass klass, Method method, Object[] args) {
    }

    public static void vmInitialize(Thread t, Klass klass, Method method, Object[] args) {
    }

    public static void objectHashCode(Thread t, Klass klass, Method method, Object[] args) {
        t.getCurrentFrame().getOperandStack().push(args[0].hashCode());
    }

    public static void floatFloatToRawIntBits(Thread t, Klass klass, Method method, Object[] args) {
        int i = Float.floatToRawIntBits((float) args[0]);
        t.getCurrentFrame().getOperandStack().push(i);
    }

    public static void doubleDoubleToRawIntBits(Thread t, Klass klass, Method method, Object[] args) {
        long i = Double.doubleToRawLongBits((double) args[0]);
        t.getCurrentFrame().getOperandStack().push(i);
    }

    public static void doubleLongBitsToDouble(Thread t, Klass klass, Method method, Object[] args) {
        double i = Double.longBitsToDouble((long) args[0]);
        t.getCurrentFrame().getOperandStack().push(i);
    }

    public static void fileDescriptorInitIDs(Thread t, Klass klass, Method method, Object[] args) {
    }
}
