package instruction;

import java.util.*;
import java.util.function.Consumer;

import klass.Klass;
import klass.Method;
import klass.constant.*;
import runtime.ClassLoader;
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

        // iconst_2
        instructionNameTable.put((byte) 0x5, "iconst_2");
        instructionTable.put((byte) 0x05, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            currentFrame.getOperandStack().push(2);
        });

        // iconst_3
        instructionNameTable.put((byte) 0x6, "iconst_3");
        instructionTable.put((byte) 0x06, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            currentFrame.getOperandStack().push(3);
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
            int data = t.readUnsignedShort();
            t.getCurrentFrame().getOperandStack().push((int) ((short) data));
        });

        // ldc
        instructionNameTable.put((byte) 0x12, "ldc");
        instructionTable.put((byte) 0x12, (t) -> {
            int index = t.readCode() & 0xFF;
            Constant constant = t.getConstant(index);
            OperandStack operandStack = t.getCurrentFrame().getOperandStack();
            if (constant instanceof IntegerConstant) {
                int data = ((IntegerConstant) constant).getBytes();
                operandStack.push(data);
            } else if (constant instanceof FloatConstant) {
                float data = ((FloatConstant) constant).getBytes();
                operandStack.push(data);
            } else if (constant instanceof StringConstant) {
                HashMap<String, Obj> stringConstantPool = t.getMemory().getStringConstantPool();
                String s = t.getCurrentKlass().getUtf8ConstantBytes(((StringConstant) constant).getStringIndex());

                ArrayObj objValue = new ArrayObj("char", s.length());
                Obj obj = newObj("java/lang/String", t.getClassLoader());
                obj.setField("value", objValue);

                for (int i = 0; i < s.length(); i++) {
                    objValue.getData()[i] = s.charAt(i);
                }

                stringConstantPool.put(s, obj);
                operandStack.push(obj);
            } else if (constant instanceof KlassConstant) {
                KlassConstant klassConstant = (KlassConstant) constant;
                t.getCurrentKlass().resolveClassConstant(klassConstant);
                Obj obj = new Obj("java/lang/class");
                obj.setField("value", t.getClassLoader().loadClass(klassConstant.getName()));
                operandStack.push(obj);
            } else {
                throw new IllegalStateException("ldc constant type not implemented");
            }
        });

        // ldc2_w
        instructionNameTable.put((byte) 0x14, "ldc2_w");
        instructionTable.put((byte) 0x14, (t) -> {
            int index = t.readUnsignedShort();
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

        // iload
        instructionNameTable.put((byte) 0x15, "iload");
        instructionTable.put((byte) 0x15, (t) -> {
            int index = t.readCode() & 0xff;

            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();

            operandStack.push(localVariable[index]);
        });

        // aload
        instructionNameTable.put((byte) 0x19, "aload");
        instructionTable.put((byte) 0x19, (t) -> {
            int index = t.readCode() & 0xff;

            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();

            operandStack.push(localVariable[index]);
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

        // iload_2
        instructionNameTable.put((byte) 0x1c, "iload_2");
        instructionTable.put((byte) 0x1c, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[2]);
        });

        // iload_3
        instructionNameTable.put((byte) 0x1d, "iload_3");
        instructionTable.put((byte) 0x1d, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[3]);
        });

        // lload_0
        instructionNameTable.put((byte) 0x1e, "lload_0");
        instructionTable.put((byte) 0x1e, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[0]);
        });

        // lload_1
        instructionNameTable.put((byte) 0x1f, "lload_1");
        instructionTable.put((byte) 0x1f, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[1]);
        });

        // lload_2
        instructionNameTable.put((byte) 0x20, "lload_2");
        instructionTable.put((byte) 0x20, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[2]);
        });

        // lload_3
        instructionNameTable.put((byte) 0x21, "lload_3");
        instructionTable.put((byte) 0x21, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[3]);
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

        // aload_2
        instructionNameTable.put((byte) 0x2c, "aload_2");
        instructionTable.put((byte) 0x2c, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            Object data = currentFrame.getLocalVariable()[2];
            currentFrame.getOperandStack().push(data);
        });

        // aload_3
        instructionNameTable.put((byte) 0x2d, "aload_3");
        instructionTable.put((byte) 0x2d, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            Object data = currentFrame.getLocalVariable()[3];
            currentFrame.getOperandStack().push(data);
        });

        // aaload
        instructionNameTable.put((byte) 0x32, "aaload");
        instructionTable.put((byte) 0x32, (t) -> {
            int index = (int) t.getCurrentFrame().getOperandStack().pop();
            ArrayObj arrayRef = ((ArrayObj) t.getCurrentFrame().getOperandStack().pop());
            t.getCurrentFrame().getOperandStack().push(arrayRef.getData()[index]);
        });

        // caload
        instructionNameTable.put((byte) 0x34, "caload");
        instructionTable.put((byte) 0x34, (t) -> {
            int index = (int) t.getCurrentFrame().getOperandStack().pop();
            ArrayObj arrayRef = ((ArrayObj) t.getCurrentFrame().getOperandStack().pop());
            Character ch = (Character) arrayRef.getData()[index];
            t.getCurrentFrame().getOperandStack().push((int) ch.charValue());
        });

        // istore
        instructionNameTable.put((byte) 0x36, "istore");
        instructionTable.put((byte) 0x36, (t) -> {
            int index = t.readCode() & 0xff;
            int value = (int) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getLocalVariable()[index] = value;
        });

        // astore
        instructionNameTable.put((byte) 0x3a, "astore");
        instructionTable.put((byte) 0x3a, (t) -> {
            int index = t.readCode() & 0xff;
            Object arrayRef = t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getLocalVariable()[index] = arrayRef;
        });

        // istore_0
        instructionNameTable.put((byte) 0x3b, "istore_0");
        instructionTable.put((byte) 0x3b, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[0] = data;
        });

        // istore_1
        instructionNameTable.put((byte) 0x3c, "istore_1");
        instructionTable.put((byte) 0x3c, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[1] = data;
        });

        // istore_2
        instructionNameTable.put((byte) 0x3d, "istore_2");
        instructionTable.put((byte) 0x3d, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[2] = data;
        });

        // istore_3
        instructionNameTable.put((byte) 0x3e, "istore_3");
        instructionTable.put((byte) 0x3e, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[3] = data;
        });

        // astore_0
        instructionNameTable.put((byte) 0x4b, "astore_0");
        instructionTable.put((byte) 0x4b, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[0] = data;
        });

        // astore_1
        instructionNameTable.put((byte) 0x4c, "astore_1");
        instructionTable.put((byte) 0x4c, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[1] = data;
        });

        // astore_2
        instructionNameTable.put((byte) 0x4d, "astore_2");
        instructionTable.put((byte) 0x4d, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[2] = data;
        });

        // astore_3
        instructionNameTable.put((byte) 0x4e, "astore_3");
        instructionTable.put((byte) 0x4e, (t) -> {
            Object data = t.getCurrentFrame().getOperandStack().pop();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[3] = data;
        });


        // aastore
        instructionNameTable.put((byte) 0x53, "aastore");
        instructionTable.put((byte) 0x53, (t) -> {
            Object value = t.getCurrentFrame().getOperandStack().pop();
            int index = (int) t.getCurrentFrame().getOperandStack().pop();
            ArrayObj arrayRef = (ArrayObj) t.getCurrentFrame().getOperandStack().pop();
            arrayRef.getData()[index] = value;
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

        // dup_x1
        instructionNameTable.put((byte) 0x5a, "dup_x1");
        instructionTable.put((byte) 0x5a, (t) -> {
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            Object value1 = operandStack.pop();
            Object value2 = operandStack.pop();
            operandStack.push(value1);
            operandStack.push(value2);
            operandStack.push(value1);
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

        // isub
        instructionNameTable.put((byte) 0x64, "isub");
        instructionTable.put((byte) 0x64, (t) -> {
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            int v2 = (int) operandStack.pop();
            int v1 = (int) operandStack.pop();
            int result = v1 - v2;
            operandStack.push(result);
        });

        // imul
        instructionNameTable.put((byte) 0x68, "imul");
        instructionTable.put((byte) 0x68, (t) -> {
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            int v2 = (int) operandStack.pop();
            int v1 = (int) operandStack.pop();
            int result = v1 * v2;
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

        // irem
        instructionNameTable.put((byte) 0x70, "irem");
        instructionTable.put((byte) 0x70, t -> {
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();
            int result = value1 - (value1 / value2) * value2;
            t.getCurrentFrame().getOperandStack().push(result);
        });

        // lshl
        instructionNameTable.put((byte) 0x79, "lshl");
        instructionTable.put((byte) 0x79, t -> {
            int bits = (int) t.getCurrentFrame().getOperandStack().pop();
            long val = (long) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(val << bits);
        });

        // iushr
        instructionNameTable.put((byte) 0x7c, "iushr");
        instructionTable.put((byte) 0x7c, t -> {
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(value1 >>> value2);
        });

        // iand
        instructionNameTable.put((byte) 0x7e, "iand");
        instructionTable.put((byte) 0x7e, t -> {
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(value1 & value2);
        });

        // land
        instructionNameTable.put((byte) 0x7f, "land");
        instructionTable.put((byte) 0x7f, t -> {
            long value1 = (long) t.getCurrentFrame().getOperandStack().pop();
            long value2 = (long) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(value1 & value2);
        });

        // ixor
        instructionNameTable.put((byte) 0x82, "ixor");
        instructionTable.put((byte) 0x82, (t) -> {
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(value1 ^ value2);
        });

        // iinc
        instructionNameTable.put((byte) 0x84, "iinc");
        instructionTable.put((byte) 0x84, (t) -> {
            int index = t.readCode() & 0xFF;
            int konst = t.readCode();
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            localVariable[index] = ((int) localVariable[index]) + konst;
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
            int value = (int) t.getCurrentFrame().getOperandStack().pop();
            int index = t.readUnsignedShort();
            if (value == 0) {
                t.incrPc(index - 3);
            }
        });

        // ifle
        instructionNameTable.put((byte) 0x9e, "ifle");
        instructionTable.put((byte) 0x9e, (t) -> {
            int value = (int) t.getCurrentFrame().getOperandStack().pop();
            int index = t.readUnsignedShort();
            if (value <= 0) {
                t.incrPc(index - 3);
            }
        });

        // ifne
        instructionNameTable.put((byte) 0x9a, "ifne");
        instructionTable.put((byte) 0x9a, (t) -> {
            int index = t.readUnsignedShort();
            Object val = t.getCurrentFrame().getOperandStack().pop();
            if ((int) val != 0) {
                t.incrPc(index - 3);
            }
        });

        // ifge
        instructionNameTable.put((byte) 0x9c, "ifge");
        instructionTable.put((byte) 0x9c, (t) -> {
            int index = t.readUnsignedShort();
            Object val = t.getCurrentFrame().getOperandStack().pop();
            if ((int) val >= 0) {
                t.incrPc(index - 3);
            }
        });

        // ifgt
        instructionNameTable.put((byte) 0x9d, "ifgt");
        instructionTable.put((byte) 0x9d, (t) -> {
            int index = t.readUnsignedShort();
            Object val = t.getCurrentFrame().getOperandStack().pop();
            if ((int) val > 0) {
                t.incrPc(index - 3);
            }
        });

        // if_icmpne
        instructionNameTable.put((byte) 0xa0, "if_icmpne");
        instructionTable.put((byte) 0xa0, (t) -> {
            int index = t.readUnsignedShort();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();

            System.out.println(value2);
            System.out.println(value1);

            if (value1 != value2) {
                t.incrPc(index - 3);
            }
        });

        // if_icmplt
        instructionNameTable.put((byte) 0xa1, "if_icmplt");
        instructionTable.put((byte) 0xa1, (t) -> {
            int index = t.readUnsignedShort();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();

            if (value1 < value2) {
                t.incrPc(index - 3);
            }
        });

        // if_icmpge
        instructionNameTable.put((byte) 0xa2, "if_icmpge");
        instructionTable.put((byte) 0xa2, (t) -> {
            int index = t.readUnsignedShort();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();

            if (value1 >= value2) {
                t.incrPc(index - 3);
            }
        });

        // if_icmpgt
        instructionNameTable.put((byte) 0xa3, "if_icmpgt");
        instructionTable.put((byte) 0xa3, (t) -> {
            int index = t.readUnsignedShort();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();

            if (value1 > value2) {
                t.incrPc(index - 3);
            }
        });

        // if_icmple
        instructionNameTable.put((byte) 0xa4, "if_icmple");
        instructionTable.put((byte) 0xa4, (t) -> {
            int index = t.readUnsignedShort();
            int value2 = (int) t.getCurrentFrame().getOperandStack().pop();
            int value1 = (int) t.getCurrentFrame().getOperandStack().pop();

            if (value1 <= value2) {
                t.incrPc(index - 3);
            }
        });

        // if_acmpeq
        instructionNameTable.put((byte) 0xa5, "if_acmpeq");
        instructionTable.put((byte) 0xa5, (t) -> {
            int index = t.readUnsignedShort();
            Obj value2 = (Obj) t.getCurrentFrame().getOperandStack().pop();
            Obj value1 = (Obj) t.getCurrentFrame().getOperandStack().pop();

            if (value1 == value2) {
                t.incrPc(index - 3);
            }
        });

        // if_acmpne
        instructionNameTable.put((byte) 0xa6, "if_acmpne");
        instructionTable.put((byte) 0xa6, (t) -> {
            int index = t.readUnsignedShort();
            Obj value2 = (Obj) t.getCurrentFrame().getOperandStack().pop();
            Obj value1 = (Obj) t.getCurrentFrame().getOperandStack().pop();

            if (value1 != value2) {
                t.incrPc(index - 3);
            }
        });

        // goto
        instructionNameTable.put((byte) 0xa7, "goto");
        instructionTable.put((byte) 0xa7, (t) -> {
            short index = (short) t.readUnsignedShort();
            t.incrPc(index - 3);
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
            int index = t.readUnsignedShort();

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
            int index = t.readUnsignedShort();

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
            int index = t.readUnsignedShort();

            FieldRefConstant fieldRefConstant = t.getFieldRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(fieldRefConstant);

            Obj objref = (Obj) t.getCurrentFrame().getOperandStack().pop();

            String name = fieldRefConstant.getName();

            Object data = objref.getField(name);
            System.out.println("get field: " + name + " data: " + data);

            t.getCurrentFrame().getOperandStack().push(data);
        });

        // putfield
        instructionNameTable.put((byte) 0xb5, "putfield");
        instructionTable.put((byte) 0xb5, (t) -> {
            int index = t.readUnsignedShort();

            FieldRefConstant fieldRefConstant = t.getFieldRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(fieldRefConstant);

            Object value = t.getCurrentFrame().getOperandStack().pop();
            Obj objref = (Obj) t.getCurrentFrame().getOperandStack().pop();

            String name = fieldRefConstant.getName();
            System.out.println("put field: " + name + " data: " + value);
            objref.setField(name, value);
        });

        // invokevirtual
        instructionNameTable.put((byte) 0xb6, "invokevirtual");
        instructionTable.put((byte) 0xb6, (t) -> {
            int index = t.readUnsignedShort();

            MethodRefConstant methodRefConstant = t.getMethodRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(methodRefConstant);
            Method method = methodRefConstant.getMethod();
            Klass klass = methodRefConstant.getKlass();

            List<String> parameters = method.getParameters();
            Object[] args = t.getCurrentFrame().getOperandStack().popByParametersWithThis(parameters);

            String runtimeKlassType = ((Obj) args[0]).getType();
            klass = t.getClassLoader().loadClass(runtimeKlassType);
            Klass.KlassMethodSearchResult klassMethodSearchResult = klass.searchMethod(method.getName(), method.getDescriptor());

            System.out.format("invoke virtual %s %s %s\n",
                    klassMethodSearchResult.getKlass().getThisClassName(),
                    klassMethodSearchResult.getMethod().getName(),
                    methodRefConstant.getType());

            doInvoke(t, klassMethodSearchResult.getKlass(), klassMethodSearchResult.getMethod(), args);
        });

        // invokespecial
        instructionNameTable.put((byte) 0xb7, "invokespecial");
        instructionTable.put((byte) 0xb7, (t) -> {
            int index = t.readUnsignedShort();

            MethodRefConstant methodRefConstant = t.getMethodRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(methodRefConstant);
            Method method = methodRefConstant.getMethod();
            Klass klass = methodRefConstant.getKlass();

            List<String> parameters = method.getParameters();
            Object[] args = t.getCurrentFrame().getOperandStack().popByParametersWithThis(parameters);

            System.out.format("invoke special %s %s %s\n", methodRefConstant.getKlassName(), methodRefConstant.getName(), methodRefConstant.getType());

            doInvoke(t, klass, method, args);
        });

        // invokestatic
        instructionNameTable.put((byte) 0xb8, "invokestatic");
        instructionTable.put((byte) 0xb8, (t) -> {
            int index = t.readUnsignedShort();

            MethodRefConstant methodRefConstant = t.getMethodRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(methodRefConstant);
            Method method = methodRefConstant.getMethod();
            Klass klass = methodRefConstant.getKlass();

            if (! klass.isInitialized()) {
                t.incrPc(-3);
                initClass(t, klass);
                return;
            }

            List<String> parameters = method.getParameters();
            Object[] args = t.getCurrentFrame().getOperandStack().popByParameters(parameters);

            System.out.format("invoke static %s %s %s\n", methodRefConstant.getKlassName(), methodRefConstant.getName(), methodRefConstant.getType());

            doInvoke(t, klass, method, args);
        });

        // invokeinterface
        instructionNameTable.put((byte) 0xb9, "invokeinterface");
        instructionTable.put((byte) 0xb9, (t) -> {
            int index = t.readUnsignedShort();
            t.readUnsignedShort();

            InterfaceMethodRefConstant interfaceMethodRefConstant = t.getInterfaceMethodRefConstant(index);
            t.getCurrentKlass().resolveRefConstant(interfaceMethodRefConstant);
            Method method = interfaceMethodRefConstant.getMethod();

            List<String> parameters = method.getParameters();
            Object[] args = t.getCurrentFrame().getOperandStack().popByParametersWithThis(parameters);

            String runtimeKlassType = ((Obj) args[0]).getType();
            Klass klass = t.getClassLoader().loadClass(runtimeKlassType);
            Klass.KlassMethodSearchResult klassMethodSearchResult = klass.searchMethod(method.getName(), method.getDescriptor());

            System.out.format("invoke interface %s %s %s\n", interfaceMethodRefConstant.getKlassName(), interfaceMethodRefConstant.getName(), interfaceMethodRefConstant.getType());

            doInvoke(t, klassMethodSearchResult.getKlass(), klassMethodSearchResult.getMethod(), args);
        });

        // new
        instructionNameTable.put((byte) 0xbb, "new");
        instructionTable.put((byte) 0xbb, (t) -> {
            int index = t.readUnsignedShort();

            Klass klass = t.getCurrentKlass();
            KlassConstant constant = t.getClassConstant(index);
            klass.resolveClassConstant(constant);

            String type = constant.getName();

            // init none static fields
            System.out.format("new object: %s\n", type);

            Obj obj = newObj(type, t.getClassLoader());

            Heap heap = t.getHeap();
            heap.add(obj);
            t.getCurrentFrame().getOperandStack().push(obj);
        });

        // anewarray
        instructionNameTable.put((byte) 0xbd, "anewarray");
        instructionTable.put((byte) 0xbd, (t) -> {
            int index = t.readUnsignedShort();

            // TODO: support non-class constant
            Klass klass = t.getCurrentKlass();
            KlassConstant constant = t.getClassConstant(index);
            klass.resolveClassConstant(constant);
            String type = constant.getName();
            int count = (int) t.getCurrentFrame().getOperandStack().pop();
            ArrayObj ref = new ArrayObj(type, count);
            t.getCurrentFrame().getOperandStack().push(ref);
        });

        // arraylength
        instructionNameTable.put((byte) 0xbe, "arraylength");
        instructionTable.put((byte) 0xbe, (t) -> {
            ArrayObj arrayRef = (ArrayObj) t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(arrayRef.getLength());
        });

        // checkcast
        instructionNameTable.put((byte) 0xc0, "checkcast");
        instructionTable.put((byte) 0xc0, (t) -> {
            // TODO: to be implement
            int index = t.readUnsignedShort();
        });

        // instanceof
        instructionNameTable.put((byte) 0xc1, "instanceof");
        instructionTable.put((byte) 0xc1, (t) -> {
            // TODO: to be implement
            int index = t.readUnsignedShort();
            Object objRef = t.getCurrentFrame().getOperandStack().pop();
            t.getCurrentFrame().getOperandStack().push(0);
        });

        // ifnull
        instructionNameTable.put((byte) 0xc6, "ifnull");
        instructionTable.put((byte) 0xc6, (t) -> {
            int index = t.readUnsignedShort();
            Object value = t.getCurrentFrame().getOperandStack().pop();
            if (value == null) {
                t.incrPc(index - 3);
            }
        });

        // ifnonnull
        instructionNameTable.put((byte) 0xc7, "ifnonnull");
        instructionTable.put((byte) 0xc7, (t) -> {
            int index = t.readUnsignedShort();
            Object value = t.getCurrentFrame().getOperandStack().pop();
            if (value != null) {
                t.incrPc(index - 3);
            }
        });
    }

    public static void doInvoke(Thread t, Klass klass, Method method, Object[] args) {
        String a = "";
        for (int i = 0; i < args.length; i++) {
            a += args[i];
            a += "\t";
        }
        System.out.println("args: " + a);
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
            } else if (klass.getThisClassName().equals("sun/misc/Unsafe") && method.getName().equals("registerNatives")) {
                NativeMethod.unsafeRegisterNatives(t, klass, method, args);
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
            } else if (klass.getThisClassName().equals("java/lang/Object") && method.getName().equals("hashCode")) {
                NativeMethod.objectHashCode(t, klass, method, args);
            } else if (klass.getThisClassName().equals("java/io/FileDescriptor") && method.getName().equals("initIDs")) {
                NativeMethod.fileDescriptorInitIDs(t, klass, method, args);
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

    public static Obj newObj(String type, ClassLoader classLoader) {
        Klass typeKlass = classLoader.loadClass(type);
        Obj obj = new Obj(type);

        while (typeKlass != null) {
            Arrays.stream(typeKlass.getFields())
                    .filter((f) -> !f.isStatic())
                    .forEach((f) -> {
                        // TODO: init field default value
                        String descriptor = f.getDescriptor();
                        Object val;
                        if (descriptor.equals("B")) {
                            val = (byte) 0;
                        } else if (descriptor.equals("C")) {
                            val = (char) 0;
                        } else if (descriptor.equals("D")) {
                            val = (double) 0;
                        } else if (descriptor.equals("F")) {
                            val = (float) 0;
                        } else if (descriptor.equals("I")) {
                            val = (int) 0;
                        } else if (descriptor.equals("J")) {
                            val = (long) 0;
                        } else if (descriptor.equals("S")) {
                            val = (short) 0;
                        } else if (descriptor.equals("Z")) {
                            val = false;
                        } else if (descriptor.startsWith("L") || descriptor.startsWith("[")) {
                            val = null;
                        } else {
                            throw new IllegalStateException("invalid field descriptro: " + descriptor);
                        }
                        obj.setField(f.getName(), val);
                    });
            String superClassName = typeKlass.getSuperClassName();
            if (superClassName != null) {
                typeKlass = classLoader.loadClass(superClassName);
            } else {
                typeKlass = null;
            }
        }
        return obj;
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
