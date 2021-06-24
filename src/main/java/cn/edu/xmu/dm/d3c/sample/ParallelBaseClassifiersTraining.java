package cn.edu.xmu.dm.d3c.sample;

import cn.edu.xmu.dm.d3c.core.WeakClassifiersIndependentTrainer;
import cn.edu.xmu.dm.d3c.utils.InitClassifiers;
import cn.edu.xmu.dm.d3c.utils.InstanceUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * FUNCTION_trainingBaseClassifiers: Train base classifiers independently.
 * FUNCTION_run: Read train data file and classifiers.xml, then train base classifiers
 *      independently, according KMeans clustering the base classifiers and ensemble part
 *      of them. Yield the ensemble classifier.
 *
 */

public class ParallelBaseClassifiersTraining {
    public List<Classifier> trainingBaseClassifiers(Instances input, Classifier[] cfsArray,
                                                    double validatePercent, int numSlots,
                                                    int timeOut, List<String> pathOfClassifiers,
                                                    List<String> parameterOfCV) throws Exception {
        Instances inputR = new Instances(input);
        Random random = new Random(1L);
        inputR.randomize(random);
        WeakClassifiersIndependentTrainer wcit = new WeakClassifiersIndependentTrainer();
        List<Classifier> bcfs = wcit.IndependentlyTrainWeakClassifiers(
                inputR, cfsArray, validatePercent, numSlots, timeOut, pathOfClassifiers, parameterOfCV);
        return bcfs;
    }

    public List<Integer> run(int numSlots, int timeOut) throws Exception {
        String datafile = "C:/Users/chenwq/wekafiles/packages/LibD3C/bupa.arff";
        InstanceUtil myutil = new InstanceUtil();
        Instances input = InstanceUtil.getInstances(datafile);
        input.setClassIndex(input.numAttributes() - 1);
        List<String> nameOfClassifiers = new ArrayList<String>();
        List<String> pathOfClassifiers = new ArrayList<String>();
        List<String> parameterOfCV = new ArrayList<String>();
        myutil.getJarPath(InstanceUtil.class);
        String config = "C:/Users/chenwq/wekafiles/packages/LibD3C/classifiers.xml";
        System.out.println(config);
        Classifier[] cfsArray = InitClassifiers.init(config, nameOfClassifiers, pathOfClassifiers, parameterOfCV);
        ParallelBaseClassifiersTraining bct = new ParallelBaseClassifiersTraining();
        double numfolds = 0.5D;
        System.out.println(cfsArray.length);
        System.out.println(pathOfClassifiers.size());
        System.out.println(parameterOfCV.size());
        bct.trainingBaseClassifiers(input, cfsArray, numfolds, numSlots, timeOut, pathOfClassifiers, parameterOfCV);
        System.out.println("clustering...");
        BaseClassifiersClustering bcc = new BaseClassifiersClustering();
        String pathPrefix = "D:/tmp/";
        File dir = new File(pathPrefix);
        if (!dir.isDirectory())
            dir.mkdir();
        String fchooseClassifiers = String.valueOf(pathPrefix) + "chooseClassifiers.txt";
        List<Integer> chooseClassifiers = bcc.clusterBaseClassifiers(fchooseClassifiers, 2);
        input.setClassIndex(input.numAttributes() - 1);
        BaseClassifiersEnsemble bce = new BaseClassifiersEnsemble();
        List<Integer> ensemClassifiers = bce.EnsembleClassifiers(input, 1, 1);
        return ensemClassifiers;
    }

    public static void main(String[] args) throws Exception {
        ParallelBaseClassifiersTraining pbc = new ParallelBaseClassifiersTraining();
        pbc.run(15, 2);
    }
}
