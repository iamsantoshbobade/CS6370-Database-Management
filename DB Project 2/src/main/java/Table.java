/****************************************************************************************
 * @file  Table.java
 *
 * @author   John Miller
 */

import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/****************************************************************************************
 * This class implements relational database tables (including attribute names,
 * domains and a list of tuples. Five basic relational algebra operators are
 * provided: project, select, union, minus and join. The insert data
 * manipulation operator is also provided. Missing are update and delete data
 * manipulation operators.
 */
public class Table implements Serializable{

	/**
	 * Relative path for storage directory
	 */
	private static final String DIR = "store" + File.separator;

	/**
	 * Filename extension for database files
	 */
	private static final String EXT = ".dbf";

	/**
	 * Counter for naming temporary tables.
	 */
	private static int count = 0;

	/**
	 * Table name.
	 */
	private final String name;

	/**
	 * Array of attribute names.
	 */
	private final String[] attribute;

	/**
	 * Array of attribute domains: a domain may be integer types: Long, Integer,
	 * Short, Byte real types: Double, Float string types: Character, String
	 */
	private final Class[] domain;

	/**
	 * Collection of tuples (data storage).
	 */
	private final List<Comparable[]> tuples;

	/**
	 * Primary key.
	 */
	private final String[] key;

	/**
	 * Index into tuples (maps key to tuple number).
	 */
	private final Map<KeyType, Comparable[]> index;
	


	// ----------------------------------------------------------------------------------
	// Constructors
	// ----------------------------------------------------------------------------------

	/************************************************************************************
	 * Construct an empty table from the meta-data specifications.
	 *
	 * @param _name
	 *            the name of the relation
	 * @param _attribute
	 *            the string containing attributes names
	 * @param _domain
	 *            the string containing attribute domains (data types)
	 * @param _key
	 *            the primary key
	 */
	
	public Table(String _name, String[] _attribute, Class[] _domain, String[] _key) {
		name = _name;
		attribute = _attribute;
		domain = _domain;
		key = _key;
		
		//tuples = new FileList(_name,get_recordsize(), this.domain);
		//System.out.println("Tuples: "+tuples.size());
		
		tuples = new ArrayList<>();
		
		//Instruction For TA: Use Only one of them at a time
		//index = new TreeMap<>(); //Uncomment this to run project using TreeMap
		//index = new LinHashMap <> (KeyType.class, Comparable [].class); //Uncomment this to run project using LinHashMap
		 index = new BpTreeMap <> (KeyType.class, Comparable [].class);//Uncomment this to run project using BpTreeMap

	} // constructor

	/************************************************************************************
	 * Construct a table from the meta-data specifications and data in _tuples
	 * list.
	 *
	 * @param _name
	 *            the name of the relation
	 * @param _attribute
	 *            the string containing attributes names
	 * @param _domain
	 *            the string containing attribute domains (data types)
	 * @param _key
	 *            the primary key
	 * @param _tuple
	 *            the list of tuples containing the data
	 */
	public Table(String _name, String[] _attribute, Class[] _domain, String[] _key, List<Comparable[]> _tuples) {
		name = _name;
		attribute = _attribute;
		domain = _domain;
		key = _key;
		tuples = _tuples;
		
		//Instruction For TA: Use Only one of them at a time
		//index = new TreeMap<>(); //Uncomment this to run project using TreeMap
		//index = new LinHashMap <> (KeyType.class, Comparable [].class);//Uncomment this to run project using LinHashMap
		 index = new BpTreeMap <> (KeyType.class, Comparable [].class); //Uncomment this to run project using BpTreeMap
	} // constructor

	/************************************************************************************
	 * Construct an empty table from the raw string specifications.
	 *
	 * @param name
	 *            the name of the relation
	 * @param attributes
	 *            the string containing attributes names
	 * @param domains
	 *            the string containing attribute domains (data types)
	 */
	public Table(String name, String attributes, String domains, String _key) {
		this(name, attributes.split(" "), findClass(domains.split(" ")), _key.split(" "));

		out.println("DDL> create table " + name + " (" + attributes + ")");
	} // constructor

