import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;
import java.lang.Object;

public class player40 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
	private int evaluations_limit_;

	public player40()
	{
		rnd_ = new Random();
	}

	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);
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
		// Initialization of population
		//The genotype is stored in parents[i].values."
		//The fitness of every parent is calculated and saved in parents[i].fitness"
		int evals = 0;
		int population_size = 100;
		Elephant[] parents = new Elephant[population_size];
		for (int i = 0; i < population_size; i++){
			double[] functionValues = new double[10];
			for(int j=0; j<10; j++){
				functionValues[j] = randomNumber(-5.0,5.0);
			}
			Elephant currentElephant = new Elephant(functionValues, i);
			currentElephant.fitness = (double) evaluation_.evaluate(currentElephant.values);
			parents[i] = currentElephant;
		}

		//while(evals<evaluations_limit_){
		while(evals<1){
		// Select parents
		// Apply crossover / mutation operators
			//Mutation
			// TODO

			//Crossover";
			double[] dna1 = Arrays.copyOfRange(parents[0].values, 0, 5);
			double[] dna2 = Arrays.copyOfRange(parents[1].values, 5, 10);
			double[] dna = new double[10];
			for(int i = 0; i<5; i++){
				dna[i] = dna1[i];
			}
			for(int i = 5; i<10; i++){
				dna[i] = dna2[i-5];
			}

			//A baby-elephant is born!;
			Elephant child = new Elephant(dna, 1000);

			// Check fitness of unknown fuction
			Double fitness = (double) evaluation_.evaluate(child.values);

			for(int i = 0; i < child.values.length; i++){
					System.out.println(child.values[i]);
			}
			System.out.println(fitness);

			evals++;

			//Select survivors;
			// TODO
		}
	}

	public double randomNumber(double min, double max){
		double random_num = (Math.random() * ((max - min) + 1)) + min;
		return random_num;
	}
}
