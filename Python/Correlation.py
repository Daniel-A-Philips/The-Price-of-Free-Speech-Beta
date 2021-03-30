# importing pandas as pd
import pandas as pd
  
# Making data frame from the csv file
readFile = ""
df = pd.read_csv(readFile)
  
# Printing the first 10 rows of the data frame for visualization
df[:10]
output = df.corr(method ='kendall')
forGivenStock = output[0][2]
forBaseline = output[1][2]
difference = forGivenStock - forBaseline
print(forGivenStock)
