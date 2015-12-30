package threading;

import java.util.ArrayList;
import java.util.List;

import core.group.Group;
/**
 * 
 * @author Gourgoulhon Maxime & Jacquette Pierrick
 *
 */
public class Service {
	
	private static final List<Couple> processes = new ArrayList<>();
	
	/**
	 * 
	 *	@author Gourgoulhon Maxime & Jacquette Pierrick
	 *
	 */
	private static class Couple {
		public final Group g;
		public final Thread t;
		
		/**
		 * constructor of a service
		 * @param g : group
		 * @param t : thread
		 */
		public Couple(Group g, Thread t) {
			this.g = g;
			this.t = t;
		}
	}
	
	/**
	 * add a service
	 * @param g : group process
	 * @param t : thread process
	 */
	public static void addProcess(Group g, Thread t) {
		Service.processes.add(new Couple(g, t));
	}
	
	/**
	 * kill a service
	 * @param l : the pid 
	 * @throws IllegalArgumentException : if the pid is not exists
	 */
	public static void killProcess(long l) throws IllegalArgumentException {
		int i = Service.processes.size();
		for (Couple c : Service.processes) {
			if (c.g.getPid() == l) {
				c.g.setPid(0);
				c.t.interrupt();
				Service.processes.remove(c);
				break;
			}
		}
		if (Service.processes.size() == i) throw new IllegalArgumentException("aze Unkown PID");
	}
	
	/**
	 *  boolean whether there are many thread
	 * @return : boolean whether there are many thread
	 */
	public static boolean isThereAnyBackgroundProcessRunning() {
		return Service.processes.size() > 0;
	}
	
	/**
	 * 
	 * @return List of Group
	 */
	public static List<Group> getProcesses() {
		List<Group> l = new ArrayList<>();
		for (Couple c : Service.processes)
			l.add(c.g);
		return l;
	}
	
	/**
	 * 
	 * @param l : remove pid 
	 * @throws IllegalArgumentException : 
	 */
	public static void remove(long l) throws IllegalArgumentException {
		
	}

}
