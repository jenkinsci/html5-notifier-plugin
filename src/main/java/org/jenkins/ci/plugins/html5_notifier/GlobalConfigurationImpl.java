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
import hudson.model.Item;
import hudson.model.PageDecorator;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundSetter;

import net.sf.json.JSONSerializer;

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
     * The default value for {@link #enabledFavorites}.
     */
    protected static final boolean DEFAULT_ENABLED_FAVORITES    = true;

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
     * Whether or not to use favorites plugin if installed
     */
    private boolean                enabledFavorites;

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
        this.enabled = DEFAULT_ENABLED;
        this.enabledFavorites = DEFAULT_ENABLED_FAVORITES;
        this.notificationTimeout = DEFAULT_NOTIFICATION_TIMEOUT;
        this.allResults = DEFAULT_ALL_RESULTS;
        load();
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

    @DataBoundSetter
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabledFavorites() {
        return enabledFavorites;
    }

    @DataBoundSetter
    public void setEnabledFavorites(final boolean enabled) {
        this.enabledFavorites = enabled;
    }

    public int getNotificationTimeout() {
        return notificationTimeout;
    }

    @DataBoundSetter
    public void setNotificationTimeout(final int notificationTimeout) {
        this.notificationTimeout = notificationTimeout;
    }

    public boolean isAllResults() {
        return allResults;
    }

    @DataBoundSetter
    public void setAllResults(final boolean allResults) {
        this.allResults = allResults;
    }

    public String getJobsForCurrentUser() {
        if (!this.isEnabledFavorites()) {
            return "null";
        }
        Jenkins instance = Jenkins.getInstanceOrNull();
        if (instance == null) {
            // not started up yet, so all jobs
            return "null";
        }
        if (instance.getPlugin("favorite") == null) {
            // all jobs
            return "null";
        }
        User me = User.current();
        if (me == null) { 
            return "null";
        }
        Iterable<Item> items = Favorites.getFavorites(me);
        
        return JSONSerializer.toJSON(
            StreamSupport 
                .stream(items.spliterator(), false)
                .map(i -> i.getFullName())
                .collect(Collectors.toList())
        ).toString();
    }
}
