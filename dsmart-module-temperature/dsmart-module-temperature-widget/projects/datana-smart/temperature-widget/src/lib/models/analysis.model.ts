export class AnalysisModel {
  constructor(
    public backendTime: Date,
    public actualTime: Date,
    public durationToBoil: number,
    public sensorId: string,
    public temperatureLast: number,
    public state: AnalysisStateModel
  ) {
  }

  get durationToBoilStr(): string {
    const mins = Math.floor(this.durationToBoil / 60000.0);
    const secs = Math.floor( this.durationToBoil / 1000.0 ) - mins * 60;
    return `${mins}m ${secs}s`;
  }
}

export class AnalysisStateModel {
  constructor(
    public id: string,
    public name: string,
    public message: string
  ) {
  }
}
