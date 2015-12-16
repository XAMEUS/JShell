package core.command;

import java.util.ArrayList;
import java.util.List;

import core.brain.TurtleBrain;

public class Command {
	
	private String name;
	private final List<String> args;
	private Command next;
	
	public Command(String name) {
		this.name = name;
		this.args = new ArrayList<>();
		this.next = null;
	}
	
	public Command(String name, List<String> args) {
		this.name = name;
		this.args = args;
		this.next = null;
	}
	
	public String getName()	{
		return this.name;
	}
	
	public void addArg(String arg) {
		this.args.add(arg);
	}
	
	public void setNext(Command c) {
		this.next = c;
	}
	
	@Override
	public String toString() {
		String s = this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this));
		s += "{name=" + this.name + ", args=" + this.args + ", next=" + this.next + "}";
		return s;
	}
	
	public void exec() throws Exception {
		if (this.next == null) {
			try {
				String msg = Command.exec(this.name, this.args);
				if (msg.length() > 0)
					TurtleBrain.out.println(msg);
			} catch (InterruptedException e) {
				System.out.println("aze");
			}
		}
		else {
			this.next.addArg(Command.exec(this.name, this.args));
			this.next.exec();
		}
	}
	
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
