package otc.stat;

import otc.math.Maths;

public abstract class AbstractMultivariateDistribution implements MultivariateDistribution {
	/**
	 * The likelihood given a sample set following the distribution.
	 */
	@Override
	public double likelihood(double[][] x) {
		return Maths.exp(logLikelihood(x));
	}

	/**
	 * The likelihood given a sample set following the distribution.
	 */
	@Override
	public double logLikelihood(double[][] x) {
		double L = 0.0;

		for (double[] xi : x)
			L += logp(xi);

		return L;
	}
}
