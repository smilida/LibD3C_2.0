# LibD3C_2.0
Ensemble classifiers with a clustering and dynamic selection strategy and solve imbalance peoblem.  

LibD3C_2.0 is the upgrades for the software LibD3C_1.2.  

LibD3C is an ensemble classifiers with a clustering and dynamic selection strategy.  

**LibD3C_2.0 was based on Affinity Propagation cluster algorithm and Weka 3.8.5 stable version.**  

usage:
 
 Usage is the same no mater windows or Linux
 
1.download the LibD3C2.0.jar and classifier.xml(This file includes all base classifiers for LibD3C2.0,you can even add other base classifier by yourself)

2.CrossValidation

 2.1.balance data
   
     Java -jar LibD3C2.0.jar -c fold-num trainFile
    
 2.2.imbalance data    
   
     Java -jar LibD3C2.0.jar -m -c fold-num trainFile
    
fold-num is the crossValiditon folds.
trainFile is the file path for the training data,softWare can only identify .arff file

3.prediction

(1)train model

Balance data

    Java -jar LibD3C2.0.jar -t trainFile

Imbalance data

    Java -jar LibD3C2.0.jar -m -t trainFile

     
-t means train model and the model would be saved in the same filePath(train.model) 
,trainFile is the file path for the training data,resultFile is the file path save prediction result

(2)predict instance

Balance data

    Java -jar LibD3C2.0.jar -p train.model testFile resultFile

Imbalance data

    Java -jar LibD3C2.0.jar -m -p train.model testFile resultFile

   -p means prediction and train.model is the filePath you trained before
,trainFile is the file path for the training data, resultFile is the file path save prediction result

  
