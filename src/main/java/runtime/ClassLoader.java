package runtime;

import klass.ArrayKlass;
import klass.Klass;
import classfile.reader.ClassFileReader;
import runtime.memory.MethodArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ClassLoader {

    private MethodArea methodArea;
    private String[] classpath;

    public ClassLoader(String[] classpath, MethodArea methodArea) {
        this.methodArea = methodArea;
        this.classpath = classpath;
    }

    // load preparation resolution
    public Klass loadClass(String className) {
        if (methodArea.containsKey(className)) {
            // TODO: check classloader
            return methodArea.get(className);
        }

        if (className.startsWith("[")) {
            return loadArrayClass(className);
        }
        System.out.println("loading class: " + className);

        Stream<String> filepath = Arrays.stream(classpath).map((p) -> p + className.replace('.', '/') + ".class");
        Stream<String> availFilepaths = filepath.filter((p) -> {
            File f = new File(p);
            return f.exists();
        });
        Optional<String> availFilepath = availFilepaths.findFirst();
        Optional<Klass> loadedClass = availFilepath.map((p) -> {
            ClassFileReader classFileReader = null;
            try {
                classFileReader = new ClassFileReader(new FileInputStream(p));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Klass klass = null;
            try {
                klass = classFileReader.readClass(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            methodArea.put(className, klass);

            // Using lazy resolute
//            clazz.resolve();
            return klass;
        });

        return loadedClass.orElseThrow(() -> new NoClassDefFoundError("failed to load class: " + className));
    }

    public ArrayKlass loadArrayClass(String className) {
        System.out.println("loading class: " + className);
        if (className.charAt(1) == '[') {
            loadArrayClass(className.substring(1));
        }

        ArrayKlass arrayKlass = new ArrayKlass(className, this);
        methodArea.put(className, arrayKlass);
        return arrayKlass;
    }

    public boolean isArrayClass(String cls) {
        return cls.indexOf(0) == '[';
    }

}
