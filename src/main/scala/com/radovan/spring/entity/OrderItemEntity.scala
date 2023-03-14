package com.radovan.spring.entity

import java.lang.Double

import javax.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, Table}

import scala.beans.BeanProperty

@Entity
@Table(name = "order_items")
@SerialVersionUID(1L)
class OrderItemEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var orderItemId:Integer = _

  @BeanProperty var quantity:Integer = _
  @BeanProperty var price:Double = _

  @Column(name = "product_name")
  @BeanProperty var productName:String = _

  @Column(name = "product_price")
  @BeanProperty var productPrice:Double = _

  @BeanProperty var discount:Double = _

  @ManyToOne
  @JoinColumn(name = "order_id")
  @BeanProperty var order:CustomerOrderEntity = _


}

