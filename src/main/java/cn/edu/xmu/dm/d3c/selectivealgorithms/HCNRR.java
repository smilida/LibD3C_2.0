package cn.edu.xmu.dm.d3c.selectivealgorithms;

import cn.edu.xmu.dm.d3c.metrics.ClassifierDiversity;
import cn.edu.xmu.dm.d3c.voters.D3CVoter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import weka.core.Instances;

public class HCNRR {
    public static double doHCNRR(Instances train, double initCorrectRate, List<List<Integer>> classifyRightOrWrong, List<List<Integer>> classifyErrorNo, List<Double> correctRateArray, List<List<double[]>> classifyDistributeForInstances, List<Double> currentResult, List<Integer> ClassifierNo) {
        double diversity, correctRate;
        int i = 0;
        int numOfClassifiers = classifyRightOrWrong.size();
        List<Integer> candidateClassifierNo = new ArrayList<Integer>();
        Random random = new Random(1000L);
        if (ClassifierNo.size() == 0) {
            int tempNo;
            diversity = ((Double)currentResult.get(0)).doubleValue();
            boolean bestBegin = false;
            if (!bestBegin) {
                int r = random.nextInt(numOfClassifiers);
                correctRate = ((Double)correctRateArray.get(r)).doubleValue();
                ClassifierNo.add(Integer.valueOf(r));
                tempNo = r;
            } else {
                correctRate = ((Double)Collections.<Double>max(correctRateArray)).doubleValue();
                int maxNo = correctRateArray.indexOf(Double.valueOf(correctRate));
                ClassifierNo.add(Integer.valueOf(maxNo));
                tempNo = maxNo;
            }
            for (int j = 0; j < numOfClassifiers; j++) {
                if (j != tempNo)
                    candidateClassifierNo.add(Integer.valueOf(j));
            }
        } else {
            diversity = ((Double)currentResult.get(0)).doubleValue();
            correctRate = ((Double)currentResult.get(1)).doubleValue();
            for (int j = 0; j < numOfClassifiers; j++)
                candidateClassifierNo.add(Integer.valueOf(j));
        }
        while (candidateClassifierNo.size() != 0 &&
                correctRate < initCorrectRate) {
            int r = random.nextInt(candidateClassifierNo.size());
            int candidateNo = ((Integer)candidateClassifierNo.get(r)).intValue();
            List[] tempList = new List[ClassifierNo.size() + 1];
            for (int k = 0; k < ClassifierNo.size(); k++)
                tempList[k] = classifyRightOrWrong.get(((Integer)ClassifierNo.get(k)).intValue());
            tempList[ClassifierNo.size()] = classifyRightOrWrong
                    .get(candidateNo);
            double tempDiversity = ClassifierDiversity.CalculateK(tempList);
            if (tempDiversity <= diversity) {
                ClassifierNo.add(Integer.valueOf(candidateNo));
                List<Double> newCorrectRateArray = new ArrayList<Double>();
                byte b;
                for (b = 0; b < ClassifierNo.size(); b++)
                    newCorrectRateArray.add(correctRateArray
                            .get(((Integer)ClassifierNo.get(b)).intValue()));
                List<List<double[]>> newClassifyDistributeForInstances = new ArrayList<List<double[]>>();
                for (b = 0; b < ClassifierNo.size(); b++)
                    newClassifyDistributeForInstances
                            .add(classifyDistributeForInstances
                                    .get(((Integer)ClassifierNo.get(b)).intValue()));
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
            candidateClassifierNo.remove(r);
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
