package algorithms.visualization;

import data.Cluster;
import utils.Constans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class EvolutionHistoryElement {
    private static int historySize = 10_000;
    private int generationNumber;
    private double x;
    private double y;
    private int mut = 0;
    private double p1x;
    private double p1y;
    private double p2x;
    private double p2y;

    public static void addIfNotFull(List<EvolutionHistoryElement> history, int generationNumber, double x, double y, int mut, double p1x, double p1y, double p2x, double p2y) {
        if(history.size() < EvolutionHistoryElement.historySize) {
            history.add(new EvolutionHistoryElement(generationNumber, x, y, mut, p1x, p1y, p2x, p2y));
        }
    }
    public EvolutionHistoryElement(int generationNumber, double x, double y, int mut, double p1x, double p1y, double p2x, double p2y) {
        this.generationNumber = generationNumber;
        this.x = x;
        this.y = y;
        this.mut = mut;
        this.p1x = p1x;
        this.p1y = p1y;
        this.p2x = p2x;
        this.p2y = p2y;
    }

    public static void toFile(List<EvolutionHistoryElement> evolutionHistory, String folderName) {
        try {
            String fullPath = folderName + File.separator + "ArchHist.csv";
            Files.createDirectories(Paths.get(folderName));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
            StringBuilder output = new StringBuilder();

            for(var e: evolutionHistory) {
                output.append(e.generationNumber);
                output.append(Constans.delimiter);
                output.append(e.x);
                output.append(Constans.delimiter);
                output.append(e.y);
                output.append(Constans.delimiter);
                output.append(e.mut);
                output.append(Constans.delimiter);
                output.append(e.p1x);
                output.append(Constans.delimiter);
                output.append(e.p1y);
                output.append(Constans.delimiter);
                output.append(e.p2x);
                output.append(Constans.delimiter);
                output.append(e.p2y);
                output.append("\n");
            }

            writer.write(output.toString());
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(int generationNumber) {
        this.generationNumber = generationNumber;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getMut() {
        return mut;
    }

    public void setMut(int mut) {
        this.mut = mut;
    }

    public double getP1x() {
        return p1x;
    }

    public void setP1x(double p1x) {
        this.p1x = p1x;
    }

    public double getP1y() {
        return p1y;
    }

    public void setP1y(double p1y) {
        this.p1y = p1y;
    }

    public double getP2x() {
        return p2x;
    }

    public void setP2x(double p2x) {
        this.p2x = p2x;
    }

    public double getP2y() {
        return p2y;
    }

    public void setP2y(double p2y) {
        this.p2y = p2y;
    }
}
