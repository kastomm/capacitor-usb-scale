export type CallbackID = string;

export type ScaleStatus = "Fault"|"Zero"|"InMotion"|"Stable"|"UnderZero"|"OverWeight"|"NeedCalibration"|"NeedZeroing"|"Unknown";
export type USBDevice = { id: string, vid: number, pid: number, serial?: string, product: { manufacturer: string, name: string } };
export type ScaleRead = { data: number[], weight: number, status: ScaleStatus };

export interface USBScalePlugin {
  /**
   * Get a list of all connected compatible USB scale devices
   */
  enumerateDevices(): Promise<{ devices: USBDevice[] }>;

  /**
   * Request permission to access the USB scale device
   *
   * @param device The device to request permission for. If not specified, the first device will be used.
   */
  requestPermission(device?: string): Promise<{ status: boolean }>;

  /**
   * Open the USB scale device for data reading
   *
   * @param device The device to open. If not specified, the first device will be used.
   */
  open(device?: string): Promise<void>;

  /**
   * Close the USB scale device
   */
  stop(): Promise<void>;

  /**
   * Sets a callback to be called when the scale sends data.
   * If callback is not set, there will bi raised an `usb_scale_read` event.
   *
   * @param callback The callback to be called when the scale sends data.
   */
  setIncomingWeightDataCallback(callback: (data: ScaleRead) => void): Promise<CallbackID>;

  /**
   * Clears the callback set by `setIncomingWeightDataCallback`.
   */
  clearIncomingWeightDataCallback(): Promise<void>;
}
