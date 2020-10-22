import {RecommendationCategoryModel} from "./recommendation-category.model";

export class RecommendationModel {
  constructor(
    public id: string,
    public dateStart: Date,
    public dateFinish: Date,
    public title: string,
    public textMessage: string,
    public category: RecommendationCategoryModel,
    public isActive: boolean
  ) {
  }
}
