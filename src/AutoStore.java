 package analytic;


import java.io.IOException;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class AutoStore {

	public AutoStore() {
		SerTimeAut st=new SerTimeAut();
		Input in=new Input();
		int N=26;//robotNum + 1
		int cm=1;//stationNum in w
		double vm=0.25;
		double ESm=st.getTw();
		double ESm2=st.getCVw();
		double ESrem=(cm-1)/(cm+1)*ESm/cm+2/(cm+1)/cm*ESm2/2/ESm;
		double[][] p=new double[N][N];
		double[] TH=new double[N];
		double[] ET=new double[N];
		double[] Q=new double[N];
		double[] EL=new double[N];
		p[0][0]=1;
		System.out.println("Tw="+ESm);
		System.out.println("CVw="+ESm2);
		System.out.println("ESrem="+ESrem);
		//计算前一部分的TH(n)
		
		for(int n=0;n<cm+1;n++) {
			ET[n]=ESm;
			TH[n]=n/(4*vm*ET[n]+2*vm*(st.getTrw1()+st.getTrw2()+st.getTrw3()+st.getTrw4()));
		}
	
		
		for(int n=(int)cm+1;n<N;n++) {
			
			Q[n-1]=ESm/cm*vm*TH[n-1]*(Q[n-2]+p[cm-1][n-2]);
			EL[n-1]=ESm/cm*vm*TH[n-1]*(EL[n-2]+Q[n-2]);
			ET[n]=Q[n-1]*ESrem+EL[n-1]*ESm/cm+ESm;
	
			TH[n]=n/(4*vm*ET[n]+2*vm*(st.getTrw1()+st.getTrw2()+st.getTrw3()+st.getTrw4()));
			
			//计算p1[0][n-1]
			
			p[0][n-1]=1-Q[n-1];
			
		}		
		Q[N-1]=ESm/cm*vm*TH[N-1]*(Q[N-2]+p[cm-1][N-2]);
		EL[N-1]=ESm/cm*vm*TH[N-1]*(EL[N-2]+Q[N-2]);
//		for(int n=0;n<N;n++) {
//			System.out.println("TH"+n+":"+TH[n]);
//			
//		}
//		for(int n=0;n<N;n++) {
//			System.out.println("p[(int)cm2-1]["+""+n+"]="+p[(int)cm-1][(int) n]);
//			
//		}
		for(int n=0;n<N;n++) {
			System.out.println("p[0]["+""+n+"]="+p[0][n]);
			
		}
		System.out.println("EL:");
		for(int n=0;n<N;n++) {
			
			System.out.println(EL[n]);
		}
		System.out.println("Q:");
		for(int n=0;n<N;n++) {

			System.out.println(Q[n]);
			
		}
//		
		double time=0;
		if(in.getPolicy()=="DedRandom" || in.getPolicy()=="DedZone") {
			time=st.getTdr()+st.getTrd()+st.getTsd();
		}
		if(in.getPolicy()=="SharedRandom" || in.getPolicy()=="SharedZone") {
			if(in.getReshuffling()=="imm") {
				time=st.getTdr()+st.getTrsi()+st.getTss();
			}
			if(in.getReshuffling()=="delay") {
				time=st.getTdr()+st.getTrsd()+st.getTss()+st.getTre2();//还得加上从storage point -> retrieval point的时间
			}
			
		}
		System.out.println("c1:");
		double[] c1=new double[N-1];
		for(int i=0;i<N-1;i++) {
			c1[i]=(i+1)/time;
			System.out.println(c1[i]);
		}
		System.out.println("c2:");
		double[] c2=new double[N-1];
		for(int i=0;i<N-1;i++) {
			c2[i]=TH[i+1];
			System.out.println(c2[ i]);
		}

		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy;
		try {
			proxy = factory.getProxy();
			
			//function [THT,Ww,Ur,Up]= equation(lam,c1,c2,EL,Q)
			Object[] result1 = proxy.returningFeval("equation",5,in.getLambda(),c2,c1,EL,Q,in.getNr(),in.getNw());

			double[] r1=(double[]) result1[0];
			double[] r2=(double[]) result1[1];
			double[] r3=(double[]) result1[2];
			double[] r4=(double[]) result1[3];
			double[] r5=(double[]) result1[4];
			double avgTht=r1[0];
			double Ww=r2[0];
			double Ur=r3[0];
			double Up=r4[0];
			double Wr=r5[0];
			System.out.println("lambda="+in.getLambda()*3600);
			System.out.println("avgTht="+avgTht);
			System.out.println("Ww="+Ww);
			System.out.println("Wr="+Wr);
			System.out.println("Ur="+Ur);
			System.out.println("Up="+Up);
			proxy.exit();
		} catch (MatlabConnectionException | MatlabInvocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		new AutoStore();
		
	}
}
