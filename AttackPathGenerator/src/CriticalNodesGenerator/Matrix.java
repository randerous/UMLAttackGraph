package CriticalNodesGenerator;


import AttackPathGenerator.*;
import java.lang.Math;
import java.math.BigInteger;
import java.util.*;

public class Matrix {
    Graph graph;
//    int n=graph.vertexes.size();//�ܽ����
    int n;
    int m;//�ڵ����нڵ����
    double[][] AdjacentMatrix =new double[n][n];//�ڽӾ����������ͼһ��
    double[][] DegreeMatrix=new double[n][n];//���+����
    double[][] Laplacian=new double[n][n];
    Map<String,Integer> vertexesIndex=new HashMap(n);//index��Ӧnode��id

    public  Matrix(Graph graph,int m){
        this.graph=graph;
        this.n=graph.vertexes.size();
        this.m=m;

        int i=0;
        for(Map.Entry<String,Vertex> v:graph.vertexes.entrySet()){
            vertexesIndex.put(new String(v.getKey()),i++);
        }
        for(Map.Entry<String,Vertex> v:graph.vertexes.entrySet()) {
            int source=vertexesIndex.get(v.getKey());
            DegreeMatrix[source][source]+=v.getValue().getNextV().size();
            for(Vertex each:v.getValue().getNextV()){
                int target=vertexesIndex.get(each.getItself().getID());
                AdjacentMatrix[source][target]=1;//�����Ժ���Ըĳɱߵ�Ȩֵ
                AdjacentMatrix[target][source]=1;
                DegreeMatrix[target][target]++;
            }
        }
    }

    public  Matrix(double[][] adjM,double[][] DegM,int n,int m){
        this.AdjacentMatrix=adjM;
        this.DegreeMatrix=DegM;
        this.n=n;
        this.m=m;
    }

    public  Matrix(double[][] lap,int n,int m){
        this.Laplacian=lap;
        this.n=n;
        this.m=m;
    }



    public void GetLaplacian(){
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
                this.Laplacian[i][j]=this.DegreeMatrix[i][j]-this.AdjacentMatrix[i][j];
    }


    public List<Integer> getCriticalNodesForce(int m, List<Double> MinEigenvalues){
        /*
        @m:ѡ��m���ڵ���Ϊ�ؼ��ڵ��飨�кܶ���ѡ������Ҫѡ����С����ֵ�������飩
        @MinEigenvalues: ���������С����ֵ����Ȼ��Ҳ��֪�����������ʲô��
        @return:����������С����ֵ�Ľڵ���
         */
        List<List<Integer>> combinations=Comb.combine(n,m);
        int size=combinations.size();
        System.out.println("size="+size);
//        List<Double>MinEigenvalues=new ArrayList<>(size);

        List<Integer> res=null;
        double max=0;
        for(int i=0;i<size;i++){
            /*
            1. ��ȡmatrix
            2. ѭ����ȡ����ֵ
             */
            List<Integer> combi=combinations.get(i);
            System.out.println("��"+i+"��");
            double[][] subMatrix=new double[n-m][n-m];
            double[] EigVal=new double[n-m];
            for(int j=1,x=0;j<=n && x<n-m;j++){
                //ɾ����j��
                if(combi.contains(j)) continue;
                for(int k=1,y=0;k<=n && y<n-m;k++){
                    //ɾ����k��
                    if(combi.contains(k)) continue;
                    subMatrix[x][y++]=Laplacian[j-1][k-1];
                }
                x++;
            }

            Jacobi.Find_Eigen(n-m,subMatrix,EigVal);
            double minEign=EigVal[0];
            for(int j=1;j<n-m;j++)
                if(EigVal[j]<minEign) minEign=EigVal[j];
            MinEigenvalues.add(minEign);
            if(max<minEign) {
                max = minEign;
                res=combi;
            }
        }

        return res;
    }



}
