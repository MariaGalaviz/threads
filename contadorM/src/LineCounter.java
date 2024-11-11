import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LineCounter implements Runnable {

    private String name;
    private final LineCountAccumulator accumulator;

    public LineCounter(String name, LineCountAccumulator accumulator) {
        this.name = name;
        this.accumulator = accumulator;
    }

    @Override
    public void run() {
        int contadorLineas = 0;
        int contadorCaracteres = 0;

        try (BufferedReader in = new BufferedReader(new FileReader(name))) {
            String linea;
            while ((linea = in.readLine()) != null) {
                contadorLineas++;
                contadorCaracteres += linea.length();
            }
        } catch (IOException e) {
            System.err.printf("Error reading file %s: %s%n", name, e.getMessage());
            return;
        }


        accumulator.addLines(contadorLineas);
        accumulator.addCharacters(contadorCaracteres);
    }

    public static class LineCountAccumulator {
        private int totalLines = 0;
        private int totalCharacters = 0;

        public synchronized void addLines(int lines) {
            totalLines += lines;
        }

        public synchronized void addCharacters(int characters) {
            totalCharacters += characters;
        }

        public synchronized int getTotalLines() {
            return totalLines;
        }

        public synchronized int getTotalCharacters() {
            return totalCharacters;
        }
    }
}