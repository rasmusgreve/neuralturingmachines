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
 * Created on Jun 5, 2005 by Philip Tucker
 */
package com.anji_ahni.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.jgapcustomised.Chromosome;
import org.jgapcustomised.Configuration;
import org.jgapcustomised.Genotype;

import com.anji_ahni.integration.Activator;
import com.anji_ahni.run.Run;
import com.anji_ahni.util.Properties;

/**
 * @author Philip Tucker
 */
public class HibernatePersistence implements Persistence {

	private final static Logger logger = Logger.getLogger(HibernatePersistence.class);

	private Connection conn = null;

	private String runName;

	/**
	 * default ctor; must call <code>init()</code> before using this object
	 */
	public HibernatePersistence() {
		super();
	}

	/**
	 * @see com.anji_ahni.util.Configurable#init(com.anji_ahni.util.Properties)
	 */
	public void init(Properties props) {
		try {
		} catch (Exception e) {
			String msg = "error initializing hibernate";
			logger.error(msg, e);
			throw new IllegalArgumentException(msg + ": " + e);
		}
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#reset()
	 */
	public void reset() {
		try {
		} catch (Throwable th) {
			String msg = "error resetting run " + runName;
			logger.error(msg, th);
			try {
				conn.rollback();
			} catch (SQLException e) {
				logger.error("error on rollback", e);
			}
			throw new IllegalStateException(msg + ": " + th);
		}
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#store(org.jgapcustomised.Chromosome)
	 */
	public void store(Chromosome c) throws Exception {
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#store(com.anji_ahni.integration.Activator)
	 */
	public void store(Activator a) throws Exception {
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#store(com.anji_ahni.run.Run)
	 */
	public void store(Run r) throws Exception {
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#loadChromosome(java.lang.String, org.jgapcustomised.Configuration)
	 */
	public Chromosome loadChromosome(String id, Configuration config) {
		return null;
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#deleteChromosome(java.lang.String)
	 */
	public void deleteChromosome(String id) throws Exception {
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#loadGenotype(org.jgapcustomised.Configuration)
	 */
	public Genotype loadGenotype(Configuration aConfig) {
		return null;
	}

	/**
	 * @see com.anji_ahni.persistence.Persistence#startRun(java.lang.String)
	 */
	public void startRun(String aRunId) {
	}

}
