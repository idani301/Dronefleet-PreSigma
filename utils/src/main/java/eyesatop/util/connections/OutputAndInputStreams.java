package eyesatop.util.connections;

import java.io.BufferedReader;
import java.io.DataOutputStream;

public class OutputAndInputStreams {
    private final DataOutputStream dataOutputStream;
    private final BufferedReader bufferedReader;

    public OutputAndInputStreams(DataOutputStream dataOutputStream, BufferedReader bufferedReader) {
        this.dataOutputStream = dataOutputStream;
        this.bufferedReader = bufferedReader;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }
}
