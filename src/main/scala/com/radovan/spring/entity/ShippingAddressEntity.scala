package com.radovan.spring.entity

import javax.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, OneToOne, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "shipping_addresses")
@SerialVersionUID(1L)
class ShippingAddressEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var shippingAddressId:Integer = _

  @BeanProperty var address:String = _
  @BeanProperty var city:String = _
  @BeanProperty var state:String = _

  @Column(name = "zip_code")
  @BeanProperty var zipcode:String = _

  @BeanProperty var country:String = _

  @OneToOne(mappedBy = "shippingAddress")
  @BeanProperty var customer:CustomerEntity = _


}

