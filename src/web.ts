import { WebPlugin } from '@capacitor/core';

import type { AndroidImagePickerPlugin } from './definitions';

export class AndroidImagePickerWeb
  extends WebPlugin
  implements AndroidImagePickerPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
