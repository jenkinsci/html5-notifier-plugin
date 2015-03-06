/*
 * The MIT License
 * 
 * Copyright (c) 2011, Jesse Farinacci
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkins.ci.plugins.html5_notifier;

import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

/**
 * Conveniently keep track of all {@link Run}s as they complete, as
 * {@link RunNotification}s, and provide time-based access to them.
 * 
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 */
@Extension
public final class RunListenerImpl extends RunListener<Run<?, ?>> {
    /**
     * The maximum number of entries in the {@link Queue}. When we move to Java
     * 6 there are some better options than this.. ugh.
     */
    private static final int                    MAX_QUEUE_SIZE = 2048;

    /**
     * The {@link Queue} of completed {@link Run}s, as {@link RunNotification}s.
     */
    private static final Queue<RunNotification> queue          = new ConcurrentLinkedQueue<RunNotification>();

    protected static void clean() {
        while (queue.size() > MAX_QUEUE_SIZE) {
            queue.remove();
        }
    }

    public static List<RunNotification> getAllFutureRunNotifications(
            final Date date) {
        final List<RunNotification> notifications = new LinkedList<RunNotification>();

        if (date != null) {
            for (final RunNotification notification : queue) {
                if (date.before(notification.getDate())) {
                    notifications.add(notification);
                }
            }
        }

        return notifications;
    }

    public static RunNotification getRunNotificationByIdx(final int idx) {
        for (final RunNotification notification : queue) {
            if (idx == notification.getIdx()) {
                return notification;
            }
        }

        return null;
    }

    @Inject
    private GlobalConfigurationImpl globalConfiguration;

    protected void add(final Run<?, ?> run) {
        if (run != null) {
            final Job<?, ?> parent = run.getParent();
            if (parent != null) {
                final JobPropertyImpl property = parent
                        .getProperty(JobPropertyImpl.class);
                if (property == null || !property.isSkip()) {
                    add(new RunNotification(run));
                }
            }
        }
    }

    protected void add(final RunNotification run) {
        if (run != null) {
            if (globalConfiguration.isAllResults()) {
                queue.add(run);
            }

            else if (run.isDifferentResult()) {
                queue.add(run);
            }
        }
    }

    public GlobalConfigurationImpl getGlobalConfiguration() {
        return globalConfiguration;
    }

    @Override
    public void onCompleted(final Run<?, ?> run, final TaskListener listener) {
        add(run);
        clean();
    }
}
