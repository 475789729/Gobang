package net.liuyao.core;

import java.util.Random;
import java.util.Vector;

public class BeginHelper {
   public final static byte[][][] beginLib = new byte[26][][];
   static{
	   for(int i = 0; i < beginLib.length; i++){
		   beginLib[i] = new byte[2][];
		   beginLib[i][0] = new byte[2];
		   beginLib[i][1] = new byte[2];
	   }
	   //����
	   beginLib[0][0][0] = 1;
	   beginLib[0][0][1] = -1;
	   beginLib[0][1][0] = 1;
	   beginLib[0][1][1] = -1;
	   
	   //Ͽ��
	   beginLib[1][0][0] = 1;
	   beginLib[1][0][1] = -1;
	   beginLib[1][1][0] = 1;
	   beginLib[1][1][1] = 0;
	   
	   //����
	   beginLib[2][0][0] = 1;
	   beginLib[2][0][1] = -1;
	   beginLib[2][1][0] = 1;
	   beginLib[2][1][1] = 1;
	   
	 //ˮ��
	   beginLib[3][0][0] = 1;
	   beginLib[3][0][1] = -1;
	   beginLib[3][1][0] = 1;
	   beginLib[3][1][1] = 2;
	   
	   //����
	   beginLib[4][0][0] = 1;
	   beginLib[4][0][1] = -1;
	   beginLib[4][1][0] = 1;
	   beginLib[4][1][1] = 3;
	   
	   //����
	   beginLib[5][0][0] = 1;
	   beginLib[5][0][1] = -1;
	   beginLib[5][1][0] = 0;
	   beginLib[5][1][1] = 1;
	   
	   //����
	   beginLib[6][0][0] = 1;
	   beginLib[6][0][1] = -1;
	   beginLib[6][1][0] = 0;
	   beginLib[6][1][1] = 2;
	   
	 //���
	   beginLib[7][0][0] = 1;
	   beginLib[7][0][1] = -1;
	   beginLib[7][1][0] = 0;
	   beginLib[7][1][1] = 3;
	   
	 //����
	   beginLib[8][0][0] = 1;
	   beginLib[8][0][1] = -1;
	   beginLib[8][1][0] = -1;
	   beginLib[8][1][1] = 2;
	   
	 //����
	   beginLib[9][0][0] = 1;
	   beginLib[9][0][1] = -1;
	   beginLib[9][1][0] = -1;
	   beginLib[9][1][1] = 3;
	   
	 //б��
	   beginLib[10][0][0] = 1;
	   beginLib[10][0][1] = -1;
	   beginLib[10][1][0] = -2;
	   beginLib[10][1][1] = 2;
	   
	 //����
	   beginLib[11][0][0] = 1;
	   beginLib[11][0][1] = -1;
	   beginLib[11][1][0] = -2;
	   beginLib[11][1][1] = 3;
	   
	 //����
	   beginLib[12][0][0] = 1;
	   beginLib[12][0][1] = -1;
	   beginLib[12][1][0] = -3;
	   beginLib[12][1][1] = 3;
	   
	 //����
	   beginLib[13][0][0] = 0;
	   beginLib[13][0][1] = -1;
	   beginLib[13][1][0] = 0;
	   beginLib[13][1][1] = -1;
	   
	   //Ϫ��
	   beginLib[14][0][0] = 0;
	   beginLib[14][0][1] = -1;
	   beginLib[14][1][0] = 1;
	   beginLib[14][1][1] = -1;
	   
	 //����
	   beginLib[15][0][0] = 0;
	   beginLib[15][0][1] = -1;
	   beginLib[15][1][0] = 2;
	   beginLib[15][1][1] = -1;
	   
	 //����
	   beginLib[16][0][0] = 0;
	   beginLib[16][0][1] = -1;
	   beginLib[16][1][0] = 1;
	   beginLib[16][1][1] = 0;
	   
	 //����
	   beginLib[17][0][0] = 0;
	   beginLib[17][0][1] = -1;
	   beginLib[17][1][0] = 2;
	   beginLib[17][1][1] = 0;
	   
	 //����
	   beginLib[18][0][0] = 0;
	   beginLib[18][0][1] = -1;
	   beginLib[18][1][0] = 1;
	   beginLib[18][1][1] = 1;
	   
	 //����
	   beginLib[19][0][0] = 0;
	   beginLib[19][0][1] = -1;
	   beginLib[19][1][0] = 2;
	   beginLib[19][1][1] = 1;
	   
	 //����
	   beginLib[20][0][0] = 0;
	   beginLib[20][0][1] = -1;
	   beginLib[20][1][0] = 0;
	   beginLib[20][1][1] = 2;
	   
	   //����
	   beginLib[21][0][0] = 0;
	   beginLib[21][0][1] = -1;
	   beginLib[21][1][0] = 1;
	   beginLib[21][1][1] = 2;
	   
	   //����
	   beginLib[22][0][0] = 0;
	   beginLib[22][0][1] = -1;
	   beginLib[22][1][0] = 2;
	   beginLib[22][1][1] = 2;
	   
	 //����
	   beginLib[23][0][0] = 0;
	   beginLib[23][0][1] = -1;
	   beginLib[23][1][0] = 0;
	   beginLib[23][1][1] = 3;
	   
	 //ɽ��
	   beginLib[24][0][0] = 0;
	   beginLib[24][0][1] = -1;
	   beginLib[24][1][0] = 1;
	   beginLib[24][1][1] = 3;
	   
	 //����
	   beginLib[25][0][0] = 0;
	   beginLib[25][0][1] = -1;
	   beginLib[25][1][0] = 2;
	   beginLib[25][1][1] = 3;
   }
   
