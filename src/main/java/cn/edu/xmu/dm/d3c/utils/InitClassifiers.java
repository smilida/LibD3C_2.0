package cn.edu.xmu.dm.d3c.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Utils;

/**
 * FUNCTION: Read the classifiers.xml file to initialize the classifier.
 *
 */

public class InitClassifiers {
    public static String[] classifiersName;

    public static ArrayList<String> classifiersOption = new ArrayList<String>();

    public static Classifier[] init(String filePath, List<String> nameOfClassifiers, List<String> pathOfClassifiers, List<String> parameterOfCV) {
        Logger logger = Logger.getLogger(InitClassifiers.class);
        Classifier[] cfsArray = { (Classifier)new J48() };
        try {
            File f = new File(filePath);
            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            Element root = doc.getRootElement();
            List<Classifier> lst = new ArrayList<Classifier>();
            Iterator<Element> iter = root.elementIterator("classifier");
            while (iter.hasNext()) {
                Element foo = iter.next();
                String classifierName = foo.attributeValue("name").trim();
                String classifierPath = foo.element("parameter")
                        .elementText("class").trim();
                String option = foo.element("parameter").elementText("options")
                        .trim();
                logger.info("classifierName:" + classifierName);
                logger.info("options:" + option);
                String[] options = Utils.splitOptions(option);
                Classifier cfs = null;
                if (!classifierName.startsWith("IB") && !classifierName.equals("LibSVM")) {
                    cfs = AbstractClassifier.forName(classifierPath, options);
                    lst.add(cfs);
                    nameOfClassifiers.add(classifierName);
                    pathOfClassifiers.add(classifierPath);
                    parameterOfCV.add("");
                    classifiersOption.add(option);
                } else if (classifierName.startsWith("IB")) {
                    IBk ibCfs = (IBk)Class.forName(classifierPath).newInstance();
                    String other = foo.element("parameter").elementText("other")
                            .trim();
                    ibCfs.setKNN(Integer.parseInt(other));
                    lst.add(ibCfs);
                    nameOfClassifiers.add(classifierName);
                    pathOfClassifiers.add(classifierPath);
                    parameterOfCV.add("");
                    classifiersOption.add(option);
                } else if (classifierName.equals("LibSVM")){
                    LibSVM libsvm = (LibSVM) Class.forName(classifierPath).newInstance();
                    String Gamma = foo.element("parameter").elementText("Gamma").trim();
                    String Cost = foo.element("parameter").elementText("Cost").trim();
                    libsvm.setGamma(Double.parseDouble(Gamma));
                    libsvm.setCost(Double.parseDouble(Cost));
                    lst.add(libsvm);
                    nameOfClassifiers.add(classifierName);
                    pathOfClassifiers.add(classifierPath);
                    parameterOfCV.add("");
                }
                System.out.println(cfs);
            }
            cfsArray = lst.<Classifier>toArray(new Classifier[lst.size()]);
            classifiersName = nameOfClassifiers.<String>toArray(new String[nameOfClassifiers.size()]);
            return cfsArray;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
