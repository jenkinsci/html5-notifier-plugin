/*
 * The MIT License
 * 
 * Copyright (c) 2011, Jesse Farinacci
 * Copyright (c) 2014, Gavin Mogan
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

import Notify from 'notifyjs';
import SSE from '@jenkins-cd/sse-gateway';

const STATUSES = {
  // see https://github.com/jenkinsci/jenkins/blob/811583eb6007ffa22f81c312d983816e5f36a670/core/src/main/java/hudson/model/Result.java
  'SUCCESS': `${document.head.dataset.resurl}/images/48x48/blue.png`,
  'UNSTABLE': `${document.head.dataset.resurl}/images/48x48/yellow.png`,  
  'FAILURE': `${document.head.dataset.resurl}/images/48x48/red.png`,
  'NOT_BUILT': `${document.head.dataset.resurl}/images/48x48/nobuilt.png`,
  'ABORTED': `${document.head.dataset.resurl}/images/48x48/aborted.png`,
};

class HTML5NotifierPlugin {
  setup() {
    if (!window.HTML5NotifierPlugin.enabled) { return; }
    if (Notify.needsPermission) {
      const button = document.getElementById('btn-html5-notifier-activate');
      button.style.display = '';
      button.addEventListener('click', () => {
        const onDone = () => {
          button.parentElement.removeChild(button);
          this.run();
        };
        if (Notify.isSupported()) {
          Notify.requestPermission(onDone, onDone);
        } else {
          window.HTML5NotifierPlugin.enabled = false;
        }
      });
    } else {
      this.run();
    }
  }
  run() {
    if (!window.HTML5NotifierPlugin.enabled) { return; }
    const connection = SSE.connect('html5-notifier');

    // Connection error handling...
    // Taken directly from sse-gateway docs
    connection.onError(() => {
      // Check the connection...
      connection.waitConnectionOk((status) => {
        if (status.connectError) {
          // The last attempt to connect was a failure, so
          // notify the user in some way....
          
        } else if (status.connectErrorCount > 0) {
          // The last attempt to connect was not a failure,
          // but we had earlier failures, so undo
          // earlier error notifications etc ...
          
          // And perhaps reload the current page, forcing
          // a login if needed....
          setTimeout(() => {
            window.location.reload(true);
          }, 2000);
        }
      });
    });

    connection.subscribe('job', (event) => {
      // if (!Notify.isSupported()) { return; }
      if (event.jenkins_event === 'job_run_ended') {
        const url = `${event.jenkins_instance_url}${event.jenkins_object_url}`;
        const myNotification = new Notify(event.job_name, {
          // icon: window.HTML5NotifierPlugin.resURL + '/images/jenkins.png',
          icon: STATUSES[event.job_run_status],
          body: event.job_run_status,
          timeout: (window.HTML5NotifierPlugin.notificationTimeout/1000),
          notifyClick() {
            window.focus(); 
            location.href = url;
          }
        });
        if (!Notify.needsPermission) {
          myNotification.show();
        }
      }
    });
  }
}

document.observe('dom:loaded', () => {
  (new HTML5NotifierPlugin()).setup();
});
