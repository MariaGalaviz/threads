import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContadorLineas extends SimpleFileVisitor<Path> {

    private final ExecutorService executorService;
    private final LineCounter.LineCountAccumulator accumulator;

    public ContadorLineas(int numberOfThreads) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.accumulator = new LineCounter.LineCountAccumulator();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String name = file.toAbsolutePath().toString();

        if (name.toLowerCase().endsWith(".txt")) {
            executorService.submit(new LineCounter(name, accumulator));
        }
        return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.err.printf("No se puede procesar: %30s - %s%n", file.toString(), exc.getMessage());
        return super.visitFileFailed(file, exc);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public LineCounter.LineCountAccumulator getAccumulator() {
        return accumulator;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.exit(2);
        }

        Path startingDir = Paths.get(args[0]);
        ContadorLineas contadorLineas = new ContadorLineas(4);

        try {
            Files.walkFileTree(startingDir, contadorLineas);
        } finally {
            contadorLineas.shutdown();
        }

        while (!contadorLineas.executorService.isTerminated()) {

        }

        LineCounter.LineCountAccumulator accumulator = contadorLineas.getAccumulator();
        System.out.printf("Total de líneas: %,d%n", accumulator.getTotalLines());
        System.out.printf("Total de caracteres: %,d%n", accumulator.getTotalCharacters());
    }
}