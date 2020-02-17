package testpkgname;

public class TestClass {

    public static void main(String[] args) {
        int result = calculate(1, 1);
        System.out.println(result);
    }

    public static int calculate(int i, int j) {
        return i + j;
    }
}
