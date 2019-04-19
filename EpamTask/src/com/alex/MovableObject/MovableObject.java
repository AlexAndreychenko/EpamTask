package com.alex.MovableObject;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.DoubleAdder;

public abstract class MovableObject implements Runnable {
    private static Random random = new Random(System.nanoTime());
    private static final double ACCELERATION = 5.0;

    private String name = "none";
    private DoubleAdder xPosition = new DoubleAdder();
    private AtomicBoolean movementInterrupted = new AtomicBoolean(false);
    private double xSpeed;
    private long timeElapsed;

    protected abstract void onMove();

    protected MovableObject(double xSpeed){
        this.xSpeed = xSpeed;
        timeElapsed = Long.MAX_VALUE;
    }

    @Override
    public void run() {
        xPosition.reset();
        timeElapsed = System.nanoTime();
        movementInterrupted.set(false);
        while (xPosition.doubleValue() < 1.0){
            try {
                Thread.sleep(Math.abs(random.nextInt()) % 500);
            } catch (InterruptedException ignored) { }
            if (movementInterrupted.get()){
                break;
            }
            xPosition.add(xSpeed);
            onMove();
        }

        timeElapsed = System.nanoTime() - timeElapsed;
    }

    public double getX(){
        return xPosition.doubleValue();
    }

    public void accelerate(){
        xPosition.add(ACCELERATION * xSpeed);
        onMove();
    }

    public long getTimeElapsed(){
        return timeElapsed;
    }

    public void setName(String value){
        name = value;
    }

    @Override
    public String toString(){
        return name;
    }

    public void cancelMoving(boolean reset){
        movementInterrupted.set(true);
        if (reset){
            xPosition.reset();
            onMove();
        }
    }
}
