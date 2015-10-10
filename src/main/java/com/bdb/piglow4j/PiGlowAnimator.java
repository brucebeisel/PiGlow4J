/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.piglow4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that controls one or more animations. This class uses an <code>Executor</code> to schedule animation updates.
 * Multiple animators can be active simultaneously, but the effects are unpredictable as the animators are not synchronized.
 * Though possible, it is not recommended.
 * 
 * @see java.util.concurrent.ScheduledExecutorService
 * @see com.bdb.piglow4j.PiGlowAnimation
 * 
 * @author Bruce Beisel
 */
public final class PiGlowAnimator implements Runnable {
    private ScheduledExecutorService executor;
    private final List<PiGlowAnimation> animations;
    private final PiGlow piGlow;
    private PiGlowLED.Cache cache;
    private static final Logger logger = Logger.getLogger(PiGlowAnimator.class.getName());

    /**
     * Constructor.
     * 
     * @param piGlow The PiGlow being animated
     */
    public PiGlowAnimator(PiGlow piGlow) {
        animations = new ArrayList<>();
        this.piGlow = piGlow;
	cache = PiGlowLED.createCache();
    }

    /**
     * Add animation.
     * 
     * @param animation The animation to add
     */
    public void addAnimation(PiGlowAnimation animation) {
        animations.add(animation);
    }

    /**
     * Return whether this animator is currently running.
     * 
     * @return True if the animator is running
     */
    public boolean isRunning() {
        if (executor == null)
            return false;
        else
            return !executor.isTerminated();
    }

    /**
     * Start the animation.
     */
    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor();
        long now = System.currentTimeMillis();
	logger.fine("Starting animation at " + now);
        animations.forEach((animation)->animation.initialize(now));
	scheduleNextStep(now);
    }

    /**
     * Stop the animation, finishing any outstanding LED changes.
     */
    public void stop() {
        if (executor != null)
            executor.shutdown();
    }

    /**
     * Wait for the animator to terminate after being asked to stop.
     * 
     * @param millis The number of milliseconds to wait for termination
     * 
     * @throws InterruptedException If the wait is interrupted
     */
    public void waitForTermination(long millis) throws InterruptedException {
        if (executor != null && !executor.awaitTermination(millis, TimeUnit.MILLISECONDS))
	    logger.warning("Timed out waiting for executor termination");

        executor = null;
    }

    /**
     * Schedule the next step of this set of animations.
     * 
     * @param now The current time
     */
    private void scheduleNextStep(long now) {
        long millis = Long.MAX_VALUE;
        //
        // Ask each animation when its next change needs to occur.
        // The one that needs the change the soonest will determine when the next
        // task is scheduled.
        //
        for (PiGlowAnimation animation : animations) {
            long nextStepMillis = animation.nextStepMillis(now);
            if (nextStepMillis >= 0)
		millis = Math.min(millis, nextStepMillis);
        }

	logger.finer("Next step in " + millis + " milliseconds");

        //
        // If the millis was never set then all of the animations have completed
        //
        if (millis == Long.MAX_VALUE)
	    executor.shutdown();
	else if (millis == 0)
	    executor.execute(this); // The next change is now
	else
	    executor.schedule(this, millis, TimeUnit.MILLISECONDS);
    }

    /**
     * Called by the executor when the scheduled timer is triggered.
     */
    @Override
    public void run() {
        try {
            long now = System.currentTimeMillis();
            //
            // Tell each animation what the current time is
            //
	    for (PiGlowAnimation animation : animations) {
		cache.refresh();
		animation.executeNextStep(now);

		if (!animation.isEnabled())
	           cache.apply();
	    }

            //
            // Change the actual LEDs
            //
            piGlow.updateLEDs();
            scheduleNextStep(now);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Animation generated an exception", e);
        }
    }
}
