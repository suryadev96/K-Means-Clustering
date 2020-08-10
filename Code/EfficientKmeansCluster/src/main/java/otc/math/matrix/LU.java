package otc.math.matrix;

public class LU {
	/**
	 * Array for internal storage of decomposition.
	 */
	protected DenseMatrix lu;

	/**
	 * pivot sign.
	 */
	protected int pivsign;

	/**
	 * Internal storage of pivot vector.
	 */
	protected int[] piv;

	/**
	 * True if the matrix is singular.
	 */
	protected boolean singular;

	/**
	 * Constructor.
	 * 
	 * @param lu
	 *            LU decomposition matrix
	 * @param piv
	 *            pivot vector
	 * @param pivsign
	 *            pivot sign. +1 if even number of row interchanges, -1 if odd
	 *            number of row interchanges.
	 * @param singular
	 *            True if the matrix is singular
	 */
	public LU(DenseMatrix lu, int[] piv, int pivsign, boolean singular) {
		this.lu = lu;
		this.piv = piv;
		this.pivsign = pivsign;
		this.singular = singular;
	}

	/**
	 * Constructor.
	 * 
	 * @param lu
	 *            LU decomposition matrix
	 * @param piv
	 *            pivot vector
	 * @param singular
	 *            True if the matrix is singular
	 */
	public LU(DenseMatrix lu, int[] piv, boolean singular) {
		this.lu = lu;
		this.piv = piv;
		this.singular = singular;

		this.pivsign = 1;

		int n = Math.min(lu.nrows(), lu.ncols());
		for (int i = 0; i < n; i++) {
			if (piv[i] != i)
				this.pivsign = -this.pivsign;
		}
	}

	/**
	 * Returns true if the matrix is singular or false otherwise.
	 */
	public boolean isSingular() {
		return singular;
	}

	/**
	 * Returns the matrix determinant
	 */
	public double det() {
		int m = lu.nrows();
		int n = lu.ncols();

		if (m != n)
			throw new IllegalArgumentException(String.format("Matrix is not square: %d x %d", m, n));

		double d = (double) pivsign;
		for (int j = 0; j < n; j++) {
			d *= lu.get(j, j);
		}

		return d;
	}

	/**
	 * Returns the matrix inverse. For pseudo inverse, use QRDecomposition.
	 */
	public DenseMatrix inverse() {
		int m = lu.nrows();
		int n = lu.ncols();

		if (m != n) {
			throw new IllegalArgumentException(String.format("Matrix is not square: %d x %d", m, n));
		}

		DenseMatrix inv = Matrix.zeros(n, n);
		for (int i = 0; i < n; i++) {
			inv.set(i, piv[i], 1.0);
		}

		solve(inv);
		return inv;
	}

	/**
	 * Solve A * x = b.
	 * 
	 * @param b
	 *            right hand side of linear system. On output, b will be overwritten
	 *            with the solution matrix.
	 * @exception RuntimeException
	 *                if matrix is singular.
	 */
	public void solve(double[] b) {
		// B use b as the internal storage. Therefore b will contains the results.
		DenseMatrix B = Matrix.newInstance(b);
		solve(B);
	}

	/**
	 * Solve A * X = B. B will be overwritten with the solution matrix on output.
	 * 
	 * @param B
	 *            right hand side of linear system. On output, B will be overwritten
	 *            with the solution matrix.
	 * @throws RuntimeException
	 *             if matrix is singular.
	 */
	public void solve(DenseMatrix B) {
		int m = lu.nrows();
		int n = lu.ncols();
		int nrhs = B.ncols();

		if (B.nrows() != m)
			throw new IllegalArgumentException(
					String.format("Row dimensions do not agree: A is %d x %d, but B is %d x %d", lu.nrows(), lu.ncols(),
							B.nrows(), B.ncols()));

		if (isSingular()) {
			throw new RuntimeException("Matrix is singular.");
		}

		DenseMatrix X = Matrix.zeros(B.nrows(), B.ncols());

		// Copy right hand side with pivoting
		for (int j = 0; j < nrhs; j++) {
			for (int i = 0; i < m; i++) {
				X.set(i, j, B.get(piv[i], j));
			}
		}

		// Solve L*Y = B(piv,:)
		for (int k = 0; k < n; k++) {
			for (int i = k + 1; i < n; i++) {
				for (int j = 0; j < nrhs; j++) {
					X.sub(i, j, X.get(k, j) * lu.get(i, k));
				}
			}
		}

		// Solve U*X = Y;
		for (int k = n - 1; k >= 0; k--) {
			for (int j = 0; j < nrhs; j++) {
				X.div(k, j, lu.get(k, k));
			}

			for (int i = 0; i < k; i++) {
				for (int j = 0; j < nrhs; j++) {
					X.sub(i, j, X.get(k, j) * lu.get(i, k));
				}
			}
		}

		// Copy the result back to B.
		for (int j = 0; j < nrhs; j++) {
			for (int i = 0; i < m; i++) {
				B.set(i, j, X.get(i, j));
			}
		}
	}
}
