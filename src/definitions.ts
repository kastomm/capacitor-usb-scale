export interface USBScalePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
