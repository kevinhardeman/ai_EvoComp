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

	// Print Debug Information
	static boolean DEBUG = false;

	// Print Fitness values for every Population Iteration
	static boolean GET_TEST_RESULTS = true;

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
	double learning_rate = 0.01;

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
		// random.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
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
	}

	public void run() {
		if (COMPLETELY_RANDOM) {

			double max_score = 0.0;

			while (true) {
				double[] totally_random_point = new double[DIMENSION-1];
				for (int j=0; j<DIMENSION-1; j++)
					totally_random_point[j] = randomDouble(-MAX_RANGE, MAX_RANGE);
					double score = (double) evaluation.evaluate(totally_random_point);

					if (score > max_score){
						max_score = score;
						System.out.println(max_score);
					}
			}
		}
		else {

			Elephant[] population = initiate(population_size, max_sigma);

			Elephant[] totaList = new Elephant[0]; //totaList
	        Elephant[] noveList = initiate(1, max_sigma); //list to keep track of all previously novel elephants. the first initiate is just te avoid nullpointerexceptions (could be implemented better)
	  		for (int i=0; i<evaluations_limit; i++){
	  			int noveListElephant = 0;
				Elephant[] children = new Elephant[population_size];

				for (int j=0; j<population_size; j++) {
					// Select Parents from population
					Elephant[] parents = select(population, 2, tournament_size, noveList, linearblend, nearestNeighbours);

					// Create child by mating parents and mutating result
					children[j] = mutate(mate(parents[0], parents[1], crossover_points), mutation_probability);
				}

				totaList = concatenate(population, noveList);
				for(int e = 0; e < children.length; e++){
					if(children[e].getNovelty(totaList, nearestNeighbours) > novelty_treshold){
						append(noveList, children[e]);
						append(totaList, children[e]);
					}
				}

				population = concatenate(population, children);
				population = select(population, population_size, tournament_size, noveList, linearblend, nearestNeighbours);
				Arrays.sort(population);

	            //TODO: Slowly moves the linearblend function to 1.
	            if(linearblend < 1){
	                linearblend += linearblend_delta;
	            }

	            // Debug Printout
	            if (DEBUG) {
		            /*
		            double mean_novelty = 0.0;
		            for (Elephant e : population) {
		            	mean_novelty += Math.abs(e.getNovelty(population, nearestNeighbours));
		            }
		            mean_novelty /= population.length;
					*/
		            double mean_sigma = 0.0;
		            for (Elephant e : population) {
		            	mean_sigma += e.getValues()[DIMENSION-1];
		            }
		            mean_sigma /= population.length;

		            System.out.print(i);
		            System.out.print(":\t");
		            System.out.print(population[0].getFitness());
		            System.out.print("\t\t");
		            System.out.print(population[noveListElephant].getNovelty(totaList, nearestNeighbours));
		            System.out.print("\t\t");
		            System.out.print(mean_sigma);
		            System.out.println();
						}
						if (GET_TEST_RESULTS){
							// print fitness of best elephant
							System.out.println(population[population.length-1].getFitness());
							}
				}
		}
	}

	public Elephant[] initiate(int population_size, double max_sigma) {
		Elephant[] parents = new Elephant[population_size];

		for (int i = 0; i < population_size; i++) {
			parents[i] = new Elephant(evaluation, random, max_sigma);
		}

		Arrays.sort(parents); // sort parents based on fitness

		return parents;
	}

	public Elephant mutate(Elephant elephant, double probability) {
		// Fixed chance 'mutation_probability' per allele to mutate to random value within MAX_RANGE

		double[] values = elephant.getValues();

		for (int i=0; i<DIMENSION; i++) {
			if (random.nextDouble() < probability) {
				// TODO: Gaussian should be Elephant-specific attribute
				double mutated_value;

				do mutated_value = values[i] + values[DIMENSION-1] * random.nextGaussian();
				while (mutated_value < -MAX_RANGE || mutated_value > MAX_RANGE);  // New values should still be in function ranges

				values[i] = mutated_value;
			}
		}

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

	// Implementation for tournament selection (Kevin)
	public Elephant[] select(Elephant[] population, int output_size, int tournament_size, Elephant[] averagesList, double linearblend, int nearestNeighbours) {
		Elephant[] total = concatenate(population, averagesList);
		Elephant[] output = new Elephant[output_size];
		// Define number of tournaments
		for (int i = 0; i < output_size; i++){

			Elephant[] tournament = new Elephant[tournament_size];

			Elephant bestElephant = null;
			// Play tournament
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
		return (random.nextDouble() * ((max - min))) + min;
	}

	public Elephant[] concatenate(Elephant[] a, Elephant[] b) {
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
