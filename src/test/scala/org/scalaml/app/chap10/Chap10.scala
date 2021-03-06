/**
 * Copyright (c) 2013-2015  Patrick Nicolas - Scala for Machine Learning - All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file 
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * The source code in this file is provided by the author for the sole purpose of illustrating the 
 * concepts and algorithms presented in "Scala for Machine Learning". 
 * ISBN: 978-1-783355-874-2 Packt Publishing.
 * 
 * Version 0.99
 */
package org.scalaml.app.chap10

import org.scalaml.app.ScalaMlTest


		/**
		 * Test driver for the techniques described in the Chapter 10 Genetic algorithm

		 * @see org.scalaml.app.ScalaMlTest
		 * @author Patrick Nicolas
		 * @since May 28, 2014
		 * @see Scala for Machine Learning Chapter 10 Genetic algorithm
		 */
final class Chap10 extends ScalaMlTest  {
		/**
		 * Name of the chapter the tests are related to
		 */
	val chapter: String = "Chapter 10"
	val maxExecutionTime: Int = 20
	
	test(s"$chapter Genetic algorithm") {
		evaluate(GAEval)
	}
}


// ----------------------------------  EOF ----------------------------------------------------------------