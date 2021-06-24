//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package weka.classifiers.functions;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class LibSVM extends AbstractClassifier implements TechnicalInformationHandler {
    protected static final String CLASS_SVM = "libsvm.svm";
    protected static final String CLASS_SVMMODEL = "libsvm.svm_model";
    protected static final String CLASS_SVMPROBLEM = "libsvm.svm_problem";
    protected static final String CLASS_SVMPARAMETER = "libsvm.svm_parameter";
    protected static final String CLASS_SVMNODE = "libsvm.svm_node";
    protected static final long serialVersionUID = 14172L;
    protected Object m_Model;
    protected Filter m_Filter = null;
    protected ReplaceMissingValues m_ReplaceMissingValues;
    protected boolean m_Normalize = false;
    private boolean m_noReplaceMissingValues;
    public static final int SVMTYPE_C_SVC = 0;
    public static final int SVMTYPE_NU_SVC = 1;
    public static final int SVMTYPE_ONE_CLASS_SVM = 2;
    public static final int SVMTYPE_EPSILON_SVR = 3;
    public static final int SVMTYPE_NU_SVR = 4;
    public static final Tag[] TAGS_SVMTYPE = new Tag[]{new Tag(0, "C-SVC (classification)"), new Tag(1, "nu-SVC (classification)"), new Tag(2, "one-class SVM (classification)"), new Tag(3, "epsilon-SVR (regression)"), new Tag(4, "nu-SVR (regression)")};
    protected int m_SVMType = 0;
    public static final int KERNELTYPE_LINEAR = 0;
    public static final int KERNELTYPE_POLYNOMIAL = 1;
    public static final int KERNELTYPE_RBF = 2;
    public static final int KERNELTYPE_SIGMOID = 3;
    public static final Tag[] TAGS_KERNELTYPE = new Tag[]{new Tag(0, "linear: u'*v"), new Tag(1, "polynomial: (gamma*u'*v + coef0)^degree"), new Tag(2, "radial basis function: exp(-gamma*|u-v|^2)"), new Tag(3, "sigmoid: tanh(gamma*u'*v + coef0)")};
    protected int m_KernelType = 2;
    protected int m_Degree = 3;
    protected double m_Gamma = 0.0D;
    protected double m_GammaActual = 0.0D;
    protected double m_Coef0 = 0.0D;
    protected double m_CacheSize = 40.0D;
    protected double m_eps = 0.001D;
    protected double m_Cost = 1.0D;
    protected int[] m_WeightLabel = new int[0];
    protected double[] m_Weight = new double[0];
    protected double m_nu = 0.5D;
    protected double m_Loss = 0.1D;
    protected boolean m_Shrinking = true;
    protected boolean m_ProbabilityEstimates = false;
    protected File m_ModelFile = new File(System.getProperty("user.dir"));
    protected static boolean m_Present = false;

    public LibSVM() {
    }

    public String globalInfo() {
        return "A wrapper class for the libsvm tools (the libsvm classes, typically the jar file, need to be in the classpath to use this classifier).\nLibSVM runs faster than SMO since it uses LibSVM to build the SVM classifier.\nLibSVM allows users to experiment with One-class SVM, Regressing SVM, and nu-SVM supported by LibSVM tool. LibSVM reports many useful statistics about LibSVM classifier (e.g., confusion matrix,precision, recall, ROC score, etc.).\n\n" + this.getTechnicalInformation().toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result = new TechnicalInformation(Type.MISC);
        result.setValue(Field.AUTHOR, "Yasser EL-Manzalawy");
        result.setValue(Field.YEAR, "2005");
        result.setValue(Field.TITLE, "WLSVM");
        result.setValue(Field.NOTE, "LibSVM was originally developed as 'WLSVM'");
        result.setValue(Field.URL, "http://www.cs.iastate.edu/~yasser/wlsvm/");
        result.setValue(Field.NOTE, "You don't need to include the WLSVM package in the CLASSPATH");
        TechnicalInformation additional = result.add(Type.MISC);
        additional.setValue(Field.AUTHOR, "Chih-Chung Chang and Chih-Jen Lin");
        additional.setValue(Field.TITLE, "LIBSVM - A Library for Support Vector Machines");
        additional.setValue(Field.YEAR, "2001");
        additional.setValue(Field.URL, "http://www.csie.ntu.edu.tw/~cjlin/libsvm/");
        additional.setValue(Field.NOTE, "The Weka classifier works with version 2.82 of LIBSVM");
        return result;
    }

    public Enumeration listOptions() {
        Vector result = new Vector();
        result.addElement(new Option("\tSet type of SVM (default: 0)\n\t\t 0 = C-SVC\n\t\t 1 = nu-SVC\n\t\t 2 = one-class SVM\n\t\t 3 = epsilon-SVR\n\t\t 4 = nu-SVR", "S", 1, "-S <int>"));
        result.addElement(new Option("\tSet type of kernel function (default: 2)\n\t\t 0 = linear: u'*v\n\t\t 1 = polynomial: (gamma*u'*v + coef0)^degree\n\t\t 2 = radial basis function: exp(-gamma*|u-v|^2)\n\t\t 3 = sigmoid: tanh(gamma*u'*v + coef0)", "K", 1, "-K <int>"));
        result.addElement(new Option("\tSet degree in kernel function (default: 3)", "D", 1, "-D <int>"));
        result.addElement(new Option("\tSet gamma in kernel function (default: 1/k)", "G", 1, "-G <double>"));
        result.addElement(new Option("\tSet coef0 in kernel function (default: 0)", "R", 1, "-R <double>"));
        result.addElement(new Option("\tSet the parameter C of C-SVC, epsilon-SVR, and nu-SVR\n\t (default: 1)", "C", 1, "-C <double>"));
        result.addElement(new Option("\tSet the parameter nu of nu-SVC, one-class SVM, and nu-SVR\n\t (default: 0.5)", "N", 1, "-N <double>"));
        result.addElement(new Option("\tTurns on normalization of input data (default: off)", "Z", 0, "-Z"));
        result.addElement(new Option("\tTurn off nominal to binary conversion.\n\tWARNING: use only if your data is all numeric!", "J", 0, "-J"));
        result.addElement(new Option("\tTurn off missing value replacement.\n\tWARNING: use only if your data has no missing values.", "V", 0, "-V"));
        result.addElement(new Option("\tSet the epsilon in loss function of epsilon-SVR (default: 0.1)", "P", 1, "-P <double>"));
        result.addElement(new Option("\tSet cache memory size in MB (default: 40)", "M", 1, "-M <double>"));
        result.addElement(new Option("\tSet tolerance of termination criterion (default: 0.001)", "E", 1, "-E <double>"));
        result.addElement(new Option("\tTurns the shrinking heuristics off (default: on)", "H", 0, "-H"));
        result.addElement(new Option("\tSet the parameters C of class i to weight[i]*C, for C-SVC.\n\tE.g., for a 3-class problem, you could use \"1 1 1\" for equally\n\tweighted classes.\n\t(default: 1 for all classes)", "W", 1, "-W <double>"));
        result.addElement(new Option("\tGenerate probability estimates for classification", "B", 0, "-B"));
        result.addElement(new Option("\tSpecifies the filename to save the libsvm-internal model to.\n\tGets ignored if a directory is provided.", "model", 1, "-model <file>"));
        Enumeration en = super.listOptions();

        while(en.hasMoreElements()) {
            result.addElement(en.nextElement());
        }

        return result.elements();
    }

    public void setOptions(String[] options) throws Exception {
        String tmpStr = Utils.getOption('S', options);
        if (tmpStr.length() != 0) {
            this.setSVMType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_SVMTYPE));
        } else {
            this.setSVMType(new SelectedTag(0, TAGS_SVMTYPE));
        }

        tmpStr = Utils.getOption('K', options);
        if (tmpStr.length() != 0) {
            this.setKernelType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_KERNELTYPE));
        } else {
            this.setKernelType(new SelectedTag(2, TAGS_KERNELTYPE));
        }

        tmpStr = Utils.getOption('D', options);
        if (tmpStr.length() != 0) {
            this.setDegree(Integer.parseInt(tmpStr));
        } else {
            this.setDegree(3);
        }

        tmpStr = Utils.getOption('G', options);
        if (tmpStr.length() != 0) {
            this.setGamma(Double.parseDouble(tmpStr));
        } else {
            this.setGamma(0.0D);
        }

        tmpStr = Utils.getOption('R', options);
        if (tmpStr.length() != 0) {
            this.setCoef0(Double.parseDouble(tmpStr));
        } else {
            this.setCoef0(0.0D);
        }

        tmpStr = Utils.getOption('N', options);
        if (tmpStr.length() != 0) {
            this.setNu(Double.parseDouble(tmpStr));
        } else {
            this.setNu(0.5D);
        }

        tmpStr = Utils.getOption('M', options);
        if (tmpStr.length() != 0) {
            this.setCacheSize(Double.parseDouble(tmpStr));
        } else {
            this.setCacheSize(40.0D);
        }

        tmpStr = Utils.getOption('C', options);
        if (tmpStr.length() != 0) {
            this.setCost(Double.parseDouble(tmpStr));
        } else {
            this.setCost(1.0D);
        }

        tmpStr = Utils.getOption('E', options);
        if (tmpStr.length() != 0) {
            this.setEps(Double.parseDouble(tmpStr));
        } else {
            this.setEps(0.001D);
        }

        this.setNormalize(Utils.getFlag('Z', options));
        this.setDoNotReplaceMissingValues(Utils.getFlag("V", options));
        tmpStr = Utils.getOption('P', options);
        if (tmpStr.length() != 0) {
            this.setLoss(Double.parseDouble(tmpStr));
        } else {
            this.setLoss(0.1D);
        }

        this.setShrinking(!Utils.getFlag('H', options));
        this.setWeights(Utils.getOption('W', options));
        this.setProbabilityEstimates(Utils.getFlag('B', options));
        tmpStr = Utils.getOption("model", options);
        if (tmpStr.length() == 0) {
            this.m_ModelFile = new File(System.getProperty("user.dir"));
        } else {
            this.m_ModelFile = new File(tmpStr);
        }

    }

    public String[] getOptions() {
        Vector result = new Vector();
        result.add("-S");
        result.add("" + this.m_SVMType);
        result.add("-K");
        result.add("" + this.m_KernelType);
        result.add("-D");
        result.add("" + this.getDegree());
        result.add("-G");
        result.add("" + this.getGamma());
        result.add("-R");
        result.add("" + this.getCoef0());
        result.add("-N");
        result.add("" + this.getNu());
        result.add("-M");
        result.add("" + this.getCacheSize());
        result.add("-C");
        result.add("" + this.getCost());
        result.add("-E");
        result.add("" + this.getEps());
        result.add("-P");
        result.add("" + this.getLoss());
        if (!this.getShrinking()) {
            result.add("-H");
        }

        if (this.getNormalize()) {
            result.add("-Z");
        }

        if (this.getDoNotReplaceMissingValues()) {
            result.add("-V");
        }

        if (this.getWeights().length() != 0) {
            result.add("-W");
            result.add("" + this.getWeights());
        }

        if (this.getProbabilityEstimates()) {
            result.add("-B");
        }

        result.add("-model");
        result.add(this.m_ModelFile.getAbsolutePath());
        return (String[])((String[])result.toArray(new String[result.size()]));
    }

    public static boolean isPresent() {
        return m_Present;
    }

    public void setSVMType(SelectedTag value) {
        if (value.getTags() == TAGS_SVMTYPE) {
            this.m_SVMType = value.getSelectedTag().getID();
        }

    }

    public SelectedTag getSVMType() {
        return new SelectedTag(this.m_SVMType, TAGS_SVMTYPE);
    }

    public String SVMTypeTipText() {
        return "The type of SVM to use.";
    }

    public void setKernelType(SelectedTag value) {
        if (value.getTags() == TAGS_KERNELTYPE) {
            this.m_KernelType = value.getSelectedTag().getID();
        }

    }

    public SelectedTag getKernelType() {
        return new SelectedTag(this.m_KernelType, TAGS_KERNELTYPE);
    }

    public String kernelTypeTipText() {
        return "The type of kernel to use";
    }

    public void setDegree(int value) {
        this.m_Degree = value;
    }

    public int getDegree() {
        return this.m_Degree;
    }

    public String degreeTipText() {
        return "The degree of the kernel.";
    }

    public void setGamma(double value) {
        this.m_Gamma = value;
    }

    public double getGamma() {
        return this.m_Gamma;
    }

    public String gammaTipText() {
        return "The gamma to use, if 0 then 1/max_index is used.";
    }

    public void setCoef0(double value) {
        this.m_Coef0 = value;
    }

    public double getCoef0() {
        return this.m_Coef0;
    }

    public String coef0TipText() {
        return "The coefficient to use.";
    }

    public void setNu(double value) {
        this.m_nu = value;
    }

    public double getNu() {
        return this.m_nu;
    }

    public String nuTipText() {
        return "The value of nu for nu-SVC, one-class SVM and nu-SVR.";
    }

    public void setCacheSize(double value) {
        this.m_CacheSize = value;
    }

    public double getCacheSize() {
        return this.m_CacheSize;
    }

    public String cacheSizeTipText() {
        return "The cache size in MB.";
    }

    public void setCost(double value) {
        this.m_Cost = value;
    }

    public double getCost() {
        return this.m_Cost;
    }

    public String costTipText() {
        return "The cost parameter C for C-SVC, epsilon-SVR and nu-SVR.";
    }

    public void setEps(double value) {
        this.m_eps = value;
    }

    public double getEps() {
        return this.m_eps;
    }

    public String epsTipText() {
        return "The tolerance of the termination criterion.";
    }

    public void setLoss(double value) {
        this.m_Loss = value;
    }

    public double getLoss() {
        return this.m_Loss;
    }

    public String lossTipText() {
        return "The epsilon for the loss function in epsilon-SVR.";
    }

    public void setShrinking(boolean value) {
        this.m_Shrinking = value;
    }

    public boolean getShrinking() {
        return this.m_Shrinking;
    }

    public String shrinkingTipText() {
        return "Whether to use the shrinking heuristic.";
    }

    public void setNormalize(boolean value) {
        this.m_Normalize = value;
    }

    public boolean getNormalize() {
        return this.m_Normalize;
    }

    public String normalizeTipText() {
        return "Whether to normalize the data.";
    }

    public String doNotReplaceMissingValuesTipText() {
        return "Whether to turn off automatic replacement of missing values. WARNING: set to true only if the data does not contain missing values.";
    }

    public void setDoNotReplaceMissingValues(boolean b) {
        this.m_noReplaceMissingValues = b;
    }

    public boolean getDoNotReplaceMissingValues() {
        return this.m_noReplaceMissingValues;
    }

    public void setWeights(String weightsStr) {
        StringTokenizer tok = new StringTokenizer(weightsStr, " ");
        this.m_Weight = new double[tok.countTokens()];
        this.m_WeightLabel = new int[tok.countTokens()];
        if (this.m_Weight.length == 0) {
            System.out.println("Zero Weights processed. Default weights will be used");
        }

        for(int i = 0; i < this.m_Weight.length; this.m_WeightLabel[i] = i++) {
            this.m_Weight[i] = Double.parseDouble(tok.nextToken());
        }

    }

    public String getWeights() {
        String result = "";

        for(int i = 0; i < this.m_Weight.length; ++i) {
            if (i > 0) {
                result = result + " ";
            }

            result = result + Double.toString(this.m_Weight[i]);
        }

        return result;
    }

    public String weightsTipText() {
        return "The weights to use for the classes (blank-separated list, eg, \"1 1 1\" for a 3-class problem), if empty 1 is used by default.";
    }

    public void setProbabilityEstimates(boolean value) {
        this.m_ProbabilityEstimates = value;
    }

    public boolean getProbabilityEstimates() {
        return this.m_ProbabilityEstimates;
    }

    public String probabilityEstimatesTipText() {
        return "Whether to generate probability estimates instead of -1/+1 for classification problems.";
    }

    public void setModelFile(File value) {
        if (value == null) {
            this.m_ModelFile = new File(System.getProperty("user.dir"));
        } else {
            this.m_ModelFile = value;
        }

    }

    public File getModelFile() {
        return this.m_ModelFile;
    }

    public String modelFileTipText() {
        return "The file to save the libsvm-internal model to; no model is saved if pointing to a directory.";
    }

    protected void setField(Object o, String name, Object value) {
        try {
            java.lang.reflect.Field f = o.getClass().getField(name);
            f.set(o, value);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    protected void setField(Object o, String name, int index, Object value) {
        try {
            java.lang.reflect.Field f = o.getClass().getField(name);
            Array.set(f.get(o), index, value);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    protected Object getField(Object o, String name) {
        Object result;
        try {
            java.lang.reflect.Field f = o.getClass().getField(name);
            result = f.get(o);
        } catch (Exception var6) {
            var6.printStackTrace();
            result = null;
        }

        return result;
    }

    protected void newArray(Object o, String name, Class type, int length) {
        this.newArray(o, name, type, new int[]{length});
    }

    protected void newArray(Object o, String name, Class type, int[] dimensions) {
        try {
            java.lang.reflect.Field f = o.getClass().getField(name);
            f.set(o, Array.newInstance(type, dimensions));
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    protected Object invokeMethod(Object o, String name, Class[] paramClasses, Object[] paramValues) {
        Object result = null;

        try {
            Method m = o.getClass().getMethod(name, paramClasses);
            result = m.invoke(o, paramValues);
        } catch (Exception var8) {
            var8.printStackTrace();
            result = null;
        }

        return result;
    }

    protected Object getParameters() {
        Object result;
        try {
            result = Class.forName("libsvm.svm_parameter").newInstance();
            this.setField(result, "svm_type", new Integer(this.m_SVMType));
            this.setField(result, "kernel_type", new Integer(this.m_KernelType));
            this.setField(result, "degree", new Integer(this.m_Degree));
            this.setField(result, "gamma", new Double(this.m_GammaActual));
            this.setField(result, "coef0", new Double(this.m_Coef0));
            this.setField(result, "nu", new Double(this.m_nu));
            this.setField(result, "cache_size", new Double(this.m_CacheSize));
            this.setField(result, "C", new Double(this.m_Cost));
            this.setField(result, "eps", new Double(this.m_eps));
            this.setField(result, "p", new Double(this.m_Loss));
            this.setField(result, "shrinking", new Integer(this.m_Shrinking ? 1 : 0));
            this.setField(result, "nr_weight", new Integer(this.m_Weight.length));
            this.setField(result, "probability", new Integer(this.m_ProbabilityEstimates ? 1 : 0));
            this.newArray(result, "weight", Double.TYPE, this.m_Weight.length);
            this.newArray(result, "weight_label", Integer.TYPE, this.m_Weight.length);

            for(int i = 0; i < this.m_Weight.length; ++i) {
                this.setField(result, "weight", i, new Double(this.m_Weight[i]));
                this.setField(result, "weight_label", i, new Integer(this.m_WeightLabel[i]));
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            result = null;
        }

        return result;
    }

    protected Object getProblem(Vector vx, Vector vy) {
        Object result;
        try {
            result = Class.forName("libsvm.svm_problem").newInstance();
            this.setField(result, "l", new Integer(vy.size()));
            this.newArray(result, "x", Class.forName("libsvm.svm_node"), new int[]{vy.size(), 0});

            int i;
            for(i = 0; i < vy.size(); ++i) {
                this.setField(result, "x", i, vx.elementAt(i));
            }

            this.newArray(result, "y", Double.TYPE, vy.size());

            for(i = 0; i < vy.size(); ++i) {
                this.setField(result, "y", i, vy.elementAt(i));
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            result = null;
        }

        return result;
    }

    protected Object instanceToArray(Instance instance) throws Exception {
        int count = 0;

        int i;
        for(i = 0; i < instance.numValues(); ++i) {
            if (instance.index(i) != instance.classIndex() && instance.valueSparse(i) != 0.0D) {
                ++count;
            }
        }

        Object result = Array.newInstance(Class.forName("libsvm.svm_node"), count);
        int index = 0;

        for(i = 0; i < instance.numValues(); ++i) {
            int idx = instance.index(i);
            if (idx != instance.classIndex() && instance.valueSparse(i) != 0.0D) {
                Array.set(result, index, Class.forName("libsvm.svm_node").newInstance());
                this.setField(Array.get(result, index), "index", new Integer(idx + 1));
                this.setField(Array.get(result, index), "value", new Double(instance.valueSparse(i)));
                ++index;
            }
        }

        return result;
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        int[] labels = new int[instance.numClasses()];
        double[] prob_estimates = null;
        if (this.m_ProbabilityEstimates) {
            this.invokeMethod(Class.forName("libsvm.svm").newInstance(), "svm_get_labels", new Class[]{Class.forName("libsvm.svm_model"), Array.newInstance(Integer.TYPE, instance.numClasses()).getClass()}, new Object[]{this.m_Model, labels});
            prob_estimates = new double[instance.numClasses()];
        }

        if (!this.getDoNotReplaceMissingValues()) {
            this.m_ReplaceMissingValues.input(instance);
            this.m_ReplaceMissingValues.batchFinished();
            instance = this.m_ReplaceMissingValues.output();
        }

        if (this.m_Filter != null) {
            this.m_Filter.input(instance);
            this.m_Filter.batchFinished();
            instance = this.m_Filter.output();
        }

        Object x = this.instanceToArray(instance);
        double[] result = new double[instance.numClasses()];
        double v;
        if (!this.m_ProbabilityEstimates || this.m_SVMType != 0 && this.m_SVMType != 1) {
            v = (Double)this.invokeMethod(Class.forName("libsvm.svm").newInstance(), "svm_predict", new Class[]{Class.forName("libsvm.svm_model"), Array.newInstance(Class.forName("libsvm.svm_node"), Array.getLength(x)).getClass()}, new Object[]{this.m_Model, x});
            if (instance.classAttribute().isNominal()) {
                if (this.m_SVMType == 2) {
                    if (v > 0.0D) {
                        result[0] = 1.0D;
                    } else {
                        result[0] = 0.0D;
                    }
                } else {
                    result[(int)v] = 1.0D;
                }
            } else {
                result[0] = v;
            }
        } else {
            v = (Double)this.invokeMethod(Class.forName("libsvm.svm").newInstance(), "svm_predict_probability", new Class[]{Class.forName("libsvm.svm_model"), Array.newInstance(Class.forName("libsvm.svm_node"), Array.getLength(x)).getClass(), Array.newInstance(Double.TYPE, prob_estimates.length).getClass()}, new Object[]{this.m_Model, x, prob_estimates});

            for(int k = 0; k < prob_estimates.length; ++k) {
                result[labels[k]] = prob_estimates[k];
            }
        }

        return result;
    }

    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();
        result.enable(Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capability.DATE_ATTRIBUTES);
        result.enableDependency(Capability.UNARY_CLASS);
        result.enableDependency(Capability.NOMINAL_CLASS);
        result.enableDependency(Capability.NUMERIC_CLASS);
        result.enableDependency(Capability.DATE_CLASS);
        switch(this.m_SVMType) {
            case 0:
            case 1:
                result.enable(Capability.NOMINAL_CLASS);
                break;
            case 2:
                result.enable(Capability.UNARY_CLASS);
                break;
            case 3:
            case 4:
                result.enable(Capability.NUMERIC_CLASS);
                result.enable(Capability.DATE_CLASS);
                break;
            default:
                throw new IllegalArgumentException("SVMType " + this.m_SVMType + " is not supported!");
        }

        result.enable(Capability.MISSING_CLASS_VALUES);
        return result;
    }

    public void buildClassifier(Instances insts) throws Exception {
        this.m_Filter = null;
        if (!isPresent()) {
            throw new Exception("libsvm classes not in CLASSPATH!");
        } else {
            insts = new Instances(insts);
            insts.deleteWithMissingClass();
            if (!this.getDoNotReplaceMissingValues()) {
                this.m_ReplaceMissingValues = new ReplaceMissingValues();
                this.m_ReplaceMissingValues.setInputFormat(insts);
                insts = Filter.useFilter(insts, this.m_ReplaceMissingValues);
            }

            this.getCapabilities().testWithFail(insts);
            if (this.getNormalize()) {
                this.m_Filter = new Normalize();
                this.m_Filter.setInputFormat(insts);
                insts = Filter.useFilter(insts, this.m_Filter);
            }

            Vector vy = new Vector();
            Vector vx = new Vector();
            int max_index = 0;

            for(int d = 0; d < insts.numInstances(); ++d) {
                Instance inst = insts.instance(d);
                Object x = this.instanceToArray(inst);
                int m = Array.getLength(x);
                if (m > 0) {
                    max_index = Math.max(max_index, (Integer)this.getField(Array.get(x, m - 1), "index"));
                }

                vx.addElement(x);
                vy.addElement(new Double(inst.classValue()));
            }

            if (this.getGamma() == 0.0D) {
                this.m_GammaActual = 1.0D / (double)max_index;
            } else {
                this.m_GammaActual = this.m_Gamma;
            }

            String error_msg = (String)this.invokeMethod(Class.forName("libsvm.svm").newInstance(), "svm_check_parameter", new Class[]{Class.forName("libsvm.svm_problem"), Class.forName("libsvm.svm_parameter")}, new Object[]{this.getProblem(vx, vy), this.getParameters()});
            if (error_msg != null) {
                throw new Exception("Error: " + error_msg);
            } else {
                this.m_Model = this.invokeMethod(Class.forName("libsvm.svm").newInstance(), "svm_train", new Class[]{Class.forName("libsvm.svm_problem"), Class.forName("libsvm.svm_parameter")}, new Object[]{this.getProblem(vx, vy), this.getParameters()});
                if (!this.m_ModelFile.isDirectory()) {
                    this.invokeMethod(Class.forName("libsvm.svm").newInstance(), "svm_save_model", new Class[]{String.class, Class.forName("libsvm.svm_model")}, new Object[]{this.m_ModelFile.getAbsolutePath(), this.m_Model});
                }

            }
        }
    }

    public String toString() {
        return "LibSVM wrapper, original code by Yasser EL-Manzalawy (= WLSVM)";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 7562 $");
    }

    public static void main(String[] args) {
        runClassifier(new LibSVM(), args);
    }

    static {
        try {
            Class.forName("libsvm.svm");
            m_Present = true;
        } catch (Exception var1) {
            m_Present = false;
        }

    }
}