	// ----------------------------------------------------------------------------------
	// Public Methods
	// ----------------------------------------------------------------------------------
	/************************************************************************************
	 *  Determine whether the two tables (this and table2) are equal, i.e., have
	 *  the same tuples with the same data items
	 *
	 * #usage 
	 *
	 * @param obj an instance of the generic Object class, which holds the instance of the rhs table to be compared with.
	 * 
	 * @author Santosh           
	 * @return whether the two tables contains same data items.
	 */
    @Override
    public boolean equals(Object obj)
    {
    	Table table2 = (Table)obj;
    	for(int i=0;i<tuples.size();i++)
    	{
    		Comparable[] t = tuples.get(i);
    		Comparable[] u = table2.tuples.get(i);
    		for(int j = 0;j < t.length;j++)
    		{
    			if(!t[j].equals(u[j]))
    				return false;
    		}//for
    	}//for
    	return true;
    }//equals

	/************************************************************************************
	 * Project the tuples onto a lower dimension by keeping only the given
	 * attributes. Check whether the original key is included in the projection.
	 *
	 * #usage movie.project ("title year studioNo")
	 *
	 * @param attributes
	 *            the attributes to project onto
	 * @author Karthik           
	 * @return a table of projected tuples
	 */
	public Table project(String attributes) {
		out.println("RA> " + name + ".project (" + attributes + ")");
		String[] attrs = attributes.split(" ");
		Class[] colDomain = extractDom(match(attrs), domain);
		String[] newKey = (Arrays.asList(attrs).containsAll(Arrays.asList(key))) ? key : attrs;
		Table t=new Table(name + count++, attrs, colDomain, newKey);
	       
		// Code written by Karthik
		int total_attributes = attrs.length;
		for (Comparable[] tup : tuples) {
			Comparable[] projected_tuple = new Comparable[total_attributes];
			for (int i = 0; i < attrs.length; i++) {
				int index = col(attrs[i]);
				projected_tuple[i] = tup[index];
			}
			t.tuples.add(projected_tuple);
			Comparable[] key1=extract(tup,newKey);
			t.index.put(new KeyType(key1),projected_tuple);
		}
		return t;
	} // project

	/************************************************************************************
	 * Select the tuples satisfying the given predicate (Boolean function).
	 *
	 * #usage movie.select (t -> t[movie.col("year")].equals (1977))
	 *
	 * @param predicate
	 *            the check condition for tuples
	 * @return a table with tuples satisfying the predicate
	 */
	public Table select(Predicate<Comparable[]> predicate) {
		out.println("RA> " + name + ".select (" + predicate + ")");

		return new Table(name + count++, attribute, domain, key,tuples.stream().filter(t -> predicate.test(t)).collect(Collectors.toList()));
	} // select

	/************************************************************************************
	 * Select the tuples satisfying the given key predicate (key = value). Use
	 * an index (Map) to retrieve the tuple with the given key value.
	 *
	 * @param keyVal
	 *            the given key value
	 * @author Karthik           
	 * @return a table with the tuple satisfying the key predicate
	 */
	public Table select(KeyType keyVal) {
		out.println("RA> " + name + ".select (" + keyVal + ")");

		List<Comparable[]> rows = new ArrayList<>();
		 Table t=new Table(name + count++, attribute, domain, key, rows);
		// Code written by Karthik
		// System.out.println("--------------MY TESTING-------------------");
		System.out.println("keytuple" + this.index.get(keyVal));
		Comparable[] keyTuple = this.index.get(keyVal);
		rows.add(keyTuple);
		
		// Code written ends
        t.index.put(keyVal,keyTuple);
		return t;
	} // select

