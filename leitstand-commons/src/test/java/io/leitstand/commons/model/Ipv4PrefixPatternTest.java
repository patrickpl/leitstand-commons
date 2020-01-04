/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static io.leitstand.commons.model.Patterns.IPV4_PREFIX;
import static io.leitstand.commons.model.Patterns.IP_PREFIX;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Ipv4PrefixPatternTest {

	@Parameters
	public static Collection<Object[]> getAddresses(){
		return asList(new Object[][] {
			   {"192.168.10.1",true},
			   {"192.168.10.1/8",true},
			   {"192.168.10.1/16",true},
			   {"192.168.10.1/32",true},
			   {"192.168.10.1/33",false},
			   {"192.168.10.1/40",false}});
	}
	
	
	
	private String cidr;
	private boolean match;
	
	public Ipv4PrefixPatternTest(String cidr, boolean match) {
		this.cidr = cidr;
		this.match = match;
	}
	
	@Test
	public void addressMatch() {
		assertThat(format("%s mismatch", cidr),cidr.matches(IP_PREFIX),is(match));
		assertThat(format("%s mismatch", cidr),cidr.matches(IPV4_PREFIX),is(match));
	}
	
}
