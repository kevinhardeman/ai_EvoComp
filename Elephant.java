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

	public double getNovelty(Elephant[] total, int k) {
		if(novelty < 0) updateNovelty(total, k);
		return novelty;
	}

	//Calculates euclidean distance between this elephant and another.
	public double getDistance(Elephant other) {
		double distance = 0.0;
		double[] otherValues = other.getValues();
		for(int i = 0; i< values.length -1; i++){
			distance = Math.abs(values[i]) + Math.abs(otherValues[i]);
		}
		return distance;
	}

	public double getScore(double p, Elephant[] total, int k) {
		return ((getFitness()) * p + (1.0 - p) * (getNovelty(total, k)));
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


	//Finds the k nearest neighbours and determines the total distance of those three. 
	//This is then divide by k to determine the average distance towards neighbours and then normalised in such a way that it is representable like fitness.
	//the total list is a list of the current population + a list of previously novel behaviour. 
	private void updateNovelty(Elephant[] total, int k){
    	double distance = 0.0;
    	double z = 0.0;
    	Elephant[] neighbours = new Elephant[k];

    	for(int a = 0; a < total.length -1; a++){
    		z = this.getDistance(total[a]);
    		for(int b = 0; b < k; b++){
    			if (neighbours[b] == null || z < this.getDistance(neighbours[b])){
    				for(int c = k; c == b; c--){
    					neighbours[c - 1] = neighbours[c];
    				}
    				neighbours[b] = total[a];

    				}
    			}
    		}
    	for(int u = 0; u < k; u++){
    		distance = distance + this.getDistance(neighbours[u]);
    	}
    	distance = (distance / k) / (MAX_RANGE * 2);
    
    	this.novelty = distance;
    	}

	private static double randomDouble(Random random, double min, double max){
		return (random.nextDouble() * (max - min)) + min;
	}




}
