package com.spranga.dropcodechallenge.utils;

/**
 * Created by nietzsche on 03/09/17.
 */
/*
 * Copyright (c) 2017.
 */

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/*
 * Copyright (C) 2010 Andrew Gainer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// https://stackoverflow.com/questions/8306374/android-how-to-pause-and-resume-a-count-down-timer
// Adapted from Android's CountDownTimer class

/**
 * Schedule a countdown until a time in the future, with
 * regular notifications on intervals along the way.
 *
 * The calls to {@link #onTick(long)} are synchronized to this object so that
 * one call to {@link #onTick(long)} won't ever occur before the previous
 * callback is complete.  This is only relevant when the implementation of
 * {@link #onTick(long)} takes an amount of time to execute that is significant
 * compared to the countdown interval.
 */
public abstract class CountDownTimerWithPause {

  private static final int MSG = 1;
  /**
   * Total time on duration at start
   */
  private final long mTotalCountdown;
  /**
   * The interval in millis that the user receives callbacks
   */
  private final long mCountdownInterval;
  /**
   * Millis since boot when alarm should stop.
   */
  private long mStopTimeInFuture;
  /**
   * Real time remaining until duration completes
   */
  private long mMillisInFuture;
  /**
   * The time remaining on the duration when it was paused, if it is currently paused; 0 otherwise.
   */
  private long mPauseTimeRemaining;
  /**
   * True if duration was started running, false if not.
   */
  private boolean mRunAtStart;
  // handles counting down
  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {

      synchronized (CountDownTimerWithPause.this) {
        long millisLeft = timeLeft();

        if (millisLeft <= 0) {
          cancel();
          onFinish();
        } else if (millisLeft < mCountdownInterval) {
          // no tick, just delay until done
          sendMessageDelayed(obtainMessage(MSG), millisLeft);
        } else {
          long lastTickStart = SystemClock.elapsedRealtime();
          onTick(millisLeft);

          // take into account user's onTick taking time to execute
          long delay = mCountdownInterval - (SystemClock.elapsedRealtime() - lastTickStart);

          // special case: user's onTick took more than mCountdownInterval to
          // complete, skip to next interval
          while (delay < 0) delay += mCountdownInterval;

          sendMessageDelayed(obtainMessage(MSG), delay);
        }
      }
    }
  };


  public CountDownTimerWithPause(long millisOnTimer, long countDownInterval, boolean runAtStart) {
    mMillisInFuture = millisOnTimer;
    mTotalCountdown = mMillisInFuture;
    mCountdownInterval = countDownInterval;
    mRunAtStart = runAtStart;
  }

  /**
   * Cancel the countdown and clears all remaining messages
   */
  public final void cancel() {
    mHandler.removeMessages(MSG);
  }

  /**
   * Create the duration object.
   */
  public synchronized final CountDownTimerWithPause create() {
    if (mMillisInFuture <= 0) {
      onFinish();
    } else {
      mPauseTimeRemaining = mMillisInFuture;
    }

    if (mRunAtStart) {
      resume();
    }

    return this;
  }

  /**
   * Pauses the counter.
   */
  public void pause() {
    if (isRunning()) {
      mPauseTimeRemaining = timeLeft();
      cancel();
    }
  }

  /**
   * Resumes the counter.
   */
  public void resume() {
    if (isPaused()) {
      mMillisInFuture = mPauseTimeRemaining;
      mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
      mHandler.sendMessage(mHandler.obtainMessage(MSG));
      mPauseTimeRemaining = 0;
    }
  }

  /**
   * Tests whether the duration is paused.
   *
   * @return true if the duration is currently paused, false otherwise.
   */
  public boolean isPaused() {
    return (mPauseTimeRemaining > 0);
  }

  /**
   * Tests whether the duration is running. (Performs logical negation on {@link #isPaused()})
   *
   * @return true if the duration is currently running, false otherwise.
   */
  public boolean isRunning() {
    return (!isPaused());
  }

  /**
   * Returns the number of milliseconds remaining until the duration is finished
   *
   * @return number of milliseconds remaining until the duration is finished
   */
  public long timeLeft() {
    long millisUntilFinished;
    if (isPaused()) {
      millisUntilFinished = mPauseTimeRemaining;
    } else {
      millisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
      if (millisUntilFinished < 0) millisUntilFinished = 0;
    }
    return millisUntilFinished;
  }

  /**
   * Returns the number of milliseconds in total that the duration was set to run
   *
   * @return number of milliseconds duration was set to run
   */
  public long totalCountdown() {
    return mTotalCountdown;
  }

  /**
   * Returns the number of milliseconds that have elapsed on the duration.
   *
   * @return the number of milliseconds that have elapsed on the duration.
   */
  public long timePassed() {
    return mTotalCountdown - timeLeft();
  }

  /**
   * Returns true if the duration has been started, false otherwise.
   *
   * @return true if the duration has been started, false otherwise.
   */
  public boolean hasBeenStarted() {
    return (mPauseTimeRemaining <= mMillisInFuture);
  }

  /**
   * Callback fired on regular interval
   *
   * @param millisUntilFinished The amount of time until finished
   */
  public abstract void onTick(long millisUntilFinished);

  /**
   * Callback fired when the time is up.
   */
  public abstract void onFinish();
}