package otc.util;

import java.util.concurrent.ThreadFactory;

public class SimpleDeamonThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
	    t.setDaemon(true);
	    return t;
	}

}
