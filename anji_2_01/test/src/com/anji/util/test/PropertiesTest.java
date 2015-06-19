/*
 * Created on Apr 26, 2005 by Philip Tucker
 */
package com.anji.util.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.anji.util.Properties;
import com.anji.util.Randomizer;

/**
 * @author Philip Tucker
 */
public class PropertiesTest extends TestCase {

private Properties uut;

private final static String STRING_KEY = "key.string";

private final static String BOOL_KEY = "key.bool";

private final static String SHORT_KEY = "key.short";

private final static String INT_KEY = "key.int";

private final static String LONG_KEY = "key.long";

private final static String FLOAT_KEY = "key.float";

private final static String DOUBLE_KEY = "key.double";

private final static String DIR_KEY = "key.dir";

private final static String IN_FILE_KEY = "key.file.in";

private final static String OUT_FILE_KEY = "key.file.out";

private final static String RSRC_KEY = "key.rsrc";

private final static String CLASS_KEY = "key";

private final static String STRING_VAL = "hello, world";

private final static String OBJECT_LIST_KEY = "object.list";

private final static Class OBJECT_1_CLASS = String.class;

private final static Class OBJECT_2_CLASS = Randomizer.class;

private final static String OBJECT_LIST_VAL = "AAA,BBB";

private final static Map OBJECT_LIST_PROPS = new HashMap();

private final static long RAND_SEED_VAL = 1000;

static {
	OBJECT_LIST_PROPS.put( OBJECT_LIST_KEY, OBJECT_LIST_VAL );
	OBJECT_LIST_PROPS.put( "AAA.class", OBJECT_1_CLASS.toString().substring( "class ".length() ) );
	OBJECT_LIST_PROPS.put( "BBB.class", OBJECT_2_CLASS.toString().substring( "class ".length() ) );
	OBJECT_LIST_PROPS.put( "BBB.random.seed", Long.toString( RAND_SEED_VAL ) );
}

private final static Boolean BOOL_VAL = Boolean.TRUE;

private final static Short SHORT_VAL = new Short( (short) 25 );

private final static Integer INT_VAL = new Integer( 25000 );

private final static Long LONG_VAL = new Long( 25000000 );

private final static Float FLOAT_VAL = new Float( 2.3f );

private final static Double DOUBLE_VAL = new Double( 23232323.32323232 );

private final static File DIR_VAL = new File( "./test/images" );

private final static File IN_FILE_VAL = new File( "./test/images/test.jpg" );

private final static File OUT_FILE_VAL = new File( "./test/images/another_test.jpg" );

private final static String RSRC_VAL = "test.properties";

private final static String PREFIX = "abc.";

/**
 * ctor
 */
public PropertiesTest() {
	this( PropertiesTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public PropertiesTest( String name ) {
	super( name );
}

/**
 * @see junit.framework.TestCase#setUp()
 */
public void setUp() throws Exception {
	java.util.Properties p = new java.util.Properties();
	p.load( ClassLoader.getSystemResourceAsStream( "test.properties" ) );

	uut = new Properties();
	uut.put( STRING_KEY, STRING_VAL );
	uut.put( BOOL_KEY, BOOL_VAL.toString() );
	uut.put( SHORT_KEY, SHORT_VAL.toString() );
	uut.put( INT_KEY, INT_VAL.toString() );
	uut.put( LONG_KEY, LONG_VAL.toString() );
	uut.put( FLOAT_KEY, FLOAT_VAL.toString() );
	uut.put( DOUBLE_KEY, DOUBLE_VAL.toString() );
	uut.put( DIR_KEY, DIR_VAL.toString() );
	uut.put( IN_FILE_KEY, IN_FILE_VAL.toString() );
	uut.put( OUT_FILE_KEY, OUT_FILE_VAL.toString() );
	uut.put( RSRC_KEY, RSRC_VAL.toString() );
	uut.put( CLASS_KEY + Properties.CLASS_SUFFIX, String.class.toString().substring(
			"class ".length() ) );
	uut.put( PREFIX + STRING_KEY, PREFIX + STRING_VAL );
	uut.put( OBJECT_LIST_KEY, OBJECT_LIST_VAL );
	uut.putAll( OBJECT_LIST_PROPS );
}

/**
 * test getters
 * @throws Exception
 */
public void testAccessors() throws Exception {
	// String
	assertEquals( "wrong string value", STRING_VAL, uut.getProperty( STRING_KEY ) );
	assertEquals( "wrong string value w/ default", STRING_VAL, uut
			.getProperty( STRING_KEY, "abc" ) );
	try {
		uut.getProperty( STRING_KEY + ".not" );
		fail( "missing string property value should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default string value", "abc", uut.getProperty( STRING_KEY + ".not",
			"abc" ) );

	// boolean
	assertEquals( "wrong bool value", BOOL_VAL.booleanValue(), uut.getBooleanProperty( BOOL_KEY ) );
	assertEquals( "wrong bool value w/ default", BOOL_VAL.booleanValue(), uut.getBooleanProperty(
			BOOL_KEY, !( BOOL_VAL.booleanValue() ) ) );
	try {
		uut.getBooleanProperty( BOOL_KEY + ".not" );
		fail( "missing bool property value should throw exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default bool value", BOOL_VAL.booleanValue(), uut.getBooleanProperty(
			BOOL_KEY + ".not", BOOL_VAL.booleanValue() ) );

	// short
	assertEquals( "wrong short value", SHORT_VAL.shortValue(), uut.getShortProperty( SHORT_KEY ) );
	assertEquals( "wrong short value w/ default", SHORT_VAL.shortValue(), uut.getShortProperty(
			SHORT_KEY, (short) ( SHORT_VAL.shortValue() + 1 ) ) );
	try {
		uut.getShortProperty( SHORT_KEY + ".not" );
		fail( "missing short property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default short value", SHORT_VAL.shortValue(), uut.getShortProperty(
			SHORT_KEY + ".not", SHORT_VAL.shortValue() ) );

	// int
	assertEquals( "wrong int value", INT_VAL.intValue(), uut.getIntProperty( INT_KEY ) );
	assertEquals( "wrong int value w/ default", INT_VAL.intValue(), uut.getIntProperty( INT_KEY,
			INT_VAL.intValue() + 1 ) );
	try {
		uut.getIntProperty( INT_KEY + ".not" );
		fail( "missing int property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default int value", INT_VAL.intValue(), uut.getIntProperty( INT_KEY
			+ ".not", INT_VAL.intValue() ) );

	// long
	assertEquals( "wrong long value", LONG_VAL.longValue(), uut.getLongProperty( LONG_KEY ) );
	assertEquals( "wrong long value w/ default", LONG_VAL.longValue(), uut.getLongProperty(
			LONG_KEY, LONG_VAL.longValue() + 1 ) );
	try {
		uut.getLongProperty( LONG_KEY + ".not" );
		fail( "missing long property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default long value", LONG_VAL.longValue(), uut.getLongProperty( LONG_KEY
			+ ".not", LONG_VAL.longValue() ) );

	// float
	assertEquals( "wrong float value", FLOAT_VAL.floatValue(), uut.getFloatProperty( FLOAT_KEY ),
			0.0f );
	assertEquals( "wrong float value w/ default", FLOAT_VAL.floatValue(), uut.getFloatProperty(
			FLOAT_KEY, FLOAT_VAL.floatValue() + 1 ), 0.0f );
	try {
		uut.getFloatProperty( FLOAT_KEY + ".not" );
		fail( "missing float property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default float value", FLOAT_VAL.floatValue(), uut.getFloatProperty(
			FLOAT_KEY + ".not", FLOAT_VAL.floatValue() ), 0.0f );

	// double
	assertEquals( "wrong double value", DOUBLE_VAL.doubleValue(), uut
			.getDoubleProperty( DOUBLE_KEY ), 0.0d );
	assertEquals( "wrong double value w/ default", DOUBLE_VAL.doubleValue(), uut
			.getDoubleProperty( DOUBLE_KEY, DOUBLE_VAL.doubleValue() + 1 ), 0.0d );
	try {
		uut.getDoubleProperty( DOUBLE_KEY + ".not" );
		fail( "missing double property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default double value", DOUBLE_VAL.doubleValue(), uut.getDoubleProperty(
			DOUBLE_KEY + ".not", DOUBLE_VAL.doubleValue() ), 0.0d );

	// directory
	assertEquals( "wrong directory value", DIR_VAL, uut.getDirProperty( DIR_KEY ) );
	try {
		uut.getDirProperty( DIR_KEY + ".not" );
		fail( "missing directory property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}

	// file input
	byte[] expectedBytes = new byte[ 1024 ];
	byte[] actualBytes = new byte[ 1024 ];
	FileInputStream fin = new FileInputStream( IN_FILE_VAL );
	fin.read( expectedBytes );
	fin.close();
	fin = uut.getFileInputProperty( IN_FILE_KEY );
	fin.read( actualBytes );
	fin.close();
	assertEquals( "wrong file value", expectedBytes, actualBytes );
	try {
		fin = uut.getFileInputProperty( IN_FILE_KEY + ".not" );
		fail( "missing file property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	finally {
		if ( fin != null )
			fin.close();
	}

	// file output
	String data = "hello, world\n";
	FileOutputStream fout = uut.getFileOutputProperty( OUT_FILE_KEY );
	assertNotNull( "null file output stream", fout );
	fout.write( data.getBytes() );
	fout.close();
	BufferedReader bin = new BufferedReader( new FileReader( OUT_FILE_VAL ) );
	String fileData = bin.readLine();
	assertEquals( "wrong data written to file", data, fileData + "\n" );
	bin.close();
	OUT_FILE_VAL.delete();

	// resource
	InputStream in = ClassLoader.getSystemResourceAsStream( RSRC_VAL );
	in.read( expectedBytes );
	in.close();
	in = uut.getResourceProperty( RSRC_KEY );
	in.read( actualBytes );
	in.close();
	assertEquals( "wrong resource value", expectedBytes, actualBytes );

	// class
	assertEquals( "wrong class value", String.class, uut.getClassProperty( CLASS_KEY
			+ Properties.CLASS_SUFFIX ) );
	assertEquals( "wrong class value w/ default", String.class, uut.getClassProperty( CLASS_KEY
			+ Properties.CLASS_SUFFIX, PropertiesTest.class ) );
	try {
		uut.getDoubleProperty( CLASS_KEY + ".not" );
		fail( "missing class property should have thrown exception" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertEquals( "wrong default class value", String.class, uut.getClassProperty( CLASS_KEY
			+ ".not", String.class ) );

	// object
	Object obj = uut.newObjectProperty( CLASS_KEY );
	assertTrue( "wrong object class 1", obj instanceof String );
	obj = uut.newObjectProperty( String.class );
	assertTrue( "wrong object class 2", obj instanceof String );
	obj = uut.singletonObjectProperty( CLASS_KEY );
	assertTrue( "wrong object class 3", obj instanceof String );
	Object obj2 = uut.singletonObjectProperty( CLASS_KEY );
	assertTrue( "two singletons", obj == obj2 );
	obj = uut.singletonObjectProperty( String.class );
	assertTrue( "wrong object class 4", obj instanceof String );
	assertFalse( "class and property singletons the same", obj == obj2 );
	obj2 = uut.singletonObjectProperty( String.class );
	assertTrue( "two singletons", obj == obj2 );

	// object list
	String key = OBJECT_LIST_KEY + ".not";
	List objectList = uut.newObjectListProperty( key, null );
	try {
		uut.newObjectListProperty( key );
		fail( "should have thrown exception for missing key " + key );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
	assertNull( key + " should return empty list", objectList );
	objectList = uut.newObjectListProperty( OBJECT_LIST_KEY, null );
	assertNotNull( key + " should not return empty list", objectList );
	assertEquals( "wrong object list size", 2, objectList.size() );
	boolean objectListStringFound = false;
	boolean objectListRandFound = false;
	Iterator it = objectList.iterator();
	while ( it.hasNext() ) {
		Object o = it.next();
		if ( o instanceof String )
			objectListStringFound = true;
		else if ( o instanceof Randomizer ) {
			objectListRandFound = true;
			Randomizer r = (Randomizer) o;
			assertEquals( "wrong rand seed", RAND_SEED_VAL, r.getSeed() );
		}
		else
			fail( "unexpected class in object list " + o.getClass() );
	}
	assertTrue( "object list string not found", objectListStringFound );
	assertTrue( "object list rand not found", objectListRandFound );

	// patterns
	Set expectedSet = new HashSet();
	expectedSet.add( STRING_KEY );
	expectedSet.add( SHORT_KEY );
	Set actualSet = uut.getKeysForPattern( "^key\\.s.*" );
	assertEquals( "wrong keys for pattern", expectedSet, actualSet );
}

/**
 * @throws Exception
 */
public void testContains() throws Exception {
	uut.containsKey( STRING_KEY );
	uut.containsKey( BOOL_KEY );
	uut.containsKey( SHORT_KEY );
	uut.containsKey( INT_KEY );
	uut.containsKey( LONG_KEY );
	uut.containsKey( FLOAT_KEY );
	uut.containsKey( DOUBLE_KEY );
	uut.containsKey( DIR_KEY );
	uut.containsKey( IN_FILE_KEY );
	uut.containsKey( OUT_FILE_KEY );
	uut.containsKey( RSRC_KEY );
	uut.containsKey( CLASS_KEY );
	uut.contains( STRING_VAL );
	uut.contains( BOOL_VAL.toString() );
	uut.contains( SHORT_VAL.toString() );
	uut.contains( INT_VAL.toString() );
	uut.contains( LONG_VAL.toString() );
	uut.contains( FLOAT_VAL.toString() );
	uut.contains( DOUBLE_VAL.toString() );
	uut.contains( DIR_VAL.toString() );
	uut.contains( IN_FILE_VAL.toString() );
	uut.contains( OUT_FILE_VAL.toString() );
	uut.contains( RSRC_VAL.toString() );
	uut.contains( String.class.toString().substring( "class ".length() ) );
}

/**
 * @throws Exception
 */
public void testSubProperties() throws Exception {
	Properties subProps = uut.getSubProperties( PREFIX );
	assertEquals( "wrong string value", PREFIX + STRING_VAL, subProps.getProperty( STRING_KEY ) );
	assertEquals( "wrong short value", SHORT_VAL.shortValue(), subProps
			.getShortProperty( SHORT_KEY ) );
	try {
		subProps.getProperty( PREFIX + STRING_KEY );
		fail( "shuold have thrown exception for missing property" );
	}
	catch ( IllegalArgumentException e ) {
		// success
	}
}

private static void assertEquals( String msg, byte[] expected, byte[] actual ) {
	assertEquals( msg + ": different lengths", expected.length, actual.length );
	for ( int i = 0; i < expected.length; ++i ) {
		assertEquals( msg + " wrong byte " + i, expected[ i ], actual[ i ] );
	}
}

}
