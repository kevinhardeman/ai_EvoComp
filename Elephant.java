import java.io.File;

// TODO: betere objectnaam? :D
public class Elephant {
	public double[] values = new double[10];
	public double fitness = 0.0;
	public int index = 0; // check if needed

	public Elephant(double[] values, int index){
		this.values = values;
		this.index = index;
	}

}
