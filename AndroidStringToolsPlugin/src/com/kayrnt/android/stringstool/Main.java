package com.kayrnt.android.stringstool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.kayrnt.android.stringstool.model.AndroidStringRessource;
import com.kayrnt.android.stringstool.model.ResourcesElement;
import com.kayrnt.android.stringstool.model.StringElement;
import com.kayrnt.android.stringstool.utils.ParserUtils;


public class Main {

	static ArrayList<AndroidStringRessource> stringsFile;
	static Unmarshaller unmarshaller;
	static Marshaller marshaller;
	static long start;
	public static boolean backup = true;
	public static boolean revert = false;

	public static void main(String[] args)
	{
		String path = null;
		// checking for a path or an option
		if(args.length > 0) {
			//looking for backup option
			for(int i = 0; i < args.length; i++) {
				if(args[i].equals("-nobackup")) {
					backup = false;
				}
				//allow to revert to the backup files ;)
				else if(args[i].equals("-revert")) {
					revert = true;
				}
				else if(args[i] != null) {
					path = args[i];
				}
			}
		}
		//appending ressources folder
		String resPath = (path == null ? "" : path+"/") +"res";
		resPath = resPath.replaceAll("//", "/");
		System.out.println("searching at path : "+resPath);
		start = System.currentTimeMillis();
		stringsFile = new ArrayList<AndroidStringRessource>();
		File file = new File(resPath);
		if(!file.exists()) {
			System.out.println("Resources of project directory not found...");
			return;
		}
		try{
			//prepare XML reader
			unmarshaller = JAXBContext.newInstance(ResourcesElement.class).createUnmarshaller();
			visitAllFiles(file);

			//time to sync !
			if(revert) {
				revertStrings();
			} else {
				syncStrings();
			}
		}
		catch(Exception e){
			System.out.print("exception : "+e.getCause().getMessage());
		}
	}

	//function to revertStrings
	private static void revertStrings() {
		for(int i = 0; i < stringsFile.size(); i++) {
			File current = stringsFile.get(i).getFile();
			//checking to revert else for the default res/values/strings.xml who isn't modified or saved
			if(!current.getAbsolutePath().endsWith("res/values/strings.xml")) {
				//replace the file by its backup if found else throws an error in the log
				String valueFolderName = current.getParentFile().getName();
				File projectFolder = current.getParentFile().getParentFile().getParentFile();
			copyFileTo(new File(projectFolder.getAbsolutePath()+"/backup/"+valueFolderName+"/strings.xml"), current);
			}
		}
		System.out.println("Revert done succesfully");
	}


	//Parse file and init our structures
	private static void readFile(File file, String parent) throws Exception
	{

		ResourcesElement adr = new ResourcesElement();
		String fileTransformed = ParserUtils.getString(file);
		//System.out.println(fileTransformed);
		adr = ResourcesElement.class.cast(unmarshaller.unmarshal(new StringReader(fileTransformed)));
		System.out.println("reading : "+file.getParent()+"/"+file.getName());
		//System.out.println("res size : "+adr.string.size());
		stringsFile.add(new AndroidStringRessource(file,adr));
	}


	//no args version of files process because we are at the root
	private static void visitAllFiles(File dir) throws Exception {
		visitAllFiles(dir, null);
	}

