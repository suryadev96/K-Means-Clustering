package otc.stat;

import java.util.ArrayList;
import java.util.List;

import otc.math.Maths;

public class Mixture extends AbstractDistribution{
	 public static class Component {
	        /**
	         * The distribution of component.
	         */
	        public Distribution distribution;
	        /**
	         * The priori probability of component.
	         */
	        public double priori;

	        public Component() {
	        }

	        public Component(double priori, Distribution distribution) {
	            this.priori = priori;
	            this.distribution = distribution;
	        }
	    }

	    List<Component> components;

	    /**
	     * Constructor.
	     */
	    Mixture() {
	        components = new ArrayList<Component>();
	    }

	    /**
	     * Constructor.
	     * @param mixture a list of distributions.
	     */
	    public Mixture(List<Component> mixture) {
	        components = new ArrayList<Component>();
	        components.addAll(mixture);

	        double sum = 0.0;
	        for (Component component : mixture) {
	            sum += component.priori;
	        }

	        if (Maths.abs(sum - 1.0) > 1E-3)
	            throw new IllegalArgumentException("The sum of priori is not equal to 1.");
	    }

	    public double mean() {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        double mu = 0.0;

	        for (Component c : components)
	            mu += c.priori * c.distribution.mean();

	        return mu;
	    }

	    public double var() {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        double variance = 0.0;

	        for (Component c : components)
	            variance += c.priori * c.priori * c.distribution.var();

	        return variance;
	    }

	    public double sd() {
	        return Maths.sqrt(var());
	    }

	    /**
	     * Shannon entropy. Not supported.
	     */
	    public double entropy() {
	        throw new UnsupportedOperationException("Mixture does not support entropy()");
	    }
	    
	    public double p(double x) {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        double p = 0.0;

	        for (Component c : components)
	            p += c.priori * c.distribution.p(x);

	        return p;
	    }

	    public double logp(double x) {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        return Maths.log(p(x));
	    }

	    public double cdf(double x) {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        double p = 0.0;

	        for (Component c : components)
	            p += c.priori * c.distribution.cdf(x);

	        return p;
	    }

	    public double rand() {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        double r = Maths.random();

	        double p = 0.0;
	        for (Component g : components) {
	            p += g.priori;
	            if (r <= p)
	                return g.distribution.rand();
	        }

	        // we should not arrive here.
	        return components.get(components.size()-1).distribution.rand();
	    }

	    public double quantile(double p) {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        if (p < 0.0 || p > 1.0) {
	            throw new IllegalArgumentException("Invalid p: " + p);
	        }

	        // Starting guess near peak of density.
	        // Expand interval until we bracket.
	        double xl, xu, inc = 1;
	        double x = (int) mean();
	        if (p < cdf(x)) {
	            do {
	                x -= inc;
	                inc *= 2;
	            } while (p < cdf(x));
	            xl = x;
	            xu = x + inc / 2;
	        } else {
	            do {
	                x += inc;
	                inc *= 2;
	            } while (p > cdf(x));
	            xu = x;
	            xl = x - inc / 2;
	        }

	        return quantile(p, xl, xu);
	    }

	    public int npara() {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        int f = components.size() - 1; // independent priori parameters
	        for (int i = 0; i < components.size(); i++)
	            f += components.get(i).distribution.npara();

	        return f;
	    }

	    /**
	     * Returns the number of components in the mixture.
	     */
	    public int size() {
	        return components.size();
	    }

	    /**
	     * BIC score of the mixture for given data.
	     */
	    public double bic(double[] data) {
	        if (components.isEmpty())
	            throw new IllegalStateException("Mixture is empty!");

	        int n = data.length;

	        double logLikelihood = 0.0;
	        for (double x : data) {
	            double p = p(x);
	            if (p > 0) logLikelihood += Math.log(p);
	        }

	        return logLikelihood - 0.5 * npara() * Math.log(n);
	    }

	    /**
	     * Returns the list of components in the mixture.
	     */
	    public List<Component> getComponents() {
	        return components;
	    }

	    @Override
	    public String toString() {
	        StringBuilder builder = new StringBuilder();
	        builder.append("Mixture[");
	        builder.append(components.size());
	        builder.append("]:{");
	        for (Component c : components) {
	            builder.append(" (");
	            builder.append(c.distribution);
	            builder.append(':');
	            builder.append(String.format("%.4f", c.priori));
	            builder.append(')');
	        }
	        builder.append("}");
	        return builder.toString();
	    }
}
