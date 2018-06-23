import static java.lang.System.out;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JUnitTesterUnionOperator {

	@Test
	public void testuUion() {
		Table movie = new Table("movie", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table cinema = new Table("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table test_table1 = new Table("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table test_table2 = new Table("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table test_table3 = new Table("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");
		
		Table test_table4 = new Table("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Comparable[] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
		Comparable[] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
		Comparable[] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
		Comparable[] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
		out.println();
		movie.insert(film0);
		movie.insert(film1);
		movie.insert(film2);
		movie.insert(film3);
		movie.print();

		Comparable[] film4 = { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
		out.println();

		Comparable[] film5 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
		Comparable[] film6 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
		out.println();
		test_table1.insert(film3);
		test_table1.insert(film2);
		test_table1.insert(film0);
		test_table1.insert(film1);
		
		Table t_union = movie.union(cinema);

		/**
		 * 
		 * movie.union(cinema)
		 * 
		 * Test 1: When there are no rows inserted in the cinema table. 
		 * Expected Result: all the rows from the movie table.
		 */
		assertEquals(test_table1, t_union);

		movie = new Table("movie", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		movie.print();

		cinema.insert(film2);
		cinema.insert(film3);

		t_union = movie.union(cinema);

		/**
		 * 
		 * movie.union(cinema) Test 2: When there are no rows inserted in the
		 * movie table. 
		 * Expected Result: all the rows from the cinema table
		 */
		System.out.println("For test 2 table result");
		t_union.print();
		
		test_table2.insert(film3);
		test_table2.insert(film2);

		assertEquals(test_table2, t_union);

		movie.insert(film0);
		movie.insert(film1);
		movie.insert(film2);
		movie.insert(film3);

		movie.print();

		t_union = movie.union(cinema);
		t_union.print();
		
		
		System.out.println("Just printed");

		test_table3.insert(film3);
		test_table3.insert(film2);
		test_table3.insert(film5);
		test_table3.insert(film6);

		/**
		 * 
		 * 
		 * movie.union(cinema) 
		 * Test 3: When there are few tuples in the both tables.
		 * Expected Result: R U S i.e. the rows that are in R  and S
		 */
		assertEquals(test_table3, t_union);
		
		
		cinema = new Table("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");
		
		
		Comparable[] film8 = { "Back To The Future", 1989, 140, "sciFi", "Universal", 32356 };
		Comparable[] film9 = { "Back To The Future  II", 1990, 120, "sciFi", "Universal", 32358 };
		cinema.insert(film8);
		cinema.insert(film9);
		
		t_union = movie.union(cinema);
		t_union.print();
		
		test_table4.insert(film3);
		test_table4.insert(film2);
		test_table4.insert(film0);
		test_table4.insert(film1);
		test_table4.insert(film8);
		test_table4.insert(film9);
		
		assertEquals(test_table4, t_union);

	}

}
