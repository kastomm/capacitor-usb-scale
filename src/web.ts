import { WebPlugin } from '@capacitor/core';

import type {
  EnumerateDevicesResponse,
  USBScalePlugin
} from './definitions';

export class USBScaleWeb extends WebPlugin implements USBScalePlugin {
  enumerateDevices(): Promise<EnumerateDevicesResponse> {
    throw this.unimplemented('Not implemented on web.');
  }

  open(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  requestPermission(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  close(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
}
