package com.kayrnt.android.stringtool.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kayrnt.android.stringstool.Main;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class StringsHandler extends AbstractHandler {
	
	//boolean to check if the synchronize or revert is pressed...
	//I don't know why when you exit eclipse without cancelling
	//it returns "Window.OK"...
	public static boolean processAccepted = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IJavaProject) {
			Main.reset();
			processAccepted = false;
			CustomDialog myDiag = new CustomDialog(shell);
			myDiag.create();
			if((myDiag.open() == Window.OK) && processAccepted) {
				createPopup(shell, firstElement);
			} 

		} else {
			MessageDialog.openInformation(shell, "Info",
					"Did you really select a project ? The target isn't recognized !");
		}
		return null;
	}

	private void createPopup(Shell shell, Object firstElement) {
		IJavaProject project = (IJavaProject) firstElement;
		IPath path = project.getResource().getLocation();
		System.out.println("project  path: "+path);
		Main.main(new String[] {path.toString()});
		MessageDialog.openInformation(shell, "Info",
				"Succesful operation !");
	}

	protected String getPersistentProperty(IResource res, QualifiedName qn) {
		try {
			return res.getPersistentProperty(qn);
		} catch (CoreException e) {
			return "";
		}
	}

	protected void setPersistentProperty(IResource res, QualifiedName qn,
			String value) {
		try {
			res.setPersistentProperty(qn, value);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}


} 