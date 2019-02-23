//package eyesatop.eyesatop.util.readfromtxtfile;
//
//import java.io.File;
//
//import javax.swing.JFileChooser;
//
//public class FileWindowPick {
//	   public JFileChooser chooser;
//	   public String resultFile = "";
//	   public String resultDir = "";
//	   public File file;
//
//	   public FileWindowPick(String currentDirectoryPath) //only files
//	   {
//		   chooser = new JFileChooser(currentDirectoryPath);
//		   chooser.setDialogTitle("Pick Pictue to Calibrate with");
//
//		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//		    	file = new File("" + chooser.getSelectedFile());
//		    	resultFile = file.getName();
//		    	resultDir = file.getPath();
//		        }
//		      else {
//		        System.err.println("No Selection ");
//		        }
//	   }
//
//	   /*
//	    * type:
//	    * 	0 - files
//	    * 	1 - folders
//	    *
//	    */
//	   public FileWindowPick(String currentDirectoryPath, int type)
//	   {
//		   chooser = new JFileChooser(currentDirectoryPath);
//		   chooser.setDialogTitle("Pick Pictue to Calibrate with");
//		   if (type == 1)
//		   {
//			   chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		   }
//
//
//		   if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//		    	file = new File("" + chooser.getSelectedFile());
//		    	resultFile = file.getName();
//		    	resultDir = file.getPath();
//		        }
//		      else {
//		        System.err.println("No Selection ");
//		        }
//	   }
//}
