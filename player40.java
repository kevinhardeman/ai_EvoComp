import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;


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

	public void run()
	{
		// Run your algorithm here

        int evals = 0;
				int population_size = 100;
				double[][] parents = new double[population_size][10];
       	for (int i = 0; i < population_size; i++){
					double[] parent = new double[10];
					for(int j=0; j<10; j++){
						parent[j] = randomNumber(-5.0,5.0);
					}
					parents[i] = parent;
       	}



				for (int i = 0; i < 10; i++){
					Double fitness_parent = (double) evaluation_.evaluate(parents[i]);
					System.out.println("Fitness for parent " + i + "    " + fitness_parent);
				}


        while(evals<evaluations_limit_){
            // Select parents
            // Apply crossover / mutation operators
            double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};

            // Check fitness of unknown fuction
            Double fitness = (double) evaluation_.evaluate(child);

						Parent parent1 = new Parent(child, fitness, 1);
            evals++;
            // Select survivors
        }

	}

	public double randomNumber(double min, double max){
		double random_num = (Math.random() * ((max - min) + 1)) + min;
		return random_num;
	}
}
