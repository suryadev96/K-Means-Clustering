package otc.miniproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import otc.math.Maths;
import otc.sort.QuickSort;
import otc.stat.GaussianDistribution;

public class GMeans extends KMeans implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(GMeans.class);


	 
	public GMeans(double[][] data, int kmax) {
		if (kmax < 2) {
			throw new IllegalArgumentException("Invalid parameter kmax = " + kmax);
		}

		int n = data.length;
		int d = data[0].length;

		k = 1;
		size = new int[k];
		size[0] = n;
		y = new int[n];
		centroids = new double[k][d];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < d; j++) {
				centroids[0][j] += data[i][j];
			}
		}

		for (int j = 0; j < d; j++) {
			centroids[0][j] /= n;
		}

		distortion = 0.0;
		for (int i = 0; i < n; i++) {
			distortion += Maths.squaredDistance(data[i], centroids[0]);
		}
		logger.info(String.format("G-Means distortion with %d clusters: %.5f", k, distortion));

		BBDTree bbd = new BBDTree(data);
		while (k < kmax) {
			ArrayList<double[]> centers = new ArrayList<>();
			double[] score = new double[k];
			KMeans[] kmeans = new KMeans[k];

			for (int i = 0; i < k; i++) {
		
				if (size[i] < 25) {
					continue;
				}

				double[][] subset = new double[size[i]][];
				for (int j = 0, l = 0; j < n; j++) {
					if (y[j] == i) {
						subset[l++] = data[j];
					}
				}

				kmeans[i] = new KMeans(subset, 2, 100, 4,true);

				double[] v = new double[d];
				for (int j = 0; j < d; j++) {
					v[j] = kmeans[i].centroids[0][j] - kmeans[i].centroids[1][j];
				}
				double vp = Maths.dot(v, v);
				double[] x = new double[size[i]];
				for (int j = 0; j < x.length; j++) {
					x[j] = Maths.dot(subset[j], v) / vp;
				}

				// normalize to mean 0 and variance 1.
				Maths.standardize(x);

				score[i] = AndersonDarling(x);
				logger.info(String.format("Cluster %3d\tAnderson-Darling adjusted test statistic: %3.4f", i, score[i]));
			}

			int[] index = QuickSort.sort(score);
			for (int i = 0; i < k; i++) {
				if (score[index[i]] <= 1.8692) {
					centers.add(centroids[index[i]]);
				}
			}

			int m = centers.size();
			for (int i = k; --i >= 0;) {
				if (score[i] > 1.8692) {
					if (centers.size() + i - m + 1 < kmax) {
						logger.info("Split cluster {}", index[i]);
						centers.add(kmeans[index[i]].centroids[0]);
						centers.add(kmeans[index[i]].centroids[1]);
					} else {
						centers.add(centroids[index[i]]);
					}
				}
			}

			// no more split.
			if (centers.size() == k) {
				break;
			}

			k = centers.size();
			double[][] sums = new double[k][d];
			size = new int[k];
			centroids = new double[k][];
			for (int i = 0; i < k; i++) {
				centroids[i] = centers.get(i);
			}

			distortion = Double.MAX_VALUE;
			for (int iter = 0; iter < 100; iter++) {
				double newDistortion = bbd.clustering(centroids, sums, size, y);
				for (int i = 0; i < k; i++) {
					if (size[i] > 0) {
						for (int j = 0; j < d; j++) {
							centroids[i][j] = sums[i][j] / size[i];
						}
					}
				}

				if (distortion <= newDistortion) {
					break;
				} else {
					distortion = newDistortion;
				}
			}

			logger.info(String.format("G-Means distortion with %d clusters: %.5f%n", k, distortion));
		}
	}


	private static double AndersonDarling(double[] x) {
		int n = x.length;
		Arrays.sort(x);

		for (int i = 0; i < n; i++) {
			x[i] = GaussianDistribution.getInstance().cdf(x[i]);
			if (x[i] == 0)
				x[i] = 0.0000001;
			if (x[i] == 1)
				x[i] = 0.9999999;
		}

		double A = 0.0;
		for (int i = 0; i < n; i++) {
			A -= (2 * i + 1) * (Maths.log(x[i]) + Maths.log(1 - x[n - i - 1]));
		}

		A = A / n - n;
		A *= (1 + 4.0 / n - 25.0 / (n * n));

		return A;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("G-Means distortion: %.5f%n", distortion));
		sb.append(String.format("Clusters of %d data points of dimension %d:%n", y.length, centroids[0].length));
		for (int i = 0; i < k; i++) {
			int r = (int) Math.round(1000.0 * size[i] / y.length);
			sb.append(String.format("%3d\t%5d (%2d.%1d%%)%n", i, size[i], r / 10, r % 10));
		}

		return sb.toString();
	}
}
