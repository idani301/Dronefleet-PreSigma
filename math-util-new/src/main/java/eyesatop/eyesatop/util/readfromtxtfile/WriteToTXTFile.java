//package eyesatop.eyesatop.util.readfromtxtfile;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * Created by Einav on 14/06/2017.
// */
//
//public class WriteToTXTFile {
//
//    private final File file;
//    private final boolean isOverride;
//
//    public WriteToTXTFile(File file, boolean isOverride){
//        this.file = file;
//        this.isOverride = isOverride;
//
//        if (isOverride && file.getName().contains(".txt")){
//            if (file.exists()){
//                file.delete();
//            }
//            createNewTXTFile();
//        }
//    }
//
//    public static WriteToTXTFile writeToTXTFileWithPickDialog(String startingPath, String fileName, boolean isOverride){
//        FileWindowPick fileWindowPick = new FileWindowPick(startingPath,1);
//        File file = new File(fileWindowPick.file.getAbsoluteFile() + "\\" + fileName + ".txt");
//        return new WriteToTXTFile(file,isOverride);
//    }
//
//    private boolean createNewTXTFile(){
//        if (!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                System.err.println("Couldn't write new file in this path");
//            }
//            return true;
//        }
//        return false;
//    }
//
//    public void printlnToTXTFile(String Str){
//        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file,true))))
//        {
//            out.println(Str);
//        }catch(IOException e){
//            System.err.println(e);
//        }
//    }
//
//    public void printToTXTFile(String Str){
//        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file,true))))
//        {
//            out.print(Str);
//        }catch(IOException e){
//            System.err.println(e);
//        }
//    }
//
//    public void printlnToTXTFile(Object object){
//        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file,true))))
//        {
//            out.println(object.toString());
//        }catch(IOException e){
//            System.err.println(e);
//        }
//    }
//
//    public void printToTXTFile(Object object){
//        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file,true))))
//        {
//            out.print(object.toString());
//        }catch(IOException e){
//            System.err.println(e);
//        }
//    }
//
//    public File getFile() {
//        return file;
//    }
//
//    public boolean isOverride() {
//        return isOverride;
//    }
//}
