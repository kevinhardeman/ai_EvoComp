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

	static int DIMENSION = 10;
	static double MAX_RANGE = 5.0;

	Random random;
	ContestEvaluation evaluation;

	private int evaluations_limit;

	public player40()
	{
		random = new Random();
	}

	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		// random.setSeed(seed);
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
		int population_size = 1000;
		int tournament_size = 5;
		double mutation_probability = 0.01;

		Elephant[] population = initiate(population_size);

		for (int i=0; i<100; i++){

			Elephant[] children = new Elephant[population_size];

			for (int j=0; j<population_size; j++) {

				// Select Parents from population
				Elephant[] parents = select(population, 2, tournament_size);

				Elephant child = new Elephant(evaluation, random);

				// Create child by mating parents and mutating result
				children[j] = mutate(mate(parents[0], parents[1]), mutation_probability);
			}

			population = concatenate(population, children);
			population = select(population, population_size, tournament_size);

			Arrays.sort(population);

			System.out.println(population[0].getFitness());
		}
	}

	public Elephant[] initiate(int population_size) {
		Elephant[] parents = new Elephant[population_size];

		for (int i = 0; i < population_size; i++) {
			parents[i] = new Elephant(evaluation, random);
		}

		Arrays.sort(parents); // sort parents based on fitness

		return parents;
	}

	public Elephant mutate(Elephant elephant, double probability) {
		// Fixed chance 'mutation_probability' per allele to mutate to random value within MAX_RANGE
		
		double[] values = elephant.getValues();

		for (int i=0; i<DIMENSION; i++) {
			if (random.nextDouble() < probability) {
				values[i] = randomDouble(-MAX_RANGE, MAX_RANGE);
			}
		}

		return new Elephant(evaluation, values, elephant.getMother(), elephant.getFather());
	}

	public Elephant mate(Elephant mother, Elephant father) {
		// Crossover using random crossover point

		double[] dna = new double[DIMENSION];
		int crossover_point = random.nextInt() % DIMENSION;

		for (int i=0; i<DIMENSION; i++) {
			dna[i] = (i > crossover_point) ? mother.getValues()[i] : father.getValues()[i];
		}

		//A baby-elephant is born!;
		return new Elephant(evaluation, dna, mother, father);
	}

	// Implementation for tournament selection (Kevin)
	public Elephant[] select(Elephant[] population, int output_size, int tournament_size) {
		Elephant[] output = new Elephant[output_size];
		// Define number of tournaments
		for (int i = 0; i < output_size; i++){
			
			Elephant[] tournament = new Elephant[tournament_size];

			Elephant bestElephant = null;
			// Play tournament
			for (int j = 0; j < tournament_size; j++){
				Elephant currentElephant = population[random.nextInt(population.length)];

				if (bestElephant == null || currentElephant.getFitness() > bestElephant.getFitness()){
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
}
