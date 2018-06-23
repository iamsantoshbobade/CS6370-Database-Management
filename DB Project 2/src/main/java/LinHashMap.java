
/************************************************************************************
 * @file LinHashMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;
//import org.apache.commons.lang3.ArrayUtils;

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 4;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /********************************************************************************
     * This inner class defines buckets that are stored in the hash table.
     */
    private class Bucket
    {
        int    nKeys;
        K []   key;
        V []   value;
        Bucket next;

        @SuppressWarnings("unchecked")
        Bucket (Bucket n)
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = n;
        } // constructor
    } // Bucket inner class

    /** The list of buckets making up the hash table.
     */
    private final List <Bucket> hTable;

    /** The modulus for low resolution hashing
     */
    private int mod1;

    /** The modulus for high resolution hashing
     */
    private int mod2;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The index of the next bucket to split.
     */
    private int split = 0;

    /*
     * If duplicate is entered then return oldvalue from put method
     */
    private V oldvalue=null;
    
    /********************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of home buckets (a power of 2, e.g., 4)
     */
    public LinHashMap (Class <K> _classK, Class <V> _classV)    // , int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();
        mod1   = 4;                        // initSize;
        mod2   = 2 * mod1;
    } // constructor

      /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @author Sakshi Sachdev
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        Set <Map.Entry <K, V>> enSet = new HashSet <> ();

        //  T O   B E   I M P L E M E N T E D
        //Map<K,V> m=new HashMap<K,V>();
        /*
         * Iterator through hashmap and get buckets and keys
         * 
         */
        for(int k=0;k<hTable.size();k++){
	   		 System.out.println("hashtable size is::"+hTable.size());
	   		 Bucket b5=hTable.get(k);
	   		 System.out.println("nkeys"+b5.nKeys);
	   		 for(int l=0;l<b5.nKeys;l++){
	   		//   System.out.println("kth bucket"+k+"--->"+b5.key[l]);
	   		   //m.put(b5.key[l],b5.value[l]);     //Put keys into map
	   			 SimpleEntry<K, V> se=new SimpleEntry<K,V>(b5.key[l],b5.value[l]);
	   			 enSet.add(se);
	   		 }//for
	   		 if(b5.next!=null){
	   			 if(b5.next.nKeys>0){
	   				 for(int j=0;j<b5.next.nKeys;j++){
	   					System.out.println("kth bucket"+k+"--->"+b5.next.key[j]);
	   				   	//m.put(b5.next.key[j],b5.next.value[j]);
	   					SimpleEntry<K, V> se=new SimpleEntry<K,V>(b5.key[j],b5.value[j]);
	   	   			 enSet.add(se);

                         	   		   		 
	   		   		 }//for
	   			 }//if
	   		 }//if
	   		 
       }//for
        //put map enteries into set
        System.out.println("size"+enSet.size());
         //enSet=m.entrySet();
       //return set conatining all key value pairs     
        return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @author Sakshi Sachdev
     * @return  the value associated with the key
     */
    public V get (Object key)
    {
    	//Calculate hash function for key
        int i = h (key);
        V value1=null;
        
        int flag1=0,in=0,flag2=0;
        /*
         * Re split it
         * 
         */
        System.out.println("In Get");
        if(i<split){
        	i=h2(key);
        }//if
        //Get bucket at that index
        //hTable.add(0,null);
        //System.out.println();
        Bucket b=hTable.get(i);
        //System.out.println("count"+count);
        //count++;
    	/*
    	 * Search for a key in that bucket
    	 * 
    	 */
        if(b.nKeys>0){
        	//System.out.println("buckets>0"+count);
        	for(int l=0;l<b.nKeys;l++){
        		//System.out.println("keytype class"+key.getClass());
        		if(key instanceof Integer){
        			//System.out.println("key is integer"+b.key[l]+"-->"+key);
        			if(b.key[l].equals(key)){
      	   			   flag1=1;
      	   			   in=l;
      	   			   break;
      	   		   }//if
        		}//if
        		else{
        			System.out.println("key in get of linhashmap"+key);
        			KeyType k2=(KeyType)key;
        			KeyType k3=(KeyType)b.key[l];
        			System.out.println("k2 and b.key[l]"+k2+"-->"+b.key[l]);
        			if(k2.equals(k3)){
        				System.out.println("both keys are equal");
       	   			   flag1=1;
       	   			   in=l;
       	   			   break;
       	   		   }//if
        		}//else
        		
 	     }//for
         	
      }//if
        if(flag1==0 && b.next!=null && b.next.nKeys>0){
        	//count++;
        	  for(int j=0;j<b.next.nKeys;j++){
        		  if(key instanceof Integer){
          			//System.out.println("key is integer");
          			
          			if(b.next.key[j].equals(key)){
        	   			   flag1=1;
        	   			   in=j;
        	   			   break;
        	   		   }//if
          		}//if
        		  else{
        			  KeyType k2=(KeyType)key;
        			  KeyType k3=(KeyType)b.next.key[j];
          			  if(k2.equals(k3)){
         	   			   flag1=1;
         	   			   in=j;
         	   			   break;
         	   		   }//if

        			  
        		  }
				  
			  }//for
        }//if 
        if(flag1==1){
        	value1=b.value[in];
        }//if
        if(flag2==1){
        	//count++;
        	value1=b.next.value[in];
        }//if
        //return value corresponding to that key
        return value1;
    } // get

    /********************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @author Sakshi Sachdev
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
    	//Calculate index for key using hash1
    	int i = h (key);
        
    	out.println ("LinearHashMap.put: key = " + key + ", h() = " + i + ", value = " + value);
         
        //System.out.println("split is"+split+"-->"+hTable.size());
        List<Integer> storeindex=new ArrayList<Integer>();
        List<Integer> storeindex1=new ArrayList<Integer>();
        //Use hash2 if key is hashed to already splited bucket
        if(i<split){
        	System.out.println("i<split");
        	i=h2(key);
        }//if
        //System.out.println("i is ::"+i);
        
         //System.out.println(hTable.size());
        /*
         * Initialize hashtable
         * 
         */
         if(hTable.size()==0){
        	 for(int j=0;j<mod1;j++){
        		 Bucket b=new Bucket(null);
        		 b.nKeys=0;
        		 hTable.add(j,b);
        	 }//for
        }//if
         if(hTable.size()>0){
        	 Bucket b1=hTable.get(i);    //get bucket at perticular index
        	 /*
        	  * If home bucket is not full then insert key in home bucket
        	  * 
        	  */
        	 if(b1.nKeys<SLOTS){
        		//System.out.println("<SLOTS");    
        		
        		/*if(get(key)!=null){
        			oldvalue=get(key);
        		}//if*/
        		b1.key[b1.nKeys]=key;
        		b1.value[b1.nKeys]=value;
        		b1.nKeys++;
        		hTable.set(i,b1);
        	 }//if
        	 /*
        	  * if homebucket is full then insert key into overflow bucket
        	  */
        	 else{
        		 //System.out.println(">slots");
        		 //System.out.println("key is"+key);
        		 if(b1.next==null){
        			 System.out.println("b1.next==null");
        			 Bucket b2=new Bucket(null);
        			 /*if(get(key)!=null){
             			oldvalue=get(key);
             		 }//if*/
             		
        			 b2.key[b2.nKeys]=key;
        			 b2.value[b2.nKeys]=value;
        			 b2.nKeys++;
        			 b1.next=b2;
        		 }//if
        		 else{
        			 //System.out.println("b1.next!=null");
        			 Bucket b3=b1.next;
        			 if(b3.nKeys<SLOTS){
        				/* if(get(key)!=null){
                  			oldvalue=get(key);
                  		 }//if*/
                  		System.out.println("<slot");
        				 b3.key[b3.nKeys]=key;
        				 b3.value[b3.nKeys]=value;
        				 b3.nKeys++;
        			  }//if
        			 else{
        				/*if(get(key)!=null){
                  			oldvalue=get(key);
                  		 }//if*/
                  		
        				 Bucket b4=new Bucket(null);
             		     b4.key[b4.nKeys]=key;
             		     b4.value[b4.nKeys]=value;
             		     b4.nKeys++;
             		     b3.next=b4;
        			 }//else
        		 }//else
        		 
        		 hTable.set(i,b1);
        	 }//else
        	 /*
        	  * Calculate load factor
        	  */
        	 double d=calLoadFactor();
        	 /*
        	  * If load factor is >50.0 then create ghost bucket
        	  * 
        	  */
        	 if(d>50.0){
        		 
        		// System.out.println("loadfactor is greater that 75%");
        		 int presplit=split;
        		 if(split==mod1-1){
        	        	//split++;
        	        	mod1=mod1*2;
        	        	split=0;
        	        }//if
        		 else{
        		 split++;
        		 }
        		 //System.out.println("new split"+split);
        		 Bucket bghost=new Bucket(null);
        		 hTable.add(hTable.size(),bghost);
        		 int keysize=0;
        		 Bucket b=hTable.get(presplit);
        		 
        		 //System.out.println("presplit keys"+b.nKeys);
        		 /*if(b.next!=null){
        		 //System.out.println("presplit next"+b.next.nKeys);
        		 /*for(int s=0;s<b.next.nKeys;s++){
        			 System.out.println("keys::"+b.next.key[s]);
        		 }//for*/
        	 //}
        		 /*
        		  * Rehash the splitted bucket and calculate the index of elements which are
        		  * to be rehashed
        		  */
        		 
        		  for(int j=0;j<b.nKeys;j++){
        			 //System.out.println("bucket keys in split bucket"+b.key[j]);
        			 //int key1=0;
        			  int newindex=h2(b.key[j]);
        			// System.out.println("newindex and presplit"+newindex+"-->"+presplit);
        		     if(newindex!=presplit){
        		    	// System.out.println("%8 for key fails"+b.key[j]+"--"+j);
        		    	 storeindex.add(j);
        		    	// System.out.println("Now hTable size is"+hTable.size());
        		     }//if
        		     
        		 }//for
        		 if(b.next!=null){
        			 //System.out.println("b.next!=null");
      		        for(int k=0;k<b.next.nKeys;k++){
      		        	//System.out.println("key is"+b.next.key[k]);
      				    int newindex=h2(b.next.key[k]);
      				    //System.out.println("new index is"+newindex);
          			    //System.out.println("next!=null");
          		        if(newindex!=presplit){
          		    	  //  System.out.println("%8 for key fails"+b.next.key[k]+"--"+k);
          		    	     storeindex1.add(k);
          		    	  //  System.out.println("Now hTable size is"+hTable.size());
          		        }//if
      			     }//for
          		    
      			 }//if
      			/*
      			 * Insert rehashed elements to different bucket
      			 * 
      			 */
        		insertElement(b,bghost,storeindex,storeindex1);
        		//System.out.println("after insert at index 2"+b.key[2]);
            	
        	 }//if
        	 
        
 }//if
         
 //Return old value of key if key is already there
  //Return null if key is not there in table
 return oldvalue;
} // put
    
    /********************************************************************************
     * Insert elements which are mapped to ghost buckets
     * and remove it from old buckets
     * @author Sakshi Sachdev
     * @return  null (not the previous value)
     */
    
    public void insertElement(Bucket b,Bucket bghost,List<Integer> storeindex,List<Integer> storeindex1){
    	System.out.println("at index 2"+b.key[2]);
    	List<K> keys1=new ArrayList<K>();
    	List<V> values1=new ArrayList<V>();
    	
    	keys1.addAll(Arrays.asList(b.key));
    	values1.addAll(Arrays.asList(b.value));
    	for(int k=0;k<storeindex.size();k++){
    		//System.out.println("index is"+storeindex.get(k)+"-->"+b.key[storeindex.get(k)]);
    		
    		if(bghost.nKeys<SLOTS){
    	   	/*if(get(b.key[storeindex.get(k)])!=null){
         			oldvalue=get(b.key[storeindex.get(k)]);
         		 }//if*/
         		
    		   bghost.key[bghost.nKeys]=b.key[storeindex.get(k)];
    	   	   bghost.value[bghost.nKeys]=b.value[storeindex.get(k)];
    	   	   bghost.nKeys++;
 	   		
    		}//if
    		else{
    		/*	if(get(b.key[storeindex.get(k)])!=null){
         			oldvalue=get(b.key[storeindex.get(k)]);
         		 }//if*/
         		
    			bghost.next.key[bghost.nKeys]=b.key[storeindex.get(k)];
     	   	    bghost.next.value[bghost.nKeys]=b.value[storeindex.get(k)];
     	   	    bghost.next.nKeys++;
  	   		
    		}//else
    	   	  //  System.out.println("index of arraylist"+storeindex.get(k));
    	   	   keys1.remove(b.key[storeindex.get(k)]);
    	   	   values1.remove(b.value[storeindex.get(k)]);
    	   	   b.nKeys--;
    	   	  
    	}//for
    	if(storeindex1.size()>0){
    		//System.out.println("storeindex1>0");
    		List<K> keys2=new ArrayList<K>();
        	List<V> values2=new ArrayList<V>();
        	keys2.addAll(Arrays.asList(b.next.key));
        	values2.addAll(Arrays.asList(b.next.value));
        	
    	for(int k=0;k<storeindex1.size();k++){
    		//System.out.println("index is"+storeindex1.get(k)+"-->"+b.key[storeindex1.get(k)]);
    		
    		if(bghost.nKeys<SLOTS){
    			/*if(get(b.next.key[storeindex1.get(k)])!=null){
         			oldvalue=get(b.next.key[storeindex1.get(k)]);
         		 }//if*/
         		   
    		   bghost.key[bghost.nKeys]=b.next.key[storeindex1.get(k)];
    	   	   bghost.value[bghost.nKeys]=b.next.value[storeindex1.get(k)];
    		   bghost.nKeys++;
        	}//if
    		else{
    			
    		/*	if(get(b.key[storeindex.get(k)])!=null){
         			oldvalue=get(b.key[storeindex.get(k)]);
         		 }//if*/
         		
    			bghost.next.key[bghost.nKeys]=b.next.key[storeindex.get(k)];
     	   	    bghost.next.value[bghost.nKeys]=b.next.value[storeindex.get(k)];
     	   	    bghost.next.nKeys++;
  	   		
           }//else
    	   	   //ArrayUtils.remove(b.key,storeindex.get(k));
    	   	   //ArrayUtils.remove(b.value,storeindex.get(k));
    	   	  // System.out.println("index of arraylist"+storeindex1.get(k));
    	   	   keys2.remove(b.next.key[storeindex1.get(k)]);
    	   	   values2.remove(b.next.value[storeindex1.get(k)]);
    	   	   b.next.nKeys--;
    	   		
    	}//for
    	keys2.toArray(b.next.key);
    	values2.toArray(b.next.value);
    }//if
    	for(int j=0;j<keys1.size();j++){
    		//System.out.println("arraylist"+keys1.get(j));
    	}//for
    	keys1.toArray(b.key);
    	values1.toArray(b.value);
    	
    	
    	
    }//insertElement
    
    /********************************************************************************
     *calculate loadfactor=(NumberofElements)/(Number of Buckets(homebuckets+ghostbuckets)*Number of slots in a bucket)
     *
     * @author Sakshi Sachdev
     * @return  null (not the previous value)
     */
    
    public double calLoadFactor(){
    	int enteries=0;
    	int bucketnumber=hTable.size();
    	int bucketcapacity=SLOTS;
    	double loadFactor=0.0;
    	for(int i=0;i<hTable.size();i++){
    		enteries+=hTable.get(i).nKeys;
    		if(hTable.get(i).next!=null){
    			enteries+=hTable.get(i).next.nKeys;
    			
    		}//if
    	}//for
    	int mul=(bucketnumber*bucketcapacity);
    	System.out.println("mul"+mul);
    	loadFactor=(enteries*100)/mul;
    	//System.out.println("Enteries"+enteries+"-->"+bucketnumber+"-->"+bucketcapacity+"-->"+loadFactor);
    	//return load factor
    	return loadFactor;
    }//calLoadFactor

    /********************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size

    /********************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
        out.println ("Hash Table (Linear Hashing)");
        out.println ("-------------------------------------------");
        System.out.println("size "+hTable.size());
        //  T O   B E   I M P L E M E N T E D
        System.out.println("BucketNumber   Key    Value");
   		
        for(int k=0;k<hTable.size();k++){
	   		 Bucket b5=hTable.get(k);
	   		 //System.out.println(b5.key[k]);
	   		 //System.out.println(k);
	   		    
	   		 for(int l=0;l<b5.nKeys;l++){
	   		   //System.out.println("BucketNumber   Key    Value");
	   		   System.out.println(k+"\t\t"+b5.key[l]+"\t"+b5.value[l]);
	   		 }//for
	   		 if(b5.next!=null){
	   			 if(b5.next.nKeys>0){
	   				
	   				 for(int j=0;j<b5.next.nKeys;j++){
	   					System.out.println(k+"\t\t"+b5.next.key[j]+"\t"+b5.next.value[j]);
	   		   		 }//for
	   			 }//if
	   		 }//if
	   		 
        }//for

        out.println ("-------------------------------------------");
    } // print

    /********************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @author Sakshi Sachdev
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
    	if(key instanceof Integer){
    		return key.hashCode()%mod1;
    	}
    	else{
    	  KeyType ktemp=(KeyType)key;
    	
          int keytemp=ktemp.hashCode()%mod1;
          if(keytemp<0){
        	keytemp=-keytemp;
          }//if
          return keytemp;        
      	
    	 }
    	
    } // h

    /********************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @author Sakshi Sachdev
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
    	if(key instanceof Integer){
    		return key.hashCode()%mod2;
    	}
    	else{
    	  KeyType ktemp=(KeyType)key;
    	
          int keytemp=ktemp.hashCode()%mod2;
          if(keytemp<0){
        	keytemp=-keytemp;
          }//if
          return keytemp;        
      	
    	 }
    	//KeyType ktemp=(KeyType)key;
     } // h2

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {

        int totalKeys    = 30;
        boolean RANDOMLY = false;

        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class);
        if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

        if (RANDOMLY) {
            Random rng = new Random ();
            for (int i = 1; i <= totalKeys; i += 2) ht.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (int i = 1; i <= totalKeys; i += 2) ht.put (i, i * i);
        } // if

        ht.print ();
        out.println ("-------------------------------------------");
        
       for (int i = 0; i <= totalKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        ht.get(27);
        System.out.println("count"+ht.count);
        ht.entrySet();
        
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) totalKeys);
    } // main

} // LinHashMap class
