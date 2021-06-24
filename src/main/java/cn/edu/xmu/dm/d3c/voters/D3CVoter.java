package cn.edu.xmu.dm.d3c.voters;

import cn.edu.xmu.dm.d3c.utils.InstanceUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Vote;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 * FUNCTION_ensembleVote: Use VoteEnsemble to integrate the classifiers. 5-fold evaluation ACC.
 * FUNCTION_probabilityVote:
 */

public class D3CVoter {
    public static double ensembleVote(Instances train, Classifier[] newCfsArray) {
        double correctRate = 0.0D;
        try {
            Vote ensemble = new Vote();
            SelectedTag tag = new SelectedTag(3,
                    Vote.TAGS_RULES);
            ensemble.setCombinationRule(tag);
            ensemble.setClassifiers(newCfsArray);
            ensemble.setSeed(2);
            ensemble.buildClassifier(train);
            Evaluation eval = new Evaluation(train);
            Random random = new Random(1000L);
            eval.crossValidateModel(ensemble, train, 5, random, new Object[0]);
            correctRate = 1.0D - eval.errorRate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return correctRate;
    }

    public static double probabilityVote(Instances train, List<Double> correctRateArray,
                                         List<List<double[]>> distributeForInstances) {
        double correctRate = 0.0D;
        List<double[]> sumOfProbability = new ArrayList<double[]>();
        int i;
        for (i = 0; i < (distributeForInstances.get(0)).size(); i++) {     // the size of dataset.
            double[] temp = new double[((double[])(distributeForInstances.get(0)).get(0)).length];     // the number of classes.
            int j;
            for (j = 0; j < temp.length; j++)
                temp[j] = 0.0D;
            for (j = 0; j < distributeForInstances.size(); j++) {    // the number of classifiers were chose.
                for (int k = 0; k < ((double[])(distributeForInstances.get(j)).get(i)).length; k++)
                    temp[k] = temp[k] + ((Double)correctRateArray.get(j)) * ((double[])(distributeForInstances.get(j)).get(i))[k];
            }
            sumOfProbability.add(temp);
        }
        InstanceUtil myutil = new InstanceUtil();
        List<Double> predict_class = new ArrayList<Double>();
        for (i = 0; i < sumOfProbability.size(); i++)    // the number of classifiers were chose.
            predict_class.add(myutil.findTheMaxNo(sumOfProbability.get(i)));
        double numOfError = 0.0D;
        System.out.println("size:" + predict_class.size());
        System.out.println("instances:" + train.numInstances());
        for (i = 0; i < predict_class.size(); i++) {
            if (train.instance(i).classValue() != ((Double)predict_class.get(i)))
                numOfError++;
        }
        correctRate = (train.numInstances() - numOfError) / train.numInstances();
        return correctRate;
    }
}
