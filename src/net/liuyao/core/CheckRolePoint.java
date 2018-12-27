package net.liuyao.core;

import java.util.Vector;
/**
 * 
 * @author Administrator
 *禁手检查的步奏分为2阶段，第一阶段检出不是禁手的点和是禁手的点，以及双活三（有可能是禁手，有可能是假禁手）。
 *第二阶段检查所有的双活三。
 *
 *
 *具体步奏:
 *首先，对于一个单线的棋形，你要正确的判断它是不是活三、冲四、活四、连五或者长连。这个根据定义就行，
 *但要注意一种特殊情况，就是“长连骨架”有可能会影响这条线上的活三、冲四、活四的成立性，在这点上不要出现判断失误。
 *还有，单线的双四在这步也要判断出来。
 *
 *接下来，我们判断棋盘上的一个空点是否是禁手。那么，我们提取出个空点上的四个单线，分别计算它们的棋形。计算好后，按如下顺序判断它是否是禁手或五连：
1. 如果至少有一个单线是五连，那么这个点是五连，不是禁手。
2. 如果至少有一个单线有长连或双四，或者至少两个单线有冲四或活四，那么这个点是禁手。
3. 如果至少有两个单线有活三，那么把这个空点摆上黑子对所有单线上的活三，分别作如下讨论：
对于每个活三，考虑使得它能成活四的所有空点。如果至少有一个空点，能让它在成活四的同时不形成连五或禁手（这里需要递归判断），那么这是一个“有效活三”。
反之，若对于所有的活四点，在走它们的同时都会形成五连或禁手，则这不是一个“有效活三”。
最后别忘了把刚才摆上的黑子去掉。
如果通过这个点至少有两个“有效活三”，则这个点是禁手。
4. 若上述条件均不满足，则这个点既不是五连，又不是禁手。
 */
public class CheckRolePoint {
      private WeighValue weighValue;
      private GameStatus gameStatus;
      //是禁手
      public final static int YES = 0;
      public final static int FOUR = 1;
      public final static int THREE = 2;
      public final static int FIVE = 4;
      public final static int SHORT = 7;
      public final static int SINGLELINEROLE = 5;
      //不是禁手
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
		   //begin到end竖线是y从小到大,其他是x从小到大，src也是这个顺序
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
	 
	    //checkpoint是检查的点的坐标，direction是方向
	   
	   private CheckRolePointFirstSingleLineResult checkStick(byte[] src, int index, int indexInFive,int num, byte[] checkPoint, int direction){
		   CheckRolePointFirstSingleLineResult result = new CheckRolePointFirstSingleLineResult();
		   result.willfourPoint = new Vector<byte[]>();
		   //五长线段的起点在数组的index
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
					   //不是禁手
					   result.flag = FIVE;
					   return result;
				   }
			   
		   }else if(num == 3){
			   
			   /*已经含有3个友军 */
			   //单线四四点判断
			   
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
			   
			   
			//一般情况
			   if(src[stickBeginIndex] == GameStatus.EMPTY  && src[stickEndIndex] == GameStatus.EMPTY){
				   //中间三子相连+1ooo1+
				  
				   
				    if(stickBeginIndex == 0 && indexInFive == 0){
					   //小心长连骨架
					   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
				   }
				   else if(stickEndIndex == src.length - 1 && indexInFive == 4){
					   //小心长连骨架
					   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result.flag = SHORT;
						   return result;
					   }else{
						   result.flag = FOUR;
						   return result;
					   }
				   }
				   
				   else{
					   //形成活四
					   result.flag = FOUR;
					   return result;
				   }
			   }else if(src[stickBeginIndex] == GameStatus.EMPTY && src[stickEndIndex-1] == GameStatus.EMPTY){
				   //+1oo1o+ 
				  
				   
				   if(stickBeginIndex >= 2 && stickEndIndex + 4 <= src.length - 1){
					  
					   if(indexInFive == 0){
						   //小心长连骨架
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
						   }
					   }else if(indexInFive == 3){
						   //小心骨架长连
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
						   //小心长连骨架
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result.flag = SHORT;
							   return result;
						   }else{
							   result.flag = FOUR;
							   return result;
							   
						   }
					   }else if(indexInFive == 3){
						   //小心长连骨架
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
						   //小心长连骨架
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
				   //+o1oo1+ 和上面的分支是对称的
				   
				   
				  
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
				   //+oo1o1+ 和上面对称

				  
				
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
				   //+ooo2+ 和上面情况对称				   
				   
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
				        //oo11o 和上面对称
				   
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
			    * 已经拥有2名友军
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
				   //1o11o 和上面对称

				   
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
				   //11o1o 和上面对称

				   
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
				   //111oo 和上面对称

				   
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
				   
				   //11oo1 和上面对称

				   
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
			   //已经有一名友军
			   result.flag = SHORT;
			   return result;
			   
		   }else if(num == 0){
			   //还没有友军
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
