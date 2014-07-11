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

import hudson.model.HealthReport;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

import java.util.Date;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
/**
 * Encapsulation for the actual "web page" HTML which the HTML5 notification API
 * will render, based on a specific {@link hudson.model.Run}.
 * 
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 */
public final class RunNotification extends RunListener<Run<?, ?>> implements
        Comparable<RunNotification> {
    private static int      IDX = 1;

    private final Date      date;

    private final int       idx;

    private final Run<?, ?> run;

    private final boolean   differentResult;

    public RunNotification(final Run<?, ?> run) {
        super();
        date = new Date();
        this.run = run;
        differentResult = RunResultUtils.isDifferentResult(run);
        idx = IDX++;
    }

    public int compareTo(final RunNotification other) {
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

        if (!(obj instanceof RunNotification)) {
            return false;
        }

        final RunNotification other = (RunNotification) obj;
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

    public boolean isDifferentResult() {
        return differentResult;
    }

    public JSONObject toJSONObject() {
        final String rootUrl = Jenkins.getInstance().getRootUrlFromRequest();
        final Result result = run.getResult();
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("project", run.getParent().getName());
        jsonObject.put("result", run.getResult().toString());
        jsonObject.put("result_icon", rootUrl + "/images/16x16/" + result.color.getImage());
        jsonObject.put("url", rootUrl + run.getUrl());
        jsonObject.put("name", run.getFullDisplayName());

        return jsonObject;
    }

    /*
     * I wish there was a better way to do this... like, internal jelly
     * interpolator
     */
    public String toHtmlString() {
        final String rootUrl = Jenkins.getInstance().getRootUrlFromRequest();
        final Result result = run.getResult();
        final HealthReport buildHealth = run.getParent().getBuildHealth();

        return new StringBuilder()
                .append("<html>")
                .append("<head>")
                .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
                .append(rootUrl)
                .append("/plugin/html5-notifier-plugin/css/html5-notifier.css\" />")
                .append("</head>")
                .append("<body>")
                .append("<div class=\"notification\">")

                // the entire "page" is a link to the build result page
                .append("<a target=\"_blank\" href=\"")
                .append(rootUrl)
                .append(run.getUrl())
                .append("\">")

                // build result ball
                .append("<img alt=\"")
                .append(result.toString())
                .append("\" title=\"")
                .append(result.toString())
                .append("\" src=\"")
                // TODO: use user's size prefernce
                .append(rootUrl).append("/images/16x16/")
                .append(result.color.getImage())
                .append("\"/>")

                // build health
                .append(" ").append("<img alt=\"")
                .append(buildHealth.getDescription()).append("\" title=\"")
                .append(buildHealth.getDescription()).append("\" src=\"")
                .append(rootUrl).append("/images/16x16/")
                .append(buildHealth.getIconUrl()).append("\"/>")

                // job name # build number
                .append(" ").append(run.getFullDisplayName())

                // result status
                .append(" - ").append(run.getResult())

                // all done
                .append("</a>").append("</div>").append("</body>")
                .append("</html>").toString();
    }
}
