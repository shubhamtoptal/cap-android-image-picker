export interface AndroidImagePickerPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
