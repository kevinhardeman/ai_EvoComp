import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.lang.Object;
import java.util.ArrayList;
import java.util.*;

public class player40 implements ContestSubmission
{

	static int DIMENSION = 11;
	static double MAX_RANGE = 5.0;

	Random random;
	ContestEvaluation evaluation;

	private int evaluations_limit;
    Elephant average = null;

	public player40()
	{
		random = new Random();
	}

	public void setSeed(long seed)
	{
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
	}

	public void run() {
		// Algorithm Parameters
		int population_size = 100;
		int tournament_size = 5;

		double mutation_probability = 0.2;
		double max_sigma = 5.0/3.0;
        
        double linearblend = 0.5; //Used to determine how much we value novelty over fitness. Lower linearblend means less fitnessbased selection.
        double linearblend_delta = 0.05;

		Elephant[] population = initiate(population_size, max_sigma);

        // List to keep track of all novel behaviour. This has to be implemented later to reward novelty.
        Elephant[] averagesList = new Elephant[0];
        average = calcAverageElephant(population);

  		for (int i=0; i<evaluations_limit; i++){

			Elephant[] children = new Elephant[population_size];

			for (int j=0; j<population_size; j++) {

				// Select Parents from population
				Elephant[] parents = select(population, 2, tournament_size, average, linearblend);

				// Create child by mating parents and mutating result
				children[j] = mutate(mate(parents[0], parents[1], 3), mutation_probability);
			}

			population = concatenate(population, children);
            average = calcAverageElephant(population);
            averagesList = append(averagesList, average);
			population = select(population, population_size, tournament_size, average, linearblend);

			Arrays.sort(population);
            //TODO: Slowly moves the linearblend function to 1. 
            if(linearblend < 1){
                linearblend += linearblend_delta;
            }

            // Statistics Printout
            double max_novelty = 0.0;
            for (Elephant e : population) {
            	double abs_novelty = Math.abs(e.getNovelty(average));
            	if (abs_novelty > max_novelty) max_novelty = abs_novelty;
            }

            double mean_sigma = 0.0;
            for (Elephant e : population) {
            	mean_sigma += e.getValues()[DIMENSION-1];
            }
            mean_sigma /= population.length;


            System.out.print(i);
            System.out.print(":\t");
            System.out.print(population[0].getFitness());
            System.out.print("\t\t");
            System.out.print(max_novelty);
            System.out.print("\t\t");
            System.out.print(mean_sigma);
            System.out.println();
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
	public Elephant[] select(Elephant[] population, int output_size, int tournament_size, Elephant average, double linearblend) {
		Elephant[] output = new Elephant[output_size];
		// Define number of tournaments
		for (int i = 0; i < output_size; i++){

			Elephant[] tournament = new Elephant[tournament_size];

			Elephant bestElephant = null;
			// Play tournament
			for (int j = 0; j < tournament_size; j++){
				Elephant currentElephant = population[random.nextInt(population.length)];
				if (bestElephant == null || currentElephant.getScore(linearblend, average) > bestElephant.getScore(linearblend, average)){
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


    public Elephant calcAverageElephant(Elephant[] elephants){
        //Creates a list of values which are used to define the average elephant.
        double[] values = new double[DIMENSION];
        for(int z = 0; z < elephants.length; z++){
            double[] eleValue = elephants[z].getValues();
            for(int i = 0; i < eleValue.length; i++){
                values[i] = values[i] + eleValue[i];
            }
        }
        
        for(int k=0; k<DIMENSION; k++){
            values[k] = values[k] / elephants.length;
            }
        Elephant averageDist = new Elephant(evaluation, values, null,null); 
        return averageDist;
    }


    public Elephant[] nearestNeighbours(Elephant main, Elephant[] elephants, Elephant[] averageList, int k){
    	Elephant[] neighbours = new Elephant[k];
    	Elephant[] total = concatenate(elephants, averageList);

    	for(int a = 0; a < total.length; a++){
    		double z = main.getDistance(total[a]);
    		for(int b = 0; b < k; b++){
    			if (z < neighbours[b].getDistance(total[a])){
    				for(int c = (k - 1); c == 0 + b; c--){
    					neighbours[c] = neighbours[c+1];
    				}
    				total[a] = neighbours[b];

    				}
    			}
    		}
    	return neighbours;
    	}
    }


