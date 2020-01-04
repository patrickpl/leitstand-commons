/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static io.leitstand.commons.model.Patterns.DNS_PATTERN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;



public class DnsPatternTest {

	@Test
	public void acceptNameWithoutDots(){
		assertTrue("leaf1".matches(DNS_PATTERN));
	}
	
	@Test
	public void acceptNameWithHyphen(){
		assertTrue("test-name".matches(DNS_PATTERN));
	}
	
	@Test
	public void acceptNameWithDots(){
		assertTrue("test-name.rtbrick.com".matches(DNS_PATTERN));
	}

	@Test
	public void rejectNameStartingWithHyphen(){
		assertFalse("-name.rtbrick.com".matches(DNS_PATTERN));
	}
	

	@Test
	public void rejectNameWithDotAfterHyphen(){
		assertFalse("name-.rtbrick.com".matches(DNS_PATTERN));
	}
	
	@Test
	public void rejectNameWithHyphenAfterDot(){
		assertFalse("name.-rtbrick.com".matches(DNS_PATTERN));
	}

	@Test
	public void acceptIpAddress(){
		assertTrue("192.168.1.2".matches(DNS_PATTERN));
	}
	
	@Test
	public void rejectNameWithTwoSubsequentialDots(){
		assertFalse("test-name..rtbrick.com".matches(DNS_PATTERN));
	}
	
}
