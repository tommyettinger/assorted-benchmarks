/*
 * Copyright (c) 2024 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.tommyettinger.gdx;

import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Trying to replicate an issue with IntMap, or IntIntMap, with libGDX.
 */
public class IntMapIssueTest {
	//655304458, 655238926, 655501067, 655369995, 655304463, 655238923, 655501064, 655369998, 655369992, 655238920, 655238928, 655435529, 655304457, 655304465, 655501066, 655369994, 655304462, 655238922, 655435531, 655369999, 655304459, 655238927, 655370000, 655435528, 655304464, 655304456, 655501065, 655238929, 655369993, 655238921, 655370001, 655435530

	private static void putGdx(IntIntMap map, int key, int value){
		map.put(key, value);
		if((key * 421 >>> 5) % 31 >= 10)
			map.remove(key-1, -1);
		System.out.println(map.get(655369993, -1));
	}

	private static void putJdk(HashMap<Integer, Integer> map, int key, int value){
		map.put(key, value);
		if((key * 421 >>> 5) % 31 >= 10)
			map.remove(key-1);
		System.out.println(map.get(655369993));
	}
	@Test
	public void testGdx() {
		IntIntMap map = new IntIntMap(24, 0.8f);
		putGdx(map, 655238920, 655238920);
		putGdx(map, 655238921, 655238921);
		putGdx(map, 655238922, 655238922);
		putGdx(map, 655238923, 655238923);
		putGdx(map, 655238926, 655238926);
		putGdx(map, 655238927, 655238927);
		putGdx(map, 655238928, 655238928);
		putGdx(map, 655238929, 655238929);
		putGdx(map, 655304456, 655304456);
		putGdx(map, 655304457, 655304457);
		putGdx(map, 655304458, 655304458);
		putGdx(map, 655304459, 655304459);
		putGdx(map, 655304462, 655304462);
		putGdx(map, 655304463, 655304463);
		putGdx(map, 655304464, 655304464);
		putGdx(map, 655304465, 655304465);
		putGdx(map, 655369992, 655369992);
		putGdx(map, 655369993, 655369993);
		putGdx(map, 655369994, 655369994);
		putGdx(map, 655369995, 655369995);
		putGdx(map, 655369998, 655369998);
		putGdx(map, 655369999, 655369999);
		putGdx(map, 655370000, 655370000);
		putGdx(map, 655370001, 655370001);
		putGdx(map, 655435528, 655435528);
		putGdx(map, 655435529, 655435529);
		putGdx(map, 655435530, 655435530);
		putGdx(map, 655435531, 655435531);
		putGdx(map, 655501064, 655501064);
		putGdx(map, 655501065, 655501065);
		putGdx(map, 655501066, 655501066);
		putGdx(map, 655501067, 655501067);
		// not in the original
		putGdx(map, 655501068, 655501068);

		System.out.println("size = " + map.size);
		Assert.assertEquals(18, map.size);
	}
	@Test
	public void testGdxManual() {
		Collections.allocateIterators = true;
		IntMap<Integer> map = new IntMap<>(5, 0.8f);
		map.put(1, 1);
		map.put(2, 2);
		map.put(3, 3);
		map.put(4, 4);
		map.put(5, 5);
		map.put(6, 6);

		IntMap.Entries<Integer> it = map.entries();
		int ctr = 0;
		while (it.hasNext && ctr < 4) {
			IntMap.Entries<Integer> it2 = map.entries();
			if(it2.hasNext()) it2.next();
			IntMap.Entry<Integer> item = it.next();
			map.remove(item.key);
			it.remove();
			ctr++;
		}

		System.out.println("size = " + map.size);
		Assert.assertEquals(-2, map.size);
	}

	@Test
	public void testJdk() {
		HashMap<Integer, Integer> map = new HashMap<>(24, 0.8f);

		putJdk(map, 655238920, 655238920);
		putJdk(map, 655238921, 655238921);
		putJdk(map, 655238922, 655238922);
		putJdk(map, 655238923, 655238923);
		putJdk(map, 655238926, 655238926);
		putJdk(map, 655238927, 655238927);
		putJdk(map, 655238928, 655238928);
		putJdk(map, 655238929, 655238929);
		putJdk(map, 655304456, 655304456);
		putJdk(map, 655304457, 655304457);
		putJdk(map, 655304458, 655304458);
		putJdk(map, 655304459, 655304459);
		putJdk(map, 655304462, 655304462);
		putJdk(map, 655304463, 655304463);
		putJdk(map, 655304464, 655304464);
		putJdk(map, 655304465, 655304465);
		putJdk(map, 655369992, 655369992);
		putJdk(map, 655369993, 655369993);
		putJdk(map, 655369994, 655369994);
		putJdk(map, 655369995, 655369995);
		putJdk(map, 655369998, 655369998);
		putJdk(map, 655369999, 655369999);
		putJdk(map, 655370000, 655370000);
		putJdk(map, 655370001, 655370001);
		putJdk(map, 655435528, 655435528);
		putJdk(map, 655435529, 655435529);
		putJdk(map, 655435530, 655435530);
		putJdk(map, 655435531, 655435531);
		putJdk(map, 655501064, 655501064);
		putJdk(map, 655501065, 655501065);
		putJdk(map, 655501066, 655501066);
		putJdk(map, 655501067, 655501067);
		// not in the original
		putJdk(map, 655501068, 655501068);

		System.out.println("size = " + map.size());
		Assert.assertEquals(18, map.size());
	}
}
