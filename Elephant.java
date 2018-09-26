import org.vu.contest.ContestEvaluation;

import java.io.File;
import java.util.Comparator;
import java.util.Random;


public class Elephant implements Comparable<Object>{

	static int DIMENSION = 10;
	static double MAX_RANGE = 5.0;

	ContestEvaluation evaluation;
	Random random;

	public double[] values = new double[DIMENSION];

	public Elephant(ContestEvaluation evaluation, Random random) {
		this.evaluation = evaluation;
		this.random = random;

		for (int j=0; j<DIMENSION; j++) {
			values[j] = randomDouble(-MAX_RANGE, MAX_RANGE);
		}
	}

	public Elephant(ContestEvaluation evaluation, Random random, double[] values){
		this.evaluation = evaluation;
		this.random = random;
		this.values = values;
	}

	public double getFitness() {
		return (double) evaluation.evaluate(values);
	}

	public int compareTo(Object e) {
		return Double.compare(((Elephant)e).getFitness(), this.getFitness());
	}

	private double randomDouble(double min, double max){
		double random_num = (random.nextDouble() * ((max - min) + 1)) + min;
		return random_num;
	}

}
