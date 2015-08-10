package com.uteacher.www.uteacherble.TestUtil;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cartman on 15/5/30.
 */
public class TestScheduler {

    private HandlerThread testThread;
    private Handler mHandler;

    private HandlerThread eventThread;
    private Handler eHandler;

    private Timer timer = null;

    private int mEvent;
    private STATE mState;

    private enum STATE {
        RUNNING,
        RUNNABLE,
        ABORT,
    }

    public TestScheduler(String name) {
        testThread = new HandlerThread(name);
        testThread.start();
        mHandler = new Handler(testThread.getLooper());

        eventThread = new HandlerThread(name + ".eventThread");
        eventThread.start();
        eHandler = new Handler(eventThread.getLooper());

        mState = STATE.RUNNABLE;
    }


    public interface eventCallback {
        void eventNotify(int id, int event);
    }

    private void postEvent(final int id, final int event, final eventCallback onEvent) {
        eHandler.post(new Runnable() {
            @Override
            public void run() {
                onEvent.eventNotify(id, event);
            }
        });
    }

    private void startTimeoutTimer(long timeout, final int timeoutEvent) {
        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (mHandler) {
                    mEvent = timeoutEvent;
                    mHandler.notify();
                }
            }
        }, timeout);
    }

    private void stopTimeoutTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void testAndWaitEvent(final int id, final Runnable runnable, final long timeout, final eventCallback onEvent, final int timeoutEvent) {
        synchronized (mHandler) {
            if (mState != STATE.RUNNABLE) {
                //FIXME: should throw exception.
                return;
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mState == STATE.ABORT) {
                            postEvent(id, mEvent, onEvent);
                            mState = STATE.RUNNABLE;
                            return;
                        }

                        runnable.run();
                        startTimeoutTimer(timeout, timeoutEvent);

                        synchronized (mHandler) {
                            mHandler.wait();
                            postEvent(id, mEvent, onEvent);
                            mState = STATE.RUNNABLE;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            mState = STATE.RUNNING;
        }

    }

    public void testAndWaitEvent(final int id, final Runnable runnable, final long delay, final long timeout, final eventCallback onEvent, final int timeoutEvent) {
        synchronized (mHandler) {
            if (mState != STATE.RUNNABLE) {
                //FIXME: should throw exception.
                return;
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mState == STATE.ABORT) {
                            postEvent(id, mEvent, onEvent);
                            mState = STATE.RUNNABLE;
                            return;
                        }

                        runnable.run();
                        startTimeoutTimer(timeout, timeoutEvent);

                        synchronized (mHandler) {
                            mHandler.wait();
                            postEvent(id, mEvent, onEvent);
                            mState = STATE.RUNNABLE;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, delay);
            mState = STATE.RUNNING;
        }

    }

    public void notifyEvent(int event) {
        synchronized (mHandler) {
            if (mState != STATE.RUNNING) {
                //FIXME: should throw exception.
                return;
            }

            stopTimeoutTimer();
            mEvent = event;
            mHandler.notify();
        }
    }

    public void abort(int abortEvent) {
        synchronized (mHandler) {
            stopTimeoutTimer();
            mState = STATE.ABORT;
            mEvent = abortEvent;
            mHandler.notify();
        }
    }

}
