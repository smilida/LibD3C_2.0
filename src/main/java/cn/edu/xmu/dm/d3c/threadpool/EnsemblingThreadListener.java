package cn.edu.xmu.dm.d3c.threadpool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EnsemblingThreadListener extends Thread {
    public static boolean isOver = false;

    public List<EnsemblingThread> array = new ArrayList<EnsemblingThread>();

    public List<Double> correctRate = new ArrayList<Double>();

    public List<String> strategies = new ArrayList<String>();

    public List<List<Integer>> ClassifierNos = new ArrayList<List<Integer>>();

    long sleepTime = 1000L;

    public void run() {
        boolean flag = false;
        long startTime = System.currentTimeMillis();
        while (!flag) {
            boolean isRemoved = false;
            try {
                Thread.sleep(this.sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < this.array.size(); i++) {
                if (((EnsemblingThread)this.array.get(i)).isFinished()) {
                    Calendar c = Calendar.getInstance();
                    this.correctRate.add(Double.valueOf(((EnsemblingThread)this.array.get(i)).getCurrentCorrectRate()));
                    this.ClassifierNos.add(((EnsemblingThread)this.array.get(i)).getClassifierNo());
                    this.strategies.add(((EnsemblingThread)this.array.get(i)).getStrategy());
                    this.array.remove(i);
                    isRemoved = true;
                }
                if (!isRemoved) {
                    long t2 = System.currentTimeMillis();
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(t2 - startTime);
                    if (c.get(13) >= 10) {
                        ((EnsemblingThread)this.array.get(i)).stop();
                        this.array.remove(i);
                    }
                }
            }
            if (this.array.size() == 0) {
                flag = true;
                isOver = true;
            }
        }
    }
}
