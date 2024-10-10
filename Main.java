import java.io.*;

public class Main {
    public static void main(String[] args) {
        File sourceFile1 = new File("source1.txt"); // файл источник 1
        File sourceFile2 = new File("source2.txt"); // файл источник 2
        File destFile1 = new File("dest1.txt"); // файл для копирования 1
        File destFile2 = new File("dest2.txt"); // файл для копирования 2

        // Последовательное копирование
        measureSequentialCopy(sourceFile1, destFile1);
        measureSequentialCopy(sourceFile2, destFile2);

        // Параллельное копирование
        measureParallelCopy(sourceFile1, destFile1, sourceFile2, destFile2);

        System.out.println("Копирование завершено!");
    }

    private static void measureSequentialCopy(File source, File destination) {
        long startTime = System.nanoTime();
        try {
            copyFile(source, destination);
            double duration = (System.nanoTime() - startTime) / 1_000_000.0; // миллисекунды
            System.out.println("Последовательное копирование " + source.getName() + " завершено за " + duration + " мс");
        } catch (IOException e) {
            System.err.println("Ошибка при копировании файла " + source.getName() + ": " + e.getMessage());
        }
    }

    private static void measureParallelCopy(File source1, File dest1, File source2, File dest2) {
        Thread thread1 = new Thread(() -> {
            measureSequentialCopy(source1, dest1);
        });

        Thread thread2 = new Thread(() -> {
            measureSequentialCopy(source2, dest2);
        });

        long startTime = System.nanoTime();

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
            double duration = (System.nanoTime() - startTime) / 1_000_000.0; // миллисекунды
            System.out.println("Параллельное копирование завершено за " + duration + " мс");
        } catch (InterruptedException e) {
            System.err.println("Ошибка при ожидании потоков: " + e.getMessage());
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        try (InputStream inputStream = new FileInputStream(source);
             OutputStream outputStream = new FileOutputStream(dest)) {

            byte[] buffer = new byte[4096]; // размер буфера увеличен
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
