package com.samsung.sec.dexter.eclipse.ui;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class UIActivatorTest {
	private static SWTWorkbenchBot bot = new SWTWorkbenchBot();
	
	@Before
	public void setup(){
		bot.sleep(3000);
		bot.button("OK").click();
		bot.button("Cancel").click();
		bot.button("OK").click();
		bot.viewByTitle("Welcome").show();
		bot.viewByTitle("Welcome").close();
		bot.viewByTitle("Project Explorer").show();
	}
	
	@Test
	public void login_failure(){
		bot.toolbarButtonWithTooltip("Login to Dexter Server (Coding-time Static Analysis Server) (Ctrl+Alt+D)").click();
		bot.textWithLabel("Single ID:").setText("wrong-id");
		bot.textWithLabel("Password:").setText("wrongpassowrd");
		bot.textWithLabel("Dexter Server(IP:Port):").setText("wrongip:123");
		bot.button("접속 테스트").click();
		bot.button("OK").click();
		bot.button("Cancel").click();
	}
}
