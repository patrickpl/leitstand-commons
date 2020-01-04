/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

import static io.leitstand.commons.ReasonTest.UnitTestReason.INVALID;
import static io.leitstand.commons.ReasonTest.UnitTestReason.UTS0001E_TEST_ERROR_REASON;
import static io.leitstand.commons.ReasonTest.UnitTestReason.UTS0002W_TEST_WARNING_REASON;
import static io.leitstand.commons.ReasonTest.UnitTestReason.UTS0003I_TEST_INFO_REASON;
import static io.leitstand.commons.ReasonTest.UnitTestReason.UTS0003X_TEST_UNKNOWN_SEVERITY;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.leitstand.commons.messages.Message.Severity;

public class ReasonTest {
	
	enum UnitTestReason implements Reason{
		
		UTS0001E_TEST_ERROR_REASON,
		UTS0002W_TEST_WARNING_REASON,
		UTS0003I_TEST_INFO_REASON,
		UTS0003X_TEST_UNKNOWN_SEVERITY,
		INVALID;
		
		@Override
		public String getReasonCode() {
			String name = name();
			if(name.length() < 8) {
				return name;
			}
			return name().substring(0,8);
		}

		@Override
		public String getMessage(Object... keys) {
			return name();
		}
		
	}
	

	@Test
	public void map_info_reason_code_to_info_severity() {
		assertEquals(Severity.INFO,UTS0003I_TEST_INFO_REASON.getSeverity());
	}
	
	@Test
	public void map_error_reason_code_to_error_severity() {
		assertEquals(Severity.ERROR,UTS0001E_TEST_ERROR_REASON.getSeverity());		
	}
	
	
	@Test
	public void map_unknown_reason_code_severity_to_error_severity() {
		assertEquals(Severity.ERROR,UTS0003X_TEST_UNKNOWN_SEVERITY.getSeverity());		
	}
	
	
	@Test
	public void map_warning_reason_code_to_warning_severity() {
		assertEquals(Severity.WARNING,UTS0002W_TEST_WARNING_REASON.getSeverity());
	}
	
	@Test(expected=AssertionError.class)
	public void map_unknown_reason_code_to_error_severity() {
		assertEquals(Severity.ERROR,INVALID.getSeverity());

	}
	
	@Test
	public void use_first_three_letters_as_module_identifier() {
		assertEquals("UTS",UTS0003I_TEST_INFO_REASON.getModule());
	}

	@Test(expected=AssertionError.class)
	public void fire_assertion_error_if_reason_code_does_not_consist_of_8_characters() {
		INVALID.getModule();
	}
	
}
