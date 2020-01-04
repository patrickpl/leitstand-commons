/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.log;

import static io.leitstand.commons.log.DiagnosticContext.clear;
import static io.leitstand.commons.log.DiagnosticContext.push;
import static io.leitstand.commons.model.StringUtil.isEmptyString;
import static java.lang.Thread.currentThread;
import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Before;
import org.junit.Test;

import io.leitstand.commons.jsonb.IsoDateAdapter;

public class PatternFormatterTest {

	
	private LogRecord record;
	
	@Before
	public void createRecord() {
		record = new LogRecord(Level.WARNING, "Unit test");
		record.setLoggerName("UnittestLogger");
		record.setThreadID((int)currentThread().getId());
		record.setThrown(new RuntimeException("unit test exception"));
		record.setMillis(IsoDateAdapter.parseIsoDate("2019-05-22T00:16:46.460Z").getTime());
	}
	
	
	@Test
	public void format_thread() {
		PatternFormatter formatter = new PatternFormatter("%t");
		assertEquals(Thread.currentThread().getName(),formatter.format(record));
	}
	
	@Test
	public void format_level() {
		PatternFormatter formatter = new PatternFormatter("%p");
		record.setLevel(SEVERE);
		assertEquals("SEVERE",formatter.format(record));
		record.setLevel(CONFIG);
		assertEquals("CONFIG",formatter.format(record));
		record.setLevel(WARNING);
		assertEquals("WARNING",formatter.format(record));
		record.setLevel(INFO);
		assertEquals("INFO",formatter.format(record));
		record.setLevel(FINE);
		assertEquals("FINE",formatter.format(record));
		record.setLevel(FINER);
		assertEquals("FINER",formatter.format(record));
		record.setLevel(FINEST);
		assertEquals("FINEST",formatter.format(record));
		
	}
	
	@Test
	public void format_datetime() {
		PatternFormatter formatter = new PatternFormatter("%d{yyyy-MM-dd HH:mm:ss.SSS}") ;
		assertEquals("2019-05-22 02:16:46.460",formatter.format(record));
	}
	
	
	@Test
	public void default_format_datetime() {
		PatternFormatter formatter = new PatternFormatter("%d") ;
		assertEquals(new Date(record.getMillis()).toString(),formatter.format(record));
	}
	
	@Test
	public void format_message() {
		PatternFormatter formatter = new PatternFormatter("%s");
		assertEquals(record.getMessage(),formatter.format(record));
	}
	
	@Test
	public void format_thrown() {
		
	}

	@Test
	public void do_nothing_if_record_contains_no_thrown_info() {
		record.setThrown(null);
		PatternFormatter formatter = new PatternFormatter("%e");
		assertTrue(isEmptyString(formatter.format(record)));
	}

	
	@Test
	public void format_newline() {
		PatternFormatter formatter = new PatternFormatter("%n");
		assertEquals("\n",formatter.format(record));
	}
	
	@Test
	public void format_limit_prefix_packages() {
		PatternFormatter formatter = new PatternFormatter("%.3c");
		record.setLoggerName("net.rtbrick.rbms.unittest.Logger");
		assertEquals("unittest.Logger",formatter.format(record));		
	}

	@Test
	public void format_logger_as_simple_class_name() {
		PatternFormatter formatter = new PatternFormatter("%c.1");
		record.setLoggerName("net.rtbrick.rbms.unittest.Logger");
		assertEquals("Logger",formatter.format(record));		
	}

	@Test
	public void format_simple_logger_suffix() {
		PatternFormatter formatter = new PatternFormatter("%c.2");
		record.setLoggerName("net.rtbrick.rbms.unittest.Logger");
		assertEquals("unittest.Logger",formatter.format(record));		
	}
	
	@Test
	public void format_logger() {
		PatternFormatter formatter = new PatternFormatter("%c");
		assertEquals(record.getLoggerName(),formatter.format(record));		
	}
	
	@Test
	public void format_diagnostic_context() {
		push("contextual info");
		push("foo=bar");
		PatternFormatter formatter = new PatternFormatter("[%x]");
		assertEquals("[contextual info foo=bar]",formatter.format(record));		
		clear();
	}
	
	@Test
	public void can_escape_parameter_marker() {
		PatternFormatter formatter = new PatternFormatter("%%");
		assertEquals("%",formatter.format(record));
	}
	
	@Test
	public void format_log_record() {
		record.setThrown(null);
		PatternFormatter formatter = new PatternFormatter("%d{yyyy-MM-dd HH:mm:ss.SSS} (%c) [%t] %p %s%1x%1e%n");
		assertEquals("2019-05-22 02:16:46.460 (UnittestLogger) ["+currentThread().getName()+"] WARNING Unit test\n",formatter.format(record));
	}
	
	
	@Test
	public void format_log_record_with_throwable() throws IOException{
		PatternFormatter formatter = new PatternFormatter("%d{yyyy-MM-dd HH:mm:ss.SSS} (%c) [%t] %p %s%1x%1e%n");
		BufferedReader reader = new BufferedReader(new StringReader( formatter.format(record)));
		assertEquals("2019-05-22 02:16:46.460 (UnittestLogger) ["+currentThread().getName()+"] WARNING Unit test java.lang.RuntimeException: unit test exception",reader.readLine());
		
	}
}
