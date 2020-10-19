import {MockListItemModel} from "./mock-list-item-model";

export class MockCasesModel {
  constructor(public cases: Array<MockListItemModel>) {
    this.cases = cases;
  }
}
