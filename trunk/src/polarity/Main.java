/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package polarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
public class Main {
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Usage: accuracy_garmin_nuvi_255W_gps.txt.data");
            System.exit(0);
        }

        postagger ptagger = new postagger(args[0]);
        SWN3 swn = new SWN3();

        ArrayList<String> line = readDataFile("src/data/"+args[0]+".out"); //Read File specified in args[0] Data file name
        ArrayList<String> origline = readDataFile("src/data/"+args[0]); //Read File specified in args[0] Data file name
        ArrayList<String> features = readDataFile("src/data/"+args[0]+".ft"); //Read File specified in args[0] Data file name
        ArrayList<String> intmresline = new  ArrayList<String>();
        ArrayList<String> res = new  ArrayList<String>();
        String[] negWords = {"not","never","n't","doesn't","cannot","can't","cannt","nor","don't","dont","no","wouldn't","shouldn't","couldn't","ain't"};
        String[] contextshifters = {"but","expect","however","only","although","though","while","whereas","although","despite","would","should","miss","refused","assumed","hard","harder","less"};

        for(int i=0;i<line.size();i++){
            //System.out.println(line.get(i));
            String sentFeat="";
            String[] wordwithTag = line.get(i).split("\\s");    //Split to get word with tags
            for(int j=0;j<wordwithTag.length;j++){
                String[] word = wordwithTag[j].split("/");      //Split to get Tags
                if(word[1].contains("NN") && word.length>1){                     //Check if the word tag is Noun
                    String[] feature = features.get(0).toLowerCase().split(",");
                    for(int k=0;k<feature.length;k++){
                        if(feature[k].equalsIgnoreCase(word[0])){
                            sentFeat+=word[0]+"="+j+";";     //To note-j is the position of the feature
                            break;
                        }
                    }
                }
            }

            if(sentFeat.equals(""))
                sentFeat = args[0]+"=-1;"; //Put Product Name here


            //Looking for Adjective of Noun
            String OW = "";
            String[] totFeat = sentFeat.split(";");
            for(int k=0;k<totFeat.length;k++){
                
                for(int j=0;j<wordwithTag.length;j++){
                    String[] wordPos = totFeat[k].split("=");
                    int pos = Integer.parseInt(wordPos[1]);
                    if((j>=pos-5 && j<=pos+5) || pos==-1){   //pos -1 if Product name is explicitly now mentioned.
                        String[] word = wordwithTag[j].split("/");
                        if(word[1].contains("JJ") && word.length>1){
                            String str = word[0]+":"+j+"="+swn.extract(word[0].toLowerCase(), "a")+";";
                            if(!OW.equalsIgnoreCase(str))
                            OW+=str; // To calculate semantic orientation of the Adjective
                            
                        }
                    }
                }

                //Repeat search for Adjective if you do not find any adjective in the restricted search
                if(OW.equals("")){
                 for(int j=0;j<wordwithTag.length;j++){
                        String[] word = wordwithTag[j].split("/");
                        if(word[1].contains("JJ") && word.length>1){
                             String str = word[0]+":"+j+"="+swn.extract(word[0].toLowerCase(), "a")+";";
                            if(!OW.equalsIgnoreCase(str))
                            OW+=str; // To calculate semantic orientation of the Adjective
                        }
                    }
                }

            }
            
            // Check Negation of the adjective
             String NcheckOW = "";
            if(!OW.equals("") ){
               
                String[] totOW = OW.split(";");
                for(int k=0;k<totOW.length;k++){
                    String[] wordPos = totOW[k].split(":")[1].split("=");
                    for(int j=0;j<wordwithTag.length;j++){
                        int pos = Integer.parseInt(wordPos[0]);
                        if(j>=pos-3 && j<=pos+3){   //pos -1 if Product name is explicitly now mentioned.
                            for(int l=0;l<negWords.length;l++){
                                if(wordwithTag[j].split("/")[0].equalsIgnoreCase(negWords[l])){
                                    String[] origStr = totOW[k].split("=");
                                    Double word = Double.parseDouble(origStr[1])*-1.0;
                                    totOW[k] = origStr[0]+"="+word;
                                }
                            }
                        }
                    }
                NcheckOW += totOW[k]+";";
                }

           }

            //Context Shifter
            String contextShifters="";
            for(int j=0;j<wordwithTag.length;j++){
                String[] word = wordwithTag[j].split("/");      //Split to get Tags
                    for(int k=0;k<contextshifters.length;k++){
                        if(contextshifters[k].equalsIgnoreCase(word[0])){
                            if(word[1].contains("JJ") && word.length>1)                     //Check if the word tag is Noun
                                contextShifters+=word[0]+"="+swn.extract(word[0].toLowerCase(), "a")+";";     //To note-j is the position of the feature
                            else if(word[1].contains("NN") && word.length > 1)
                                contextShifters+=word[0]+"="+swn.extract(word[0].toLowerCase(), "n")+";";
                            else if(word[1].contains("RB") && word.length > 1)
                                contextShifters+=word[0]+"="+swn.extract(word[0].toLowerCase(), "r")+";";
                            else if(word[1].contains("VB") && word.length > 1)
                                contextShifters+=word[0]+"="+swn.extract(word[0].toLowerCase(), "v")+";";
                            else
                                contextShifters+=word[0]+"=0;";
                            
                        }
                    }
                }

             //Adverbs
            String adverbs="";
            for(int j=0;j<wordwithTag.length;j++){
                String[] word = wordwithTag[j].split("/");      //Split to get Tags
               if(word[1].contains("RB") && word.length > 1)
                    adverbs+=word[0]+"="+swn.extract(word[0].toLowerCase(), "r")+";";
                }
            //Calculate Average
            String calcavgof = adverbs+contextShifters+NcheckOW;
            String[] feat = calcavgof.split(";");
            Double sum=0.0;
            for(int j=0;j<feat.length;j++){
                try{
                 sum+= Double.parseDouble(feat[j].split("=")[1]);
                }
                catch(Exception e){
                  sum+= 0.0;
                }
            }
            Double avg =sum/feat.length;
            String polarity = "Neutral";
            if(avg>0)
                polarity = "Positive";
            else if(avg<0)
                polarity = "Negative";
            intmresline.add(polarity+" || "+adverbs+" || "+contextShifters+" || "+NcheckOW+" || "+OW+" || "+sentFeat+" || "+origline.get(i).toString());
            res.add(polarity+" || "+origline.get(i).toString());
        }
        //printArrayList(intmresline);
        writeDataFile("src/data/"+args[0]+".res",printArrayList(res));
        System.out.println("Output redirected to src/data/"+args[0]+".res");
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
