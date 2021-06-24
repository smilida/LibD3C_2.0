//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.edu.xmu.dm.d3c.core;

import cn.edu.xmu.dm.d3c.threadpool.ClassifierTrainingTask;
import cn.edu.xmu.dm.d3c.threadpool.ClassifiersTrainingExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import weka.classifiers.Classifier;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;
import weka.core.SerializedObject;
import weka.core.Utils;

//  cross validation train weak classifier    (5-fold)


public class SelectiveEnsemble {
    public SelectiveEnsemble() {
    }

    public List<Classifier> IndependentTrain(Instances input, Classifier[] cfsArray,
                                             double validatePercent, int numSlots,
                                             int timeOut, List<List<Integer>> classifyRightOrWrong,
                                             List<List<Integer>> classifyErrorNo, List<Double> correctRateArray,
                                             List<List<double[]>> classifyDistributeForInstances,
                                             Random random, List<Integer> qqs, List<String> pathOfClassifiers,
                                             List<String> parameterOfCV) throws Exception {
        input.randomize(random);
        Instances train = input.trainCV(5, 0);
        Instances validation= input.testCV(5, 0);

        int processorNum;
        int i;
        processorNum = Runtime.getRuntime().availableProcessors();
        ClassifiersTrainingExecutor myExecutor = new ClassifiersTrainingExecutor(numSlots, processorNum, 200L, TimeUnit.SECONDS, new LinkedBlockingDeque());
        List<Future<Classifier>> results = new ArrayList();
        List<Classifier> lcfs = Arrays.asList(cfsArray);

        Future f;
        for(i = 0; i < lcfs.size(); ++i) {
            ClassifierTrainingTask task = new ClassifierTrainingTask(input, train, validation,
                    (Classifier)lcfs.get(i), classifyRightOrWrong, classifyErrorNo,
                    correctRateArray, classifyDistributeForInstances, i, qqs,
                    pathOfClassifiers, parameterOfCV);
            f = null;
            f = myExecutor.submit(task);
            results.add(f);

        }

        List<Classifier> bcfs = new ArrayList();
        Iterator var30 = results.iterator();

        while(var30.hasNext()) {
            f = (Future)var30.next();

            try {
                f.get((long)timeOut, TimeUnit.MINUTES);
            } catch (Exception var32) {
                f.cancel(true);
            }
        }

        myExecutor.shutdownNow();
        var30 = results.iterator();

        while(var30.hasNext()) {
            f = (Future)var30.next();
            if (!f.isCancelled()) {
                //System.out.println(f.get());
                bcfs.add((Classifier)f.get());
            }
        }

        System.out.println("classifyRightOrWrong.size()"+classifyRightOrWrong.size());
        System.out.println("classifyDistributeForInstances.size()"+classifyDistributeForInstances.size());
        System.out.println("correctRateArray.size()"+correctRateArray.size());
        System.out.println("classifyErrorNo.size()"+classifyErrorNo.size());
        System.out.println("qqs.size()"+qqs.size());
        System.out.println("bcfs.size()"+bcfs.size());
        System.out.println("results.size()"+results.size());

        List<Integer> moveClassifierList = getWorseClassiferIndex(correctRateArray);
        System.out.println("moveClassifierList.size()"+moveClassifierList.size());
        for(int n = moveClassifierList.size() - 1 ; n >= 0; n--){
            System.out.println(moveClassifierList.get(n));
            classifyRightOrWrong.remove((int)moveClassifierList.get(n));
            classifyDistributeForInstances.remove((int)moveClassifierList.get(n));
            correctRateArray.remove((int)moveClassifierList.get(n));
            classifyErrorNo.remove((int)moveClassifierList.get(n));
            qqs.remove(moveClassifierList.get(n));
            bcfs.remove((int)moveClassifierList.get(n));
            results.remove((int)moveClassifierList.get(n));
        }

        for(int n = 0; n < correctRateArray.size(); n++){
            System.out.println("!!!!!!!!"+correctRateArray.get(n));
        }
        System.out.println("基分类器单独训练完成!");
        return bcfs;
    }

    public Classifier CrossValidationModelForDistribute(Instances input, Instances train,
                                                        Instances validation, Classifier classifier,
                                                        List<List<Integer>> classifyRightOrWrong,
                                                        List<List<Integer>> classifyErrorNo,
                                                        List<Double> correctRateArray,
                                                        List<List<double[]>> classifyDistributeForInstances,
                                                        int index, List<Integer> qqs,
                                                        List<String> pathOfClassifiers, List<String> parameterOfCV) {
        Classifier copiedClassifier = null;

        try {
            input = new Instances(input);
            int kfold = 5;

            List<Integer> single_classifyRightOrWrong = new ArrayList();
            List<Integer> single_classifyErrorNo = new ArrayList();
            List<double[]> single_classifyDistributeForInstances = new ArrayList();
            CVParameterSelection cvps = new CVParameterSelection();
            double numOfError = 0.0D;
            if (!((String)parameterOfCV.get(index)).equals("")) {
                copiedClassifier = (Classifier)Utils.forName(Classifier.class, (String)pathOfClassifiers.get(index), cvps.getBestClassifierOptions());
            } else {
                copiedClassifier = (Classifier)(new SerializedObject(classifier)).getObject();
            }
            copiedClassifier.buildClassifier(train);

            for(int j = 0; j < validation.numInstances(); ++j) {
                double real_class = validation.instance(j).classValue();
                double predict_class = copiedClassifier.classifyInstance(validation.instance(j));
                double[] distribute = copiedClassifier.distributionForInstance(validation.instance(j));
                single_classifyDistributeForInstances.add(distribute);
                if (real_class != predict_class) {
                    ++numOfError;
                    single_classifyRightOrWrong.add(0);
                } else if (real_class == predict_class) {
                    single_classifyRightOrWrong.add(1);
                }
            }

            classifyRightOrWrong.add(single_classifyRightOrWrong);

            for(int i = 0; i < single_classifyRightOrWrong.size(); ++i) {
                if ((Integer)single_classifyRightOrWrong.get(i) == 0) {
                    single_classifyErrorNo.add(i);
                }
            }

            classifyErrorNo.add(single_classifyErrorNo);
            double correctRate = ((double)validation.numInstances() - numOfError) / (double)validation.numInstances();
            correctRateArray.add(correctRate);
            classifyDistributeForInstances.add(single_classifyDistributeForInstances);
            qqs.add(index);
        } catch (Exception var30) {
        }

        return copiedClassifier;
    }

    public List<Integer> getWorseClassiferIndex(List<Double> correctRateArray){
        int i;
        double temp = 0.0D;
        System.out.println("before"+correctRateArray.size());
        for(i = 0; i < correctRateArray.size(); i++){
            temp = temp + correctRateArray.get(i);
            System.out.println(correctRateArray.get(i));
        }
        double listAvg = temp/(double)correctRateArray.size();
        System.out.println("Average is: "+ listAvg);
        List<Integer> moveList = new ArrayList<>();
        for(i = 0; i < correctRateArray.size(); i++){
            if(correctRateArray.get(i) <= listAvg){
                moveList.add(i);
                //System.out.println(correctRateArray.get(i));
            }
        }
        //System.out.println("Move list size:" + moveList.size());
        return moveList;
    }


}