	/************************************************************************************
	 * Union this table and table2. Check that the two tables are compatible.
	 *
	 * #usage movie.union (show)
	 *
	 * @param table2
	 *            the rhs table in the union operation
	 * @author Yash
	 * @return a table representing the union
	 */
	public Table union(Table table2) {
		out.println("RA> " + name + ".union (" + table2.name + ")");
		if (!compatible(table2))
			return null;
		List<Comparable[]> rows = new ArrayList<>();
		Table t1=new Table(name + count++, attribute, domain,table2.attribute);
		// inserting the tuples of "this" table in rows
		for (Map.Entry<KeyType, Comparable[]> e : this.index.entrySet()) {
			KeyType k1 = new KeyType(e.getValue());
			t1.index.put(k1,e.getValue());
			t1.tuples.add(e.getValue());
		
		}

		for (Map.Entry<KeyType, Comparable[]> e : index.entrySet()) {
			out.println(e.getKey() + " -> " + Arrays.toString(e.getValue()));
		} // for

		// inserting the tuples of "table2" table in rows
		for (Map.Entry<KeyType, Comparable[]> f : table2.index.entrySet()) {
			System.out.println("keytype" + f.getKey());
			KeyType k2 = new KeyType(f.getValue());
			if (t1.index.get(k2) == null) { // use index to remove duplicates
				KeyType k1 = new KeyType(f.getValue());
				//t1.index.put(k1, rows.get(cnt1));
				t1.index.put(k2,f.getValue());
				t1.tuples.add(f.getValue());
			
			} // if
		} // for
		/*
		 * for (Map.Entry<KeyType, Comparable[]> e : index.entrySet()) {
		 * out.println(e.getKey() + " -> " + Arrays.toString(e.getValue())); }
		 * // for
		 */

		return t1;
		} // union

