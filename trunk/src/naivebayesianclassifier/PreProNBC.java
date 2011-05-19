/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package naivebayesianclassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Nilay
 *
 * a=adjective
 * n=noun
 * r=adverb
 * v=verb
 *
 * Refer this link "http://www.computing.dcu.ie/~acahill/tagset.html" for tag set.
 */
public class PreProNBC {
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Usage: accuracy_garmin_nuvi_255W_gps.txt.data");
            System.exit(0);
        }
        String[] negWords = {"not","never","n't","doesn't","cannot","can't","cannt","nor","don't","dont","no","wouldn't","shouldn't","couldn't","ain't"};
        String[] contextshifters = {"but","expect","however","only","although","though","while","whereas","although","despite","would","should","miss","refused","assumed","hard","harder","less"};
       
        ArrayList<String> line = readDataFile("src/data/"+args[0]+".out"); //Read File specified in args[0] Data file name
        ArrayList<String> origline = readDataFile("src/data/"+args[0]+".gold"); //Read File specified in args[0] Data file name
       // ArrayList<String> features = readDataFile("src/data/"+args[0]+".ft"); //Read File specified in args[0] Data file name
         ArrayList<String> AdjList = new  ArrayList<String>();
        ArrayList<String> negList = new  ArrayList<String>();
        ArrayList<String> CSList = new  ArrayList<String>();
        ArrayList<String> AdvList = new  ArrayList<String>();
        ArrayList<String> res = new  ArrayList<String>();
        ArrayList<String> finalres = new  ArrayList<String>();
        int maxAdj = 0;
        int maxCS = 0;
        int maxAdv = 0;
        int maxNeg = 0;
        for(int i=0;i<line.size();i++){
            //System.out.println(line.get(i));
            String adj="";
            int countAdj = 0;
            int countCS = 0;
            int countAdv = 0;
            int countNeg = 0;
            String[] wordwithTag = line.get(i).split("\\s");    //Split to get word with tags
           
               for(int j=0;j<wordwithTag.length;j++){
                    String[] word = wordwithTag[j].split("/");
                    if(word[1].contains("JJ") && word.length>1){
                       adj+=word[0].toLowerCase()+",";
                       countAdj++;
                    }
               }
           if(maxAdj<countAdj)
               maxAdj=countAdj;
                
        
            // Check Negation of the adjective
            String neg="";
              for(int j=0;j<wordwithTag.length;j++){
                            for(int l=0;l<negWords.length;l++){
                                if(wordwithTag[j].split("/")[0].equalsIgnoreCase(negWords[l])){
                                    neg+=negWords[l]+",";
                                }
                            }
                        }
             if(maxNeg<countNeg)
               maxNeg=countNeg;
          //Context Shifter
            String contextShifters="";
            for(int j=0;j<wordwithTag.length;j++){
                String[] word = wordwithTag[j].split("/");      //Split to get Tags
                    for(int k=0;k<contextshifters.length;k++){
                        if(contextshifters[k].equalsIgnoreCase(word[0])){
                            if(word[1].contains("JJ") && word.length>1)                     //Check if the word tag is Noun
                                contextShifters+=word[0].toLowerCase()+",";     //To note-j is the position of the feature
                            else if(word[1].contains("NN") && word.length > 1)
                                contextShifters+=word[0].toLowerCase()+",";
                            else if(word[1].contains("RB") && word.length > 1)
                                contextShifters+=word[0].toLowerCase()+",";
                            else if(word[1].contains("VB") && word.length > 1)
                                contextShifters+=word[0].toLowerCase()+",";
                            else
                                contextShifters+=word[0].toLowerCase()+",";
                            countCS++;
                        }
                    }
                }
             if(maxCS<countCS)
               maxCS=countCS;
             //Adverbs
            String adverbs="";
            for(int j=0;j<wordwithTag.length;j++){
                String[] word = wordwithTag[j].split("/");      //Split to get Tags
               if(word[1].contains("RB") && word.length > 1)
                    adverbs+=word[0].toLowerCase()+",";
                    countAdv++;
                }
                if(maxAdv<countAdv)
               maxAdv=countAdv;
            CSList.add(contextShifters);
            AdvList.add(adverbs);
            negList.add(neg);
            AdjList.add(adj);
            finalres.add(adj+adverbs+contextShifters+neg+origline.get(i).split("\\s")[0]);
        }
       
        //writeDataFile("src/data/"+args[0]+".arff",printArrayList(finalres));
        //System.out.println("Output redirected to src/data/"+args[0]+".arff");
        System.out.println("Preprocessing Done!!");
        NaiveBayCla nbc = new NaiveBayCla();
        String[] arguments={args[0]+".arff","10","random"};
        NaiveBayCla.main(arguments);

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

     public static String printArrayList(ArrayList<String> str){
         String res="";
         for(int i=0;i<str.size();i++){
             System.out.println(str.get(i));
             res+=str.get(i)+"\n";
         }
         return res;
     }
}
