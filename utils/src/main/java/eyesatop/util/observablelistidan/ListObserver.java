package eyesatop.util.observablelistidan;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Idan on 18/12/2017.
 */
public abstract class ListObserver<T> {

    public abstract void added(T value,int index);
    public abstract void removed(T value,int oldIndex);

    public void replaced(T oldValue,T newValue,int index){
    }

    public void positionChanged(HashMap<T,PositionChange> positionChanges){
    }

    public void swapped(T firstValue,T secondValue,int firstValueOldIndex,int secondValueOldIndex){
    }

    public void added(Collection<? extends T> values, int startIndex){

        int counter = 0;
        for(T value : values){
            added(value,startIndex+counter);
            counter++;
        }
    }
    public void removed(HashMap<T,Integer> removedValuesAndIndexesMap){
        for(T key : removedValuesAndIndexesMap.keySet()){
            removed(key,removedValuesAndIndexesMap.get(key));
        }
    }
}
