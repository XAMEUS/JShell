package core.brain;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public class TurtleBrain {

	public static boolean stop = false;

	public static final PrintStream out = System.out;

	public static final String prompt = "turtle > ";

	public static final InputStream in = System.in;

	public static String cwd = System.getProperty("user.dir");

	public static final FileSystem fileSystem = FileSystems.getDefault();
	
	// Windows \\\\ trick vs Unix / (regex)
	public static final String fileSeparator = (!fileSystem.getSeparator().equals("\\")) ? fileSystem.getSeparator()
			: fileSystem.getSeparator() + fileSystem.getSeparator();

}
