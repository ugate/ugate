package org.ugate.service;

import org.apache.openjpa.lib.jdbc.JDBCEvent;
import org.apache.openjpa.lib.jdbc.JDBCListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global listener for JDBC transactions
 */
public class UGateJdbcListener implements JDBCListener {
	
	private static final Logger log = LoggerFactory.getLogger(UGateJdbcListener.class);

	@Override
	public void afterCommit(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Commited " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void afterConnect(final JDBCEvent jdbcEvent) {
		if (log.isInfoEnabled()) {
			log.info("Connected to " + jdbcEvent.getConnection());
		}
	}

	@Override
	public void afterCreateStatement(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Created statement: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void afterExecuteStatement(final JDBCEvent jdbcEvent) {
		if (log.isInfoEnabled()) {
			log.info("Executed statement: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void afterPrepareStatement(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Prepared statement: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void afterRollback(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Rolled back: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void beforeClose(final JDBCEvent jdbcEvent) {
		if (log.isInfoEnabled()) {
			log.info("Closing connection to " + jdbcEvent.getConnection());
		}
	}

	@Override
	public void beforeCommit(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Before commit: " + (jdbcEvent.getSQL() == null ? "initialization SQL" : jdbcEvent.getSQL()));
		}
	}

	@Override
	public void beforeCreateStatement(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Creating statement: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void beforeExecuteStatement(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Executing statement: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void beforePrepareStatement(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Preparing statement: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void beforeRollback(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Rolling back: " + jdbcEvent.getSQL());
		}
	}
}
