package otc.validation;

public interface ClusterMeasure {
	/**
     * Returns an index to measure the quality of clustering.
     * @param y1 the cluster labels.
     * @param y2 the alternative cluster labels.
     */
    public double measure(int[] y1, int[] y2);
}
