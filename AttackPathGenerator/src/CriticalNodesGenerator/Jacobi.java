package CriticalNodesGenerator;


import java.lang.reflect.Array;
import java.util.*;

public class Jacobi {
//     int N=1005;
     static double eps=1e-5;
     static int lim=100;


    public static void Find_Eigen(int n,double[][] a,double[] EigVal){
        /*
        @a:N×N
        @EigVec:N×N 特征向量
        @EigVal：N×1 特征值
         */
        //初始化单位矩阵
//        for (int i=1;i<=n;i++) EigVec[i][i]=1.0;
        int count=0;

        while (true) {
            //统计迭代次数
            count++;
            //找绝对值最大的元素
            double mx_val = 0;
            int row_id = 0, col_id = 0;
            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    if (Math.abs(a[i][j]) > mx_val) {
                        mx_val = Math.abs(a[i][j]);
                        row_id = i;
                        col_id = j;
                    }
            if (mx_val < eps || count > lim) break;
            //进行旋转变换
            int p = row_id, q = col_id;
            double Apq = a[p][q], App = a[p][p], Aqq = a[q][q];
            double theta = 0.5 * Math.atan2(-2.0 * Apq, Aqq - App);
            double sint = Math.sin(theta), cost = Math.cos(theta);
            double sin2t = Math.sin(2.0 * theta), cos2t = Math.cos(2.0 * theta);
            a[p][p] = App * cost * cost + Aqq * sint * sint + 2.0 * Apq * cost * sint;
            a[q][q] = App * sint * sint + Aqq * cost * cost - 2.0 * Apq * cost * sint;
            a[p][q] = a[q][p] = 0.5 * (Aqq - App) * sin2t + Apq * cos2t;
            for (int i = 0; i < n; i++)
                if (i != p && i != q) {
                    double u = a[p][i], v = a[q][i];
                    a[p][i] = u * cost + v * sint;
                    a[q][i] = v * cost - u * sint;
                    u = a[i][p];
                    v = a[i][q];
                    a[i][p] = u * cost + v * sint;
                    a[i][q] = v * cost - u * sint;
                }


        for (int i=0;i<n;i++)
        {
            EigVal[i]=a[i][i];

        }

     }


    }
}


