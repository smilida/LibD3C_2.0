package cn.edu.xmu.dm.d3c.selectivealgorithms;

import cn.edu.xmu.dm.d3c.metrics.ClassifierDiversity;
import cn.edu.xmu.dm.d3c.voters.D3CVoter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import weka.core.Instances;

public class HCRR {
    public static double doHCRR(Instances train, double initCorrectRate,
                                List<List<Integer>> classifyRightOrWrong,
                                List<List<Integer>> classifyErrorNo, List<Double> correctRateArray,
                                List<List<double[]>> classifyDistributeForInstances,
                                List<Double> currentResult, List<Integer> ClassifierNo) {
        double diversity, correctRate;
        int numOfClassifiers = classifyRightOrWrong.size();
        int count = numOfClassifiers;
        int i = 0;
        Random random = new Random();
        if (ClassifierNo.size() == 0) {
            diversity = ((Double)currentResult.get(0)).doubleValue();
            boolean bestBegin = false;
            if (!bestBegin) {
                int r = random.nextInt(numOfClassifiers);
                correctRate = ((Double)correctRateArray.get(r)).doubleValue();
                ClassifierNo.add(Integer.valueOf(r));
            } else {
                correctRate = ((Double)Collections.<Double>max(correctRateArray)).doubleValue();
                int maxNo = correctRateArray.indexOf(Double.valueOf(correctRate));
                ClassifierNo.add(Integer.valueOf(maxNo));
            }
        } else {
            diversity = ((Double)currentResult.get(0)).doubleValue();
            correctRate = ((Double)currentResult.get(1)).doubleValue();
        }
        while (i < count &&
                correctRate < initCorrectRate) {
            int r = random.nextInt(numOfClassifiers);
            List[] tempList = new List[ClassifierNo.size() + 1];
            for (int k = 0; k < ClassifierNo.size(); k++)
                tempList[k] = classifyRightOrWrong.get(((Integer)ClassifierNo.get(k)).intValue());
            tempList[ClassifierNo.size()] = classifyRightOrWrong.get(r);
            double tempDiversity = ClassifierDiversity.CalculateK(tempList);
            if (tempDiversity <= diversity) {
                ClassifierNo.add(Integer.valueOf(r));
                List<Double> newCorrectRateArray = new ArrayList<Double>();
                List<List<double[]>> newClassifyDistributeForInstances = new ArrayList<List<double[]>>();
                for (int j = 0; j < ClassifierNo.size(); j++) {
                    newCorrectRateArray.add(correctRateArray
                            .get(((Integer)ClassifierNo.get(j)).intValue()));
                    newClassifyDistributeForInstances
                            .add(classifyDistributeForInstances
                                    .get(((Integer)ClassifierNo.get(j)).intValue()));
                }
                double voteCorrectRate = D3CVoter.probabilityVote(train,
                        newCorrectRateArray,
                        newClassifyDistributeForInstances);
                if (voteCorrectRate > correctRate) {
                    diversity = tempDiversity;
                    correctRate = voteCorrectRate;
                } else {
                    ClassifierNo.remove(ClassifierNo.size() - 1);
                }
            }
            i++;
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
