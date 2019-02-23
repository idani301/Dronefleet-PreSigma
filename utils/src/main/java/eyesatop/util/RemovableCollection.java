package eyesatop.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class RemovableCollection implements Removable {

    private final ArrayList<Removable> removables = new ArrayList<>();

    public RemovableCollection(Removable ... removables) {
        this (Arrays.asList(removables));
    }

    public RemovableCollection(Collection<Removable> removables) {
        for(Removable removable : removables){
            this.removables.add(removable);
        }
    }

    public void add(Removable removable){
        removables.add(removable);
    }

    public void removeRemovable(Removable removable){
        removables.remove(removable);
        removable.remove();
    }

    @Override
    public void remove() {
        for (Removable removable : removables) {
            removable.remove();
        }
        removables.clear();
    }

    public int size(){
        return removables.size();
    }
}
