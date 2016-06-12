/*
 * Copyright 2016 Jon Humble
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.me.jon_humble.cart

/**
 * Public interface of the shopping cart.
 * To get an instance, use Cart.EmptyCart
 * from the companion object.
 */
sealed trait Cart {
  /**
   * Returns a new cart with one instance of an SKU added.
   */
  def add(sku: SKU): Cart

  /**
   * Returns a new cart with one instance of an SKU removed, if there
   * was one, otherwise the cart unchanged.
   */
  def remove(sku: SKU): Cart

  /**
   * Returns a new cart with the same contents but using the pricing rules in the
   * file 'prices'. File must be on the classpath.
   */
  def using(prices: PriceFileLocation): Cart

  /**
   * Calculates the total of the SKUs in the cart.
   * Will use the default pricing unless overidden with a
   * call to using(prices)
   */
  def checkout(): Price
}
object Cart {
  /**
   * Empty cart. Used as a starting point for cart activity.
   */
  val EmptyCart: Cart = ShoppingCart(Vector())(None)
}

/*
  Concrete implemetation of a shopping cart.
*/
private final case class ShoppingCart(contents: Seq[SKU])(prices: Option[PriceFileLocation]) extends Cart {
  override def add(sku: SKU): Cart = cartWith(contents :+ sku)
  override def remove(sku: SKU): Cart = cartWith(contents diff Seq(sku))
  override def using(altPrices: PriceFileLocation): Cart = cartWith(altPrices)
  override def checkout(): Price = Pricing.price(contents, prices)

  /*
    Helper functions - construct and return new instances of a Cart based on
    this and the supplied parameter.
  */
  def cartWith(cts: Seq[SKU]): Cart = ShoppingCart(cts)(prices)
  def cartWith(pfl: PriceFileLocation) = ShoppingCart(contents)(Some(pfl))
}
