package eyesatop.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class Group implements InvocationHandler {

    public static <T> Builder<T> of(Class<T> cls) {
        return new Builder<>(cls);
    }

    public static class Builder<T> {
        private final Class<T> cls;
        private final Collection<T> objects;

        public Builder(Class<T> cls) {
            this.cls = cls;
            objects = new LinkedList<>();
        }

        @SafeVarargs
        public final Builder<T> add(T ... objects) {
            this.objects.addAll(Arrays.asList(objects));
            return this;
        }

        public T build() {
            return cls.cast(Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, new Group(objects)));
        }
    }

    private static final Stub stub = new Stub();

    private final Collection objects;

    private Group(Collection objects) {
        this.objects = objects;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (Object object : objects) {
            method.invoke(object, args);
        }
        return stub.invoke(proxy, method, args);
    }
}
