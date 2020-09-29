export class TemperatureModel {
  constructor(
    public temperature: number,
    public timeBackend: Date,
    public timeLatest: Date,
    public timeEarliest: Date,
    public duration: number,
    public temperatureMax: number,
    public temperatureMin: number
  ) {
  }
}
