package com.radovan.spring.dto

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class ShippingAddressDto extends Serializable {

  @BeanProperty var shippingAddressId:Integer = _
  @BeanProperty var address:String = _
  @BeanProperty var city:String = _
  @BeanProperty var state:String = _
  @BeanProperty var zipcode:String = _
  @BeanProperty var country:String = _
  @BeanProperty var customerId:Integer = _
}