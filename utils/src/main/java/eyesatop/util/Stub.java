package eyesatop.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A utility which creates stub implementations of interfaces.
 */
public class Stub implements InvocationHandler {

    // No need to create more than one instance as this class holds no state.
    private static final Stub instance = new Stub();

    /**
     * Returns the specified {@code object} unless it is null, in which case a {@code Stub} implementation of
     * {@code iface} will be returned.
     * @param <T>   The type to stub.
     * @param object    The object to condition the stubbing upon.
     * @param iface     The interface to stub.
     * @return  The newly created {@code Stub} implementation.
     */
    public static <T> T ifNull(T object, Class<T> iface) {
        if (object == null) {
            object = of(iface);
        }
        return object;
    }

    /**
     * Creates a new {@code Stub} implementation of the specified interface. The stub implementation will return
     * 0s and nulls for all invocations.
     * @param iface The interface to stub.
     * @param <T>   The type to stub.
     * @return  The newly created {@code Stub} implementation.
     */
    public static <T> T of(Class<T> iface) {
        if (!iface.isInterface()) throw new IllegalArgumentException("can only stub interfaces");
        return iface.cast(Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] {iface}, instance));
    }

    /// If we have the ability to find something that's definitely evil, why not make it funny too.
    protected static class WildPunkException extends RuntimeException {
        public WildPunkException(String message) {
            super(message);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> rt = method.getReturnType();
        if (rt.isPrimitive()) {
            if (rt.equals(boolean.class)) {
                return false;
            } else if (rt.equals(byte.class) || rt.equals(short.class)) {
                throw new WildPunkException("\nNever use byte or short as return types. Especially not in interfaces.\n" +
                        "Don't even use them as references to numbers, use an int or a long.\nIf you are trying to save " +
                        "memory then you're in tough luck: a byte consumes a whole word block, which for the JVM is " +
                        "exactly 4 bytes.\nBesides being a futile practice even if it did consume less than a word, " +
                        "look around!\nHave you ever run across a method in the runtime libraries that does this?\n" +
                        "Is everyone dumb?\nEven if they built the platform that you're now consuming?\nIs the world " +
                        "full of mindless zombies?");
            } else if (rt.equals(int.class)) {
                return 0;
            } else if (rt.equals(long.class)) {
                return 0L;
            } else if (rt.equals(float.class)) {
                return 0F;
            } else if (rt.equals(double.class)) {
                return 0D;
            }
        } else if (rt.isArray()) {
            return Array.newInstance(rt.getComponentType(), 0);
        } else if (Set.class.isAssignableFrom(rt)) {
            return Collections.emptySet();
        } else if (List.class.isAssignableFrom(rt)) {
            return Collections.emptyList();
        }
        return null;
    }
}