package otc.stat;

import otc.math.Maths;

public abstract class AbstractDistribution implements Distribution {
	protected double rejection(double pmax, double xmin, double xmax) {
		double x;
		double y;
		do {
			x = xmin + Maths.random() * (xmax - xmin);
			y = Maths.random() * pmax;
		} while (p(x) < y);

		return x;
	}

	/**
	 * Use inverse transform sampling (also known as the inverse probability
	 * integral transform or inverse transformation method or Smirnov transform) to
	 * draw a sample from the given distribution. This is a method for generating
	 * sample numbers at random from any probability distribution given its
	 * cumulative distribution function (cdf). Subject to the restriction that the
	 * distribution is continuous, this method is generally applicable (and can be
	 * computationally efficient if the cdf can be analytically inverted), but may
	 * be too computationally expensive in practice for some probability
	 * distributions. The Box-Muller transform is an example of an algorithm which
	 * is less general but more computationally efficient. It is often the case
	 * that, even for simple distributions, the inverse transform sampling method
	 * can be improved on, given substantial research effort, e.g. the ziggurat
	 * algorithm and rejection sampling.
	 */
	protected double inverseTransformSampling() {
		double u = Maths.random();
		return quantile(u);
	}

	/**
	 * Inversion of CDF by bisection numeric root finding of "cdf(x) = p" for
	 * continuous distribution.
	 */
	protected double quantile(double p, double xmin, double xmax, double eps) {
		if (eps <= 0.0) {
			throw new IllegalArgumentException("Invalid epsilon: " + eps);
		}

		while (Maths.abs(xmax - xmin) > eps) {
			double xmed = (xmax + xmin) / 2;
			if (cdf(xmed) > p) {
				xmax = xmed;
			} else {
				xmin = xmed;
			}
		}

		return xmin;
	}

	/**
	 * Inversion of CDF by bisection numeric root finding of "cdf(x) = p" for
	 * continuous distribution. The default epsilon is 1E-6.
	 */
	protected double quantile(double p, double xmin, double xmax) {
		return quantile(p, xmin, xmax, 1.0E-6);
	}

	/**
	 * The likelihood given a sample set following the distribution.
	 */
	public double likelihood(double[] x) {
		return Maths.exp(logLikelihood(x));
	}

	/**
	 * The likelihood given a sample set following the distribution.
	 */
	public double logLikelihood(double[] x) {
		double L = 0.0;

		for (double xi : x)
			L += logp(xi);

		return L;
	}
}