	/************************************************************************************
	 * Take the difference of this table and table2. Check that the two tables
	 * are compatible.
	 *
	 * #usage movie.minus (show)
	 *
	 * @param table2
	 *            The rhs table in the minus operation
	 * @return a table representing the difference
	 * @author Santosh
	 */
	public Table minus(Table table2) {
		out.println("RA> " + name + ".minus (" + table2.name + ")");
		if (!compatible(table2))
			return null;

		Table t1=new Table(name + count++, attribute, domain, key);
		boolean found = false;
		for (Map.Entry<KeyType, Comparable[]> lhsEntry : index.entrySet()) {
			found = false;
			for (Map.Entry<KeyType, Comparable[]> rhsEntry : table2.index.entrySet()) {
				if (lhsEntry.getKey().equals(rhsEntry.getKey())) {
					found = true;
					break;
				} // if
			} // for

			if (!found) {
				t1.index.put(lhsEntry.getKey(), lhsEntry.getValue());
				t1.tuples.add(lhsEntry.getValue());
			} // if
		} // for
		return t1;
	} // minus

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by append "2" to the end of any duplicate attribute name.
     *
     * #usage movie.join ("studioNo", "name", studio)
     *
     * @param attribute1  
     * 				the attributes of this table to be compared (Foreign Key)
     * @param attribute2  
     * 				the attributes of table2 to be compared (Primary Key)
     * @param table2      
     * 				the rhs table in the join operation
     * @author sakshi
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (String attributes1, String attributes2, Table table2)
    {
		out.println("RA> " + name + ".join (" + attributes1 + ", " + attributes2 + ", " + table2.name + ")");
        
		String[] t_attrs = attributes1.split(" ");
		String[] u_attrs = attributes2.split(" ");
		String[] combinedKey = new String[t_attrs.length + u_attrs.length];
		int[] index1 = new int[t_attrs.length];
		int[] index2 = new int[t_attrs.length];
		int k1 = 0;
		int k = 0, cnt1 = 0;

		KeyType combKey = new KeyType(combinedKey);
		// Map<KeyType,Comparable[]> indexnew=new
		// LinHashMap<KeyType,Comparable[]>();;
		Map<KeyType, Comparable[]> index = new LinHashMap<>(KeyType.class, Comparable[].class);
		List<Comparable[]> rows = new ArrayList<>();
		String[] tup1 = new String[tuples.size()];
		Comparable[] temp;
		Comparable[] temp1;
		Table t =null;
		// System.out.println("index"+index1[0]);
		// System.out.println("domian"+domain[index1[0]]);
		int flag1 = 0;
		for (int i = 0; i < t_attrs.length; i++) {
			index1[i] = col(t_attrs[i]); // Find Index Positions of attributes
											// in a table
		} // for
		for (int j = 0; j < u_attrs.length; j++) {
			index2[j] = table2.col(u_attrs[j]); // Find Index Positions of
												// attributes in a table

		} // for
		for (int j = 0; j < t_attrs.length; j++) {
			combinedKey[cnt1] = t_attrs[j];
			cnt1++;
		} // for
		for (int i = 0; i < u_attrs.length; i++) {
			combinedKey[cnt1] = u_attrs[i];
			cnt1++;
		} // for

		/*
		 * for(int i=0;i<index1.length;i++){
		 * if(domain[index1[i]].equals("Integer")){ flag1=1;
		 * 
		 * }//if else{ flag1=0; break; }//else }
		 */

		System.out.println("flag1" + flag1);
		int flag = 0, tupin = 0, cnt = 0;

		KeyType lhskey = new KeyType(key);
		KeyType lhsattr = new KeyType(t_attrs);
		KeyType rhskey = new KeyType(table2.key);
		KeyType rhsattr = new KeyType(u_attrs);
		Comparable[] rows1;
		// System.out.println("key"+lhskey);
		// System.out.println("t_attrs"+lhsattr);
		/*
		 * If LHS Table is Primary key and RHS table is Foreign key Linear
		 * search in RHS and retrieve tuple by use of index from LHS table
		 * 
		 */

		if (lhskey.equals(lhsattr)) {
			System.out.println("Table1 has pk");
			t=new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
					ArrayUtil.concat(domain, table2.domain),table2.key);
			
			for (int i = 0; i < table2.tuples.size(); i++) {
				Comparable[] tup = table2.tuples.get(i);
				Comparable[] c = new Comparable[index2.length];
				for (int l = 0; l < index2.length; l++) {
					c[l] = tup[index2[l]];
				} // for

				KeyType k2 = new KeyType(c);
				// System.out.println("k2"+k2.getClass()+"-->"+c.g);
				if (this.index.get(k2) != null) { // use index to check whether
													// key is present in 1st
													// table
					// System.out.println("both are equal");
					temp = table2.tuples.get(i);
					temp1 = this.index.get(k2);
					Comparable[] temp2 = ArrayUtil.concat(temp, temp1);
					rows.add(temp2);

					t.index.put(rhskey, rows.get(cnt));// New key is primary key
														// of foreign key table
				   //t.tuples.set(cnt,rows.get(cnt));
					t.tuples.add(rows.get(cnt));
					cnt++;
				} // if

			} // for

		} // if
		/*
		 * If RHS Table is Primary key and LHS table is Foreign key Linear
		 * search in LHS and retrieve tuple by use of index from RHS table
		 * 
		 */

		else if (rhskey.equals(rhsattr)) {
			System.out.println("Table2");
			t=new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
					ArrayUtil.concat(domain, table2.domain),key);
		
