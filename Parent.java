// TODO: betere objectnaam? :D
public class Parent {
	public double[] values = new double[10];
	public double fitness = 0.0;
	public int index = 0; // check if needed

	public Parent(double[] values, double fitness, int index){
		this.values = values;
		this.fitness = fitness;
		this.index = index;
	}

}
