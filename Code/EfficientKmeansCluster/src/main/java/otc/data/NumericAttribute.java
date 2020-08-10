package otc.data;

import java.text.ParseException;

public class NumericAttribute extends Attribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NumericAttribute(String name) {
		super(Type.NUMERIC, name);
	}

	/**
	 * Constructor.
	 */
	public NumericAttribute(String name, double weight) {
		super(Type.NUMERIC, name, weight);
	}

	/**
	 * Constructor.
	 */
	public NumericAttribute(String name, String description) {
		super(Type.NUMERIC, name, description);
	}

	/**
	 * Constructor.
	 */
	public NumericAttribute(String name, String description, double weight) {
		super(Type.NUMERIC, name, description, weight);
	}

	@Override
	public String toString(double x) {
		return Double.toString(x);
	}

	@Override
	public double valueOf(String s) throws ParseException {
		return Double.valueOf(s);
	}
}
