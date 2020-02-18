package instruction;

import java.util.*;
import java.util.function.Consumer;

import klass.Klass;
import klass.Method;
import klass.constant.*;
import runtime.NativeMethod;
import runtime.Obj;
import runtime.memory.*;
import runtime.memory.Thread;

public class Instruction {
    private static Map<Byte, Consumer<Thread>> instructionTable = new HashMap<>();
    private static Map<Byte, String> instructionNameTable = new HashMap<>();

    static {
        // aconst_null
        instructionNameTable.put((byte) 0x1, "aconst_null");
        instructionTable.put((byte) 0x1, (t) -> {
            t.getCurrentFrame().getOperandStack().push(null);
        });

        // iconst_0
        instructionNameTable.put((byte) 0x3, "iconst_0");
        instructionTable.put((byte) 0x03, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            currentFrame.getOperandStack().push(0);
        });

        // iconst_1
        instructionNameTable.put((byte) 0x4, "iconst_1");
        instructionTable.put((byte) 0x04, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            currentFrame.getOperandStack().push(1);
        });

        // bipush
        instructionNameTable.put((byte) 0x10, "bipush");
        instructionTable.put((byte) 0x10, (t) -> {
            Byte data = t.readCode();
            t.getCurrentFrame().getOperandStack().push((int) data);
        });

        // sipush
        instructionNameTable.put((byte) 0x11, "sipush");
        instructionTable.put((byte) 0x11, (t) -> {
            int byte1 = t.readCode();
            int byte2 = t.readCode();
            int data = ((byte1 << 8) + (byte2 << 0));
            t.getCurrentFrame().getOperandStack().push(data);
        });

        // ldc
        instructionNameTable.put((byte) 0x12, "ldc");
        instructionTable.put((byte) 0x12, (t) -> {
            Byte index = t.readCode();
            Constant constant = t.getConstant(index);
            OperandStack operandStack = t.getCurrentFrame().getOperandStack();
            if (constant instanceof IntegerConstant) {
                int data = ((IntegerConstant) constant).getBytes();
                operandStack.push(data);
            } else if (constant instanceof FloatConstant) {
                float data = ((FloatConstant) constant).getBytes();
                operandStack.push(data);
            } else if (constant instanceof StringConstant) {
                String s = t.getCurrentKlass().getUtf8ConstantBytes(((StringConstant) constant).getStringIndex());
                operandStack.push(s);
            } else if (constant instanceof ClassConstant) {
                ClassConstant classConstant = (ClassConstant) constant;
                t.getCurrentKlass().resolveClassConstant(classConstant);
                Obj obj = new Obj("java/lang/class");
                obj.setField("value", t.getClassLoader().loadClass(classConstant.getName()));
                operandStack.push(obj);
            } else {
                throw new IllegalStateException("ldc constant type not implemented");
            }
        });

        // ldc2_w
        instructionNameTable.put((byte) 0x14, "ldc2_w");
        instructionTable.put((byte) 0x14, (t) -> {
            int index = t.readConstantIndex();
            Constant constant = t.getConstant(index);
            if (constant instanceof LongConstant) {
                LongConstant longConstant = ((LongConstant) constant);
                t.getCurrentFrame().getOperandStack().push(longConstant.getValue());
            } else if (constant instanceof DoubleConstant) {
                DoubleConstant doubleConstant = ((DoubleConstant) constant);
                t.getCurrentFrame().getOperandStack().push(doubleConstant.getValue());
            } else {
                throw new IllegalStateException("illegal constant type " + constant.getClass());
            }
        });

        // iload_0
        instructionNameTable.put((byte) 0x1a, "iload_0");
        instructionTable.put((byte) 0x1a, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[0]);
        });

        // iload_1
        instructionNameTable.put((byte) 0x1b, "iload_1");
        instructionTable.put((byte) 0x1b, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[1]);
        });

        // fload_0
        instructionNameTable.put((byte) 0x22, "fload_0");
        instructionTable.put((byte) 0x22, (t) -> {
            float value = (float) t.getCurrentFrame().getLocalVariable()[0];
            t.getCurrentFrame().getOperandStack().push(value);
        });

        // fload_1
        instructionNameTable.put((byte) 0x23, "fload_1");
        instructionTable.put((byte) 0x23, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            Object data = currentFrame.getLocalVariable()[1];
            currentFrame.getOperandStack().push(data);
        });

        // fload_2
        instructionNameTable.put((byte) 0x24, "fload_2");
        instructionTable.put((byte) 0x24, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            Object data = currentFrame.getLocalVariable()[2];
            currentFrame.getOperandStack().push(data);
        });

        // aload_0
        instructionNameTable.put((byte) 0x2a, "aload_0");
        instructionTable.put((byte) 0x2a, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            Object data = currentFrame.getLocalVariable()[0];
            currentFrame.getOperandStack().push(data);
        });

        // aload_1
        instructionNameTable.put((byte) 0x2b, "aload_1");
        instructionTable.put((byte) 0x2b, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            Object data = currentFrame.getLocalVariable()[1];
            currentFrame.getOperandStack().push(data);
        });

        // istore_1
        instructionNameTable.put((byte) 0x3c, "istore_1");
        instructionTable.put((byte) 0x3c, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[1] = data;
        });

        // astore_1
        instructionNameTable.put((byte) 0x4c, "astore_1");
        instructionTable.put((byte) 0x4c, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[1] = data;
        });

        // pop
        instructionNameTable.put((byte) 0x57, "pop");
        instructionTable.put((byte) 0x57, (t) -> {
            t.getCurrentFrame().getOperandStack().pop();
        });

        // dup
        instructionNameTable.put((byte) 0x59, "dup");
        instructionTable.put((byte) 0x59, (t) -> {
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            Object data = operandStack.peek();
            operandStack.push(data);
        });

        // iadd
        instructionNameTable.put((byte) 0x60, "iadd");
        instructionTable.put((byte) 0x60, (t) -> {
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            Integer v1 = (Integer) operandStack.pop();
            Integer v2 = (Integer) operandStack.pop();
            int result = v1 + v2;
            operandStack.push(result);
        });

        // ladd
        instructionNameTable.put((byte) 0x61, "ladd");
        instructionTable.put((byte) 0x61, (t) -> {
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            long v1 = (long) operandStack.pop();
            long v2 = (long) operandStack.pop();
            long result = v1 + v2;
            operandStack.push(result);
        });

        // fmul
        instructionNameTable.put((byte) 0x6a, "fmul");
        instructionTable.put((byte) 0x6a, (t) -> {
            // TODO: support NAN
            float value1 = (float) t.getCurrentFrame().getOperandStack().pop();
            float value2 = (float) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(value1 * value2);
        });

        // lshl
        instructionNameTable.put((byte) 0x79, "lshl");
        instructionTable.put((byte) 0x79, t -> {
            int bits = (int) t.getCurrentFrame().getOperandStack().pop();
            long val = (long) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(val << bits);
        });

        // land
        instructionNameTable.put((byte) 0x7f, "land");
        instructionTable.put((byte) 0x7f, t -> {
            long value1 = (long) t.getCurrentFrame().getOperandStack().pop();
            long value2 = (long) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(value1 & value2);
        });

        // i2l
        instructionNameTable.put((byte) 0x85, "i2l");
        instructionTable.put((byte) 0x85, (t) -> {
            int value = (int) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push((long) value);
        });

        // i2f
        instructionNameTable.put((byte) 0x86, "i2f");
        instructionTable.put((byte) 0x86, (t) -> {
            int value = (int) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push((float) value);
        });

        // f2i
        instructionNameTable.put((byte) 0x8b, "f2i");
        instructionTable.put((byte) 0x8b, (t) -> {
            float value = (float) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push((int) value);
        });

        // fcmpl
        instructionNameTable.put((byte) 0x95, "fcmpl");
        instructionTable.put((byte) 0x95, (t) -> {
            OperandStack operandStack = t.getCurrentFrame().getOperandStack();
            float value2 = (float) operandStack.pop();
            float value1 = (float) operandStack.pop();
            if (value1 > value2) {
                operandStack.push(1);
            } else if (value1 == value2) {
                operandStack.push(0);
            } else if (value1 < value2) {
                operandStack.push(-1);
            } else {
                operandStack.push(-1);
            }
        });

        // fcmpg
        instructionNameTable.put((byte) 0x96, "fcmpg");
        instructionTable.put((byte) 0x96, (t) -> {
            OperandStack operandStack = t.getCurrentFrame().getOperandStack();
            float value2 = (float) operandStack.pop();
            float value1 = (float) operandStack.pop();
            if (value1 > value2) {
                operandStack.push(1);
            } else if (value1 == value2) {
                operandStack.push(0);
            } else if (value1 < value2) {
                operandStack.push(-1);
            } else {
                operandStack.push(1);
            }
        });

        // ifeq
        instructionNameTable.put((byte) 0x99, "ifeq");
        instructionTable.put((byte) 0x99, (t) -> {
            Integer value = (Integer) t.getCurrentFrame().getOperandStack().pop();
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            if (value == 0) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });

        // ifle
        instructionNameTable.put((byte) 0x9e, "ifle");
        instructionTable.put((byte) 0x9e, (t) -> {
            int value = (int) t.getCurrentFrame().getOperandStack().pop();
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            if (value <= 0) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });

        // ifne
        instructionNameTable.put((byte) 0x9a, "ifne");
        instructionTable.put((byte) 0x9a, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            Object val = t.getCurrentFrame().getOperandStack().pop();
            if ((int) val != 0) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });

        // ifge
        instructionNameTable.put((byte) 0x9c, "ifge");
        instructionTable.put((byte) 0x9c, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            Object val = t.getCurrentFrame().getOperandStack().pop();
            if ((int) val >= 0) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });

        // ifgt
        instructionNameTable.put((byte) 0x9d, "ifgt");
        instructionTable.put((byte) 0x9d, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            Object val = t.getCurrentFrame().getOperandStack().pop();
            if ((int) val > 0) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });

        // if_icmplt
        instructionNameTable.put((byte) 0xa1, "if_icmplt");
        instructionTable.put((byte) 0xa1, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();

            if (value1 < value2) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });

        // if_icmple
        instructionNameTable.put((byte) 0xa4, "if_icmple");
        instructionTable.put((byte) 0xa4, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();

            if (value1 <= value2) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });

        // ireturn
        instructionNameTable.put((byte) 0xac, "ireturn");
        instructionTable.put((byte) 0xac, (t) -> {
            Frame oldFrame = t.pop();
            Object result = oldFrame.getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(result);
            t.setPc(oldFrame.getLastPc());
        });

        // freturn
        instructionNameTable.put((byte) 0xae, "freturn");
        instructionTable.put((byte) 0xae, (t) -> {
            Frame oldFrame = t.pop();
            Object result = oldFrame.getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(result);
            t.setPc(oldFrame.getLastPc());
        });

        // dreturn
        instructionNameTable.put((byte) 0xaf, "dreturn");
        instructionTable.put((byte) 0xaf, (t) -> {
            Frame oldFrame = t.pop();
            Object result = oldFrame.getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(result);
            t.setPc(oldFrame.getLastPc());
        });

        // fconst_0
        instructionNameTable.put((byte) 0xb, "fconst_0");
        instructionTable.put((byte) 0xb, (t) -> {
            t.getCurrentFrame().getOperandStack().push((float) 0.0);
        });

        // areturn
        instructionNameTable.put((byte) 0xb0, "areturn");
        instructionTable.put((byte) 0xb0, (t) -> {
            Frame oldFrame = t.pop();
            Object result = oldFrame.getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(result);
            t.setPc(oldFrame.getLastPc());
        });

        // return
        instructionNameTable.put((byte) 0xb1, "return");
        instructionTable.put((byte) 0xb1, (t) -> {
            Frame oldFrame = t.pop();
            t.setPc(oldFrame.getLastPc());
        });

        // getstatic
        instructionNameTable.put((byte) 0xb2, "getstatic");
        instructionTable.put((byte) 0xb2, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            Klass klass = t.getCurrentKlass();
            FieldRefConstant fieldRefConstant = t.getFieldRefConstant(index);
            klass.resolveRefConstant(fieldRefConstant);

            Klass targetKlass = fieldRefConstant.getKlass();
            if (! targetKlass.isInitialized()) {
                t.incrPc(-3);
                initClass(t, targetKlass);
                return;
            }

            Object staticField = targetKlass.getStaticField(fieldRefConstant.getName());
            t.getCurrentFrame().getOperandStack().push(staticField);
        });

        // putstatic
        instructionNameTable.put((byte) 0xb3, "putstatic");
        instructionTable.put((byte) 0xb3, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            Klass klass = t.getCurrentKlass();
            FieldRefConstant fieldRefConstant = t.getFieldRefConstant(index);
            klass.resolveRefConstant(fieldRefConstant);

            Klass targetKlass = fieldRefConstant.getKlass();
            if (! targetKlass.isInitialized()) {
                t.incrPc(-3);
                initClass(t, targetKlass);
                return;
            }

            String name = fieldRefConstant.getName();
            Object value = t.getCurrentFrame().getOperandStack().pop();

            // TODO: type conversion
            targetKlass.getStaticFields().put(name, value);
        });

        // getfield
        instructionNameTable.put((byte) 0xb4, "getfield");
        instructionTable.put((byte) 0xb4, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            FieldRefConstant fieldRefConstant = t.getFieldRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(fieldRefConstant);

            Obj objref = (Obj) t.getCurrentFrame().getOperandStack().pop();

            String name = fieldRefConstant.getName();

            Object data = objref.getField(name);

            t.getCurrentFrame().getOperandStack().push(data);
        });

        // putfield
        instructionNameTable.put((byte) 0xb5, "putfield");
        instructionTable.put((byte) 0xb5, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            FieldRefConstant fieldRefConstant = t.getFieldRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(fieldRefConstant);

            Object value = t.getCurrentFrame().getOperandStack().pop();
            Obj objref = (Obj) t.getCurrentFrame().getOperandStack().pop();

            String name = fieldRefConstant.getName();

            objref.setField(name, value);
        });

        // invokevirtual
        instructionNameTable.put((byte) 0xb6, "invokevirtual");
        instructionTable.put((byte) 0xb6, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            MethodRefConstant methodRefConstant = t.getMethodRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(methodRefConstant);
            Method targetMethod = methodRefConstant.getMethod();

            List<String> parameters = targetMethod.getParameters();
            Object[] args = t.getCurrentFrame().getOperandStack().popByParametersWithThis(parameters);

            Klass targetKlass = t.getClassLoader().loadClass(((Obj) args[0]).getType());
            targetMethod = targetKlass.searchMethod(targetMethod.getName(), targetMethod.getDescriptor());

            System.out.format("invoke virtual %s %s %s\n", methodRefConstant.getClazzName(), methodRefConstant.getName(), methodRefConstant.getType());

            doInvoke(t, targetKlass, targetMethod, args);
        });

        // invokespecial
        instructionNameTable.put((byte) 0xb7, "invokespecial");
        instructionTable.put((byte) 0xb7, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            Klass klass = t.getCurrentKlass();
            MethodRefConstant methodRefConstant = t.getMethodRefConstant(index);
            klass.resolveRefConstant(methodRefConstant);
            Klass targetKlass = methodRefConstant.getKlass();
            Method targetMethod = methodRefConstant.getMethod();

            List<String> parameters = targetMethod.getParameters();
            Object[] args = t.getCurrentFrame().getOperandStack().popByParametersWithThis(parameters);

            System.out.format("invoke special %s %s %s\n", methodRefConstant.getClazzName(), methodRefConstant.getName(), methodRefConstant.getType());

            doInvoke(t, targetKlass, targetMethod, args);
        });

        // invokestatic
        instructionNameTable.put((byte) 0xb8, "invokestatic");
        instructionTable.put((byte) 0xb8, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            Klass klass = t.getCurrentKlass();
            MethodRefConstant methodRefConstant = t.getMethodRefConstant(index);
            klass.resolveRefConstant(methodRefConstant);

            Klass targetKlass = methodRefConstant.getKlass();
            if (! targetKlass.isInitialized()) {
                t.incrPc(-3);
                initClass(t, targetKlass);
                return;
            }
            Method targetMethod = methodRefConstant.getMethod();

            List<String> parameters = targetMethod.getParameters();
            Object[] args = t.getCurrentFrame().getOperandStack().popByParameters(parameters);

            System.out.format("invoke static %s %s %s\n", methodRefConstant.getClazzName(), methodRefConstant.getName(), methodRefConstant.getType());

            doInvoke(t, targetKlass, targetMethod, args);
        });

        // new
        instructionNameTable.put((byte) 0xbb, "new");
        instructionTable.put((byte) 0xbb, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            Klass klass = t.getCurrentKlass();
            ClassConstant constant = t.getClassConstant(index);
            klass.resolveClassConstant(constant);

            String type = constant.getName();

            // init none static fields
            System.out.format("new object: %s\n", type);

            Klass typeKlass = t.getClassLoader().loadClass(type);
            Obj obj = new Obj(type);
            Arrays.stream(typeKlass.getFields())
                    .filter((f) -> ! f.isStatic())
                    .forEach((f) -> {
                        // TODO: init field default value
                        obj.setField(f.getName(), null);
                    });

            Heap heap = t.getHeap();
            heap.add(obj);
            t.getCurrentFrame().getOperandStack().push(obj);
        });

        // anewarray
        instructionNameTable.put((byte) 0xbd, "anewarray");
        instructionTable.put((byte) 0xbd, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;

            // TODO: support non-class constant
            Klass klass = t.getCurrentKlass();
            ClassConstant constant = t.getClassConstant(index);
            klass.resolveClassConstant(constant);
            String type = constant.getName();
            int count = (int) t.getCurrentFrame().getOperandStack().pop();
            ArrayObj ref = new ArrayObj(type, count);
            t.getCurrentFrame().getOperandStack().push(ref);
        });

        // new
        instructionNameTable.put((byte) 0xc7, "ifnonnull");
        instructionTable.put((byte) 0xc7, (t) -> {
            Integer value = (Integer) t.getCurrentFrame().getOperandStack().pop();
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            if (value != null) {
                int index = (indexByte1 << 8) | indexByte2;
                t.incrPc(index - 3);
            }
        });
    }

    public static void doInvoke(Thread t, Klass klass, Method method, Object[] args) {
        // TODO: support native method
        if(method.isNative()) {
            if (klass.getThisClassName().equals("java/lang/System") && method.getName().equals("initProperties")) {
                NativeMethod.systemInitProperties(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/lang/Class") && method.getName().equals("getPrimitiveClass")) {
                NativeMethod.classGetPrimitiveClass(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/lang/Class") && method.getName().equals("desiredAssertionStatus0")) {
                NativeMethod.classDesiredAssertionStatus0(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/lang/Class") && method.getName().equals("registerNatives")) {
                NativeMethod.classRegisterNatives(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/lang/System") && method.getName().equals("registerNatives")) {
                NativeMethod.systemRegisterNatives(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/lang/Float") && method.getName().equals("floatToRawIntBits")) {
                NativeMethod.floatFloatToRawIntBits(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/lang/Double") && method.getName().equals("doubleToRawLongBits")) {
                NativeMethod.doubleDoubleToRawIntBits(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/lang/Double") && method.getName().equals("longBitsToDouble")) {
                NativeMethod.doubleLongBitsToDouble(t, klass, method, args);
            } else if (klass.getThisClassName().equals("sun/misc/VM") && method.getName().equals("initialize")) {
                NativeMethod.vmInitialize(t, klass, method, args);
            } else {
                throw new IllegalStateException("native method not found " + klass.getThisClassName() + " " + method.getName());
            }
            return;
        }

        Frame frame = new Frame(klass, method, t.getPc());
        Object[] localVariable = frame.getLocalVariable();
        for (int i = 0; i < args.length; i++) {
            localVariable[i] = args[i];
        }
        t.push(frame);
        t.resetPc();
    }

    public static Consumer<Thread> getInstruction(byte code) {
        Optional<Consumer<Thread>> instruction = Optional.ofNullable(instructionTable.get(code));
        return instruction.orElseThrow(() -> new IllegalStateException("Unknow instruction: " + String.format("0x%x", code)));
    }

    public static String getInstructionName(byte code) {
        Optional<String> instruction = Optional.ofNullable(instructionNameTable.get(code));
        return instruction.orElseThrow(() -> new IllegalStateException("Unknow instruction: " + String.format("0x%x", code)));
    }

    public static void initClass(Thread t, Klass klass) {
        System.out.format("init class: %s\n", klass.getThisClassName());
        klass.initialize();
        Method klassInitMethod = klass.getClassInitializeMethod();
        if (null != klassInitMethod) {
            doInvoke(t, klass, klassInitMethod, new Object[0]);
        }
        klass.setInitialized(true);
    }
}
