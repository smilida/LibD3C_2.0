package cn.edu.xmu.dm.d3c.clustering;

import weka.core.Instance;
import weka.core.Instances;

import java.util.*;


public class AffinityPropagation {

    private double[][] similarities;
    private double[][] responsibilities;
    private double[][] availabilities;
    private double[][] combined;

    private int numberOfClusters;

    private int iter = 230;
    private double dampingFactor = 0.5;   // damping
    private double slider=0.5;                // moveable median
    //private double gama;

    private ArrayList<Integer> exemplar = new ArrayList<Integer>();
    private ArrayList<Integer> newMarker = new ArrayList<Integer>();
    private ArrayList<Integer> oldMarker = new ArrayList<Integer>();
    private int[] m_ClusterSizes;

    public void buildClusterer(Instances data) {}

    public int numberOfClusters() throws Exception {
        return this.numberOfClusters;
    }

    /**
     * does the actual clustering
     */
    public void buildClusterer(Instances data, List<Integer> chooseClassifiers, List<Double> correctRateArray) throws Exception {
        int maxIteration = 0, coverageIteration = 0;
        affinityPropagation_instantiation(data);
        calculateSimilarities(data);

        while(true) {
            updateResponsibilities(data);
            updateAvailabilities(data);
            for (int k = 0; k < data.numInstances(); k++) {
                //System.out.println(similarities[k][k]);
                if (responsibilities[k][k] + availabilities[k][k] > 0.0) {
                    //exemplar.put(k,allTestSampleMap.get(testSampleNames[k]));
                    exemplar.add(k);
                    newMarker.add(k);
                }
            }

            Set<Integer> setA = new HashSet<Integer>(newMarker);
            Set<Integer> setB = new HashSet<Integer>(oldMarker);
            if (!setA.isEmpty() && !setB.isEmpty() && setA.equals(setB)) {
                coverageIteration++;

            } else {
                coverageIteration = 0;
            }
            oldMarker.clear();

            if (maxIteration > 1000 || coverageIteration > 50) {
                break;
            } else {
                maxIteration++;
                for (int j = 0; j < newMarker.size(); j++) {
                    oldMarker.add(newMarker.get(j));
                }
                newMarker.clear();
                exemplar.clear();
            }
        }
        int[] clusterAssignments = new int[data.numInstances()];
        for (int m = 0; m < data.numInstances(); m++) {
            double maxDist = 0.00d;
            if (newMarker.contains(m)) {
                maxDist = 100000;
                clusterAssignments[m] = m;
            } else {
                int center = 0;
                for (int n = 0; n < newMarker.size(); n++) {
                    int d = newMarker.get(n);
                    if (similarities[d][m] > maxDist) {
                        maxDist = similarities[d][m];
                        center = d;
                    }
                }
                clusterAssignments[m] = center;
            }
        }
        this.numberOfClusters = newMarker.size();
        System.out.println(this.numberOfClusters);
        selectClassifier(clusterAssignments, chooseClassifiers, correctRateArray);

    }

    /**
     * just a couple of instantiations to do affinity propagation
     * @param data waiting to be clustered
     */
    public void affinityPropagation_instantiation(Instances data) {
        this.availabilities = new double[data.numInstances()][data.numInstances()];
        this.responsibilities = new double[data.numInstances()][data.numInstances()];
        this.combined = new double[data.numInstances()][data.numInstances()];
        this.similarities = new double[data.numInstances()][data.numInstances()];

        for (int i = 0; i < data.numInstances(); i++) {
            for (int k = 0; k < data.numInstances(); k++) {
                this.responsibilities[i][k] = 0;
                this.availabilities[i][k] = 0;
            }
        }
    }

    /**
     * calculates the common self similarity, or preference, given a similarity matrix as the median of other similarities
     * @param similarity a 2D array of doubles
     * @return returns the self similarity, or preference, of the input
     */
    private double calculatePreference(double[][] similarity) {
        int size = (int) (Math.pow(similarity.length, 2) - similarity.length);
        double[] allSimilarities = new double[size];
        int index, added = 0;
        double preference;

        // creating an array consisting of all off main diagonal elements in the similarity
        // matrix (all similarities, not preferences)
        for(int i = 0; i < similarity.length; i += 1) {
            for(int j = 0; j < similarity[i].length; j += 1) {
                if(i != j) {
                    allSimilarities[added] = similarity[i][j];
                    added += 1;
                }
            }
        }

        // sorting similarities from smallest to largest
        selectionSort(allSimilarities);

        // finding the "shifted median"
        index = (int) (allSimilarities.length * slider);
        preference = allSimilarities[index];

        return preference;
    }

    /**
     * calculates
     * @param data
     */
    public void calculateSimilarities(Instances data){
        //compute similarity between data point i and j (i is not equal to j)
        for(int i = 0; i < data.numInstances()-1; i++){
            for(int j = i+1; j < data.numInstances(); j++){
                this.similarities[i][j] = myDistance(data.instance(i), data.instance(j));
                this.similarities[j][i] = this.similarities[i][j];
            }
        }
        //compute preferences for all data points: median
        double preference = calculatePreference(this.similarities);

        for(int i = 0; i < data.numInstances(); i++){
            this.similarities[i][i] = preference;
        }


    }