			for (int i = 0; i < tuples.size(); i++) {
				Comparable[] tup = tuples.get(i);
				Comparable[] c = extract(tup, t_attrs);
				KeyType k2 = new KeyType(c);
				// System.out.println("k2"+k2);
				if (table2.index.get(k2) != null) {// use index to check whether
													// key is present in 2nd
													// table
					// System.out.println("both are equal");
					temp = tuples.get(i);
					temp1 = table2.index.get(k2);
					Comparable[] temp2 = ArrayUtil.concat(temp, temp1);
					rows.add(temp2);
					t.index.put(lhskey, rows.get(cnt));// New key is primary key
														// of foreign key table
					//t.tuples.set(cnt,rows.get(cnt));
					t.tuples.add(rows.get(cnt));
					cnt++;

				} // if
			} // for
		} // if
		/*
		 * If Both keys are not Primary and foreign keys then same as 1st
		 * program
		 * 
		 */
		else {

			
			t=new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
					ArrayUtil.concat(domain, table2.domain),ArrayUtil.concat(key,table2.key));
		
			for (int i = 0; i < tuples.size(); i++) {
				Comparable[] tup = tuples.get(i);
				for (int j = 0; j < table2.tuples.size(); j++) {
					Comparable[] tup2 = table2.tuples.get(j);
					for (int l = 0; l < index1.length; l++) {
						if (tup[index1[l]].equals(tup2[index2[l]])) {
							flag = 1;
						} // if
						else {
							flag = 0;
							break;
						}

					} // for
					if (flag == 1) {
						temp = tuples.get(i);
						temp1 = table2.tuples.get(j);
						Comparable[] temp2 = ArrayUtil.concat(temp, temp1);
						rows.add(temp2);
						t.index.put(combKey, rows.get(cnt));// If both are not
															// primary key then
															// combination of
															// both keys will be
															// Primary Key
						t.tuples.set(cnt,rows.get(cnt));
						cnt++;
					} // if
				} // for
			} // for
		} // else

		for (Map.Entry<KeyType, Comparable[]> e : index.entrySet()) {
			out.println(e.getKey() + " -> " + Arrays.toString(e.getValue()));
		} // for
          
		//t.tuples=rows;
		//Table t = new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
			//	ArrayUtil.concat(domain, table2.domain), key, null);
		// t.index = index;
		//Table t = new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
			//	ArrayUtil.concat(domain, table2.domain), key,rows);
		//t.tuples=rows;
		return t;// new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),ArrayUtil.concat(domain, table2.domain), key,rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing an "natural join".  Tuples from both tables
     * are compared requiring common attributes to be equal.  The duplicate column is also
     * eliminated.
     *
     * #usage movieStar.join (starsIn)
     *
     * @param table2  the rhs table in the join operation
     * @author sakshi
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (Table table2)
    {
		out.println("RA> " + name + ".join (" + table2.name + ")");

		List<Comparable[]> rows = new ArrayList<>();
		Map<KeyType, Comparable[]> indexnew = new LinHashMap<>(KeyType.class, Comparable[].class);
		int len1 = attribute.length;
		int len2 = table2.attribute.length;
		List<String> commonattr = new ArrayList<String>();
		Comparable[] temp;
		Comparable[] temp1;
		boolean table1PrimaryKey = false;
		boolean table2PrimaryKey = false;
		int keycnt = 0;
		String[] combinedKey = new String[key.length + table2.key.length];
		KeyType lhskey = new KeyType(key);
		KeyType rhskey = new KeyType(table2.key);
		for (int i = 0; i < key.length; i++) {
			combinedKey[keycnt] = key[i];
			// System.out.println("combinedkey"+combinedKey[keycnt]);
			keycnt++;
		} // for
		for (int j = 0; j < table2.key.length; j++) {
			combinedKey[keycnt] = table2.key[j];
			// System.out.println("combinedkey"+combinedKey[keycnt]);

			keycnt++;
		} // for
		KeyType keycombine = new KeyType(combinedKey);
		/*
		 * Find common attributes in both the tables
		 * 
		 */
		if (len1 >= len2) {
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					if (attribute[i].equals(table2.attribute[j])) {
						commonattr.add(attribute[i]);
					} // if
				} // for

			} // for
		} // if
		if (len2 > len1) {
			for (int i = 0; i < len2; i++) {
				for (int j = 0; j < len1; j++) {
					if (attribute[j].equals(table2.attribute[i])) {
						// System.out.println("matches");
						commonattr.add(attribute[j]);

					} // if
				} // for

			} // for
		} // if

		/*
		 * If No common attributes than returns cross product of two tables
		 * 
		 */
		int f = 0;
		if (commonattr.size() == 0) {
			System.out.println("no common attributes");
			for (int i = 0; i < tuples.size(); i++) {
				Comparable[] tup = tuples.get(i);
				// System.out.println("tuples"+tuples.size()+"-->"+tup.length+"-->"+table2.tuples.size());
				for (int j = 0; j < table2.tuples.size(); j++) {
					temp = tuples.get(i);
					temp1 = table2.tuples.get(j);
					Comparable[] temp2 = ArrayUtil.concat(temp, temp1);
					rows.add(temp2);
					indexnew.put(keycombine, rows.get(f)); // New key is
															// Combined Key
					// System.out.println("rows"+rows.get(f));
					f++;
				} // for
			} // for
			return new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
					ArrayUtil.concat(domain, table2.domain),ArrayUtil.concat(key,table2.key), rows);

		} // if

		Integer[] index1 = new Integer[commonattr.size()];
		Integer[] index2 = new Integer[commonattr.size()];
		int k1 = 0;
		int k = 0;
		List<Comparable[]> rows1 = new ArrayList<Comparable[]>();

		for (int i = 0; i < commonattr.size(); i++) {
			index1[i] = col(commonattr.get(i)); // Find Index Positions of
												// attributes in a table

		} // for

		for (int j = 0; j < commonattr.size(); j++) {
			index2[j] = table2.col(commonattr.get(j));// Find Index Positions of
														// attributes in a table

		} // for
		/*
		 * Compare elements of tuples of common attributes and add common tuples
		 * in rows1 list
		 */
		int flag = 0;
		for (int i = 0; i < tuples.size(); i++) {
			Comparable[] tup = tuples.get(i);
			// System.out.println("tuples"+tuples.size()+"-->"+tup.length+"-->"+table2.tuples.size());
			for (int j = 0; j < table2.tuples.size(); j++) {
				Comparable[] tup1 = table2.tuples.get(j);
				// System.out.println("index1"+index1.length);
				for (int l = 0; l < index1.length; l++) {
					// System.out.println("tup"+tup[index1[l]]+"--->"+tup1[index2[l]]);
					if (tup[index1[l]].equals(tup1[index2[l]])) {
						flag = 1;
					} // if
					else {
						flag = 0;
						break;
					}

				} // for
				if (flag == 1) {
					temp = tuples.get(i);
					temp1 = table2.tuples.get(j);
					// System.out.println("temp1::"+temp1[0]+"---->"+temp1[1]+"-->"+temp1[2]);
					Comparable[] temp2 = ArrayUtil.concat(temp, temp1);
					// System.out.println("temp2"+temp2.length);
					rows1.add(temp2);
				} // if

			} // for
		} // for

		if (key.length > 0) {
			for (int i = 0; i < key.length; i++) {
				for (int j = 0; j < commonattr.size(); j++) {
					if (commonattr.get(j).equals(key[i])) {
						table1PrimaryKey = true;
						System.out.println("Table1 is PrimaryKey");
					} // if
				} // for

			} // for
		} // if

		if (table2.key.length > 0) {
			for (int i = 0; i < table2.key.length; i++) {
				for (int j = 0; j < commonattr.size(); j++) {
					if (commonattr.get(j).equals(table2.key[i])) {
						table2PrimaryKey = true;
						System.out.println("Table2 is PrimaryKey");
					} // if
				} // for
			} // for

		} // if

		// FIX - eliminate duplicate columns
		/*
		 * Eliminate duplicate names
		 */
		String[] key1=new String[key.length];
		
		String[] attr = new String[attribute.length + table2.attribute.length - commonattr.size()];
		// System.out.println("rows1"+rows1.size());
		for (Comparable[] c : rows1) {
			int s1 = 0;
			int l = 0;
			int l1 = 0;

			Map<String, Comparable> m = new HashMap<String, Comparable>();
			Comparable[] r = new Comparable[attribute.length + table2.attribute.length - commonattr.size()];
			// System.out.println("c size"+c.length);
			for (Comparable c1 : c) {
				if (l1 < table2.attribute.length) {
					if (l >= attribute.length) {
						m.put(table2.attribute[l1], c1);
						l1++;
					} // if
					else {
						m.put(attribute[l], c1);
						l++;
					}
				} // if

			} // for
			for (String s : m.keySet()) {
				attr[s1] = s;
				r[s1] = m.get(s);
				s1++;
			} // for
			rows.add(r);
			if (table1PrimaryKey && table2PrimaryKey == false) {
				indexnew.put(lhskey, rows.get(f)); // New key is from table1
				for(int keyin=0;keyin<key.length;keyin++){
					key1[keyin]=key[keyin];
				}//for
			} // if
			else if (table2PrimaryKey && table1PrimaryKey == false) {
				indexnew.put(rhskey, rows.get(f)); // New key is from table2
				for(int keyin=0;keyin<table2.key.length;keyin++){
					key1[keyin]=table2.key[keyin];
				}//for
			
			} // else
			else {
				// System.out.println("keycombine"+keycombine);
				indexnew.put(keycombine, rows.get(f)); // New Key is combinedKey
														// from both table
				key1=ArrayUtil.concat(key,table2.key);
			} // else
			f++;
		} // for

		for (Map.Entry<KeyType, Comparable[]> e : index.entrySet()) {
			out.println(e.getKey() + " -> " + Arrays.toString(e.getValue()));
		} // for

		/*
		 * return new table with output tuples in List rows
		 */
		return new Table(name + count++, attr, ArrayUtil.concat(domain, table2.domain), key1, rows);
  } // join


	/************************************************************************************
	 * Return the column position for the given attribute name.
	 *
	 * @param attr
	 *            the given attribute name
	 * @return a column position
	 */
	public int col(String attr) {
		for (int i = 0; i < attribute.length; i++) {
			if (attr.equals(attribute[i]))
				return i;
		} // for

		return -1; // not found
	} // col

	/************************************************************************************
	 * Insert a tuple to the table.
	 *
	 * #usage movie.insert ("'Star_Wars'", 1977, 124, "T", "Fox", 12345)
	 *
	 * @param tup
	 *            the array of attribute values forming the tuple
	 * @return whether insertion was successful
	 */
	public boolean insert(Comparable[] tup) {
		out.println("DML> insert into " + name + " values ( " + Arrays.toString(tup) + " )");

		if (typeCheck(tup)) {
			tuples.add(tup);
			Comparable[] keyVal = new Comparable[key.length];
			int[] cols = match(key);
			for (int j = 0; j < keyVal.length; j++)
				keyVal[j] = tup[cols[j]];
			index.put(new KeyType(keyVal), tup);
			return true;
		} else {
			return false;
		} // if
	} // insert

	/************************************************************************************
	 * Get the name of the table.
	 *
	 * @return the table's name
	 */
	public String getName() {
		return name;
	} // getName

	/************************************************************************************
	 * Print this table.
	 */
	public void print() {
		out.println("\n Table " + name);
		out.print("|-");
		for (int i = 0; i < attribute.length; i++)
			out.print("---------------");
		out.println("-|");
		out.print("| ");
		for (String a : attribute)
			out.printf("%15s", a);
		out.println(" |");
		out.print("|-");
		for (int i = 0; i < attribute.length; i++)
			out.print("---------------");
		out.println("-|");
		for (Comparable[] tup : tuples) {
			out.print("| ");
			for (Comparable attr : tup)
				out.printf("%15s", attr);
			out.println(" |");
		} // for
		out.print("|-");
		for (int i = 0; i < attribute.length; i++)
			out.print("---------------");
		out.println("-|");
	} // print

	/************************************************************************************
	 * Print this table's index (Map).
	 */
	public void printIndex() {
		out.println("\n Index for " + name);
		out.println("-------------------");
		for (Map.Entry<KeyType, Comparable[]> e : index.entrySet()) {
			out.println(e.getKey() + " -> " + Arrays.toString(e.getValue()));
		} // for
		out.println("-------------------");
	} // printIndex

	/************************************************************************************
	 * Load the table with the given name into memory.
	 *
	 * @param name
	 *            the name of the table to load
	 */
	public static Table load(String name) {
		Table tab = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DIR + name + EXT));
			tab = (Table) ois.readObject();
			ois.close();
		} catch (IOException ex) {
			out.println("load: IO Exception");
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			out.println("load: Class Not Found Exception");
			ex.printStackTrace();
		} // try
		return tab;
	} // load

	/************************************************************************************
	 * Save this table in a file.
	 */
	public void save() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DIR + name + EXT));
			oos.writeObject(this);
			oos.close();
		} catch (IOException ex) {
			out.println("save: IO Exception");
			ex.printStackTrace();
		} // try
	} // save

	// ----------------------------------------------------------------------------------
	// Private Methods
	// ----------------------------------------------------------------------------------

	/************************************************************************************
	 * Determine whether the two tables (this and table2) are compatible, i.e.,
	 * have the same number of attributes each with the same corresponding
	 * domain.
	 *
	 * @param table2
	 *            the rhs table
	 * @return whether the two tables are compatible
	 */
	private boolean compatible(Table table2) {
		if (domain.length != table2.domain.length) {
			out.println("compatible ERROR: table have different arity");
			return false;
		} // if
		for (int j = 0; j < domain.length; j++) {
			if (domain[j] != table2.domain[j]) {
				out.println("compatible ERROR: tables disagree on domain " + j);
				return false;
			} // if
		} // for
		return true;
	} // compatible

	/************************************************************************************
	 * Match the column and attribute names to determine the domains.
	 *
	 * @param column
	 *            the array of column names
	 * @return an array of column index positions
	 */
	private int[] match(String[] column) {
		int[] colPos = new int[column.length];

		for (int j = 0; j < column.length; j++) {
			boolean matched = false;
			for (int k = 0; k < attribute.length; k++) {
				if (column[j].equals(attribute[k])) {
					matched = true;
					colPos[j] = k;
				} // for
			} // for
			if (!matched) {
				out.println("match: domain not found for " + column[j]);
			} // if
		} // for

		return colPos;
	} // match

	/************************************************************************************
	 * Extract the attributes specified by the column array from tuple t.
	 *
	 * @param t
	 *            the tuple to extract from
	 * @param column
	 *            the array of column names
	 * @return a smaller tuple extracted from tuple t
	 */
	private Comparable[] extract(Comparable[] t, String[] column) {
		Comparable[] tup = new Comparable[column.length];
		int[] colPos = match(column);
		for (int j = 0; j < column.length; j++)
			tup[j] = t[colPos[j]];
		return tup;
	} // extract

	/************************************************************************************
	 * Check the size of the tuple (number of elements in list) as well as the
	 * type of each value to ensure it is from the right domain.
	 *
	 * @param t
	 *            the tuple as a list of attribute values
	 * @author Sakshi
	 * @return whether the tuple has the right size and values that comply with
	 *         the given domains
	 */
	private boolean typeCheck(Comparable[] t) {
	
		if(t.length != attribute.length) return false;
		for(int i=0;i<t.length;i++)
				if(!domain[i].equals(t[i].getClass())) return false;
					
	return true;
	} // typeCheck

	/************************************************************************************
	 * Find the classes in the "java.lang" package with given names.
	 *
	 * @param className
	 *            the array of class name (e.g., {"Integer", "String"})
	 * @return an array of Java classes
	 */
	private static Class[] findClass(String[] className) {
		Class[] classArray = new Class[className.length];

		for (int i = 0; i < className.length; i++) {
			try {
				classArray[i] = Class.forName("java.lang." + className[i]);
			} catch (ClassNotFoundException ex) {
				out.println("findClass: " + ex);
			} // try
		} // for

		return classArray;
	} // findClass

	/************************************************************************************
	 * Extract the corresponding domains.
	 *
	 * @param colPos
	 *            the column positions to extract.
	 * @param group
	 *            where to extract from
	 * @return the extracted domains
	 */
	private Class[] extractDom(int[] colPos, Class[] group) {
		Class[] obj = new Class[colPos.length];

		for (int j = 0; j < colPos.length; j++) {
			obj[j] = group[colPos[j]];
		} // for

		return obj;
	} // extractDom

	
	
	
	
} // Table class
