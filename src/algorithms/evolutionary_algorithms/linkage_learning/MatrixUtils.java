package algorithms.evolutionary_algorithms.linkage_learning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MatrixUtils {
    public static void toFile(String toStringVal, double[][] matrix, String matrixName, String fileName, String path) {
        StringBuilder fileContent = new StringBuilder();
        fileContent.append(matrixName + "\n");
        fileContent.append(toStringVal);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + File.separator + fileName));
            writer.write(fileContent.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
