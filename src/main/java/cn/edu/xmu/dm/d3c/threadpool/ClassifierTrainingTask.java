package cn.edu.xmu.dm.d3c.threadpool;

import cn.edu.xmu.dm.d3c.core.SelectiveEnsemble;
import java.util.List;
import java.util.concurrent.Callable;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * FUNCTION: Train a weak classifier
 *
 */

public class ClassifierTrainingTask implements Callable<Classifier> {
    private double value;
    private Instances input;
    private Instances train;
    private Instances validation;
    private Classifier classifier;
    private List<List<Integer>> classifyRightOrWrong;
    private List<List<Integer>> classifyErrorNo;
    private List<Double> correctRateArray;
    private List<List<double[]>> classifyDistributeForInstances;
    private int index;
    private List<Integer> qqs;
    private List<String> pathOfClassifiers;
    private List<String> parameterOfCV;
    private long executeTime;
    private boolean isFinished;

    public ClassifierTrainingTask(Instances input, Instances train,
                                  Instances validation, Classifier classifier,
                                  List<List<Integer>> classifyRightOrWrong,
                                  List<List<Integer>> classifyErrorNo, List<Double> correctRateArray,
                                  List<List<double[]>> classifyDistributeForInstances,
                                  int index, List<Integer> qqs, List<String> pathOfClassifiers,
                                  List<String> parameterOfCV) {
        this.input = input;
        this.train = train;
        this.validation = validation;
        this.classifier = classifier;
        this.classifyRightOrWrong = classifyRightOrWrong;
        this.classifyErrorNo = classifyErrorNo;
        this.correctRateArray = correctRateArray;
        this.classifyDistributeForInstances = classifyDistributeForInstances;
        this.index = index;
        this.qqs = qqs;
        this.pathOfClassifiers = pathOfClassifiers;
        this.parameterOfCV = parameterOfCV;
    }

    public Classifier call() throws Exception {
        SelectiveEnsemble se = new SelectiveEnsemble();
        Classifier cf = se.CrossValidationModelForDistribute(
                this.input,
                this.train,
                this.validation,
                this.classifier,
                this.classifyRightOrWrong,
                this.classifyErrorNo,
                this.correctRateArray,
                this.classifyDistributeForInstances,
                this.index,
                this.qqs,
                this.pathOfClassifiers,
                this.parameterOfCV);
        this.isFinished = true;
        return cf;
    }
}
