// import { createDateFormatter } from 'intl-dateformat';
//
// const formatDate = createDateFormatter({
//   // numeric hour
//   h: ({ hour }) => hour[0] === '0' ? hour[1] : hour,
//   // milliseconds
//   SSS: (parts, date) => String(date.getTime()).slice(-3)
// });

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

  displayTime(): string {
    // return formatDate(this.timeStart, 'DD.MM.YYYY HH:mm:ss.SSS МСК', { timezone: 'Europe/Moscow' });
    return this.timeStart.toTimeString();
  }
}
