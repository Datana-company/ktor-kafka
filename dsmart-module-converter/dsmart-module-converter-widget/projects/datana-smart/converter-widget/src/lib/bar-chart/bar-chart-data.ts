export class BarChartData {
    constructor(
        public slagRate: number,
        public steelRate: number
    ) {
    }

    getValue(): any[] {
        return [
            {
                "name": "Шлак",
                "value": 60//this.slagRate
            },
            {
                "name": "Допустимая доля металла",
                "value": 20
            },
            {
                "name": "Металл",
                "value": 40//this.steelRate
            }
        ];
    }
}
