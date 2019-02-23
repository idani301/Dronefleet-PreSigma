package eyesatop.util.observablelistidan;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eyesatop.util.BlockingExecutor;
import eyesatop.util.Removable;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableBoolean;

/**
 * Created by Idan on 17/12/2017.
 */

public class ObservableListIdan<T> extends AbstractList<T> {

    private final Lock listChangeLock = new StubLock();
    private final Lock observersChangeLock = new StubLock();

    private final ArrayList<T> list = new ArrayList<>();
    private final List<ListObservation<T>> observers = new ArrayList<>();

    private final BooleanProperty isEmpty = new BooleanProperty(true);

    public ObservableListIdan(){
        observe(new ListObserver<T>() {
            @Override
            public void added(T value, int index) {
                isEmpty.setIfNew(false);
            }

            @Override
            public void removed(T value, int oldIndex) {
                if(list.size() == 0){
                    isEmpty.setIfNew(true);
                }
            }
        });
    }

    @Override
    public T get(int i) {
        return list.get(i);
    }

    @Override
    public int size() {
        return list.size();
    }

    public Removable observe(ListObserver<T> observer){
        return observe(observer,BlockingExecutor.INSTANCE);
    }

    public void observeCurrentValue(ListObserver<T> observer){

        observersChangeLock.lock();
        for(ListObservation observation : observers){
            if(observation.getListObserver().equals(observer)) {
                observation.added(list, 0);
            }
        }

        observersChangeLock.unlock();
    }

    public Removable observe(ListObserver<T> observer,Executor executor){
        final ListObservation newObservation = new ListObservation(executor,observer);

        observers.add(newObservation);
        return new Removable() {
            @Override
            public void remove() {
                observersChangeLock.lock();
                observers.remove(newObservation);
                observersChangeLock.unlock();
            }
        };
    }

    @Override
    public void add(int i, T t) {

        listChangeLock.lock();
        list.add(i,t);

        HashMap<T,PositionChange> positionChanges = new HashMap<>();
        for(int j=i+1; j<list.size(); j++){
            positionChanges.put(list.get(j),new PositionChange(j-1,j));
        }
        listChangeLock.unlock();

        observersChangeLock.lock();
        for(ListObservation observation : observers){
            observation.added(t,i);
            if(positionChanges.size() > 0){
                observation.positionChanged(positionChanges);
            }
        }
        observersChangeLock.unlock();
    }

    @Override
    public T remove(int i) {

        T removedObject = list.remove(i);
        if(removedObject != null){

            HashMap<T,PositionChange> positionChanges = new HashMap<>();

            for(int j=i; j<list.size(); j++){
                positionChanges.put(list.get(j),new PositionChange(j+1,j));
            }

            for(ListObservation<T> observation : observers){
                observation.removed(removedObject,i);
                observation.positionChanged(positionChanges);
            }
        }
        return removedObject;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return addAll(0,collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {

        boolean valueToReturn = list.addAll(i,collection);

        if(valueToReturn == false){
            return false;
        }

        HashMap<T,PositionChange> positionChangesMap = new HashMap<>();
        for(int j = i+collection.size();j < list.size(); j++){
            positionChangesMap.put(list.get(j),new PositionChange(j-collection.size(),j));
        }

        for(ListObservation<T> observation : observers){
            observation.added(collection,i);
            observation.positionChanged(positionChangesMap);
        }

        return valueToReturn;
    }

    public void replace(T objectA,T objectB) throws IllegalArgumentException {
        listChangeLock.lock();

        for(int i=0; i<list.size();i++){
            if(list.get(i).equals( objectA)){

            }

            if(list.get(i).equals( objectB)){

            }
        }

        listChangeLock.unlock();
    }

    public void swap(T objectA,T objectB){
        listChangeLock.lock();

        Integer aIndex = null;
        Integer bIndex = null;

        for(int i=0; i < list.size(); i++){
            if(objectA.equals(list.get(i))){
                aIndex = i;
            }

            if(objectB.equals(list.get(i))){
                bIndex = i;
            }

            if(aIndex != null && bIndex != null){
                break;
            }
        }

        if(aIndex != null && bIndex != null){

            list.remove(objectA);
            list.remove(objectB);

            if(aIndex < bIndex){
                list.add(aIndex,objectB);
                list.add(bIndex,objectA);
            }
            else{
                list.add(bIndex,objectA);
                list.add(aIndex,objectB);
            }

            observersChangeLock.lock();
            for(ListObservation observation : observers){
                observation.swapped(objectA,objectB,aIndex,bIndex);
            }
            observersChangeLock.unlock();

        }
        listChangeLock.unlock();
    }

    public void pushUp(T value) {

        T objectToSwap = null;

        listChangeLock.lock();

        for(int i=0; i<list.size();i++){
            if(value.equals(list.get(i))){
                if(list.size() -1 != i){
                    objectToSwap = list.get(i+1);
                }
                break;
            }
        }
        listChangeLock.unlock();

        if(objectToSwap != null){
            swap(value,objectToSwap);
        }
    }

    public void pushDown(T value){
        T objectToSwap = null;

        listChangeLock.lock();

        for(int i=1; i<list.size();i++){
            if(value.equals(list.get(i))){
                objectToSwap = list.get(i-1);
                break;
            }
        }
        listChangeLock.unlock();

        if(objectToSwap != null){
            swap(value,objectToSwap);
        }
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public void removeRange(int i, int i1) {

        if(i < 0 || i >= list.size()){
            return;
        }

        if(i1 < i || i1 > list.size()){
            return;
        }

        HashMap<T,Integer> removedValuesAndIndexesMap = new HashMap<>();

        for(int j=i; j < i1; j++){
            T tempObject = list.remove(i);
            if(tempObject != null){
                removedValuesAndIndexesMap.put(tempObject,j);
            }
        }

        HashMap<T,PositionChange> positionChangesMap = new HashMap<>();
        for(int j = i+removedValuesAndIndexesMap.size();j < list.size(); j++){
            positionChangesMap.put(list.get(j),new PositionChange(j-removedValuesAndIndexesMap.size(),j));
        }

        for(ListObservation<T> observation : observers){
            observation.removed(removedValuesAndIndexesMap);
            observation.positionChanged(positionChangesMap);
        }

//        super.removeRange(i, i1);
    }

    public List<T> createList(){
        ArrayList<T> tempList = new ArrayList<>();
        listChangeLock.lock();

        for(int i=0; i<list.size(); i++){
            tempList.add(list.get(i));
        }

        listChangeLock.unlock();
        return tempList;
    }

    public ObservableBoolean isListEmpty(){
        return isEmpty;
    }
}