	// Process only files under dir
	private static void visitAllFiles(File dir, String parent) throws Exception {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i= 0; i<children.length; i++) {
				visitAllFiles(new File(dir, children[i]), dir.getName());
			}
		} else {
			readStrings(dir, parent);
		}
	}

	private static void readStrings(File file, String parent) throws Exception {
		//we read the file only if it's a strings.xml
		if(file.getName().equals("strings.xml")) {
			readFile(file, parent);
		}
	}

	private static void syncStrings() {
		System.out.println("number of standard XML : "+stringsFile.size());
		// finding the standard values
		AndroidStringRessource standardXML = null;
		for(int i = 0; i < stringsFile.size(); i++) {
			AndroidStringRessource current = stringsFile.get(i);
			if(current.getFile().getAbsolutePath().endsWith("res/values/strings.xml")) {
				//reference file
				standardXML = current;
			} else {
				backupFileIfRequired(current.getFile());
			}
		}

		//if null print error and stop
		if(standardXML == null) {
			System.out.println("No standard strings.xml found in res/values/ of this project");
			System.out.println("The program found : "+stringsFile.size()+" values files.");
			for(int i = 0; i < stringsFile.size(); i++) {
				System.out.println("path for element "+i+" is "+stringsFile.get(i).getFile().getAbsolutePath());
			}
		} else {
			System.out.println("default xml strings found...");
		}

		//preparing the marshaller for string elements 
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(StringElement.class);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//Removing the standardXML from list that is going
		stringsFile.remove(standardXML);

		//preparing string element list
		List<StringElement> standardStrings = standardXML.getResources().string;

		//init the vectors to this size
		int standardStringsSize = standardStrings.size();
		System.out.println("strings : "+standardStrings.size());
		for(int i = 0; i < stringsFile.size(); i++) {
			AndroidStringRessource current = stringsFile.get(i);
			current.setParts(new String[standardStringsSize]);
		}

		//Now iterating on its values
		final ExecutorService pool;
		pool = Executors.newCachedThreadPool();
		for(int i = 0; i < standardStrings.size(); i++) {
			final StringElement currentElement = standardStrings.get(i);
			pool.execute(new ComparingHandler(currentElement, i));
		}

		//Callable that will be used when every string is processed
		ArrayList<Callable<Void>> callables = new ArrayList<Callable<Void>>();
		callables.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				//multithread forced to put strings in right place to be merged
				Main.mergeStrings();
				//writing final files
				Main.closeAndroidXMLStrings();
				//benchmark !
				System.out.println("time consumed :"+(System.currentTimeMillis()-start)+" ms");
				//time to quit ;)
				pool.shutdown();
				return null;
			}
		});

		//callable set up... time to invoke !
		try {
			pool.invokeAll(callables);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void backupFileIfRequired(File file) {
		//adding .backup to the name ... hopefully it won't be a too bad idea since I don't do any check
		String valueFolderName = file.getParentFile().getName();
		File projectFolder = file.getParentFile().getParentFile().getParentFile();
		File to = new File(projectFolder.getAbsolutePath()+"/backup/"+valueFolderName+"/strings.xml");
		File parent = to.getParentFile();
		if(!parent.exists() && !parent.mkdirs()){
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		copyFileTo(file, to);
		System.out.println("backup done for element : "+file.getAbsolutePath());
	}

	private static void copyFileTo(File from, File to) {

		try{
			InputStream in = new FileInputStream(from);

			//For Overwrite the file.
			OutputStream out = new FileOutputStream(to);

			//buffer writings... yey
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
		catch(FileNotFoundException ex){
			System.out.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		}
		catch(IOException e){
			System.out.println(e.getMessage());  
		}
	}


	//function to compare an element in other files
	static void checkElementIsInOtherStringsXML(StringElement stringElement, int position) {
		for(int i = 0; i < stringsFile.size(); i++) {
			List<StringElement> currentList = stringsFile.get(i).getResources().string;
			String[] parts = stringsFile.get(i).getParts();
			//It's a comment so we just rename it and print it properly
			if(stringElement.name.startsWith("__comment_")) {
				parts[position] = "<!-- "+stringElement.text+" -->\n";
			} else {
				//lets search it...
				boolean found = false;
				for(int j = 0; j < currentList.size(); j++) {
					StringElement currentString = null;
					//					synchronized (stringsFile) {
					currentString = currentList.get(j);
					//					}
					//if found we print and break
					if(currentString.name.equals(stringElement.name)) {
						found = true;
						parts[position] = "<string name=\""+currentString.name+"\">"+currentString.text+"</string>\n";
						//						synchronized (stringsFile) {
						//							currentList.remove(j);
						//						}
						break;
					}
				}
				//else we print as empty
				if(!found){
					parts[position] = "<string name=\""+stringElement.name+"\"></string>\n";
				}
			}
		}
	}

	//merging function
	static void mergeStrings() {
		for(int i = 0; i < stringsFile.size(); i++) {
			AndroidStringRessource current = stringsFile.get(i);
			StringBuilder builder = new StringBuilder();
			String[] parts = current.getParts();
			for(int j = 0; j < parts.length; j++) {
				builder.append(parts[j]);
			}
			//put the merged string of the xml content into the structure
			current.setTransformed("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"+ builder.toString()+"</resources>");
		}
	}


	static void closeAndroidXMLStrings() {
		//close the strings resources and write
		for(int i = 0; i < stringsFile.size(); i++) {
			// file
			try{
				// Create file 
				FileWriter fstream = new FileWriter(stringsFile.get(i).getFile());
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(stringsFile.get(i).getTransformed());
				//Close the output stream
				out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
	
	public static void reset() {
		Main.backup = true;
		Main.revert = false;
	}

}
