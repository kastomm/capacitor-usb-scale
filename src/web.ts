import { WebPlugin } from '@capacitor/core';

import type { USBScalePlugin } from './definitions';

export class USBScaleWeb extends WebPlugin implements USBScalePlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
