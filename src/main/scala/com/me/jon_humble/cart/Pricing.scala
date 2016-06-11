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

object Pricing {

  def price(skus: Seq[SKU]): Price = {
    val uniqueSkus = skus.distinct
    // For each SKU, store the sku with its frequency in the skus sequence
    val skuCounts = uniqueSkus.map(sku => (sku, skus.count(_ == sku)))
    // Price each of the sku counts and return the sum
    skuCounts.map(c => Prices.price(c._1, c._2)).fold(0)(_ + _)
  }

  private object Prices {
    val config = ConfigFactory.load()
    val objects: Seq[ConfigObject] = config.getObjectList(PriceList).asScala
    val unitPrices = objects
      // Map each item to a Config from ConfigObject
      .map(_.toConfig)
      // Pull out the sku and the unit price
      .map(config => (config.getString(Sku), config.getInt(Price)))
      // Convert the sequence of tuple to a map
      .toMap[String, Int]

    val bulkPrices = objects
      // // Map each item to a Config from ConfigObject
      .map(_.toConfig)
      // Remove those that don't have bulk prices
      .filter(_.hasPath(BulkPrice))
      // Convert to a tuple2
      .map(config => (config.getString(Sku), (config.getInt(BulkAmount), config.getInt(BulkPrice))))
      // then to a map
      .toMap[String, (Int, Int)]

    def price(sku: SKU, quantity: Int): Price = {
      val unitPrice = unitPrices(sku)
      if (hasBulkPricing(sku)) {
        val (bulkAmount, bulkPrice) = bulkPrices(sku)
        val batches = quantity / bulkAmount
        val units = quantity % bulkAmount
        (batches * bulkPrice) + (units * unitPrice)
      } else {
        quantity * unitPrice
      }
    }

    def hasBulkPricing(sku: SKU): Boolean = bulkPrices.contains(sku)
  }

}
