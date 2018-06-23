
/*******************************************************************************
 * @file  FileList.java
 *
 * @author   John Miller
 */

import java.io.*;
import java.io.ObjectOutputStream.PutField;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import static java.lang.System.out;
import java.util.*;

/*******************************************************************************
 * This class allows data tuples/tuples (e.g., those making up a relational
 * table) to be stored in a random access file. This implementation requires
 * that each tuple be packed into a fixed length byte array.
 */
public class FileList extends AbstractList<Comparable[]> implements List<Comparable[]>, RandomAccess, Iterable<Comparable[]> {

	
	private List<Comparable[]> tuples;
	/**
	 * File extension for data files.
	 */
	private static final String EXT = ".dat";

	/**
	 * The random access file that holds the tuples.
	 */
	private RandomAccessFile file;

	/**
	 * The name of table.
	 */
	private final String tableName;

	/**
	 * The number bytes required to store a "packed tuple"/record.
	 */
	private final int recordSize;

	/**
	 * Counter for the number of tuples in this list.
	 */
	private int nRecords = 0;

	/**
	 * String Byte array size
	 */
	private final static int str_bytearray_size = Table.str_bytearray_size;
	
//	private final Map<> map; 
	
	private  Class[] domain;

	/***************************************************************************
	 * Construct a FileList.
	 * 
	 * @param _tableName
	 *            the name of the table
	 * @param _recordSize
	 *            the size of tuple in bytes.
	 */
	public FileList(String _tableName, int _recordSize, Class[] domain) {

		tableName = _tableName;
		recordSize = _recordSize;
		this.domain  = domain;

		try {
			file = new RandomAccessFile(tableName + EXT, "rw");
			System.out.println("Made");
		} catch (FileNotFoundException ex) {
			file = null;
			out.println("FileList.constructor: unable to open - " + ex);
		} // try

	} // constructor

