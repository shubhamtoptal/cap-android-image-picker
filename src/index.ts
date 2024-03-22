import { registerPlugin } from '@capacitor/core';

import type { AndroidImagePickerPlugin } from './definitions';

const AndroidImagePicker = registerPlugin<AndroidImagePickerPlugin>(
  'AndroidImagePicker',
  {
    web: () => import('./web').then(m => new m.AndroidImagePickerWeb()),
  },
);

export * from './definitions';
export { AndroidImagePicker };
