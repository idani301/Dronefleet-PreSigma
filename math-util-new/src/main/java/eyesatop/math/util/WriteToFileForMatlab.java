package eyesatop.math.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eyesatop.math.MathException;

/**
 * Created by Einav on 23/11/2017.
 */

public class WriteToFileForMatlab {

    private final String folder = "E:\\matlab\\";
    private final File fileName;

    public WriteToFileForMatlab(String fileName) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy--kk-mm-ss");
        String fullFileName = folder + fileName + "_" + format.format(cal.getTime()) + ".csv";
        this.fileName = new File(fullFileName);
    }

    public void printToFile(List<List<Double>> list) throws IOException, MathException {
        FileWriter fileWriter = new FileWriter(fileName);
        for (int i = 0; i < list.get(0).size(); i++) {
            for (int j = 0; j < list.size()-1; j++) {
                fileWriter.append(list.get(j).get(i) + ",");
            }
            fileWriter.append(list.get(list.size()-1).get(i) + "\r\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

    public void printToFile(ArrayList<Double> x) throws IOException, MathException {
        List<List<Double>> lists = new ArrayList<>();
        lists.add(x);
        printToFile(lists);
    }

    public void printToFile(List<Double> x, List<Double> y) throws IOException, MathException {
        List<List<Double>> lists = new ArrayList<>();
        lists.add(x);
        lists.add(y);
        printToFile(lists);
    }

    public void printToFile(List<Double> x, List<Double> y, List<Double> z) throws IOException, MathException {
        List<List<Double>> lists = new ArrayList<>();
        lists.add(x);
        lists.add(y);
        lists.add(z);
        printToFile(lists);
    }

    public void printToFile(List<Double> x, List<Double> y, List<Double> z, List<Double> w) throws IOException, MathException {
        List<List<Double>> lists = new ArrayList<>();
        lists.add(x);
        lists.add(y);
        lists.add(z);
        lists.add(w);
        printToFile(lists);
    }
}
