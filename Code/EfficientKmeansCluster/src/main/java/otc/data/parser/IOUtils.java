package otc.data.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
	/** Utility classes should not have public constructors. */
	private IOUtils() {

	}

	private static String home = System.getProperty("otc.home", "src/test/");

	/** Get the file path of sample dataset. */
	public static String getTestDataPath(String path) {
		return home + path;
	}

	/** Get the file object of sample dataset. */
	public static File getTestDataFile(String path) {
		return new java.io.File(getTestDataPath(path));
	}

	/** Get the reader of sample datasets. */
	public static BufferedReader getTestDataReader(String path) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(getTestDataFile(path))));
	}

	/**
	 * Returns the lines of an <code>InputStream</code> as a list of Strings.
	 *
	 * @param input
	 *            the <code>InputStream</code> to read from.
	 * @return the list of lines.
	 */
	public static List<String> readLines(InputStream input) throws IOException {
		final InputStreamReader reader = new InputStreamReader(input);
		return readLines(reader);
	}

	/**
	 * Returns the lines of an <code>InputStream</code> as a list of Strings.
	 *
	 * @param input
	 *            the <code>InputStream</code> to read from.
	 * @param charset
	 *            a charset.
	 * @return the list of lines.
	 */
	public static List<String> readLines(InputStream input, Charset charset) throws IOException {
		final InputStreamReader reader = new InputStreamReader(input, charset);
		return readLines(reader);
	}

	/**
	 * Returns the lines of a <code>Reader</code> as a list of Strings.
	 *
	 * @param input
	 *            the <code>Reader</code> to read from.
	 * @return the list of lines.
	 */
	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = input instanceof BufferedReader ? (BufferedReader) input : new BufferedReader(input);
		List<String> list = new ArrayList<>();
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		reader.close();
		return list;
	}
}
