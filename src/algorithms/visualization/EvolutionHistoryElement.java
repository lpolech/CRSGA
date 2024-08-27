package algorithms.visualization;

import data.Cluster;
import utils.Constans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class EvolutionHistoryElement {
    private int generationNumber;
    private double x;
    private double y;
    private int mut = 0;
    private double p1x;
    private double p1y;
    private double p2x;
    private double p2y;

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
