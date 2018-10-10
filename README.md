This is Group 40's implementation of an Evolutionary Algorithm

Requirements
------------

- Java > 8
- make

Run The Code
------------
- ```make submit``` to compile the code and create ```submission.jar```
- ```make <1/2/3>``` to evaluate BentCigarFunction / KatsuuraEvaluation / SchaffersEvaluation

Hyperparameter Tuning
---------------------
- Make sure to ```pip install scikit-optimize```, as it is a dependency!
- ```python3 tune.py <1/2/3>``` to tune the hyperparameters of the system (WARNING: Takes a long time)