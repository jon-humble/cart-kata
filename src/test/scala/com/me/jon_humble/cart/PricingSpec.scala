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

class PricingSpec extends FlatSpec {

  "SKU A" should "be priced to 50" in {
    assert(
      (EmptyCart add "A" checkout) == 50
    )
  }

  "SKU B" should "be priced to 30" in {
    assert(
      (EmptyCart add "B" checkout) == 30
    )
  }

  "SKU C" should "be priced to 20" in {
    assert(
      (EmptyCart add "C" checkout) == 20
    )
  }

  "SKU D" should "be priced to 15" in {
    assert(
      (EmptyCart add "D" checkout) == 15
    )
  }

  "SKU A" should "be have bulk pricing 3 for 130" in {
    assert(
      (EmptyCart add "A" add "A" add "A" checkout) == 130
    )
  }

  "Bulk and non-bulk pricing" should "be combined for SKU A" in {
    assert(
      (EmptyCart add "A" add "A" add "A" add "A" checkout) == 130 + 50
    )
  }

  "SKUs" should "be able to be mixed in the cart" in {
    assert(
      (EmptyCart add "B" add "A" add "B" add "C" checkout) == 45 + 50 + 20
    )
  }
}
