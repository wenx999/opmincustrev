/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package naivebayesianclassifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import synonym.TestJAWS;

/**
 *
 * @author Nilay
 */
public class NaiveBayCla {
    private int k;                      //For k-fold Cross Validation
    private DecimalFormat Pre8Form;
    private boolean random;
    private int startergyConti;
    private int startergyDisc;

    public NaiveBayCla() {}

    public NaiveBayCla(int k,String random) {
        this.k = k;
        this.Pre8Form = new DecimalFormat("#.########");
        if(random.equals("random")){
            this.random = true;
        }
        else
            this.random = false;
        this.startergyConti = startergyConti;
        this.startergyDisc = startergyDisc;
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //args[0]="crx.data";              //File
        //args[1]="10";                    //For k-fold Cross Validation
        //args[2]="random";                //For k-fold Cross Validation
        //args[3]="1";                     //Startergy select for Continuous Data
        //args[4]="2";                     //Startergy select for Discrete Data
        if(args.length != 3){
            System.out.println("Need 5 arguments:-\nFor Example arguments to be passed cabe be \n\"crx.data 10 random 1 2\"\n or \n \"crx.data 5 notrandom 2 1\"");
            System.exit(0);
        }
        NaiveBayCla nbc = new NaiveBayCla(Integer.parseInt(args[1]),args[2]); //args[1] will have value of 'k' from user which is converted to integer and sent to constructor
        ArrayList<String> ars = nbc.readDataFile("src/data/"+args[0]); //Read File specified in args[0] Data file name
        Iterator iter = ars.iterator();         //Iterator for arraylist to read lines

        int numOfAttr;
        int numOfInstances;
        String[][] A;                     //Matrix to store file data
        ArrayList indexOfRealAttri=new ArrayList();

        if(iter.hasNext()){               //Make sure file is not empty!!
           /* String[] firstLine = iter.next().toString().split(",");
            numOfAttr = firstLine.length;*/
            numOfInstances=ars.size();
          /*  A = new String[numOfAttr][numOfInstances];  //Instantiating the Matrix


            for(int i=0;i<firstLine.length;i++)
                  A[i][0]=firstLine[i];

            int j=1;
            while (iter.hasNext()) {
                String[] line = iter.next().toString().split(",");
                for(int i=0;i<line.length;i++)
                    A[i][j]=line[i];                   //Filling in the Matrix
                j++;
           }*/

//            A=nbc.fillInMissingValues(A);               //Data Cleaning - Data PreProcessing
            System.out.println("=== Run information ===");
            System.out.println("Scheme:       Naive Bayesian");
            System.out.println("Relation:     "+args[0].split("\\.")[0]);
            System.out.println("Instances:    "+numOfInstances);
           // System.out.println("Attributes:   "+numOfAttr);
           // for(int i=0;i<numOfAttr;i++)
           //     System.out.println("              A"+i);
            System.out.println("Test mode:    "+nbc.k+"-fold cross-validation");
            System.out.println("\n=== Classifier model (full training set) ===\n");
            //String[] classifiers = nbc.identifySetOfValuesFromAttri(A[numOfAttr-1]); //Identify Classifiers
           double finalmae=0.0;
            double finalcorrect=0.0;
           double finalincorrect=0.0;
          double finalcorrecres=0.0;
          double finalincorrecres=0.0;
          for(int t=0;t<nbc.k;t++){
            //Separate Training and Testing set
            ArrayList<String> trainars = new ArrayList<String>();
            ArrayList<String> testars = new ArrayList<String>();
            if(nbc.random)
                Collections.shuffle(ars);
            int min=0;
            int max = ars.size()/10;
            for(int i=0;i<ars.size();i++){
                    if(i>=min && i<max ){
                       testars.add((String) ars.get(i));
                    }
                    else{
                        trainars.add((String) ars.get(i));
                    }
                }
            //Count classifiers
            String[] classifiers = new String[trainars.size()];
            int countClassPos=0,countClassNeg=0,countClassNeu=0;
            for(int i=0;i<trainars.size();i++){
                String[] line= trainars.get(i).split(",");
                classifiers[i] = line[line.length-1];
                if(classifiers[i].equalsIgnoreCase("Positive"))
                    countClassPos++;
                else if(classifiers[i].equalsIgnoreCase("Negative"))
                    countClassNeg++;
                else if(classifiers[i].equalsIgnoreCase("Neutral"))
                    countClassNeu++;
            }

            //System.out.println("Pos="+countClassPos+"Neg="+countClassNeg+"Neu="+countClassNeu);
            String[] resArr = new String[testars.size()];
            for(int i=0;i<testars.size();i++){
                String[] attr = testars.get(i).split(",");
                double[][][] countArr = new double[3][attr.length][testars.size()];
                double[] Pxc = {1.0,1.0,1.0};
                for(int j=0;j<attr.length;j++){
                    String[] attribute = TestJAWS.synonyms(attr[j].toLowerCase()).split(",");
                    int cPos=0,cNeg=0,cNeu=0;
                    for(int k=0;k<trainars.size();k++){
                        for(int a=0;a<attribute.length;a++)
                            if(trainars.get(k).toLowerCase().contains(attribute[a])){
                                if(trainars.get(k).toLowerCase().contains("positive"))
                                     cPos++;
                                else if(trainars.get(k).toLowerCase().contains("negative"))
                                    cNeg++;
                                else if(trainars.get(k).toLowerCase().contains("neutral"))
                                    cNeu++;
                                a=attribute.length;
                            }
                        
                    }
                    //1 is added to avoid getting zero probability - Laplace
                    countArr[0][j][i] = (double)(cPos+1)/(double)(countClassPos+1);
                    countArr[1][j][i] = (double)(cNeg+1)/(double)(countClassNeg+1);
                    countArr[2][j][i] = (double)(cNeu+1)/(double)(countClassNeu+1);
                    //System.out.println("Pxc[0]="+countArr[0][j][i]+"Pxc[1]="+countArr[1][j][i]+"Pxc[2]="+countArr[2][j][i]);

                    Pxc[0] *= (countArr[0][j][i]);
                    Pxc[1] *= (countArr[1][j][i]);
                    Pxc[2] *= (countArr[2][j][i]);
                }

               //System.out.println("Pxc[0]="+Pxc[0]+"Pxc[1]="+Pxc[1]+"Pxc[2]="+Pxc[2]);
                
               if(Pxc[0]>Pxc[1] && Pxc[0]>Pxc[1])
                   //System.out.println(testars.get(i)+"=Positive");
                   resArr[i]="Positive";
               else if (Pxc[1] > Pxc[0] && Pxc[1] > Pxc[2])
                  // System.out.println(testars.get(i)+"=Negative");
                   resArr[i]="Negative";
               else if (Pxc[2] > Pxc[0] && Pxc[2] > Pxc[1])
                  // System.out.println(testars.get(i)+"=Neutral");
                   resArr[i]="Neutral";

               
             }
             //Accuracy
            int correct=0;
            int incorrect = 0;
                for(int j=0;j<testars.size();j++){
                    String[] attr=testars.get(j).split(",");
                    if(attr[attr.length-1].equalsIgnoreCase(resArr[j]))
                        correct++;
                    else
                        incorrect++;
                }

            double correcres = (double)correct*100/testars.size();
            double incorrecres = (double)100.0-correcres;
            double mae = (double)incorrect/testars.size();

            finalcorrect += correct;
            finalincorrect += testars.size()-correct;
            finalcorrecres += correcres;
            finalincorrecres += incorrecres;
            finalmae += mae;
            }
            finalmae =finalmae/nbc.k;
            finalcorrect = finalcorrect/nbc.k;
            finalincorrect = finalincorrect/nbc.k;
            finalcorrecres = finalcorrecres/nbc.k;
            finalincorrecres = finalincorrecres/nbc.k;

            System.out.println("Correctly Classified Instances\t"+"\t\t"+nbc.Pre8Form.format(finalcorrecres)+"%");
            System.out.println("Inorrectly Classified Instances\t"+"\t\t"+nbc.Pre8Form.format(finalincorrecres)+"%");
            System.out.println("Mean Absolute Error\t"+nbc.Pre8Form.format(finalmae));
            //System.out.println("Correctly Classified Instances\t"+finalcorrect+"\t\t"+nbc.Pre8Form.format(finalcorrecres)+"%");
            //System.out.println("Inorrectly Classified Instances\t"+finalincorrect+"\t\t"+nbc.Pre8Form.format(finalincorrecres)+"%");

}
        else
        {
            System.out.println("File is empty");
            System.exit(0);
        }
    }


   
public int crossvalidatekfold(ArrayList trainars, ArrayList testars, NaiveBayCla nbc ){
            //Now Find results using training dataset
            HashMap<String,String> P = new HashMap<String,String>();
            //To store Means and Probabilities
            //Find Probablities of Training dataset


            //I have put training Dataset in Main Array A
         Iterator   iter = trainars.iterator();
        if(iter.hasNext()){               //Make sure file is not empty!!
            String[] firstLine = iter.next().toString().split(",");
            int numOfAttr = firstLine.length;
            int numOfInstances=trainars.size();
            String[][] A = new String[numOfAttr][numOfInstances];  //Instantiating the Matrix


            for(int i=0;i<firstLine.length;i++)
                  A[i][0]=firstLine[i];

             int j=1;
            while (iter.hasNext()) {
                String[] line = iter.next().toString().split(",");
                for(int i=0;i<line.length;i++)
                    A[i][j]=line[i];                   //Filling in the Matrix
                j++;
           }
            String[] classifiers = nbc.identifySetOfValuesFromAttri(A[numOfAttr-1]); //Identify Classifiers
            
            for(int i=0;i<classifiers.length;i++){

               String[] indexOfC = nbc.probC(A[numOfAttr-1],classifiers[i]);  //Will return Array with index of identified Classifiers
               String[][] AA = nbc.constClassifierArraySet(A,indexOfC);     //Collecting only those rows which has particular classfier say 'yes' or '+'
               double probC = indexOfC.length/(double)A[numOfAttr-1].length; //Finding Probability of Classifier C
                for(j=0;j<numOfAttr;j++){
                    String[] subAttri = nbc.identifySetOfValuesFromAttri(A[j]); //Identify Set of Values from Particular Attribute
                    
                    for(int k=0;k<subAttri.length;k++){
                        String res = Double.toString(nbc.probXoverC(AA[j],subAttri[k],indexOfC.length));
                        P.put(classifiers[i]+" A"+(j+1)+" "+subAttri[k],classifiers[i]+" "+subAttri[k]+" "+res);   //And put all results in HashMap
                        
                    }
                 
               }
                }
                iter = testars.iterator();
                if(iter.hasNext()){               //Make sure file is not empty!!
                    firstLine = iter.next().toString().split(",");
                    numOfAttr = firstLine.length;
                    numOfInstances=testars.size();
                    A = new String[numOfAttr][numOfInstances];  //Instantiating the Matrix


                    for(int i=0;i<firstLine.length;i++)
                          A[i][0]=firstLine[i];

                     j=1;
                    while (iter.hasNext()) {
                        String[] line = iter.next().toString().split(",");
                        for(int i=0;i<line.length;i++)
                            A[i][j]=line[i];                   //Filling in the Matrix
                        j++;
                   }
                     boolean[] correctlyclassified = new boolean[A[0].length];
                    //A=nbc.fillInMissingValues(A);
                    classifiers = nbc.identifySetOfValuesFromAttri(A[numOfAttr-1]); //Identify Classifiers
                    //P.put(classifiers[i]+" A"+(j+1)+" "+subAttri[k],classifiers[i]+" "+subAttri[k]+" "+res);
                      for(int k=0;k<A[0].length;k++){
                          double[] prob = new double[classifiers.length];
                            for(int i=0;i<classifiers.length;i++){
                               prob[i] = 1.0;
                              for(j=0;j<numOfAttr-1;j++){

                                          try{
                                          String[] res = P.get(classifiers[i]+" A"+(j+1)+" "+A[j][k]).split("\\s");
                                           //System.out.println(classifiers[i]+" A"+(j+1)+" "+A[j][k]+" "+res[2]);
                                           prob[i] = prob[i] * Double.parseDouble(res[2].trim());
                                          }
                                          catch(Exception e){

                                          }
                                }

                            }
                          int res = nbc.indexOfMaxDbl(prob);

                          if(classifiers[res].equals(A[numOfAttr-1][k]))
                              correctlyclassified[k]=true;
                          else
                              correctlyclassified[k]=false;

                          //System.out.println(classifiers[res]+"********"+correctlyclassified[k]);
                        }
                        int reccorrectlyclassified = 0;
                        for(int i=0;i<correctlyclassified.length;i++){
                            if(correctlyclassified[i])
                                reccorrectlyclassified++;
                        }
                       
                        return reccorrectlyclassified;

                }
                else
                {
                    System.out.println("File is empty");
                    return 0;
                }
            }
            else
            {
                System.out.println("File is empty");
                return 0;
            }
}


