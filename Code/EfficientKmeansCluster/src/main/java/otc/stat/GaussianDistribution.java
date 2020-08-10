package otc.stat;

import otc.math.Maths;
import otc.math.special.Erf;

public class GaussianDistribution extends AbstractDistribution implements ExponentialFamily {
	private static final double LOG2PIE_2 = Maths.log(2 * Math.PI * Math.E) / 2;
	private static final double LOG2PI_2 = Maths.log(2 * Math.PI) / 2;
	private static final GaussianDistribution singleton = new GaussianDistribution(0.0, 1.0);

	private double mu;
	private double sigma;
	private double variance;

	private double entropy;
	private double pdfConstant;

	/**
	 * Constructor
	 * 
	 * @param mu
	 *            mean.
	 * @param sigma
	 *            standard deviation.
	 */
	public GaussianDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		variance = sigma * sigma;

		entropy = Maths.log(sigma) + LOG2PIE_2;
		pdfConstant = Maths.log(sigma) + LOG2PI_2;
	}

	/**
	 * Constructor. Mean and standard deviation will be estimated from the data by
	 * MLE.
	 */
	public GaussianDistribution(double[] data) {
		mu = Maths.mean(data);
		sigma = Maths.sd(data);
		variance = sigma * sigma;

		entropy = Maths.log(sigma) + LOG2PIE_2;
		pdfConstant = Maths.log(sigma) + LOG2PI_2;
	}

	public static GaussianDistribution getInstance() {
		return singleton;
	}

	public int npara() {
		return 2;
	}

	public double mean() {
		return mu;
	}

	public double var() {
		return variance;
	}

	public double sd() {
		return sigma;
	}

	public double entropy() {
		return entropy;
	}

	@Override
	public String toString() {
		return String.format("Gaussian Distribution(%.4f, %.4f)", mu, sigma);
	}

	/**
	 * Cache the generated random number in Box-Muller algorithm.
	 */
	private Double boxMuller;

	/**
	 * Uses the Box-Muller algorithm to transform Random.random()'s into Gaussian
	 * deviates.
	 */
	public double rand() {
		double out, x, y, r, z;

		if (boxMuller != null) {
			out = boxMuller.doubleValue();
			boxMuller = null;

		} else {
			do {
				x = Maths.random(-1, 1);
				y = Maths.random(-1, 1);
				r = x * x + y * y;
			} while (r >= 1.0);

			z = Maths.sqrt(-2.0 * Math.log(r) / r);
			boxMuller = new Double(x * z);
			out = y * z;
		}

		return mu + sigma * out;
	}

	/**
	 * Uses Inverse CDF method to generate a Gaussian deviate.
	 */
	public double randInverseCDF() {
		final double a0 = 2.50662823884;
		final double a1 = -18.61500062529;
		final double a2 = 41.39119773534;
		final double a3 = -25.44106049637;
		final double b0 = -8.47351093090;
		final double b1 = 23.08336743743;
		final double b2 = -21.06224101826;
		final double b3 = 3.13082909833;
		final double c0 = 0.3374754822726147;
		final double c1 = 0.9761690190917186;
		final double c2 = 0.1607979714918209;
		final double c3 = 0.0276438810333863;
		final double c4 = 0.0038405729373609;
		final double c5 = 0.0003951896511919;
		final double c6 = 0.0000321767881768;
		final double c7 = 0.0000002888167364;
		final double c8 = 0.0000003960315187;

		double y, r, x;

		double u = Maths.random();
		while (u == 0.0) {
			u = Maths.random();
		}

		y = u - 0.5;

		if (Maths.abs(y) < 0.42) {
			r = y * y;
			x = y * (((a3 * r + a2) * r + a1) * r + a0) / ((((b3 * r + b2) * r + b1) * r + b0) * r + 1);

		} else {
			r = u;
			if (y > 0) {
				r = 1 - u;
			}
			r = Maths.log(-Math.log(r));
			x = c0 + r * (c1 + r * (c2 + r * (c3 + r * (c4 + r * (c5 + r * (c6 + r * (c7 + r * c8)))))));
			if (y < 0) {
				x = -(x);
			}
		}

		return mu + sigma * x;
	}

	public double p(double x) {
		if (sigma == 0) {
			if (x == mu) {
				return 1.0;
			} else {
				return 0.0;
			}
		}

		return Maths.exp(logp(x));
	}

	public double logp(double x) {
		if (sigma == 0) {
			if (x == mu) {
				return 0.0;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		}

		double d = x - mu;
		return -0.5 * d * d / variance - pdfConstant;
	}

	public double cdf(double x) {
		if (sigma == 0) {
			if (x < mu) {
				return 0.0;
			} else {
				return 1.0;
			}
		}

		return 0.5 * Erf.erfc(-0.707106781186547524 * (x - mu) / sigma);
	}

	/**
	 * The quantile, the probability to the left of quantile(p) is p. This is
	 * actually the inverse of cdf.
	 *
	 * Original algorythm and Perl implementation can be found at:
	 * http://www.math.uio.no/~jacklam/notes/invnorm/index.html
	 */
	public double quantile(double p) {
		if (p < 0.0 || p > 1.0) {
			throw new IllegalArgumentException("Invalid p: " + p);
		}

		if (sigma == 0.0) {
			if (p < 1.0) {
				return mu - 1E-10;
			} else {
				return mu;
			}
		}
		return -1.41421356237309505 * sigma * Erf.inverfc(2.0 * p) + mu;
	}

	public Mixture.Component M(double[] x, double[] posteriori) {
		double alpha = 0.0;
		double mean = 0.0;
		double sd = 0.0;

		for (int i = 0; i < x.length; i++) {
			alpha += posteriori[i];
			mean += x[i] * posteriori[i];
		}

		mean /= alpha;

		for (int i = 0; i < x.length; i++) {
			double d = x[i] - mean;
			sd += d * d * posteriori[i];
		}

		sd = Maths.sqrt(sd / alpha);

		Mixture.Component c = new Mixture.Component();
		c.priori = alpha;
		c.distribution = new GaussianDistribution(mean, sd);

		return c;
	}
}
