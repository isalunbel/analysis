import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(() -> {
            while (true) {
                String text = generateText("abc", 100);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executor.submit(() -> {
                while (true) {
                    try {
                        String text = null;
                        if (finalI == 0) {
                            text = queueA.take();
                        } else if (finalI == 1) {
                            text = queueB.take();
                        } else {
                            text = queueC.take();
                        }
                        int count = 0;
                        for (int j = 0; j < text.length(); j++) {
                            if (text.charAt(j) == 'a' && finalI == 0) {
                                count++;
                            } else if (text.charAt(j) == 'b' && finalI == 1) {
                                count++;
                            } else if (text.charAt(j) == 'c' && finalI == 2) {
                                count++;
                            }
                        }
                        System.out.println("Символов " + (char) (finalI + 97) + ": " + count);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}