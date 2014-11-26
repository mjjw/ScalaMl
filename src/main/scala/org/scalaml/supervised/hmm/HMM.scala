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
package org.scalaml.supervised.hmm


import org.scalaml.core.Types.ScalaMl._
import org.scalaml.core.design.{PipeOperator, Model}
import org.scalaml.core.XTSeries
import org.scalaml.util.Matrix
import scala.util.{Try, Success, Failure}
import scala.annotation.implicitNotFound
import org.apache.log4j.Logger
import org.scalaml.util.Display
import HMM._



	/**
	 * <p>Enumeration class to specify the canonical form of the HMM</p>
	 * @author Patrick Nicolas
	 * @since March 9, 2014
	 * @note Scala for Machine Learning Chapter 7 Sequential data models/Hidden Markov Model - Evaluation
	 */
object HMMForm extends Enumeration {
	type HMMForm = Value
	val EVALUATION, DECODING = Value
}


import HMMForm._
		/**
		 * <p>Generic model for dynamic programming algorithms used in HMM.</p>
		 * @throws IllegalArgumenException If either Lambda or the observation are undefined.
		 * @param lambda Lambda (pi, A, B) model for the HMM composed of the initial state probabilities, the state-transition probabilities matrix and the emission proabilities matrix.
		 * @param obs Array of observations as integer (categorical data)
		 * @author Patrick Nicolas
		 * @since March 29, 2014
		 * @note Scala for Machine Learning Chapter 7/Sequential data models/Hidden Markov Model - Evaluation
		 */
abstract class HMMModel(val lambda: HMMLambda, val obs: Array[Int]) extends Model {
	import HMMModel._
	
	check(lambda, obs)
	val persists = "models/hmm"
}


object HMMModel {
	private def check(lambda: HMMLambda, obs: Array[Int]): Unit = {
		require(lambda != null, "HMMModel.check Cannot create a model (dynamic programming) for HMM with undefined lambda model")
		require(obs != null && obs.size > 0, "HMMModel.check Cannot create a model (dynamic programming) with undefined observations")
	}
}


	/**
	 * <p>Generic class for the alpha (forward) pass and beta (backward) passes used in
	 * the evaluation form of the HMM.</p>
	 * @param lambda Lambda (pi, A, B) model for the HMM composed of the initial state probabilities, the state-transition probabilities matrix and the emission proabilities matrix.
	 * @param obs Array of observations as integer (categorical data)
	 * @author Patrick Nicolas
	 * @since March 29, 2014
	 * @note Scala for Machine Learning Chapter 7/Sequential data models/Hidden Markov Model - Evaluation
	 */
protected class Pass(lambda: HMMLambda, obs: Array[Int]) extends HMMModel(lambda, obs) { 
	protected var alphaBeta: Matrix[Double] = _
	protected val ct = Array.fill(lambda.getT)(0.0)

	protected def normalize(t: Int): Unit = {
		import HMMConfig._
		require(t >= 0 && t < lambda.getT, s"Incorrect argument $t for normalization")
		ct.update(t, foldLeft(lambda.getN, (s, n) => s + alphaBeta(t, n)))
		alphaBeta /= (t, ct(t))
	}

	def getAlphaBeta: Matrix[Double] = alphaBeta
}
		/**
		 * <p>Implementation of the Hidden Markov Model (HMM). The HMM classifier defines the
		 * three canonical forms (Decoding, training and evaluation).<br>
		 * <pre><span style="font-size:9pt;color: #351c75;font-family: &quot;Helvetica Neue&quot;,Arial,Helvetica,sans-serif;">
		 * The three canonical forms are defined as
		 * <b>Evaluation<b> Computation of the probability (or likelihood) of the observed sequence Ot given a Lambda (pi, A, B) model<br>
		 * <b>Training</b> Generation of the Lambda (pi, A, B)-model given a set of observations and a sequence of states.<br>
		 * <b>Decoding</b> Extraction the most likely sequence of states {qt} given a set of observations Ot and a Lambda (pi, A, B)-model.</span></pre></p>
		 * @constructor Create a HMM algorithm with either a predefined Lambda model for evaluation and prediction or a Lambda model to generate through training
		 * @throws IllegalArgumentException if the any of the class parameters is undefined
		 * @param lambda lambda model generated through training or used as input for the evaluation and decoding phase
		 * @param form     Canonical form (evaluation or decoding) used in the prediction of sequence
		 * @param maxIters Maximum number of iterations used in the Baum-Welch algorithm
		 * @param f  Implicit conversion of a Double vector a parameterized type bounded to Array[Int] (Discretization)
		 * 
		 * @throws ImplicitNotFoundException if the implicit conversion from DblVector to T is not defined prior the instantiation of the class
		 * @author Patrick Nicolas
		 * @since March 23, 2014
		 * @note Scala for Machine Learning Chapter 7 Sequential data models / Hidden Markov Model
		 */
