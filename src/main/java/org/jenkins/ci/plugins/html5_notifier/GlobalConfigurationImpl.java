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
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * The {@link jenkins.model.GlobalConfiguration} configuration data for this
 * plugin, {@link javax.inject.Inject}ed into other {@link hudson.Extension}s.
 * 
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 */
@Extension
public final class GlobalConfigurationImpl extends GlobalConfiguration {
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
     * The default value for {@link #allResults}.
     */
    protected static final boolean DEFAULT_ALL_RESULTS          = true;

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
     * Whether or not to show notifications for all build results (enabled) or
     * only build results which have changed the status quo (disabled).
     */
    private boolean                allResults;

    /**
     * Create a default HTML5 web notification {@link PageDecorator}.
     */
    public GlobalConfigurationImpl() {
        this(DEFAULT_ENABLED, DEFAULT_QUERY_TIMEOUT,
                DEFAULT_NOTIFICATION_TIMEOUT, DEFAULT_ALL_RESULTS);
    }

    /**
     * @since 1.1
     */
    @Deprecated
    public GlobalConfigurationImpl(final boolean enabled,
            final int queryTimeout, final int notificationTimeout) {
        this(enabled, queryTimeout, notificationTimeout, DEFAULT_ALL_RESULTS);
    }

    /**
     * Create a HTML5 web notification {@link PageDecorator} with the specified
     * configuration.
     */
    @DataBoundConstructor
    public GlobalConfigurationImpl(final boolean enabled,
            final int queryTimeout, final int notificationTimeout,
            final boolean allResults) {
        super();
        load();
        this.enabled = enabled;
        this.queryTimeout = queryTimeout;
        this.notificationTimeout = notificationTimeout;
        this.allResults = allResults;
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

    public boolean isAllResults() {
        return allResults;
    }

    public void setAllResults(final boolean allResults) {
        this.allResults = allResults;
    }
}
