package cn.edu.xmu.dm.d3c.selectivealgorithms;

import cn.edu.xmu.dm.d3c.voters.D3CVoter;
import java.util.ArrayList;
import java.util.List;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * FUNCTION_doDS:
 *      All weak classifiers are trained independently in parallel.
 */

public class DS {
    public static double doDS(Instances train, Classifier[] cfsArray,
                              List<Integer> D, List<Double> correctRateArray,
                              double initCorrectRate, List<Double> currentResult,
                              List<Integer> ClassifierNo) {
        int threshold = 2 * cfsArray.length;
        List<Integer> tempD = new ArrayList<Integer>();
        tempD.addAll(D);
        ClassifierNo.add(tempD.get(0));
        double correctRate = ((Double)correctRateArray.get(((Integer)tempD.get(0)).intValue())).doubleValue();
        tempD.remove(0);
        while (tempD.size() != 0) {
            if (ClassifierNo.size() > threshold) {
                correctRate = ((Double)currentResult.get(1)).doubleValue();
                while (ClassifierNo.size() != ((Double)currentResult.get(2)).doubleValue())
                    ClassifierNo.remove(ClassifierNo.size() - 1);
            }
            if (correctRate >= initCorrectRate)
                break;
            ClassifierNo.add(tempD.get(0));
            Classifier[] newCfsArray = new Classifier[ClassifierNo.size()];
            for (int k = 0; k < ClassifierNo.size(); k++)
                newCfsArray[k] = cfsArray[((Integer)ClassifierNo.get(k)).intValue()];
            correctRate = D3CVoter.ensembleVote(train, newCfsArray);
            tempD.remove(0);
            if (correctRate > ((Double)currentResult.get(1)).doubleValue() && ClassifierNo.size() <= threshold) {
                currentResult.clear();
                currentResult.add(Double.valueOf(Double.MAX_VALUE));
                currentResult.add(Double.valueOf(correctRate));
                currentResult.add(Double.valueOf(ClassifierNo.size()));
            }
        }
        currentResult.clear();
        currentResult.add(Double.valueOf(0.0D));
        currentResult.add(Double.valueOf(correctRate));
        currentResult.add(Double.valueOf(ClassifierNo.size()));
        return correctRate;
    }
}
