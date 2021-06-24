package cn.edu.xmu.dm.d3c.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.core.Instances;
/**
 * FUNCTION_
 *
 *
 */
public class myClassifier implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instances m_instances = null;

    private Instances less_instances = null;

    private Instances more_instances = null;

    private int bestClassifierNum = imDC1.bestClassifiers.length;

    private Classifier[] bestClassifiers = new Classifier[this.bestClassifierNum];

    private int iterationNum = imDC1.bestClassifiers.length;

    private Classifier[] iterationClassifiers = null;

    public static double[] iterationWeight;

    private int lessLabelNum = 0;

    private double lessLabel = 0.0D;

    private double[] weight;

    private int[] flag;

    protected List<Classifier> m_preBuiltClassifiers = new ArrayList<Classifier>();

    public myClassifier(Instances m_instances, Classifier[] bestClassifiers, int lessLabelNum, double lessLabel, int bestClassifierNum) {
        this.m_instances = m_instances;
        this.bestClassifiers = bestClassifiers;
        this.lessLabelNum = lessLabelNum;
        this.lessLabel = lessLabel;
        this.bestClassifierNum = bestClassifierNum;
    }

    public myClassifier() {}

    public void initmyclassifier() throws Exception {
        this.less_instances = new Instances(this.m_instances, this.m_instances.numInstances());
        this.more_instances = new Instances(this.m_instances, this.m_instances.numInstances());
        int numInstances = this.m_instances.numInstances();
        double moreWeight = 1.0D;
        this.weight = new double[numInstances - this.lessLabelNum];
        this.flag = new int[numInstances - this.lessLabelNum];
        int temp = 0;
        int i;
        for (i = numInstances - 1; i >= 0; i--) {
            if (this.m_instances.instance(i).classValue() == this.lessLabel) {
                this.less_instances.add(this.m_instances.instance(i));
            } else {
                this.more_instances.add(this.m_instances.instance(i));
                this.weight[temp] = moreWeight;
                temp++;
            }
        }
        this.iterationClassifiers = new Classifier[this.iterationNum];
        iterationWeight = new double[this.iterationNum];
        for (i = 0; i < this.iterationNum; i++)
            this.iterationClassifiers[i] = this.bestClassifiers[i];
    }

    public String getRevision() {
        return "";
    }

    public Classifier[] build(Instances data) throws Exception {
        System.out.println("buildClassifier......");
        int num_more = 0, num_more_wrong = 0;
        int num_less = 0, num_less_wrong = 0;
        for (int i = 0; i < this.iterationNum; i++) {
            buildClassifierWithWeights(i);
            int j;
            for (j = 0; j < this.more_instances.numInstances(); j++) {
                double instanceResult = this.iterationClassifiers[i]
                        .classifyInstance(this.more_instances.instance(j));
                double instanceReal = this.more_instances.instance(j).classValue();
                if (instanceResult == instanceReal) {
                    this.flag[j] = 1;
                    num_more++;
                } else {
                    this.flag[j] = 2;
                    num_more++;
                    num_more_wrong++;
                }
            }
            for (j = 0; j < this.less_instances.numInstances(); j++) {
                double instanceResult = this.iterationClassifiers[i]
                        .classifyInstance(this.less_instances.instance(j));
                double instanceReal = this.less_instances.instance(j).classValue();
                if (instanceResult == instanceReal) {
                    num_less++;
                } else {
                    num_less++;
                    num_less_wrong++;
                }
            }
            setWeights(num_more, num_more_wrong);
            double g = (1.0D * (num_more - num_more_wrong) / num_more + 1.0D * (
                    num_less - num_less_wrong) / num_less) / 2.0D;
            iterationWeight[i] = g;
        }
        return this.iterationClassifiers;
    }

    protected void buildClassifierWithWeights(int iteration) throws Exception {
        Instances trainData = selectWeightQuantile();
        for (int i = 0; i < this.less_instances.numInstances(); i++)
            trainData.add(this.less_instances.instance(i));
        this.iterationClassifiers[iteration].buildClassifier(trainData);
    }

    protected Instances selectWeightQuantile() {
        Instances trainData = new Instances(this.more_instances,
                this.more_instances.numInstances());
        Random r = new Random();
        double sum = 0.0D;
        int index = 0;
        int number = 0;
        HashMap<Object, Object> map_Data = new HashMap<Object, Object>();
        for (int j = 0; j < this.weight.length; j++)
            map_Data.put(String.valueOf(j),
                    String.valueOf(r.nextInt(100) / 100.0D * this.weight[j]));
        List<Map.Entry<String, String>> list_Data = new ArrayList<Map.Entry<String, String>>(
                (Collection)map_Data.entrySet());
        Collections.sort(list_Data,
                new Comparator<Map.Entry<String, String>>() {
                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                        if (o2.getValue() != null && o1.getValue() != null && (
                                (String)o2.getValue()).compareTo(o1.getValue()) > 0)
                            return 1;
                        if (o2.getValue() != null &&
                                o1.getValue() != null && (
                                (String)o2.getValue()).compareTo(o1.getValue()) == 0)
                            return 0;
                        return -1;
                    }
                });
        Iterator<Map.Entry<String, String>> iterator = list_Data.iterator();
        while (iterator.hasNext() && number <= 2 * this.lessLabelNum) {
            index = Integer.parseInt((String)((Map.Entry)iterator.next()).getKey());
            trainData.add(this.more_instances.instance(index));
            number++;
        }
        return trainData;
    }

    protected void setWeights(int num_more, int num_more_wrong) {
        double w = 0.0D;
        double weightSum = 0.0D;
        int i;
        for (i = 0; i < this.flag.length; i++) {
            if (this.flag[i] == 1) {
                w = 0.0D;
            } else {
                w = ((num_more_wrong + 1) / num_more);
            }
            this.weight[i] = this.weight[i] + w;
            weightSum += this.weight[i];
        }
        for (i = 0; i < this.weight.length; i++)
            this.weight[i] = this.weight[i] / weightSum;
    }
}
