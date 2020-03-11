package runtime;

import instruction.Instruction;
import klass.Field;
import klass.Klass;
import klass.Method;
import runtime.memory.ArrayObj;
import runtime.memory.Frame;
import runtime.memory.Thread;

import java.util.Map;

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

    public static void unsafeArrayBaseOffset(Thread t, Klass klass, Method method, Object[] args) {
        t.getCurrentFrame().getOperandStack().push(0);
    }

    public static void unsafeArrayIndexScale(Thread t, Klass klass, Method method, Object[] args) {
        t.getCurrentFrame().getOperandStack().push(0);
    }

    public static void unsafeAddressSize(Thread t, Klass klass, Method method, Object[] args) {
        t.getCurrentFrame().getOperandStack().push(0);
    }

    public static void doPrivileged(Thread t, Klass klass, Method method, Object[] args) {
        Obj arg = (Obj) args[0];
        klass = t.getClassLoader().loadClass(arg.getType());
        Klass.KlassMethodSearchResult klassMethodSearchResult = klass.searchMethod("run", "()Ljava/lang/Object;");
        Instruction.doInvoke(t, klassMethodSearchResult.getKlass(), klassMethodSearchResult.getMethod(), args);
    }

    public static void reflectGetDeclaredFields0(Thread t, Klass klass, Method method, Object[] args) {
        int publicOnly = (int) args[1];
        Obj _this = ((Obj) args[0]);
        klass = (Klass) _this.getField("value");
        Field[] fields = klass.getFields();
        int count = fields.length;

        ArrayObj result = new ArrayObj("java/lang/reflect/Field", count);

        for (int i = 0; i < count; i++) {
            Obj field = new Obj("java/lang/reflect/Field");
            // TODO: set attributes
//            field.setField("name", new Obj);
            result.getData()[i] = field;
        }

        t.getCurrentFrame().getOperandStack().push(result);
    }

    public static void stringIntern(Thread t, Klass klass, Method method, Object[] args) {
        StringBuilder stringVal = new StringBuilder();

        Obj arg = (Obj) args[0];
        ArrayObj value = (ArrayObj) arg.getField("value");
        int length = value.getLength();
        for (int i = 0; i < length; i++) {
            Character ch = ((Character) value.getData()[i]);
            stringVal.append(ch);
        }

        t.getMemory().getStringConstantPool().put(stringVal.toString(), arg);
        t.getCurrentFrame().getOperandStack().push(arg);
    }

    public static void systemArrayCopy(Thread t, Klass klass, Method method, Object[] args) {
        ArrayObj fromArray = ((ArrayObj) args[0]);
        int fromPos = ((int) args[1]);

        ArrayObj destArray = ((ArrayObj) args[2]);
        int destPos = ((int) args[3]);

        int end = ((int) args[4]) + fromPos;

        while (fromPos < end) {
            destArray.getData()[destPos++] = fromArray.getData()[fromPos++];
        }
    }

    public static void reflectGetCallerClass(Thread t, Klass klass, Method method, Object[] args) {
        Frame top = t.getStack().peek();
        Obj obj = new Obj("java/lang/class");
        obj.setField("value", t.getClassLoader().loadClass(top.getKlass().getThisClassName()));
        t.getCurrentFrame().getOperandStack().push(obj);
    }
}
