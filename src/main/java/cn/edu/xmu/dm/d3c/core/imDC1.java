package cn.edu.xmu.dm.d3c.core;

import cn.edu.xmu.dm.d3c.utils.InitClassifiers;
import cn.edu.xmu.dm.d3c.utils.InstanceUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class imDC1 {
    public Instances m_instances = null;

    public static Classifier[] bestClassifiers;

    public int lessLabelNum = 0;

    public static double lessLabel = 0.0D;

    public void getBestClassifier(Instances data) throws Exception {
        this.m_instances = data;
        int numInstances = this.m_instances.numInstances();
        int numAttributes = this.m_instances.numAttributes();
        Instances trainData = new Instances(this.m_instances, numInstances);
        this.m_instances = data;
        System.out.println("");
        double[] labelArray = this.m_instances
                .attributeToDoubleArray(numAttributes - 1);
        Hashtable<Double, Integer> labelHashTable = new Hashtable<Double, Integer>();
        double key = 0.0D;
        int value = 0;
        for (int i = 0; i <= labelArray.length - 1; i++) {
            key = labelArray[i];
            if (labelHashTable.containsKey(Double.valueOf(key))) {
                value = ((Integer)labelHashTable.get(Double.valueOf(key))).intValue() + 1;
                labelHashTable.put(Double.valueOf(key), Integer.valueOf(value));
            } else {
                labelHashTable.put(Double.valueOf(key), Integer.valueOf(1));
            }
        }
        Enumeration<Double> keys = labelHashTable.keys();
        key = ((Double)keys.nextElement()).doubleValue();
        this.lessLabelNum = ((Integer)labelHashTable.get(Double.valueOf(key))).intValue();
        lessLabel = key;
        while (keys.hasMoreElements()) {
            key = ((Double)keys.nextElement()).doubleValue();
            if (((Integer)labelHashTable.get(Double.valueOf(key))).intValue() < this.lessLabelNum) {
                this.lessLabelNum = ((Integer)labelHashTable.get(Double.valueOf(key))).intValue();
                lessLabel = key;
            }
        }
        System.out.println(""+ lessLabel + "," + this.lessLabelNum);
        System.out.println("");
                Instances instanceMore = new Instances(this.m_instances, numInstances);
        for (int j = numInstances - 1; j >= 0; j--) {
            if (this.m_instances.instance(j).classValue() == lessLabel) {
                trainData.add(this.m_instances.instance(j));
            } else {
                instanceMore.add(this.m_instances.instance(j));
            }
        }
        Random r = new Random();
        for (int k = 0; k < this.lessLabelNum; k++) {
            int randomNum = r.nextInt(instanceMore.numInstances() - 1);
            trainData.add(instanceMore.instance(randomNum));
        }
        List<String> nameOfClassifiers = new ArrayList<String>();
        List<String> pathOfClassifiers = new ArrayList<String>();
        List<String> parameterOfCV = new ArrayList<String>();
        Classifier[] classifiers = InitClassifiers.init("classifiers.xml",
                nameOfClassifiers, pathOfClassifiers, parameterOfCV);
        bestClassifiers = classifiers;
    }

    public static double query(double[] preres) {
        double max = 0.0D;
        int index = 0;
        for (int i = 0; i < preres.length; i++) {
            if (preres[i] > max) {
                max = preres[i];
                index = i;
            }
        }
        return index;
    }

    public static void main(String[] args) throws Exception {
        InstanceUtil iu = new InstanceUtil();
        Instances input = InstanceUtil.getInstances("D://train.arff");
        input.setClassIndex(input.numAttributes() - 1);
        imDC1 imdc = new imDC1();
    }
}
