/**
 * 
 */
package com.samsung.sec.dexter.eclipse.ui.login;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
    private DexterConfig config = DexterConfig.getInstance();
    final private IDexterClient client = DexterUIActivator.getDefault().getDexterClient();

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

        try {
            if (client instanceof EmptyDexterClient == false)
                serverText.setText(client.getServerHost() + ":" + client.getServerPort()); //$NON-NLS-1$
        } catch (DexterRuntimeException e) {
            serverText.setText("DexterServer_IP:DexterServer_Port"); //$NON-NLS-1$
        }

        serverText.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent arg0) {
                // validate();
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

                if (client.isServerAlive(serverText.getText().trim())) {
                    setMessage(Messages.LoginDialog_NETWORK_OK_MSG, IMessageProvider.INFORMATION);
                } else {
                    setMessage(Messages.LoginDialog_NETWORK_ERROR_MSG, IMessageProvider.ERROR);
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
                // validate();
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
                // validate();
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

    /*
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        if (validate() == false)
            return;

        final boolean isStandalone = standaloneModeButton.getSelection();
        final boolean isStandaloneModeChanged = config.isStandalone() != isStandalone;
        final String id = idText.getText().trim();
        final String pwd = pwdText.getText().trim();
        final String serverAddress = serverText.getText().trim();
        final String dexterHome = dexterHomeText.getText().trim();

        config.setStandalone(isStandalone);

        if (isStandalone) {
            DexterUIActivator.getDefault().initDexter(dexterHome, isStandalone, id, pwd, serverAddress);
            DexterUIActivator.getDefault().setDexterPreferences(serverAddress, id, pwd, isStandalone, dexterHome);
        } else {
            setMessage(Messages.LoginDialog_CHECK_SERVER_MSG);
            DexterUIActivator.getDefault().initDexter(dexterHome, isStandalone, id, pwd, serverAddress);
            if (loginAfterCheck(id, pwd) == false)
                return;
            DexterUIActivator.getDefault().runLoginInfoHandler();
            DexterUIActivator.getDefault().setDexterPreferences(serverAddress, id, pwd, isStandalone, dexterHome);
        }

        runStandaloneListeners(isStandaloneModeChanged, isStandalone);
        super.okPressed();
    }

    private boolean validate() {
        setMessage(Messages.LoginDialog_HANDLE_LOGIN_MSG);
        setErrorMessage(null);

        if (standaloneModeButton.getSelection()) {
            return validateDexterHomePath();
        }

        return validateID() && validatePassword() && validateServerAddress() && validateDexterHomePath();
    }

    private boolean validateID() {
        final String id = idText.getText().trim();
        if (Strings.isNullOrEmpty(id)) {
            setErrorMessage(Messages.LoginDialog_ID_VAL_MSG);
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        final String pwd = pwdText.getText().trim();

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

        return true;
    }

    private boolean validateServerAddress() {
        final String serverAddress = serverText.getText().trim();

        if (Strings.isNullOrEmpty(serverAddress)) {
            setErrorMessage(Messages.LoginDialog_SERVER_ERROR_MSG);
            return false;
        }

        setMessage(Messages.LoginDialog_NETWORK_TESTING_MSG, IMessageProvider.INFORMATION);
        if (client.isServerAlive(serverAddress) == false) {
            setMessage(Messages.LoginDialog_NETWORK_ERROR_MSG, IMessageProvider.ERROR);
            return false;
        }

        setMessage(Messages.LoginDialog_NETWORK_OK_MSG, IMessageProvider.INFORMATION);
        return true;
    }

    private boolean validateDexterHomePath() {
        final String dexterHomePath = dexterHomeText.getText().trim();

        if (Strings.isNullOrEmpty(dexterHomePath)) {
            setErrorMessage(Messages.LoginDialog_DEXTER_HOME_ERROR_MSG);
            return false;
        }

        // TODO should be tested.
        if (dexterHomePath.indexOf(' ') >= 0) {
            setErrorMessage(Messages.LoginDialog_DEXTER_HOME_SPACE_ERROR_MSG);
            return false;
        }

        final File homePath = new File(dexterHomePath);
        // TODO should ask the user before creating the path automatically
        if (homePath.exists() == false) {
            homePath.mkdir();
        }

        return true;
    }

    private boolean loginAfterCheck(final String id, final String pwd) {
        try {
            if (client.hasAccount(id) == false) {
                createAccount(id, pwd);
            }

            client.login(id, pwd);

            return true;
        } catch (DexterRuntimeException e) {
            setMessage(Messages.LoginDialog_LOGIN_ERROR_MSG, IMessageProvider.ERROR);
            return false;
        }
    }

    private void createAccount(final String id, final String pwd) {
        MessageBox dialog = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        dialog.setText(Messages.LoginDialog_CREATE_ACCOUNT_DIALOG_TITLE);
        dialog.setMessage(Messages.LoginDialog_NO_ACCOUNT_ERROR_MSG + id + "'. " // $NON-NLS-2$ //$NON-NLS-1$
                + Messages.LoginDialog_ACCOUNT_ASK_PREFIX_MSG + id + Messages.LoginDialog_ACCOUNT_ASK_POSTFIX_MSG);

        if (dialog.open() == SWT.OK) {
            if (handleCreateAccount(id, pwd)) {
                setMessage(Messages.LoginDialog_ACCOUNT_CREATED_MSG, IMessageProvider.INFORMATION);
            } else {
                setMessage(Messages.LoginDialog_FAIL_TO_CREATE_ACCOUNT_MSG, IMessageProvider.ERROR);
                return;
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

    private void runStandaloneListeners(final boolean isChangedStandaloneMode, final boolean isStandalone) {
        if (isChangedStandaloneMode) {
            if (isStandalone) {
                config.runListenerHandlerWhenStandalone();
            } else {
                config.runListenerHandlerWhenNotStandalone();
            }
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
