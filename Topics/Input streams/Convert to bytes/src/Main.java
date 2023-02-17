import java.io.InputStream;

class Main {
    public static void main(String[] args) throws Exception {
        InputStream inputStream = System.in;

        int currentByte = inputStream.read();
        while (currentByte != -1) {
            System.out.print(currentByte);
            currentByte = inputStream.read();
        }
        System.out.println();
    }
}