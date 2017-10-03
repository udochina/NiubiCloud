package com.uxcontry.geekserver;

import java.util.ArrayList;
import java.util.List;

/*
 * 简单的计时任务系统
 */

public class Timer 
{
	private static Thread timerThread = null;
	private static List<Task> tasks;
	public static final void initiate(){
		timerThread = new TimerThread();
		tasks = new ArrayList<Task>();
		timerThread.setPriority(Thread.MAX_PRIORITY-3);
		timerThread.setName("Anyfast.TimerThread");
		timerThread.start();
	}
	public static final void destroy(){
		timerThread.interrupt();
	}
	public static class TimerThread extends Thread
	{
		public void run()
		{
			for(;;){
				try {
						sleep(800);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
				}
				for(Task t : tasks) {
					if((t!=null) && (System.currentTimeMillis() - t.startTime) >= (t.timeout * 1000)) {
						if(t.next) {
							new Thread(t.task).start();
							if(!t.interval) {
								t.next = false;
							} else {
								t.startTime = System.currentTimeMillis();
							}
						}
					}
				}
			}
		}
	}
	public static class Task{
		public Runnable task = null;
		public int timeout = 0;
		public long startTime = 0;
		public boolean interval = false;
		public boolean next = true;
	}
	public static final int setTimeout(Runnable task,int timeout){
		Task t = new Task();
		t.task = task;
		t.timeout = timeout;
		t.startTime = System.currentTimeMillis();
		tasks.add(t);
		return tasks.size()-1;
	}
	public static final int setInterval(Runnable task,int timeout){
		Task t = new Task();
		t.task = task;
		t.timeout = timeout;
		t.interval = true;
		t.startTime = System.currentTimeMillis();
		tasks.add(t);
		return tasks.size()-1;
	}
	public static final void remove(int id){
		tasks.remove(id);
	}
	public static final void clear(){
		tasks.clear();
	}
	public static final int getAllThreadNum(){
		return Thread.getAllStackTraces().size();
	}
}
