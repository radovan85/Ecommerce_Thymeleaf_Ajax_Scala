package com.radovan.spring.entity

import java.sql.Timestamp

import javax.persistence.{CascadeType, Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, OneToOne, Table}
import org.hibernate.annotations.CreationTimestamp

import scala.beans.BeanProperty

@Entity
@Table(name = "reviews")
@SerialVersionUID(1L)
class ReviewMessageEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "review_id")
  @BeanProperty var reviewMessageId:Integer = _

  @BeanProperty var text:String = _

  @CreationTimestamp
  @BeanProperty var date:Timestamp = _

  @OneToOne(cascade = Array(CascadeType.MERGE), fetch = FetchType.EAGER)
  @JoinColumn(name = "customer_id")
  @BeanProperty var customer:CustomerEntity = _


}

