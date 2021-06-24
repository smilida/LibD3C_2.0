package cn.edu.xmu.dm.d3c.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;

public class ClassifyResultArffLoader {
    static String pathPrefix = "";

    public ClassifyResultArffLoader() {
        File dir = new File(pathPrefix);
        if (!dir.isDirectory())
            dir.mkdir();
    }
    /**
     * FUNCTION_writeClassifyResult: Write down the result of weak classifiers.
     */
    public static void writeClassifyResult(Instances input, double validatePercent,
                                           String filepath, List<Integer> available_cfsArray,
                                           List<List<Integer>> available_classifyRightOrWrong,
                                           List<List<Integer>> available_classifyErrorNo,
                                           List<Double> correctRateArray,
                                           List<List<double[]>> available_classifyDistributeForInstances) throws IOException {
        InstanceUtil myutil = new InstanceUtil();
        myutil.createClassifyResultFile((int)(input.numInstances() * validatePercent),
                available_classifyRightOrWrong);
        String fcfsArray = String.valueOf(pathPrefix) + "cfsArray.txt";
        String fclassifyRightOrWrong = String.valueOf(pathPrefix) + "classifyRightOrWrong.txt";
        String fclassifyErrorNo = String.valueOf(pathPrefix) + "classifyErrorNo.txt";
        String fcorrectRateArray = String.valueOf(pathPrefix) + "correctRateArray.txt";
        String fclassifyDistributeForInstances = String.valueOf(pathPrefix) + "classifyDistributeForInstances.txt";
        System.out.println(fcorrectRateArray);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fcorrectRateArray));
        for (int i = 0; i < correctRateArray.size(); i++) {
            writer.write(((Double)correctRateArray.get(i)).toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
        BufferedWriter cfsArray = new BufferedWriter(new FileWriter(fcfsArray));
        BufferedWriter classifyRightOrWrongWriter = new BufferedWriter(new FileWriter(fclassifyRightOrWrong));
        BufferedWriter classifyErrorNoWriter = new BufferedWriter(new FileWriter(fclassifyErrorNo));
        BufferedWriter classifyDistributeForInstancesWriter = new BufferedWriter(new FileWriter(fclassifyDistributeForInstances));
        for (int j = 0; j < available_classifyRightOrWrong.size(); j++) {
            cfsArray.write(((Integer)available_cfsArray.get(j)).toString());
            int k;
            for (k = 0; k < ((List)available_classifyRightOrWrong.get(j)).size(); k++) {
                classifyRightOrWrongWriter.write(((Integer)((List<Integer>)available_classifyRightOrWrong.get(j)).get(k)).toString());
                classifyRightOrWrongWriter.write(",");
            }
            for (k = 0; k < ((List)available_classifyErrorNo.get(j)).size(); k++) {
                classifyErrorNoWriter.write(((Integer)((List<Integer>)available_classifyErrorNo.get(j)).get(k)).toString());
                classifyErrorNoWriter.write(",");
            }
            for (k = 0; k < ((List)available_classifyDistributeForInstances.get(j)).size(); k++) {
                double[] p = ((List<double[]>)available_classifyDistributeForInstances.get(j)).get(k);
                classifyDistributeForInstancesWriter.write(String.valueOf(p[0]));
                classifyDistributeForInstancesWriter.write("\t");
                classifyDistributeForInstancesWriter.write(String.valueOf(p[1]));
                classifyDistributeForInstancesWriter.write(",");
            }
            cfsArray.newLine();
            classifyRightOrWrongWriter.newLine();
            classifyErrorNoWriter.newLine();
            classifyDistributeForInstancesWriter.newLine();
        }
        cfsArray.flush();
        cfsArray.close();
        classifyRightOrWrongWriter.flush();
        classifyRightOrWrongWriter.close();
        classifyErrorNoWriter.flush();
        classifyErrorNoWriter.close();
        classifyDistributeForInstancesWriter.flush();
        classifyDistributeForInstancesWriter.close();
    }

    /**
     * FUNCTION_loadClassifyResultFromArff: Load the ClassifyResult.arff
     */
    public static Instances loadClassifyResultFromArff(String filepath) throws Exception {
        InstanceUtil myutil = new InstanceUtil();
        myutil.getJarPath(InstanceUtil.class);
        String tempPath = String.valueOf(pathPrefix) + "ClassifyResult.arff";
        Instances classifyResult = InstanceUtil.getInstances(tempPath);
        return classifyResult;
    }

    /**
     * FUNCTION_loadClassifyResultFromArff: Load the cfsArray.txt
     */
    public static List<Integer> loadCfsArray(String filepath) throws Exception {
        filepath = String.valueOf(pathPrefix) + "cfsArray.txt";
        List<Integer> cfsArray = new ArrayList<Integer>();
        File file = new File(filepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null)
                cfsArray.add(Integer.valueOf(Integer.parseInt(tempString)));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException iOException) {}
        }
        return cfsArray;
    }

    /**
     * FUNCTION_loadCorrectRate: Load the correctRateArray.txt
     */
    public static List<Double> loadCorrectRate(String filepath) throws Exception {
        filepath = String.valueOf(pathPrefix) + "correctRateArray.txt";
        List<Double> correctRateArray = new ArrayList<Double>();
        File file = new File(filepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null)
                correctRateArray.add(Double.valueOf(Double.parseDouble(tempString)));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException iOException) {}
        }
        return correctRateArray;
    }

    /**
     * FUNCTION_loadCorrectRate: Load the classifyRightOrWrong.txt
     */
    public static List<List<Integer>> loadClassifyRightOrWrong(String filepath) throws Exception {
        filepath = String.valueOf(pathPrefix) + "classifyRightOrWrong.txt";
        List<List<Integer>> classifyRightOrWrong = new ArrayList<List<Integer>>();
        File file = new File(filepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] tempStrings = tempString.split(",");
                List<Integer> lst = new ArrayList<Integer>();
                for (int i = 0; i < tempStrings.length; i++)
                    lst.add(Integer.valueOf(Integer.parseInt(tempStrings[i])));
                classifyRightOrWrong.add(lst);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException iOException) {}
        }
        return classifyRightOrWrong;
    }

    /**
     * FUNCTION_loadCorrectRate: Load the classifyDistributeForInstances.txt
     */
    public static List<List<double[]>> loadClassifyDistributeForInstances(String filepath) throws Exception {
        filepath = String.valueOf(pathPrefix) + "classifyDistributeForInstances.txt";
        List<List<double[]>> classifyDistributeForInstances = new ArrayList<List<double[]>>();
        File file = new File(filepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] tempStrings = tempString.split(",");
                List<double[]> lst = (List)new ArrayList<double[]>();
                for (int i = 0; i < tempStrings.length; i++) {
                    double[] p = new double[2];
                    p[0] = Double.parseDouble(tempStrings[i].split("\t")[0]);
                    p[1] = Double.parseDouble(tempStrings[i].split("\t")[1]);
                    lst.add(p);
                }
                classifyDistributeForInstances.add(lst);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException iOException) {}
        }
        return classifyDistributeForInstances;
    }

    /**
     * FUNCTION_loadCorrectRate: Load the classifyErrorNo.txt
     */
    public static List<List<Integer>> loadClassifyErrorNo(String filepath) throws Exception {
        filepath = String.valueOf(pathPrefix) + "classifyErrorNo.txt";
        List<List<Integer>> classifyErrorNo = new ArrayList<List<Integer>>();
        File file = new File(filepath);
        BufferedReader reader = null;
        String tempString = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((tempString = reader.readLine()) != null) {
                String[] tempStrings = tempString.split(",");
                List<Integer> lst = new ArrayList<Integer>();
                for (int i = 0; i < tempStrings.length; i++) {
                    if (!tempStrings[i].equals(""))
                        lst.add(Integer.valueOf(Integer.parseInt(tempStrings[i])));
                }
                classifyErrorNo.add(lst);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException iOException) {}
        }
        return classifyErrorNo;
    }

    /**
     * FUNCTION_loadCorrectRate: Load the chooseClassifiers.txt
     */
    public static List<Integer> loadChooseClassifiers(String filepath) throws Exception {
        filepath = String.valueOf(pathPrefix) + "chooseClassifiers.txt";
        List<Integer> chooseClassifiers = new ArrayList<Integer>();
        File file = new File(filepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null)
                chooseClassifiers.add(Integer.valueOf(Integer.parseInt(tempString)));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException iOException) {}
        }
        return chooseClassifiers;
    }
}
