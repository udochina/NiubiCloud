package com.niubicloud.service;

import java.util.ArrayList;

import com.niubicloud.base.Service;

public class Timer {
	ArrayList<Task> tasks = new ArrayList<Task>();
	Runner runner = null;
	
	private class Task {
		public Task(Service service2, int t, int i) {
			// TODO Auto-generated constructor stub
			this.service = service2;
			this.timeByMilles = t;
			this.maxRunCount = i;
			this.lastRunTime = System.currentTimeMillis();
		}
		public int maxRunCount = 0;
		public int timeByMilles = 0;
		public Service service = null;
		
		public long lastRunTime = 0;
		public int lastRunCount = 0;
	}
	
	public class Runner extends Thread {
		@Override
		public void run() {
			for(;;) {
				for(Task task : tasks) {
					if(task == null) {
						continue;
					}
					if(task.service == null) {
						continue;
					}
					if(System.currentTimeMillis() - task.lastRunTime >= task.timeByMilles) {
						task.service.handle();
						task.lastRunTime = System.currentTimeMillis();
						task.lastRunCount++;
						if(task.lastRunCount >= task.maxRunCount && task.maxRunCount > 0) {
							tasks.remove(task);
						}
					}
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
	}
	
	private void start() {
		// TODO Auto-generated method stub
		runner = new Runner();
		runner.start();
	}
	
	
	static Timer timer = null;
	
	public static final void init() {
		if(timer == null) {
			timer = new Timer();
			timer.start();
		}
	}
	
	public static boolean setTimeout(Service service,int t) {
		return timer.tasks.add(timer.new Task(service,t,1));
	}
	
	public static boolean setInterval(Service service,int t) {
		return timer.tasks.add(timer.new Task(service,t,0));
	}
	
	public static boolean setInterval(Service service,int t,int maxCount) {
		return timer.tasks.add(timer.new Task(service,t,maxCount));
	}
}
