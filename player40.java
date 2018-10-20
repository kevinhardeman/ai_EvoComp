import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.lang.Object;
import java.util.ArrayList;
import java.util.*;
import java.io.*;

public class player40 implements ContestSubmission
{
	// Print Fitness values for every Population Iteration
	static boolean GET_TEST_RESULTS = false;

	// Perform Completely Random Baseline
	static boolean COMPLETELY_RANDOM = false;

	// Function Dimensionality and Range (-MAX_RANGE, MAX_RANGE)
	static int DIMENSION = 11;
	static double MAX_RANGE = 5.0;

	// Random Object, for Stochastic Reasons
	Random random;

	// Pointer to Evaluation Object 
	ContestEvaluation evaluation;

	// << Algorithm Parameters >> //

	// Maximum Number of Evaluations
	int evaluations_limit;

	// Number of Elephants in Population at any given time
	int population_size = 211;

	// Number of Elephants cosidered in every Tournament Selection
	int tournament_size = 2;
	
	// Number of Crossover Points for every DNA Combination
	int crossover_points = 5;

	// Probability of Mutation per Allele
	double mutation_probability = 0.35;

	// Maximum Mutation Sigma at Initialization Time
	double max_sigma = 1.712238291064383;

	// Muation Sigma Learning Rate
	double learning_rate = 0.3;

	// Minimum Novelty for an Elephant to be added to Novelty List
	double novelty_treshold = 0.0;

	// Used to determine how much we value novelty over fitness.
	// Lower linearblend means less Fitness-based selection.
    double linearblend = 1.0;

    // How much linearblend changes each Population Iteration
    double linearblend_delta = 0.1;

    // Number of Neighbours to consider when calculating Novelty
    int nearestNeighbours = 5;


	public player40() {
		random = new Random();
	}

	public void setSeed(long seed) {
		// Set seed of algortihms random process
		random.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		this.evaluation = evaluation;
		// Get evaluation properties
		Properties props = evaluation.getProperties();
		// Get evaluation limit
		evaluations_limit = Integer.parseInt(props.getProperty("Evaluations"));

		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
		boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
		boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
		boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		if (hasStructure) { // Schaffers
			population_size = 250;
			tournament_size = 2;
			crossover_points = 5;
			mutation_probability = 0.35;
			max_sigma = 2.0;
			learning_rate = 0.23394022657643965;
			novelty_treshold = 0.0;
			linearblend = 1.0;
			linearblend_delta = 0.0;
			nearestNeighbours = 2;
		}
		else if (isMultimodal) { // Katsuura
			population_size = 208;
			tournament_size = 2;
			crossover_points = 2;
			mutation_probability = 0.35;
			max_sigma = 1.0971574946931633;
			learning_rate = 0.23423232947955983;
			novelty_treshold = 2.736732536773115;
			linearblend = 0.1;
			linearblend_delta = 0.09877776913042888;
			nearestNeighbours = 6;
		}
		else { // Bent Cigar
			population_size = 98;
			tournament_size = 3;
			crossover_points = 4;
			mutation_probability = 0.35;
			max_sigma = 1.7874596076741391;
			learning_rate = 0.5703412535711101;
			novelty_treshold = 0.0;
			linearblend = 1.0;
			linearblend_delta = 0.0;
			nearestNeighbours = 2;
		}

		// Get Hyperparameters from command line
		if (System.getProperty("population_size") != null)
			population_size = Integer.parseInt(System.getProperty("population_size"));

		if (System.getProperty("tournament_size") != null)
			tournament_size = Integer.parseInt(System.getProperty("tournament_size"));

		if (System.getProperty("crossover_points") != null)
			crossover_points = Integer.parseInt(System.getProperty("crossover_points"));

		if (System.getProperty("mutation_probability") != null)
			mutation_probability = Double.parseDouble(System.getProperty("mutation_probability"));

		if (System.getProperty("max_sigma") != null)
			max_sigma = Double.parseDouble(System.getProperty("max_sigma"));

		if (System.getProperty("learning_rate") != null)
			learning_rate = Double.parseDouble(System.getProperty("learning_rate"));

		if (System.getProperty("novelty_treshold") != null)
			novelty_treshold = Double.parseDouble(System.getProperty("novelty_treshold"));

		if (System.getProperty("linearblend") != null)
			linearblend = Double.parseDouble(System.getProperty("linearblend"));

		if (System.getProperty("linearblend_delta") != null)
			linearblend_delta = Double.parseDouble(System.getProperty("linearblend_delta"));

		if (System.getProperty("nearestNeighbours") != null)
			nearestNeighbours = Integer.parseInt(System.getProperty("nearestNeighbours"));
	}

