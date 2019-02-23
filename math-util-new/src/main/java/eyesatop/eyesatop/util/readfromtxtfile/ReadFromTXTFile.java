package eyesatop.eyesatop.util.readfromtxtfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Einav on 07/05/2017.
 */

public class ReadFromTXTFile {

    private BufferedReader bufferedReader;

    public ReadFromTXTFile(File file) {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readAllLineToArrayString(){
        ArrayList<String> strings = new ArrayList<>();
        String line = null;
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        strings.add(line);
        while (true){
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line != null){
                strings.add(line);
            }
            else {
                break;
            }
        }

        return strings;
    }

    public ArrayList<GroupOfRegexFindings> getRegexFromTXTFile(String pattern, ArrayList<String> allLines){
        ArrayList<GroupOfRegexFindings> groupOfRegexFindingses = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        for (int i = 0; i < allLines.size(); i++) {
            Matcher matcher = r.matcher(allLines.get(i));
            strings = new ArrayList<>();
            while (matcher.find()){
                if(matcher.groupCount() > 1) {
                    for (int j = 1; j <= matcher.groupCount(); j++) {
                        if(matcher.group(j) != null)
                            strings.add(matcher.group(j));
                    }
                }
                else {
                    strings.add(matcher.group(1));
                }
            }
            if (strings.size() > 0)
                groupOfRegexFindingses.add(new GroupOfRegexFindings(strings));

        }
        return groupOfRegexFindingses;
    }

    public ArrayList<ArrayList<GroupOfRegexFindings>> getAllRegexFromTxtFile(ArrayList<String> regex){

        ArrayList<String> strings = readAllLineToArrayString();
        ArrayList<ArrayList<GroupOfRegexFindings>> arrayListsOfStrings = new ArrayList<>();

        for (int i = 0; i < regex.size(); i++){
            ArrayList<GroupOfRegexFindings> groupOfRegexFindingses = getRegexFromTXTFile(regex.get(i),strings);
            if (groupOfRegexFindingses.size() > 0)
                arrayListsOfStrings.add(groupOfRegexFindingses);
        }

        return arrayListsOfStrings;
    }

    public void close() throws IOException {
        bufferedReader.close();

    }


}
