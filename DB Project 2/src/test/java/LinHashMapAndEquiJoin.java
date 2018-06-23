import static java.lang.System.out;
import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class LinHashMapAndEquiJoin {

	@Test
	public void testJoinStringStringTable() {
		//fail("Not yet implemented");
		Table movie = new Table ("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");
		
		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
        Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
        Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
        Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
        out.println ();
       movie.insert (film0);
       movie.insert (film1);
       movie.insert (film2);
       movie.insert (film3);
       movie.print ();
       
       Table movieStar = new Table ("movieStar", "name address gender birthdate",
               "String String Character String", "name");

       Comparable [] star0 = { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" };
       Comparable [] star1 = { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
       Comparable [] star2 = { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
       out.println ();
       movieStar.insert (star0);
       movieStar.insert (star1);
       movieStar.insert (star2);
       movieStar.print ();


       Table movieExec = new Table ("movieExec", "certNo name address fee",
               "Integer String String Float", "certNo");

       Comparable [] exec0 = { 9999, "S_Spielberg", "Hollywood", 10000.00F };
       Comparable [] exec1 = { 9999, "Carrie_Fisher", "Hollywood", 10000.00F };
       
       out.println ();
       movieExec.insert (exec0);
       movieExec.insert(exec1);
       movieExec.print ();


        Table studio = new Table ("studio", "name address presNo",
                "String String Integer", "name");
        Comparable [] studio0 = { "Fox", "Los_Angeles", 7777 };
        Comparable [] studio1 = { "Universal", "Universal_City", 8888 };
        Comparable [] studio2 = { "DreamWorks", "Universal_City", 9999 };
        out.println ();
        studio.insert (studio0);
        studio.insert (studio1);
        studio.insert (studio2);
        studio.print ();
        
        Table cinema = new Table ("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        Comparable [] film4 = { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
        out.println ();
        cinema.insert (film2);
        cinema.insert (film3);
        cinema.insert (film4);
        cinema.print ();
        
        Table starsIn = new Table ("starsIn", "movieTitle movieYear starName",
                                         "String Integer String", "movieTitle movieYear starName");

        Comparable [] starin3 = { "Rocky",1994,"S_Spielberg"};
        starsIn.insert(starin3);

      /*test equijoin with with one attribute to be equal
        **i.e studioName attribute of Studio table and name attribute of movie table 
         Also change attributevalue of studioName so that no name matches between two tables
         Using index of LinHashMap in Table() constructor*/
      
        Table equTable=new Table("equTable","title year length genre studioName producerNo name address presNo","String Integer Integer String String Integer String String Integer","title year");
        Comparable [] equtable1={"Star_Wars",1977,124,"sciFi","Fox",12345,"Fox","Los_Angeles",7777};
        Comparable [] equtable2={"Star_Wars_2",1980,124,"sciFi","Fox",12345,"Fox","Los_Angeles",7777};
        Comparable [] equtable3={"Rocky",1985,200,"action","Universal",12125,"Universal","Universal_City",8888};
        Comparable [] equtable4={"Rambo",1978,100,"action","Universal",32355,"Universal","Universal_City",8888};
        equTable.insert(equtable1);
        equTable.insert(equtable2);
        equTable.insert(equtable3);
        equTable.insert(equtable4);
        
        
        
        Table t_join5=movie.join ("studioName", "name", studio);
        t_join5.print ();
        
        assertTrue(t_join5.equals(equTable));
        
        //test equijoin with with more than one attributes to be equal
        //i.e name and address of movieStar and movieExec tables
        //Using index of LinHashMap in Table() constructor
        Table t_join = movieStar.join ("name address", "name address", movieExec);
        t_join.print ();
  
        Table equTable2=new Table("equTable2","name address gender birthdate certNo name address fee","String String Character String Integer String String Float","name");
        Comparable [] equTable5 = { "Carrie_Fisher ", "Hollywood", "F","9/9/99", 9999,"Carrie_Fisher","Hollywood",10000.0};
        equTable2.insert(equTable5);
        
        assertTrue(equTable2.equals(t_join));

        /*
         * Test LinHashMap put() and then get()
         * 
         */
        int totalKeys    = 30;
        boolean RANDOMLY = false;
        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class);
        
        
        if (RANDOMLY) {
            Random rng = new Random ();
            for (int i = 1; i <= totalKeys; i += 2) ht.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (int i = 1; i <= totalKeys; i += 2) ht.put (i, i * i);
        } // if
        
           Integer i=ht.get(3);
           assertTrue(i.equals(9));
           Integer i1=ht.get(9);
           assertTrue(i1.equals(81));
           Integer j=ht.get(2);
           assertTrue(j==null);
      }//test

}
