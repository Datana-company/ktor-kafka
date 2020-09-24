export class AnalysisModel {
  constructor(
    public boilTime: Date,
    public state: AnalysisStateModel
  ) {
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
