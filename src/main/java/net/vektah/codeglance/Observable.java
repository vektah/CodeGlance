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
