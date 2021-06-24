package cn.edu.xmu.dm.d3c.core;

import java.util.ArrayList;
import java.util.List;
import weka.classifiers.Classifier;

/**
 * FUNCTION: A class of public parameters
 *
 * PARAM: cfNum is the number of classifiers
 */

public class Parameterz {
    public List<List<Integer>> classifyRightOrWrong;

    public List<List<Integer>> classifyErrorNo;

    public List<Double> correctRateArray;

    public List<List<double[]>> classifyDistributeForInstances;

    public List<Integer> qqs;

    public Classifier[] available_cfsArray;

    public List<List<Integer>> available_classifyRightOrWrong;

    public List<List<Integer>> available_classifyErrorNo;

    public List<Double> available_correctRateArray;

    public List<List<double[]>> available_classifyDistributeForInstances;

    public List<Integer> temp_qc_1;

    public Parameterz(int cfNum) {
        this.classifyRightOrWrong = new ArrayList<List<Integer>>();
        this.classifyErrorNo = new ArrayList<List<Integer>>();
        this.correctRateArray = new ArrayList<Double>();
        this.classifyDistributeForInstances = new ArrayList<List<double[]>>();
        this.qqs = new ArrayList<Integer>();
        this.available_cfsArray = new Classifier[cfNum];
        this.available_classifyRightOrWrong = new ArrayList<List<Integer>>();
        this.available_classifyErrorNo = new ArrayList<List<Integer>>();
        this.available_correctRateArray = new ArrayList<Double>();
        this.available_classifyDistributeForInstances = new ArrayList<List<double[]>>();
        this.temp_qc_1 = new ArrayList<Integer>();
    }
}