    public String[][] constClassifierArraySet(String[][] A, String[] indexOfC){
        String[][] AA = new String[A.length][indexOfC.length];
        int k=0;
        for(int i=0;i<A[0].length;i++){
            if(Arrays.asList(indexOfC).contains(Integer.toString(i))){
                for(int j=0;j<A.length;j++){
                   AA[j][k]=A[j][i];
                }
            k++;
            }
            
        }
        return AA;
    }
    
    public double probXoverC(String[] X, String classifier, int countC){
        int countX=0;
        for(int i=0;i<X.length;i++)
            if(classifier.equals(X[i]))
                countX++;

        double temp = countX/(double)countC;
        return Double.valueOf(this.Pre8Form.format(temp));
    }

    public String[] probC(String[] C, String classifier){
        ArrayList<String> tempAttrClassSet=new ArrayList<String>();
        for(int i=0;i<C.length;i++){
            if(classifier.equals(C[i])){
                tempAttrClassSet.add(Integer.toString(i));
            }
        }
         String[] AttrClassSet=new String[tempAttrClassSet.size()];
         tempAttrClassSet.toArray(AttrClassSet);
         return AttrClassSet;
    }
    
    public String[] identifySetOfValuesFromAttri(String[] C){
        HashSet<String> classifiers = new HashSet<String>(); //HashSet to remove Duplicates
        classifiers.addAll(Arrays.asList(C));               //List copied to HashSet with no duplicates
        classifiers.remove("?");                            //Remove ? from set
        classifiers.remove(null);                           //Remove null from set
        String[] temp=new String[classifiers.size()];       //Copy HashSet to Array
        classifiers.toArray(temp);
        Arrays.sort(temp);
        return temp;
    }

