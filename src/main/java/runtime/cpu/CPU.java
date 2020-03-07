package runtime.cpu;

import instruction.Instruction;
import runtime.memory.OperandStack;
import runtime.memory.Thread;

import java.util.function.Consumer;

public class CPU {
    public void run(Thread thread) {
        while (true) {
            if (thread.getCurrentFrame() == null)  {
                break;
            }

            byte code = thread.readCode();

            String className = thread.getCurrentKlass().getThisClassName();
            String methodName = thread.getCurrentMethod().getName();
            String methodDescriptor = thread.getCurrentMethod().getDescriptor();
            String codeName = Instruction.getInstructionName(code);
            System.out.format("class: %s, method: %s%s, cp: %d, %s 0x%x\n", className, methodName, methodDescriptor, thread.getPc() - 1, codeName, code);

            Consumer<Thread> instruction = Instruction.getInstruction(code);
            try {
                instruction.accept(thread);
            } catch (Exception e) {
                OperandStack operandStack = thread.getCurrentFrame().getOperandStack();
                System.out.println("operand tack:");
                String out = "";
                for (int i = 0; i < operandStack.size(); i++) {
                    out += (operandStack.get(i) + "\t");
                }
                System.out.println(out);

                Object[] localVariable = thread.getCurrentFrame().getLocalVariable();
                System.out.println("local variable:");
                for (int i = 0; i < localVariable.length; i++) {
                    System.out.println(i + "\t" + localVariable[i]);
                }
                throw e;
            }
        }
    }
}
