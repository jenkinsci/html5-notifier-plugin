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
import hudson.model.RootAction;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 */
@Extension
public final class RootActionImpl implements RootAction {
    protected static final String ATTRIBUTE_RUN_ID   = "run";

    protected static final String SESSION_LAST_QUERY = "lastQuery";

    protected static final String URL_NAME           = "/html5-notifier-plugin";

    protected static Date getAndUpdateLastQueryDate(final StaplerRequest request) {
        if (request == null) {
            return new Date();
        }

        final HttpSession session = request.getSession();
        if (session == null) {
            return new Date();
        }

        final Date now = new Date();
        final Date lastQuery = validateNotNull(
                (Date) session.getAttribute(SESSION_LAST_QUERY), now);
        session.setAttribute(SESSION_LAST_QUERY, now);
        return lastQuery;
    }

    protected static JSONArray getHtml5NotifierRunNotificationJSONArray(
            final Date date) {
        final JSONArray array = new JSONArray();

        for (final RunNotification notification : RunListenerImpl
                .getAllFutureRunNotifications(date)) {
            array.add(toJSONObject(notification));
        }

        return array;
    }

    protected static JSONObject toJSONObject(final RunNotification notification) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", URL_NAME + "/query?" + ATTRIBUTE_RUN_ID + "="
                + notification.getIdx());
        return jsonObject;
    }

    protected static Date validateNotNull(final Date a, final Date b) {
        if (a != null) {
            return a;
        }

        return b;
    }

    protected static void writeResponse(final PrintWriter writer,
            final String string) throws ServletException, IOException {
        if (writer != null) {
            writer.append(string);
            writer.close();
            writer.flush();
        }
    }

    protected static void writeResponse(final StaplerResponse response,
            final RunNotification notification) throws ServletException,
            IOException {
        if (notification != null) {
            writeResponse(response, notification.toHtmlString());
        }
    }

    protected static void writeResponse(final StaplerResponse response,
            final String string) throws ServletException, IOException {
        if (response != null) {
            writeResponse(response.getWriter(), string);
        }
    }

    protected static void writeResponse(final Writer writer, final String string)
            throws ServletException, IOException {
        if (writer != null) {
            if (writer instanceof PrintWriter) {
                writeResponse((PrintWriter) writer, string);
            }

            else {
                writeResponse(new PrintWriter(writer), string);
            }
        }
    }

    public void doList(final StaplerRequest request,
            final StaplerResponse response) throws ServletException,
            IOException {
        final JSONObject result = new JSONObject();
        result.put(
                "new",
                getHtml5NotifierRunNotificationJSONArray(getAndUpdateLastQueryDate(request)));
        response.setContentType("application/json");
        writeResponse(response, result.toString());
    }

    public void doQuery(final StaplerRequest request,
            final StaplerResponse response) throws ServletException,
            IOException {
        final String run = request.getParameter(ATTRIBUTE_RUN_ID);
        if (!StringUtils.isEmpty(run)) {
            try {
                writeResponse(response,
                        RunListenerImpl.getRunNotificationByIdx(Integer
                                .valueOf(run)));
            }

            catch (final NumberFormatException nfe) {
                /* do nothing */
            }
        }
    }

    public String getDisplayName() {
        /* intentionally null */
        return null;
    }

    public String getIconFileName() {
        /* intentionally null */
        return null;
    }

    public String getUrlName() {
        return URL_NAME;
    }
}
