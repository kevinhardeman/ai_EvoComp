import java.io.File;
import java.util.Comparator;


public class Elephant implements Comparable<Object>{
	public double[] values = new double[10];
	public double fitness = 0.0;
	public int index = 0; // check if needed

	public Elephant(double[] values, int index){
		this.values = values;
		this.index = index;
	}

	public double getFitness(){
		return this.fitness;
	}

	public int compareTo(Object e) {
		Elephant elephant = (Elephant)e;
		return Double.compare(elephant.getFitness(), this.fitness);
	}

}
