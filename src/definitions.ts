export type CallbackID = string;

export type ScaleStatus = "Fault"|"Zero"|"InMotion"|"Stable"|"UnderZero"|"OverWeight"|"NeedCalibration"|"NeedZeroing"|"Unknown";
export type USBDevice = { id: string, vid: number, pid: number, serial?: string, product: { manufacturer: string, name: string } };
export type ScaleRead = { data: number[], weight: number, status: ScaleStatus };

export interface USBScalePlugin {
  enumerateDevices(): Promise<{ devices: USBDevice[] }>;

  requestPermission(device?: string): Promise<{ status: boolean }>;

  open(device?: string): Promise<void>;

  stop(): Promise<void>;

  setIncomingWeightDataCallback(callback: (data: ScaleRead) => void): Promise<CallbackID>;
  clearIncomingWeightDataCallback(): Promise<void>;
}
