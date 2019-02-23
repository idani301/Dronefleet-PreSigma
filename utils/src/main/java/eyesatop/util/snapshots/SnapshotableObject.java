package eyesatop.util.snapshots;

/**
 * Created by Idan on 17/12/2017.
 */

public interface SnapshotableObject<T> {
    SnapshotObject<T> createSnapshot();
    void restoreFromSnapshot(SnapshotObject<T> snapshot);
}
