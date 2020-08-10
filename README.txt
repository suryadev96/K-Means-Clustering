/*INFO*/

1)This is the paper implementation of Effective KMeans clustering algorithm using KD trees for pruning the centers to remove unwanted node-center computations.
2)It also uses Kmeans++ logic of initial clustering configuration as a seed. It uses weighted probability distribution function to spread the intial k centers apart. 
3)I have added wrapper kind of function on top of these to learn the value of k automatically using Gaussian Distribution Normality test called Anderson Darling statistic.


/*EVALUATION*/
1)So I have created a multivariate Gaussian Distribution for 3 dimensional data with four different mu and sigma values.Basically that means,the data created was put into four different clusters.
2)Evaluation of the implemented algorithms against these synthetically created data has done using validation techniques like Rand Index and Adjusted Rand Index.
3)They basically calculate the agreement of two partitions. One is the Artificially created one with 4 clusters and the other one is our algorithm implementation on the same data which is created.
Agreement of two partitions means the number of pairs of data that are either in the same group or in different groups in both partitions divided by the total number of pairs of objects. 


/*DEPENDENCIES*/
JUnit
JAMA Java Matrix Package
NOTE: I have added all other required dependencies in the form of packages in the main folder. Hence you need not give any dependencies in the POM file.


/*RUN*/
I have written several JUnit test cases for KMeans and GMeans on several K values and on the USPS dataset..
Basically I had tested my code on the USPS dataset. You could do it on any other images or data very similarly to what i have done for USPS dataset.
Only one thing is just copy the dataset src/test/ folder and reference the dataset name directly in the JUnit Test case.
You could run them directly as a java application


/*REFERENCES*/

Tapas Kanungo, David M. Mount, Nathan S. Netanyahu, Christine D. Piatko, Ruth Silverman, and Angela Y. Wu. An Efficient k-Means Clustering Algorithm: Analysis and Implementation. IEEE TRANS. PAMI, 2002
D. Arthur and S. Vassilvitskii. "K-means++: the advantages of careful seeding". ACM-SIAM symposium on Discrete algorithms, 1027-1035, 2007
G. Hamerly and C. Elkan. Learning the k in k-means. NIPS, 2003
Dan Pelleg and Andrew Moore. X-means: Extending K-means with Efficient Estimation of the Number of Clusters. ICML, 2000











