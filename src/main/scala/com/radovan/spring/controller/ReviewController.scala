package com.radovan.spring.controller

import com.radovan.spring.dto.ReviewMessageDto
import com.radovan.spring.service.ReviewMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMapping, RequestMethod}

@Controller
@RequestMapping(value = Array("/review"))
class ReviewController {

  @Autowired
  private var reviewService:ReviewMessageService = _

  @RequestMapping(value = Array("/sendReview"), method = Array(RequestMethod.GET))
  def renderReviewForm(map: ModelMap): String = {
    val review = new ReviewMessageDto
    map.put("review", review)
    "fragments/reviewForm :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/sendReview"), method = Array(RequestMethod.POST))
  def sendReview(@ModelAttribute("review") review: ReviewMessageDto): String = {
    reviewService.addReview(review)
    "fragments/homePage :: ajaxLoadedContent"
  }

  @RequestMapping(value = Array("/reviewSent"), method = Array(RequestMethod.GET))
  def reviewResult = "fragments/reviewSent :: ajaxLoadedContent"
}