@implicitNotFound("HMM Conversion from DblVector to type T undefined")
final protected class HMM[@specialized T <% Array[Int]](lambda: HMMLambda, form: HMMForm, maxIters: Int)(implicit f: DblVector => T)  
				extends PipeOperator[DblVector, HMMPredictor] {
	
	check(lambda, maxIters)
	
	private val logger = Logger.getLogger("HMM")
	private val state = HMMState(lambda, maxIters)
	
		/**
		 * <p>Classifier for the Hidden Markov Model. The pipe operator evaluates the 
		 * HMM if form == EVALUATION or decodes a HMM if form == DECODING for a given
		 * set of observations obs and a lambda model.</p>
		 * @param obs set of observation of type bounded by Array[Int]
		 * @return HMMPredictor instance if no computation error occurs, NONE otherwise
		 */
		
	override def |> : PartialFunction[DblVector, HMMPredictor] = {
		case obs: DblVector if(obs != null && obs.size > 1) => {
			Try { 
				form match {
					case EVALUATION => evaluate(obs)
					case DECODING => decode(obs)
				} 
			} match {
				case Success(prediction) =>prediction
				case Failure(e) => Display.error("HMM.|> ", logger, e); null
			}
		}
	}
	
		/**
		 * <p>Implements the 3rd canonical form of the HMM</p>
		 * @param obsIdx given sequence of observations
		 * @return HMMPredictor predictor as a tuple of (likelihood, sequence (array) of observations indexes)
		 */
	def decode(obs: T): HMMPredictor = (ViterbiPath(lambda, obs).maxDelta, state.QStar())
	
		/**
		 * <p>Implements the 'Evaluation' canonical form of the HMM</p>
		 * @param obsIdx index of the observation O in a sequence
		 * @return HMMPredictor predictor as a tuple of (likelihood, sequence (array) of observations indexes)
		 */
	def evaluate(obs: T): HMMPredictor = (-Alpha(lambda, obs).logProb, obs)
	
		/**
		 * <p>Retrieve the Lambda model associated to this HMM</p>
		 * @return lambda model
		 */
	final def getModel: HMMLambda = lambda
}



	/**
	 * <p>Companion object for the HMM that defines a HMMPredictor type and the constructor 
	 * apply for the HMM.</p>
	 * @author Patrick Nicolas
	 * @since March 11, 2014
	 */
object HMM {
		/**
		 * <p>Define the result of the prediction (decoding or evaluation) as a
		 * a tuple of (likelihood, sequence (array) of observations indexes).</p>
		 */
	type HMMPredictor = (Double, Array[Int])
	def apply[T <% Array[Int]](lambda: HMMLambda, form: HMMForm, maxIters: Int)(implicit f: DblVector => T): HMM[T] = 
		new HMM[T](lambda, form, maxIters)
		
	def apply[T <% Array[Int]](lambda: HMMLambda, form: HMMForm)(implicit f: DblVector => T): HMM[T] =  
		new HMM[T](lambda, form, HMMState.DEFAULT_MAXITERS)
	
	
	def apply[T <% Array[Int]](config: HMMConfig, obsIndx: Array[Int], form: HMMForm,  maxIters: Int, eps: Double)(implicit f: DblVector => T): Option[HMM[T]] = {
		val baumWelchEM = new BaumWelchEM(config, obsIndx, maxIters, eps)
		if( baumWelchEM.maxLikelihood != None)
			Some(new HMM[T](baumWelchEM.lambda, form, maxIters))
		else 
			None
	}
	
	val MAX_NUM_ITERS = 1024
	private def check(lambda: HMMLambda, maxIters: Int): Unit = {
		require(lambda != null, "HMM.check Cannot execute a HMM with undefined lambda model")
		require(maxIters > 1 && maxIters < MAX_NUM_ITERS, s"HMM.check  Maximum number of iterations to train a HMM $maxIters is out of bounds")
	}
}



// ----------------------------------------  EOF ------------------------------------------------------------