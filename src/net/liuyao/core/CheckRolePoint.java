package net.liuyao.core;

import java.util.Vector;
/**
 * 
 * @author Administrator
 *���ּ��Ĳ����Ϊ2�׶Σ���һ�׶μ�����ǽ��ֵĵ���ǽ��ֵĵ㣬�Լ�˫�������п����ǽ��֣��п����Ǽٽ��֣���
 *�ڶ��׶μ�����е�˫������
 *
 *
 *���岽��:
 *���ȣ�����һ�����ߵ����Σ���Ҫ��ȷ���ж����ǲ��ǻ��������ġ����ġ�������߳�����������ݶ�����У�
 *��Ҫע��һ��������������ǡ������Ǽܡ��п��ܻ�Ӱ���������ϵĻ��������ġ����ĵĳ����ԣ�������ϲ�Ҫ�����ж�ʧ��
 *���У����ߵ�˫�����ⲽҲҪ�жϳ�����
 *
 *�������������ж������ϵ�һ���յ��Ƿ��ǽ��֡���ô��������ȡ�����յ��ϵ��ĸ����ߣ��ֱ�������ǵ����Ρ�����ú󣬰�����˳���ж����Ƿ��ǽ��ֻ�������
1. ���������һ����������������ô����������������ǽ��֡�
2. ���������һ�������г�����˫�ģ������������������г��Ļ���ģ���ô������ǽ��֡�
3. ������������������л�������ô������յ���Ϻ��Ӷ����е����ϵĻ������ֱ����������ۣ�
����ÿ������������ʹ�����ܳɻ��ĵ����пյ㡣���������һ���յ㣬�������ڳɻ��ĵ�ͬʱ���γ��������֣�������Ҫ�ݹ��жϣ�����ô����һ������Ч��������
��֮�����������еĻ��ĵ㣬�������ǵ�ͬʱ�����γ���������֣����ⲻ��һ������Ч��������
�������˰ѸղŰ��ϵĺ���ȥ����
���ͨ���������������������Ч����������������ǽ��֡�
4. �����������������㣬�������Ȳ����������ֲ��ǽ��֡�
 */
public class CheckRolePoint {
      private WeighValue weighValue;
      private GameStatus gameStatus;
      //�ǽ���
      public final static int YES = 0;
      public final static int FOUR = 1;
      public final static int THREE = 2;
      public final static int FIVE = 4;
      public final static int SHORT = 7;
      public final static int SINGLELINEROLE = 5;
      //���ǽ���
      public final static int NO = 3;
      public final static int MAYBE = 6;
      
      public final static int HENG = 100;
      public final static int SHU = 101;
      public final static int LEFTXIE = 102;
      public final static int RIGHTXIE = 103;
      public CheckRolePoint(WeighValue weighValue, GameStatus gameStatus){
    	  this.weighValue = weighValue;
    	  this.gameStatus = gameStatus;
      }
      
