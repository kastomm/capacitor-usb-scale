export interface USBScalePlugin {
  enumerateDevices(): Promise<{ devices: { id: string, vid: number, pid: number, serial?: string, product: { manufacturer: string, name: string } }[] }>;

  requestPermission(device?: string): Promise<{ status: boolean }>;
  open(device?: string): Promise<void>;
  stop(): Promise<void>;
}
