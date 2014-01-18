/*
 * Copyright © 2013, Adam Scarr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.vektah.codeglance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Observable<T> implements InvocationHandler {
	private List<T> pool = new CopyOnWriteArrayList<T>();
	private T eventDispatcher;
	private Class<T> collectionClass;

	public Observable(Class<T> collectionClass) {
		this.collectionClass = collectionClass;
	}

	public void add(T observer) {
		pool.add(observer);
	}

	public boolean remove(T observer) {
		return pool.remove(observer);
	}

	@SuppressWarnings( "unchecked" )
	public T dispatch() {
		if(eventDispatcher == null) {
			T dispatcher = (T) Proxy.newProxyInstance(
					collectionClass.getClassLoader(),
					new Class[]{collectionClass},
					this
			);

			eventDispatcher = dispatcher;
		}

		return eventDispatcher;
	}

	@Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		for(T observer: pool) {
			method.invoke(observer, args);
		}

		return null;
	}
}
