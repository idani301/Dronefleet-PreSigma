package eyesatop.imageprocessing;

public class LoadOpenCVDLL {
	
	public LoadOpenCVDLL()
	{
		if(System.getProperty("java.vm.name").contains("64"))
		{
			new LoadOpenCV64bitDLL();
			System.out.println("64 bit");
		}
		else
		{
			new LoadOpenCV32bitDLL();
			System.out.println("32 bit");
		}
	}
}