    public String[][] fillInMissingValues(String[][] A){
        String missVal=null;
        //Loop to be moved on each Attribute
        for(int i=0;i<A.length;i++){
            String[] valuesOfAttri = this.identifySetOfValuesFromAttri(A[i]); //Get the set of values of Attributes

             try{
                    double checkDouble = Double.parseDouble(valuesOfAttri[0]);
                    double[] doubleA = this.convertStrToDbArray(A[i]);
                    if(this.startergyConti == 1)
                        missVal = Double.toString(this.mean(doubleA));
                    else
                        missVal = Double.toString(this.meansqerror(doubleA));
            }
            catch(Exception e){
                int[] res=new int[valuesOfAttri.length];
                for(int j=0;j<valuesOfAttri.length;j++){
                        String[] indexOfC=this.probC(A[i], valuesOfAttri[j]);
                        res[j]=indexOfC.length;
                }
                    if(this.startergyDisc == 1)
                        missVal = valuesOfAttri[this.indexOfMax(res)];
                    else
                        missVal = valuesOfAttri[this.indexOfMin(res)];
            }

            //System.out.println("For Attri A"+(i+1)+" "+missVal);
            for(int j=0;j<A[i].length-1;j++){
               // System.out.println(A[i][j]);
                if(A[i][j].equals("?")){
                   
                    A[i][j]=missVal.toString();
                }
            }
        }
        return A;
    }

