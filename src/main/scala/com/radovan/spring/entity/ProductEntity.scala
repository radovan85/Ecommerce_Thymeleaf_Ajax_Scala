package com.radovan.spring.entity

import java.lang.Double
import java.util

import javax.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, Table, Transient}

import scala.beans.BeanProperty

@Entity
@Table(name = "products")
@SerialVersionUID(1L)
class ProductEntity extends Serializable {

  @Id
  @Column(name = "Id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty var productId:Integer = _

  @Column(name = "category")
  @BeanProperty var productCategory:String = _

  @Column(name = "description")
  @BeanProperty var productDescription:String = _

  @Column(name = "product_status")
  @BeanProperty var productStatus:String = _

  @Column(name = "product_brand")
  @BeanProperty var productBrand:String = _

  @Column(name = "product_model")
  @BeanProperty var productModel:String = _

  @Column(name = "product_name")
  @BeanProperty var productName:String = _

  @Column(name = "price")
  @BeanProperty var productPrice:Double = _

  @Column(name = "unit")
  @BeanProperty var unitStock:Integer = _

  @Column(name = "image_name")
  @BeanProperty var imageName:String = _

  @BeanProperty var discount:Double = _

  @Transient
  def getMainImagePath: String = {
    if (productId == null || imageName == null) return "/images/productImages/unknown.jpg"
    "/images/productImages/" + this.imageName
  }

  @Transient
  @BeanProperty var categoryList:util.List[String] = _


}

