import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import core.brain.TurtleBrain;
import core.group.Group;
import core.parser.Parser;
/**
 * 
 * @author Gourgoulhon Maxime & Jacquette Pierrick
 *
 */
public class Turtle {
	/**
	 * 
	 *This is the "main" program
	 */
	public static void main(String[] args) {
		
		while (true) {
			System.out.print(TurtleBrain.prompt);
			BufferedReader reader = new BufferedReader(new InputStreamReader(TurtleBrain.in));
			try {
				String s = reader.readLine();
				if (s == null) {
					reader.close();
					reader = new BufferedReader(new InputStreamReader(System.in));
					continue;
				}
				List<Group> l = Parser.inst(s);
				for (Group g : l)
					g.runAll();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				//TRY AGAIN! :)
				System.out.println(e.getMessage());
			}
			
		}
		
	}
	
}
