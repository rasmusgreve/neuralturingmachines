/*
 * Copyright (C) 2004  Derek James and Philip Tucker
 *
 * This file is part of ANJI (Another NEAT Java Implementation).
 *
 * ANJI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * created by Philip Tucker on Dec 11, 2004
 */
package com.anji.fingerprint.test;

import junit.framework.TestCase;

import com.anji.fingerprint.Classification;
import com.anji.fingerprint.ClassifierResult;

/**
 * ClassifierResultTest
 */
public class ClassifierResultTest extends TestCase {

	/**
	 * ctor
	 */
	public ClassifierResultTest() {
		this( ClassifierResultTest.class.toString() );
	}

	/**
	 * ctor
	 * 
	 * @param arg0
	 */
	public ClassifierResultTest( String arg0 ) {
		super( arg0 );
	}

	private static void assertConfidences( ClassifierResult uut, float arch, float whorl,
			float left, float right, float none ) {
		assertEquals( "wrong arch confidence", arch, uut.getConfidence( Classification.ARCH ), 0f );
		assertEquals( "wrong whorl confidence", whorl, uut.getConfidence( Classification.WHORL ),
				0f );
		assertEquals( "wrong left confidence", left, uut.getConfidence( Classification.LEFT_LOOP ),
				0f );
		assertEquals( "wrong right confidence", right, uut
				.getConfidence( Classification.RIGHT_LOOP ), 0f );
		assertEquals( "wrong none confidence", none, uut.getConfidence( Classification.NONE ), 0f );
	}

	/**
	 * tets ClassifierResult
	 * 
	 * @throws Exception
	 */
	public void testClassifierResult() throws Exception {
		// initial settings
		ClassifierResult uut = new ClassifierResult();
		assertEquals( "wrong initial primary classification", Classification.NONE, uut
				.getPrimaryClassification() );
		assertEquals( "wrong initial secondary classification", Classification.NONE, uut
				.getSecondaryClassification() );
		assertConfidences( uut, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f );

		// set one confidence
		uut.setConfidence( Classification.ARCH, 0.75f );
		assertEquals( "wrong  primary classification", Classification.ARCH, uut
				.getPrimaryClassification() );
		assertEquals( "wrong  secondary classification", Classification.NONE, uut
				.getSecondaryClassification() );
		assertConfidences( uut, 0.75f, 0.5f, 0.5f, 0.5f, 0.5f );

		// change 1st confidence
		try {
			uut.setConfidence( Classification.ARCH, 0.55f );
			fail( "should have been illegal argument, setting same class twice" );
		}
		catch ( IllegalArgumentException e ) {
			// success
		}
		assertEquals( "wrong  primary classification", Classification.ARCH, uut
				.getPrimaryClassification() );
		assertEquals( "wrong  secondary classification", Classification.NONE, uut
				.getSecondaryClassification() );
		assertConfidences( uut, 0.75f, 0.5f, 0.5f, 0.5f, 0.5f );

		// set 2nd confidence
		uut.setConfidence( Classification.WHORL, 0.65f );
		assertEquals( "wrong  primary classification", Classification.ARCH, uut
				.getPrimaryClassification() );
		assertEquals( "wrong  secondary classification", Classification.WHORL, uut
				.getSecondaryClassification() );
		assertConfidences( uut, 0.75f, 0.65f, 0.5f, 0.5f, 0.5f );

		// change 2nd confidence
		try {
			uut.setConfidence( Classification.WHORL, 0.55f );
			fail( "should have been illegal argument, setting same class twice" );
		}
		catch ( IllegalArgumentException e ) {
			// success
		}
		assertEquals( "wrong  primary classification", Classification.ARCH, uut
				.getPrimaryClassification() );
		assertEquals( "wrong  secondary classification", Classification.WHORL, uut
				.getSecondaryClassification() );
		assertConfidences( uut, 0.75f, 0.65f, 0.5f, 0.5f, 0.5f );

		// set another 1st confidence
		uut.setConfidence( Classification.LEFT_LOOP, 0.85f );
		assertEquals( "wrong  primary classification", Classification.LEFT_LOOP, uut
				.getPrimaryClassification() );
		assertEquals( "wrong  secondary classification", Classification.ARCH, uut
				.getSecondaryClassification() );
		assertConfidences( uut, 0.75f, 0.65f, 0.85f, 0.5f, 0.5f );

		// set another 2nd confidence
		uut.setConfidence( Classification.RIGHT_LOOP, 0.80f );
		assertEquals( "wrong  primary classification", Classification.LEFT_LOOP, uut
				.getPrimaryClassification() );
		assertEquals( "wrong  secondary classification", Classification.RIGHT_LOOP, uut
				.getSecondaryClassification() );
		assertConfidences( uut, 0.75f, 0.65f, 0.85f, 0.80f, 0.5f );
	}
}
