import {RecommendationCategoryModel} from "./recommendation-category.model";

export class RecommendationModel {
  constructor(
    public date: Date,
    public category: RecommendationCategoryModel,
    public textMessage: string
  ) {
  }
}
