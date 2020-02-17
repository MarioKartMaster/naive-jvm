package classfile.reader;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Reader extends DataInputStream {

    public Reader(InputStream in) {
        super(in);
    }

    public int readU2() throws IOException {
        return readUnsignedShort();
    }

    public int readU4() throws IOException {
        return readInt();
    }

}
