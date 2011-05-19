package polarity;

import java.io.BufferedReader;
import java.io.FileReader;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.BufferedWriter;
import java.io.FileWriter;




public class postagger
{
	
	public postagger(String fname)
        {
		try{
			MaxentTagger tagger = new MaxentTagger("src/models/left3words-wsj-0-18.tagger");
			BufferedReader stdInput = new BufferedReader(new FileReader("src/data/"+fname));//FROM THE GIVEN PATH READS THE DATA
			String s = null;
                        String taggedString = "";
			while ((s = stdInput.readLine()) != null){
				int last = s.lastIndexOf(".");
				int  l = s.length();
				if(last != l-1)
					s += ".";
                                taggedString+= tagger.tagString(s)+"\n";
			}
                        writeDataFile("src/data/"+fname+".out",taggedString);
		}
		catch(Exception e){}
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

}
