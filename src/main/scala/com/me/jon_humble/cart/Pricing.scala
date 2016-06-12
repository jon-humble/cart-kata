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

import com.typesafe.config.{ ConfigFactory, ConfigObject }
import collection.JavaConverters._

/**
 * Singleton object used by the Cart to price up its contents.
 * @see Cart#checkout
 */
object Pricing {

  /**
   * Convenience overload for using default prices
   */
  def price(contents: Seq[SKU]): Price = price(contents, None)

  /**
   * Price the supplied sequence of SKUs using the supplied pricing rules.
   */
  def price(contents: Seq[SKU], priceFileLocation: Option[PriceFileLocation]): Price = {
    val pricer = Prices(priceFileLocation)
    val uniqueSkus = contents.distinct
    // For each SKU, store the sku with its frequency in the skus sequence
    val skuCounts = uniqueSkus.map(sku => (sku, contents.count(_ == sku)))
    // Price each of the sku counts and return the sum
    skuCounts.map(c => pricer.price(c._1, c._2)).fold(0)(_ + _)
  }

  /*
    skuData of this class use the supplied pricing rules to price SKUs.
  */
  private final class Prices(pfl: Option[PriceFileLocation]) {
    val prices = pfl
      .map(ConfigFactory.load(_)) // Load the price list if supplied
      .getOrElse(ConfigFactory.load()) // or else use the default prices

    // Note: We are using JavaConverters to get a Scala Sequence
    // from the returned Java list.
    val skuData: Seq[ConfigObject] = prices.getObjectList(PriceList).asScala

    val unitPrices = skuData
      // Map each item to a Config from ConfigObject
      .map(_.toConfig)
      // Pull out the sku and the unit price
      .map(o => (o.getString(Sku), o.getInt(Price)))
      // Convert the sequence of tuple to a map
      .toMap[String, Int]

    val bulkPrices = skuData
      // // Map each item to a Config from ConfigObject
      .map(_.toConfig)
      // Remove those that don't have bulk prices
      .filter(_.hasPath(BulkPrice))
      // Convert to a tuple2
      .map(o => (o.getString(Sku), (o.getInt(BulkAmount), o.getInt(BulkPrice))))
      // then to a map
      .toMap[String, (Int, Int)]

    def price(sku: SKU, quantity: Int): Price = {
      val pricer =
        bulkPricing // Try to use bulk pricing first
          .orElse(unitPricing) // If there are no bulk prices, use unit prices only
          .orElse(invalidSkuPricing) // If the SKU is not found, return 0

      pricer(sku)(quantity)
    }

    def bulkPricing: PartialFunction[SKU, (Int => Price)] = {
      case sku if hasBulkPricing(sku) => {
        quantity =>
          {
            val unitPrice = unitPrices(sku)
            val (bulkAmount, bulkPrice) = bulkPrices(sku)
            val batches = quantity / bulkAmount
            val units = quantity % bulkAmount
            (batches * bulkPrice) + (units * unitPrice)
          }
      }
    }

    def unitPricing: PartialFunction[SKU, (Int => Price)] = {
      case sku if isValidSku(sku) => {
        quantity => quantity * unitPrices(sku)
      }
    }

    def invalidSkuPricing: PartialFunction[SKU, (Int => Price)] = {
      case _ => quantity => 0
    }

    def hasBulkPricing(sku: SKU): Boolean = {
      bulkPrices.contains(sku)
    }

    def isValidSku(sku: SKU): Boolean = {
      unitPrices.contains(sku)
    }
  }
  private object Prices {
    def apply(altPrices: Option[PriceFileLocation]) = {
      new Prices(altPrices)
    }
  }

}
