package com.kayrnt.android.stringtool.handlers;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kayrnt.android.stringstool.Main;

public class CustomDialog extends TitleAreaDialog {

	public CustomDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle("Synchronise all your Android strings.xml");
		// Set the message
		setMessage("Are you sure you want to synchronise your strings.xml or revert to your backup ?", 
				IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.verticalAlignment = GridData.CENTER;

		Button backupActivate = new Button(parent, SWT.CHECK);
		backupActivate.setText("Check NOT to backup strings.xml (only for synchronize)");
		backupActivate.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Main.backup = !Main.backup;
			}
		});

		backupActivate.setLayoutData(gridData);

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
//		((GridLayout) parent.getLayout()).makeColumnsEqualWidth = true;
		
		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", true);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
		
		Button revert = createRevertButton(parent, OK, "Revert", false);
		
		// Create Synchronize button
		Button synchronize = createSynchronizeButton(parent, OK, "Synchronize", false);


	}

	protected Button createSynchronizeButton(Composite parent, int id, 
			String label,
			boolean defaultButton) {
		Button button = new Button(parent, SWT.PUSH);
		((GridLayout) parent.getLayout()).numColumns++;
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				StringsHandler.processAccepted = true;
				okPressed();
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}


	protected Button createRevertButton(Composite parent, int id, 
			String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				StringsHandler.processAccepted = true;
				Main.revert = true;
				okPressed();
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

}