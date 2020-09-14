export class TemperatureModel {
  constructor(
    public temperature: number,
    public timeStart: Date,
    public duration: number,
    public temperatureMax: number,
    public temperatureMin: number
  ) {
  }

  displayTemp(scale: string): string {
    const temp = this.temperature;
    if (temp == null) { return 'NaN'; }
    let tempScaled: number;
    switch (scale) {
      case 'C': { tempScaled = temp - 273.15; break; }
      case 'F': { tempScaled = (temp - 273.15) * 9.0 / 5.0 + 32.0; break; }
      default: { tempScaled = temp; break; }
    }
    return tempScaled?.toFixed(1) || 'NaN';
  }

}
