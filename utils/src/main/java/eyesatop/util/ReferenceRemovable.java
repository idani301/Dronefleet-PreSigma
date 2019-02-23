package eyesatop.util;

public class ReferenceRemovable implements Removable {

    private Object o;

    public ReferenceRemovable(Object o) {
        this.o = o;
    }

    @Override
    public void remove() {
        o = null;
    }
}
