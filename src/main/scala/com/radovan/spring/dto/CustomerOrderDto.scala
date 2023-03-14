package com.radovan.spring.dto

import java.util
import scala.beans.BeanProperty

@SerialVersionUID(1L)
class CustomerOrderDto extends Serializable {

  @BeanProperty var customerOrderId:Integer = _
  @BeanProperty var cartId:Integer = _
  @BeanProperty var customerId:Integer = _
  @BeanProperty var orderedItemsIds:util.List[Integer] = _
  @BeanProperty var addressId:Integer = _
}
