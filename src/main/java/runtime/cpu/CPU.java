package runtime.cpu;

import instruction.Instruction;
import runtime.memory.Thread;

import java.util.function.Consumer;

public class CPU {
    public void run(Thread thread) {
        while (true) {
            if (thread.getCurrentFrame() == null)  {
                break;
            }

            Byte code = thread.readCode();

            String className = thread.getCurrentKlass().getThisClassName();
            String methodName = thread.getCurrentMethod().getName();
            String methodDescriptor = thread.getCurrentMethod().getDescriptor();
            String codeName = Instruction.getInstructionName(code);
            System.out.format("class: %s, method: %s%s, %s 0x%x\n", className, methodName, methodDescriptor, codeName, code);

            Consumer<Thread> instruction = Instruction.getInstruction(code);
            instruction.accept(thread);
        }
    }
}
