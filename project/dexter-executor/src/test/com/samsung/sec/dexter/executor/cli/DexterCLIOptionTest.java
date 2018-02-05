package com.samsung.sec.dexter.executor.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.samsung.sec.dexter.core.exception.*;

import org.junit.Before;
import org.junit.Test;

public class DexterCLIOptionTest {
	IDexterCLIOption cliOption;
	HelpFormatter helpFormatter;
	
	@Before
	public void setUp() throws Exception {
		helpFormatter = mock(HelpFormatter.class);
	}

	@Test
	public void DexterCliOption_doNotSetConfigFilePathWithInvalidOption() {
		// given
		String[] invalidOptions = { "invalid" };
		
		// when
		cliOption = new DexterCLIOption(invalidOptions, helpFormatter);
		
		// then
		assertNull(cliOption.getConfigFilePath());
	}
	
	@Test
	public void DexterCliOption_printHelpForInvalidArgumentRuntimeException() {
		// given
		String[] invalidOptions = { "invalid" };
		
		
		try {
			// when
			cliOption = new DexterCLIOption(invalidOptions, helpFormatter);

		} catch (InvalidArgumentRuntimeException e) {
			// then
			verify(helpFormatter).printHelp(anyString(), any(Options.class));
		}
	}
}
