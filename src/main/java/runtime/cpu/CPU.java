package runtime.cpu;

import instruction.Instruction;
import runtime.memory.Thread;

import java.util.function.Consumer;

public class CPU {
    public void run(Thread thread) {
        while (true) {
            byte code = thread.readCode();
            Consumer<Thread> instruction = Instruction.getInstruction(code);
            instruction.accept(thread);
        }
    }
}
