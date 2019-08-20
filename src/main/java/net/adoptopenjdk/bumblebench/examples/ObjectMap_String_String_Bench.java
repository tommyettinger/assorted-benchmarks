/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.utils.ObjectMap;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * ObjectMap_String_String_Bench score: 1628225.375000 (1.628M 1430.3%)
 *                           uncertainty:  40.0%
 */
public final class ObjectMap_String_String_Bench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		ObjectMap<String, String> coll = new ObjectMap<String, String>();
		for (long i = 0; i < numIterations; i++) {
			String s = String.valueOf(i);
			coll.put(s, s);
		}
		return numIterations;
	}
}

