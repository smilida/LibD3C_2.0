package cn.edu.xmu.dm.d3c.sample;

import cn.edu.xmu.dm.d3c.clustering.AffinityPropagation;
import cn.edu.xmu.dm.d3c.utils.ClassifyResultArffLoader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;

/**
 * FUNCTION_clusterBaseClassifiers:According to KMeans clustering classifiers.
 *      Yield the part of the classifiers after clustering.
 */

public class BaseClassifiersClustering {
    public List<Integer> clusterBaseClassifiers(String fchooseClassifiers, int numClusters) throws Exception {
        List<Integer> chooseClassifiers = new ArrayList<Integer>();
        AffinityPropagation ap = new AffinityPropagation();
        //KMeanz km = new KMeanz(numClusters);
        Instances classifyResult = ClassifyResultArffLoader.loadClassifyResultFromArff("");
        List<Double> correctRateArray = ClassifyResultArffLoader.loadCorrectRate("");
        ap.buildClusterer(classifyResult, chooseClassifiers,
                correctRateArray);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fchooseClassifiers));
        for (int i = 0; i < chooseClassifiers.size(); i++) {
            writer.write(((Integer)chooseClassifiers.get(i)).toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
        System.out.println("Save ChooseClassifiers in file:" + fchooseClassifiers);
        return chooseClassifiers;
    }

    /*public static void main(String[] args) throws Exception {
        BaseClassifiersClustering bcc = new BaseClassifiersClustering();
        String pathPrefix = "C:/Users/chenwq/wekafiles/packages/LibD3C/";
        String fchooseClassifiers = String.valueOf(pathPrefix) + "chooseClassifiers.txt";
        List<Integer> chooseClassifiers = bcc.clusterBaseClassifiers(fchooseClassifiers, 6);
    }*/
}
