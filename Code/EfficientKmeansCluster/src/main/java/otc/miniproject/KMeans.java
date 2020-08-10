package otc.miniproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import otc.math.Maths;
import otc.util.MulticoreExecutor;

public class KMeans extends PartitionClustering<double[]> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(KMeans.class);
	double distortion;

	double[][] centroids;

	KMeans() {
	}

	public double getdistortion() {
		return distortion;
	}

	public double[][] centroids() {
		return centroids;
	}

	public int predict(double[] x) {
		double minDist = Double.MAX_VALUE;
		int bestCluster = 0;

		for (int i = 0; i < k; i++) {
			double dist = Maths.squaredDistance(x, centroids[i]);
			if (dist < minDist) {
				minDist = dist;
				bestCluster = i;
			}
		}

		return bestCluster;
	}

	public KMeans(double[][] data, int k) {
		this(data, k, 100,true);
	}

	public KMeans(double[][] data, int k, int maxIter,boolean flag) {
		this(new BBDTree(data), data, k, maxIter,flag);
	}

	KMeans(BBDTree bbd, double[][] data, int k, int maxIter, boolean flag) {
		if (k < 2) {
			throw new IllegalArgumentException("Invalid number of clusters: " + k);
		}

		if (maxIter <= 0) {
			throw new IllegalArgumentException("Invalid maximum number of iterations: " + maxIter);
		}

		int n = data.length;
		int d = data[0].length;

		this.k = k;
		distortion = Double.MAX_VALUE;
		
		if (flag == true)
			y = seed(data, k, ClusteringDistance.EUCLIDEAN);
		else
			y = randomseed(data,k,ClusteringDistance.EUCLIDEAN);
		
		size = new int[k];
		centroids = new double[k][d];

		for (int i = 0; i < n; i++) {
			size[y[i]]++;
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < d; j++) {
				centroids[y[i]][j] += data[i][j];
			}
		}

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < d; j++) {
				centroids[i][j] /= size[i];
			}
		}

		double[][] sums = new double[k][d];
		for (int iter = 1; iter <= maxIter; iter++) {
			double dist = bbd.clustering(centroids, sums, size, y);
			for (int i = 0; i < k; i++) {
				if (size[i] > 0) {
					for (int j = 0; j < d; j++) {
						centroids[i][j] = sums[i][j] / size[i];
					}
				}
			}

			if (distortion <= dist) {
				break;
			} else {
				distortion = dist;
			}
		}
	}

	public KMeans(double[][] data, int k, int maxIter,int runs,boolean flag) {
		if (k < 2) {
			throw new IllegalArgumentException("Invalid number of clusters: " + k);
		}

		if (maxIter <= 0) {
			throw new IllegalArgumentException("Invalid maximum number of iterations: " + maxIter);
		}

		if (runs <= 0) {
			throw new IllegalArgumentException("Invalid number of runs: " + runs);
		}

		BBDTree bbd = new BBDTree(data);

		List<KMeansThread> tasks = new ArrayList<KMeansThread>();

		for (int i = 0; i < runs; i++) {
			tasks.add(new KMeansThread(bbd, data, k, maxIter,flag));
		}

		KMeans best = new KMeans();
		best.distortion = Double.MAX_VALUE;

		try {
			List<KMeans> clusters = MulticoreExecutor.run(tasks);
			for (KMeans kmeans : clusters) {
				if (kmeans.distortion < best.distortion) {
					best = kmeans;
				}
			}
		} catch (Exception ex) {
			logger.error("Failed to run K-Means on multi-core", ex);
			for (int i = 0; i < runs; i++) {
				KMeans kmeans = lloyd(data, k, maxIter);
				if (kmeans.distortion < best.distortion) {
					best = kmeans;
				}
			}
		}

		this.k = best.k;
		this.distortion = best.distortion;
		this.centroids = best.centroids;
		this.y = best.y;
		this.size = best.size;
	}

	static class KMeansThread implements Callable<KMeans> {
		final BBDTree bbd;
		final double[][] data;
		final int k;
		final int maxIter;
		final boolean flag;

		KMeansThread(BBDTree bbd, double[][] data, int k, int maxIter,boolean flag) {
			this.bbd = bbd;
			this.data = data;
			this.k = k;
			this.maxIter = maxIter;
			this.flag = flag;
		}

		public KMeans call() {
			return new KMeans(bbd, data, k, maxIter,flag);
		}
	}
	
	
    public static KMeans lloyd(double[][] data, int k, int maxIter) {
        if (k < 2) {
            throw new IllegalArgumentException("Invalid number of clusters: " + k);
        }

        if (maxIter <= 0) {
            throw new IllegalArgumentException("Invalid maximum number of iterations: " + maxIter);
        }

        int n = data.length;
        int d = data[0].length;
        int[][] nd = new int[k][d]; // The number of non-missing values per cluster per variable.

        double distortion = Double.MAX_VALUE;
        int[] size = new int[k];
        double[][] centroids = new double[k][d];
        int[] y = seed(data, k, ClusteringDistance.EUCLIDEAN_MISSING_VALUES);

        int np = MulticoreExecutor.getThreadPoolSize();
        List<LloydThread> tasks = null;
        if (n >= 1000 && np >= 2) {
            tasks = new ArrayList<LloydThread>(np + 1);
            int step = n / np;
            if (step < 100) {
                step = 100;
            }

            int start = 0;
            int end = step;
            for (int i = 0; i < np-1; i++) {
                tasks.add(new LloydThread(data, centroids, y, start, end));
                start += step;
                end += step;
            }
            tasks.add(new LloydThread(data, centroids, y, start, n));
        }

        for (int iter = 0; iter < maxIter; iter++) {
            Arrays.fill(size, 0);
            for (int i = 0; i < k; i++) {
                Arrays.fill(centroids[i], 0);
                Arrays.fill(nd[i], 0);
            }

            for (int i = 0; i < n; i++) {
                int m = y[i];
                size[m]++;
                for (int j = 0; j < d; j++) {
                    if (!Double.isNaN(data[i][j])) {
                        centroids[m][j] += data[i][j];
                        nd[m][j]++;
                    }
                }
            }

            for (int i = 0; i < k; i++) {
                for (int j = 0; j < d; j++) {
                    centroids[i][j] /= nd[i][j];
                }
            }

            double wcss = Double.NaN;
            if (tasks != null) {
                try {
                    wcss = 0.0;
                    for (double ss : MulticoreExecutor.run(tasks)) {
                        wcss += ss;
                    }
                } catch (Exception ex) {
                    logger.error("Failed to run K-Means on multi-core", ex);

                    wcss = Double.NaN;
                }
            }

            if (Double.isNaN(wcss)) {
                wcss = 0.0;
                for (int i = 0; i < n; i++) {
                    double nearest = Double.MAX_VALUE;
                    for (int j = 0; j < k; j++) {
                        double dist = squaredDistance(data[i], centroids[j]);
                        if (nearest > dist) {
                            y[i] = j;
                            nearest = dist;
                        }
                    }
                    wcss += nearest;
                }              
            }
            
            if (distortion <= wcss) {
                break;
            } else {
                distortion = wcss;
            }
        }

        // In case of early stop, we should recalculate centroids and clusterSize.
        Arrays.fill(size, 0);
        for (int i = 0; i < k; i++) {
            Arrays.fill(centroids[i], 0);
            Arrays.fill(nd[i], 0);
        }

        for (int i = 0; i < n; i++) {
            int m = y[i];
            size[m]++;
            for (int j = 0; j < d; j++) {
                if (!Double.isNaN(data[i][j])) {
                    centroids[m][j] += data[i][j];
                    nd[m][j]++;
                }
            }
        }

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < d; j++) {
                centroids[i][j] /= nd[i][j];
            }
        }

        KMeans kmeans = new KMeans();
        kmeans.k = k;
        kmeans.distortion = distortion;
        kmeans.size = size;
        kmeans.centroids = centroids;
        kmeans.y = y;

        return kmeans;
    }

	static class LloydThread implements Callable<Double> {

		/**
		 * The start index of data portion for this task.
		 */
		final int start;
		/**
		 * The end index of data portion for this task.
		 */
		final int end;
		final double[][] data;
		final int k;
		final double[][] centroids;
		int[] y;

		LloydThread(double[][] data, double[][] centroids, int[] y, int start, int end) {
			this.data = data;
			this.k = centroids.length;
			this.y = y;
			this.centroids = centroids;
			this.start = start;
			this.end = end;
		}

		public Double call() throws Exception {
			double wcss = 0.0;
			for (int i = start; i < end; i++) {
				double nearest = Double.MAX_VALUE;
				for (int j = 0; j < k; j++) {
					double dist = squaredDistance(data[i], centroids[j]);
					if (nearest > dist) {
						y[i] = j;
						nearest = dist;
					}
				}
				wcss += nearest;
			}

			return wcss;
		}
	}
}
