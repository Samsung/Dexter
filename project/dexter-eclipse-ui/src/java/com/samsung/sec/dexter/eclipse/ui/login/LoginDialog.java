/**
 * 
 */
package com.samsung.sec.dexter.eclipse.ui.login;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.job.DexterJobFacade;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
/**
 * 
 * Copyright 2014 by Samsung Electronics, Inc.,
 * 
 * This software is the confidential and proprietary information of Samsung
 * Electronics, Inc. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Samsung.
 */
public class LoginDialog extends TitleAreaDialog {
	private IDexterClient client = DexterClient.getInstance();
	private DexterConfig config = DexterConfig.getInstance();
	
	private Text idText;
	private Text pwdText;
	private Text serverText;
	private Text dexterHomeText;
	private Button standaloneModeButton;

	/**
	 * @param parentShell
	 */
	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}

	public static void loginJob(final Shell shell) {
		final Shell localShell;
		
		if(shell == null || shell.isDisposed()){
			if(Display.getDefault() != null
					&& Display.getDefault().getActiveShell() != null){
				localShell = Display.getDefault().getActiveShell(); 
			} else {
				return;
			}
		} else {
			localShell = shell;
		}
		
		localShell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if(Strings.isNullOrEmpty(DexterConfig.getInstance().getDexterHome()) == false
						&& Strings.isNullOrEmpty(DexterClient.getInstance().getCurrentUserId()) == false){
					return;
				}
				
				final LoginDialog dialog = new LoginDialog(localShell);
				final int ret = dialog.open();
				
				if (ret == InputDialog.CANCEL) {
					MessageDialog
					.openError(localShell, "Dexter Login Error", //$NON-NLS-1$
							Messages.LoginDialog_LOGIN_GUIDE_MSG);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();

		setTitle(Messages.LoginDialog_DEXTER_LOGIN_DIALOG_TITLE);
		if (client.isLogin()) {
			setMessage(Messages.LoginDialog_LOGIN_STATUS_MSG + client.getCurrentUserId());
		} else {
			setMessage(Messages.LoginDialog_DEXTER_LOGIN_DIALOG_DESC);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		final Composite container = new Composite(area, SWT.NONE);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		addFieldForId(container);
		addFieldForPwd(container);
		addFieldForServer(container);
		addFieldForDexterHome(container);
		addFieldForStandaloneMode(container);
		updateWidgetsForStandaloneMode();

		return area;
	}

	private void addFieldForDexterHome(final Composite container) {
		final Label dexterHomeLabel = new Label(container, SWT.NONE);
		dexterHomeLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		dexterHomeLabel.setText("Dexter Home Path:"); //$NON-NLS-1$

		dexterHomeText = new Text(container, SWT.BORDER);
		dexterHomeText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		if (Strings.isNullOrEmpty(config.getDexterHome()) == false) {
			dexterHomeText.setText(config.getDexterHome());
		} else {
			dexterHomeText.setText(EclipseUtil.getDefaultDexterHomePath());
		}
		
		dexterHomeText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				dexterHomeText.selectAll();
				setMessage(Messages.LoginDialog_DEXTER_HOME_DESC);
			}
		});

		final Button findButton = new Button(container, SWT.PUSH);
		findButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		findButton.setText(Messages.LoginDialog_FIND);

		findButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				widgetDefaultSelected(arg0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(container.getShell());
				dialog.setText(Messages.LoginDialog_DEXTER_HOME_DIALOG_TITLE);
				dialog.setMessage(Messages.LoginDialog_DEXTER_HOME_DIALOG_DESC);
				dialog.setFilterPath(dexterHomeText.getText());

				String dir = dialog.open();

				if (dir != null) {
					dexterHomeText.setText(dir);
				}
			}
		});

	}

	private void addFieldForServer(final Composite container) {
		final Label serverLabel = new Label(container, SWT.NONE);
		serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		serverLabel.setText("Dexter Server(IP:Port):"); //$NON-NLS-1$

		serverText = new Text(container, SWT.BORDER);
		serverText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		try{
			serverText.setText(client.getServerHost() + ":" //$NON-NLS-1$
					+ client.getServerPort());
		} catch (DexterRuntimeException e){
			serverText.setText("DexterServer_IP:DexterServer_Port"); //$NON-NLS-1$
		}
		
		serverText.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				//validate();
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				serverText.selectAll();
				setMessage(Messages.LoginDialog_DEXTER_SERVER_DESC);
			}
		});

		final Button testButton = new Button(container, SWT.PUSH);
		testButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		testButton.setText(Messages.LoginDialog_NETWORK_TEST);

		testButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				widgetDefaultSelected(arg0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				setMessage(Messages.LoginDialog_NETWORK_TEST_MSG, IMessageProvider.INFORMATION);
				if (client.isServerAddressOk(serverText.getText().trim()) == false) {
					setMessage(Messages.LoginDialog_NETWORK_ERROR_MSG, IMessageProvider.ERROR);
				} else {
					setMessage(Messages.LoginDialog_NETWORK_OK_MSG, IMessageProvider.INFORMATION);
				}
			}
		});
	}

	private void addFieldForPwd(final Composite container) {
		final Label pwdLabel = new Label(container, SWT.NONE);
		pwdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		pwdLabel.setText("Password:"); //$NON-NLS-1$

		pwdText = new Text(container, SWT.BORDER | SWT.PASSWORD);
		pwdText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		if (Strings.isNullOrEmpty(client.getCurrentUserPwd()) == false) {
			pwdText.setText(client.getCurrentUserPwd());
		}

		pwdText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				//validate();
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				pwdText.selectAll();
				setMessage(Messages.LoginDialog_PASSWORD_DESC);
			}
		});
	}

	private void addFieldForId(final Composite container) {
		final Label idLabel = new Label(container, SWT.NONE);
		idLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		idLabel.setText("Single ID:"); //$NON-NLS-1$

		idText = new Text(container, SWT.BORDER);
		idText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		if (Strings.isNullOrEmpty(client.getCurrentUserId()) == false) {
			idText.setText(client.getCurrentUserId());
		}

		idText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				//validate();
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				idText.selectAll();
				setMessage(Messages.LoginDialog_PASSWORD_DESC);
			}
		});
	}
	
	private void addFieldForStandaloneMode(final Composite container) {
		final Label separator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		
		standaloneModeButton = new Button(container, SWT.CHECK);
		standaloneModeButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 3, 1));
		standaloneModeButton.setText("Run in Standalone mode");
		standaloneModeButton.setSelection(config.isStandalone());

		standaloneModeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updateWidgetsForStandaloneMode();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				updateWidgetsForStandaloneMode();
			}
		});
	}
	
	private void updateWidgetsForStandaloneMode() {
		setErrorMessage(null);
		
		if (standaloneModeButton.getSelection()) {
			idText.setEditable(false);
			pwdText.setEditable(false);
			serverText.setEditable(false);
		} else {
			idText.setEditable(true);
			pwdText.setEditable(true);
			serverText.setEditable(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(Messages.LoginDialog_SHELL_TITLE);
	}

	private boolean validate() {
		setErrorMessage(null);
		
		boolean isStandaloneMode = standaloneModeButton.getSelection();
		if (isStandaloneMode == false) {
			final String id = idText.getText();
			if (Strings.isNullOrEmpty(id)) {
				setErrorMessage(Messages.LoginDialog_ID_VAL_MSG);
				return false;
			}
	
			final String pwd = pwdText.getText();
			if (Strings.isNullOrEmpty(pwd)) {
				setErrorMessage(Messages.LoginDialog_PASSWORD_VAL_MSG);
				return false;
			}
			if (pwd.length() < 4 || pwd.length() > 20) {
				setErrorMessage(Messages.LoginDialog_PASSWORD_LENGTH_VAL_MSG);
				return false;
			}
	
			final Pattern p = Pattern.compile("[\\S]{4,20}"); //$NON-NLS-1$
			final Matcher m = p.matcher(pwd);
			if (m.matches() == false) {
				setErrorMessage(Messages.LoginDialog_PASSWORD_ERROR_MSG);
				return false;
			}
	
			final String server = serverText.getText();
			if (Strings.isNullOrEmpty(server)) {
				setErrorMessage(Messages.LoginDialog_SERVER_ERROR_MSG);
				return false;
			}
		}

		// dexter home path
		final String homePathStr = dexterHomeText.getText();
		if (Strings.isNullOrEmpty(homePathStr)) {
			setErrorMessage(Messages.LoginDialog_DEXTER_HOME_ERROR_MSG);
			return false;
		}
		
		if (homePathStr.indexOf(' ') >= 0){
			setErrorMessage(Messages.LoginDialog_DEXTER_HOME_SPACE_ERROR_MSG);
			return false;
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		setMessage(Messages.LoginDialog_HANDLE_LOGIN_MSG);
		if (validate() == false) {
			return;
		}
		
		boolean isStandalone = standaloneModeButton.getSelection();
		config.setStandalone(isStandalone);
		
		if (isStandalone == false) {
			String oldServerHost;
			
			int oldServerPort = client.getServerPort();
			String oldUserId = client.getCurrentUserId();
			
			try {
				oldServerHost = client.getServerHost();
			} catch (DexterRuntimeException e) {
				oldServerHost = "";
			}
	
			setMessage(Messages.LoginDialog_CHECK_SERVER_MSG);
			// server connection
			setMessage(Messages.LoginDialog_NETWORK_TESTING_MSG, IMessageProvider.INFORMATION);
			if (client.isServerAddressOk(serverText.getText().trim()) == false) {
				setMessage(Messages.LoginDialog_NETWORK_ERROR_MSG, IMessageProvider.ERROR);
				return;
			}
			client.setDexterServer(serverText.getText().trim());
			setMessage(Messages.LoginDialog_NETWORK_OK_MSG, IMessageProvider.INFORMATION);
	
			// login
			final String id = idText.getText();
			final String pwd = pwdText.getText();
			
			if(client.hasAccount(id) == false){
				createAccount(id, pwd);
			}
			
			try {
				client.login(id, pwd);
				DexterJobFacade.getInstance().startDexterServerJobs();
	        } catch (DexterRuntimeException e) {
	        	setMessage(Messages.LoginDialog_LOGIN_ERROR_MSG, IMessageProvider.ERROR);
	        	return;
	        }
			client.runLoginInfoHandler(oldServerHost, oldServerPort, oldUserId);
		}

		final File homePath = new File(dexterHomeText.getText());
		if (homePath.exists() == false) {
			homePath.mkdir();
		}

		// plugin store
		setMessage(Messages.LoginDialog_INIT_ENV_MSG);
		final IPreferenceStore store = DexterUIActivator.getDefault().getPreferenceStore();
		store.setValue("userId", idText.getText()); //$NON-NLS-1$
		store.setValue("userPwd", pwdText.getText()); //$NON-NLS-1$
		store.setValue("serverAddress", serverText.getText()); //$NON-NLS-1$
		store.setValue("isStandalone", isStandalone);
		store.setValue(DexterConfig.DEXTER_HOME_KEY, dexterHomeText.getText());
		
		System.setProperty(DexterConfig.DEXTER_HOME_KEY, dexterHomeText.getText());
		config.setDexterHome(dexterHomeText.getText());
		
		// client store
		client.setCurrentUserId(idText.getText());
		client.setCurrentUserPwd(pwdText.getText());
		client.setDexterServer(serverText.getText());

		super.okPressed();
	}

	private void createAccount(final String id, final String pwd) {
    	MessageBox dialog = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
    	dialog.setText(Messages.LoginDialog_CREATE_ACCOUNT_DIALOG_TITLE);
    	dialog.setMessage(Messages.LoginDialog_NO_ACCOUNT_ERROR_MSG + id + "'. " //$NON-NLS-2$ //$NON-NLS-1$
    	        + Messages.LoginDialog_ACCOUNT_ASK_PREFIX_MSG + id + Messages.LoginDialog_ACCOUNT_ASK_POSTFIX_MSG);

    	if (dialog.open() == SWT.OK) {
    		if(handleCreateAccount(id, pwd)){
    			setMessage(Messages.LoginDialog_ACCOUNT_CREATED_MSG, IMessageProvider.INFORMATION);
    		} else {
    			setMessage(Messages.LoginDialog_FAIL_TO_CREATE_ACCOUNT_MSG, IMessageProvider.ERROR);
    		}
    	}
    }

	private boolean handleCreateAccount(final String id, final String pwd) {
		try {
	        client.createAccount(id, pwd, false);
	        return true;
        } catch (DexterRuntimeException e) {
        	DexterUIActivator.LOG.error(e.getMessage(), e);
        	setMessage(Messages.LoginDialog_ACCOUNT_ERROR_MSG, IMessageProvider.ERROR);
        	return false;
        }
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}
}
