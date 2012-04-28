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
		if (log.isInfoEnabled()) {
			log.info("Commited " + jdbcEvent.getSQL());
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
	}

	@Override
	public void afterExecuteStatement(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Executing: " + jdbcEvent.getSQL());
		}
	}

	@Override
	public void afterPrepareStatement(final JDBCEvent jdbcEvent) {
	}

	@Override
	public void afterRollback(final JDBCEvent jdbcEvent) {
		if (log.isDebugEnabled()) {
			log.debug("Rolling back: " + jdbcEvent.getSQL());
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
	}

	@Override
	public void beforeCreateStatement(final JDBCEvent jdbcEvent) {
	}

	@Override
	public void beforeExecuteStatement(final JDBCEvent jdbcEvent) {
	}

	@Override
	public void beforePrepareStatement(final JDBCEvent jdbcEvent) {
	}

	@Override
	public void beforeRollback(final JDBCEvent jdbcEvent) {
	}
}
