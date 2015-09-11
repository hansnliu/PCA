package com.myml.pca.main;

import Jama.Matrix;

import com.myml.pca.dao.DataSetdao;
import com.myml.pca.impl.PcaDataImpl;
import com.myml.pca.util.MatrixOperate;

public class PCA implements PcaDataImpl {
	@Override
	public double[][] getPcaData() {
		// TODO Auto-generated method stub

		// 获取某一数据集
		DataSetdao datasetdao = new DataSetdao();
		double[][] array = datasetdao.getAllData("wine");

		// leukemia(白血病)
		// LeukemiaDao lmdao = new LeukemiaDao();
		// double[][] array = lmdao.getAllLeukemia();

		// featureNum表示有多少个特征
		int featureNum = array[0].length;

		// 后面的方法定义在MatrixOperate类中
		MatrixOperate mo = new MatrixOperate();

		// 1'最大最小规范化预处理，落在[0,1]空间
		// System.out.println("最大最小规范化预处理：");
		// array = mo.minmaxNormalization(array);

		// 1'零均值规范化预处理
		// System.out.println("最大最小规范化预处理：");
		array = mo.zeromeanNormalization(array);

		// 1'特征中心化
		// System.out.println("特征中心化：");
		// array = mo.FeaCenter(array);

		// 2'得到array协方差数组,行列数（特征值）自己定义
		// double[][] covariation = new double[featureNum][featureNum];
		double[][] covariation = new double[featureNum][featureNum];
		for (int i = 0; i < covariation.length; i++) {
			for (int j = 0; j <= i; j++) {
				covariation[i][j] = mo.GetCovariation(array, i, j);
				covariation[j][i] = covariation[i][j]; // 对称
			}
		}

/*		System.out.println("协方差矩阵：");
		for (int i = 0; i < covariation.length; i++) {
			for (int j = 0; j < covariation.length; j++) {
				System.out.print(covariation[i][j] + "\t");
			}
			System.out.println();
		}*/

		// 协方差数组转换为矩阵，便于后面直接调用特征*函数计算
		Matrix cov = new Matrix(covariation);

		// 3'得到主特征向量矩阵
		double[][] mainEigenvectorArray = mo.GetMainEigenvectorArray(cov, 0.99);

		// 4'原数据集矩阵乘上主特征向量矩阵实现降维
		Matrix arraymat = new Matrix(array);
		Matrix meamat = new Matrix(mainEigenvectorArray);
		arraymat = arraymat.times(meamat);

		// 5'将矩阵转为二维数组，便于后面使用者不引jama包
		double[][] pcaArray = arraymat.getArray();

		// 5+'数据集样本加上编号
		pcaArray = addDataId(pcaArray);

		System.out.println("降维后的数据集：");
		int row = pcaArray.length;
		int colunmn = pcaArray[0].length;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < colunmn; j++) {
				System.out.print(pcaArray[i][j] + "\t");
			}
			System.out.println();
		}

		return pcaArray;
	}

	// 降维后数据集加上编号
	public double[][] addDataId(double[][] array) {
		int row = array.length;
		int column = array[0].length + 1;
		double[][] idarray = new double[row][column];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				if (j == 0) {
					idarray[i][j] = i + 1;
				} else
					idarray[i][j] = array[i][j - 1];
			}
		}
		/*
		 * for (int i = 0; i < row; i++) { for (int j = 0; j < column; j++) {
		 * System.out.print(idarray[i][j]+"\t"); } System.out.println(); }
		 */
		return idarray;
	}

	public static void main(String[] args) {
		new PCA().getPcaData();
	}
}
