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

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

import java.util.Date;

import jenkins.model.Jenkins;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 */
public final class Html5NotifierRunNotification extends RunListener<Run<?, ?>>
        implements Comparable<Html5NotifierRunNotification> {
    private static int IDX = 1;

    protected static String toImgHtmlString(final Result result) {
        if (result == null) {
            return "";
        }

        return new StringBuilder().append("<img alt=\"")
                .append(result.toString()).append("\"").append(" src=\"")
                .append("/images/16x16/").append(result.color.getImage())
                .append("\"/>").toString();
    }

    private final Date      date;

    private final int       idx;

    private final Run<?, ?> run;

    public Html5NotifierRunNotification(final Run<?, ?> run) {
        super();
        date = new Date();
        this.run = run;
        idx = IDX++;
    }

    public int compareTo(final Html5NotifierRunNotification other) {
        if (this == other) {
            return 0;
        }

        return date.compareTo(other.getDate());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Html5NotifierRunNotification)) {
            return false;
        }

        final Html5NotifierRunNotification other = (Html5NotifierRunNotification) obj;
        return date.equals(other.getDate()) && run.equals(other.getRun());
    }

    public Date getDate() {
        return date;
    }

    public int getIdx() {
        return idx;
    }

    public Run<?, ?> getRun() {
        return run;
    }

    @Override
    public int hashCode() {
        return date.hashCode() * run.hashCode();
    }

    public String toHtmlString() {
        return new StringBuilder().append("<div>")
                .append("<a target=\"_blank\" href=\"")
                .append(Jenkins.getInstance().getRootUrl())
                .append(run.getUrl()).append("\">")
                .append(toImgHtmlString(run.getResult()))
                .append(run.getResult()).append(" -- ")
                .append(run.getFullDisplayName()).append("</a>")
                .append("</div>").toString();
    }
}
