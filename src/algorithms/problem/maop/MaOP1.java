package algorithms.problem.maop;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseProblemRepresentation;

import java.util.Arrays;
import java.util.List;

/**
 * Describes a solution for MaOP1 problem.
 * Taken from http://web.mysites.ntu.edu.sg/epnsugan/PublicSite/Shared%20Documents/SWEVO-SI-MOEA-2018/SWEVO-TR.pdf
 */
public class MaOP1 extends BaseProblemRepresentation {

  private double[] genes;
  private double[] scalingFactors;
  private int numObjectives;
  private int n;

  /**
   * Constructor
   *
   * @param m number of objectives
   * @param n number of genes
   */
  public MaOP1(int m, int n) {
    genes = new double[n];
    for (int i = 0; i < n; ++i) {
      genes[i] = 0.5;
    }
    numObjectives = m;
    this.n = n;
    calculateScalingFactors(m);
  }

  public void calculateScalingFactors(int m) {
    scalingFactors = new double[m];
    for (int i = 0; i < m; ++i) {
      scalingFactors[i] = 0.1 + (10 * i);
    }
  }

  @Override
  public BaseProblemRepresentation cloneDeep() {
    MaOP1 maop1 = new MaOP1(scalingFactors.length, genes.length);
    maop1.setGenes(genes.clone());
    maop1.setScalingFactors(scalingFactors.clone());
    return maop1;
  }

  @Override
  public int getNumGenes() {
    return numObjectives - 1;
  }

  public int getNumObjectives() {
    return numObjectives;
  }

  @Override
  public BaseProblemRepresentation buildSolution(List<? extends Number> genes, ParameterSet<? extends Number, ? extends BaseProblemRepresentation> parameters) {
    for (int i = 0; i < genes.size(); ++i) {
      this.genes[i] = genes.get(i).doubleValue();
    }
    for (int i = genes.size(); i < this.genes.length; ++i) {
      this.genes[i] = 0.5;
    }
    this.setHashCode();
    return this;
  }

  @Override
  public void setHashCode() {
    this.hashCode = Arrays.hashCode(genes);
  }

  public double getShape(int i) {
    double result;
    if (i == 0) {
      result = genes[numObjectives - 2];
    } else {
      result = 1 - genes[numObjectives - 1 - i];
    }

    for (int j = numObjectives - i - 1; j >= 0; --j) {
      result *= genes[j];
    }
    return 1 - result;
  }

  public double getDistance(int i) {
    double result = 0;

    for (int j = numObjectives -1; j < n; ++j) {
      result += ( Math.pow(genes[j] - 0.5, 2) + 1 - Math.cos(20 * Math.PI * (genes[j])) );
    }

    return result / n;
  }

  public void setGenes(double[] genes) {
    this.genes = genes;
  }

  public void setScalingFactors(double[] scalingFactors) {
    this.scalingFactors = scalingFactors;
  }

  public double[] getScalingFactors() {
    return scalingFactors;
  }

}
