import type { PluginListenerHandle } from '@capacitor/core';

export type CallbackID = string;

export interface EnumerateDevicesResponse {
  devices: USBDevice[];
}

export interface RequestPermissionOptions {
  /**
   * The device to request permission for. If not specified, the first device will be used.
   */
  device_id: string;
}

export interface HasPermissionOptions {
  /**
   * The device to check permission for. If not specified, the first device will be used.
   */
  device_id: string;
}

export interface HasPermissionResponse {
  /**
   * Whether the app has permission to access the USB scale device
   */
  permission: boolean;
}

export interface OpenOptions {
  /**
   * The device to open. If not specified, the first device will be used.
   */
  device_id: string;
}

export interface OnReadEvent {
  data: number[];
  weight: number;
  status: ScaleStatus;
}

export interface OnScaleConnectedEvent {
  device: USBDevice;
}

export interface OnScaleDisconnectedEvent {
  device: USBDevice;
}

export interface USBDevice {
  id: string;
  vid: number;
  pid: number;
  serial?: string;
  product: { manufacturer: string, name: string };
}

export enum ScaleStatus {
  Fault = "fault",
  Zero = "zero",
  InMotion = "in-motion",
  Stable = "stable",
  UnderZero = "under-zero",
  OverWeight = "over-weight",
  NeedCalibration = "need-calibration",
  NeedZeroing = "need-zeroing",
  Unknown = "unknown",
}

export interface USBScalePlugin {
  /**
   * Get a list of all connected compatible USB scale devices
   */
  enumerateDevices(): Promise<EnumerateDevicesResponse>;

  /**
   * Request permission to access the USB scale device
   *
   * Throws an error if permission is denied
   */
  requestPermission(options?: RequestPermissionOptions): Promise<void>;

  /**
   * Check if app has permission to access the USB scale device
   */
  hasPermission(options?: HasPermissionOptions): Promise<HasPermissionResponse>;

  /**
   * Open the USB scale device for data reading
   *
   * Throws an error if the device is not connected or permission is denied
   */
  open(options?: OpenOptions): Promise<void>;

  /**
   * Close the USB scale device
   */
  close(): Promise<void>;

  /**
   * Event emitted when the scale sends data
   */
  addListener(
      eventName: 'onRead',
      listenerFunc: (event: OnReadEvent) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Event emitted when a compatible USB scale device is connected.
   */
  addListener(
      eventName: 'onScaleConnected',
      listenerFunc: (event: OnScaleConnectedEvent) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Event emitted when a compatible USB scale device is disconnected.
   */
  addListener(
      eventName: 'onScaleDisconnected',
      listenerFunc: (event: OnScaleDisconnectedEvent) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Removes all listeners
   */
  removeAllListeners(): Promise<void>;
}
