package cn.edu.xmu.dm.d3c.core;

import cn.edu.xmu.dm.d3c.selectivealgorithms.DS;
import cn.edu.xmu.dm.d3c.selectivealgorithms.EBSS;
import cn.edu.xmu.dm.d3c.selectivealgorithms.EFSS;
import cn.edu.xmu.dm.d3c.selectivealgorithms.HCNRR;
import cn.edu.xmu.dm.d3c.selectivealgorithms.HCRR;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class CircleCombine {
    private boolean bestBegin = true;

    public String tempMatrixString = "";

    public String bestMatrixString = "";

    public String tempClassDetailsString = "";

    public String bestClassDetailsString = "";

    public static double doCircleCombine(Instances train, double initCorrectRate,
                                         double interval, List<List<Integer>> classifyRightOrWrong,
                                         List<List<Integer>> classifyErrorNo, List<Double> correctRateArray,
                                         List<List<double[]>> classifyDistributeForInstances, List<Double> currentResult,
                                         List<Integer> ClassifierNo, String CCAlgorithm) throws Exception {
        List<Integer> OptimalNo = new ArrayList<Integer>();
        List<Double> optimalResult = new ArrayList<Double>();
        optimalResult.add(Double.valueOf(Double.MAX_VALUE));
        optimalResult.add(Double.valueOf(0.0D));
        int circle = 0;
        DecimalFormat df = new DecimalFormat("0.00000");
        while (initCorrectRate >= 0.0D) {
            if (CCAlgorithm.equals("HCNRR")) {
                HCNRR.doHCNRR(
                        train,
                        initCorrectRate,
                        classifyRightOrWrong,
                        classifyErrorNo,
                        correctRateArray,
                        classifyDistributeForInstances,
                        currentResult,
                        ClassifierNo);
            } else if (CCAlgorithm.equals("HCRR")) {
                HCRR.doHCRR(
                        train,
                        initCorrectRate,
                        classifyRightOrWrong,
                        classifyErrorNo,
                        correctRateArray,
                        classifyDistributeForInstances,
                        currentResult,
                        ClassifierNo);
            } else if (CCAlgorithm.equals("EBSS")) {
                EBSS.doEBSS(
                        train,
                        initCorrectRate,
                        classifyRightOrWrong,
                        classifyErrorNo,
                        correctRateArray,
                        classifyDistributeForInstances,
                        currentResult,
                        ClassifierNo);
            } else if (CCAlgorithm.equals("EFSS")) {
                EFSS.doEFSS(
                        train,
                        initCorrectRate,
                        classifyRightOrWrong,
                        classifyErrorNo,
                        correctRateArray,
                        classifyDistributeForInstances,
                        currentResult,
                        ClassifierNo);
            } else {
                throw new Exception("Could not find selective algorithm:" + CCAlgorithm);
            }
            if (((Double)currentResult.get(1)).doubleValue() > ((Double)optimalResult.get(1)).doubleValue()) {
                optimalResult.clear();
                optimalResult.add(currentResult.get(0));
                optimalResult.add(currentResult.get(1));
                OptimalNo.clear();
                OptimalNo.addAll(ClassifierNo);
            }
            if (((Double)optimalResult.get(1)).doubleValue() >= initCorrectRate)
                break;
            initCorrectRate -= interval;
            circle++;
        }
        return ((Double)optimalResult.get(1)).doubleValue();
    }

    public double CC(Instances train, Classifier[] cfsArray,
                     List<Integer> D, List<Double> correctRateArray,
                     double initCorrectRate, double interval,
                     List<Double> currentResult, List<Integer> ClassifierNo) {
        int circle = 0;
        double correctRate = 0.0D;
        while (initCorrectRate >= 0.0D) {
            System.out.println("Circle:" + circle);
            correctRate = DS.doDS(train, cfsArray, D, correctRateArray, initCorrectRate, currentResult, ClassifierNo);
            if (correctRate >= initCorrectRate) {
                System.out.print(String.valueOf(initCorrectRate) + "\t");
                System.out.print(String.valueOf(correctRate) + "\t");
                System.out.println("ClassifierNo:" + ClassifierNo);
                break;
            }
            System.out.print(String.valueOf(initCorrectRate) + "\t");
            System.out.print(String.valueOf(correctRate) + "\t");
            System.out.println("ClassifierNo:" + ClassifierNo);
            initCorrectRate -= interval;
            circle++;
        }
        return correctRate;
    }

    public void setBestBegin(boolean bb) {
        this.bestBegin = bb;
    }

    public void setTempMatrixString(String str) {
        this.tempMatrixString = str;
    }

    public void setBestMatrixString(String str) {
        this.bestMatrixString = str;
    }

    public void setTempClassDetailsString(String str) {
        this.tempClassDetailsString = str;
    }

    public void setBestClassDetailsString(String str) {
        this.bestClassDetailsString = str;
    }
}
