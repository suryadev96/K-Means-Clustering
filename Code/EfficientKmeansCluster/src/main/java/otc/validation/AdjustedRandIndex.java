package otc.validation;

import otc.math.Maths;

public class AdjustedRandIndex implements ClusterMeasure {
	@Override
	public double measure(int[] y1, int[] y2) {
		if (y1.length != y2.length) {
			throw new IllegalArgumentException(
					String.format("The vector sizes don't match: %d != %d.", y1.length, y2.length));
		}

		// Get # of non-zero classes in each solution
		int n = y1.length;

		int[] label1 = Maths.unique(y1);
		int n1 = label1.length;

		int[] label2 = Maths.unique(y2);
		int n2 = label2.length;

		// Calculate N contingency matrix
		int[][] count = new int[n1][n2];
		for (int i = 0; i < n1; i++) {
			for (int j = 0; j < n2; j++) {
				int match = 0;

				for (int k = 0; k < n; k++) {
					if (y1[k] == label1[i] && y2[k] == label2[j]) {
						match++;
					}
				}

				count[i][j] = match;
			}
		}

		// Marginals
		int[] count1 = new int[n1];
		int[] count2 = new int[n2];

		for (int i = 0; i < n1; i++) {
			for (int j = 0; j < n2; j++) {
				count1[i] += count[i][j];
				count2[j] += count[i][j];
			}
		}

		// Calculate RAND - Adj
		double rand1 = 0.0;
		for (int i = 0; i < n1; i++) {
			for (int j = 0; j < n2; j++) {
				if (count[i][j] >= 2) {
					rand1 += Maths.choose(count[i][j], 2);
				}
			}
		}

		double rand2a = 0.0;
		for (int i = 0; i < n1; i++) {
			if (count1[i] >= 2) {
				rand2a += Maths.choose(count1[i], 2);
			}
		}

		double rand2b = 0;
		for (int j = 0; j < n2; j++) {
			if (count2[j] >= 2) {
				rand2b += Maths.choose(count2[j], 2);
			}
		}

		double rand3 = rand2a * rand2b;
		rand3 /= Maths.choose(n, 2);
		double rand_N = rand1 - rand3;

		// D
		double rand4 = (rand2a + rand2b) / 2;
		double rand_D = rand4 - rand3;

		double rand = rand_N / rand_D;
		return rand;
	}

	@Override
	public String toString() {
		return "Adjusted Rand Index";
	}
}
