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
import hudson.model.PageDecorator;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 */
@Extension
public final class Html5NotifierPageDecorator extends PageDecorator {
    /**
     * The default value for {@link #enabled}.
     */
    protected static final boolean DEFAULT_ENABLED              = true;

    /**
     * The default value for {@link #queryTimeout}, in seconds.
     */
    protected static final int     DEFAULT_QUERY_TIMEOUT        = 30;

    /**
     * The default value for {@link #notificationTimeout}, in milliseconds.
     */
    protected static final int     DEFAULT_NOTIFICATION_TIMEOUT = 15000;

    /**
     * Whether or not to allow HTML5 notifications.
     */
    private boolean                enabled;

    /**
     * The default time, in seconds, to wait between polling.
     */
    private int                    queryTimeout;

    /**
     * The default time, in milliseconds, to display a notification.
     */
    private int                    notificationTimeout;

    /**
     * Create a default HTML5 web notification {@link PageDecorator}.
     */
    public Html5NotifierPageDecorator() {
        this(DEFAULT_ENABLED, DEFAULT_QUERY_TIMEOUT,
                DEFAULT_NOTIFICATION_TIMEOUT);
    }

    /**
     * Create a HTML5 web notification {@link PageDecorator} with the specified
     * configuration.
     */
    @DataBoundConstructor
    public Html5NotifierPageDecorator(final boolean enabled,
            final int queryTimeout, final int notificationTimeout) {
        super(Html5NotifierPageDecorator.class);
        load();
        this.enabled = enabled;
        this.queryTimeout = queryTimeout;
        this.notificationTimeout = notificationTimeout;
    }

    @Override
    public String getDisplayName() {
        return Messages.HTML5_Notifier_Plugin_DisplayName();
    }

    @Override
    public boolean configure(final StaplerRequest request, final JSONObject json)
            throws FormException {
        request.bindJSON(this, json);
        save();
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(final int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getNotificationTimeout() {
        return notificationTimeout;
    }

    public void setNotificationTimeout(final int notificationTimeout) {
        this.notificationTimeout = notificationTimeout;
    }
}
