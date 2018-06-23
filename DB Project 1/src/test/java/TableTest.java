import static java.lang.System.out;
import static org.junit.Assert.*;

import org.junit.Test;

public class TableTest {

	@Test
	public void testProject() 
	{
		//Test1 Projecting the movie attribute
		Table movie = new Table ("movie", "title year length genre studioName producerNo","String Integer Integer String String Integer", "title year");
		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
        Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
        Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
        Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
        movie.insert (film0);
        movie.insert (film1);
        movie.insert (film2);
        movie.insert (film3);
        Table test1result_exp = new Table ("movie", "title year length genre studioName producerNo","String Integer Integer String String Integer", "title");
        Comparable [] test1result0 = {"Star_Wars"};
        Comparable [] test1result1 = {"Star_Wars_2"};
        Comparable[] test1result2 = {"Rocky"};
        Comparable[] test1result3 = {"Rambo"};
        test1result_exp.insert(test1result0);
        test1result_exp.insert(test1result1);
        test1result_exp.insert(test1result2);
        test1result_exp.insert(test1result3);
        Table resultTable = movie.project("title");
        assertEquals(test1result_exp,resultTable);
		//fail("Not yet implemented");
        
        //Test 2
        Table test2result_exp = new Table ("movie", "title year length genre studioName producerNo","String Integer Integer String String Integer", "title");
        Comparable [] test2result0 = {"Star_Wars","Fox"};
        Comparable [] test2result1 = {"Star_Wars_2","Fox"};
        Comparable[] test2result2 = {"Rocky","Universal"};
        Comparable[] test2result3 = {"Rambo","Universal"};
        test2result_exp.insert(test2result0);
        test2result_exp.insert(test2result1);
        test2result_exp.insert(test2result2);
        test2result_exp.insert(test2result3);
        Table resultTable2 = movie.project("title studioName");
        assertEquals(test2result_exp,resultTable2);        
        
        //Test 3
        
        
	}

	@Test
	public void testSelectKeyType()
	{
		 Table movieStar = new Table ("movieStar", "name address gender birthdate","String String Character String", "name");
		 Comparable [] star0 = { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" };
	     Comparable [] star1 = { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
	     Comparable [] star2 = { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
	     out.println ();
	     movieStar.insert (star0);
	     movieStar.insert (star1);
	     movieStar.insert (star2);
	     movieStar.print ();
	     Table result1exp = new Table("movieStar", "name address gender birthdate","String String Character String", "name");
	     Comparable[] resut_tuple0 = {"Mark_Hamill"};
	     result1exp.insert(resut_tuple0);
	     Table resultTable0 = movieStar.select(new KeyType("Mark_Hamill"));
	     assertEquals(result1exp,resultTable0);
	}

}
