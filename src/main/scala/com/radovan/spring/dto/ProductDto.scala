package com.radovan.spring.dto

import java.lang.Double
import java.util
import scala.beans.BeanProperty

@SerialVersionUID(1L)
class ProductDto extends Serializable {

  @BeanProperty var productId:Integer = _
  @BeanProperty var productCategory:String = _
  @BeanProperty var productDescription:String = _
  @BeanProperty var productStatus:String = _
  @BeanProperty var productBrand:String = _
  @BeanProperty var productModel:String = _
  @BeanProperty var productName:String = _
  @BeanProperty var productPrice:Double = _
  @BeanProperty var unitStock:Integer = _
  @BeanProperty var imageName:String = _
  @BeanProperty var discount:Double = _

  def getMainImagePath: String = {
    if (productId == null || imageName == null) return "/images/productImages/unknown.jpg"
    "/images/productImages/" + this.imageName
  }

  @BeanProperty var categoryList:util.List[String] = _

  categoryList = new util.ArrayList[String]
  categoryList.add("Laptop")
  categoryList.add("Mobile")
  categoryList.add("Camera")
  categoryList.add("TV")
  categoryList.add("Refrigerator")
  categoryList.add("Tablet")
  categoryList.add("Micro Oven")
  categoryList.add("DVD Player")
  categoryList.add("Fan")
  categoryList.add("Printer")
  categoryList.add("Desktop")
  categoryList.add("Washing Machine")
  categoryList.add("ipad")
  categoryList.add("Game console")
  categoryList.add("Router")

  override def toString: String = "ProductDto [productId=" + productId + ", productCategory=" + productCategory + ", productDescription=" + productDescription + ", productBrand=" + productBrand + ", productModel=" + productModel + ", productStatus=" + productStatus + ", productName=" + productName + ", productPrice=" + productPrice + ", unitStock=" + unitStock + ", imageName=" + imageName + ", discount=" + discount + ", categoryList=" + categoryList + "]"
}
