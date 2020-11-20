export class ConverterFrameModel {
  constructor(
    public frameId: string,
    public frameTime: number,
    public framePath: string,
    public image: string,
    public channel: string
  ) {
  }
}
