package otc.stat;

public interface Distribution {
	 public int npara();

	    /**
	     * The mean of distribution.
	     */
	    public double mean();
	    
	    /**
	     * The variance of distribution.
	     */
	    public double var();
	    
	    /**
	     * The standard deviation of distribution.
	     */
	    public double sd();

	    /**
	     * Shannon entropy of the distribution.
	     */
	    public double entropy();

	    /**
	     * Generates a random number following this distribution.
	     */
	    public double rand();
	    
	    /**
	     * The probability density function for continuous distribution
	     * or probability mass function for discrete distribution at x.
	     */
	    public double p(double x);

	    /**
	     * The density at x in log scale, which may prevents the underflow problem.
	     */
	    public double logp(double x);

	    /**
	     * Cumulative distribution function. That is the probability to the left of x.
	     */
	    public double cdf(double x);

	    /**
	     * The quantile, the probability to the left of quantile is p. It is
	     * actually the inverse of cdf.
	     */
	    public double quantile(double p);

	    /**
	     * The likelihood of the sample set following this distribution.
	     */
	    public double likelihood(double[] x);
	    
	    /**
	     * The log likelihood of the sample set following this distribution.
	     */
	    public double logLikelihood(double[] x);
}
