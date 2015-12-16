package threading;

import java.util.ArrayList;
import java.util.List;

import core.group.Group;

public class Service {
	
	private static final List<Couple> processes = new ArrayList<>();
	
	private static class Couple {
		public final Group g;
		public final Thread t;
		public Couple(Group g, Thread t) {
			this.g = g;
			this.t = t;
		}
	}
	
	public static void addProcess(Group g, Thread t) {
		Service.processes.add(new Couple(g, t));
	}
	
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
	
	public static boolean isThereAnyBackgroundProcessRunning() {
		return Service.processes.size() > 0;
	}
	
	public static List<Group> getProcesses() {
		List<Group> l = new ArrayList<>();
		for (Couple c : Service.processes)
			l.add(c.g);
		return l;
	}
	
	public static void remove(long l) throws IllegalArgumentException {
		
	}

}
