package classfile;

import classfile.reader.ClassFileReader;
import classfile.view.ClassFileView;
import clazz.Clazz;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = new DataInputStream(new FileInputStream(args[0]));
        Clazz clazz = new ClassFileReader(inputStream).readClass();
        ClassFileView classFileView = new ClassFileView(clazz);
        classFileView.show();
    }
}
