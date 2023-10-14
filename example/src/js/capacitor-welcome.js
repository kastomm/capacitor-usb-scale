import { SplashScreen } from '@capacitor/splash-screen';
import { USBScale } from '@kduma-autoid/capacitor-usb-scale';
import { WebViewWatchDog } from "@kduma-autoid/capacitor-webview-watchdog";

import { ScaleStatus } from "../../../src";
import {App} from "@capacitor/app";

window.customElements.define(
  'capacitor-welcome',
  class extends HTMLElement {
    constructor() {
      super();

      SplashScreen.hide();
      WebViewWatchDog.ping();

      const root = this.attachShadow({ mode: 'open' });

      root.innerHTML = `
    <style>
      :host {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        display: block;
        width: 100%;
        height: 100%;
      }
      h1, h2, h3, h4, h5 {
        text-transform: uppercase;
      }
      .button {
        display: inline-block;
        padding: 10px;
        background-color: #73B5F6;
        color: #fff;
        font-size: 0.9em;
        border: 0;
        border-radius: 3px;
        text-decoration: none;
        cursor: pointer;
      }
      main {
        padding: 15px;
      }
      main hr { height: 1px; background-color: #eee; border: 0; }
      main h1 {
        font-size: 1.4em;
        text-transform: uppercase;
        letter-spacing: 1px;
      }
      main h2 {
        font-size: 1.1em;
      }
      main h3 {
        font-size: 0.9em;
      }
      main p {
        color: #333;
      }
      main pre {
        white-space: pre-line;
      }
    </style>
    <div>
      <capacitor-welcome-titlebar>
        <h1>@kduma-autoid/capacitor-usb-scale</h1>
      </capacitor-welcome-titlebar>
      <main>
        <h1>Weight: <span id="weight">- g</span></h1>
        <p>
          <button class="button" id="enumerate">enumerateDevices()</button>
          <button class="button" id="request">requestPermission()</button>
          <button class="button" id="open">open()</button>
          <button class="button" id="stop">stop()</button>
        </p>
        <h2>Demo Events</h2>
        <p id="output"></p>
      </main>
    </div>
    `;
    }

    connectedCallback() {
      const self = this;

      self.shadowRoot.querySelector('#enumerate').addEventListener('click', async function (e) {
        const output = self.shadowRoot.querySelector('#output');

        const devices = await USBScale.enumerateDevices();

        output.innerHTML = "<b>enumerateDevices():</b><br><pre><code>" + JSON.stringify(devices, null, 3) + "</code></pre><hr>" + output.innerHTML;
      });

      self.shadowRoot.querySelector('#request').addEventListener('click', async function (e) {
        const output = self.shadowRoot.querySelector('#output');

        try {
          const request = await USBScale.requestPermission();
          output.innerHTML = "<b>requestPermission():</b><br><pre><code>" + JSON.stringify(request, null, 3) + "</code></pre><hr>" + output.innerHTML;
        } catch (err) {
          output.innerHTML = "<b>requestPermission() - EXCEPTION!:</b><br><pre><code>" + err.message + "</code></pre><hr>" + output.innerHTML;
        }
      });

      self.shadowRoot.querySelector('#open').addEventListener('click', async function (e) {
        const output = self.shadowRoot.querySelector('#output');
        self.shadowRoot.querySelector('#weight').innerHTML = "- g";

        try {
          const request = await USBScale.open();
          output.innerHTML = "<b>open():</b><br><pre><code>" + JSON.stringify(request, null, 3) + "</code></pre><hr>" + output.innerHTML;
        } catch (err) {
          output.innerHTML = "<b>open() - EXCEPTION!:</b><br><pre><code>" + err.message + "</code></pre><hr>" + output.innerHTML;
        }
      });

      self.shadowRoot.querySelector('#stop').addEventListener('click', async function (e) {
        const output = self.shadowRoot.querySelector('#output');
        self.shadowRoot.querySelector('#weight').innerHTML = "- g";

        try {
          const request = await USBScale.stop();
          output.innerHTML = "<b>stop():</b><br><pre><code>" + JSON.stringify(request, null, 3) + "</code></pre><hr>" + output.innerHTML;
        } catch (err) {
          output.innerHTML = "<b>stop() - EXCEPTION!:</b><br><pre><code>" + err.message + "</code></pre><hr>" + output.innerHTML;
        }
      });

      USBScale.addListener('onRead', function(e) {
        const output = self.shadowRoot.querySelector('#output');
        output.innerHTML = '<b>onRead:</b><br><pre>' + JSON.stringify(e, null, 3) + '</pre><hr>' + output.innerHTML;

        if (e.status !== ScaleStatus.Zero && e.status !== ScaleStatus.InMotion && e.status !== ScaleStatus.Stable) {
          self.shadowRoot.querySelector('#weight').innerHTML = '~ g';
        } else if (e.weight < 1000) {
          self.shadowRoot.querySelector('#weight').innerHTML = e.weight + ' g';
        } else {
          self.shadowRoot.querySelector('#weight').innerHTML = e.weight / 1000 + ' kg';
        }
      });

      USBScale.addListener('onScaleDisconnected', function(e) {
        const output = self.shadowRoot.querySelector('#output');
        output.innerHTML = "<b>onScaleDisconnected:</b><br><pre>" + JSON.stringify(e, null, 3) + "</pre><hr>" + output.innerHTML;

        self.shadowRoot.querySelector('#weight').innerHTML = "- g";
      });

      USBScale.addListener('onScaleConnected', async function (e) {
        const output = self.shadowRoot.querySelector('#output');
        output.innerHTML = "<b>onScaleConnected:</b><br><pre>" + JSON.stringify(e, null, 3) + "</pre><hr>" + output.innerHTML;


        try {
          const request = await USBScale.open();
          output.innerHTML = "<b>onScaleConnected -> open():</b><br><pre><code>" + JSON.stringify(request, null, 3) + "</code></pre><hr>" + output.innerHTML;
        } catch (err) {
          output.innerHTML = "<b>onScaleConnected -> open() - EXCEPTION!:</b><br><pre><code>" + err.message + "</code></pre><hr>" + output.innerHTML;
          let listener = App.addListener('resume', async () => {
            try {
              const request = await USBScale.open();
              output.innerHTML = "<b>onScaleConnected -> resume -> open():</b><br><pre><code>" + JSON.stringify(request, null, 3) + "</code></pre><hr>" + output.innerHTML;
            } catch (err) {
              output.innerHTML = "<b>onScaleConnected -> resume -> open() - EXCEPTION!:</b><br><pre><code>" + err.message + "</code></pre><hr>" + output.innerHTML;
            }
            await listener.remove();
          });
        }
      });
    }
  }
);

window.customElements.define(
  'capacitor-welcome-titlebar',
  class extends HTMLElement {
    constructor() {
      super();
      const root = this.attachShadow({ mode: 'open' });
      root.innerHTML = `
    <style>
      :host {
        position: relative;
        display: block;
        padding: 15px 15px 15px 15px;
        text-align: center;
        background-color: #73B5F6;
      }
      ::slotted(h1) {
        margin: 0;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        font-size: 0.9em;
        font-weight: 600;
        color: #fff;
      }
    </style>
    <slot></slot>
    `;
    }
  }
);
