package otc.miniproject;

import java.util.Random;

import otc.math.Maths;

public abstract class PartitionClustering<T> implements Clustering<T> {

	protected int k;

	protected int[] y;

	protected int[] size;

	public int getNumClusters() {
		return k;
	}

	public int[] getClusterLabel() {
		return y;
	}

	public int[] getClusterSize() {
		return size;
	}

	static double squaredDistance(double[] x, double[] y) {
		int n = x.length;
		int m = 0;
		double dist = 0.0;

		for (int i = 0; i < n; i++) {
			if (!Double.isNaN(x[i]) && !Double.isNaN(y[i])) {
				m++;
				double d = x[i] - y[i];
				dist += d * d;
			}
		}

		if (m == 0) {
			dist = Double.MAX_VALUE;
		} else {
			dist = n * dist / m;
		}

		return dist;
	}
	
	public static int[] randomseed(double[][] data, int k, ClusteringDistance distance) {
		int n = data.length;
		int[] y = new int[n];
		Random rand = new Random();
		boolean isChosen[] = new boolean[n];
		int index = rand.nextInt(n);
		double[] centroid = data[index];
		isChosen[index] = true;

		double[] d = new double[n];
		for (int i = 0; i < n; i++) {
			d[i] = Double.MAX_VALUE;
		}

		for (int j = 1; j < k; j++) {
			for (int i = 0; i < n; i++) {
				double dist = 0.0;
				switch (distance) {
				case EUCLIDEAN:
					dist = Maths.squaredDistance(data[i], centroid);
					break;
				case EUCLIDEAN_MISSING_VALUES:
					dist = squaredDistance(data[i], centroid);
					break;
				}
				if (dist < d[i]) {
					d[i] = dist;
					y[i] = j - 1;
				}
			}
			index = rand.nextInt(n);
			if (isChosen[index] == true) {
				index = rand.nextInt(n);
				continue;
			}	
			else {
				isChosen[index] = true;
				centroid = data[index];
			}
				
		}

		for (int i = 0; i < n; i++) {
			double dist = 0.0;
			switch (distance) {
			case EUCLIDEAN:
				dist = Maths.squaredDistance(data[i], centroid);
				break;
			case EUCLIDEAN_MISSING_VALUES:
				dist = squaredDistance(data[i], centroid);
				break;
			}
			if (dist < d[i]) {
				d[i] = dist;
				y[i] = k - 1;
			}
		}
		return y;
	}

	public static int[] seed(double[][] data, int k, ClusteringDistance distance) {
		int n = data.length;
		int[] y = new int[n];
		Random rand = new Random();
		double[] centroid = data[rand.nextInt(n)];

		double[] d = new double[n];
		for (int i = 0; i < n; i++) {
			d[i] = Double.MAX_VALUE;
		}

		for (int j = 1; j < k; j++) {
			for (int i = 0; i < n; i++) {
				double dist = 0.0;
				switch (distance) {
				case EUCLIDEAN:
					dist = Maths.squaredDistance(data[i], centroid);
					break;
				case EUCLIDEAN_MISSING_VALUES:
					dist = squaredDistance(data[i], centroid);
					break;
				}
				if (dist < d[i]) {
					d[i] = dist;
					y[i] = j - 1;
				}
			}

			double cutoff = Math.random() * Maths.sum(d);
			double cost = 0.0;
			int index = 0;
			for (; index < n; index++) {
				cost += d[index];
				if (cost >= cutoff) {
					break;
				}
			}
			centroid = data[index];
		}

		for (int i = 0; i < n; i++) {
			double dist = 0.0;
			switch (distance) {
			case EUCLIDEAN:
				dist = Maths.squaredDistance(data[i], centroid);
				break;
			case EUCLIDEAN_MISSING_VALUES:
				dist = squaredDistance(data[i], centroid);
				break;
			}
			if (dist < d[i]) {
				d[i] = dist;
				y[i] = k - 1;
			}
		}
		return y;
	}

}
