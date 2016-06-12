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

sealed trait Cart {
  def add(sku: SKU): Cart
  def remove(sku: SKU): Cart
  def using(prices: PriceFileLocation): Cart
  def checkout(): Price
}

private final case class FilledCart(contents: Seq[SKU])(prices: Option[PriceFileLocation]) extends Cart {
  override def add(sku: SKU): Cart = cartWith(contents :+ sku)
  override def remove(sku: SKU): Cart = cartWith(contents diff Seq(sku))
  override def using(altPrices: PriceFileLocation): Cart = cartWith(altPrices)
  override def checkout(): Price = Pricing.price(contents, prices)

  def cartWith(cts: Seq[SKU]): Cart = FilledCart(cts)(prices)
  def cartWith(pfl: PriceFileLocation) = FilledCart(contents)(Some(pfl))
}

object Cart {
  val EmptyCart: Cart = FilledCart(Vector())(None)
}
