package com.myml.pca.util;

import java.util.Arrays;
import java.util.HashMap;

import Jama.Matrix;

public class MatrixOperate {
	// 规范化处理

	// 最小-最大规范化，计算公式：[Ai-min(A)/(max(A)-min(A))]*(new_max(A)-new_min(A))+new_min(A)
	// 比如[(5-2)/(10-2)]*(1-0)+0
	public double[][] minmaxNormalization(double[][] array) {
		double max, min;
		int featurenum = array[0].length;
		int datanum = array.length;
		// 得到每一位最大和最小值
		for (int j = 0; j < featurenum; j++) {
			max = array[0][j];
			min = array[0][j];
			for (int i = 0; i < datanum; i++) {
				if (array[i][j] > max)
					max = array[i][j];
				if (array[i][j] < min)
					min = array[i][j];

			}
			// 每一维数据由公式形成新的数据组
			for (int i = 0; i < datanum; i++) {
				array[i][j] = (array[i][j] - min) / (max - min);
			}
		}
		return array;
	}

	// z分数规范化（零均值规范化），计算公式：Ai-mean(A)/sigma(A) siama(A)是属性A的标准差
	// 也可以用这个公式:Ai-mean(A)/s(A) s(A)是属性A的均值绝对偏差
	// s(A)=(|A1-mean(A)|+...+|An-mean(A)|)/n
	public double[][] zeromeanNormalization(double[][] array) {
		double[] center = new double[array[0].length]; // 所有维的均值数组
		double[] deviation = new double[array[0].length];
		double size = array.length;
		double sum = 0, deviasum = 0;

		// 得到零均值规范化后数组
		for (int j = 0; j < center.length; j++) {
			// 计算每一维均值
			for (int i = 0; i < size; i++) {
				sum += array[i][j];
			}
			center[j] = sum / size;

			// 计算每一维均值绝对偏差
			for (int i = 0; i < size; i++) {
				deviasum += Math.abs(array[i][j] - center[j]);
			}
			deviation[j] = deviasum / size;

			// 每一维数据减去该均值后除以均值绝对偏差，形成新的数据组
			for (int i = 0; i < size; i++) {
				array[i][j] = (array[i][j] - center[j]) / deviation[j];
			}
		}

		/*
		 * for (int i = 0; i < array.length; i++) { for (int j = 0; j <
		 * array[0].length; j++) { System.out.print(array[i][j]+"\t"); }
		 * System.out.println(); }
		 */

		return array;
	}

	// 特征中心化
	public double[][] FeaCenter(double[][] array) {
		// 每一维的数据都减去该维的均值。这里的“维”指的就是一个特征（或属性），变换之后每一维的均值都变成了0。
		double[] center = new double[array[0].length]; // 所有维的均值数组
		double size = array.length;
		double sum;

		// 得到所有维的均值数组
		for (int j = 0; j < center.length; j++) {
			sum = 0;
			for (int i = 0; i < size; i++) {
				sum += array[i][j];
			}
			center[j] = sum / size;
			// 每一维数据减去该均值，形成新的数据组
			for (int i = 0; i < size; i++) {
				array[i][j] = array[i][j] - center[j];
			}
		}
		return array;
	}

	// 求第i列(数组是i-1)和第j列(数组是j-1)的协方差
	public double GetCovariation(double[][] array, int i, int j) {
		double sum = 0;
		for (int k = 0; k < array.length; k++) {
			sum += array[k][i] * array[k][j];
		}
		return sum / (array.length - 1);
	}

	// 得到主特征值它们对应的特征向量矩阵
	public double[][] GetMainEigenvectorArray(Matrix cov, double standard) {
		/*
		 * cov.eig().getD().print(featureNum, 16);
		 * cov.eig().getV().print(featureNum, 16);
		 */
		// 协方差矩阵的特征值矩阵
		double[][] eigenvalueArray2D = cov.eig().getD().getArray();
		// 协方差矩阵的特征向量矩阵 (转置为了后面存储时用一行代表是列向量)
		double[][] eigenvectorArray = cov.eig().getV().transpose().getArray();

		// 原特征值矩阵从二维降到一维（取对角线的值）
		double[] eigenvalueArray1D = new double[eigenvalueArray2D.length];
		for (int i = 0; i < eigenvalueArray1D.length; i++) {
			eigenvalueArray1D[i] = eigenvalueArray2D[i][i];
		}

		// 将特征值和特征向量用map<Double,double[]>存入，以形成映射
		// 这里Double大写
		HashMap<Double, double[]> eigenMap = new HashMap<Double, double[]>();
		for (int i = 0; i < eigenvalueArray1D.length; i++) {
			eigenMap.put(eigenvalueArray1D[i], eigenvectorArray[i]);
		}

		// 将hashmap按照键的从小到大排序，取主成分
		Object[] keys = eigenMap.keySet().toArray();
		Arrays.sort(keys);

		// 主成分根据前多少个特征值之和占全部的百分之standar来取多少个
		// 因为是从小到大排的，所以从后往前取
		double sum = 0;
		for (int i = eigenMap.size() - 1; i >= 0; i--) {
			sum += (Double) keys[i];
		}
		double eachsum = 0;
		int j;
		for (j = eigenMap.size() - 1; j >= 0; j--) {
			eachsum += (Double) keys[j];
			if ((eachsum / sum) >= standard)
				break;
		}

		// 已找到满足要求的主成分，返回对应的特征向量矩阵
		double[][] mainEigenvectorArray = new double[eigenMap.size() - j][eigenvalueArray1D.length];
		for (int i = 0; i < mainEigenvectorArray.length; i++) {
			mainEigenvectorArray[i] = eigenMap
					.get(keys[eigenMap.size() - 1 - i]);
		}
		// 该矩阵要转置
		mainEigenvectorArray = new Matrix(mainEigenvectorArray).transpose()
				.getArray();

		/*
		 * System.out.println("特征值和特征值向量：(一行代表一个特征值对应的特征向量)"); 输出hashMap存储结果 for
		 * (int i = 0; i < eigenvalueArray1D.length; i++) {
		 * System.out.print("特征值："+keys[i]+" 对应的特征向量：\t"); double[]
		 * vector=eigenMap.get(keys[i]); for (j = 0; j < vector.length; j++) {
		 * System.out.print(vector[j]+"\t"); } System.out.println(); }
		 */

		/*System.out.println("主成分对应特征向量：");
		for (int i = 0; i < featureNum; i++) {
			for (int k = 0; k < mainEigenvectorArray[0].length; k++) {
				System.out.print(mainEigenvectorArray[i][k] + "\t");
			}
			System.out.println();
		}*/

		return mainEigenvectorArray;
	}

}
