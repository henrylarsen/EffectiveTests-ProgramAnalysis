/**
 * In attempting to build a monitor to track
 * objects when they are dereferenced/finalized,
 * I found this. So, the following is taken from
 * Ricardo Artur Staroksi with several modifications.
 *
 * Thanks, Ricardo!
 *
 * https://github.com/staroski/gc-listener/tree/master
 */
package org.effective.tests.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * This class allows to detect when a object is garbage collected.<br>
 * Two steps are necessary in order that a class can be notified about the garbage collection of some object:<br>
 * - It need to {@link #addListener(Consumer) add} a {@link Consumer<Integer> listener} to this class;<br>
 * - It need to {@link #bind(Object) bind} the desired object;<br>
 * If these steps are done then when a garbage collection occurs the method {@link Consumer#accept(Object)} will be called receiving the key of the
 * garbage collected object.
 *
 * @author Ricardo Artur Staroski and Mazen Kotb
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class GCMonitor {

    // unique instance of the GcMonitor
    private static final GCMonitor INSTANCE = new GCMonitor();

    /**
     * Retrieves the unique instance of this class.
     *
     * @return the unique instance of this class.
     */
    public static GCMonitor get() {
        return INSTANCE;
    }

    // listeners that need to be noticed when a object is garbage collected
    private final List<Consumer<Integer>> listeners = new LinkedList<>();

    // reference queue where the VM puts the objects being garbage collected
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
    private final Map<Integer, WeakReference<Object>> weakReferences = new ConcurrentHashMap<>();

    // private constructor to avoid that other classes try to instantiate this one
    private GCMonitor() {
        // this runnable runs forever inside a daemon thread
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while (true) { // runs forever in a daemon thread
                    // get the reference of the object that was garbage collected
                    Reference<Object> garbage;

                    try {
                        garbage = (Reference<Object>) referenceQueue.remove();
                    } catch (InterruptedException e) {
                        continue;
                    }

                    // get the key for that object
                    int key = System.identityHashCode(garbage.get());

                    weakReferences.remove(key);

                    // send the key to the listeners
                    synchronized (listeners) {
                        for (Consumer<Integer> listener : listeners) {
                            try {
                                listener.accept(key);
                            } catch (Throwable ex) {
                                System.out.println("Error occurred when executing GC listener");
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        };

        String name = getClass().getSimpleName() + " Monitor Thread";
        Thread thread = new Thread(runnable, name);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Adds a {@link Consumer<Integer> listener} to this {@link GCMonitor garbage monitor}.
     *
     * @param listener
     *            The listener to be added.
     * @return This object itself.
     */
    public GCMonitor addListener(Consumer<Integer> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Binds the specified object with the specified key and returns a {@link WeakReference} of it.
     *
     * @param object The object to be binded with the key.
     * @return A unique key to identify the object that will be later passed to the consumers
     */
    public int bind(Object object) {
        int key = System.identityHashCode(object);

        // if we already have a reference to it, just return the key
        if (weakReferences.containsKey(key)) {
            return key;
        }

        // create a reference associated with this object
        WeakReference reference = new WeakReference<>(object, referenceQueue);

        // keep reference in memory while object is alive
        weakReferences.put(key, reference);

        return key;
    }

    /**
     * Removes a {@link Consumer<Integer> listener} from this {@link GCMonitor garbage monitor}.
     *
     * @param listener The listener to be removed.
     * @return This object itself.
     */
    public GCMonitor removeListener(Consumer<Integer> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
        return this;
    }
}
