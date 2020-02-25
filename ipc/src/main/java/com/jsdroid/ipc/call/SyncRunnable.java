package com.jsdroid.ipc.call;

public class SyncRunnable implements Runnable {
    private final Runnable mTarget;
    private boolean mComplete;

    public SyncRunnable(Runnable target) {
        mTarget = target;
    }

    public void run() {
        mTarget.run();
        synchronized (this) {
            mComplete = true;
            notifyAll();
        }
    }

    public void sync() {
        synchronized (this) {
            while (!mComplete) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
    public void sync(int time) {
        synchronized (this) {
            if (!mComplete) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
