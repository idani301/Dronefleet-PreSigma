package eyesatop.util.android.logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * General logger for all my projects.
 * Able to write messages, close and reopen the file when needed.
 * The close and reopen methods mainly were created in order for the onPause and onResume methods
 * of android activities.
 */
public class GeneralLogger {

    PrintWriter printer;
    File root;
    File filepath;
    String logger_directory;
    String log_file_name;

    /**
     * This function will create new log file.
     * if the wanted log file is already exists, we will add _<number> for the name until
     * we will find a match.
     * @throws IOException
     */
    private void create_new_file() throws IOException {

        boolean is_done = false;

        if(!root.exists()) {
            root.mkdirs();
        }

        int counter = 1;
        String temp_log_file_name = log_file_name;

        filepath = new File(root,log_file_name + ".txt");

        if(!filepath.exists()){
            filepath.createNewFile();
            is_done = true;
        }

        while(is_done == false){

            temp_log_file_name = log_file_name + "_"+ counter;
            filepath = new File(root,temp_log_file_name + ".txt");

            if(!filepath.exists()){
                filepath.createNewFile();
                is_done = true;
                this.log_file_name = temp_log_file_name;
            }
            counter++;
        }

        printer = new PrintWriter(new FileWriter(filepath, true));
        printer.append("Created file \n");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //get current date time with Date()
        Date date = new Date();
        printer.append("Created at " + dateFormat.format(date) + "\n");
    }

    private void create_if_not_exists() throws IOException {

        if(!root.exists()) {
            root.mkdirs();
        }

        if(!filepath.exists()){
            filepath.createNewFile();
        }
    }

    public GeneralLogger(String logger_directory, String log_file_name) throws IOException {

        this.log_file_name = log_file_name;
        this.logger_directory = logger_directory;

        root = new File(logger_directory);
        filepath = new File(root,log_file_name);

        try {
            create_new_file();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close_log() {
        printer.close();
    }
    public String get_log_path(){
        return root + "/" + log_file_name  + ".txt";
    }
    public void reopen_log() throws IOException {

        filepath = new File(root,log_file_name + ".txt");

        try {
            printer = new PrintWriter(new FileWriter(filepath, true));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void flush(){
        try {
            printer.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void write_message(String message){
        printer.append(message + "\n");
    }
}
