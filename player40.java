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
	ContestEvaluation evaluation_;

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
		evaluation_ = evaluation;

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
		Elephant[] parents = new Elephant[population_size];
		for (int i = 0; i < population_size; i++){
			double[] functionValues = new double[DIMENSION];
			for(int j=0; j<DIMENSION; j++){
				functionValues[j] = randomNumber(-MAX_RANGE, MAX_RANGE);
			}
			Elephant currentElephant = new Elephant(functionValues, i);
			currentElephant.fitness = (double) evaluation_.evaluate(currentElephant.values);
			parents[i] = currentElephant;
		}
		Arrays.sort(parents); // sort parents based on fitness
		//while(evals<evaluations_limit_){
		while(evals<1){
		// Select parents
		// Apply crossover / mutation operators

			// Mutation
			// Fixed chance 'mutation_probability' per allele to mutate to random value within MAX_RANGE
			for (Elephant e : parents) {
				for (int i=0; i<DIMENSION; i++) {
					if (random.nextDouble() < mutation_probability) {
						e.values[i] = randomNumber(-MAX_RANGE, MAX_RANGE);
					}
				}
			}

			// Crossover
			// 1. Pick top two parents (change this!)
			// 2. Pick random crossover point [0-9]
			// 3. Create new Elephant!
			Elephant parent0 = parents[0];
			Elephant parent1 = parents[1];

			int crossover_point = random.nextInt() % DIMENSION;

			double[] dna = new double[DIMENSION];

			for (int i=0; i<DIMENSION; i++) {
				dna[i] = (i > crossover_point) ? parent0.values[i] : parent1.values[i];
			}

			//A baby-elephant is born!;
			Elephant child = new Elephant(dna, 1000);


			// Check fitness of unknown fuction
			Double fitness = (double) evaluation_.evaluate(child.values);

			for(int i = 0; i < child.values.length; i++){
					System.out.println(child.values[i]);
			}

			//Select survivors;
			// TODO


			System.out.println(fitness);
			evals++;
		}
	}

	public double randomNumber(double min, double max){
		double random_num = (random.nextDouble() * ((max - min) + 1)) + min;
		return random_num;
	}
}
