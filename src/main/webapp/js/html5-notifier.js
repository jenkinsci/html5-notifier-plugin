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

function isNotificationsEnabled() {
	return window.webkitNotifications;
}

function isNotificationsPermitted() {
	return isNotificationsEnabled()
			&& window.webkitNotifications.checkPermission() == 0;
}

function setup() {
	if (enabled) {
		if (isNotificationsEnabled()) {
			window.webkitNotifications.requestPermission();
		} else {
			console
					.log("Notifications are not supported for this web browser.");
		}
	}
}

function run() {
	if (enabled) {
		new PeriodicalExecuter(function(pe) {
			if (isNotificationsPermitted()) {
				new Ajax.Request('/html5-notifier-plugin/list', {
					onSuccess : function(response) {
						$H(response.responseJSON).get('new').each(
								function(item) {
									notification = window.webkitNotifications
											.createHTMLNotification($H(item)
													.get('url'));
									notification.ondisplay = new function() {
										window.setTimeout(function() {
											notification.cancel();
										}, notificationTimeout);
									}
									notification.show();
								});
					}
				});
			}
		}, queryTimeout);
	}
}

setup();
run();

0;
