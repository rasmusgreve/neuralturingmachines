/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * Created on May 14, 2005 by Philip Tucker
 */
package com.anji.neat.test;

import org.jgap.InvalidConfigurationException;
import org.jgap.test.DummyFitnessFunction;

import com.anji.neat.NeatConfiguration;
import com.anji.util.Properties;

/**
 * @author Philip Tucker
 */
public class TestNeatConfigurationFactory {

private static TestNeatConfigurationFactory instance = null;

private TestNeatConfigurationFactory() {
	// no-op
}

/**
 * @return singleton
 */
public static TestNeatConfigurationFactory getInstance() {
	if ( instance == null )
		instance = new TestNeatConfigurationFactory();
	return instance;
}

/**
 * build dummy configuraiton function
 * @param props
 * @return new <code>NeatConfiguration</code> object
 * @throws InvalidConfigurationException
 */
public NeatConfiguration buildConfig( Properties props ) throws InvalidConfigurationException {
	NeatConfiguration config = new NeatConfiguration( props );
	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	config.setPopulationSize( 100 );
	config.lockSettings();
	config.load();

	return config;
}

}