	public void run() {

		// Run Random Baseline
		//  1. Get random point in 10 Dimensional Space
		//  2. Evaluate Point
		//  3. Profit! (Or not, it is really bad)
		if (COMPLETELY_RANDOM) {
			double max_score = 0.0;
			while (true) {
				double[] totally_random_point = new double[DIMENSION-1];
				for (int j=0; j<DIMENSION-1; j++) {
					totally_random_point[j] = randomDouble(-MAX_RANGE, MAX_RANGE);
					double score = (double) evaluation.evaluate(totally_random_point);

					if (score > max_score){
						max_score = score;
						System.out.println(max_score);
					}
				}
			}
		}

		// Run Actual Evolutionary Algorithm
		else {

			// Initialize Population with Random DNA & Random Mutation Sigma
			Elephant[] population = initiate(population_size, max_sigma);

			// list to keep track of all previously novel elephants. 
			// The first initiate is just to avoid nullpointerexceptions...
	        Elephant[] noveList = initiate(1, max_sigma); 
	  		
	  		while (true) {

	  			// Initialize Children
				Elephant[] children = new Elephant[population_size];

				// Create n=population_size Children
				for (int j=0; j<population_size; j++) {

					// Select Parents from population
					Elephant[] parents = select(population, 2, tournament_size, noveList, linearblend, nearestNeighbours);

					// Create child by mating parents and mutating result
					children[j] = mutate(mate(parents[0], parents[1], crossover_points), mutation_probability);
				}

				// Update noveList
				Elephant[] totaList = concatenate(population, noveList);
				for (Elephant child : children){
					if(child.getNovelty(totaList, nearestNeighbours) > novelty_treshold) {
						append(noveList, child);
						append(totaList, child);
					}
				}

				// Select new Population from old Population, Children and NoveList
				population = concatenate(population, children);
				population = select(population, population_size, tournament_size, noveList, linearblend, nearestNeighbours);
				Arrays.sort(population);

	            // Slowly Move linearblend to 1
	            if(linearblend < 1) linearblend += linearblend_delta;

				if (GET_TEST_RESULTS){
					// Print Fitness of Best Elephant each iteration
					System.out.println(population[population.length-1].getFitness());
				}
			}
		}
	}

	public Elephant[] initiate(int population_size, double max_sigma) {

		// Intialize Elephant Population at Random points within function range
		Elephant[] parents = new Elephant[population_size];
		for (int i = 0; i < population_size; i++)
			parents[i] = new Elephant(evaluation, random, max_sigma);

		// Sort Elephants based on Fitness
		Arrays.sort(parents);

		return parents;
	}

	public Elephant mutate(Elephant elephant, double probability) {
		// Fixed chance 'mutation_probability' per allele to mutate to random value within MAX_RANGE

		// Get DNA (+ Sigma) from Elephant
		double[] values = elephant.getValues();

		// Mutate sigma
		double mutated_sigma = values[DIMENSION-1] * Math.exp(learning_rate * random.nextGaussian());
		values[DIMENSION-1] = mutated_sigma;

		// Mutate Alleles based on Sigma
		for (int i=0; i<DIMENSION-1; i++) {
			if (random.nextDouble() < probability) {
				double mutated_value;

				// Mutate Value, but make sure it is in function range
				do mutated_value = values[i] + mutated_sigma * random.nextGaussian();
				while (mutated_value < -MAX_RANGE || mutated_value > MAX_RANGE);  // New values should still be in function ranges

				values[i] = mutated_value;
			}
		}

		// Return Mutated Elephant
		return new Elephant(evaluation, values, elephant.getMother(), elephant.getFather());
	}

	public Elephant mate(Elephant mother, Elephant father) {
		// Uniform Crossover (Probability per Allele to be either mother/father)

		// Create new DNA
		double[] dna = new double[DIMENSION];

		for (int i=0; i<DIMENSION; i++) {
			// Set DNA at current index to mothers/fathers randomly
			dna[i] = (random.nextDouble() >= 0.5) ? mother.getValues()[i] : father.getValues()[i];
		}

		// A baby-elephant is born!;
		return new Elephant(evaluation, dna, mother, father);
	}

	public Elephant mate(Elephant mother, Elephant father, int n) {
		// N-Point Crossover

		// Create new DNA
		double[] dna = new double[DIMENSION];

		// Create Random Crossover points
		int[] crossover_points = new int[n];
		for (int i=0; i<n; i++) crossover_points[i] = random.nextInt(DIMENSION);
		Arrays.sort(crossover_points);

		int crossover_index = 0;
		for (int i=0; i<DIMENSION; i++) {

			// Check wether current index is bigger or equal to the current crossover index -> increment crossover index
			if (crossover_index < crossover_points.length && i >= crossover_points[crossover_index]) crossover_index++;

			// Set DNA at current index to mothers/fathers based on crossover index
			dna[i] = (crossover_index % 2 == 0) ? mother.getValues()[i] : father.getValues()[i];
		}

		// A baby-elephant is born!;
		return new Elephant(evaluation, dna, mother, father);
	}

	public Elephant[] select(Elephant[] population, int output_size, int tournament_size, Elephant[] averagesList, double linearblend, int nearestNeighbours) {
		// Tournament Selection Implementation

		Elephant[] total = concatenate(population, averagesList);
		Elephant[] output = new Elephant[output_size];

		// Run 'output_size' tournaments
		for (int i = 0; i < output_size; i++){
			Elephant bestElephant = null;

			// Run Tournament
			for (int j = 0; j < tournament_size; j++){
				Elephant currentElephant = population[random.nextInt(population.length)];
				if (bestElephant == null || currentElephant.getScore(linearblend, total, nearestNeighbours) > bestElephant.getScore(linearblend, total, nearestNeighbours)){
					bestElephant = currentElephant;
				}
			}
			output[i] = bestElephant;
		}

		return output;
	}

	public double randomDouble(double min, double max){
		// Get Random double x: min <= x <= max

		return (random.nextDouble() * ((max - min))) + min;
	}

	public Elephant[] concatenate(Elephant[] a, Elephant[] b) {
		// Concatenate two arrays

		Elephant[] c = new Elephant[a.length + b.length];

		int index = 0;

		for (int i=0; i<a.length; i++) {
			c[index] = a[i];
			index++;
		}

		for (int i=0; i<b.length; i++) {
			c[index] = b[i];
			index++;
		}

    	return c;
	}

    public Elephant[] append(Elephant[] a, Elephant b) {
    	// Append Elephant to Elephant Array

        Elephant[] c = new Elephant[a.length + 1];
        int index = 0;
        for (int i=0; i<a.length; i++) {
			c[index] = a[i];
			index++;
		}
        c[index]  = b;
        return c;
    }


}
