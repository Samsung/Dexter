/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.samsung.sec.dexter.daemon;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	
	IWorkbenchAction helpAction;
	IWorkbenchAction aboutAction;
	private IWorkbenchAction exitAction;
	
	/*
	IWorkbenchAction preferencesAction;
	*/
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	
    	exitAction = ActionFactory.QUIT.create(window);
    	register(exitAction);
    	
    	/*
    	helpAction = ActionFactory.HELP_CONTENTS.create(window);
    	register(helpAction);
    	aboutAction = ActionFactory.ABOUT.create(window);
    	register(aboutAction);
    	*/
    	/*
    	preferencesAction = ActionFactory.PREFERENCES.create(window);
    	*/
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	
    	MenuManager exitMenu = new MenuManager ("&Exit", "Exit Action");
    	exitMenu.add(exitAction);
    	
    	menuBar.add(exitMenu);
    	
    	/*
    	MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    	helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        helpMenu.add(new Separator()); 
    	helpMenu.add(helpAction);
    	helpMenu.add(aboutAction);
    	
    	menuBar.add(helpMenu);
    	*/
    }
}
