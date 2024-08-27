package algorithms.visualization;

import utils.Constans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class EvolutionHistory {
    private List<EvolutionHistoryElement> history;
    private static int historySizeToSave = 150_000; // needs to be limited due to HEAP SPACE for the output string builder
    private static int numberOfSaves = 5;
    private String folderName;
    private boolean appendToFile;

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public EvolutionHistory() {
        this.appendToFile = false;
        this.history = new ArrayList<>();
    }

    public void addIfNotFull(int generationNumber, double x, double y, int mut, double p1x, double p1y, double p2x, double p2y) {
        if(numberOfSaves > 0) {
            history.add(new EvolutionHistoryElement(generationNumber, x, y, mut, p1x, p1y, p2x, p2y));

            if (history.size() > historySizeToSave) {
                toFile();
                appendToFile = true;
                numberOfSaves--;
            }
        }
    }

    public void toFile() {
        try {
            String fullPath = folderName + File.separator + "ArchHist.csv";
            Files.createDirectories(Paths.get(folderName));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath, appendToFile));
            StringBuilder output = new StringBuilder();

            for(var e: optimiseMut(history)) {
                output.append(e.getGenerationNumber());
                output.append(Constans.delimiter);
                output.append(e.getX());
                output.append(Constans.delimiter);
                output.append(e.getY());
                output.append(Constans.delimiter);
                output.append(e.getMut());
                output.append(Constans.delimiter);
                output.append(e.getP1x());
                output.append(Constans.delimiter);
                output.append(e.getP1y());
                output.append(Constans.delimiter);
                output.append(e.getP2x());
                output.append(Constans.delimiter);
                output.append(e.getP2y());
                output.append("\n");
            }

            writer.write(output.toString());
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static List<EvolutionHistoryElement> optimiseMut(List<EvolutionHistoryElement> evolutionHistory) {
        List<EvolutionHistoryElement> optimisedMut = new ArrayList<EvolutionHistoryElement>();

        HashMap<Integer, HashMap<Integer, Integer>> genClsIdMapping = new HashMap();

        for(var e: evolutionHistory) {
            if(e.getMut() <= 0) {
                optimisedMut.add(new EvolutionHistoryElement(e.getGenerationNumber(), e.getX(), e.getY(), e.getMut(),
                                    e.getP1x(), e.getP1y(), e.getP2x(), e.getP2y()));
                continue;
            }
            int gen = e.getGenerationNumber();

            if(genClsIdMapping.containsKey(gen)) {
                HashMap<Integer, Integer> idMapping = genClsIdMapping.get(gen);
                if(idMapping.containsKey(e.getMut())) {
                    Integer mappedId = idMapping.get(e.getMut());
                    optimisedMut.add(new EvolutionHistoryElement(gen, e.getX(), e.getY(), mappedId, e.getP1x(),
                                                                e.getP1y(), e.getP2x(), e.getP2y()));
                } else {
                    int currentMaxId = Collections.max(idMapping.entrySet(), Map.Entry.comparingByValue()).getValue();
                    idMapping.put(e.getMut(), currentMaxId+1);
                    optimisedMut.add(new EvolutionHistoryElement(gen, e.getX(), e.getY(), currentMaxId+1,
                                                                    e.getP1x(), e.getP1y(), e.getP2x(), e.getP2y()));
                }
            } else {
                genClsIdMapping.put(gen, new HashMap<>());
                genClsIdMapping.get(gen).put(e.getMut(), 1);
                optimisedMut.add(new EvolutionHistoryElement(gen, e.getX(), e.getY(), 1, e.getP1x(), e.getP1y(),
                                                                    e.getP2x(), e.getP2y()));
            }
        }

        return optimisedMut;
    }
}
