package com.radovan.spring.dto

import java.sql.Timestamp

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class ReviewMessageDto extends Serializable {

  @BeanProperty var reviewMessageId:Integer = _
  @BeanProperty var text:String = _
  @BeanProperty var date:Timestamp = _
  @BeanProperty var customerId:Integer = _
}
