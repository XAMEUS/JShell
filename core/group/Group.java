package core.group;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import core.command.Command;
import threading.Service;
/**
 * 
 * @author Gourgoulhon Maxime & Jacquette Pierrick
 *
 */
public class Group implements Runnable {

	private long pid;
	public final List<Command> commands;
	public Command running = null;
	private boolean onNewThread = false;
	
	/**
	 * Constructor of a group with the pid and the list of commands	
	 */
	public Group() {
		this.pid = 0;
		//Service.addProcess(this);
		this.commands = new ArrayList<>();
	}
	
	/**
	 *  add the command to the group
	 * @param c : Command : add the commands to the group
	 */
	public void add(Command c) {
		this.commands.add(c);
	}
	
	/**
	 * shows whether it is a new thread or not
	 * @param b : new Thread ?
	 */
	public void setOnNewThread(boolean b) {
		this.onNewThread = b;
	}
	
	/**
	 * know the pid ocurant
	 * @return pid 
	 */
	public long getPid() {
		return this.pid;
	}
	
	/**
	 * the composition of the group
	 * @return String : the composition of the group
	 */
	@Override
	public String toString() {
		String s = this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " - onNewThread = " + this.onNewThread + "\n{\n";
		for (int i = 0; i < this.commands.size() - 1; i++)
			s += "   " + this.commands.get(i).toString() + ",\n";
		if (this.commands.size() >= 1)
			s += "   " + this.commands.get(this.commands.size()-1).toString();
		return s+"\n}";
	}

	/**
	 * execution of all commands in the group
	 */
	@Override
	public void run() {
		for (Command c : this.commands)
			try {
				this.running = c;
	//			System.out.println(c);
				c.exec();
			} catch (FileNotFoundException e) {
				System.out.println("Erreur : " + e.getMessage());
			} catch (Exception e) {
				System.out.println("Erreur : " + e.getMessage());
			}
		if (this.pid != 0)
			Service.killProcess(this.pid);
	}
	
	/**
	 * execution of all commands in the group and if the group is in a new thread , it executes accordingly
	 */
	public void runAll() {
		if (this.onNewThread) {
			Thread t = new Thread(this);
			this.pid = t.getId();
			Service.addProcess(this, t);
			System.out.println("[" + this.pid + "]");
			t.start();
		}
		else {
			this.run();
		}
	}

	/**
	 * Set the pid
	 * @param l : the new pid 
	 */
	public void setPid(long l) {
		this.pid = l;
	}
	
}
