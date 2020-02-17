package instruction;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import clazz.Clazz;
import clazz.Method;
import clazz.constant.MethodRefConstant;
import runtime.memory.Frame;
import runtime.memory.Thread;

public class Instruction {
    private static Map<Byte, Consumer<Thread>> instructionTable = new HashMap<>();

    static {
        // iconst_1
        instructionTable.put((byte) 0x04, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            currentFrame.getOperandStack().push(1);
        });
        // iload_0
        instructionTable.put((byte) 0x1a, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[0]);
        });
        // iload_1
        instructionTable.put((byte) 0x1b, (t) -> {
            Object[] localVariable = t.getCurrentFrame().getLocalVariable();
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            operandStack.push(localVariable[1]);
        });
        // iadd
        instructionTable.put((byte) 0x60, (t) -> {
            Stack<Object> operandStack = t.getCurrentFrame().getOperandStack();
            Integer v1 = (Integer) operandStack.pop();
            Integer v2 = (Integer) operandStack.pop();
            int result = v1 + v2;
            operandStack.push(result);
        });
        // ireturn
        instructionTable.put((byte) 0xac, (t) -> {
            Frame currentFrame = t.getCurrentFrame();
            Object result = currentFrame.getOperandStack().pop();
            Stack<Frame> stack = t.getStack();
            stack.pop();
            t.getCurrentFrame().getOperandStack().push(result);
        });
        instructionTable.put((byte) 0xb8, (t) -> {
            byte indexByte1 = t.readCode();
            byte indexByte2 = t.readCode();
            int index = (indexByte1 << 8) | indexByte2;
            MethodRefConstant methodRefConstant = (MethodRefConstant) t.getConstant(index);
            t.getClazz().resolveMethodRefConstant(methodRefConstant);

            String className = methodRefConstant.getClassName();
            String methodName = methodRefConstant.getName();
            String methodType = methodRefConstant.getType();

            Clazz newClazz;
            try {
                newClazz = t.getBootstrapClassLoader().loadClass(className);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

            Method newMethod = newClazz.getMethod(methodName, methodType);
            newClazz.resolveMethod(newMethod);

            Frame frame = new Frame(newClazz, newMethod);
            Frame currentFrame = t.getCurrentFrame();

            Object[] localVariable = frame.getLocalVariable();
            // TODO: if parameter type is L or D popLong() popDouble()
            List<String> args = newMethod.getParameters();
            for (int i = 0; i < args.size(); i++) {
                localVariable[i] = currentFrame.getOperandStack().pop();
            }

            t.getStack().push(frame);

            // TODO: support restore PC after return
            t.resetPc();
        });
    }

    public static Consumer<Thread> getInstruction(byte code) {
        Optional<Consumer<Thread>> instruction = Optional.ofNullable(instructionTable.get(code));
        return instruction.orElseThrow(() -> new IllegalStateException("Unknow instruction: " + String.format("0x%x", code)));
    }
}
