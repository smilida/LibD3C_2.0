package weka.classifiers.meta;

import cn.edu.xmu.dm.d3c.core.imDC1;
import cn.edu.xmu.dm.d3c.core.myClassifier;
import cn.edu.xmu.dm.d3c.sample.BaseClassifiersClustering;
import cn.edu.xmu.dm.d3c.sample.BaseClassifiersEnsemble;
import cn.edu.xmu.dm.d3c.sample.ParallelBaseClassifiersTraining;
import cn.edu.xmu.dm.d3c.utils.InitClassifiers;
import cn.edu.xmu.dm.d3c.utils.InstanceUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableMultipleClassifiersCombiner;
import weka.classifiers.functions.LibSVM;
import weka.core.Capabilities;
import weka.core.Environment;
import weka.core.EnvironmentHandler;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;

public class LibD3C extends RandomizableMultipleClassifiersCombiner implements TechnicalInformationHandler, EnvironmentHandler {
    protected static final long serialVersionUID = 1L;

    protected int numClusters = 10;

    protected double TargetCorrectRate = 1.0D;

    protected double Interval = 0.05D;

    List<String> nameOfClassifiers = new ArrayList<String>();

    List<String> pathOfClassifiers = new ArrayList<String>();

    List<String> parameterOfCV = new ArrayList<String>();

    List<Classifier> cfsArrayCopy = new ArrayList<Classifier>();

    public static ArrayList<String> Option;

    List<Integer> ensemClassifiers;

    public static Classifier[] cfsArray;

    public static boolean flag_im = false;

    protected Random m_Random = new Random(1000L);

    protected List<String> m_classifiersToLoad = new ArrayList<String>();

    protected List<Classifier> m_preBuiltClassifiers = new ArrayList<Classifier>();

    protected transient Environment m_env = Environment.getSystemWide();

    protected Tag[] TAGS_CIRCLECOMBINEALGORITHM = new Tag[] { new Tag(0, "HCNRR"),
            new Tag(1, "HCRR"), new Tag(2, "EFSS"), new Tag(3, "EBSS") };

    protected int m_SelectiveAlgorithm_Type = 2;

    protected final Tag[] TAGS_SELECTIVEALGORITHM = new Tag[] { new Tag(0, "CC"),
            new Tag(1, "DS"), new Tag(2, "HCNRR"), new Tag(3, "HCRR"),
            new Tag(4, "EFSS"), new Tag(5, "EBSS") };

    protected int m_CircleCombine_Type = 1;

    protected Tag[] TAGS_RULES = new Tag[] { new Tag(1, "Average of Probabilities"),
            new Tag(2, "Product of Probabilities"),
            new Tag(3, "Majority Voting"), new Tag(4, "Minimum Probability"),
            new Tag(5, "Maximum Probability"), new Tag(6, "Median Voting") };

    protected int m_CombinationRule = 1;

    protected double validatePercent = 0.2D;

    protected int m_numExecutionSlots = 1;

    protected String classifiersxml = "classifiers.xml";

    protected int timeOut = 20;

    public boolean getflag_im() {
        return flag_im;
    }

    public void setflag_im(boolean flag_im) {
        flag_im = flag_im;
    }

    public int getNumClusters() {
        return this.numClusters;
    }

    public void setNumClusters(int num) {
        this.numClusters = num;
    }

    public double getTargetCorrectRate() {
        return this.TargetCorrectRate;
    }

    public void setTargetCorrectRate(double correctrate) {
        this.TargetCorrectRate = correctrate;
    }

    public double getInterval() {
        return this.Interval;
    }

    public void setInterval(double interval) {
        this.Interval = interval;
    }

    public SelectedTag getSelectiveAlgorithm() {
        return new SelectedTag(this.m_SelectiveAlgorithm_Type,
                this.TAGS_SELECTIVEALGORITHM);
    }

    public void setSelectiveAlgorithm(SelectedTag value) {
        if (value.getTags() == this.TAGS_SELECTIVEALGORITHM)
            this.m_SelectiveAlgorithm_Type = value.getSelectedTag().getID();
    }

