package com.radovan.spring.service.impl

import java.util
import java.util.Optional

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ReviewMessageDto
import com.radovan.spring.entity.UserEntity
import com.radovan.spring.repository.{CustomerRepository, ReviewMessageRepository}
import com.radovan.spring.service.ReviewMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
@Transactional
class ReviewMessageServiceImpl extends ReviewMessageService {

  @Autowired
  private var reviewRepository:ReviewMessageRepository = _

  @Autowired
  private var tempConverter:TempConverter = _

  @Autowired
  private var customerRepository:CustomerRepository = _

  override def addReview(review: ReviewMessageDto): ReviewMessageDto = {
    val authUser = SecurityContextHolder.getContext.getAuthentication.getPrincipal.asInstanceOf[UserEntity]
    val customerEntity = customerRepository.findByUserId(authUser.getId)
    val reviewEntity = tempConverter.reviewMessageDtoToEntity(review)
    reviewEntity.setCustomer(customerEntity)
    val storedReview = reviewRepository.save(reviewEntity)
    val returnValue = tempConverter.reviewMessageEntityToDto(storedReview)
    returnValue
  }

  override def deleteReview(reviewId: Integer): Unit = {
    reviewRepository.deleteById(reviewId)
    reviewRepository.flush()
  }

  override def getReview(reviewId: Integer): ReviewMessageDto = {
    val reviewEntity = Optional.ofNullable(reviewRepository.getById(reviewId))
    var returnValue:ReviewMessageDto = null
    if (reviewEntity.isPresent) returnValue = tempConverter.reviewMessageEntityToDto(reviewEntity.get)
    returnValue
  }

  override def allReviews: util.List[ReviewMessageDto] = {
    val allReviews = Optional.ofNullable(reviewRepository.findAll)
    val returnValue = new util.ArrayList[ReviewMessageDto]
    if (!allReviews.isEmpty) {
      for (reviewEntity <- allReviews.get.asScala) {
        val reviewDto = tempConverter.reviewMessageEntityToDto(reviewEntity)
        returnValue.add(reviewDto)
      }
    }
    returnValue
  }

  override def deleteAllByCustomerId(customerId: Integer): Unit = {
    reviewRepository.deleteAllByCustomerId(customerId)
    reviewRepository.flush()
  }
}

