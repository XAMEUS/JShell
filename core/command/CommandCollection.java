package core.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import core.brain.TurtleBrain;
import core.group.Group;
import threading.Service;

public class CommandCollection {
	
	public static String ls() {
		File f = new File(TurtleBrain.cwd);
		File[] l = f.listFiles();
		String s = "";
		for (int i = 0; i < l.length - 1; i++)
			s += String.format("%s%n", l[i].getName());
		if (l.length > 0)
			s += String.format("%s", l[l.length-1].getName());
		return s;
	}
	
	private static List<String> listDir() {
		return CommandCollection.listDir(TurtleBrain.cwd);
	}
	
	private static List<String> listDir(String location) {
		File wd = new File(location);
		List<String> dirs = new ArrayList<>();
		for (File f : wd.listFiles())
			if (f.isDirectory())
				dirs.add(f.getName());
		return dirs;
	}
	
	private static List<String> listFiles(String location) {
		File wd = new File(location);
		List<String> dirs = new ArrayList<>();
		for (File f : wd.listFiles())
			if (f.isFile())
				dirs.add(f.getName());
		return dirs;
	}
	
	private static void cdParent() {
		File f = new File(TurtleBrain.cwd);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		if (!fsv.isFileSystemRoot(f)) {
			System.setProperty("user.dir", fsv.getParentDirectory(f).getAbsolutePath());
			TurtleBrain.cwd = System.getProperty("user.dir");
		}
	}
	
	private static void cdDir(String dir) throws FileNotFoundException {
		if (listDir().contains(dir)) {
			System.setProperty("user.dir", TurtleBrain.cwd + TurtleBrain.fileSystem.getSeparator() + dir);
			TurtleBrain.cwd = System.getProperty("user.dir");
		} else throw new FileNotFoundException("Cannot find directory : " + TurtleBrain.cwd + TurtleBrain.fileSystem.getSeparator() + dir);
	}
	
	public static String cd(List<String> args) throws FileNotFoundException {
		if (args.size() == 0) {
			System.setProperty("user.dir", System.getProperty("user.home"));
			TurtleBrain.cwd = System.getProperty("user.dir");
		}
		else {
			String[] l = args.get(0).split(TurtleBrain.fileSeparator);
			for (String dir : l) {
				if (dir.equals(".."))
					CommandCollection.cdParent();
				else
					CommandCollection.cdDir(dir);
			}
		}
		return "";
	}
	
	public static String pwd() {
		return TurtleBrain.cwd;
	}
	
	public static String find(List<String> args) throws IllegalArgumentException, Exception {
		if (args.size() < 3)
			throw new Exception("find : pas assez d'arguments, utilisez : find <chemin> -opt <expr. reg.> avec opt = name|iname");
		if (!args.get(1).equals("-name") && !args.get(1).equals("-iname"))
			throw new IllegalArgumentException("find : mauvais arguement " + args.get(1) + ", utilisez : -name|-iname");
		return find(args.get(0), args.get(1).equals("-iname"), args.get(2)).trim();
	}
	
	private static String find(String location, boolean iname, String regex) {
		StringBuilder s = new StringBuilder();
		for (String fname : CommandCollection.listFiles(location))
			if (((iname)?Pattern.compile(regex, Pattern.CASE_INSENSITIVE):Pattern.compile(regex)).matcher(fname).find())
				s.append(String.format("%s%n", fname));
		for (String dname : CommandCollection.listDir(location))
			s.append(find(location + TurtleBrain.fileSeparator + dname, iname, regex));
		return s.toString();
	}
	
	public static String ps() {
		StringBuilder s = new StringBuilder();
		for (Group g : Service.getProcesses())
			s.append(String.format("%s : %s%n", g.getPid(), g.running.toString()));
		return s.toString().trim();
	}
	
	public static String sleep(List<String> args) throws Exception {
		if (args.size() == 0) throw new Exception("sleep : pas assez d'arguements, il manque le temps");
		Thread.sleep(Long.valueOf(args.get(0)));
		return "";
	}
	
	public static String kill(List<String> args) throws IllegalArgumentException, Exception {
		if (args.size() == 0) throw new Exception("kill : pas assez d'arguments, il manque le pid");
		Service.killProcess(Long.valueOf(args.get(0)));
		return "";
	}
	
	public static String write(List<String> args) throws Exception {
		if (args.size() < 3) throw new Exception("write : pas assez d'arguments...");
		if (args.get(1).equals("-a")) {
			try {
				Files.write(Paths.get(args.get(0)), args.get(2).getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				return String.format("Le fichier %s n'a pas pu être créé", args.get(0));
			}
		}
		else {
			try {
				Files.write(Paths.get(args.get(0)), args.get(2).getBytes());
			} catch (IOException e) {
				return String.format("Le fichier %s n'a pas pu être édité", args.get(0));
			}
		}
		return "";
	}
	
	public static String date(List<String> args) {
		String s = "+%Y-%m-%d";
		if (args.size() > 0)
			s = args.get(0);
		s = s.substring(1);
		s = s.replaceAll("%H", "HH");
		s = s.replaceAll("%Y", "yyyy");
		s = s.replaceAll("%m", "MM");
		s = s.replaceAll("%M", "mm");
		s = s.replaceAll("%d", "dd");
		return new SimpleDateFormat(s).format(new Date());
	}
	
	public static String WAIT() {
		while (Service.isThereAnyBackgroundProcessRunning()) {
			// be patient :)
			System.out.print("");
		}
		return "";
	}
	
	public static String grep(List<String> args) throws FileNotFoundException, Exception {
		if (args.size() == 0)
			throw new Exception("grep : il manque la regex");
		
		BufferedReader br = null;
		if (args.size() > 1)
			br = new BufferedReader(new FileReader(args.get(1)));
		
		Pattern pattern = Pattern.compile(args.get(0));
		
		StringBuilder s = new StringBuilder("");
		
		for (int i = 1; i < args.size(); i++) {
			int l = 0;
			String line = br.readLine();
			while (line != null) {
				if (pattern.matcher(line).find()) {
			        s.append(args.get(i) + ":" + l + ":: " + line);
			        s.append(String.format("%n"));
				}
		        line = br.readLine();
		        l++;
			}
			br.close();
			if (args.size() > 1 && i < args.size() - 1) {
				br = new BufferedReader(new FileReader(args.get(i+1)));
			}
		}
		
		Scanner sc = new Scanner(TurtleBrain.in);
		if (args.size() == 1) {
			do {
				String line = sc.nextLine();
				if (pattern.matcher(line).find()) {
			        TurtleBrain.out.println(line);
				}
			} while (sc.hasNext());
			//sc.close(); SURTOUT PAS !!!
		}
		
		return s.toString();
	}
	
}
