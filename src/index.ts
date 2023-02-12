import { registerPlugin } from '@capacitor/core';

import type { USBScalePlugin } from './definitions';

const USBScale = registerPlugin<USBScalePlugin>('USBScale', {
  web: () => import('./web').then(m => new m.USBScaleWeb()),
});

export * from './definitions';
export { USBScale };
