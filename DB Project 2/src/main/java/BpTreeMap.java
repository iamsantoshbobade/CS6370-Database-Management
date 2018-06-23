
/************************************************************************************
 * @file BpTreeMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.System.out;

import java.awt.RenderingHints.Key;

/************************************************************************************
 * This class provides B+Tree maps.  B+Trees are used as multi-level index structures
 * that provide efficient access for both point queries and range queries.
 * All keys will be at the leaf level with leaf nodes linked by references.
 * Internal nodes will contain divider keys such that divKey corresponds to the
 * largest key in its left subtree.
 */
public class BpTreeMap <K extends Comparable <K>, V>
extends AbstractMap <K, V>
implements Serializable, Cloneable, SortedMap <K, V>
{
	/** The maximum fanout (number of children) for a B+Tree node.
	 *  May wish to increase for better performance for Program 3.
	 */
	private static final int ORDER = 3;

	/** The floor of half the ORDER.
	 */
	private static final int MID = ORDER / 2;

	/** The debug flag
	 */
	private static final boolean DEBUG = true;

	/** The class for type K.
	 */
	private final Class <K> classK;

	/** The class for type V.
	 */
	private final Class <V> classV;

	/********************************************************************************
	 * This inner class defines nodes that are stored in the B+tree map.
	 */
	private int treesize = 1;
	private class Node
	{
		boolean   isLeaf;
		int       nKeys;
		K []      key;
		Object [] ref;

		@SuppressWarnings("unchecked")
		Node (boolean _isLeaf)
		{
			isLeaf = _isLeaf;
			nKeys  = 0;
			key    = (K []) Array.newInstance (classK, ORDER - 1);
			if (isLeaf)
			{
				//ref = (V []) Array.newInstance (classV, ORDER);
				ref = new Object [ORDER];
			} 
			else 
			{
				ref = (Node []) Array.newInstance (Node.class, ORDER);
			} // if
		} // constructor
	} // Node inner class

	/** The root of the B+Tree
	 */
	Node root;

	/** The first (leftmost) leaf in the B+Tree
	 */
	private final Node firstLeaf;

	/** The counter for the number nodes accessed (for performance testing).
	 */
	int count = 0;

	// Variable added by me
	public  K middlekey;

	public K largestLeft;

	/********************************************************************************
	 * Construct an empty B+Tree map.
	 * @param _classK  the class for keys (K)
	 * @param _classV  the class for values (V)
	 */
	public BpTreeMap (Class <K> _classK, Class <V> _classV)
	{
		classK    = _classK;
		classV    = _classV;
		root      = new Node (true);
		firstLeaf = root;
	} // constructor

	/********************************************************************************
	 * Return null to use the natural order based on the key type.  This requires the
	 * key type to implement Comparable.
	 */
	public Comparator <? super K> comparator () 
	{
		return null;
	} // comparator

	/********************************************************************************
	 * Return a set containing all the entries as pairs of keys and values.
	 * @return  the set view of the map
	 */
	public Set <Map.Entry <K, V>> entrySet ()
	{
		Set <Map.Entry <K, V>> enSet = new HashSet <> ();
		Node firstLeafNode = this.root;

		while(!firstLeafNode.isLeaf){
			firstLeafNode = (Node)firstLeafNode.ref[0];
		}

		while(firstLeafNode!=null){
			for(int i=0;i<firstLeafNode.nKeys;i++){
				SimpleEntry<K, V> simpleEntry = new SimpleEntry<K,V>(firstLeafNode.key[i], (V)firstLeafNode.ref[i]);
				enSet.add(simpleEntry);
			}
			firstLeafNode = (Node)firstLeafNode.ref[ORDER-1];

		}
		return enSet;
	} // entrySet 

	/********************************************************************************
	 * Given the key, look up the value in the B+Tree map.
	 * @param key  the key used for look up
	 * @return  the value associated with the key or null if not found
	 */
	@SuppressWarnings("unchecked")
	public V get (Object key)
	{
		return find ((K) key, root);
	} // get

	/********************************************************************************
	 * Put the key-value pair in the B+Tree map.
	 * @param key    the key to insert
	 * @param value  the value to insert
	 * @return  null, not the previous value for this key
	 */
	public V put (K key, V value)
	{
		insert (key, value, root);
		return null;
	} // put

	/********************************************************************************
	 * Return the first (smallest) key in the B+Tree map.
	 * @return  the first key in the B+Tree map.
	 */
	public K firstKey () 
	{
		Node tempRoot = this.root;
		if(tempRoot == null)
			return null;
		while(!tempRoot.isLeaf)
		{
			tempRoot = (Node)tempRoot.ref[0];
		}

		return tempRoot.key[0];
	} // firstKey

	/********************************************************************************
	 * Return the last (largest) key in the B+Tree map.
	 * @return  the last key in the B+Tree map.
	 */
	public K lastKey () 
	{
		Node tempRoot = this.root;
		if(tempRoot == null)
			return null;
		while(!tempRoot.isLeaf)
		{
			tempRoot = (Node)tempRoot.ref[tempRoot.nKeys];
		}

		return tempRoot.key[tempRoot.nKeys-1];
	} // lastKey

	/********************************************************************************
	 * Return the portion of the B+Tree map where key < toKey.
	 * @return  the submap with keys in the range [firstKey, toKey)
	 */
	public SortedMap <K,V> headMap (K toKey)
	{
		//  T O   B E   I M P L E M E N T E D
		/*    	Node temp = this.root;

    	BpTreeMap<K,V> newmap = new BpTreeMap<K,V>(this.classK,this.classV);

    	while(!(temp.isLeaf))
    	{
    		temp = (Node)temp.ref[0];
    	}
    	K currentKey = temp.key[0];
    	V currentRef;
    	boolean done = false;
    	while(currentKey.compareTo(toKey) < 0)
    	{
    		for(int i=0;i<temp.nKeys;i++)
    		{
    			currentKey = temp.key[i];
				currentRef = (V)temp.ref[i];
    			if(currentKey.compareTo(toKey)<0)
    			{

    				newmap.put(currentKey, currentRef);
    			}
    			else
    			{
    				done = true;
    				break;
    			}
    		}
    		if(done)
    			break;
    		temp = (Node)temp.ref[ORDER-1];
    	}
    	out.println("_________________________HEAD MAP____________________________");
    	newmap.print(newmap.root, 0);*/

		return this.subMap(this.firstKey(), toKey);
	} // headMap

	/********************************************************************************
	 * Return the portion of the B+Tree map where fromKey <= key.
	 * @return  the submap with keys in the range [fromKey, lastKey]
	 */
	public SortedMap <K,V> tailMap (K fromKey)
	{
		//  T O   B E   I M P L E M E N T E D
		return this.subMap(fromKey, this.lastKey());
	} // tailMap

	/********************************************************************************
	 * Return the portion of the B+Tree map whose keys are between fromKey and toKey,
	 * i.e., fromKey <= key < toKey.
	 * @return  the submap with keys in the range [fromKey, toKey)
	 */
	public SortedMap <K,V> subMap (K fromKey, K toKey)
	{
		//  T O   B E   I M P L E M E N T E D
		Node temp = this.root;

		BpTreeMap<K,V> newmap = new BpTreeMap<K,V>(this.classK,this.classV);
		while(!(temp.isLeaf))
		{
			temp = (Node)temp.ref[0];
		}
		K currentKey = temp.key[0];
		V currentRef;
		boolean done = false;
		while(currentKey.compareTo(fromKey) < 0)
		{
			for(int i=0;i<temp.nKeys;i++)
			{
				currentKey = temp.key[i];
				currentRef = (V)temp.ref[i];
				if(currentKey.compareTo(fromKey)<0)
				{
				}
				else
				{
					done = true;
					break;
				}
			}
			if(done)
				break;
			temp = (Node)temp.ref[ORDER-1];
		}
		done = false;
		while(currentKey.compareTo(toKey)<0)
		{
			for(int i=0;i<temp.nKeys;i++)
			{
				currentKey = temp.key[i];
				currentRef = (V)temp.ref[i];
				if(currentKey.compareTo(toKey)<0)
				{
					newmap.put(currentKey, currentRef);
				}
				else
				{
					done = true;
					break;
				}
			}
			if(done)
				break;
			temp = (Node)temp.ref[ORDER-1];
		}
		newmap.print(newmap.root, 0);
		return newmap;
	} // subMap

	/********************************************************************************
	 * Return the size (number of keys) in the B+Tree.
	 * @return  the size of the B+Tree
	 */
	public int size ()
	{
		//        int sum = 0;
		//        System.out.println(this.treesize);
		//  T O   B E   I M P L E M E N T E D

		return  this.treesize;
	} // size

	/********************************************************************************
	 * Print the B+Tree using a pre-order traveral and indenting each level.
	 * @param n      the current node to print
	 * @param level  the current level of the B+Tree
	 */
	@SuppressWarnings("unchecked") void print (Node n, int level)
	{
		//out.println ("BpTreeMap");
		// out.println ("-------------------------------------------");
		out.println(level);
		for (int j = 0; j < level; j++) out.print ("\t");
		if(level==0)out.print("ROOT -- > ");
		out.print ("[ . ");
		//System.out.println("IN THE PRINT METHOD"+n.nKeys);
		for (int i = 0; i < n.nKeys; i++) out.print (n.key [i] + " . ");
		out.println ("]");
		if ( ! n.isLeaf) {
			for (int i = 0; i <= n.nKeys; i++) print ((Node) n.ref [i], level + 1);
		} // if

		//out.println ("-------------------------------------------");
	} // print

	@Override
	public boolean equals(Object obj)
	{
		System.out.println("in equals of bptree: "+this.hashCode());
		return true;

	}


	/********************************************************************************
	 * Recursive helper function for finding a key in B+trees.
	 * @param key  the key to find
	 * @param ney  the current node
	 */
	@SuppressWarnings("unchecked")
	private V find (K key, Node n)
	{
		count++;
		for (int i = 0; i < n.nKeys; i++) {
			K k_i = n.key [i];
			if (key.compareTo (k_i) <= 0)
			{
				if (n.isLeaf)
				{
					//System.out.println(key.hashCode());
					//System.out.println(k_i.hashCode());
					return (key.compareTo (k_i)==0) ? (V) n.ref [i] : null;
				} 
				else 
				{
					return find (key, (Node) n.ref [i]);
				} // if
			} // if
		} // for
		return (n.isLeaf) ? null : find (key, (Node) n.ref [n.nKeys]);
	} // find

	/********************************************************************************
	 * Recursive helper function for inserting a key in B+trees.
	 * @param key  the key to insert
	 * @param ref  the value/node to insert
	 * @param n    the current node
	 * @return  the node inserted into (may wish to return more information)
	 */
	private Node insert(K key, V ref, Node n)
	{
		boolean inserted = false;
		if (n.isLeaf) 
		{                                  // handle leaf node
			if (n.nKeys < ORDER - 1) 
			{
				for (int i = 0; i < n.nKeys; i++) 
				{
					K k_i = n.key [i];
					if (key.compareTo (k_i) < 0) 
					{
						wedgeL (key, ref, n, i);
						inserted = true;
						return null;
						//break;
					} 
					else if (key.equals (k_i)) 
					{
						out.println ("BpTreeMap.insert: attempt to insert duplicate key = " + key);
						inserted = true;
						return null;
						//break;
					} // if
				} // for
				if (! inserted) wedgeL (key, ref, n, n.nKeys);
			} 
			else 
			{
				V if_found = find(key,root);
				if(if_found != null)
				{
					out.println("Key already found");
					return null;
				}
				Node sib = splitL (key, ref, n);
				if(root.isLeaf)
				{
					this.treesize += 1;
					//                	System.out.println("Calling recursively");
					Node rootnode = new Node(false);
					this.root = rootnode;
					this.root.ref[0] = n;
					this.root.ref[1] = sib;
					this.root.key[0] = n.key[n.nKeys-1];
					this.root.nKeys += 1;
					System.out.println(this.root.key[0]);
				}
				else
				{
					return sib;        // JUST A THOUGHT
				}

				//  T O   B E   I M P L E M E N T E D

			} // if

		} 
		else 
		{							// handle internal node
			boolean insertion = false;
			int insert_pos = 0;
			for(int i=0;i < n.nKeys;i++)
			{
				K key_i = n.key[i];
				if(key.compareTo(key_i) < 0)
				{
					insert_pos = i;
					insertion = true;
					break;
				}
			}
			if(!insertion)
				insert_pos = n.nKeys;
			int i = insert_pos;
			Node newchild = insert(key,ref,(Node)n.ref[i]);
			if(newchild == null)
			{
				//DO NOTHING
				return null;
			}
			else
			{
				//THE CASE WHEN THE BELOW LEVEL SPLITS INTO TWO PARTS
				if(n.nKeys == ORDER-1)
				{
					if(n.equals(this.root))
					{
						this.treesize += 1;
						Node newrootnode = new Node(false);
						Node newrootchild = null;
						if(newchild.isLeaf)
							newrootchild = splitI(largestLeft,newchild,n);
						else
							newrootchild = splitI(middlekey,newchild,n);
						newrootnode.ref[0] = n;
						newrootnode.ref[1] = newrootchild;
						newrootnode.key[0] = middlekey;
						newrootnode.nKeys += 1;
						this.root = newrootnode;
						return newrootnode;
					}
					if(!newchild.isLeaf)
					{
						Node toUpperlevel = splitI(middlekey,newchild,n);
						return toUpperlevel;
					}
					else
					{
						Node toUpperlevel = splitI(largestLeft,newchild,n);
						return toUpperlevel;
					}
				}
				else
				{
					if(!newchild.isLeaf)
					{
						wedgeI(middlekey,newchild,n,i);
						return null;
					}
					else
					{
						wedgeI(largestLeft,newchild,n,i);
						return null;
					}
				}

			}
		}
		return null;
	}
	//        }
	//mmmm
	/*        	if(!root_insert)
        	{
        		Node newchild2 = insert(key,ref,(Node)(n.ref[n.nKeys]));
        	}
        }

//	        			Node nodeofinterest = ((Node)n.ref[i]);
	        			//Finding the middle node
	        			if(n.equals(root))
	        			{
	        				K newrootkey;
	        				int leftnodekeys = nodeofinterest.nKeys;
	        				int rightnodekeys = newchild.nKeys;
	        				int middle = (leftnodekeys+rightnodekeys)/2;
	        				if(middle <= leftnodekeys-1)
	        				{
	        					newrootkey = nodeofinterest.key[middle-1];
	        				}
	        				else
	        				{
	        					newrootkey = newchild.key[middle-leftnodekeys];
	        				}
	        				out.println("Found the middle node to be "+newrootkey);
	        				wedgeI(newrootkey,newchild,newchild,i+1);	
	        			}

	        			K maxvalue = nodeofinterest.key[nodeofinterest.nKeys-1];
	        			wedgeI(maxvalue,newchild,n,1);
	        			n.ref[n.nKeys] = newchild;
	        			n.key[n.nKeys] = newchild.key[newchild.nKeys-1];
	        			n.nKeys += 1;
	   }
	        	System.out.println("Calling Recursively");
	        	if(!root_insert)
	        	{
	        		Node newchild = insert(key,ref,(Node)n.ref[n.nKeys]); // NEW CHILD RETURNED BY THE LOWER LEVEL
	        		//WEDGE THE NEWCHILD AND KEY AT THE POSITION N.NKEYS. 
	        		// GOT TO POINT THE LAST ADDRESS TO THE NEW CHILD
	        	}
        	}//  T O   B E   I M P L E M E N T E D
        	else
        	{
        		// TRY TO INSERT INTO THE LAST REFERENCE NODE AND CHECK FOR THE RETURN VALUE
        		// Node childnode = insert(key,ref,(Node)n.ref[ORDER-1]);
        		// if(childnode == null)
        		// DO NOTHING
        		// else
        		// splitI(K,node,node);
        		// IF THE LAST INSERTION RETURNS A CHILD, SINCE OUR ROOT IS FULL WE HAVE TO SPLIT IT
        		// GUESS SHOULD DO SPLIT INTERNAL HERE
        		// TAKE THE RETURNED NODE. CREATE A NEW ROOT AND MARK POINTERS APPROPRIATELY
        	}


        }// if
	 */
	//       if (DEBUG) print (root, 0);
	//       return null;                                     // FIX: return useful information
	//    } // insert

	/********************************************************************************
	 * Wedge the key-ref pair into leaf node n.
	 * @param key  the key to insert
	 * @param ref  the value/node to insert
	 * @param n    the current node
	 * @param i    the insertion position within node n
	 */
	private void wedgeL (K key, V ref, Node n, int i)
	{
		for (int j = n.nKeys; j > i; j--) {
			n.key [j] = n.key [j-1];
			n.ref [j] = n.ref [j-1];
		} // for
		n.key [i] = key;
		n.ref [i] = ref;
		n.nKeys++;
	} // wedgeL

	/**************************	`******************************************************
	 * Wedge the key-ref pair into internal node n.
	 * @param key  the key to insert
	 * @param ref  the value/node to insert
	 * @param n    the current node
	 * @param i    the insertion position within node n
	 */
	private void wedgeI (K key, Node ref, Node n, int i)   //Changed the method signature. Changed the type V to Node.
	{
		//        out.println ("wedgeI not implemented yet");
		Object tempref = n.ref[i];
		for(int j=n.nKeys;j>i;j--)
		{
			n.key[j] = n.key[j-1];
			n.ref[j] = n.ref[j-1];
		}
		n.key[i] = key;
		n.ref[i] = tempref;
		n.ref[i+1] = ref;
		n.nKeys += 1;
		//n.key[n.nKeys] = null;
		// n.nKeys -= 1;
		//  T O   B E   I M P L E M E N T E D
	} // wedgeI

	/********************************************************************************
	 * Split leaf node n and return the newly created right sibling node rt.
	 * Split first (MID keys for both node n and node rt), then add the new key and ref.
	 * @param key  the new key to insert
	 * @param ref  the new value/node to insert
	 * @param n    the current node
	 * @return  the right sibling node (may wish to provide more information)
	 */
	private Node splitL (K key, V ref, Node n)
	{
		this.treesize += 1;
		//        out.println ("splitL not implemented yet");
		Node rt = new Node (true);
		int rt_index = 0; 
		int temp = n.nKeys;
		for(int i = MID;i < temp;i++)
		{
			rt.key[rt_index] = n.key[i];
			rt.ref[rt_index] = n.ref[i];
			n.key[i] = null;
			n.ref[i] = null;
			rt_index += 1;
			n.nKeys -= 1;
			rt.nKeys += 1;
		}
		n.ref[ORDER-1] = rt;
		boolean left = false;
		boolean insert_success = false;
		if(key.compareTo(n.key[n.nKeys-1]) < 0 )
		{
			left = true;
		}
		if(left)
		{
			for(int i=0;i < n.nKeys && !insert_success;i++)
			{
				K k_i = n.key[i];
				if(key.compareTo(k_i) < 0)
				{
					wedgeL(key,ref,n,i);
					insert_success = true;
				}
			}
			if(!insert_success)
				wedgeL(key,ref,n,n.nKeys);
			this.largestLeft = n.key[n.nKeys-1];
		}
		else
		{
			this.largestLeft = n.key[n.nKeys-1];
			for(int i=0;i<rt.nKeys && !insert_success;i++)
			{
				K key_i = rt.key[i];
				if(key.compareTo(key_i)<0)
				{
					wedgeL(key,ref,rt,i);
					insert_success = true;
				}
			}
			if(!insert_success)
				wedgeL(key,ref,rt,rt.nKeys);
		}
		//  T O   B E   I M P L E M E N T E D

		return rt;
	} // splitL

	/********************************************************************************
	 * Split internal node n and return the newly created right sibling node rt.
	 * Split first (MID keys for node n and MID-1 for node rt), then add the new key and ref.
	 * @param key  the new key to insert
	 * @param ref  the new value/node to insert
	 * @param n    the current node
	 * @return  the right sibling node (may wish to provide more information)
	 */
	private Node splitI (K key, Node ref, Node n)
	{
		this.treesize += 1;
		//        out.println ("splitI not implemented yet");
		Node rt = new Node (false);
		int rt_index = 0;
		int middleindex = (ORDER-1)/2;
		int middlekeyindex = middleindex;
		K toCompare = n.key[middleindex];
		boolean left = false;
		int temp = n.nKeys;
		if(key.compareTo(toCompare) < 0)
		{
			middlekey = n.key[middleindex-1];
			middlekeyindex -= 1 ;
			left = true;
		}
		else
		{
			middlekey = toCompare; 
		}
		if(left)
		{
			for(int i=middleindex;i<temp;i++)
			{
				rt.key[rt_index] = n.key[i];
				rt.ref[rt_index] = n.ref[i];
				rt.key[rt_index] = null;
				rt.ref[rt_index] = null;
				n.nKeys -= 1;
				rt.nKeys += 1;
			}
			rt.ref[rt.nKeys] = n.ref[ORDER-1];
			n.ref[ORDER-1] = null;
			//FINDING POSITION
			int insert_pos = 0;
			boolean found = false;
			for(int i=0;i<n.nKeys && !found;i++)
			{
				K key_i = n.key[i];
				if(key.compareTo(key_i)<0)
				{
					insert_pos = i;
					found = true;
				}
			}
			if(!found)
				insert_pos = n.nKeys;
			n.key[middlekeyindex] = null;
			n.nKeys -= 1;
			wedgeI(key,ref,n,insert_pos);
		}
		else
		{
			for(int i=middleindex+1;i<temp;i++)
			{
				rt.key[rt_index] = n.key[i];
				rt.ref[rt_index] = n.ref[i];
				rt.key[rt_index] = null;
				rt.ref[rt_index] = null;
				n.nKeys -= 1;
				rt.nKeys += 1;
			}
			rt.ref[rt.nKeys] = n.ref[ORDER-1];
			n.ref[ORDER-1] = null;
			//FINDING POSITION
			boolean found = false;
			int insert_pos = 0;
			for(int i=0;i<rt.nKeys && !found;i++)
			{
				K key_i = rt.key[i];
				if(key.compareTo(key_i)<0)
				{
					insert_pos = i;
					found = true;
				}
			}
			if(!found)
				insert_pos = rt.nKeys;
			n.key[middlekeyindex] = null;
			n.nKeys -= 1;
			wedgeI(key,ref,rt,insert_pos);
		}
		return rt;
	}


	//COMMENTED CODE
	/*K midKey = n.key[MID];
        for(int i=MID+1;i<temp;i++)
        {
        	rt.key[rt_index] = n.key[i];
        	rt.ref[rt_index] = n.ref[i];
        	n.key[i] = null;
        	n.ref[i] = null;
        	n.nKeys -= 1;
        	rt.nKeys += 1;
        }
        //rt.ref[rt.nKeys] = n.ref[ORDER-1];
        //n.ref[ORDER-1] = null;
        boolean left = false;
        boolean inserted = false;
        if(key.compareTo(midKey)<=0)
        	left = true;
        if(left)
        {
        	for(int i=0;i < n.nKeys;i++)
        	{
        		K k_i = n.key[i];
        		if(key.compareTo(k_i) <= 0)
        		{
        			wedgeI(key,ref,n,i);
        			inserted = true;
        		}
        	}
        }
        rt.ref[rt.nKeys] = n.ref[ORDER-1]; 
        middlekey = n.key[n.nKeys-1];
        n.ke
	 */
	//  T O   B E   I M P L E M E N T E D


	//    } // splitI

	/********************************************************************************
	 * The main method used for testing.
	 * @param  the command-line arguments (args [0] gives number of keys to insert)
	 */
	public static void main (String [] args)
	{
		int totalKeys    = 200;
		boolean RANDOMLY = false;

		BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
		if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

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
		//out.println(bpt.entrySet().size());
		out.println ("Average number of nodes accessed = " + bpt.count / (double) totalKeys);
	} // main

} // BpTreeMap class
