package cn.edu.xmu.dm.d3c.threadpool;

import cn.edu.xmu.dm.d3c.core.CircleCombine;
import cn.edu.xmu.dm.d3c.selectivealgorithms.EBSS;
import cn.edu.xmu.dm.d3c.selectivealgorithms.EFSS;
import cn.edu.xmu.dm.d3c.selectivealgorithms.HCNRR;
import cn.edu.xmu.dm.d3c.selectivealgorithms.HCRR;
import java.util.List;
import java.util.Random;
import weka.core.Instances;

public class EnsemblingThread extends Thread {
    private boolean isFinished = false;

    private String selectiveAlgorithm;

    private String CCAlgorithm = "EBSS";

    private String strategy;

    private double currentCorrectRate = 0.0D;

    private Instances input;

    private double newInitCorrectRate = 1.0D;

    private double newInterval = 0.05D;

    private List<List<Integer>> newClassifyRightOrWrong;

    private List<List<Integer>> newClassifyErrorNo;

    private List<Double> newCorrectRateArray;

    private List<List<double[]>> newClassifyDistributeForInstances;

    private List<Double> currentResult;

    private List<Integer> ClassifierNo;

    public void run() {
        Instances inputR = new Instances(this.input);
        Random random = new Random(1L);
        inputR.randomize(random);
        if (this.selectiveAlgorithm.equals("HCNRR")) {
            this.currentCorrectRate = HCNRR.doHCNRR(inputR, this.newInitCorrectRate,
                    this.newClassifyRightOrWrong, this.newClassifyErrorNo,
                    this.newCorrectRateArray, this.newClassifyDistributeForInstances,
                    this.currentResult, this.ClassifierNo);
        } else if (this.selectiveAlgorithm.equals("HCRR")) {
            this.currentCorrectRate = HCRR.doHCRR(inputR, this.newInitCorrectRate,
                    this.newClassifyRightOrWrong, this.newClassifyErrorNo,
                    this.newCorrectRateArray, this.newClassifyDistributeForInstances,
                    this.currentResult, this.ClassifierNo);
        } else if (this.selectiveAlgorithm.equals("EBSS")) {
            this.currentCorrectRate = EBSS.doEBSS(inputR, this.newInitCorrectRate,
                    this.newClassifyRightOrWrong, this.newClassifyErrorNo,
                    this.newCorrectRateArray, this.newClassifyDistributeForInstances,
                    this.currentResult, this.ClassifierNo);
        } else if (this.selectiveAlgorithm.equals("EFSS")) {
            this.currentCorrectRate = EFSS.doEFSS(inputR, this.newInitCorrectRate,
                    this.newClassifyRightOrWrong, this.newClassifyErrorNo,
                    this.newCorrectRateArray, this.newClassifyDistributeForInstances,
                    this.currentResult, this.ClassifierNo);
        } else if (this.selectiveAlgorithm.equals("CC")) {
            try {
                this.currentCorrectRate = CircleCombine.doCircleCombine(inputR,
                        this.newInitCorrectRate, this.newInterval, this.newClassifyRightOrWrong,
                        this.newClassifyErrorNo, this.newCorrectRateArray,
                        this.newClassifyDistributeForInstances, this.currentResult,
                        this.ClassifierNo, this.CCAlgorithm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (this.selectiveAlgorithm.equals("DS")) {
            try {
                this.currentCorrectRate = CircleCombine.doCircleCombine(inputR,
                        this.newInitCorrectRate, this.newInterval, this.newClassifyRightOrWrong,
                        this.newClassifyErrorNo, this.newCorrectRateArray,
                        this.newClassifyDistributeForInstances, this.currentResult,
                        this.ClassifierNo, this.CCAlgorithm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                throw new Exception("Could not find selective algorithm:" +
                        this.selectiveAlgorithm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.isFinished = true;
    }

    public String getSelectiveAlgorithm() {
        return this.selectiveAlgorithm;
    }

    public void setSelectiveAlgorithm(String selectiveAlgorithm) {
        this.selectiveAlgorithm = selectiveAlgorithm;
    }

    public String getCCAlgorithm() {
        return this.CCAlgorithm;
    }

    public void setCCAlgorithm(String cCAlgorithm) {
        this.CCAlgorithm = cCAlgorithm;
    }

    public double getCurrentCorrectRate() {
        return this.currentCorrectRate;
    }

    public void setCurrentCorrectRate(double currentCorrectRate) {
        this.currentCorrectRate = currentCorrectRate;
    }

    public Instances getInput() {
        return this.input;
    }

    public void setInput(Instances input) {
        this.input = input;
    }

    public double getNewInitCorrectRate() {
        return this.newInitCorrectRate;
    }

    public void setNewInitCorrectRate(double newInitCorrectRate) {
        this.newInitCorrectRate = newInitCorrectRate;
    }

    public double getNewInterval() {
        return this.newInterval;
    }

    public void setNewInterval(double newInterval) {
        this.newInterval = newInterval;
    }

    public List<List<Integer>> getNewClassifyRightOrWrong() {
        return this.newClassifyRightOrWrong;
    }

    public void setNewClassifyRightOrWrong(List<List<Integer>> newClassifyRightOrWrong) {
        this.newClassifyRightOrWrong = newClassifyRightOrWrong;
    }

    public List<List<Integer>> getNewClassifyErrorNo() {
        return this.newClassifyErrorNo;
    }

    public void setNewClassifyErrorNo(List<List<Integer>> newClassifyErrorNo) {
        this.newClassifyErrorNo = newClassifyErrorNo;
    }

    public List<Double> getNewCorrectRateArray() {
        return this.newCorrectRateArray;
    }

    public void setNewCorrectRateArray(List<Double> newCorrectRateArray) {
        this.newCorrectRateArray = newCorrectRateArray;
    }

    public List<List<double[]>> getNewClassifyDistributeForInstances() {
        return this.newClassifyDistributeForInstances;
    }

    public void setNewClassifyDistributeForInstances(List<List<double[]>> newClassifyDistributeForInstances) {
        this.newClassifyDistributeForInstances = newClassifyDistributeForInstances;
    }

    public List<Double> getCurrentResult() {
        return this.currentResult;
    }

    public void setCurrentResult(List<Double> currentResult) {
        this.currentResult = currentResult;
    }

    public List<Integer> getClassifierNo() {
        return this.ClassifierNo;
    }

    public void setClassifierNo(List<Integer> classifierNo) {
        this.ClassifierNo = classifierNo;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public String getStrategy() {
        return this.strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
