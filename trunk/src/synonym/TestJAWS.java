package synonym;

import java.util.Set;
import java.util.TreeMap;

import edu.smu.tspell.wordnet.*;

/**
 * Displays word forms and definitions for synsets containing the word form
 * specified on the command line. To use this application, specify the word
 * form that you wish to view synsets for, as in the following example which
 * displays all synsets containing the word form "airplane":
 * <br>
 * java TestJAWS airplane
 */
public class TestJAWS
{
	public static String synonyms(String word)
	{
		System.setProperty("wordnet.database.dir", "C:\\Program Files\\WordNet\\2.1\\dict");
		String wordForm = word;
		//  Get the synsets containing the wrod form
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		SynsetType adj = null;
		Synset[] synsets = database.getSynsets(wordForm,adj);
		//  Display the word forms and definitions for synsets retrieved
		TreeMap<String,Integer> tmm = new TreeMap<String,Integer>();
		if (synsets.length > 0)
		{
			//System.out.println("The following synsets contain '" +
					//wordForm + "' or a possible base form " +
					//"of that text:");
			
			int value1 = 0;
			for (int i = 0; i < synsets.length; i++)
			{
				//System.out.println("");
				String[] wordForms = synsets[i].getWordForms();
				for (int j = 0; j < wordForms.length; j++)
				{
					//System.out.print((j > 0 ? "," : "") +
							//wordForms[j]);
					String key  = wordForms[j];
					//System.out.println(key);
					int count1 = 0;
					if(tmm.containsKey(key))
					{
						value1 = tmm.get(key);
						tmm.remove(key);
						value1++;
						tmm.put(key, value1);
						
					}
					else
					{
						count1++;
						tmm.put(key,count1);
					}
				}
				//System.out.println(": " + synsets[i].getDefinition());
			}
		}
		else
		{
			//System.err.println("No synsets exist that contain " +
					//"the word form '" + wordForm + "'");
		}
		
		Set a1 = tmm.keySet();
		int leng = tmm.size();
		String[] abcde = new String[leng];
		Object[] objj = new Object[leng];
	
		//abc = aa.toArray(abc);
		objj = a1.toArray();
	
		for(int k2= 0; k2 < leng; k2++)
		{
			abcde[k2] = objj[k2].toString();
		}
		String res=word;
		for(int i=0;i<abcde.length;i++)
		{
			//System.out.print(abcde[i]+",");
                        res+=","+abcde[i];
		}
             return res;
	}

	/**
	 * Main entry point. The command-line arguments are concatenated together
	 * (separated by spaces) and used as the word form to look up.
	 */
	public static void main(String[] args)
	{
		synonyms("sweet");
		/*
		{
			System.err.println("You must specify " +
					"a word form for which to retrieve synsets.");
		}*/
	}

}