	/***************************************************************************
	 * Add a new tuple into the file list by packing it into a record and
	 * writing this record to the random access file. Write the record either at
	 * the end-of-file or into a empty slot.
	 * 
	 * @author Yash
	 * @param tuple
	 *            to add
	 * @return whether the addition succeeded
	 */
	public boolean add(Comparable[] tuple) {
		long file_size;
		byte[] record = pack(tuple); // FIX: table.pack (tuple);
		// System.out.println("RECORRRDD " + Arrays.toString(record));
        System.out.print("Called My ADD");
		if (record.length != recordSize) {
			out.println("FileList.add: wrong record size " + record.length);
			return false;
		} // if
		else {

			try {
				file_size = file.length();

				System.out.println("Before write pointer    " + file_size);
				file.seek(file_size);
				file.write(record);
				System.out.println("After write pointer     " + file.length());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

	} // add_tuple

	/***************************************************************************
	 * Get the ith tuple by seeking to the correct file position and reading the
	 * record.
	 * 
	 * @author Yash
	 * @param i,
	 *            the index of the tuple to get
	 * @return the ith tuple
	 */
	public Comparable[] get (int i) {
		byte[] record = new byte[recordSize];
		int file_counter = i * recordSize;
		try {
			// System.out.println(file.getFilePointer());
			file.seek(file_counter);
			file.readFully(record);
			// file.seek(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("Record is :"+ Arrays.toString(record));

		System.out.println("Final Tuple is  :" + Arrays.toString(unpack(record)));

		return unpack(record);

		// FIX: table.unpack (record);
	} // get

	/***************************************************************************
	 * Return the size of the file list in terms of the number of
	 * tuples or records.
	 * 
	 * @author Yash
	 * @return the number of tuples
	 */
	public int size() {
		try {
			nRecords = (int) file.length() / recordSize;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nRecords;
	} // size

	/***************************************************************************
	 * Close the file.
	 */
	public void close() {
		try {
			file.close();
		} catch (IOException ex) {
			out.println("FileList.close: unable to close - " + ex);
		} // try

	} // close

	///////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	/***
	 * @author Yash
	 * @param Comarable
	 *            tuple
	 * @return byte[] of the tuple that is required to be packed
	 */
	public byte[] pack(Comparable[] tuple) {
		Class[] class_temp = domain;
		int index_counter = -1;
		byte[] bytes = new byte[recordSize];

		for (int i = 0; i < class_temp.length; i++) {
			if (class_temp[i].getName().equals("java.lang.String")) {
				byte[] temp_byte = toByteArrayfromString((String) tuple[i]);
				System.arraycopy(temp_byte, 0, bytes, index_counter + 1, temp_byte.length);
				index_counter = index_counter + temp_byte.length;
				// System.out.println(Arrays.toString(bytes));
			}
			if (class_temp[i].getName().equals("java.lang.Double")) {
				byte[] temp_byte = toByteArrayfromDouble((double) tuple[i]);
				System.arraycopy(temp_byte, 0, bytes, index_counter + 1, temp_byte.length);
				index_counter = index_counter + temp_byte.length;

			}
			if (class_temp[i].getName().equals("java.lang.Integer")) {
				byte[] temp_byte = toByteArrayfromInt((int) tuple[i]);
				// System.out.println(Arrays.toString(temp_byte));
				System.arraycopy(temp_byte, 0, bytes, index_counter + 1, temp_byte.length);
				index_counter = index_counter + temp_byte.length;
				// System.out.println(Arrays.toString(bytes));
			}
			if (class_temp[i].getName().equals("java.lang.Long")) {
				byte[] temp_byte = toByteArrayfromLong((long) tuple[i]);
				System.arraycopy(temp_byte, 0, bytes, index_counter + 1, temp_byte.length);
				index_counter = index_counter + temp_byte.length;
			}
			if (class_temp[i].getName().equals("java.lang.Float")) {
				byte[] temp_byte = toByteArrayfromFloat((float) tuple[i]);
				System.arraycopy(temp_byte, 0, bytes, index_counter + 1, temp_byte.length);
				index_counter = index_counter + temp_byte.length;
			}
			if (class_temp[i].getName().equals("java.lang.Short")) {
				byte[] temp_byte = toByteArrayfromShort((short) tuple[i]);
				System.arraycopy(temp_byte, 0, bytes, index_counter + 1, temp_byte.length);
				index_counter = index_counter + temp_byte.length;
			}
			if (class_temp[i].getName().equals("java.lang.Character")) {
				byte[] temp_byte = toByteArrayfromChar((char) tuple[i]);
				System.arraycopy(temp_byte, 0, bytes, index_counter + 1, temp_byte.length);
				index_counter = index_counter + temp_byte.length;
				// System.out.println("Char array " +
				// Arrays.toString(temp_byte));

			}
			if (class_temp[i].getName().equals("java.lang.Byte")) {
				bytes[index_counter + 1] = (byte) tuple[i];
				index_counter = index_counter + 1;

			}
		}

		// System.out.println(Arrays.toString(bytes));
		return bytes;
	}
	
	/**
	 * @author Yash
	 * @param byte[] to unpack
	 * @return unpacked Comparable[] tuple
	 */

	public Comparable[] unpack(byte[] b) {
		Class[] class_temp = this.domain;
		Comparable[] tuple = new Comparable[this.domain.length];
		int index_counter = -1;
		for (int i = 0; i < class_temp.length; i++) {
			if (class_temp[i].getName().equals("java.lang.String")) {
				byte[] temp_byte = new byte[str_bytearray_size];
				System.arraycopy(b, index_counter + 1, temp_byte, 0, str_bytearray_size);
				String str_temp = toString(temp_byte);
				index_counter = index_counter + str_bytearray_size;
				tuple[i] = str_temp;
				// System.out.println(str_temp);
			}
			if (class_temp[i].getName().equals("java.lang.Double")) {
				byte[] temp_byte = new byte[8];
				System.arraycopy(b, index_counter + 1, temp_byte, 0, 8);
				double double_temp = toDouble(temp_byte);
				index_counter = index_counter + 8;
				tuple[i] = double_temp;
				// System.out.println(double_temp);
			}
			if (class_temp[i].getName().equals("java.lang.Integer")) {
				byte[] temp_byte = new byte[4];
				System.arraycopy(b, index_counter + 1, temp_byte, 0, 4);
				int int_temp = toInt(temp_byte);
				index_counter = index_counter + 4;
				tuple[i] = int_temp;
			}
			if (class_temp[i].getName().equals("java.lang.Long")) {
				byte[] temp_byte = new byte[8];
				System.arraycopy(b, index_counter + 1, temp_byte, 0, 8);
				long long_temp = toLong(temp_byte);
				index_counter = index_counter + 8;
				tuple[i] = long_temp;
			}
			if (class_temp[i].getName().equals("java.lang.Float")) {
				byte[] temp_byte = new byte[4];
				System.arraycopy(b, index_counter + 1, temp_byte, 0, 4);
				float float_temp = toFloat(temp_byte);
				index_counter = index_counter + 4;
				tuple[i] = float_temp;
			}
			if (class_temp[i].getName().equals("java.lang.Short")) {
				byte[] temp_byte = new byte[2];
				System.arraycopy(b, index_counter + 1, temp_byte, 0, 2);
				short short_temp = toShort(temp_byte);
				index_counter = index_counter + 2;
				tuple[i] = short_temp;
			}
			if (class_temp[i].getName().equals("java.lang.Character")) {
				byte[] temp_byte = new byte[2];
				System.arraycopy(b, index_counter + 1, temp_byte, 0, 2);
				char char_temp = toChar(temp_byte);
				index_counter = index_counter + 2;
				tuple[i] = char_temp;
			}
			if (class_temp[i].getName().equals("java.lang.Byte")) {

				tuple[i] = b[index_counter + 1];
				index_counter = index_counter + 1;
			}
		}

		// System.out.println(Arrays.toString(tuple));
		return tuple;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	 public Iterator<Comparable[]> iterator(){
		 Iterator<Comparable[]> it = new Iterator<Comparable[]>() {

	            private int currentIndex = 0;

	            @Override
	            public boolean hasNext() {
	                return currentIndex < size() && get(currentIndex) != null;
	            }

	            @Override
	            public Comparable[] next() {
	                return get(currentIndex++);
	            }

//	            @Override
//	            public void remove() {
//	                throw new UnsupportedOperationException();
//	            }
	        };
	        return it;
	    }
		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Conversion of DataType into Byte array and vice versa
	/////////////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////

	/*******************************************************************************
	 * Convert Double type attribute into Byte array
	 * 
	 * @author Yash
	 * @param Double
	 *            value
	 * @return byte array
	 */

	public static byte[] toByteArrayfromDouble(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}// toByte

	/**
	 * Convert Double byte array to Double value
	 * 
	 * @author Yash
	 * @param bytes
	 *            for Double
	 * @return Double Value
	 */
	public static double toDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}// toDouble
		///////////////////////////////////////////////////////////////////////////////////

	// Float put
	/**
	 * @author Yash
	 * @param float
	 *            value to convert into byte array
	 * @return byte[] of float value
	 */
	public static byte[] toByteArrayfromFloat(float value) {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putFloat(value);
		return bytes;
	}

	// Float get
	/**
	 * @author Yash
	 * @param bytes[]
	 *            to be converted into float
	 * @return float value of byte[]
	 */
	public static float toFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getFloat();
	}
	////////////////////////////////////////////////////////////////////////////////////

	// Long put
	/**
	 * @author Yash
	 * @param Long
	 *            value to convert into byte array
	 * @return byte[] of float Long
	 */
	public static byte[] toByteArrayfromLong(long value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putLong(value);
		return bytes;
	}

	// Long get
	/**
	 * @author Yash
	 * @param bytes[]
	 *            to be converted into Long
	 * @return Long value of byte[]
	 */
	public static long toLong(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getLong();
	}
	///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @author Yash
	 * @param Int
	 *            value to convert into byte array
	 * @return byte[] of Int value
	 */
	public static byte[] toByteArrayfromInt(int value) {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(value);
		return bytes;
	}

	/**
	 * @author Yash
	 * @param bytes[]
	 *            to be converted into Long
	 * @return Long value of byte[]
	 */
	public static int toInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}
	///////////////////////////////////////////////////////////////////////////////////////

	// Put Short
	/**
	 * @author Yash
	 * @param Short
	 *            value to convert into byte array
	 * @return byte[] of Short value
	 */
	public static byte[] toByteArrayfromShort(short value) {
		byte[] bytes = new byte[2];
		ByteBuffer.wrap(bytes).putShort(value);
		return bytes;
	}

	// get Short
	/**
	 * @author Yash
	 * @param bytes[]
	 *            to be converted into Long
	 * @return Long value of byte[]
	 */
	public static short toShort(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getShort();
	}
	//////////////////////////////////////////////////////////////////////////////////////////

	// put Char
	/**
	 * @author Yash
	 * @param Char
	 *            value to convert into byte array
	 * @return byte[] of char value
	 */
	public static byte[] toByteArrayfromChar(char value) {
		byte[] bytes = new byte[2];
		// System.out.println(bytes.length);
		ByteBuffer.wrap(bytes).putChar(value);
		// System.out.println(bytes.length);
		return bytes;
	}

	// get Char
	/**
	 * @author Yash
	 * @param bytes[]
	 *            to be converted into char
	 * @return char value of byte[]
	 */
	public static char toChar(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getChar();
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////

	// put String
	/**
	 * @author Yash
	 * @param String
	 *            value to convert into byte array
	 * @return byte[] of String value
	 */
	public static byte[] toByteArrayfromString(String value) {

		byte[] dst = new byte[str_bytearray_size]; // Can take String upto 127
		// length
		byte[] src = value.getBytes();// get the String Byte array
		// System.out.println(Arrays.toString(src));

		// get lengh of the String byte array, i.e. the length of "value" and
		// save
		// it to the first position of the main byte array
		dst[0] = (byte) src.length;
		System.arraycopy(src, 0, dst, 1, src.length);
		// System.out.println("Byte array for string: " + Arrays.toString(dst));
		return dst;
	}

	// get String
	/**
	 * @author Yash
	 * @param bytes[]
	 *            to be converted into String
	 * @return String value of byte[]
	 */
	public static String toString(byte[] bytes) {
		int temp_int = bytes[0];
		// System.out.println(temp_int);
		byte[] temp_byte = new byte[temp_int];
		System.arraycopy(bytes, 1, temp_byte, 0, temp_int);
		// System.out.println(Arrays.toString(temp_byte));
		String str = new String(temp_byte);
		// System.out.println("String is " + str);
		return str;
	}
	
	public static void main(String args[]) {
		Table movieStar = new Table("movieStar", "name address gender birthdate", "String String Character String",
				"name");
		// System.out.println(get_recordsize(movieStar));
		//
		// String rand = "test.dat";
		// try {
		// RandomAccessFile file = new RandomAccessFile ( rand , "rw");
		//
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();

		// byte[] temp = toByteArrayfromString("YIwanna be bkhsa jasgdfkjh
		// jhsgdf");
		// toString(temp);
		// toByteArrayfromInt(4);
		// System.out.println(Arrays.toString(temp));
		// System.out.println(toInt(temp));
	}
	//
	// }

	

} // FileList class