    /**
     * calculates and sets the responsibility message sent from reference point with indexA to reference point with indexB
     * @param indexA index of the first reference Instance
     * @param indexB index of the second reference Instance
     * @param data
     */
    private void setResponsibility(int indexA , int indexB, Instances data){
        double responsibility, prev = responsibilities[indexA][indexB];
        Double curr, max = -1.0 * Double.MAX_VALUE;
        int i;

        for(i = 0; i < data.numInstances(); i += 1) {
            if(i != indexB) {
                curr = this.availabilities[indexA][i] + this.similarities[indexA][i];
                if(curr > max) {
                    max = curr;
                }
            }
        }

        responsibility = this.similarities[indexA][indexB] - max;
        responsibility = dampingFactor * prev + (1 - dampingFactor) * responsibility;
        responsibilities[indexA][indexB] = responsibility;
    }

    /**
     * calculates the sets the availability message sent from reference point with indexB to reference point with indexA
     * @param indexA first refernece point
     * @param indexB second reference point
     */
    private void setAvailability(int indexA, int indexB){
        double availability, curr, prev = availabilities[indexA][indexB], sum = 0.0;
        int i;

        // calculating the expression: sum(max{0,r(i',k)}) where i' is not equal to i and k
        for(i = 0; i < responsibilities.length; i += 1) {
            if(i != indexA && i != indexB) {
                curr = responsibilities[i][indexB];
                if(curr < 0) {
                    curr = 0.0;
                }
                sum += curr;
            }
        }

        // this will be the final availability if reference points a and b are the same
        availability = sum;

        if(indexA != indexB){
            availability += responsibilities[indexB][indexB];
            if(availability > 0){
                availability = 0.0;
            }
        }

        // to avoid numerical oscillations
        availability = dampingFactor * prev + (1 - dampingFactor) * availability;
        availabilities[indexA][indexB] = availability;
    }

    /**
     * updates all pairwise responsibility messages between all reference points
     */
    public void updateResponsibilities(Instances data) {
        for(int i = 0; i < data.numInstances(); i += 1) {
            for(int j = 0; j < data.numInstances(); j += 1) {
                setResponsibility(i, j, data);
            }
        }
    }

    /**
     * updates all pairwise availability messages between all reference points
     */
    public void updateAvailabilities(Instances data) {
        for(int i = 0; i < data.numInstances(); i += 1){
            for(int j = 0; j < data.numInstances(); j += 1){
                setAvailability(i, j);
            }
        }
    }


    public void selectClassifier(int[] clusterAssignments, List<Integer> chooseClassifiers, List<Double> correctRateArray) {
        int chooseID = 0;
        List<Integer> clusterCentroids = new ArrayList<Integer>();
        for (int i = 0; i < clusterAssignments.length; i++){
            if(!clusterCentroids.contains(clusterAssignments[i])){
                clusterCentroids.add(clusterAssignments[i]);
            }
        }
        countFreq(clusterAssignments, clusterAssignments.length);
        int[] whetherAdd = new int[clusterAssignments.length];
        int k = 0;
        while(chooseClassifiers.size()<10 && k<10){
            for (int i = 0; i < clusterCentroids.size(); i++) {
                double correctRate = 0.0D;
                double bestCorrectRate = 0.0D;
                for (int j = 0; j < clusterAssignments.length; j++) {
                    if (clusterAssignments[j] == clusterCentroids.get(i)
                            && correctRate < ((Double)correctRateArray.get(j))
                            && whetherAdd[j] == 0) {
                        correctRate = ((Double)correctRateArray.get(j));
                        chooseID = j;
                        bestCorrectRate = correctRate;
                        whetherAdd[j] = 1;
                        System.out.println("bestCorrectRate:" + bestCorrectRate);
                    }
                }
                if (!chooseClassifiers.contains(chooseID)){
                    chooseClassifiers.add(chooseID);
                }
            }
            k++;
            System.out.println("chooseClassifiers size: "+ chooseClassifiers.size());
        }
        System.out.println("chooseClassifiers size: "+ chooseClassifiers.size());

    }

    protected double myDistance(Instance first, Instance second){
        int Intersect = 0;
        for (int i = 0; i < first.numAttributes(); i++) {
            if (first.value(i) == second.value(i))
                Intersect++;
        }
        return (double) Intersect/first.numAttributes();
    }

    /**
     * sorts an array of double in ascending order in place
     * @param array a 1D array of doubles
     */
    private void selectionSort(double[] array)
    {
        double temp;

        for(int i = 0; i < array.length; i += 1)
        {
            for(int j = i; j < array.length; j += 1)
            {
                if(array[j] < array[i])
                {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
    }

    public static void countFreq(int arr[], int n)
    {
        Map<Integer, Integer> mp = new HashMap<>();

        // Traverse through array elements and
        // count frequencies
        for (int i = 0; i < n; i++)
        {
            mp.put(arr[i], mp.get(arr[i]) == null ? 1 : mp.get(arr[i]) + 1);
        }

        // To print elements according to first
        // occurrence, traverse array one more time
        // print frequencies of elements and mark
        // frequencies as -1 so that same element
        // is not printed multiple times.
        for (int i = 0; i < n; i++)
        {
            if (mp.get(arr[i]) != -1)
            {
                System.out.println(arr[i] + " " + mp.get(arr[i]));
                mp.put(arr[i], -1);
            }
        }
    }


}
