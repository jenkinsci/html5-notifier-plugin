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

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 */
public final class RunResultUtils {
    public static boolean isDifferentResult(final Result r1, final Result r2) {
        if (r1 == null) {
            return true;
        }

        if (r2 == null) {
            return true;
        }

        return r1.isBetterThan(r2) || r1.isWorseThan(r2);
    }

    public static boolean isDifferentResult(final Run<?, ?> r1) {
        if (r1 == null) {
            return true;
        }

        return isDifferentResult(r1, r1.getPreviousBuild());
    }

    public static boolean isDifferentResult(final Run<?, ?> r1,
            final Run<?, ?> r2) {
        if (r1 == null) {
            return true;
        }

        if (r2 == null) {
            return true;
        }

        return isDifferentResult(r1.getResult(), r2.getResult());
    }

    /**
     * Static-only access.
     */
    private RunResultUtils() {
        // static-only access
    }
}