    public ArrayList<String> readDataFile(String fname){
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

    public double mean(double[] p) {
        double sum = 0;  // sum of all the elements
        for (int i=0; i<p.length; i++)
            sum += p[i];
        double temp = sum / p.length;
        return Double.valueOf(this.Pre8Form.format(temp));
    }

    public double meansqerror(double[] p) {
        double sumofsq = 0;  // sum of all the elements
        for (int i=0; i<p.length; i++)
            sumofsq += (p[i]*p[i]);
        double temp = Math.sqrt(sumofsq / p.length);
        return Double.valueOf(this.Pre8Form.format(temp));
    }

    public double sd ( double[] data ){
          // sd is sqrt of sum of (values-mean) squared divided by n - 1
          // Calculate the mean
          double mean = 0;
          final int n = data.length;
          if ( n < 2 )
             {
             return Double.NaN;
             }
          for ( int i=0; i<n; i++ )
             {
             mean += data[i];
             }
          mean /= n;
          // calculate the sum of squares
          double sum = 0;
          for ( int i=0; i<n; i++ )
             {
             final double v = data[i] - mean;
             sum += v * v;
             }
          // Change to ( n - 1 ) to n if you have complete data instead of a sample.
          double temp = Math.sqrt( sum / ( n - 1 ) );
          return Double.valueOf(this.Pre8Form.format(temp));
      }

    public int indexOfMax(int[] t) {
    int maximum = t[0];   // start with the first value
    int indMax = 0;
    for (int i=1; i<t.length; i++) {
        if (t[i] > maximum) {
            maximum = t[i];   // new maximum
            indMax = i;
        }
    }
    return indMax;
    }

     public int indexOfMin(int[] t) {
    int minimum = t[0];   // start with the first value
    int indMin = 0;
    for (int i=1; i<t.length; i++) {
        if (t[i] < minimum) {
            minimum = t[i];   // new maximum
            indMin = i;
        }
    }
    return indMin;
    }

    public int indexOfMaxDbl(double[] t) {
    double maximum = t[0];   // start with the first value
    int indMax = 0;
    for (int i=1; i<t.length; i++) {
        if (t[i] > maximum) {
            maximum = t[i];   // new maximum
            indMax = i;
        }
    }
    return indMax;
    }

    public double[] convertStrToDbArray(String[] arrString){
        //Calculating Arraysize without ?
        int countArrLength=0;
        for(int i=0; i<arrString.length; i++)
            if(!arrString[i].equals("?"))
                countArrLength++;
        //Filling in Array without ? so that we can get right mean
        double[] arrDouble = new double[countArrLength];
        for(int i=0,j=0; i<arrString.length; i++)
            if(!arrString[i].equals("?"))
                arrDouble[j++] = Double.parseDouble(arrString[i]);
       
        return arrDouble;
    }

    public double max(double[] t) {
    double maximum = t[0];   // start with the first value
    for (int i=1; i<t.length; i++) {
        if (t[i] > maximum) {
            maximum = t[i];   // new maximum
        }
    }
    return maximum;
}

    public double min(double[] t) {
    double minimum = t[0];   // start with the first value
    for (int i=1; i<t.length; i++) {
        if (t[i] < minimum) {
            minimum = t[i];   // new minimum
        }
    }
    return minimum;
}
}
