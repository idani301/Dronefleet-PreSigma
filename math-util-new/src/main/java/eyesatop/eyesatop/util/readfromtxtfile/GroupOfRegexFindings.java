package eyesatop.eyesatop.util.readfromtxtfile;

import java.util.ArrayList;

/**
 * Created by Einav on 07/05/2017.
 */

public class GroupOfRegexFindings {

    private final ArrayList<String> strings;

    public static final String DOUBLEPATTERN = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
    public static final String DOUBLEPATTERNCOMMET = "([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)";

    public GroupOfRegexFindings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public static String getPatternForNumberOfNumber(char separateBy, int numberOfNumbers){
        String string = "";

        for (int i = 0; i < numberOfNumbers-1; i++) {
            string += "(" + DOUBLEPATTERN + ")" + "\\s*" + separateBy + "\\s*";
        }
        string += "(" + DOUBLEPATTERN + ")";
        return string;
    }

    public String getSpesipicStringElement(int i){
        return strings.get(i);
    }

    public String getTheFirstElement(){
        return strings.get(0);
    }

    public double getTheFirstElementAsDouble(){
        double var = Double.parseDouble(strings.get(0));
        return var;
    }

    public double getElementAsDouble(int i){
        double var = Double.parseDouble(strings.get(i));
        return var;
    }

    @Override
    public String toString() {
        return "GroupOfRegexFindings{" +
                "strings=" + strings +
                '}';
    }
}