    public SelectedTag getCircleCombineAlgorithm() {
        return new SelectedTag(this.m_CircleCombine_Type,
                this.TAGS_CIRCLECOMBINEALGORITHM);
    }

    public void setCircleCombineAlgorithm(SelectedTag value) {
        if (value.getTags() == this.TAGS_CIRCLECOMBINEALGORITHM)
            this.m_CircleCombine_Type = value.getSelectedTag().getID();
    }

    public int getNumExecutionSlots() {
        return this.m_numExecutionSlots;
    }

    public void setNumExecutionSlots(int m_numExecutionSlots) {
        this.m_numExecutionSlots = m_numExecutionSlots;
    }

    public double getValidationRatio() {
        return this.validatePercent;
    }

    public void setValidationRatio(double validatePercent) {
        this.validatePercent = validatePercent;
    }

    public SelectedTag getEnsembleVotingRule() {
        return new SelectedTag(this.m_CombinationRule, this.TAGS_RULES);
    }

    public void setEnsembleVotingRule(SelectedTag value) {
        if (value.getTags() == this.TAGS_RULES)
            this.m_CombinationRule = value.getSelectedTag().getID();
    }

    public int getTimeOut() {
        return this.timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String globalInfo() {
        return "Class for combining classifiers. Different combinations of probability estimates for classification are available.\n\nFor more information see:\n\n" +
                getTechnicalInformation().toString();
    }

    public Enumeration listOptions() {
        Vector<Option> result = new Vector();
        result.addElement(new Option("\tRandom number seed.\n\t(default 1)",
                "S", 1, "-S <num>"));
        result.add(new Option("\tTarget correct rate", "target-correct-rate",
                1, "-target-correct-rate 1.0"));
        result.add(new Option("\tClassifiers file's path", "classifiers-xml",
                1, "-classifiers-xml C:/Program Files/Weka-3-7/classifiers.xml"));
        result.add(new Option(
                "\tInterval for the decreasing target correct rate", "I", 1,
                "-I 0.5"));
        result.add(new Option(
                "\tValidation set's proportion respect to Training Set",
                "validation-ratio", 1, "-validation-ratio 0.2"));
        result.addElement(new Option(
                "\tCombination rule to use\n\t(default: AVG)",
                "ensemble-vote-rule", 1, "-ensemble-vote-rule " + this.TAGS_RULES[0]));
        result.add(new Option(
                "\tCluster number for clustering the base classifiers", "K", 1,
                "-K 5"));
        result.addElement(new Option(
                "\tCircle combination algorithm of the ensemble pharse",
                "circle-combination-algorithm", 1,
                "-circle-combination-algorithm " +
                        this.TAGS_CIRCLECOMBINEALGORITHM[0]));
        result.addElement(new Option(
                "\tSelective algorithm type of the ensemble pharse",
                "selective-algorithm", 1, "-selective-algorithm " +
                this.TAGS_SELECTIVEALGORITHM[3]));
        result.add(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)",
                "num-slots", 1,
                "-num-slots <num>"));
        result.add(new Option(
                "\tMaximum minutes to train each base classifier", "time-out",
                1, "-time-out 2"));
        return result.elements();
    }

    public String[] getOptions() {
        String[] superOptions = super.getOptions();
        String[] options = new String[superOptions.length + 20];
        int current = 0;
        options[current++] = "-target-correct-rate";
        options[current++] = String.valueOf(getTargetCorrectRate());
        options[current++] = "-I";
        options[current++] = String.valueOf(getInterval());
        options[current++] = "-validation-ratio";
        options[current++] = String.valueOf(getValidationRatio());
        options[current++] = "-ensemble-vote-rule";
        options[current++] = String.valueOf(getEnsembleVotingRule());
        options[current++] = "-K";
        options[current++] = String.valueOf(getNumClusters());
        options[current++] = "-circle-combination-algorithm";
        options[current++] = String.valueOf(getCircleCombineAlgorithm());
        options[current++] = "-selective-algorithm";
        options[current++] = String.valueOf(getSelectiveAlgorithm());
        options[current++] = "-num-slots";
        options[current++] = String.valueOf(getNumExecutionSlots());
        options[current++] = "-time-out";
        options[current++] = String.valueOf(getTimeOut());
        System.arraycopy(superOptions, 0, options, current, superOptions.length);
        current += superOptions.length;
        while (current < options.length)
            options[current++] = "";
        return options;
    }

    public void setOptions(String[] options) throws Exception {
        setTargetCorrectRate(Double.parseDouble(Utils.getOption(
                "target-correct-rate", options)));
        setInterval(Double.parseDouble(Utils.getOption("I", options)));
        setValidationRatio(Double.parseDouble(Utils.getOption(
                "validation-ratio", options)));
        setEnsembleVotingRule(new SelectedTag(Utils.getOption(
                "ensemble-vote-rule", options), this.TAGS_RULES));
        setNumClusters(Integer.parseInt(Utils.getOption("K", options)));
        setCircleCombineAlgorithm(new SelectedTag(Utils.getOption(
                "circle-combination-algorithm", options),
                this.TAGS_CIRCLECOMBINEALGORITHM));
        setSelectiveAlgorithm(new SelectedTag(Utils.getOption(
                "selective-algorithm", options), this.TAGS_SELECTIVEALGORITHM));
        setNumExecutionSlots(Integer.parseInt(Utils.getOption("num-slots",
                options)));
        setTimeOut(Integer.parseInt(Utils.getOption("time-out", options)));
        String seed = Utils.getOption('S', options);
        if (seed.length() != 0) {
            setSeed(Integer.parseInt(seed));
        } else {
            setSeed(1);
        }
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result = new TechnicalInformation(
                TechnicalInformation.Type.ARTICLE);
        result.setValue(TechnicalInformation.Field.AUTHOR,
                "Chen Lin, Wenqiang Chen, Cheng Qiu, Yunfeng Wu, Sridhar Krishnan, Quan Zou");
        result.setValue(TechnicalInformation.Field.TITLE,
                "LibD3C: Ensemble classifiers with a clustering and dynamic selection strategy");
        result.setValue(TechnicalInformation.Field.YEAR, "2013");
        result.setValue(TechnicalInformation.Field.JOURNAL, "Neurocomputing");
        result.setValue(TechnicalInformation.Field.VOLUME, "123");
        result.setValue(TechnicalInformation.Field.PAGES, " 424");
        return result;
    }

    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        if (this.m_preBuiltClassifiers.size() > 0) {
            if (this.m_Classifiers.length == 0)
                result = (Capabilities)((Classifier)this.m_preBuiltClassifiers
                        .get(0)).getCapabilities().clone();
            for (int i = 1; i < this.m_preBuiltClassifiers.size(); i++)
                result.and(((Classifier)this.m_preBuiltClassifiers.get(i))
                        .getCapabilities());
            byte b;
            int j;
            Capabilities.Capability[] arrayOfCapability;
            for (j = (arrayOfCapability = Capabilities.Capability.values()).length, b = 0; b < j; ) {
                Capabilities.Capability cap = arrayOfCapability[b];
                result.enableDependency(cap);
                b++;
            }
        }
        if (this.m_CombinationRule == 2 || this.m_CombinationRule == 3) {
            result.disableAllClasses();
            result.disableAllClassDependencies();
            result.enable(Capabilities.Capability.NOMINAL_CLASS);
            result.enableDependency(Capabilities.Capability.NOMINAL_CLASS);
        } else if (this.m_CombinationRule == 6) {
            result.disableAllClasses();
            result.disableAllClassDependencies();
            result.enable(Capabilities.Capability.NUMERIC_CLASS);
            result.enableDependency(Capabilities.Capability.NUMERIC_CLASS);
        }
        return result;
    }

    public void buildClassifier(Instances data) throws Exception {
        Instances newData = new Instances(data);
        newData.deleteWithMissingClass();
        this.m_Random = new Random(1000L);
        cfsArray = this.cfsArrayCopy.<Classifier>toArray(new Classifier[this.cfsArrayCopy.size()]);
        ParallelBaseClassifiersTraining bct = new ParallelBaseClassifiersTraining();
        data.setClassIndex(data.numAttributes() - 1);
        if (!flag_im) {
            List<Classifier> bcfs = bct.trainingBaseClassifiers(data, cfsArray,
                    this.validatePercent, this.m_numExecutionSlots, this.timeOut,
                    this.pathOfClassifiers, this.parameterOfCV);
            BaseClassifiersClustering bcc = new BaseClassifiersClustering();
            String pathPrefix = "";
            String fchooseClassifiers = String.valueOf(pathPrefix) + "chooseClassifiers.txt";
            List<Integer> chooseClassifiers = bcc.clusterBaseClassifiers(
                    fchooseClassifiers, this.numClusters);
            BaseClassifiersEnsemble bce = new BaseClassifiersEnsemble();
            this.ensemClassifiers = bce.EnsembleClassifiers(data,
                    this.m_SelectiveAlgorithm_Type, this.m_CircleCombine_Type);
            this.m_Classifiers = null;
            this.m_Classifiers = new Classifier[this.ensemClassifiers.size()];
            int mIndex = 0;
            for (Iterator<Integer> iterator = this.ensemClassifiers.iterator(); iterator.hasNext(); ) {
                int i = ((Integer)iterator.next()).intValue();
                this.m_Classifiers[mIndex++] = bcfs.get(i);
            }
        } else {
            imDC1 imdc = new imDC1();
            imdc.getBestClassifier(newData);
            myClassifier myclassifier = new myClassifier(newData,
                    imDC1.bestClassifiers, imdc.lessLabelNum, imDC1.lessLabel,
                    cfsArray.length);
            myclassifier.initmyclassifier();
            Classifier[] bc = myclassifier.build(newData);
            this.m_Classifiers = null;
            this.m_Classifiers = new Classifier[bc.length];
            int mIndex = 0;
            this.m_Classifiers = bc;
        }
        data = null;
    }

    public double classifyInstance(Instance instance) throws Exception {
        double result;
        double[] dist;
        switch (this.m_CombinationRule) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                dist = distributionForInstance(instance);
                if (instance.classAttribute().isNominal()) {
                    int index = Utils.maxIndex(dist);
                    if (dist[index] == 0.0D) {
                        result = Utils.missingValue();
                    } else {
                        result = index;
                    }
                } else if (instance.classAttribute().isNumeric()) {
                    result = dist[0];
                } else {
                    result = Utils.missingValue();
                }
                return result;
            case 6:
                result = classifyInstanceMedian(instance);
                return result;
        }
        throw new IllegalStateException("Unknown combination rule '" + this.m_CombinationRule + "'!");
    }

    protected double classifyInstanceMedian(Instance instance) throws Exception {
        double result, results[] = new double[this.m_Classifiers.length +
                this.m_preBuiltClassifiers.size()];
        int i;
        for (i = 0; i < this.m_Classifiers.length; i++)
            results[i] = this.m_Classifiers[i].classifyInstance(instance);
        for (i = 0; i < this.m_preBuiltClassifiers.size(); i++)
            results[i + this.m_Classifiers.length] = ((Classifier)this.m_preBuiltClassifiers
                    .get(i)).classifyInstance(instance);
        if (results.length == 0) {
            result = 0.0D;
        } else if (results.length == 1) {
            result = results[0];
        } else {
            result = Utils.kthSmallestValue(results, results.length / 2);
        }
        return result;
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        double[] result = new double[instance.numClasses()];
        switch (this.m_CombinationRule) {
            case 1:
                result = distributionForInstanceAverage(instance);
                break;
            case 2:
                result = distributionForInstanceProduct(instance);
                break;
            case 3:
                result = distributionForInstanceMajorityVoting(instance);
                break;
            case 4:
                result = distributionForInstanceMin(instance);
                break;
            case 5:
                result = distributionForInstanceMax(instance);
                break;
            case 6:
                result[0] = classifyInstance(instance);
                break;
            default:
                throw new IllegalStateException("Unknown combination rule '" +
                        this.m_CombinationRule + "'!");
        }
        if (!instance.classAttribute().isNumeric() &&
                Utils.sum(result) > 0.0D)
            Utils.normalize(result);
        return result;
    }

    protected double[] distributionForInstanceAverage(Instance instance) throws Exception {
        double[] probs = (this.m_Classifiers.length > 0) ? getClassifier(0)
                .distributionForInstance(instance) : (
                (Classifier)this.m_preBuiltClassifiers.get(0))
                .distributionForInstance(instance);
        for (int i = 1; i < this.m_Classifiers.length; i++) {
            double[] dist = getClassifier(i).distributionForInstance(instance);
            for (int m = 0; m < dist.length; m++)
                probs[m] = probs[m] + dist[m];
        }
        int index = (this.m_Classifiers.length > 0) ? 0 : 1;
        for (int k = index; k < this.m_preBuiltClassifiers.size(); k++) {
            double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(k))
                    .distributionForInstance(instance);
            for (int m = 0; m < dist.length; m++)
                probs[m] = probs[m] + dist[m];
        }
        for (int j = 0; j < probs.length; j++)
            probs[j] = probs[j] / (this.m_Classifiers.length + this.m_preBuiltClassifiers
                    .size());
        return probs;
    }

    protected double[] distributionForInstanceProduct(Instance instance) throws Exception {
        double[] probs = (this.m_Classifiers.length > 0) ? getClassifier(0)
                .distributionForInstance(instance) : (
                (Classifier)this.m_preBuiltClassifiers.get(0))
                .distributionForInstance(instance);
        for (int i = 1; i < this.m_Classifiers.length; i++) {
            double[] dist = getClassifier(i).distributionForInstance(instance);
            for (int k = 0; k < dist.length; k++)
                probs[k] = probs[k] * dist[k];
        }
        int index = (this.m_Classifiers.length > 0) ? 0 : 1;
        for (int j = index; j < this.m_preBuiltClassifiers.size(); j++) {
            double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(j))
                    .distributionForInstance(instance);
            for (int k = 0; k < dist.length; k++)
                probs[k] = probs[k] * dist[k];
        }
        return probs;
    }

    protected double[] distributionForInstanceMajorityVoting(Instance instance) throws Exception {
        double[] probs = new double[instance.classAttribute().numValues()];
        double[] votes = new double[probs.length];
        int i;
        for (i = 0; i < this.m_Classifiers.length; i++) {
            probs = getClassifier(i).distributionForInstance(instance);
            int maxIndex = 0;
            int n;
            for (n = 0; n < probs.length; n++) {
                if (probs[n] > probs[maxIndex])
                    maxIndex = n;
            }
            for (n = 0; n < probs.length; n++) {
                if (probs[n] == probs[maxIndex])
                    votes[n] = votes[n] + 1.0D;
            }
        }
        for (i = 0; i < this.m_preBuiltClassifiers.size(); i++) {
            probs = ((Classifier)this.m_preBuiltClassifiers.get(i))
                    .distributionForInstance(instance);
            int maxIndex = 0;
            int n;
            for (n = 0; n < probs.length; n++) {
                if (probs[n] > probs[maxIndex])
                    maxIndex = n;
            }
            for (n = 0; n < probs.length; n++) {
                if (probs[n] == probs[maxIndex])
                    votes[n] = votes[n] + 1.0D;
            }
        }
        int tmpMajorityIndex = 0;
        for (int k = 1; k < votes.length; k++) {
            if (votes[k] > votes[tmpMajorityIndex])
                tmpMajorityIndex = k;
        }
        Vector<Integer> majorityIndexes = new Vector();
        for (int j = 0; j < votes.length; j++) {
            if (votes[j] == votes[tmpMajorityIndex])
                majorityIndexes.add(Integer.valueOf(j));
        }
        int majorityIndex = ((Integer)majorityIndexes.get(this.m_Random
                .nextInt(majorityIndexes.size()))).intValue();
        for (int m = 0; m < probs.length; m++)
            probs[m] = 0.0D;
        probs[majorityIndex] = 1.0D;
        return probs;
    }

    protected double[] distributionForInstanceMax(Instance instance) throws Exception {
        double[] max = (this.m_Classifiers.length > 0) ? getClassifier(0)
                .distributionForInstance(instance) : (
                (Classifier)this.m_preBuiltClassifiers.get(0))
                .distributionForInstance(instance);
        for (int i = 1; i < this.m_Classifiers.length; i++) {
            double[] dist = getClassifier(i).distributionForInstance(instance);
            for (int k = 0; k < dist.length; k++) {
                if (max[k] < dist[k])
                    max[k] = dist[k];
            }
        }
        int index = (this.m_Classifiers.length > 0) ? 0 : 1;
        for (int j = index; j < this.m_preBuiltClassifiers.size(); j++) {
            double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(j))
                    .distributionForInstance(instance);
            for (int k = 0; k < dist.length; k++) {
                if (max[k] < dist[k])
                    max[k] = dist[k];
            }
        }
        return max;
    }

    protected double[] distributionForInstanceMin(Instance instance) throws Exception {
        double[] min = (this.m_Classifiers.length > 0) ? getClassifier(0)
                .distributionForInstance(instance) : (
                (Classifier)this.m_preBuiltClassifiers.get(0))
                .distributionForInstance(instance);
        for (int i = 1; i < this.m_Classifiers.length; i++) {
            double[] dist = getClassifier(i).distributionForInstance(instance);
            for (int k = 0; k < dist.length; k++) {
                if (dist[k] < min[k])
                    min[k] = dist[k];
            }
        }
        int index = (this.m_Classifiers.length > 0) ? 0 : 1;
        for (int j = index; j < this.m_preBuiltClassifiers.size(); j++) {
            double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(j))
                    .distributionForInstance(instance);
            for (int k = 0; k < dist.length; k++) {
                if (dist[k] < min[k])
                    min[k] = dist[k];
            }
        }
        return min;
    }

    public String toString() {
        if (this.m_Classifiers == null)
            return "Vote: No model built yet.";
        String result = "Vote combines";
        result = String.valueOf(result) +
                " the probability distributions of these base learners:\n";
        for (int i = 0; i < this.m_Classifiers.length; i++)
            result = String.valueOf(result) + '\t' + getClassifierSpec(i) + '\n';
        for (Classifier c : this.m_preBuiltClassifiers)
            result = String.valueOf(result) + "\t" + c.getClass().getName() +
                    Utils.joinOptions(((OptionHandler)c).getOptions()) +
                    "\n";
        result = String.valueOf(result) + "using the '";
        switch (this.m_CombinationRule) {
            case 1:
                result = String.valueOf(result) + "Average of Probabilities";
                result = String.valueOf(result) + "' combination rule \n";
                return result;
            case 2:
                result = String.valueOf(result) + "Product of Probabilities";
                result = String.valueOf(result) + "' combination rule \n";
                return result;
            case 3:
                result = String.valueOf(result) + "Majority Voting";
                result = String.valueOf(result) + "' combination rule \n";
                return result;
            case 4:
                result = String.valueOf(result) + "Minimum Probability";
                result = String.valueOf(result) + "' combination rule \n";
                return result;
            case 5:
                result = String.valueOf(result) + "Maximum Probability";
                result = String.valueOf(result) + "' combination rule \n";
                return result;
            case 6:
                result = String.valueOf(result) + "Median Probability";
                result = String.valueOf(result) + "' combination rule \n";
                return result;
        }
        throw new IllegalStateException("Unknown combination rule '" + this.m_CombinationRule + "'!");
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 7220 $");
    }

    public void setEnvironment(Environment env) {
        this.m_env = env;
    }

    public void ListChange() {
        cfsArray = InitClassifiers.init(this.classifiersxml,
                this.nameOfClassifiers, this.pathOfClassifiers,
                this.parameterOfCV);
        Option = InitClassifiers.classifiersOption;
        this.m_Classifiers[0] = (Classifier)new LibSVM();
        int i;
        for (i = 0; i < cfsArray.length; i++)
            this.cfsArrayCopy.add(cfsArray[i]);
        for (i = 0; i < this.m_Classifiers.length; i++)
            this.cfsArrayCopy.add(this.m_Classifiers[i]);
    }

    public void printInfo(Evaluation eval) throws Exception {
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString("LibD3C details result"));
        System.out.println(eval.toMatrixString());
    }

    public static void main(String[] argv) throws Exception {
        String TrainFilePath = null, cvNum = null, TestFilePath = null, modelPath = null, resultFilePath = null;
        boolean cross = false;
        boolean train = false;
        boolean predict = false;
        TrainFilePath = argv[0];
        try {
            if (argv[0].equals("-m")) {
                flag_im = true;
                if (argv[1].equals("-c")) {
                    cvNum = argv[2];
                    cross = true;
                    cvNum = argv[2];
                    TrainFilePath = argv[3];
                } else if (argv[1].equals("-t")) {
                    TrainFilePath = argv[2];
                    train = true;
                } else if (argv[1].equals("-p")) {
                    modelPath = argv[2];
                    TestFilePath = argv[3];
                    resultFilePath = argv[4];
                    predict = true;
                }
            } else if (argv[0].equals("-c")) {
                cvNum = argv[1];
                TrainFilePath = argv[2];
                cross = true;
            } else if (argv[0].equals("-t")) {
                TrainFilePath = argv[1];
                train = true;
            } else if (argv[0].equals("-p")) {
                modelPath = argv[1];
                TestFilePath = argv[2];
                resultFilePath = argv[3];
                predict = true;
            }
            InstanceUtil iu = new InstanceUtil();
            BaseClassifiersEnsemble tt = new BaseClassifiersEnsemble();
            LibD3C d3c = new LibD3C();
            if (flag_im) {
                if (train) {
                    d3c.ListChange();
                    Instances input = InstanceUtil.getInstances(TrainFilePath);
                    input.setClassIndex(input.numAttributes() - 1);
                    iu.SaveModel(d3c, input);
                } else if (cross) {
                    d3c.ListChange();
                    Instances input = InstanceUtil.getInstances(TrainFilePath);
                    input.setClassIndex(input.numAttributes() - 1);
                    Evaluation eval = new Evaluation(input);
                    eval.crossValidateModel((Classifier)d3c, input,
                            Integer.parseInt(cvNum), new Random(d3c.getSeed()), new Object[0]);
                    d3c.printInfo(eval);
                } else if (predict) {
                    iu.LoadModel(modelPath, TestFilePath, resultFilePath);
                }
            } else if (train) {
                d3c.ListChange();
                Instances input = InstanceUtil.getInstances(TrainFilePath);
                input.setClassIndex(input.numAttributes() - 1);
                iu.SaveModel(d3c, input);
            } else if (cross) {
                d3c.ListChange();
                Instances input = InstanceUtil.getInstances(TrainFilePath);
                input.setClassIndex(input.numAttributes() - 1);
                Evaluation eval = new Evaluation(input);
                eval.crossValidateModel((Classifier)d3c, input,
                        Integer.parseInt(cvNum), new Random(d3c.getSeed()), new Object[0]);
                System.out.println("-------");
                for (Integer index : BaseClassifiersEnsemble.classifer)
                    System.out.println(String.valueOf(d3c.pathOfClassifiers.get(index.intValue())) +
                            " " + (String)Option.get(index.intValue()));
                d3c.printInfo(eval);
            } else if (predict) {
                iu.LoadModel(modelPath, TestFilePath, resultFilePath);
            }
        } catch (Exception e) {
            System.out.println("");
                    e.printStackTrace();
        }
    }
}
