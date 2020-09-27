export class TemperatureModel {
  constructor(
    public temperature: number,
    public timeBackend: Date,
    public timeStart: Date,
    public duration: number,
    public temperatureMax: number,
    public temperatureMin: number
  ) {
  }
}
