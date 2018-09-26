import org.vu.contest.ContestEvaluation;

import java.io.File;
import java.util.Comparator;
import java.util.Random;


public class Elephant implements Comparable<Object>{

	static int DIMENSION = 10;
	static double MAX_RANGE = 5.0;

	private Elephant mother = null;
	private Elephant father = null;

	private double[] values;
	private double fitness = 0.0;
	private double novelty = 0.0;

	private ContestEvaluation evaluation;

	public Elephant(ContestEvaluation evaluation, Random random) {
		this.evaluation = evaluation;

		this.values = new double[DIMENSION];
		for (int j=0; j<DIMENSION; j++) {
			this.values[j] = randomDouble(random, -MAX_RANGE, MAX_RANGE);
		}

		updateFitness(this.values);
	}

	public Elephant(ContestEvaluation evaluation, double[] values, Elephant mother, Elephant father){
		this.evaluation = evaluation;
		this.values = values;

		this.mother = mother;
		this.father = father;

		updateFitness(this.values);
		updateNovelty(this.values, this.mother, this.father);
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
		if (fitness < 0) updateFitness();
		return fitness;
	}

	public double getNovelty() {
		if (novelty < 0) updateNovelty();
		return novelty;
	}

	public double getScore(int p) {

		return (getFitness() * p + (1 - p) *getNovelty());
	}

	public int compareTo(Object e) {
		return Double.compare(((Elephant)e).getFitness(), this.getFitness());
	}

	private void updateFitness(double[] values) {
		try { this.fitness = (double) this.evaluation.evaluate(values); }
		catch(NullPointerException e) {
			throw new RuntimeException("Exceeded computational budget!");
		}
	}

	private void updateNovelty(double[] values, Elephant mother, Elephant father) {
		this.novelty = 0.0; // TODO: Implement
	}

	private static double randomDouble(Random random, double min, double max){
		return (random.nextDouble() * (max - min)) + min;
	}

}
