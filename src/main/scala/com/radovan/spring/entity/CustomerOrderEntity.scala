package com.radovan.spring.entity

import java.util
import javax.persistence.{CascadeType, Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, OneToMany, OneToOne, Table}
import org.hibernate.annotations.{Fetch, FetchMode}

import scala.beans.BeanProperty

@Entity
@Table(name = "customer_orders")
@SerialVersionUID(1L)
class CustomerOrderEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var customerOrderId:Integer = _

  @OneToOne
  @JoinColumn(name = "cart_id")
  @BeanProperty var cart:CartEntity = _

  @OneToOne
  @JoinColumn(name = "customer_id")
  @BeanProperty var customer:CustomerEntity = _

  @OneToMany(mappedBy = "order", cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  @BeanProperty var orderedItems:util.List[OrderItemEntity] = _

  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER)
  @JoinColumn(name = "address_id")
  @BeanProperty var address:OrderAddressEntity = _


}

