package otc.math.matrix;

public class Cholesky {
	/**
	 * Array for internal storage of decomposition.
	 */
	protected DenseMatrix L;

	/**
	 * Constructor.
	 * 
	 * @param L
	 *            the lower triangular part of matrix contains the Cholesky
	 *            factorization.
	 */
	public Cholesky(DenseMatrix L) {
		if (L.nrows() != L.ncols()) {
			throw new UnsupportedOperationException("Cholesky constructor on a non-square matrix");
		}
		this.L = L;
	}

	/**
	 * Returns lower triangular factor.
	 */
	public DenseMatrix getL() {
		return L;
	}

	/**
	 * Returns the matrix determinant
	 */
	public double det() {
		double d = 1.0;
		for (int i = 0; i < L.nrows(); i++) {
			d *= L.get(i, i);
		}

		return d * d;
	}

	/**
	 * Returns the matrix inverse.
	 */
	public DenseMatrix inverse() {
		int n = L.nrows();
		DenseMatrix inv = Matrix.eye(n);
		solve(inv);
		return inv;
	}

	/**
	 * Solve the linear system A * x = b. On output, b will be overwritten with the
	 * solution vector.
	 * 
	 * @param b
	 *            the right hand side of linear systems. On output, b will be
	 *            overwritten with solution vector.
	 */
	public void solve(double[] b) {
		// B use b as the internal storage. Therefore b will contains the results.
		DenseMatrix B = Matrix.newInstance(b);
		solve(B);
	}

	/**
	 * Solve the linear system A * X = B. On output, B will be overwritten with the
	 * solution matrix.
	 * 
	 * @param B
	 *            the right hand side of linear systems.
	 */
	public void solve(DenseMatrix B) {
		if (B.nrows() != L.nrows()) {
			throw new IllegalArgumentException(
					String.format("Row dimensions do not agree: A is %d x %d, but B is %d x %d", L.nrows(), L.ncols(),
							B.nrows(), B.ncols()));
		}

		int n = B.nrows();
		int nrhs = B.ncols();

		// Solve L*Y = B;
		for (int k = 0; k < n; k++) {
			for (int j = 0; j < nrhs; j++) {
				for (int i = 0; i < k; i++) {
					B.sub(k, j, B.get(i, j) * L.get(k, i));
				}
				B.div(k, j, L.get(k, k));
			}
		}

		// Solve L'*X = Y;
		for (int k = n - 1; k >= 0; k--) {
			for (int j = 0; j < nrhs; j++) {
				for (int i = k + 1; i < n; i++) {
					B.sub(k, j, B.get(i, j) * L.get(i, k));
				}
				B.div(k, j, L.get(k, k));
			}
		}
	}
}
