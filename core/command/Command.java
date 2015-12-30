package core.command;

import java.util.ArrayList;
import java.util.List;

import core.brain.TurtleBrain;
/**
 * 
 * @author Gourgoulhon Maxime & Jacquette Pierrick
 *
 */
public class Command {
	
	private String name;
	private final List<String> args;
	private Command next;
	
	/**
	 * This is the constructor of command , with the name , the list of arguments is null and the following command as null
	 * @param name : String that is the name of the command
	 */
	public Command(String name) {
		this.name = name;
		this.args = new ArrayList<>();
		this.next = null;
	}
	
	/**
	 * This is the constructor of command , with the name , the following command is null
	 * @param name : String that is the name of the command
	 * @param args : which is a list of the command arguments
	 */
	public Command(String name, List<String> args) {
		this.name = name;
		this.args = args;
		this.next = null;
	}
	
	/**
	 * allows to know the name of the command
	 * @return String : String that is the name of the command
	 */
	public String getName()	{
		return this.name;
	}
	
	/**
	 * Adding an argument to the current command
	 * @param arg  :Adding an argument to the list of the command arguments
	 */
	public void addArg(String arg) {
		this.args.add(arg);
	}
	
	/**
	 * adding a command for the current command
	 * @param c : add the following command
	 */
	public void setNext(Command c) {
		this.next = c;
	}
	
	/**
	 * displays the current command
	 */
	@Override
	public String toString() {
		String s = this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this));
		s += "{name=" + this.name + ", args=" + this.args + ", next=" + this.next + "}";
		return s;
	}
	
	/**
	 * execution of the command by checking the arguments
	 * @throws Exception : if there is an error during the execution of the command
	 */
	public void exec() throws Exception {
		if (this.next == null) {
			try {
				String msg = Command.exec(this.name, this.args);
				if (msg.length() > 0)
					TurtleBrain.out.println(msg);
			} catch (InterruptedException e) {
				System.out.println("Problem execution ");
			}
		}
		else {
			this.next.addArg(Command.exec(this.name, this.args));
			this.next.exec();
		}
	}
	
	/**
	 * execution of the command
	 * @param cname : name of command
	 * @param args :  argument list of command
	 * @return : String which is the result of the command
	 * @throws Exception : a problem when executing
	 */
	public static String exec(String cname, List<String> args) throws Exception {
		switch (cname) {
		case "exit":
			System.exit(0);
			break;
		case "ls":
			return CommandCollection.ls();
		case "cd":
			return CommandCollection.cd(args);
		case "pwd":
			return CommandCollection.pwd();
		case "find":
			return CommandCollection.find(args);
		case "ps":
			return CommandCollection.ps();
		case "sleep":
			return CommandCollection.sleep(args);
		case "kill":
			return CommandCollection.kill(args);
		case "write":
			return CommandCollection.write(args);
		case "date":
			return CommandCollection.date(args);
		case "wait":
			return CommandCollection.WAIT();
		case "grep":
			return CommandCollection.grep(args);
		default:
			return "Commande inconnue : " + cname;
		}
		return "";
	}
	
}
