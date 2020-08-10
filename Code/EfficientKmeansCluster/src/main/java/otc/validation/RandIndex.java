package otc.validation;

import otc.math.Maths;

public class RandIndex implements ClusterMeasure{
	 @Override
	    public double measure(int[] y1, int[] y2) {
	        if (y1.length != y2.length) {
	            throw new IllegalArgumentException(String.format("The vector sizes don't match: %d != %d.", y1.length, y2.length));
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

	        // Calculate RAND - Non-adjusted
	        double rand_T = 0.0;
	        for (int i = 0; i < n1; i++) {
	            for (int j = 0; j < n2; j++) {
	                rand_T += Maths.sqr(count[i][j]);
	            }
	        }
	        rand_T -= n;

	        double rand_P = 0.0;
	        for (int i = 0; i < n1; i++) {
	            rand_P += Maths.sqr(count1[i]);
	        }
	        rand_P -= n;

	        double rand_Q = 0.0;
	        for (int j = 0; j < n2; j++) {
	            rand_Q += Maths.sqr(count2[j]);
	        }
	        rand_Q -= n;

	        double rand = (rand_T - 0.5 * rand_P - 0.5 * rand_Q + Maths.choose(n, 2)) / Maths.choose(n, 2);
	        return rand;
	    }

	    @Override
	    public String toString() {
	        return "Rand Index";
	    }
	}
