/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.eclipse.ui.analysis;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.executor.DexterAnalyzer;
import com.samsung.sec.dexter.executor.ProjectAnalysisConfiguration;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RunDexterAnalysisDialog extends TitleAreaDialog {
    /**  */
    private static final int MIN_WIDTH = 100;
    /**  */
    private static final int DIR_HEIGHT = 110;
    private Text projectNameText;
    private Text projectFullPathText;
    private List sourceDirList;
    private List headerDirList;
    //private List sourceEncodingList;
    private List cfgList;

    private Button[] typeButtons;
    private List targetDirList;
    private Composite targetDirComposite;

    private Shell shell;
    private Label targetDirLabel;

    private ProjectAnalysisConfiguration projectCfg = new ProjectAnalysisConfiguration();

    /**
     * @param parentShell
     */
    public RunDexterAnalysisDialog(Shell parentShell) {
        super(parentShell);
        this.shell = parentShell;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    @Override
    public void create() {
        super.create();

        setTitle("Run Dexter Static Analysis");
        setShellStyle(SWT.RESIZE);
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

        addProjectNameFields(container);
        addProjectFullPathFields(container);
        addTypeFields(container);
        addSourceDirFields(container);
        addHeaderDirFields(container);
        //addSourceEncodingFields(container);
        addTargetDirFields(container);
        addCfgListFields(container);

        getMemento();

        return area;
    }

    private void addProjectNameFields(final Composite container) {
        final Label idLabel = new Label(container, SWT.NONE);
        idLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        idLabel.setText("Project Name(*):"); //$NON-NLS-1$

        projectNameText = new Text(container, SWT.BORDER);
        projectNameText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

        projectNameText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent arg0) {
                validate();
            }

            @Override
            public void focusGained(FocusEvent e) {
                setMessage("Enter the project name of Source Insight. eg) photo-player-tv");
            }
        });
    }

    private void addProjectFullPathFields(final Composite container) {
        final Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label.setText("Project Home Directory(*):"); //$NON-NLS-1$

        projectFullPathText = new Text(container, SWT.BORDER);
        projectFullPathText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        projectFullPathText.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                validate();
            }

            @Override
            public void focusGained(FocusEvent e) {
                setMessage("Select the base directory of the package or project. it should be full path.");
            }
        });

        final Button findButton = new Button(container, SWT.PUSH);
        findButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        findButton.setText("Browser");

        findButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                DirectoryDialog dialog = new DirectoryDialog(container.getShell());
                dialog.setText("Find Project Home Directory");
                dialog.setMessage("Select the directory of your project");
                if (Strings.isNullOrEmpty(projectFullPathText.getText()) == false) {
                    dialog.setFilterPath(projectFullPathText.getText());
                }

                String dir = dialog.open();

                if (dir != null) {
                    validate();
                    projectFullPathText.setText(dir);
                }
            }
        });
    }

    private void addTypeFields(final Composite container) {
        final Label idLabel = new Label(container, SWT.NONE);
        idLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        idLabel.setText("Scope(*):"); //$NON-NLS-1$

        Composite radioComp = new Composite(container, SWT.LEFT);
        radioComp.setLayout(new RowLayout());
        radioComp.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));

        typeButtons = new Button[3];
        typeButtons[0] = new Button(radioComp, SWT.RADIO);
        typeButtons[0].setText("PROJECT");
        typeButtons[0].setToolTipText("Analyze the project including all source and header foldes");

        typeButtons[1] = new Button(radioComp, SWT.RADIO);
        typeButtons[1].setText("FOLDER");
        typeButtons[1].setToolTipText("Analyze only selected folders in a project");

        typeButtons[2] = new Button(radioComp, SWT.RADIO);
        typeButtons[2].setText("SNAPSHOT");
        typeButtons[2].setToolTipText(
                "Analyze the project including all source and header folders, then make a snapshot(only for admin role)");

        /*
         * typeButtons[3] = new Button(radioComp, SWT.RADIO);
         * typeButtons[3].setText("SNAPSHOT");
         */

        typeButtons[0].setSelection(true);

        for (final Button button : typeButtons) {
            button.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (targetDirComposite == null) {
                        return;
                    }

                    String name = button.getText();
                    if ("PROJECT".equals(name) || "SNAPSHOT".equals(name)) {
                        setEnableTargetDirField(false);
                    } else {
                        setEnableTargetDirField(true);
                    }
                }

            });
        }
    }

    private void setEnableTargetDirField(boolean enabled) {
        targetDirList.setEnabled(enabled);
        targetDirComposite.setEnabled(enabled);
        targetDirLabel.setEnabled(enabled);
    }

    private void addSourceDirFields(final Composite container) {
        final Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label.setText("Source Directory(*):"); //$NON-NLS-1$

        sourceDirList = new List(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.heightHint = DIR_HEIGHT;
        gridData.minimumWidth = MIN_WIDTH;
        gridData.widthHint = 600;
        sourceDirList.setLayoutData(gridData);

        final Composite buttonComposite = new Composite(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        buttonComposite.setLayout(gridLayout);
        buttonComposite.setLayoutData(new GridData());

        Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        addButton.setText("+");

        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                DirectoryDialog dialog = new DirectoryDialog(container.getShell());
                dialog.setText("Find Source Directory");
                dialog.setMessage("Select the directory of your sourcecode files");
                if (Strings.isNullOrEmpty(projectFullPathText.getText()) == false) {
                    dialog.setFilterPath(projectFullPathText.getText());
                }
                if (Strings.isNullOrEmpty(projectFullPathText.getText()) == false) {
                    dialog.setFilterPath(projectFullPathText.getText());
                }

                String dir = dialog.open();

                if (dir != null) {
                    sourceDirList.add(dir);
                }
                validate();
            }
        });

        final Button removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        removeButton.setText("-");

        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                int[] selected = sourceDirList.getSelectionIndices();
                sourceDirList.remove(selected);
                validate();
            }
        });

        final Button autoButton = new Button(buttonComposite, SWT.PUSH);
        autoButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        autoButton.setText("auto");

        autoButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                setMessage("It takes a long time, if you have many sub folders. please wait until it finished");

                if (Strings.isNullOrEmpty(projectFullPathText.getText())) {
                    setErrorMessage("You should set the 'Project Home Directory' field first");
                    return;
                }

                if (MessageDialog.openConfirm(container.getShell(), "Warning",
                        "It takes a long time, if it has many sub folders. Do you want to add source folders automatically?") == false) {
                    return;
                }

                container.getShell().getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        java.util.List<String> result = DexterUtil
                                .getSourceDirsFromProjectPath(projectFullPathText.getText());
                        if (result.size() == 0) {
                            MessageDialog.openInformation(container.getShell(), "Automatical Adding Result",
                                    "There is no source folders");
                            return;
                        }

                        for (String dir : result) {
                            sourceDirList.add(dir);
                        }

                        validate();
                    }
                });

            }
        });

        final Button resetButton = new Button(buttonComposite, SWT.PUSH);
        resetButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        resetButton.setText("reset");

        resetButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                sourceDirList.removeAll();
                validate();
            }
        });
    }

    private void addHeaderDirFields(final Composite container) {
        final String tooltip = "This field is for only C/C++";
        final Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label.setText("Header Directory:"); //$NON-NLS-1$
        label.setToolTipText(tooltip);

        headerDirList = new List(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.heightHint = DIR_HEIGHT;
        gridData.minimumWidth = MIN_WIDTH;
        gridData.widthHint = 600;
        headerDirList.setLayoutData(gridData);
        headerDirList.setToolTipText(tooltip);

        final Composite buttonComposite = new Composite(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        buttonComposite.setLayout(gridLayout);
        buttonComposite.setLayoutData(new GridData());

        Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        addButton.setText("+");

        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                DirectoryDialog dialog = new DirectoryDialog(container.getShell());
                dialog.setText("Find Header Directory");
                dialog.setMessage("Select the directory of your header files");
                if (Strings.isNullOrEmpty(projectFullPathText.getText()) == false) {
                    dialog.setFilterPath(projectFullPathText.getText());
                }
                if (Strings.isNullOrEmpty(projectFullPathText.getText()) == false) {
                    dialog.setFilterPath(projectFullPathText.getText());
                }

                String dir = dialog.open();

                if (dir != null) {
                    headerDirList.add(dir);
                }
                validate();
            }
        });

        final Button removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        removeButton.setText("-");

        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                int[] selected = headerDirList.getSelectionIndices();
                headerDirList.remove(selected);
                validate();
            }
        });

        final Button autoButton = new Button(buttonComposite, SWT.PUSH);
        autoButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        autoButton.setText("auto");

        autoButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                setMessage("It takes a long time, if you have many sub folders. please wait until it finished");

                if (Strings.isNullOrEmpty(projectFullPathText.getText())) {
                    setErrorMessage("You should set the 'Project Home Directory' field first");
                    return;
                }

                if (MessageDialog.openConfirm(container.getShell(), "Warning",
                        "It takes a long time, if it has many sub folders. Do you want to add header folders automatically?") == false) {
                    return;
                }

                container.getShell().getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        java.util.List<String> result = DexterUtil
                                .getHeaderDirsFromProjectPath(projectFullPathText.getText());
                        if (result.size() == 0) {
                            MessageDialog.openInformation(container.getShell(), "Automatical Adding Result",
                                    "There is no header folders");
                            return;
                        }

                        for (String dir : result) {
                            headerDirList.add(dir);
                        }

                        validate();
                    }
                });

            }
        });

        final Button resetButton = new Button(buttonComposite, SWT.PUSH);
        resetButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        resetButton.setText("reset");

        resetButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                headerDirList.removeAll();
                validate();
            }
        });
    }

    private void addTargetDirFields(final Composite container) {
        targetDirLabel = new Label(container, SWT.NONE);
        targetDirLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        targetDirLabel.setText("Target Directory(*):"); //$NON-NLS-1$

        targetDirList = new List(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.heightHint = DIR_HEIGHT;
        gridData.minimumWidth = MIN_WIDTH;
        gridData.widthHint = 600;
        targetDirList.setLayoutData(gridData);

        targetDirComposite = new Composite(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        targetDirComposite.setLayout(gridLayout);
        targetDirComposite.setLayoutData(new GridData());

        Button addButton = new Button(targetDirComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        addButton.setText("+");

        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                DirectoryDialog dialog = new DirectoryDialog(container.getShell());
                dialog.setText("Find Header Directory");
                dialog.setMessage("Select the directory of your header files");
                if (Strings.isNullOrEmpty(projectFullPathText.getText()) == false) {
                    dialog.setFilterPath(projectFullPathText.getText());
                }
                if (Strings.isNullOrEmpty(projectFullPathText.getText()) == false) {
                    dialog.setFilterPath(projectFullPathText.getText());
                }

                String dir = dialog.open();

                if (dir != null) {
                    targetDirList.add(dir);
                }
                validate();
            }
        });

        final Button removeButton = new Button(targetDirComposite, SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        removeButton.setText("-");

        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                int[] selected = targetDirList.getSelectionIndices();
                targetDirList.remove(selected);
                validate();
            }
        });

        final Button resetButton = new Button(targetDirComposite, SWT.PUSH);
        resetButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        resetButton.setText("reset");

        resetButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                targetDirList.removeAll();
                validate();
            }
        });
    }

    private void addCfgListFields(final Composite container) {
        final String tooltip = "Save your configuration information and reload it.";
        final Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        label.setText("Configuration List:"); //$NON-NLS-1$
        label.setToolTipText(tooltip);

        cfgList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        final GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.heightHint = 100;
        gridData.minimumWidth = MIN_WIDTH;
        gridData.widthHint = 600;
        cfgList.setLayoutData(gridData);
        cfgList.setToolTipText(tooltip);

        cfgList.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                final String key = cfgList.getItem(cfgList.getSelectionIndex());
                ProjectAnalysisConfiguration p = DexterAnalyzer.getInstance().getConfParamByKey(key);
                if (p == null) {
                    return;
                }

                projectNameText.setText(p.getProjectName());
                projectFullPathText.setText(p.getProjectFullPath());

                sourceDirList.removeAll();
                for (String dir : p.getSourceDirs()) {
                    sourceDirList.add(dir);
                }

                headerDirList.removeAll();
                for (String dir : p.getHeaderDirs()) {
                    headerDirList.add(dir);
                }

                targetDirList.removeAll();
                for (String dir : p.getTargetDirs()) {
                    targetDirList.add(dir);
                }
            }
        });

        final Composite buttonComposite = new Composite(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        buttonComposite.setLayout(gridLayout);
        buttonComposite.setLayoutData(new GridData());

        final Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        addButton.setText("+");
        addButton.setToolTipText("add this configuration in the configuration list.");

        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

                if (validate() == false) {
                    return;
                }

                final String cfgKey = projectNameText.getText() + " - " + projectFullPathText.getText();

                // 동일한 것이 있으면 덮어쓰기
                String[] items = cfgList.getItems();
                for (String item : items) {
                    if (item.equals(cfgKey)) {
                        if (MessageDialog.openConfirm(shell, "Duplicate Configuration",
                                "There is already same configuration. Do you want to update it?")) {
                            createParameter();
                            DexterAnalyzer.getInstance().setCfgParam(projectCfg);
                        }
                        return;
                    }
                }

                cfgList.add(cfgKey);
                createParameter();
                DexterAnalyzer.getInstance().addProjectAnalysisConfiguration(projectCfg);
            }
        });

        final Button removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        removeButton.setText("-");
        removeButton.setToolTipText("remove selected configuration");

        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                int[] selected = cfgList.getSelectionIndices();
                for (int i : selected) {
                    String key = cfgList.getItem(i);
                    DexterAnalyzer.getInstance().removeCfgParam(key);
                    cfgList.remove(selected);
                }

            }
        });

        final Button resetButton = new Button(buttonComposite, SWT.PUSH);
        resetButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        resetButton.setText("reset");
        resetButton.setToolTipText("remove all configiratuons");

        resetButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                widgetDefaultSelected(arg0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                cfgList.removeAll();
                DexterAnalyzer.getInstance().removeAllCfgParam();
                validate();
            }
        });
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

        newShell.setText("Run Dexter Analysis");
    }

    private boolean validate() {
        setErrorMessage(null);

        if (Strings.isNullOrEmpty(projectNameText.getText())) {
            setErrorMessage("Project Name field can't be empty");
            return false;
        }

        final String fullPath = projectFullPathText.getText();
        if (Strings.isNullOrEmpty(fullPath)) {
            setErrorMessage("Project Home Directory field can't be empty");
            return false;
        }

        if (new File(fullPath).exists() == false) {
            setErrorMessage("project folder is not exist : " + fullPath);
            return false;
        }

        if (sourceDirList.getItems().length <= 0) {
            setErrorMessage("Source Directory field can't be empty");
            return false;
        }

        for (String dir : sourceDirList.getItems()) {
            if (new File(dir).exists() == false) {
                setErrorMessage("source folder is not exist : " + dir);
                return false;
            }
        }

        for (String dir : headerDirList.getItems()) {
            if (new File(dir).exists() == false) {
                setErrorMessage("header folder is not exist : " + dir);
                return false;
            }
        }

        if (typeButtons[1].getSelection()) { // type = FOLDER
            for (String dir : targetDirList.getItems()) {
                if (new File(dir).exists() == false) {
                    setErrorMessage("target folder is not exist : " + dir);
                    return false;
                }
            }

            if (targetDirList.getItemCount() == 0) {
                setErrorMessage("you have to add more than one target folder");
                return false;
            }
        }

        setMessage("Your input is fine");

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        if (validate() == false) {
            return;
        }

        setMemento();
        createParameter();

        super.okPressed();
    }

    private void createParameter() {
        projectCfg.setProjectName(projectNameText.getText());
        projectCfg.setProjectFullPath(projectFullPathText.getText());
        projectCfg.setSourceDirs(sourceDirList.getItems());
        projectCfg.setHeaderDirs(headerDirList.getItems());
        projectCfg.setTargetDirs(targetDirList.getItems());

        for (final Button button : typeButtons) {
            if (button.getSelection()) {
                projectCfg.setType(button.getText());
                break;
            }
        }
    }

    private void setMemento() {
        final IPreferenceStore store = DexterUIActivator.getDefault().getPreferenceStore();
        store.setValue("dexterTarget-projectName", projectNameText.getText());
        store.setValue("dexterTarget-projectFullPath", projectFullPathText.getText());
        for (final Button button : typeButtons) {
            if (button.getSelection()) {
                store.setValue("dexterTarget-type", button.getText());
                break;
            }
        }

        store.setValue("dexterTarget-sourceDirs", DexterUtil.toPathsFromArray(sourceDirList.getItems()));
        store.setValue("dexterTarget-headerDirs", DexterUtil.toPathsFromArray(headerDirList.getItems()));
        store.setValue("dexterTarget-targetDirs", DexterUtil.toPathsFromArray(targetDirList.getItems()));
    }

    private void getMemento() {
        final IPreferenceStore store = DexterUIActivator.getDefault().getPreferenceStore();

        if (Strings.isNullOrEmpty(store.getString("dexterTarget-projectName")) == false) {
            projectNameText.setText(store.getString("dexterTarget-projectName"));
        }

        if (Strings.isNullOrEmpty(store.getString("dexterTarget-projectFullPath")) == false) {
            projectFullPathText.setText(store.getString("dexterTarget-projectFullPath"));
        }

        if (Strings.isNullOrEmpty(store.getString("dexterTarget-type")) == false) {
            for (final Button button : typeButtons) {
                if (store.getString("dexterTarget-type").equals(button.getText())) {
                    button.setSelection(true);

                    if ("PROJECT".equals(button.getText()) || "SNAPSHOT".equals(button.getText())) {
                        setEnableTargetDirField(false);
                    } else {
                        setEnableTargetDirField(true);
                    }
                } else {
                    button.setSelection(false);
                }
            }
        } else {
            setEnableTargetDirField(false);
        }

        final String sourceDirStr = store.getString("dexterTarget-sourceDirs");
        if (Strings.isNullOrEmpty(sourceDirStr) == false) {
            sourceDirList.setItems(sourceDirStr.split(";"));
        }

        final String headerDirStr = store.getString("dexterTarget-headerDirs");
        if (Strings.isNullOrEmpty(headerDirStr) == false) {
            headerDirList.setItems(headerDirStr.split(";"));
        }

        final String targetDirStr = store.getString("dexterTarget-targetDirs");
        if (Strings.isNullOrEmpty(targetDirStr) == false) {
            targetDirList.setItems(targetDirStr.split(";"));
        }

        for (final ProjectAnalysisConfiguration p : DexterAnalyzer.getInstance()
                .getProjectAnalysisConfigurationList()) {
            cfgList.add(p.getCfgKey());
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

    /**
     * @return the projectCfg
     */
    public ProjectAnalysisConfiguration getProjectAnalysisConfiguration() {
        return projectCfg;
    }
}
