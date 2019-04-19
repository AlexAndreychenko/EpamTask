package com.alex.RaceManager;

import com.alex.MovableObject.MovableObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RaceManager {
    private Map<MovableObject, Thread> threadMap = new HashMap<>();
    private Thread dispatcher;
    private AtomicBoolean interrupted;
    private int raceId;

    private void waitTermination(Thread t){
        if (t != null && t.isAlive()) {
            while (t.isAlive()) {
                try {
                    t.join();
                } catch (InterruptedException ignored) { }
            }
        }
    }

    protected abstract void updateLeader(String name);
    protected abstract void writeSummary(String log);
    protected abstract void writeInterrupted(String log);

    protected RaceManager(List<? extends MovableObject> list){
        for (MovableObject o : list){
            threadMap.put(o, null);
        }
        interrupted = new AtomicBoolean(false);
    }

    public void startRace() {
        stopRace();
        raceId++;
        interrupted.set(false);
        dispatcher = new Thread(() -> {
            while (!interrupted.get()) {
                double max = 0;
                MovableObject maxObj = null;
                for (MovableObject o : threadMap.keySet()) {
                    if (max < o.getX()){
                        max = o.getX();
                        maxObj = o;
                    }
                }
                if (max >= 1.0){
                    break;
                }
                updateLeader(maxObj != null ? maxObj.toString() : "unknown");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
            }

            for (Thread t : threadMap.values()){
                waitTermination(t);
            }

            if (!interrupted.get()){
                StringBuilder builder = new StringBuilder();
                String separator = System.getProperty("line.separator");
                builder.append("Race ").append(raceId).append(" summary: ");
                builder.append(separator);
                TreeSet<MovableObject> set = new TreeSet<>(Comparator.comparingLong(MovableObject::getTimeElapsed));
                set.addAll(threadMap.keySet());
                updateLeader(set.first().toString());
                int counter = 1;
                for (MovableObject o : set){
                    builder.append(counter).append(" th Place : ").append(o);
                    builder.append(separator);
                    counter++;
                }
                builder.append("------------------------");
                writeSummary(builder.toString());
            }else{
                writeInterrupted("Race " + raceId + " has been interrupted");
            }

        });
        for (Map.Entry<MovableObject, Thread> e : threadMap.entrySet()){
            e.setValue(new Thread(e.getKey()));
            e.getValue().start();
        }
        dispatcher.start();
    }

    public void stopRace(){
        for (MovableObject o : threadMap.keySet()){
            o.cancelMoving(true);
        }
        interrupted.set(true);
        waitTermination(dispatcher);
    }
}
