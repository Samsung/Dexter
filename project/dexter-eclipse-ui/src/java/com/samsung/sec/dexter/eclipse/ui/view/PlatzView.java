package com.samsung.sec.dexter.eclipse.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class PlatzView extends ViewPart {
	public final static String ID = "dexter-eclipse.platz";
	private Browser browser = null;

	public PlatzView() {

	}

	@Override
	public void createPartControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		browser = new Browser(composite, SWT.NONE);

	}

	@Override
	public void setFocus() {
	}

	public void setUrl(String url) {
		browser.setUrl(url);
	}
}
