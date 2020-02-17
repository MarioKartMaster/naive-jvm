package runtime;

import clazz.Clazz;
import classfile.reader.ClassFileReader;
import runtime.memory.MethodArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class BootstrapClassLoader {

    private MethodArea methodArea;
    private String[] classpath;

    public BootstrapClassLoader(String[] classpath, MethodArea methodArea) {
        this.methodArea = methodArea;
        this.classpath = classpath;
    }

    // load preparation resolution
    public Clazz loadClass(String className) throws IOException {
        if (methodArea.containsKey(className)) {
            // TODO: check class loader
            return methodArea.get(className);
        }

        Stream<String> filepath = Arrays.stream(classpath).map((p) -> p + className.replace('.', '/') + ".class");
        Stream<String> availFilepaths = filepath.filter((p) -> {
            File f = new File(p);
            return f.exists();
        });
        Optional<String> availFilepath = availFilepaths.findFirst();
        Optional<Clazz> loadedClass = availFilepath.map((p) -> {
            ClassFileReader classFileReader = null;
            try {
                classFileReader = new ClassFileReader(new FileInputStream(p));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Clazz clazz = null;
            try {
                clazz = classFileReader.readClass();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String thisClassName = clazz.getThisClassName();
            String superClassName = clazz.getSuperClassName();

            Optional<String> superClassNameOption = Optional.ofNullable(superClassName);
            superClassNameOption.ifPresent((n) -> {
                try {
                    loadClass(superClassName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            methodArea.put(thisClassName, clazz);
            return clazz;
        });

        return loadedClass.orElse(null);
    }

    public boolean isArrayClass(String cls) {
        return cls.indexOf(0) == '[';
    }

}
