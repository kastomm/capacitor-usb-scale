import { WebPlugin } from '@capacitor/core';

import type { USBScalePlugin } from './definitions';

export class USBScaleWeb extends WebPlugin implements USBScalePlugin {
  enumerateDevices(): Promise<{ devices: { id: string; vid: number; pid: number; serial?: string; product: { manufacturer: string; name: string } }[] }> {
    throw this.unimplemented('Not implemented on web.');
  }

  open(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  requestPermission(): Promise<{ status: boolean }> {
    throw this.unimplemented('Not implemented on web.');
  }

  stop(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
}
