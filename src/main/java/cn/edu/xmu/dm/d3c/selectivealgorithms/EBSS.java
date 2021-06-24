package cn.edu.xmu.dm.d3c.selectivealgorithms;

import cn.edu.xmu.dm.d3c.metrics.ClassifierDiversity;
import cn.edu.xmu.dm.d3c.voters.D3CVoter;
import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;

/**
 * FUNCTION_doEBSS: 简单说就是把分类器都加进去，慢慢删除。但是threshold为什么*2还不知道
 */

public class EBSS {
    public static double doEBSS(Instances train, double initCorrectRate,
                                List<List<Integer>> classifyRightOrWrong,
                                List<List<Integer>> classifyErrorNo, List<Double> correctRateArray,
                                List<List<double[]>> classifyDistributeForInstances,
                                List<Double> currentResult, List<Integer> ClassifierNo) {
        double diversity, correctRate, tempDiversity = Double.MAX_VALUE;
        List<Double> tempResult = new ArrayList<Double>();
        int i = 0;
        int numOfClassifiers = classifyRightOrWrong.size();
        int threshold = 2 * numOfClassifiers;
        if (ClassifierNo.size() == 0) {
            List[] temp = new List[numOfClassifiers];
            int j;
            for (j = 0; j < numOfClassifiers; j++)
                temp[j] = classifyRightOrWrong.get(j);
            diversity = ClassifierDiversity.CalculateK(temp);
            correctRate = D3CVoter.probabilityVote(train, correctRateArray,
                    classifyDistributeForInstances);
            currentResult.clear();
            currentResult.add(diversity);
            currentResult.add(correctRate);
            currentResult.add(Double.valueOf(numOfClassifiers));
            for (j = 0; j < numOfClassifiers; j++)
                ClassifierNo.add(j);
        } else {
            diversity = ((Double)currentResult.get(0));
            correctRate = ((Double)currentResult.get(1));
            if (correctRate >= initCorrectRate)
                return correctRate;
            tempResult.add(diversity);
            tempResult.add(currentResult.get(2));
            for (int j = 0; j < numOfClassifiers; j++) {
                List[] tempList = new List[ClassifierNo.size() + 1];
                for (int m = 0; m < ClassifierNo.size(); m++)
                    tempList[m] = classifyRightOrWrong.get(((Integer)ClassifierNo.get(m)));
                tempList[ClassifierNo.size()] = classifyRightOrWrong.get(j);               //  classifier j twice????
                tempDiversity = ClassifierDiversity.CalculateK(tempList);
                ClassifierNo.add(j);
                if (tempDiversity < ((Double)tempResult.get(0)) &&
                        ClassifierNo.size() <= threshold) {
                    tempResult.clear();
                    tempResult.add(tempDiversity);
                    tempResult.add(Double.valueOf(ClassifierNo.size()));
                }
            }
            diversity = tempDiversity;
            if (ClassifierNo.size() > threshold) {
                while (ClassifierNo.size() != ((Double)tempResult.get(1)))
                    ClassifierNo.remove(ClassifierNo.size() - 1);
                diversity = ((Double)tempResult.get(0));
            }
            List<Double> newCorrectRateArray = new ArrayList<Double>();
            List<List<double[]>> newClassifyDistributeForInstances = new ArrayList<List<double[]>>();
            for (int k = 0; k < ClassifierNo.size(); k++) {
                newCorrectRateArray.add(correctRateArray.get(((Integer)ClassifierNo.get(k))));
                newClassifyDistributeForInstances
                        .add(classifyDistributeForInstances.get(((Integer)ClassifierNo.get(k))));
            }
            correctRate = D3CVoter.probabilityVote(train, newCorrectRateArray,
                    newClassifyDistributeForInstances);
        }
        int r = 0;
        while (r != numOfClassifiers &&
                correctRate < initCorrectRate) {
            int index = ClassifierNo.indexOf(r);
            if (index != -1) {
                byte b = 0;
                List[] tempList = new List[ClassifierNo.size() - 1];
                int k;
                for (k = 0; k < ClassifierNo.size(); k++) {
                    if (k != index) {
                        tempList[b] = classifyRightOrWrong.get(((Integer)ClassifierNo.get(k)));
                        b++;
                    }
                }
                System.out.println("---: " + tempList.length);
                tempDiversity = ClassifierDiversity.CalculateK(tempList);
                if (tempDiversity <= diversity) {
                    List<Double> newCorrectRateArray = new ArrayList<Double>();
                    List<List<double[]>> newClassifyDistributeForInstances = new ArrayList<List<double[]>>();
                    b = 0;
                    for (k = 0; k < ClassifierNo.size(); k++) {
                        if (k != index) {
                            newCorrectRateArray.add(b, correctRateArray
                                    .get(((Integer)ClassifierNo.get(k))));
                            newClassifyDistributeForInstances.add(b,
                                    classifyDistributeForInstances.get(((Integer)ClassifierNo.get(k))));
                            b++;
                        }
                    }
                    double voteCorrectRate = D3CVoter.probabilityVote(train,
                            newCorrectRateArray,
                            newClassifyDistributeForInstances);
                    if (voteCorrectRate >= correctRate) {
                        ClassifierNo.remove(index);
                        diversity = tempDiversity;
                        correctRate = voteCorrectRate;
                    }
                }
            }
            r++;
        }
        if (diversity == Double.MAX_VALUE)
            diversity = 0.0D;
        currentResult.clear();
        currentResult.add(diversity);
        currentResult.add(correctRate);
        currentResult.add(Double.valueOf(ClassifierNo.size()));
        return correctRate;
    }
}
