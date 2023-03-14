package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.ReviewMessageDto

trait ReviewMessageService {

  def addReview(review: ReviewMessageDto): ReviewMessageDto

  def getReview(reviewId: Integer): ReviewMessageDto

  def allReviews: util.List[ReviewMessageDto]

  def deleteReview(reviewId: Integer): Unit

  def deleteAllByCustomerId(customerId: Integer): Unit
}
