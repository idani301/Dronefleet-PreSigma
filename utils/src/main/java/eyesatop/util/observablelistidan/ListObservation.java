package eyesatop.util.observablelistidan;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Idan on 18/12/2017.
 */
public class ListObservation<T> extends ListObserver<T>{

    private final Executor executor;
    private final ListObserver listObserver;

    public ListObservation(Executor executor, ListObserver listObserver) {
        this.executor = executor;
        this.listObserver = listObserver;
    }

    @Override
    public void added(final T value, final int index) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listObserver.added(value,index);
            }
        });
    }

    @Override
    public void removed(final T value, final int oldIndex) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listObserver.removed(value,oldIndex);
            }
        });
    }

    @Override
    public void replaced(final T oldValue, final T newValue, final int index) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listObserver.replaced(oldValue,newValue,index);
            }
        });
    }

    @Override
    public void added(final Collection<? extends T> values, final int startIndex) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listObserver.added(values,startIndex);
            }
        });
    }

    @Override
    public void removed(final HashMap<T, Integer> removedValuesAndIndexesMap) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listObserver.removed(removedValuesAndIndexesMap);
            }
        });
    }

    @Override
    public void positionChanged(final HashMap<T, PositionChange> positionChanges) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listObserver.positionChanged(positionChanges);
            }
        });
    }

    @Override
    public void swapped(final T firstValue, final T secondValue, final int firstValueOldIndex, final int secondValueOldIndex) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listObserver.swapped(firstValue,secondValue,firstValueOldIndex,secondValueOldIndex);
            }
        });
    }

    public ListObserver getListObserver() {
        return listObserver;
    }
}
