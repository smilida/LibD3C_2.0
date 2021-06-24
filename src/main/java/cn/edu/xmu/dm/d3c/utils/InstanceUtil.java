package cn.edu.xmu.dm.d3c.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.LibD3C;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;

/**
 * FUNCTION_getInstances: Read the file as Instance type.
 * FUNCTION_printInstances: Print the Instance.
 * FUNCTION_getJarPath: Get the jar path.
 * FUNCTION_createClassifyResultFile: Convert the txt file of ClassifyResult to arff file.
 *      attribute: the result(correct/incorrect) of validation data
 *      data: different classifier
 * FUNCTION_getCurrentTime:
 * FUNCTION_timeCompare:
 * FUNCTION_findTheMaxNo:
 * FUNCTION_SaveModel: Save the model trained.
 * FUNCTION_LoadModel:
 */

public class InstanceUtil {
    public String jarName;

    public String jarPath;

    public String pathPrefix = "Model";

    public static Instances getInstances(String filename) throws Exception {
        File file = new File(filename);
        return getInstances(file);
    }

    public static Instances getInstances(File file) throws Exception {
        Instances inst = null;
        try {
            ArffLoader loader = new ArffLoader();
            loader.setFile(file);
            inst = loader.getDataSet();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return inst;
    }

    public static void printInstances(Instances ins) {
        for (int i = 0; i < ins.numInstances(); i++)
            System.out.println(ins.instance(i));
    }

    public void getJarPath(Class clazz) throws Exception {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation()
                .getFile();
        path = URLDecoder.decode(path, "UTF-8");
        File jarFile = new File(path);
        this.jarName = jarFile.getName();
        File parent = jarFile.getParentFile();
        if (parent != null)
            this.jarPath = parent.getAbsolutePath();
    }

    public void createClassifyResultFile(int num, List<List<Integer>> classifyRightOrWrong) {
        try {
            getJarPath(InstanceUtil.class);
            String pathPrefix = "";
            File dir = new File(pathPrefix);
            if (!dir.isDirectory())
                dir.mkdir();
            String tempPath = String.valueOf(pathPrefix) + "ClassifyResult.arff";
            System.out.println(tempPath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempPath));
            String content = new String();
            content = "@relation ClassifyResult";
            writer.write(content);
            writer.newLine();
            int i;
            for (i = 1; i <= num; i++) {
                content = new String();
                content = "@attribute\tA" + i + "\t" + "{0,1}";
                writer.write(content);
                writer.newLine();
            }
            content = new String();
            content = "@data";
            writer.write(content);
            for (i = 0; i < classifyRightOrWrong.size(); i++) {
                writer.newLine();
                content = new String();
                for (int j = 0; j < ((List)classifyRightOrWrong.get(i)).size(); j++) {
                    if (j == 0) {
                        content = ((Integer)((List<Integer>)classifyRightOrWrong.get(i)).get(j)).toString();
                    } else {
                        content = String.valueOf(content) + "," + String.valueOf(classifyRightOrWrong.get(i).get(j));
                    }
                }
                writer.write(content);
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static long timeCompare(String t1, String t2) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d1 = formatter.parse(t1);
            d2 = formatter.parse(t2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long result = (d2.getTime() - d1.getTime()) / 1000L;
        return result;
    }

    public double findTheMaxNo(double[] distributeForInstance) {
        List<Double> temp = new ArrayList<Double>();
        for (int i = 0; i < distributeForInstance.length; i++)
            temp.add(Double.valueOf(distributeForInstance[i]));
        double max = ((Double)Collections.<Double>max(temp));
        double No = temp.indexOf(max);
        return No;
    }

    public void printInfo(Evaluation eval) throws Exception {
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString("LibD3C"));
        System.out.println(eval.toMatrixString());

    }

    public void SaveModel(LibD3C c, Instances data) throws Exception {
        Evaluation evaluation = new Evaluation(data);
        c.buildClassifier(data);
        evaluation.evaluateModel(c, data);
        printInfo(evaluation);
        weka.core.SerializationHelper.write("train.model", c);
        System.out.println("The model save as : train.model");
    }

    public void LoadModel(String modelPath, String testPath, String resultFilePath) throws Exception {
        try {
            LibD3C c1 = (LibD3C) weka.core.SerializationHelper.read(modelPath);
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(testPath);
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    resultFilePath));
            writer.write("predcition\torigin classs");
            writer.newLine();
            for (int j = 0; j < data.numInstances(); j++) {
                writer.write(String.valueOf(String.valueOf(c1.classifyInstance(data.get(j)))) +
                        ",");
                writer.write(String.valueOf(data.get(j).classValue()));
                writer.newLine();
            }
            writer.flush();
            writer.close();
            Evaluation evaluation = new Evaluation(data);
            evaluation.evaluateModel(c1, data);
            printInfo(evaluation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
