import static java.lang.System.out;
import static org.junit.Assert.*;

import java.util.Random;


import org.junit.Test;

public class JUnitBpTree {

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
        int totalKeys    = 19;
        boolean RANDOMLY = false;

        BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
        //if (args.length == 1) totalKeys = Integer.valueOf (args [0]);
   
        if (RANDOMLY) {
            Random rng = new Random ();
            for (int i = 1; i <= totalKeys; i += 2) bpt.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (int i = 1; i <= totalKeys; i += 2) bpt.put (i, i * i);
        } // if

        bpt.print (bpt.root, 0);
        for (int i = 0; i <= totalKeys; i++) {
            out.println ("key = " + i + " value = " + bpt.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of nodes accessed = " + bpt.count / (double) totalKeys);
        //assertEquals(1,bpt.firstKey().intValue());
        
        
        assertEquals(19, bpt.lastKey().intValue());
    
	}

}
