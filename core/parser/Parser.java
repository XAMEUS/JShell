package core.parser;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.brain.TurtleBrain;
import core.command.Command;
import core.group.Group;
/**
 * 
 * @author Gourgoulhon Maxime & Jacquette Pierrick
 *
 */
public class Parser {
	
	// date +%Y-%m:%d 
	/**
	 * verifies that the user has entered a correct statement semantically
	 * @param input : Entering the User
	 * @return parsed input
	 */
	public static List<Group> inst(String input) {
		String commandName = "([\\p{L}[0-9]]+)";
		String arg = String.format("\\s+((\".*\")|([%s.\\p{L}-[0-9]*\\[\\]+%%:$^]+))", TurtleBrain.fileSeparator);
		String args = String.format("(%s)*", arg);
		String and = "\\s*&?\\s*";
		String filename = ".*";
		String command = String.format("(%s)(%s)", commandName, args);
		String commandL = String.format("(%s)(\\s*[|]\\s*%s)*(\\s*([>]|([>][>]))\\s*%s)?", command, command, arg);
		String group = String.format(
				"(((%s)\\s*;\\s*)*(%s)(%s))|(\\(((%s)\\s*;\\s*)*(%s)\\)(%s))",
				commandL, commandL, and, commandL, commandL, and);
		String groupsList = String.format("((%s)\\s*;\\s*)*(%s)",
				group, group);
		
		//System.out.println(Pattern.compile(groupsList).matcher(input).matches());
		if(!Pattern.matches(groupsList, input)) {
			throw new RuntimeException("no command matches");
		}
		
		String s = input;
		Pattern patternGroup = Pattern.compile(group);
		Pattern patternCommand = Pattern.compile(command);
		Pattern patternCommandL = Pattern.compile(commandL);
		Pattern patternCommandName = Pattern.compile(commandName);
		Pattern patternArgs = Pattern.compile(arg);
		Matcher matcherGroup = patternGroup.matcher(s);
		List<Group> listGroup = new ArrayList<>();
		
		while(matcherGroup.lookingAt()) {
			
			Group g = new Group();

			String cmds = matcherGroup.group(0);

			boolean onNewThread = false;
			cmds = cmds.replaceAll("\\s+$", "");
			if (cmds.length() > 0 && cmds.charAt(cmds.length()-1) == '&') {
				onNewThread = true;
				cmds = cmds.substring(0, cmds.length() - 1);
				cmds = cmds.replaceAll("\\s+$", "");
			}
			
			if (cmds.charAt(0) == '(')
				cmds = cmds.substring(1);
			if (cmds.charAt(cmds.length()-1) == ')')
				cmds = cmds.substring(0, cmds.length()-1);
			
			Command cL = null;
			Matcher matcherCommandL = patternCommandL.matcher(cmds);
			while(matcherCommandL.lookingAt()) {
				String cmd = matcherCommandL.group(0);
				
				Matcher matcherCommand = patternCommand.matcher(cmd);
				Command lastCommand = null;
				while (matcherCommand.lookingAt()) {
					Command c = null;
					Matcher matcherNameCommand = patternCommandName.matcher(cmd);
					if (matcherNameCommand.lookingAt()){
						c = new Command(matcherCommand.group(1));
						cmd = cmd.substring(matcherNameCommand.end());
					}

					Matcher matcherArg = patternArgs.matcher(cmd);
					while (matcherArg.lookingAt()) {
						String arg0 = matcherArg.group(1);
						c.addArg(arg0);
						matcherArg.region(matcherArg.end(), matcherArg.regionEnd());
					}
					
					if (lastCommand != null)
						lastCommand.setNext(c);
					else cL = c;

					if (cmd.length() > 0)
						cmd = cmd.substring(matcherCommand.end() - c.getName().length());
					cmd = cmd.replaceAll("^\\s*", "");
					if (cmd.length() > 0 && cmd.charAt(0) == '|')
						cmd = cmd.substring(1);
					else if (cmd.length() > 0 && cmd.charAt(0) == '>') {
						String option = "-o";
						if (cmd.length() > 1 && cmd.charAt(1) == '>') {
							cmd = cmd.substring(2);
							option = "-a";
						} else cmd = cmd.substring(1);
						cmd = cmd.replaceAll("^\\s*", "");
						Command end = new Command("write");
						end.addArg(cmd);
						end.addArg(option);
						c.setNext(end);
						cmd = ""; //end
					}
					cmd = cmd.replaceAll("^\\s*", "");
					lastCommand = c;
					matcherCommand = patternCommand.matcher(cmd);
				}
				
				cmds = cmds.substring(matcherCommandL.end());
				cmds = cmds.replaceAll("^\\s*", "");
				if (cmds.length() > 0 && cmds.charAt(0) == ';')
					cmds = cmds.substring(1);
				cmds = cmds.replaceAll("^\\s*", "");
				matcherCommandL = patternCommandL.matcher(cmds);
				
				g.add(cL);
			}
			
			s = s.substring(matcherGroup.end());
			s = s.replaceFirst("^\\s*", "");
			if (s.length() > 0 && s.charAt(0) == ';')
				s = s.substring(1);
			s = s.replaceFirst("^\\s*", "");
			g.setOnNewThread(onNewThread);
			listGroup.add(g);
			matcherGroup = patternGroup.matcher(s);
			
		}
		
		return listGroup;
	}
}