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

import org.scalatest.FlatSpec
import scala.language.postfixOps
import Cart.EmptyCart

class CartSpec extends FlatSpec {

  "An empty Cart" should "checkout to zero" in {
    assert(
      (EmptyCart checkout) == 0
    )
  }

  "Adding and removing a SKU" should "checkout to zero" in {
    val cart = (
      EmptyCart
      add "A"
      remove "A"
    )

    assert(cart.checkout == 0)
  }

  "Removing a SKU from an empty cart" should "have no effect" in {
    assert(
      (EmptyCart remove "A" checkout) == 0
    )
  }

  "Checking out a cart with one item" should "equal the price of that item" in {
    assert(
      (EmptyCart add "A" checkout) == Pricing.price(Seq("A"))
    )
  }
}