   //��������
   public static byte[] getDuStep(Vector<byte[]> historyPoint){
	   byte[] result = new byte[2];
	   result[0] = -1;
	   result[1] = -1;
	   int r =  new Random().nextInt(13);
	   if(historyPoint.size() == 1){
		 if(r % 2 == 0){
			 result[0] = (byte) (historyPoint.get(0)[0] + 1);
			 result[1] = (byte) (historyPoint.get(0)[1] - 1);
		 }else{
			 result[0] = historyPoint.get(0)[0];
			 result[1] = (byte) (historyPoint.get(0)[1] - 1);
		 }
	   }else if(historyPoint.size() == 2){
		   int x = historyPoint.get(1)[0] - historyPoint.get(0)[0];
		   int y = historyPoint.get(1)[1] - historyPoint.get(0)[1];
		   if(x == 1 && y == -1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[6][1][0];
			   int yA = beginLib[6][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] + yA);
		   }else if(x == 1 && y == 1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[6][1][0];
			   int yA = beginLib[6][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] - yA);
		   }else if(x == -1 && y == -1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[6][1][0];
			   int yA = beginLib[6][1][1];
			   result[0] = (byte) (sec[0] - xA);
			   result[1] = (byte) (sec[1] + yA);
		   }else if(x == -1 && y == 1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[6][1][0];
			   int yA = beginLib[6][1][1];
			   result[0] = (byte) (sec[0] - xA);
			   result[1] = (byte) (sec[1] - yA);
		   }else if(x == 0 && y == -1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[16][1][0];
			   int yA = beginLib[16][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] + yA);
		   }else if(x == 0 && y == 1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[16][1][0];
			   int yA = beginLib[16][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] - yA);
		   }else if(x == 1 && y == 0){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[16][1][0];
			   int yA = beginLib[16][1][1];
			   result[0] = (byte) (sec[0] + yA);
			   result[1] = (byte) (sec[1] - xA);
		   }else if(x == -1 && y == 0){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[16][1][0];
			   int yA = beginLib[16][1][1];
			   result[0] = (byte) (sec[0] - yA);
			   result[1] = (byte) (sec[1] - xA);
		   }else{
			   return null;
		   }
	   }else{
		   return null;
	   }
	   
	   if(result[0] >= 0 && result[0] <= 14 && result[1] >= 0 && result[1] <= 14){
		   return result;
	   }else{
		   return null;
	   }
   }
   
   
   public static byte[] getStep(Vector<byte[]> historyPoint){
	   byte[] result = new byte[2];
	   result[0] = -1;
	   result[1] = -1;
	   int r =  new Random().nextInt(13);
	   if(historyPoint.size() == 1){
		 if(r % 2 == 0){
			 result[0] = (byte) (historyPoint.get(0)[0] + 1);
			 result[1] = (byte) (historyPoint.get(0)[1] - 1);
		 }else{
			 result[0] = historyPoint.get(0)[0];
			 result[1] = (byte) (historyPoint.get(0)[1] - 1);
		 }
	   }else if(historyPoint.size() == 2){
		   int x = historyPoint.get(1)[0] - historyPoint.get(0)[0];
		   int y = historyPoint.get(1)[1] - historyPoint.get(0)[1];
		   if(x == 1 && y == -1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r][1][0];
			   int yA = beginLib[r][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] + yA);
		   }else if(x == 1 && y == 1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r][1][0];
			   int yA = beginLib[r][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] - yA);
		   }else if(x == -1 && y == -1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r][1][0];
			   int yA = beginLib[r][1][1];
			   result[0] = (byte) (sec[0] - xA);
			   result[1] = (byte) (sec[1] + yA);
		   }else if(x == -1 && y == 1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r][1][0];
			   int yA = beginLib[r][1][1];
			   result[0] = (byte) (sec[0] - xA);
			   result[1] = (byte) (sec[1] - yA);
		   }else if(x == 0 && y == -1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r + 13][1][0];
			   int yA = beginLib[r + 13][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] + yA);
		   }else if(x == 0 && y == 1){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r + 13][1][0];
			   int yA = beginLib[r + 13][1][1];
			   result[0] = (byte) (sec[0] + xA);
			   result[1] = (byte) (sec[1] - yA);
		   }else if(x == 1 && y == 0){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r + 13][1][0];
			   int yA = beginLib[r + 13][1][1];
			   result[0] = (byte) (sec[0] + yA);
			   result[1] = (byte) (sec[1] - xA);
		   }else if(x == -1 && y == 0){
			   byte[] sec = historyPoint.get(1);
			   int xA = beginLib[r + 13][1][0];
			   int yA = beginLib[r + 13][1][1];
			   result[0] = (byte) (sec[0] - yA);
			   result[1] = (byte) (sec[1] - xA);
		   }else{
			   return null;
		   }
	   }else{
		   return null;
	   }
	   
	   if(result[0] >= 0 && result[0] <= 14 && result[1] >= 0 && result[1] <= 14){
		   return result;
	   }else{
		   return null;
	   }
   }
}
