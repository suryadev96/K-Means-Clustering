package otc.math.matrix;

public interface MatrixMultiplication<A, B> {
	/**
	 * Returns the result of matrix multiplication A * B.
	 */
	public A abmm(B b);

	/**
	 * Returns the result of matrix multiplication A * B'.
	 */
	public A abtmm(B b);

	/**
	 * Returns the result of matrix multiplication A' * B.
	 */
	public A atbmm(B b);
}