      public CheckRolePointFirstResult  checkRolePointFirst(byte[] position){
    	  CheckRolePointFirstResult totalResult = new CheckRolePointFirstResult();
    	  
		   boolean empty = true;
		   if(gameStatus.pointStatus[position[0]][position[1]] == GameStatus.WHITE){
			   throwWrong();
		   }else if(gameStatus.pointStatus[position[0]][position[1]] == GameStatus.BLACK){
			   empty = false;
			   
			   gameStatus.pointStatus[position[0]][position[1]] = GameStatus.EMPTY;
		   }
		   //begin��end������y��С����,������x��С����srcҲ�����˳��
		  int[] hengInformation = weighValue.getHengStick(position, GameStatus.BLACK);
		  int[] shuInformation = weighValue.getShuStick(position, GameStatus.BLACK);
		  int[] leftXieInformation = weighValue.getLeftXie(position, GameStatus.BLACK);
		  int[] rightXieInformation = weighValue.getRightXie(position, GameStatus.BLACK);
		  CheckRolePointFirstSingleLineResult hengResult = checkHeng(position, hengInformation);
		  CheckRolePointFirstSingleLineResult shuResult = checkShu(position, shuInformation);
		  CheckRolePointFirstSingleLineResult leftXieResult = checkLeftXie(position, leftXieInformation);
		  CheckRolePointFirstSingleLineResult rightXieResult = checkRightXie(position, rightXieInformation);
		  totalResult.heng = hengResult;
		  totalResult.shu = shuResult;
		  totalResult.leftxie = leftXieResult;
		  totalResult.rightxie = rightXieResult;
		  if(!empty){
		    gameStatus.pointStatus[position[0]][position[1]] = GameStatus.BLACK;
		  }
		  
		  int FIVECOUNT = 0;
		  if(hengResult.flag == FIVE){
			  FIVECOUNT++;
		  }
		  if(shuResult.flag == FIVE){
			  FIVECOUNT++;
		  }
		  if(leftXieResult.flag == FIVE){
			  FIVECOUNT++;
		  }
		  if(rightXieResult.flag == FIVE){
			  FIVECOUNT++;
		  }
		  if(FIVECOUNT > 0){
			  totalResult.finalFlag = FIVE;
			  return totalResult;
		  }
		  
		  int SINGLELINECOUNT = 0;
		  if(hengResult.flag == SINGLELINEROLE){
			  SINGLELINECOUNT++;
		  }
		  if(shuResult.flag == SINGLELINECOUNT){
			  SINGLELINECOUNT++;
		  }
		  if(leftXieResult.flag == SINGLELINECOUNT){
			  SINGLELINECOUNT++;
		  }
		  if(rightXieResult.flag == SINGLELINECOUNT){
			  SINGLELINECOUNT++;
		  }
		  if(SINGLELINECOUNT > 0){
			  totalResult.finalFlag = YES;
			  return totalResult;
		  }
		  
		  int FOURCOUNT = 0;
		  if(hengResult.flag == FOUR){
			  FOURCOUNT++;
		  }
		  if(shuResult.flag == FOUR){
			  FOURCOUNT++;
		  }
		  if(leftXieResult.flag == FOUR){
			  FOURCOUNT++;
		  }
		  if(rightXieResult.flag == FOUR){
			  FOURCOUNT++;
		  }
		  if(FOURCOUNT > 1){
			  totalResult.finalFlag = YES;
			  return totalResult;
		  }
		  int THREECOUNT = 0;
		  if(hengResult.flag == THREE){
			  THREECOUNT++;
		  }
		  if(shuResult.flag == THREE){
			  THREECOUNT++;
		  }
		  if(leftXieResult.flag == THREE){
			  THREECOUNT++;
		  }
		  if(rightXieResult.flag == THREE){
			  THREECOUNT++;
		  }
		
		  if(THREECOUNT > 1){
			  totalResult.finalFlag = MAYBE;
			  return totalResult;
		  }else{
			  totalResult.finalFlag = NO;
			  return totalResult;
		  }
		  
	   }
      public CheckRolePointFirstResult checkRolePointFinal(byte[] position){
    	  CheckRolePointFirstResult totalResult = checkRolePointFirst(position);
    	  if(totalResult.finalFlag == FIVE || totalResult.finalFlag == NO){
     		 return totalResult;
     	 }else if(totalResult.finalFlag == YES){
     		 return totalResult;
     	 }else if(totalResult.finalFlag == MAYBE){
     		 boolean empty = false;
     		 if(gameStatus.pointStatus[position[0]][position[1]] == GameStatus.BLACK){
     			empty = false;
     		 }else if(gameStatus.pointStatus[position[0]][position[1]] == GameStatus.EMPTY){
     			empty = true;
     		 }else{
     			 throwWrong();
     		 }
     		gameStatus.pointStatus[position[0]][position[1]] = GameStatus.BLACK;
     		 
     		 int countATHREE = 0;
     		 if(totalResult.heng.flag == THREE){
     			 
     			 for(int m = 0; m < totalResult.heng.willfourPoint.size(); m++){
     				 byte[] fourPoint = totalResult.heng.willfourPoint.get(m);
     				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
     				CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
     				if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
     					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
     				}else{
     					countATHREE++;
     					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
     					break;
     				}
     				 
     			 }
     		 }
     		if(totalResult.shu.flag == THREE){
    			 for(int m = 0; m < totalResult.shu.willfourPoint.size(); m++){
    				 byte[] fourPoint = totalResult.shu.willfourPoint.get(m);
    				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
    				 CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
    				 if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
    					 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
    				 }else{
    					 countATHREE++;
    					 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
    					 break;
    				 }
    				
    			 }
    		 }
     		if(totalResult.leftxie.flag == THREE){
     			for(int m = 0; m < totalResult.leftxie.willfourPoint.size(); m++){
   				 byte[] fourPoint = totalResult.leftxie.willfourPoint.get(m);
   				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
   				CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
   				if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
   					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
   				}else{
   					countATHREE++;
   					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
   					break;
   				}
   				 
   			 }
     		}
     		if(totalResult.rightxie.flag == THREE){
     			for(int m = 0; m < totalResult.rightxie.willfourPoint.size(); m++){
      				 byte[] fourPoint = totalResult.rightxie.willfourPoint.get(m);
      				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
      				CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
      				if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
      					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
      				}else{
      					countATHREE++;
      					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
      					break;
      				}
      				 
      			 }
     		}
     		if(empty){
     			gameStatus.pointStatus[position[0]][position[1]] = GameStatus.EMPTY;
     		}
     		
     		if(countATHREE > 1){
     			totalResult.finalFlag = YES;
     			return totalResult;
     		}else{
     			totalResult.finalFlag = NO;
     			return totalResult;
     		}
     	 }
     	 else{
     		 throwWrong();
     		 return null;
     	 }
      }
     public CheckRolePointFirstResult checkRolePointFinal(byte[] position, CheckRolePointFirstResult totalResult){

   	 
   	  if(totalResult.finalFlag == FIVE || totalResult.finalFlag == NO){
    		 return totalResult;
    	 }else if(totalResult.finalFlag == YES){
    		 return totalResult;
    	 }else if(totalResult.finalFlag == MAYBE){
    		 int countATHREE = 0;
    		 if(totalResult.heng.flag == THREE){
    			 for(int m = 0; m < totalResult.heng.willfourPoint.size(); m++){
    				 byte[] fourPoint = totalResult.heng.willfourPoint.get(m);
    				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
    				CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
    				if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
    					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
    				}else{
    					countATHREE++;
    					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
    					break;
    				}
    				 
    			 }
    		 }
    		if(totalResult.shu.flag == THREE){
   			 for(int m = 0; m < totalResult.shu.willfourPoint.size(); m++){
   				 byte[] fourPoint = totalResult.shu.willfourPoint.get(m);
   				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
   				 CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
   				 if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
   					 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
   				 }else{
   					 countATHREE++;
   					 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
   					 break;
   				 }
   				
   			 }
   		 }
    		if(totalResult.leftxie.flag == THREE){
    			for(int m = 0; m < totalResult.leftxie.willfourPoint.size(); m++){
  				 byte[] fourPoint = totalResult.leftxie.willfourPoint.get(m);
  				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
  				CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
  				if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
  					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
  				}else{
  					countATHREE++;
  					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
  					break;
  				}
  				 
  			 }
    		}
    		if(totalResult.rightxie.flag == THREE){
    			for(int m = 0; m < totalResult.rightxie.willfourPoint.size(); m++){
     				 byte[] fourPoint = totalResult.rightxie.willfourPoint.get(m);
     				 gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.BLACK;
     				CheckRolePointFirstResult fourPointResult = checkRolePointFinal(fourPoint);
     				if(fourPointResult.finalFlag == FIVE || fourPointResult.finalFlag == YES){
     					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
     				}else{
     					countATHREE++;
     					gameStatus.pointStatus[fourPoint[0]][fourPoint[1]] = GameStatus.EMPTY;
     					break;
     				}
     				 
     			 }
    		}
    		if(countATHREE > 1){
    			totalResult.finalFlag = YES;
    			return totalResult;
    		}else{
    			totalResult.finalFlag = NO;
    			return totalResult;
    		}
    	 }
    	 else{
    		 throwWrong();
    		 return null;
    	 }
     
     }
	   private CheckRolePointFirstSingleLineResult checkHeng(byte[] position, int[] information){
		   
		   if(information[3] - information[2] + 1 < 5){
			   CheckRolePointFirstSingleLineResult result = new CheckRolePointFirstSingleLineResult();
			   result.flag = SHORT;
			   return result;
			   
		   }
		     byte[] src = new byte[information[3] - information[2] + 1];
		     for(int i = 0; i < information[3] - information[2] + 1; i++){
		    	 src[i] = gameStatus.pointStatus[information[2]+i][position[1]];
		     }
		     return checkStick(src, position[0] - information[2], information[0], information[1], position, HENG);
		    
	   }
	   
	   private CheckRolePointFirstSingleLineResult checkShu(byte[] position, int[] information){
		   if(information[3] - information[2] + 1 < 5){
			   CheckRolePointFirstSingleLineResult result = new CheckRolePointFirstSingleLineResult();
			   result.flag = SHORT;
			   return result;
			   
		   }
		   byte[] src = new byte[information[3] - information[2] + 1];
		   for(int i = 0; i < information[3] - information[2] + 1; i++){
			   src[i] = gameStatus.pointStatus[position[0]][information[2]+i];
		   }
		     return checkStick(src, position[1] - information[2], information[0], information[1], position, SHU);
	   }
	   
	   private CheckRolePointFirstSingleLineResult checkRightXie(byte[] position, int[] information){
		   if(information[3] - information[2] + 1 < 5){
			   CheckRolePointFirstSingleLineResult result = new CheckRolePointFirstSingleLineResult();
			   result.flag = SHORT;
			   return result;
			   
		   }
		   byte[] src = new byte[information[3] - information[2] + 1];
		   for(int i = 0; i < information[3] - information[2] + 1; i++){
			   src[i] = gameStatus.pointStatus[information[2]+i][position[1] + (position[0]-information[2]) - i];
		   }
		     return checkStick(src, position[0] - information[2], information[0], information[1], position, RIGHTXIE);
	   }
	   
	   private CheckRolePointFirstSingleLineResult checkLeftXie(byte[] position, int[] information){
		   if(information[3] - information[2] + 1 < 5){
			   CheckRolePointFirstSingleLineResult result = new CheckRolePointFirstSingleLineResult();
			   result.flag = SHORT;
			   return result;
			   
		   }
		   byte[] src = new byte[information[3] - information[2] + 1];
		   for(int i = 0; i < information[3] - information[2] + 1; i++){
			   src[i] = gameStatus.pointStatus[information[2]+i][position[1] - (position[0] - information[2]) + i];
		   }
		     return checkStick(src, position[0] - information[2], information[0], information[1], position, LEFTXIE);
	   }
	 
	    //checkpoint�Ǽ��ĵ�����꣬direction�Ƿ���
	   
	   private CheckRolePointFirstSingleLineResult checkStick(byte[] src, int index, int indexInFive,int num, byte[] checkPoint, int direction){
		   CheckRolePointFirstSingleLineResult result = new CheckRolePointFirstSingleLineResult();
		   result.willfourPoint = new Vector<byte[]>();
		   //�峤�߶ε�����������index
		   int stickBeginIndex =index - indexInFive;
		   int stickEndIndex = index + 4 - indexInFive;
		   if(num == 4){
			  
				   
				   if(
						   (stickBeginIndex > 0 && src[stickBeginIndex - 1] == GameStatus.BLACK)
						   || (stickEndIndex != src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK)					
						   ){
					   result.flag = SINGLELINEROLE;
					   return result;
				   }else{
					   //���ǽ���
					   result.flag = FIVE;
					   return result;
				   }
			   
		   }else if(num == 3){
			   
			   /*�Ѿ�����3���Ѿ� */
			   //�������ĵ��ж�
			   
			   if(indexInFive == 3 && src[stickBeginIndex + 1] == GameStatus.EMPTY && stickEndIndex + 2 <= src.length - 1){
				   if(
						   (stickBeginIndex == 0 || (stickBeginIndex > 0 && src[stickBeginIndex - 1] == GameStatus.EMPTY))
						   && src[stickEndIndex + 1] == GameStatus.EMPTY
						   && src[stickEndIndex + 2] == GameStatus.BLACK
						   && ((stickEndIndex + 2 == src.length - 1) || (stickEndIndex + 2 < src.length - 1 && src[stickEndIndex + 3] == GameStatus.EMPTY))
						   ){
					   
						   result.flag = SINGLELINEROLE;
						   return result;
					   
				   }
			   }
			   
			  if(indexInFive == 3 && src[stickBeginIndex + 2] == GameStatus.EMPTY && stickEndIndex + 3 <= src.length - 1){
				  if(
						src[stickEndIndex + 1] == GameStatus.EMPTY
						&& src[stickEndIndex + 2] == GameStatus.BLACK
						&& src[stickEndIndex + 3] == GameStatus.BLACK
						&& (stickBeginIndex == 0 || (stickBeginIndex > 0 && src[stickBeginIndex - 1] == GameStatus.EMPTY))
						&&(stickEndIndex + 3 == src.length - 1 || (stickEndIndex + 3 < src.length - 1 && src[stickEndIndex + 4] == GameStatus.EMPTY))
						  ){
					  
					  
					        result.flag = SINGLELINEROLE;
						   return result;
					  
				  }
			  }
			   
			   
			//һ�����
			   if(src[stickBeginIndex] == GameStatus.EMPTY  && src[stickEndIndex] == GameStatus.EMPTY){
				   //�м���������+1ooo1+
				  
				   
				    if(stickBeginIndex == 0 && indexInFive == 0){
					   //С�ĳ����Ǽ�
					   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
				   }
				   else if(stickEndIndex == src.length - 1 && indexInFive == 4){
					   //С�ĳ����Ǽ�
					   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
				   }
				   
				   else{
					   //�γɻ���
					   result.flag = FOUR;
					   return result;
				   }
			   }else if(src[stickBeginIndex] == GameStatus.EMPTY && src[stickEndIndex-1] == GameStatus.EMPTY){
				   //+1oo1o+ 
				  
				   
				   if(stickBeginIndex >= 2 && stickEndIndex + 4 <= src.length - 1){
					  
					   if(indexInFive == 0){
						   //С�ĳ����Ǽ�
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   //С�ĹǼܳ���
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 4 > src.length - 1 && stickEndIndex+1 <= src.length-1){
					 //+1oo1o+ 
					   if(indexInFive == 0){
						   //С�ĳ����Ǽ�
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
							   
						   }
					   }else if(indexInFive == 3){
						   //С�ĳ����Ǽ�
						 //+1oo1o+ 
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex < 2 && stickEndIndex + 4 <= src.length - 1){
					 //+1oo1o+
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   //+1oo1o+
						   //С�ĳ����Ǽ�
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex+1 > src.length-1){
					 //+1oo1o+
					   if(indexInFive == 0){
						  
						   result.flag = FOUR;
						   return result;
					   }else if(indexInFive == 3){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickBeginIndex < 2 && stickEndIndex + 4 > src.length - 1 && stickEndIndex+1 <= src.length-1){
					
					 //+1oo1o+
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex < 2 &&  stickEndIndex+1 > src.length-1){
					   
					   
					   result.flag = FOUR;
					   return result;
				   }
				   else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == GameStatus.EMPTY && src[stickBeginIndex+1] == GameStatus.EMPTY){
				   //+o1oo1+ ������ķ�֧�ǶԳƵ�
				   
				   
				  
				   if(stickEndIndex+2 <= src.length-1 && stickBeginIndex >= 4){
					   
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else if(indexInFive == 1){
						 //+o1oo1+
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 <= src.length-1 && stickBeginIndex < 4 && stickBeginIndex >= 1){
					 //+o1oo1+
					  if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 1){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 > src.length-1 && stickBeginIndex >= 4){
					 //+o1oo1+
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 1){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 <= src.length-1 && stickBeginIndex < 1){
					 //+o1oo1+
					  
					   if(indexInFive == 4){
						  
						   result.flag = FOUR;
						   return result;
					   }else{
						  
						   result.flag = FOUR;
						   return result;
					   }
				   }else if(stickEndIndex+2 > src.length-1 && stickBeginIndex < 4 && stickBeginIndex >= 1){
					 //+o1oo1+
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 1){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 > src.length-1 &&  stickBeginIndex < 1){
					 //+o1oo1+
					   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   if(indexInFive == 1){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else{
						   result.flag = FOUR;
						   return result;
					   }   
				   }
				   else{
					   throwWrong();
				   }
			   
			   }else if(src[stickBeginIndex] == GameStatus.EMPTY  && src[stickBeginIndex+2] == GameStatus.EMPTY){
				   //+1o1oo+
				 
				   
				   if(stickEndIndex == src.length-1 && stickBeginIndex >= 3){
					  
					   if(indexInFive == 0){
						
						   result.flag = FOUR;
						   return result;
					   }else if(indexInFive == 2){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						 throwWrong();
						   
					   }
				   }else if(stickEndIndex == src.length-1 && stickBeginIndex < 3){
					 //+1o1oo+
					   if(indexInFive == 2 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
					   
				   }else if(stickEndIndex < src.length-1 && stickEndIndex+3 > src.length-1 && stickBeginIndex >= 3){
					 //+1o1oo+
					   
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 2){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   &&stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
						   
					   }
				   }else if(stickEndIndex < src.length-1 && stickEndIndex+3 > src.length-1 && stickBeginIndex < 3){
					 //+1o1oo+
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 2){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickEndIndex+3 <= src.length-1 && stickBeginIndex >= 3){
					 //+1o1oo+
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 2){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickEndIndex+3 <= src.length-1 && stickBeginIndex < 3){
					 //+1o1oo+
					   
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 2){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
				   
			   }else if(src[stickEndIndex] == GameStatus.EMPTY  && src[stickEndIndex-2] == GameStatus.EMPTY){
				   //+oo1o1+ ������Գ�

				  
				
				   if(stickBeginIndex == 0 && stickEndIndex + 3 <=  src.length - 1){
					   
					   if(indexInFive == 4){
						   
						   result.flag = FOUR;
						   return result;
					   }
					   else{
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }  
					   }
				   }else if(stickBeginIndex == 0 && stickEndIndex + 3 >  src.length - 1){
					 //+oo1o1+
					   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
					   
				   }else if(stickBeginIndex > 0 && stickBeginIndex < 3 && stickEndIndex + 3 <=  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 2){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
						   
					   }
				   }else if(stickBeginIndex > 0 && stickBeginIndex < 3 && stickEndIndex + 3 >  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 2){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickBeginIndex >= 3 && stickEndIndex + 3 <=  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
							   
						   }
					   }else if(indexInFive == 2){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickBeginIndex >= 3 && stickEndIndex + 3 >  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 2){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
				   
			   
			   }else if(src[stickBeginIndex] == GameStatus.EMPTY  && src[stickBeginIndex+1] == GameStatus.EMPTY){
				   //+2ooo+
				   
				   if(stickEndIndex +2 <= src.length - 1 && stickBeginIndex >= 4){
					  
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 1){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickEndIndex +2 <= src.length - 1 && stickBeginIndex < 4){
					 //+2ooo+
					   if(indexInFive == 0){
						  if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							  result.flag = SHORT;
							  return result;
						  }else{
							  result.flag = FOUR;
							   return result;
						  }
					   }else if(indexInFive == 1){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
								  return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }
				   else if(stickEndIndex + 1 == src.length - 1 && stickBeginIndex >= 4){
					 //+2ooo+
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
								  return result;
							  }else{
								  result.flag = FOUR;
								   return result;
							  }
					   }else if(indexInFive == 1){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
								  return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickEndIndex + 1 == src.length - 1 && stickBeginIndex < 4){
					 //+2ooo+
					   if(indexInFive == 0){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
								  return result;
							  }else{
								  result.flag = FOUR;
								   return result;
							  }
					   }else if(indexInFive == 1){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
								  return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }
				   else if(stickEndIndex == src.length - 1 && stickBeginIndex >= 4){
					 //+2ooo+
					   if(indexInFive == 0){
						  
						   result.flag = FOUR;
						   return result;
					   }else if(indexInFive == 1){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex == src.length - 1 && stickBeginIndex < 4){
					 //+2ooo+
					   if(indexInFive == 1 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
					   
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == GameStatus.EMPTY  && src[stickEndIndex - 1] == GameStatus.EMPTY){
				   //+ooo2+ ����������Գ�				   
				   
				   if(stickBeginIndex >= 2 && stickEndIndex + 4 <= src.length - 1){
					 
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 4 > src.length - 1){
					 //+ooo2+ 
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }
				   else if(stickBeginIndex == 1 && stickEndIndex + 4 <= src.length - 1){
					 //+ooo2+ 
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickBeginIndex == 1 && stickEndIndex + 4 > src.length - 1){
					 //+ooo2+ 
					   
					   if(indexInFive == 4){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }
				   else if(stickBeginIndex == 0 && stickEndIndex + 4 <= src.length - 1){
					 //+ooo2+
					   if(indexInFive == 4){
						   
						   result.flag = FOUR;
						   return result;
					   }else if(indexInFive == 3){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickBeginIndex == 0 && stickEndIndex + 4 > src.length - 1){
					   if(indexInFive == 3 && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
					   
				   }else{
					   throwWrong();
				   }
			   
				   
				   
			   }else if(src[stickBeginIndex + 1] == GameStatus.EMPTY  && src[stickEndIndex - 1] == GameStatus.EMPTY){
				   //+o1o1o+
				   
				   if(stickBeginIndex < 2 && stickEndIndex + 2 > src.length - 1){
					   if(indexInFive == 1 && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(indexInFive == 3 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
					   
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 2 > src.length - 1){
					   if(indexInFive == 1){
						   
						 //+o1o1o+
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   if(
									   (stickBeginIndex == 2 || (stickBeginIndex >= 3 && src[stickBeginIndex - 3] != GameStatus.BLACK))
									   && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
								   result.flag = FOUR;
								   return result; 
							   }else{
								   result.flag = SHORT;
								   return result;
							   }
							   
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else{
						   //indexInFive == 3
						  if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							  result.flag = SHORT;
							   return result;
						  }else{
							  result.flag = FOUR;
							   return result;
						  }
						   
					   }
				   }else if(stickBeginIndex < 2 && stickEndIndex + 2 <= src.length - 1){
					 //+o1o1o+
					   if(indexInFive == 1){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else{
						 //indexInFive == 3
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   if(
									   (stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK)) 
									   && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
								   result.flag = FOUR;
								   return result;
							   }else{
								   result.flag = SHORT;
								   return result;
							   }
							   
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 2 <= src.length - 1){
					 //+o1o1o+
					   if(indexInFive == 1 && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   if(
								   (stickBeginIndex == 2 || (stickBeginIndex >= 3 && src[stickBeginIndex - 3] != GameStatus.BLACK))
								   && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = SHORT;
							   return result;
						   }
						   
					   }else if(indexInFive == 3 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   if(
								   (stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK))
								   && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = FOUR;
							   return result;
						   }else{
							   result.flag = SHORT;
							   return result;
						   }
						   
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
						   
					  
				   }
			   }else if(src[stickBeginIndex + 1] == GameStatus.EMPTY  && src[stickBeginIndex + 2] == GameStatus.EMPTY){
				   //+o2oo+
				   
				   if(indexInFive == 1){
					   if(stickBeginIndex >= 3){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   if(
									   (stickBeginIndex == 3 || (stickBeginIndex >= 4 && src[stickBeginIndex - 4] != GameStatus.BLACK))
									   && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK && src[stickBeginIndex - 3] == GameStatus.BLACK){
								   result.flag = FOUR;
								   return result;
							   }else{
								   result.flag = SHORT;
								   return result;
							   }
							   
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else if(stickBeginIndex < 3){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					 //+o2oo+
					   
					   if(stickEndIndex + 2 <= src.length - 1){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   ){
							   if(stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK)){
								   result.flag = FOUR;
								   return result;
							   }else{
								   result.flag = SHORT;
								   return result;
							   }
							   
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
								   if(stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK)){
									   result.flag = FOUR;
									   return result;
								   }else{
									   result.flag = SHORT;
									   return result;
								   }
								   
							   }else{
								   result.flag = SHORT;
								   return result;
							   }
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else if(stickEndIndex + 2 > src.length - 1){
						   if(stickEndIndex + 1 <= src.length -1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length -1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
							  
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickBeginIndex + 2] == GameStatus.EMPTY  && src[stickBeginIndex + 3] == GameStatus.EMPTY){
				        //oo11o ������Գ�
				   
				   if(indexInFive == 3){
					   if(stickEndIndex + 3 <= src.length - 1){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   if(src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK && src[stickEndIndex + 3] == GameStatus.BLACK){
								  if(stickEndIndex + 3 == src.length - 1 || (stickEndIndex + 4 <= src.length - 1 && src[stickEndIndex + 4] != GameStatus.BLACK)){
									  result.flag = FOUR;
									   return result;
								  }else{
									  result.flag = SHORT;
									   return result;
								  }
								   
							   }else{
								   result.flag = SHORT;
								   return result;
							   }
							   
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else if(stickEndIndex + 3 > src.length - 1){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
						   
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					 //oo11o
					   if(stickBeginIndex >= 2){
						   if(
								   (stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK)
								   || (stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK)
								   ){
							   if(src[stickBeginIndex - 1] == GameStatus.BLACK || src[stickBeginIndex - 2] == GameStatus.BLACK){
								   if(stickBeginIndex == 2 || (stickBeginIndex >= 3 && src[stickBeginIndex - 3] != GameStatus.BLACK)){
									   result.flag = FOUR;
									   return result;
								   }else{
									   result.flag = SHORT;
									   return result;
								   }
							   }else{
								   result.flag = SHORT;
								   return result;
							   }
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else if(stickBeginIndex < 2){
						   if(
								   (stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK)
								   || (stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK)
								   ){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
			   
				       
			   }else{
				   throwWrong();
			   }
		   }else if(num == 2){
			   /* 
			    * �Ѿ�ӵ��2���Ѿ�
			    * */
			   			   
			  			   			  			   		   			   
			   if(src[stickBeginIndex] == GameStatus.BLACK && src[stickEndIndex] == GameStatus.BLACK){
				   result.flag = SHORT;
				   return result;
			   }else if(src[stickBeginIndex] == GameStatus.BLACK && src[stickEndIndex - 1] == GameStatus.BLACK){
				   //o11o1
				   if(indexInFive == 1){
					   //oo1o1
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfour = new byte[2];
						   if(direction == HENG){
							   willfour[0] = (byte) (checkPoint[0] + 1);
							   willfour[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfour[0] = checkPoint[0];
							   willfour[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == LEFTXIE){
							   willfour[0] = (byte) (checkPoint[0] + 1);
							   willfour[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == RIGHTXIE){
							   willfour[0] = (byte) (checkPoint[0] + 1);
							   willfour[1] = (byte) (checkPoint[1] - 1);
						   }
						   result.willfourPoint.add(willfour);
						   return result;
					   }
					   
				   }else if(indexInFive == 2){
					   //o11o1
					   //o1oo1
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfour = new byte[2];
						   if(direction == HENG){
							   willfour[0] = (byte) (checkPoint[0] - 1);
							   willfour[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfour[0] = checkPoint[0];
							   willfour[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == LEFTXIE){
							   willfour[0] = (byte) (checkPoint[0] - 1);
							   willfour[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == RIGHTXIE){
							   willfour[0] = (byte) (checkPoint[0] - 1);
							   willfour[1] = (byte) (checkPoint[1] + 1);
						   }
						   result.willfourPoint.add(willfour);
						   return result;
					   }
					   
				   }else if(indexInFive == 4){
					   //o11o1
					   //o11oo
					   result.flag = SHORT;
					   return result;
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == GameStatus.BLACK && src[stickBeginIndex + 1] == GameStatus.BLACK){
				   //1o11o ������Գ�

				   
				   if(indexInFive == 3){
					   //1o1oo
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfour = new byte[2];
						   if(direction == HENG){
							   willfour[0] = (byte) (checkPoint[0] - 1);
							   willfour[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfour[0] = checkPoint[0];
							   willfour[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == LEFTXIE){
							   willfour[0] = (byte) (checkPoint[0] - 1);
							   willfour[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == RIGHTXIE){
							   willfour[0] = (byte) (checkPoint[0] - 1);
							   willfour[1] = (byte) (checkPoint[1] + 1);
						   }
						   result.willfourPoint.add(willfour);
						   return result;
					   }
					   
				   }else if(indexInFive == 2){
					   //1o11o
					   //1oo1o
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfour = new byte[2];
						   if(direction == HENG){
							   willfour[0] = (byte) (checkPoint[0] + 1);
							   willfour[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfour[0] = checkPoint[0];
							   willfour[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == LEFTXIE){
							   willfour[0] = (byte) (checkPoint[0] + 1);
							   willfour[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == RIGHTXIE){
							   willfour[0] = (byte) (checkPoint[0] + 1);
							   willfour[1] = (byte) (checkPoint[1] - 1);
						   }
						   result.willfourPoint.add(willfour);
						   return result;
					   }
				   }else if(indexInFive == 0){
					   //1o11o
					   //oo11o
					   result.flag = SHORT;
					   return result;
				   }else{
					   throwWrong();
				   }
			   
			   }else if(src[stickBeginIndex] == GameStatus.BLACK && src[stickEndIndex - 2] == GameStatus.BLACK){
				   //o1o11
				   if(indexInFive == 1){
					   //ooo11
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK
							   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickBeginIndex == 1){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] - 2);
							   }
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }else{
						   if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK){
							   result.flag = THREE;
							   
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] - 2);
							   }
							   
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){

							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] + 2);
							   }
							   
							   result.willfourPoint.add(willfourBefore);
							   
							   return result;
						   
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] + 2);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] - 2);
							   }
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
						   
					   }
					   
				   }else if(indexInFive == 3){
					 //o1o11
					   //o1oo1
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfourBefore = new byte[2];
						   if(direction == HENG){
							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
							   willfourBefore[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourBefore[0] = checkPoint[0];
							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
						   }else if(direction == LEFTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
						   }else if(direction == RIGHTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
							   willfourBefore[1] = (byte) (checkPoint[1] + 2);
						   }
						   result.willfourPoint.add(willfourBefore);
						   return result;
					   }
					   
				   }else if(indexInFive == 4){
					 //o1o11
					 //o1o1o
					  result.flag = SHORT;
					  return result;
				   }
			   }else if(src[stickEndIndex] == GameStatus.BLACK && src[stickBeginIndex + 2] == GameStatus.BLACK){
				   //11o1o ������Գ�

				   
				   if(indexInFive == 3){
					   //11o1o
					   //11ooo
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK
							   && stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickEndIndex + 1 == src.length - 1){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] + 2);
							   }
							   result.willfourPoint.add(willfourBefore);
							   return result;
						   }
						   
					   }else{
                           if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
                        	   result.flag = THREE;
    						   
    						   byte[] willfourAfter = new byte[2];
    						   if(direction == HENG){
    							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
    							   willfourAfter[1] = checkPoint[1];
    						   }else if(direction == SHU){
    							   willfourAfter[0] = checkPoint[0];
    							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
    						   }else if(direction == LEFTXIE){
    							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
    							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
    						   }else if(direction == RIGHTXIE){
    							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
    							   willfourAfter[1] = (byte) (checkPoint[1] - 2);
    						   }
    						   
    						   result.willfourPoint.add(willfourAfter);
    						   return result;
                           }else if(stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
                        	   result.flag = THREE;
    						   byte[] willfourBefore = new byte[2];
    						   if(direction == HENG){
    							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
    							   willfourBefore[1] = checkPoint[1];
    						   }else if(direction == SHU){
    							   willfourBefore[0] = checkPoint[0];
    							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
    						   }else if(direction == LEFTXIE){
    							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
    							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
    						   }else if(direction == RIGHTXIE){
    							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
    							   willfourBefore[1] = (byte) (checkPoint[1] + 2);
    						   }
    						   
    						   result.willfourPoint.add(willfourBefore);
    						 
    						   return result;
                           }else{
                        	   result.flag = THREE;
    						   byte[] willfourBefore = new byte[2];
    						   if(direction == HENG){
    							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
    							   willfourBefore[1] = checkPoint[1];
    						   }else if(direction == SHU){
    							   willfourBefore[0] = checkPoint[0];
    							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
    						   }else if(direction == LEFTXIE){
    							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
    							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
    						   }else if(direction == RIGHTXIE){
    							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
    							   willfourBefore[1] = (byte) (checkPoint[1] + 2);
    						   }
    						   byte[] willfourAfter = new byte[2];
    						   if(direction == HENG){
    							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
    							   willfourAfter[1] = checkPoint[1];
    						   }else if(direction == SHU){
    							   willfourAfter[0] = checkPoint[0];
    							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
    						   }else if(direction == LEFTXIE){
    							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
    							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
    						   }else if(direction == RIGHTXIE){
    							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
    							   willfourAfter[1] = (byte) (checkPoint[1] - 2);
    						   }
    						   result.willfourPoint.add(willfourBefore);
    						   result.willfourPoint.add(willfourAfter);
    						   return result;
                           }
						   
						   
					   
					   }
					   
				   }else if(indexInFive == 1){
					 //11o1o
					   //1oo1o
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfourAfter = new byte[2];
						   if(direction == HENG){
							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
							   willfourAfter[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourAfter[0] = checkPoint[0];
							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
						   }else if(direction == LEFTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
						   }else if(direction == RIGHTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
							   willfourAfter[1] = (byte) (checkPoint[1] - 2);
						   }
						   
						   result.willfourPoint.add(willfourAfter);
						   return result;
					   }
					  
				   }else if(indexInFive == 0){
					 //o1o1o
					 result.flag = SHORT;
					 return result;
				   }else{
					   throwWrong();
				   }
			   
			   }else if(src[stickBeginIndex] == GameStatus.BLACK && src[stickEndIndex - 3] == GameStatus.BLACK){
				   //oo111
				   if(indexInFive == 2){
					   //ooo11
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK
							   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickBeginIndex == 1){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							   
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }else{
						   if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK){
							   result.flag = THREE;
							   
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							   
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							  
							   result.willfourPoint.add(willfourBefore);
							   
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }
					  
				   }else if(indexInFive == 3){
					 //oo111
					   //oo1o1
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						  result.flag = THREE;
						  byte[] willfourBefore = new byte[2];
						   if(direction == HENG){
							   willfourBefore[0] = (byte) (checkPoint[0] - 1);
							   willfourBefore[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourBefore[0] = checkPoint[0];
							   willfourBefore[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == LEFTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 1);
							   willfourBefore[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == RIGHTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 1);
							   willfourBefore[1] = (byte) (checkPoint[1] + 1);
						   }
						   result.willfourPoint.add(willfourBefore);
						   return result;
					   }
					   
				   }else if(indexInFive == 4){
					 //oo111
					 //oo11o
					  result.flag = SHORT;
					  return result;
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == GameStatus.BLACK && src[stickBeginIndex + 3] == GameStatus.BLACK){
				   //111oo ������Գ�

				   
				   if(indexInFive == 2){
					 //111oo
					   //11ooo
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK
							   && stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickEndIndex + 1 == src.length - 1){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   result.willfourPoint.add(willfourBefore);
							   return result;
						   }
						   
					   }else{
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = THREE;
							   
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   
							   result.willfourPoint.add(willfourBefore);
							   
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }
					   
				   }else if(indexInFive == 1){
					   //111oo
					   //1o1oo
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfourAfter = new byte[2];
						   if(direction == HENG){
							   willfourAfter[0] = (byte) (checkPoint[0] + 1);
							   willfourAfter[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourAfter[0] = checkPoint[0];
							   willfourAfter[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == LEFTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 1);
							   willfourAfter[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == RIGHTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 1);
							   willfourAfter[1] = (byte) (checkPoint[1] - 1);
						   }
						   
						   result.willfourPoint.add(willfourAfter);
						   return result;
					   }
					   
				   }else if(indexInFive == 0){
					 //111oo
					 //o11oo
					 result.flag = SHORT;
					 return result;
				   }else{
					   throwWrong();
				   }
			   
			   }else if(src[stickBeginIndex + 1] == GameStatus.BLACK && src[stickEndIndex - 1] == GameStatus.BLACK){
				   //1o1o1
				   if(indexInFive == 0){
					 //1o1o1
					   //oo1o1
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfourAfter = new byte[2];
						   if(direction == HENG){
							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
							   willfourAfter[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourAfter[0] = checkPoint[0];
							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
						   }else if(direction == LEFTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
							   willfourAfter[1] = (byte) (checkPoint[1] + 2);
						   }else if(direction == RIGHTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 2);
							   willfourAfter[1] = (byte) (checkPoint[1] - 2);
						   }
						   result.willfourPoint.add(willfourAfter);
						   return result;
					   }
					   
				   }else if(indexInFive == 2){
					 //1o1o1
					   //1ooo1
					   if(stickBeginIndex == 0 && stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK
							   && stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickBeginIndex == 0){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] - 2);
							   }
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
						   
					   }else if(stickEndIndex == src.length - 1){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] + 2);
							   }
							   result.willfourPoint.add(willfourBefore);
							   return result;
						   }
						   
					   }else{
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = THREE;
							   
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] - 2);
							   }
							   
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] + 2);
							   }
							   
							   result.willfourPoint.add(willfourBefore);
							  
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] - 2);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 2);
								   willfourBefore[1] = (byte) (checkPoint[1] + 2);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] + 2);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 2);
								   willfourAfter[1] = (byte) (checkPoint[1] - 2);
							   }
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }
					  
				   }else if(indexInFive == 4){
					 //1o1o1
					   //1o1oo
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfourBefore = new byte[2];
						   if(direction == HENG){
							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
							   willfourBefore[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourBefore[0] = checkPoint[0];
							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
						   }else if(direction == LEFTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
							   willfourBefore[1] = (byte) (checkPoint[1] - 2);
						   }else if(direction == RIGHTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 2);
							   willfourBefore[1] = (byte) (checkPoint[1] + 2);
						   }
						   result.willfourPoint.add(willfourBefore);
						   return result;
					   }
					  
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickBeginIndex + 1] == GameStatus.BLACK && src[stickBeginIndex + 2] == GameStatus.BLACK){
				   //1oo11
				   if(indexInFive == 0){
					   //ooo11
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK
							   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickBeginIndex == 1){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						  
					   }else{
						   if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK){
							   result.flag = THREE;
							  
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   
							   result.willfourPoint.add(willfourBefore);
							   
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }
					   
				   }else if(indexInFive == 3){
					 //1oo11
					   //1ooo1
					   if(stickBeginIndex == 0 && stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK
							   && stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickBeginIndex == 0){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }else if(stickEndIndex == src.length - 1){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							   result.willfourPoint.add(willfourBefore);
							   return result;
						   }
						   
					   }else{
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = THREE;
							   
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							  
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							   
							   result.willfourPoint.add(willfourBefore);
						
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }
					   
				   }else if(indexInFive == 4){
					 //1oo11
					   //1oo1o
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfourBefore = new byte[2];
						   if(direction == HENG){
							   willfourBefore[0] = (byte) (checkPoint[0] - 1);
							   willfourBefore[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourBefore[0] = checkPoint[0];
							   willfourBefore[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == LEFTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 1);
							   willfourBefore[1] = (byte) (checkPoint[1] - 1);
						   }else if(direction == RIGHTXIE){
							   willfourBefore[0] = (byte) (checkPoint[0] - 1);
							   willfourBefore[1] = (byte) (checkPoint[1] + 1);
						   }
						   result.willfourPoint.add(willfourBefore);
						   return result;
					   }
					 
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex - 1] == GameStatus.BLACK && src[stickBeginIndex + 2] == GameStatus.BLACK){
				   
				   //11oo1 ������Գ�

				   
				   if(indexInFive == 4){
					 //11oo1
					   //11ooo
					   if(stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK
							   && stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickEndIndex + 1 == src.length - 1){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							   result.willfourPoint.add(willfourBefore);
							   return result;
						   }
						   
					   }else{
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result.flag = THREE;
							   
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							  
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							   
							   result.willfourPoint.add(willfourBefore);
						
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] - 3);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 3);
								   willfourBefore[1] = (byte) (checkPoint[1] + 3);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] + 1);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 1);
								   willfourAfter[1] = (byte) (checkPoint[1] - 1);
							   }
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }
					 
				   }else if(indexInFive == 1){
					 //11oo1
					   //1ooo1
					   if(stickBeginIndex == 0 && stickEndIndex == src.length - 1){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK
							   && stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else if(stickBeginIndex == 0){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }else if(stickEndIndex == src.length - 1){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   result.willfourPoint.add(willfourBefore);
							   return result;
						   }
						   
					   }else{
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result.flag = THREE;
							   
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   
							   
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   
							   
							   result.willfourPoint.add(willfourBefore);
							   
							   return result;
						   }else{
							   result.flag = THREE;
							   byte[] willfourBefore = new byte[2];
							   if(direction == HENG){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourBefore[0] = checkPoint[0];
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == LEFTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] - 1);
							   }else if(direction == RIGHTXIE){
								   willfourBefore[0] = (byte) (checkPoint[0] - 1);
								   willfourBefore[1] = (byte) (checkPoint[1] + 1);
							   }
							   byte[] willfourAfter = new byte[2];
							   if(direction == HENG){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = checkPoint[1];
							   }else if(direction == SHU){
								   willfourAfter[0] = checkPoint[0];
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == LEFTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] + 3);
							   }else if(direction == RIGHTXIE){
								   willfourAfter[0] = (byte) (checkPoint[0] + 3);
								   willfourAfter[1] = (byte) (checkPoint[1] - 3);
							   }
							   
							   result.willfourPoint.add(willfourBefore);
							   result.willfourPoint.add(willfourAfter);
							   return result;
						   }
						   
					   }
					 
				   }else if(indexInFive == 0){
					   //11oo1 
					   //o1oo1
					   if(stickBeginIndex == 0){
						   result.flag = SHORT;
						   return result;
					   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 2] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }
					   else{
						   result.flag = THREE;
						   byte[] willfourAfter = new byte[2];
						   if(direction == HENG){
							   willfourAfter[0] = (byte) (checkPoint[0] + 1);
							   willfourAfter[1] = checkPoint[1];
						   }else if(direction == SHU){
							   willfourAfter[0] = checkPoint[0];
							   willfourAfter[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == LEFTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 1);
							   willfourAfter[1] = (byte) (checkPoint[1] + 1);
						   }else if(direction == RIGHTXIE){
							   willfourAfter[0] = (byte) (checkPoint[0] + 1);
							   willfourAfter[1] = (byte) (checkPoint[1] - 1);
						   }
						   
						   result.willfourPoint.add(willfourAfter);
						   return result;
					   }
					   
				   }else{
					   throwWrong();
				   }
			   
			   }else{
				   throwWrong();
			   }
			   
			   
			   
		   }else if(num == 1){
			   //�Ѿ���һ���Ѿ�
			   result.flag = SHORT;
			   return result;
			   
		   }else if(num == 0){
			   //��û���Ѿ�
			   result.flag = SHORT;
			   return result;
		   }else{
			   throwWrong();
			   return null;
		   }
		   
		    return null;
	   }
      public void throwWrong(){
   	   int a = 1/0;
      }
}
