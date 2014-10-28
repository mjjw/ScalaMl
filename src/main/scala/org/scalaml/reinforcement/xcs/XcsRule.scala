/**
 * Copyright 2013, 2014  by Patrick Nicolas - Scala for Machine Learning - All rights reserved
 *
 * The source code in this file is provided by the author for the sole purpose of illustrating the 
 * concepts and algorithms presented in "Scala for Machine Learning" ISBN: 978-1-783355-874-2 Packt Publishing.
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package org.scalaml.reinforcement.xcs

import scala.collection.mutable.HashMap
import org.scalaml.trading.{Signal, EQUAL}
import org.scalaml.ga.{Operator, Gene, Discretization}
import org.scalaml.reinforcement.qlearning.QLState
import org.scalaml.core.types.ScalaMl._
import XcsRule._
import org.scalaml.ga.Chromosome
import scala.util.Random


class XcsAction(val sensorid: String, val _target: Double)(implicit val _discr: Discretization) extends Gene(sensorid, _target, EQUAL)

object XcsAction {
	val XCSACTION_SIZE = 32
	def apply(action: XcsAction, r: Random): XcsAction = (action ^ r.nextInt(XCSACTION_SIZE)).asInstanceOf[XcsAction]
}

		/**
		 * <p>Class that define a rule or policy in XCS algorithm. The rule is encoded as a gene so
		 * it can be manipulated by the Genetic Algorithm. A rule is defined by the format:<br>
		 * IF signal THEN action.<br> The constructor increase a global rules count used to automatically
		 * assigned a label to each signal/predicate.</p>
		 * @param signal  signal or predicated of the rule
		 * @param action action of the rule
		 * @throws IllegalArgumenException if the predicate or the action of the rule is undefined
		 * 
		 * @author Patrick Nicolas
		 * @since March 24, 2014
		 * @note Scala for Machine Learning
		 */

class XcsRule(val signal: Signal, val action: XcsAction) {
   require(signal != null, "Cannot create an XCS rule with undefined signal/predicate")
   require(action != null, "Cannot create an XCS rule with undefined action")
}



			/**
			 * <p>Companion singleton for the XCS rule, The object defines the following element:<br>
			 * Default discretization method, DIGITIZE<br>
			 * XcsAction type as a Q-Learning state<br>
			 * Global rule counter, rulesCount<br>
			 * Creationof random action required by the covering process.</p>
			 * 
			 * @author Patrick Nicolas
			 * @since March 24, 2014
		     * @note Scala for Machine Learning
			 */

object XcsRule {
	import QLState._
//	final val DIGITIZE = Discretization((x:Double) => (x*1e+4).floor.toInt, (n: Int) => n.toDouble*1e-4 )
    type XcsSensor = Signal

//	var rulesCount = 0
//	def apply[T <% Double](signal: Signal, action: XcsAction[T]): XcsRule[T] = new XcsRule[T](signal, action)
//	def apply[T <% Double](id: String, threshold: Double, op: Operator, action: XcsAction[T]): XcsRule[T] = new XcsRule[T](Signal(id, threshold, op)(DIGITIZE), action)
}

   

// -----------------------------------------  EOF ----------------------------------------