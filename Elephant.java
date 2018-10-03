import org.vu.contest.ContestEvaluation;


import java.io.File;
import java.util.Comparator;
import java.util.Random;
import java.util.Arrays;


public class Elephant implements Comparable<Object>{

	static int DIMENSION = 11;
	static double MAX_RANGE = 5.0;

	private Elephant mother = null;
	private Elephant father = null;

	private double[] values;
	private double fitness = -1.0;
	private double novelty = -1.0;

	private ContestEvaluation evaluation;

	public Elephant(ContestEvaluation evaluation, Random random, double max_sigma) {
		this.evaluation = evaluation;

		this.values = new double[DIMENSION];
		for (int j=0; j<DIMENSION; j++) {
			this.values[j] = randomDouble(random, -MAX_RANGE, MAX_RANGE);
		}

		this.values[DIMENSION-1] = randomDouble(random, 0, max_sigma);
	}

	public Elephant(ContestEvaluation evaluation, double[] values, Elephant mother, Elephant father){
		this.evaluation = evaluation;
		this.values = values;

		this.mother = mother;
		this.father = father;
	}

	public Elephant getMother() {
		return mother;
	}

	public Elephant getFather() {
		return father;
	}

	public double[] getValues() {
		return values;
	}

	public double getFitness() {
		if (fitness < 0) updateFitness(this.values);
		return fitness;
	}

	public double getNovelty(Elephant average) {
		updateNovelty(this.mother, this.father, average);
		return novelty;
	}

	public double getDistance(Elephant other) {
		double distance = 0.0;
		double[] otherValues = other.getValues();
		for(int i = 0; i< values.length -1; i++){
			distance = Math.abs(values[i]) + Math.abs(otherValues[i]);
		}
		return distance;
	}

	public double getScore(double p, Elephant average) {
		return ((getFitness()) * p + (1.0 - p) * (getNovelty(average)));
	}

	public int compareTo(Object e) {
		return Double.compare(((Elephant)e).getFitness(), this.getFitness());
	}

	private void updateFitness(double[] values) {
		try { this.fitness = (double) this.evaluation.evaluate(Arrays.copyOf(values, DIMENSION - 1)); }
		catch(NullPointerException e) {
			throw new RuntimeException("Exceeded computational budget!");
		}
	}

	private void updateNovelty(Elephant mother, Elephant father, Elephant average) {
       //Takes the average Elephant to determine it's distance from it. This is then in turn used to determine the "novelty"
       //TODO: IMPLEMENT LIST OF PREVIOUS NOVEL BEHAVIOUR to determine current novelty.
        novelty = getDistance(average);
        novelty = novelty / (MAX_RANGE * 2);
        
        this.novelty = novelty; 
	}

	private static double randomDouble(Random random, double min, double max){
		return (random.nextDouble() * (max - min)) + min;
	}




}
