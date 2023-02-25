import type { USBDevice, ScaleRead } from './definitions';

interface Window {
  addEventListener(type: "usb_scale_read", listener: (ev: ScaleRead) => any, useCapture?: boolean): void;
  addEventListener(type: "usb_scale_disconnected", listener: (ev: { device: USBDevice }) => any, useCapture?: boolean): void;
  addEventListener(type: "usb_scale_connected", listener: (ev: { device: USBDevice }) => any, useCapture?: boolean): void;
}