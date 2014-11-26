/**
 * Copyright 2013, 2014  by Patrick Nicolas - Scala for Machine Learning - All rights reserved
 *
 * The source code in this file is provided by the author for the sole purpose of illustrating the 
 * concepts and algorithms presented in "Scala for Machine Learning" ISBN: 978-1-783355-874-2 Packt Publishing.
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * Version 0.96a
 */
package org.scalaml.plots



import java.awt.{GradientPaint, Color, Stroke, Shape, Paint, BasicStroke}
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import org.jfree.chart.{ChartFactory, JFreeChart}
import org.jfree.chart.plot.{PlotOrientation, XYPlot, CategoryPlot}
import org.jfree.chart.ChartFrame
import org.jfree.util.ShapeUtilities
import org.jfree.chart.renderer.xy.{XYDotRenderer, XYLineAndShapeRenderer}
import org.jfree.data.category.{DefaultCategoryDataset, CategoryDataset}
import org.jfree.chart.renderer.category.LineAndShapeRenderer
import org.jfree.data.statistics.DefaultMultiValueCategoryDataset
import java.util.List
import org.scalaml.core.Types.ScalaMl._
import org.jfree.data.xy.XYDataset
import java.awt.geom.Ellipse2D
import org.jfree.chart.renderer.xy.XYShapeRenderer
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.axis.NumberAxis
import Plot._


object ChartType extends Enumeration {
	type ChartType = Value
	val LINE, TIME_SERIES, SCATTER, BAR = Value
}



		/**
		 * <p>Generic plotting class that uses the JFreeChart library.<br>
		 * @throws IllegalArgumentException if the class parameters, config and theme are undefined
		 * @param config  Configuration for the plot of type <b>PlotInfo</b>
		 * @param theme Configuration for the display of plots of type <b>PlotTheme</b>
		 * @author Patrick Nicolas
		 * @since  November 18, 2013
		 * @note Scala for Machine Learning
		 */
abstract class Plot(config: PlotInfo, theme: PlotTheme) {
	import Plot._
	require(config != null, "Plot Cannot create a plot with undefined stateuration")
	require(theme != null, "Plot Cannot create a plot with undefined theme")

		/**
		 * Display array of tuple (x,y) in a 2D plot for a given width and height
		 * @param xy Array of pair (x,y)
		 * @param width Width for the display (pixels)
		 * @param height Heigth of the chart (pixels)
		 */
	def display(xy: XYTSeries, width: Int, height: Int): Unit

		/**
		 * Display a vector of Double value in a 2D plot with counts [0, n] on X-Axis and
		 * vector value on Y-Axis with a given width and height
		 * @param xy Array of pair (x,y)
		 * @param width Width for the display (pixels)
		 * @param height Heigth of the chart (pixels)
		 */
	def display(y: DblVector, width: Int, height: Int): Unit
   
	protected[this] def createFrame(id: String, chart: JFreeChart): Unit = {
		val frame = new ChartFrame(s"Chart ${count+1}: $id", chart)
		val anchor = getLocation
		frame.setLocation(anchor._1, anchor._2)
		frame.pack
		frame.setVisible(true)
	}
}



object Plot {
	type PlotInfo = (String, String, String)
	final val DISPLAY_OFFSET = 25
	
	var count = 0
	final def getLocation: (Int, Int) = {
		count += 1
		val offset = count * DISPLAY_OFFSET
		(offset, offset)
	}

	private val MIN_DISPLAY_SIZE = 60
	private val MAX_DISPLAY_SIZE = 1280
	
	def validateDisplay[T](y: Array[T], width: Int, height: Int, comment: String): Unit = {
		require(y != null && y.size > 0, s"$comment Cannot display an undefined series")
		validateDim(width, height, comment)
	}
	
	import scala.collection.immutable.List
	def validateDisplay[T](y: List[T], width: Int, height: Int, comment: String): Unit = {
		require(y != null && y.size > 0, s"$comment Cannot display an undefined series")
		validateDim(width, height, comment)
	}
	
	
	def validateDim(width: Int, height: Int, comment: String): Unit = {
		require( width > MIN_DISPLAY_SIZE && width < MAX_DISPLAY_SIZE, s"$comment Width $width is out of range")
		require( height > MIN_DISPLAY_SIZE && height < MAX_DISPLAY_SIZE, s"$comment  height $height is out of range")
	}
}

// ------------------------  EOF ----------------------------------------------