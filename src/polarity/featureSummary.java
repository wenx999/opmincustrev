package polarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.stanford.nlp.util.ArrayUtils;

//import org.apache.commons.lang.*;


public class featureSummary {
	private static int wordCount;
	/**
	 * @param args
	 */
	private static ArrayList<String> inputList = null;
	private static Hashtable<String,ArrayList<Integer>> featureCount;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
            ArrayList<String> features = readDataFile("src/data/"+args[0]+".ft");
            	String ft = features.toString();
                ft=ft.replace("[", "");
                ft=ft.replace("]", "");
                String[] stopWords = ft.split(",");
                inputList = new ArrayList<String>();
		ArrayList<Integer> counts = null;
		//input file
		File inputFile = new File("src/data/"+args[0]+".res");
		BufferedReader buffReader;
		try {
			buffReader = new BufferedReader( new FileReader(inputFile));
			String str = "";
			//stop words array
			
			while( (str = buffReader.readLine()) != null){
				
				inputList.add(str);
			}
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		featureCount = new Hashtable<String, ArrayList<Integer>>();
		
		for(int i =0;i<stopWords.length;i++){
			int positiveCount = 0;
			int negativeCount = 0;
			int neutralCount = 0;
			
			for(String str:inputList)
			{
				if(str.contains("Positive") && str.contains(stopWords[i])){
					positiveCount++;
				}
				if(str.contains("Negative") && str.contains(stopWords[i])){
					negativeCount++;
				}
				if(str.contains("Neutral") && str.contains(stopWords[i])){
					neutralCount++;
				}
				
			}
			counts = new ArrayList<Integer>();
			counts.add(positiveCount);
			counts.add(negativeCount);
			counts.add(neutralCount);
			featureCount.put(stopWords[i], counts);
			
			
		}
		
		//System.out.println(featureCount);


		
		//output file
		/*FileWriter outputData;
		try {
			outputData = new FileWriter("src/data/"+args[0]+".sum");
			BufferedWriter out = new BufferedWriter(outputData);
			 Enumeration e = featureCount.keys();
			    
			    //iterate through Hashtable keys Enumeration
			    while(e.hasMoreElements()){
			    	String str = "";
			    	String key =(String) e.nextElement();
			    	ArrayList<Integer> values = featureCount.get(key);
			    	
			    	str += key + ",";
			    	for(Integer val:values){
			    		
			    		str += Integer.toString(val) + ",";
			    		
			    	}
			    	
			    	out.write(str);
                               	out.write("\r\n");
			    	

                                //Spitting the output
                                 System.out.println(str);

			    }
			    
			    out.close();*/

                FileWriter outputData;
		try {
			outputData = new FileWriter("src/data/"+args[0]+".sum");
			BufferedWriter out = new BufferedWriter(outputData);
			 Enumeration e = featureCount.keys();

			    //iterate through Hashtable keys Enumeration
			    while(e.hasMoreElements()){
			    	String str = "";
			    	String key =(String) e.nextElement();
			    	ArrayList<Integer> values = featureCount.get(key);

			    	str += key + ",";
			    	for(Integer val:values){

                                    str += Integer.toString(val) + ",";

			    	}

			    	//out.write(str);
                               	//out.write("\r\n");
			    	String[] val = str.split(",");
			    	int val1 = Integer.parseInt(val[1]);
                                int val2 = Integer.parseInt(val[2]);
                                int val3 = Integer.parseInt(val[3]);

                                if(val1>=val2 && val1>=val3)
                                    str = val[0] + ": Positive -> [" + val[1] +","+ val[2] +","+ val[3] +"]";

                                if(val2>=val1 && val2>=val3)
                                    str = val[0] + ": Negative -> [" + val[1] +","+ val[2] +","+ val[3] +"]";

                                if(val3>=val1 && val3>=val2)
                                    str = val[0] + ": Neutral -> [" + val[1] +","+ val[2] +","+ val[3] +"]";

                                out.write(str);
                               	out.write("\r\n");
                                    //Spitting the output
                                 System.out.println(str);

			    }

			    out.close();
			    
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
//	    for(int a=0;a<result.size();a++){
//	      	out.write(result.get(a));
//	       	out.write("\r\n");
//	    } 	
//		out.close();
	    
	   
	     
		
		
	}
  public static void writeDataFile(String fname, String res){
          try{
        // Create file
        FileWriter fstream = new FileWriter(fname);
            BufferedWriter out = new BufferedWriter(fstream);
        out.write(res);
        //Close the output stream
        out.close();
        }catch (Exception e){//Catch exception if any
          System.err.println("Error: " + e.getMessage());
        }
    }

   public static ArrayList<String> readDataFile(String fname){
        String line;
        ArrayList file = new ArrayList();
        try{
            BufferedReader in = new BufferedReader(new FileReader(fname));
            while ((line = in.readLine()) != null){
                line = line.trim();   //Trim the line to remove leading spaces
                file.add(line);
              }
            in.close();
        }
        catch (IOException e){
            System.out.println(e);
            return null;
        }
        return file;
    }

    public static String printArrayList(ArrayList<String> str){
         String res="";
         for(int i=0;i<str.size();i++){
             System.out.println(str.get(i));
             res+=str.get(i)+"\n";
         }
         return res;
     }
}
