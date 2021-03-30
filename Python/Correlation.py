import numpy as np
import pandas as pd
import csv

# https://pandas.pydata.org/pandas-docs/stable/reference/api/pandas.Series.corr.html#pandas.Series.corr

def histogram_intersection(a,b):
    v = np.minimum(a,b).sum()
    return v
readfile = ""
reader = csv.reader(readfile)
VarianceData = []
TweetsData = []
for line in reader:
    lineData = line.split(",")
    VarianceData.append((float)(lineData[0]))
    TweetsData.append((float)(lineData[1]))
Variance = pd.Series(VarianceData)
Tweets = pd.Series(TweetsData)
print("Test Correlation:",Variance.corr(Tweets,method=histogram_intersection))


