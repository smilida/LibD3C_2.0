package cn.edu.xmu.dm.d3c.sample;

import cn.edu.xmu.dm.d3c.core.CircleCombine;
import cn.edu.xmu.dm.d3c.selectivealgorithms.EBSS;
import cn.edu.xmu.dm.d3c.selectivealgorithms.EFSS;
import cn.edu.xmu.dm.d3c.selectivealgorithms.HCNRR;
import cn.edu.xmu.dm.d3c.selectivealgorithms.HCRR;
import cn.edu.xmu.dm.d3c.utils.ClassifyResultArffLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import weka.core.Instances;

/**
 * FUNCTION_EnsembleClassifiers: Integrate the selected classifiers based on some methods.
 *      Methods include: DS, EBSS, EFSS, HCNRR, HCRR, CC
 */

public class BaseClassifiersEnsemble {
    public static List<Integer> classifer;

    public List<Integer> EnsembleClassifiers(Instances input, int iselectiveAlgorithm, int iCCAlgorithm) throws Exception {
        String selectiveAlgorithm = "";
        String CCAlgorithm = "";
        switch (iselectiveAlgorithm) {
            case 1:
                selectiveAlgorithm = "HCNRR";
            case 2:
                selectiveAlgorithm = "HCRR";
            case 3:
                selectiveAlgorithm = "EFSS";
            case 4:
                selectiveAlgorithm = "EBSS";
            case 5:
                selectiveAlgorithm = "CC";
            case 6:
                selectiveAlgorithm = "DS";
                break;
        }
        switch (iCCAlgorithm) {
            case 1:
                CCAlgorithm = "HCNRR";
            case 2:
                CCAlgorithm = "HCRR";
            case 3:
                CCAlgorithm = "EFSS";
            case 4:
                CCAlgorithm = "EBSS";
                break;
        }
        List<Integer> chooseClassifiers =
                ClassifyResultArffLoader.loadChooseClassifiers("");
        List<Integer> cfsArray = ClassifyResultArffLoader.loadCfsArray("");
        List<List<Integer>> classifyRightOrWrong =
                ClassifyResultArffLoader.loadClassifyRightOrWrong("");
        List<List<Integer>> classifyErrorNo =
                ClassifyResultArffLoader.loadClassifyErrorNo("");
        List<Double> correctRateArray =
                ClassifyResultArffLoader.loadCorrectRate("");
        List<List<double[]>> classifyDistributeForInstances =
                ClassifyResultArffLoader.loadClassifyDistributeForInstances("");
        List<Integer> newCfsArray = new ArrayList<Integer>();
        List<List<Integer>> newClassifyRightOrWrong = new ArrayList<List<Integer>>();
        List<List<Integer>> newClassifyErrorNo = new ArrayList<List<Integer>>();
        List<Double> newCorrectRateArray = new ArrayList<Double>();
        List<List<double[]>> newClassifyDistributeForInstances = new ArrayList<List<double[]>>();
        for (int i = 0; i < chooseClassifiers.size(); i++) {
            newCfsArray.add(cfsArray.get(((Integer)chooseClassifiers.get(i)).intValue()));
            newClassifyRightOrWrong.add(classifyRightOrWrong
                    .get(((Integer)chooseClassifiers.get(i)).intValue()));
            newClassifyErrorNo
                    .add(classifyErrorNo.get(((Integer)chooseClassifiers.get(i)).intValue()));
            newCorrectRateArray.add(correctRateArray.get(((Integer)chooseClassifiers
                    .get(i)).intValue()));
            newClassifyDistributeForInstances
                    .add(classifyDistributeForInstances.get(((Integer)chooseClassifiers
                            .get(i)).intValue()));
        }
        double newInitCorrectRate = 1.0D;
        double newInterval = 0.05D;
        List<Integer> ClassifierNo = new ArrayList<Integer>();
        List<Double> currentResult = new ArrayList<Double>();
        currentResult.add(Double.valueOf(Double.MAX_VALUE));
        currentResult.add(Double.valueOf(0.0D));
        currentResult.add(Double.valueOf(0.0D));
        double currentCorrectRate = 0.0D;
        Instances inputR = new Instances(input);
        Random random = new Random(1L);
        inputR.randomize(random);
        if (selectiveAlgorithm.equals("HCNRR")) {
            currentCorrectRate = HCNRR.doHCNRR(inputR, newInitCorrectRate,
                    newClassifyRightOrWrong, newClassifyErrorNo,
                    newCorrectRateArray, newClassifyDistributeForInstances,
                    currentResult, ClassifierNo);
        } else if (selectiveAlgorithm.equals("HCRR")) {
            currentCorrectRate = HCRR.doHCRR(inputR, newInitCorrectRate,
                    newClassifyRightOrWrong, newClassifyErrorNo,
                    newCorrectRateArray, newClassifyDistributeForInstances,
                    currentResult, ClassifierNo);
        } else if (selectiveAlgorithm.equals("EBSS")) {
            currentCorrectRate = EBSS.doEBSS(inputR, newInitCorrectRate,
                    newClassifyRightOrWrong, newClassifyErrorNo,
                    newCorrectRateArray, newClassifyDistributeForInstances,
                    currentResult, ClassifierNo);
        } else if (selectiveAlgorithm.equals("EFSS")) {
            currentCorrectRate = EFSS.doEFSS(inputR, newInitCorrectRate,
                    newClassifyRightOrWrong, newClassifyErrorNo,
                    newCorrectRateArray, newClassifyDistributeForInstances,
                    currentResult, ClassifierNo);
        } else if (selectiveAlgorithm.equals("CC")) {
            currentCorrectRate = CircleCombine.doCircleCombine(inputR,
                    newInitCorrectRate, newInterval, newClassifyRightOrWrong,
                    newClassifyErrorNo, newCorrectRateArray,
                    newClassifyDistributeForInstances, currentResult,
                    ClassifierNo, CCAlgorithm);
        } else if (selectiveAlgorithm.equals("DS")) {
            currentCorrectRate = CircleCombine.doCircleCombine(inputR,
                    newInitCorrectRate, newInterval, newClassifyRightOrWrong,
                    newClassifyErrorNo, newCorrectRateArray,
                    newClassifyDistributeForInstances, currentResult,
                    ClassifierNo, CCAlgorithm);
        } else {
            throw new Exception("Could not find selective algorithm:" +
                    selectiveAlgorithm);
        }
        classifer = ClassifierNo;
        return ClassifierNo;
    }

    public static void main(String[] args) throws Exception {}
}
