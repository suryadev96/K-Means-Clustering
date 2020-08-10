package otc.math.matrix;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import otc.stat.GaussianDistribution;

public abstract class Matrix implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Matrix.class);

	/**
	 * True if the matrix is symmetric.
	 */
	private boolean symmetric = false;

	/**
	 * Returns an matrix initialized by given two-dimensional array.
	 */
	public static DenseMatrix newInstance(double[][] A) {
		return Factory.matrix(A);
	}

	/**
	 * Returns a column vector/matrix initialized by given one-dimensional array.
	 */
	public static DenseMatrix newInstance(double[] A) {
		return Factory.matrix(A);
	}

	/**
	 * Returns all-zero matrix.
	 */
	public static DenseMatrix zeros(int rows, int cols) {
		return Factory.matrix(rows, cols);
	}

	/**
	 * Return an all-one matrix.
	 */
	public static DenseMatrix ones(int rows, int cols) {
		return Factory.matrix(rows, cols, 1.0);
	}

	/**
	 * Returns an n-by-n identity matrix.
	 */
	public static DenseMatrix eye(int n) {
		DenseMatrix matrix = Factory.matrix(n, n);

		for (int i = 0; i < n; i++) {
			matrix.set(i, i, 1.0);
		}

		return matrix;
	}

	/**
	 * Returns an m-by-n identity matrix.
	 */
	public static DenseMatrix eye(int m, int n) {
		DenseMatrix matrix = Factory.matrix(m, n);

		int k = Math.min(m, n);
		for (int i = 0; i < k; i++) {
			matrix.set(i, i, 1.0);
		}

		return matrix;
	}

	/**
	 * Returns a square diagonal matrix with the elements of vector diag on the main
	 * diagonal.
	 * 
	 * @param A
	 *            the array of diagonal elements.
	 */
	public static DenseMatrix diag(double[] A) {
		int n = A.length;
		DenseMatrix matrix = Factory.matrix(n, n);

		for (int i = 0; i < n; i++) {
			matrix.set(i, i, A[i]);
		}

		return matrix;
	}

	/**
	 * Returns a random matrix of standard normal distributed values with given mean
	 * and standard dev.
	 */
	public static DenseMatrix randn(int rows, int cols) {
		return randn(rows, cols, 0.0, 1.0);
	}

	/**
	 * Returns a random matrix of normal distributed values with given mean and
	 * standard dev.
	 */
	public static DenseMatrix randn(int rows, int cols, double mu, double sigma) {
		DenseMatrix a = zeros(rows, cols);
		GaussianDistribution g = new GaussianDistribution(mu, sigma);

		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < rows; i++) {
				a.set(i, j, g.rand());
			}
		}

		return a;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * Returns the string representation of matrix.
	 * 
	 * @param full
	 *            Print the full matrix if true. Otherwise only print top left 7 x 7
	 *            submatrix.
	 */
	public String toString(boolean full) {
		StringBuilder sb = new StringBuilder();
		int m = full ? nrows() : Math.min(7, nrows());
		int n = full ? ncols() : Math.min(7, ncols());

		String newline = n < ncols() ? "...\n" : "\n";

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				sb.append(String.format("%8.4f  ", get(i, j)));
			}
			sb.append(newline);
		}

		if (m < nrows()) {
			sb.append("  ...\n");
		}

		return sb.toString();
	}

	/** Returns true if the matrix is symmetric. */
	public boolean isSymmetric() {
		return symmetric;
	}

	/**
	 * Sets if the matrix is symmetric. It is the caller's responability to make
	 * sure if the matrix symmetric. Also the matrix won't update this property if
	 * the matrix values are changed.
	 */
	public void setSymmetric(boolean symmetric) {
		this.symmetric = symmetric;
	}

	/**
	 * Returns the number of rows.
	 */
	public abstract int nrows();

	/**
	 * Returns the number of columns.
	 */
	public abstract int ncols();

	/**
	 * Returns the matrix transpose.
	 */
	public abstract Matrix transpose();

	/**
	 * Returns the entry value at row i and column j.
	 */
	public abstract double get(int i, int j);

	/**
	 * Returns the entry value at row i and column j. For Scala users.
	 */
	public double apply(int i, int j) {
		return get(i, j);
	}

	/**
	 * Returns the diagonal elements.
	 */
	public double[] diag() {
		int n = otc.math.Maths.min(nrows(), ncols());

		double[] d = new double[n];
		for (int i = 0; i < n; i++) {
			d[i] = get(i, i);
		}

		return d;
	}

	/**
	 * Returns the matrix trace. The sum of the diagonal elements.
	 */
	public double trace() {
		int n = Math.min(nrows(), ncols());

		double t = 0.0;
		for (int i = 0; i < n; i++) {
			t += get(i, i);
		}

		return t;
	}

	/**
	 * Returns A' * A
	 */
	public abstract Matrix ata();

	/**
	 * Returns A * A'
	 */
	public abstract Matrix aat();

	/**
	 * y = A * x
	 * 
	 * @return y
	 */
	public abstract double[] ax(double[] x, double[] y);

	/**
	 * y = A * x + y
	 * 
	 * @return y
	 */
	public abstract double[] axpy(double[] x, double[] y);

	/**
	 * y = A * x + b * y
	 * 
	 * @return y
	 */
	public abstract double[] axpy(double[] x, double[] y, double b);

	/**
	 * y = A' * x
	 * 
	 * @return y
	 */
	public abstract double[] atx(double[] x, double[] y);

	/**
	 * y = A' * x + y
	 * 
	 * @return y
	 */
	public abstract double[] atxpy(double[] x, double[] y);

	/**
	 * y = A' * x + b * y
	 * 
	 * @return y
	 */
	public abstract double[] atxpy(double[] x, double[] y, double b);

	/**
	 * Find k largest approximate eigen pairs of a symmetric matrix by the Lanczos
	 * algorithm.
	 *
	 * @param k
	 *            the number of eigenvalues we wish to compute for the input matrix.
	 *            This number cannot exceed the size of A.
	 */
	public EVD eigen(int k) {
		return eigen(k, 1.0E-8, 10 * nrows());
	}

	/**
	 * Find k largest approximate eigen pairs of a symmetric matrix by the Lanczos
	 * algorithm.
	 *
	 * @param k
	 *            the number of eigenvalues we wish to compute for the input matrix.
	 *            This number cannot exceed the size of A.
	 * @param kappa
	 *            relative accuracy of ritz values acceptable as eigenvalues.
	 * @param maxIter
	 *            Maximum number of iterations.
	 */
	public EVD eigen(int k, double kappa, int maxIter) {
		try {
			Class<?> clazz = Class.forName("smile.netlib.ARPACK");
			java.lang.reflect.Method method = clazz.getMethod("eigen", Matrix.class, Integer.TYPE, String.class,
					Double.TYPE, Integer.TYPE);
			return (EVD) method.invoke(null, this, k, "LA", kappa, maxIter);
		} catch (Exception e) {
			if (!(e instanceof ClassNotFoundException)) {
				logger.info("Matrix.eigen({}, {}, {}):", k, kappa);
			}
			return Lanczos.eigen(this, k, kappa, maxIter);
		}
	}

	/**
	 * Find k largest approximate singular triples of a matrix by the Lanczos
	 * algorithm.
	 *
	 * @param k
	 *            the number of singular triples we wish to compute for the input
	 *            matrix. This number cannot exceed the size of A.
	 */
	public SVD svd(int k) {
		return svd(k, 1.0E-8, 10 * nrows());
	}

	/**
	 * Find k largest approximate singular triples of a matrix by the Lanczos
	 * algorithm.
	 *
	 * @param k
	 *            the number of singular triples we wish to compute for the input
	 *            matrix. This number cannot exceed the size of A.
	 * @param kappa
	 *            relative accuracy of ritz values acceptable as singular values.
	 * @param maxIter
	 *            Maximum number of iterations.
	 */
	public SVD svd(int k, double kappa, int maxIter) {
		ATA B = new ATA(this);
		EVD eigen = Lanczos.eigen(B, k, kappa, maxIter);

		double[] s = eigen.getEigenValues();
		for (int i = 0; i < s.length; i++) {
			s[i] = Math.sqrt(s[i]);
		}

		int m = nrows();
		int n = ncols();

		if (m >= n) {

			DenseMatrix V = eigen.getEigenVectors();

			double[] tmp = new double[m];
			double[] vi = new double[n];
			DenseMatrix U = Matrix.zeros(m, s.length);
			for (int i = 0; i < s.length; i++) {
				for (int j = 0; j < n; j++) {
					vi[j] = V.get(j, i);
				}

				ax(vi, tmp);

				for (int j = 0; j < m; j++) {
					U.set(j, i, tmp[j] / s[i]);
				}
			}

			return new SVD(U, V, s);

		} else {

			DenseMatrix U = eigen.getEigenVectors();

			double[] tmp = new double[n];
			double[] ui = new double[m];
			DenseMatrix V = Matrix.zeros(n, s.length);
			for (int i = 0; i < s.length; i++) {
				for (int j = 0; j < m; j++) {
					ui[j] = U.get(j, i);
				}

				atx(ui, tmp);

				for (int j = 0; j < n; j++) {
					V.set(j, i, tmp[j] / s[i]);
				}
			}

			return new SVD(U, V, s);
		}
	}

	/**
	 * For SVD, we compute eigen decomposition of A' * A when m >= n, or that of A *
	 * A' when m < n.
	 */
	private static class ATA extends Matrix {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Matrix A;
		Matrix AtA;
		double[] buf;

		public ATA(Matrix A) {
			this.A = A;
			setSymmetric(true);

			if (A.nrows() >= A.ncols()) {
				buf = new double[A.nrows()];

				if ((A.ncols() < 10000) && (A instanceof DenseMatrix)) {
					AtA = A.ata();
				}
			} else {
				buf = new double[A.ncols()];

				if ((A.nrows() < 10000) && (A instanceof DenseMatrix)) {
					AtA = A.aat();
				}
			}
		}

		@Override
		public int nrows() {
			if (A.nrows() >= A.ncols()) {
				return A.ncols();
			} else {
				return A.nrows();
			}
		}

		@Override
		public int ncols() {
			return nrows();
		}

		@Override
		public ATA transpose() {
			return this;
		}

		@Override
		public ATA ata() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ATA aat() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double[] ax(double[] x, double[] y) {
			if (AtA != null) {
				AtA.ax(x, y);
			} else {
				if (A.nrows() >= A.ncols()) {
					A.ax(x, buf);
					A.atx(buf, y);
				} else {
					A.atx(x, buf);
					A.ax(buf, y);
				}
			}

			return y;
		}

		@Override
		public double[] atx(double[] x, double[] y) {
			return ax(x, y);
		}

		@Override
		public double[] axpy(double[] x, double[] y) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double[] axpy(double[] x, double[] y, double b) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double get(int i, int j) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double apply(int i, int j) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double[] atxpy(double[] x, double[] y) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double[] atxpy(double[] x, double[] y, double b) {
			throw new UnsupportedOperationException();
		}
	}
}
