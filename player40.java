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

	private int evaluations_limit_;

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
		evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
		boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
		boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
		boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));
	}

	public void run() {


		// Algorithm Parameters
		int population_size = 100;
		double mutation_probability = 0.01;

		// Initialization of population
		//The genotype is stored in parents[i].values."
		//The fitness of every parent is calculated and saved in parents[i].fitness"

		int evals = 0;

		Elephant[] parents = initiate(population_size);

		while(evals<1){

			// Select parents - TODO: Select More and Better
			Elephant mother = parents[0];
			Elephant father = parents[1];
			
			// Apply crossover / mutation operators

			// Mutation
			for (Elephant elephant : parents) {
				elephant = mutate(elephant, mutation_probability);
			}

			// Crossover
			Elephant child = mate(mother, father);

			// TODO: Select survivors;
			Arrays.sort(parents);

			for (Elephant e : parents) {
				System.out.println(e.getFitness());
			}

			evals++;
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
		
		for (int i=0; i<DIMENSION; i++) {
			if (random.nextDouble() < probability) {
				elephant.values[i] = randomDouble(-MAX_RANGE, MAX_RANGE);
			}
		}
		return elephant;
	}

	public Elephant mate(Elephant mother, Elephant father) {
		// Crossover using random crossover point

		double[] dna = new double[DIMENSION];
		int crossover_point = random.nextInt() % DIMENSION;

		for (int i=0; i<DIMENSION; i++) {
			dna[i] = (i > crossover_point) ? mother.values[i] : father.values[i];
		}

		//A baby-elephant is born!;
		return new Elephant(evaluation, random, dna);
	}

	public double randomDouble(double min, double max){
		double random_num = (random.nextDouble() * ((max - min) + 1)) + min;
		return random_num;
	}
}
