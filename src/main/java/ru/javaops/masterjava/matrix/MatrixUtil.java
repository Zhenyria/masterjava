package ru.javaops.masterjava.matrix;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        List<Future<Integer>> futures =
                IntStream.range(0, matrixSize)
                        .parallel()
                        .mapToObj(index ->
                                completionService.submit(
                                        () -> computeMatrixPart(matrixA, matrixB, matrixC, matrixSize, index), index))
                        .collect(Collectors.toList());

        while (!futures.isEmpty()) {
            Future<Integer> completionFuture = completionService.take();
            futures.remove(completionFuture);
        }

        return matrixC;
    }

    public static int[][] singleThreadMultiply(final int[][] matrixA, final int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        try {
            for (int i = 0; ; i++) {
                computeMatrixPart(matrixA, matrixB, matrixC, matrixSize, i);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void computeMatrixPart(int[][] matrixA,
                                          int[][] matrixB,
                                          int[][] matrixC,
                                          int matrixSize,
                                          int index) {
        int[] matrixBColumn = new int[matrixSize];
        int[] matrixARow;

        for (int i = 0; i < matrixSize; i++) {
            matrixBColumn[i] = matrixB[i][index];
        }

        for (int i = 0; i < matrixSize; i++) {
            matrixARow = matrixA[i];

            int sum = 0;
            for (int j = 0; j < matrixSize; j++) {
                sum += matrixARow[j] * matrixBColumn[j];
            }
            matrixC[index][i] = sum;
        }
    }
}
