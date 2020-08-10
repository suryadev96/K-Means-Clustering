package otc.miniproject;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import otc.data.AttributeDataset;
import otc.data.NominalAttribute;
import otc.data.parser.DelimitedTextParser;
import otc.validation.AdjustedRandIndex;
import otc.validation.RandIndex;

public class GMeansTest {

	
	 public GMeansTest() {
	    }

	    @BeforeClass
	    public static void setUpClass() throws Exception {
	    }

	    @AfterClass
	    public static void tearDownClass() throws Exception {
	    }
	    
	    @Before
	    public void setUp() {
	    }
	    
	    @After
	    public void tearDown() {
	    }
	    
	    @Test
	    public void testUSPS() {
	        System.out.println("USPS");
	        DelimitedTextParser parser = new DelimitedTextParser();
	        parser.setResponseIndex(new NominalAttribute("class"), 0);
	        try {
	            AttributeDataset train = parser.parse("USPS Train", otc.data.parser.IOUtils.getTestDataFile("usps/zip.train"));
	            AttributeDataset test = parser.parse("USPS Test", otc.data.parser.IOUtils.getTestDataFile("usps/zip.test"));

	            double[][] x = train.toArray(new double[train.size()][]);
	            int[] y = train.toArray(new int[train.size()]);
	            double[][] testx = test.toArray(new double[test.size()][]);
	            int[] testy = test.toArray(new int[test.size()]);
	            
	            AdjustedRandIndex ari = new AdjustedRandIndex();
	            RandIndex rand = new RandIndex();
	            GMeans gmeans = new GMeans(x, 10);
	            
	            double r = rand.measure(y, gmeans.getClusterLabel());
	            double r2 = ari.measure(y, gmeans.getClusterLabel());
	            System.out.format("Training rand index = %.2f%%\tadjusted rand index = %.2f%%%n", 100.0 * r, 100.0 * r2);
	            assertTrue(r > 0.85);
	            assertTrue(r2 > 0.4);
	            
	            int[] p = new int[testx.length];
	            for (int i = 0; i < testx.length; i++) {
	                p[i] = gmeans.predict(testx[i]);
	            }
	            
	            r = rand.measure(testy, p);
	            r2 = ari.measure(testy, p);
	            System.out.format("Testing rand index = %.2f%%\tadjusted rand index = %.2f%%%n", 100.0 * r, 100.0 * r2);
	            assertTrue(r > 0.85);
	            assertTrue(r2 > 0.4);
	        } catch (Exception ex) {
	            System.err.println(ex);
	        }
	    }

}
