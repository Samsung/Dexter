package com.samsung.sec.dexter.eclipse.ui;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class RunOnStandalongModeAtFirstTest {
	private static SWTWorkbenchBot bot = new SWTWorkbenchBot();
	
	@Before
	public void setup(){
		bot.sleep(3000);
	}
	
	@Test
	public void testRunDexterInStandalongMode(){
		bot.checkBox("Run in Standalone mode").click();
		bot.button("OK").click();
		bot.viewByTitle("Welcome").close();
		bot.viewByTitle("Project Explorer").show();
		bot.toolbarButtonWithTooltip("Login to Dexter Server (Coding-time Static Analysis Server) (Ctrl+Alt+D)").click();
		bot.button("OK").click();	
	}
}
