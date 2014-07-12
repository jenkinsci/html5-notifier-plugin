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

window.HTML5NotifierPlugin = window.HTML5NotifierPlugin || {};

window.HTML5NotifierPlugin.setup = function() {
	if (window.HTML5NotifierPlugin.enabled) {
		if (Notify.isSupported()) {
      if (Notify.needsPermission()) {
        var button = $("btn-html5-notifier-activate").show();
        button.observe('click', function() {
          Notify.requestPermission(function() {
            button.remove();
          });
        });
      }
      HTML5NotifierPlugin.run();
		} else {
			console.log("Notifications are not supported for this web browser.");
      window.HTML5NotifierPlugin.enabled = false;
		}
	}
};
window.HTML5NotifierPlugin.run = function () {
	if (window.HTML5NotifierPlugin.enabled) {
		new PeriodicalExecuter(function(pe) {
			if (Notify.isSupported()) {
				new Ajax.Request(window.HTML5NotifierPlugin.jenkinsrooturl +'/html5-notifier-plugin/list', {
					onSuccess : function(response) {
						$H(response.responseJSON).get('new').each(function(item) {
              var $item = $H(item);
              /*{
                name: "test #15",
                project: "test",
                result: "SUCCESS",
                result_icon: "http://url//images/16x16/blue.png",
                url: "http://url/job/test/15/"
              }*/
              var url = $item.get('url');
              var myNotification = new Notify($item.get('name'), {
                // icon: window.HTML5NotifierPlugin.resURL + '/images/jenkins.png',
                icon: $item.get('result_icon'),
                body: $item.get('result'),
                timeout: (window.HTML5NotifierPlugin.notificationTimeout/1000),
                notifyClick: function() {
                  setTimeout(function() {
                    window.focus(); 
                    location.href = url;
                  },0);
                }
              });

              myNotification.show();
            });
          }
        });
			}
		}, window.HTML5NotifierPlugin.queryTimeout);
	}
};
document.observe("dom:loaded", window.HTML5NotifierPlugin.setup);

0;
