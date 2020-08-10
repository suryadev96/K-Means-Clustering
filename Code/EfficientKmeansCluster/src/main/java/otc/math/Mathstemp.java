package otc.math;

public class Mathstemp {
	public static double squaredDistance(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("Input vector sizes are different.");
		}

		double sum = 0.0;
		for (int i = 0; i < x.length; i++) {
			sum += sqr(x[i] - y[i]);
		}

		return sum;
	}

	public static double round(double x, int decimal) {
		if (decimal < 0) {
			return round(x / pow(10, -decimal)) * pow(10, -decimal);
		} else {
			return round(x * pow(10, decimal)) / pow(10, decimal);
		}
	}

	public static long round(double a) {
		return java.lang.Math.round(a);
	}

	public static int round(float a) {
		return java.lang.Math.round(a);
	}

	public static double sqr(double x) {
		return x * x;
	}

	public static double pow(double a, double b) {
		return java.lang.Math.pow(a, b);
	}
	
	/**
     * Returns the mean of an array.
     */
    public static double mean(int[] x) {
        return (double) sum(x) / x.length;
    }

    /**
     * Returns the mean of an array.
     */
    public static double mean(float[] x) {
        return sum(x) / x.length;
    }

    /**
     * Returns the mean of an array.
     */
    public static double mean(double[] x) {
        return sum(x) / x.length;
    }
    
    /**
     * Returns the sum of an array.
     */
    public static int sum(int[] x) {
        double sum = 0.0;

        for (int n : x) {
            sum += n;
        }

        if (sum > Integer.MAX_VALUE || sum < -Integer.MAX_VALUE) {
            throw new ArithmeticException("Sum overflow: " + sum);
        }
        
        return (int) sum;
    }

    /**
     * Returns the sum of an array.
     */
    public static double sum(float[] x) {
        double sum = 0.0;

        for (float n : x) {
            sum += n;
        }

        return sum;
    }

    /**
     * Returns the sum of an array.
     */
    public static double sum(double[] x) {
        double sum = 0.0;

        for (double n : x) {
            sum += n;
        }

        return sum;
    }
    
    /**
     * Returns the standard deviation of an array.
     */
    public static double sd(int[] x) {
        return sqrt(var(x));
    }

    /**
     * Returns the standard deviation of an array.
     */
    public static double sd(float[] x) {
        return sqrt(var(x));
    }

    /**
     * Returns the standard deviation of an array.
     */
    public static double sd(double[] x) {
        return sqrt(var(x));
    }
    
    public static double sqrt(double a) {
        return java.lang.Math.sqrt(a);
    }
    
    /**
     * Returns the variance of an array.
     */
    public static double var(int[] x) {
        if (x.length < 2) {
            throw new IllegalArgumentException("Array length is less than 2.");
        }

        double sum = 0.0;
        double sumsq = 0.0;
        for (int xi : x) {
            sum += xi;
            sumsq += xi * xi;
        }

        int n = x.length - 1;
        return sumsq / n - (sum / x.length) * (sum / n);
    }

    /**
     * Returns the variance of an array.
     */
    public static double var(float[] x) {
        if (x.length < 2) {
            throw new IllegalArgumentException("Array length is less than 2.");
        }

        double sum = 0.0;
        double sumsq = 0.0;
        for (float xi : x) {
            sum += xi;
            sumsq += xi * xi;
        }

        int n = x.length - 1;
        return sumsq / n - (sum / x.length) * (sum / n);
    }

    /**
     * Returns the variance of an array.
     */
    public static double var(double[] x) {
        if (x.length < 2) {
            throw new IllegalArgumentException("Array length is less than 2.");
        }

        double sum = 0.0;
        double sumsq = 0.0;
        for (double xi : x) {
            sum += xi;
            sumsq += xi * xi;
        }

        int n = x.length - 1;
        return sumsq / n - (sum / x.length) * (sum / n);
    }
}
