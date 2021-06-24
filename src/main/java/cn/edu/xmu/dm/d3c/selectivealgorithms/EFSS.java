package cn.edu.xmu.dm.d3c.selectivealgorithms;

import cn.edu.xmu.dm.d3c.metrics.ClassifierDiversity;
import cn.edu.xmu.dm.d3c.voters.D3CVoter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import weka.core.Instances;

public class EFSS {
    public static double doEFSS(Instances train, double initCorrectRate,
                                List<List<Integer>> classifyRightOrWrong,
                                List<List<Integer>> classifyErrorNo, List<Double> correctRateArray,
                                List<List<double[]>> classifyDistributeForInstances,
                                List<Double> currentResult, List<Integer> ClassifierNo) {
        double diversity, correctRate, voteCorrectRate = 0.0D;
        List<Integer> sortedNo = new ArrayList<Integer>();
        int i = 0;
        int numOfClassifiers = classifyRightOrWrong.size();
        int threshold = 2 * numOfClassifiers;
        List<Double> newCorrectRateArray = new ArrayList<Double>();
        List<Double> tempCorrectRateArray = new ArrayList<Double>();
        newCorrectRateArray.addAll(correctRateArray);
        tempCorrectRateArray.addAll(correctRateArray);
        List<Integer> temp = new ArrayList<Integer>();
        for (i = 0; i < tempCorrectRateArray.size(); i++)
            temp.add(Integer.valueOf(i));
        Collections.sort(newCorrectRateArray);
        for (i = newCorrectRateArray.size() - 1; i >= 0; i--) {
            int tempNo = tempCorrectRateArray.indexOf(newCorrectRateArray.get(i));
            sortedNo.add(temp.get(tempNo));
            tempCorrectRateArray.remove(tempNo);
            temp.remove(tempNo);
        }
        if (ClassifierNo.size() == 0) {
            diversity = ((Double)currentResult.get(0)).doubleValue();
            ClassifierNo.add(sortedNo.get(0));
            correctRate = ((Double)correctRateArray.get(((Integer)sortedNo.get(0)).intValue())).doubleValue();
            sortedNo.remove(0);
        } else {
            diversity = ((Double)currentResult.get(0)).doubleValue();
            correctRate = ((Double)currentResult.get(1)).doubleValue();
        }
        while (sortedNo.size() != 0 &&
                correctRate < initCorrectRate) {
            int currentNo = ((Integer)sortedNo.get(0)).intValue();
            List[] tempList = new List[ClassifierNo.size() + 1];
            for (int k = 0; k < ClassifierNo.size(); k++)
                tempList[k] = classifyRightOrWrong.get(((Integer)ClassifierNo.get(k)).intValue());
            tempList[ClassifierNo.size()] = classifyRightOrWrong.get(currentNo);
            double tempDiversity = ClassifierDiversity.CalculateK(tempList);
            if (tempDiversity <= diversity) {
                ClassifierNo.add(sortedNo.get(0));
                List<Double> new_correctRateArray = new ArrayList<Double>();
                List<List<double[]>> new_classifyDistributeForInstances = new ArrayList<List<double[]>>();
                for (int j = 0; j < ClassifierNo.size(); j++) {
                    new_correctRateArray.add(correctRateArray.get(((Integer)ClassifierNo.get(j)).intValue()));
                    new_classifyDistributeForInstances.add(classifyDistributeForInstances.get(((Integer)ClassifierNo.get(j)).intValue()));
                }
                voteCorrectRate = D3CVoter.probabilityVote(train, new_correctRateArray, new_classifyDistributeForInstances);
                if (voteCorrectRate > correctRate) {
                    diversity = tempDiversity;
                    correctRate = voteCorrectRate;
                } else {
                    ClassifierNo.remove(ClassifierNo.size() - 1);
                }
            }
            sortedNo.remove(0);
        }
        if (diversity == Double.MAX_VALUE)
            diversity = 0.0D;
        currentResult.clear();
        currentResult.add(Double.valueOf(diversity));
        currentResult.add(Double.valueOf(correctRate));
        currentResult.add(Double.valueOf(ClassifierNo.size()));
        return correctRate;
    }
}
