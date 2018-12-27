package net.liuyao.core;

import java.util.Vector;

public class VCTHelper {
	/**
	 * 生命周期:攻方可能性为冲点，或者守点()，守方可能性为更强冲点，或者守点。某方赢棋或者攻方已经没有冲点结束(轮攻方时发现没有冲点，轮守方时发现没有攻方的高分点)
	 *整个类存在逻辑bug，但是仍然有使用价值，详见checkG,checkF方法
	 *逻辑bug的危害，如果不是vct判断为vct危害最大，如果漏掉，则危害稍小，在getBest方法里面谨慎使用
	 *修正逻辑bug的方法很简单，去掉check范围裁剪。但是下棋速度受影响
	 */
   private GameStatus gameStatus;
   private AI ai;
   private int range = 20;
   private int initF = -1;
   private byte gColor;
   public  static int MAX_DEEP = 10;
   //AI在第几步走出了vct
   public int AIvctRecoder = -1;
   //防守方冲四转移攻方注意
   private Vector<Integer> fGoFour = new Vector<Integer>();
   
   public VCTHelper(GameStatus gameStatus, AI ai){
	   this.gameStatus = gameStatus;
	   this.ai = ai;
   }
   //color传入攻方
   //生命周期结束返回null，表示不是vct
   //返回剪裁好的可能位置
   private byte[][] chooseGMayBe(int color){
	 //前range位是攻方冲点，后range位是守方冲点
	   byte[][] maybe = new byte[range * 2][];
	   for(int i = 0; i < maybe.length; i++){
		   maybe[i] = new byte[2];
		   maybe[i][0] = -1;
		   maybe[i][1] = -1;
	   }
	   float[][][] gScore = null;
	   float[][][] fScore = null;
	   int gCount = 0;  
	   int glevelfiveCount = 0;
	   int gleveldoublethreeCount = 0;
	   int gleveldoublefourCount = 0;  
	   int flevelfiveCount = 0;   
	   if(color == GameStatus.BLACK){
		   //攻方是黑棋
		   gScore = gameStatus.pointBlackScore;
		   fScore = gameStatus.pointWhiteScore;
	   }else{
			   //攻方是白棋
		   gScore = gameStatus.pointWhiteScore;
		   fScore = gameStatus.pointBlackScore;
		   }
		   //为攻方选择前range位，攻方冲点
		   for(byte i = gameStatus.left; i <= gameStatus.right; i++){
			   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
				   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY 
						  ){
					   if(    gScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL ||
							   (
							   (gScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL ||
							   gScore[i][j][1] == WeighValue.DEFAULT_THREE_LEVEL ||
							   gScore[i][j][0] > 10000f)
							   && checkG(i, j)
							   )
							   ){
						   for(int m = 0; m < range; m++){
							   if(maybe[m][0] == -1){
								   maybe[m][0] = i;
								   maybe[m][1] = j;
								   gCount++;
								   if(gScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL){
									   glevelfiveCount++;
								   }else if(gScore[i][j][0] > 100000f){
									   gleveldoublefourCount++;
								   }else if(gScore[i][j][0] > 10000f){
									   gleveldoublethreeCount++;
								   }
								   break;
							   }
						   }
					   }
				   }
			   }
		   }
		   //为攻方选择后range位，守方冲点
		   for(byte i = gameStatus.left; i <= gameStatus.right; i++){
			   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
				   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
					   if(fScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL){
						   for(int m = 0; m < range; m++){
							   if(maybe[range + m][0] == -1){
								   maybe[range + m][0] = i;
								   maybe[range + m][1] = j;
								   flevelfiveCount++;
								   break;
							   }
						   }
					   }
				   }
			   }
			}
		   
		   //判断生命周期是否结束，如果攻方赢棋，结束，但是不要返回null，仅仅做裁剪   
		   //如果攻方冲点数量为0，结束，返回null
		   if(gCount == 0){
			   return null;
		   }else{
			  
				   //剪裁
				   
				   
				   if(glevelfiveCount > 0){
					   //裁剪前range
					   for(int i = 0; i < range; i++){
						   int x = maybe[i][0];
						   int y = maybe[i][1];
						   if(x != -1){
							   if(gScore[x][y][1] != WeighValue.DEFAULT_FIVE_LEVEL){
								   maybe[i][0] = -1;
								   maybe[i][1] = -1;
							   }
						   }
						   
					   }
					   //裁剪后range
					   for(int i = 0; i < range; i++){
						   maybe[i + range][0] = -1;
						   maybe[i + range][1] = -1;
					   }
				   }else if(flevelfiveCount > 0){
					   for(int i = 0; i < range; i++){
						   maybe[i][0] = -1;
						   maybe[i][1] = -1;
						   
					   }
					   for(int i = 0; i < range; i++){
						   int x = maybe[i + range][0];
						   int y = maybe[i + range][1];
						   if(x != -1){
							   if(fScore[x][y][1] != WeighValue.DEFAULT_FIVE_LEVEL){
								   maybe[i + range][0] = -1;
								   maybe[i + range][1] = -1;
							   }
						   }
						   
					   }
				   }else if(gleveldoublefourCount > 0){
					   for(int i = 0; i < range; i++){
						   int x = maybe[i][0];
						   int y = maybe[i][1];
						   if(x != -1){
							   if(gScore[x][y][0] < 100000f){
								   maybe[i][0] = -1;
								   maybe[i][1] = -1;
							   }
						   }
						   
					   }
					   
					   for(int i = 0; i < range; i++){
						   maybe[i + range][0] = -1;
						   maybe[i + range][1] = -1;
					   }
				   }else if(gleveldoublethreeCount > 0){
					   for(int i = 0; i < range; i++){
						   int x = maybe[i][0];
						   int y = maybe[i][1];
						   if(x != -1){
							   if(gScore[x][y][1] == WeighValue.DEFAULT_THREE_LEVEL){
								   maybe[i][0] = -1;
								   maybe[i][1] = -1;
							   }
						   }
						   
					   }
				   }
			   
		   }
	   
	   return maybe;
   }
   //color传入守方color
   private byte[][] chooseFMaybe(int color){
	   //前range位是攻方冲点，后range位是守方冲点
	   byte[][] maybe = new byte[range * 2][];
	   for(int i = 0; i < maybe.length; i++){
		   maybe[i] = new byte[2];
		   maybe[i][0] = -1;
		   maybe[i][1] = -1;
	   }
	   float[][][] gScore = null;
	   float[][][] fScore = null;
	   
	   int gCount = 0;
	 
	   
	   int glevelfiveCount = 0;
	 
	   int flevelfiveCount = 0;
	  
	   int fleveldoublefourlevel = 0;
	   
	   if(color == GameStatus.WHITE){
		   fScore = gameStatus.pointWhiteScore;
		   gScore = gameStatus.pointBlackScore;
	   }else{
		   fScore = gameStatus.pointBlackScore;
		   gScore = gameStatus.pointWhiteScore;
	   }
	   //选择前range，攻方冲点
	   for(byte i = gameStatus.left; i <= gameStatus.right; i++){
		   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
			   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY
					  ){
				   if(
						   (gScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL
						|| gScore[i][j][1] == WeighValue.DEFAULT_DOUBLEFOUR
						|| gScore[i][j][1] == WeighValue.DEFAULT_AFOUR_LEVEL
						|| gScore[i][j][1] == WeighValue.DEFAULT_THREEFOUR
						|| gScore[i][j][1] == WeighValue.DEFAULT_DOUBLETHREE
						|| againstAThree(i, j, color))
						
						   ){
					   gCount++;
					   for(int m = 0; m < range; m++){
						   if(maybe[m][0] == -1){
							   maybe[m][0] = i;
							   maybe[m][1] = j;
							   if(gScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL){
								   glevelfiveCount++;
							   }
							   break;
						   }
					   }
				   }
			   }
			     
			   }
		   }
	   
	   //选择后range，守方冲点
	   for(byte i = gameStatus.left; i <= gameStatus.right; i++){
		   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
			   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
				   if(
						   (fScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL
					  || fScore[i][j][0] > 100000f	 
					  || (fScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL && checkF(i, j)))
					  
						   ){
					 
					   for(int m = 0; m < range; m++){
						   if(maybe[range + m][0] == -1){
							   maybe[range + m][0] = i;
							   maybe[range + m][1] = j;
							   if(fScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL){
								   flevelfiveCount++;
							   }else if(fScore[i][j][0] > 100000f){
								   fleveldoublefourlevel++;
							   }
							   break;
						   }
					   }
				   }
				   
			   }
			  }
		   }
	   
	   //判断是否生命周期结束
	   if(gCount == 0){
		   return null;
	   }
	   
	   //剪裁
	   if(flevelfiveCount > 0){
		   for(int i = 0; i < range; i++){
			   maybe[i][0] = -1;
			   maybe[i][1] = -1;
			   
		   }
		   for(int i = 0; i < range; i++){
			   int x = maybe[i + range][0];
			   int y = maybe[i + range][1];
			   if(x != -1){
				   if(fScore[x][y][1] != WeighValue.DEFAULT_FIVE_LEVEL){
					   maybe[i + range][0] = -1;
					   maybe[i + range][1] = -1;
				   }
			   }
			   
		   }
	   }else if(glevelfiveCount > 0){
		   for(int i = 0; i < range; i++){
			   int x = maybe[i][0];
			   int y = maybe[i][1];
			   if(x != -1){
				   if(gScore[x][y][1] != WeighValue.DEFAULT_FIVE_LEVEL){
					   maybe[i][0] = -1;
					   maybe[i][1] = -1;
				   }
			   }
			   
		   }
		   
		   for(int i = 0; i < range; i++){
			   maybe[range + i][0] = -1;
			   maybe[range + i][1] = -1;
		   }
	   }else if(fleveldoublefourlevel > 0){
		   for(int i = 0; i < range; i++){
			   maybe[i][0] = -1;
			   maybe[i][1] = -1;
			   
		   }
		   
		   for(int i = 0; i < range; i++){
			   int x = maybe[i + range][0];
			   int y = maybe[i + range][1];
			   if(x != -1){
				   if(fScore[x][y][0] < 100000f){
					   maybe[i + range][0] = -1;
					   maybe[i + range][1] = -1;
				   }
			   }
			   
		   }
	   }
	   
	   return maybe;
   }
   
   //补丁方法，守方堵活三（不紧密的活三）时候，采取堵两边的方法，同时让自己方面获得活三。之前没考虑这种情况，造成很多错杀
   //color传入守方color
   private boolean againstAThree(byte x, byte y, int color){
	   float[][][] gScore = null;
	   float[][][] fScore = null;
	   if(color == GameStatus.WHITE){
		   fScore = gameStatus.pointWhiteScore;
		   gScore = gameStatus.pointBlackScore;
	   }else{
		   fScore = gameStatus.pointBlackScore;
		   gScore = gameStatus.pointWhiteScore;
	   }
	   
	   if(gScore[x][y][1] == WeighValue.DEFAULT_DFOUR_LEVEL && fScore[x][y][1] == WeighValue.DEFAULT_THREE_LEVEL){
		   return true;
	   }else{
		   return false;
	   }
   }
   //补丁方法，如果堵三四的同时，形成守方四，造成误判
   //color形成三四，对方堵四，并且形成反攻四
   private boolean againstFour(int color){
	   float[][][] gScore = null;
	   float[][][] fScore = null;
	   if(color == GameStatus.BLACK){
		   fScore = gameStatus.pointWhiteScore;
		   gScore = gameStatus.pointBlackScore;
	   }else{
		   fScore = gameStatus.pointBlackScore;
		   gScore = gameStatus.pointWhiteScore;
	   }
	   byte[] lastPoint = ai.forwardPoint.get(ai.forwardPoint.size() - 1);
	   if(gScore[lastPoint[0]][lastPoint[1]][1] == WeighValue.DEFAULT_THREEFOUR){
		   for(byte i = gameStatus.left; i <= gameStatus.right; i++){
			   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
				   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
					   if(
							   gScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL 
							   && (fScore[i][j][1] == WeighValue.DEFAULT_AFOUR_LEVEL || fScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL || fScore[i][j][1] == WeighValue.DEFAULT_DOUBLEFOUR || fScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL || fScore[i][j][1] == WeighValue.DEFAULT_THREEFOUR)
							   ){
						   return true;
					   }
				   }
				   
			   }
			}
	   }
	   
	   
	   return false;
   }
   
   //外部调用此方法，color传入攻方color,deep传入1
   public VCTresult getVCT(int color, int deep){
	   
	   
	   byte[][] maybe = null;	   
	   VCTresult result = new VCTresult();
       if(deep > MAX_DEEP){
    	   result.x = 0;
		   return result;
	    }
	   if(color == getGcolor()){
		   maybe = chooseGMayBe(getGcolor());
		   result.x = 0;
	   }else{
		   maybe = chooseFMaybe(0 - getGcolor());
		   result.x = 1;
		  
	   }
	   if(maybe == null){
		  
		   result.x = 0;
		   return result;
	   }
	   for(int i = 0; i < maybe.length; i++){
		   if(maybe[i][0] != -1 && maybe[i][1] != -1){
			   //细节处理，初始化initF
			   if(getGcolor() == color && initF == -1){
				   float[][][] gScore = null;
				   int x = maybe[i][0];
				   int y = maybe[i][1];
				   if(getGcolor() == GameStatus.BLACK){
					   gScore = gameStatus.pointBlackScore;
				   }else{
					   gScore = gameStatus.pointWhiteScore;
				   }
				   if(gScore[x][y][1] == WeighValue.DEFAULT_DOUBLETHREE || gScore[x][y][1] == WeighValue.DEFAULT_THREE_LEVEL){
					   initF = ai.forwardPoint.size() + 1;
				   }
			   }
			   //细节处理，记录防守冲四。它会转移攻击的注意力，使得后面的攻击不是接着最后一步进行。
			   if(getGcolor() == 0 - color){
				   float[][][] fScore = null;
				   int x = maybe[i][0];
				   int y = maybe[i][1];
				   if(getGcolor() == GameStatus.BLACK){
					   fScore = gameStatus.pointWhiteScore;
				   }else{
					   fScore = gameStatus.pointBlackScore;
				   }
				   if(fScore[x][y][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
					   fGoFour.add(ai.forwardPoint.size() + 1);
				   }
			   }
			   //做正事
			   ai.forward(new byte[]{maybe[i][0],maybe[i][1]}, (byte) color, AI.DEEPANALYZE);
			   if(gameStatus.winner == color){
				   if(color == getGcolor()){
					   result.x = 1;
					   result.position = new byte[2];
					   result.position[0] = maybe[i][0];
					   result.position[1] = maybe[i][1];
				   }else{
					   result.x = 0;
					   
				   }
			   }else if(gameStatus.winner == 0 - color){
				   
			   }
			   
			   else if(gameStatus.black_DoubleFour > 0 && gameStatus.white_DoubleFour == -1 && !(againstFour(GameStatus.BLACK))){
				   if(color == GameStatus.BLACK){
					   //color是黑棋并且赢了
					   if(color == getGcolor()){
						   result.x = 1;
						   result.position = new byte[2];
						   result.position[0] = maybe[i][0];
						   result.position[1] = maybe[i][1];
					   }else{
						   result.x = 0;
						   
					   }
				   }else{
					   
				   }
			   }else if(gameStatus.black_DoubleFour == -1 && gameStatus.white_DoubleFour > 0 && !(againstFour(GameStatus.WHITE))){
				   if(color == GameStatus.WHITE){
					   //color是白棋并且赢了
					   if(color == getGcolor()){
						   result.x = 1;
						   result.position = new byte[2];
						   result.position[0] = maybe[i][0];
						   result.position[1] = maybe[i][1];
					   }else{
						   result.x = 0;
						   
					   }
				   }else{
					   
				   }
			   }
			    
			    else{
				   VCTresult r = getVCT(0 - color, deep + 1);
				   if(color == getGcolor()){
					   if(r.x == 1){
						   result.x = 1;
						   result.position = new byte[2];
						   result.position[0] = maybe[i][0];
						   result.position[1] = maybe[i][1];
					   }
				   }else{
					   if(r.x == 0){
						   result.x = 0;
					   }
				   }
			   }
			   
			   ai.backward();
			   //细节处理
			   if(ai.forwardPoint.size() < initF){
				   initF = -1;
			   }
			   //细节处理
			   if(fGoFour.size() > 0 && ai.forwardPoint.size() < fGoFour.get(fGoFour.size() - 1)){
				   fGoFour.remove(fGoFour.size() - 1);
			   }
			   //做正事
			   if(color == getGcolor()){
				   if(result.x == 1){
					   break;
				   }
			   }else{
				   if(result.x == 0){
					   break;
				   }
			   }
		   }
	   }
	   return result;
   }
   
   public byte getGcolor(){
	   return this.gColor;
   }
   public void setGcolor(byte color){
	   this.gColor = color;
   }
   
   //是否在米字范围
   private boolean check(int i, int j,int lastX, int lastY, int color){
	   int hengBegin = lastX;
		int hengEnd = lastX;
		int shuBegin = lastY;
		int shuEnd = lastY;
		int leftXieBegin = lastX;
		int leftXieEnd = lastX;
		int rightXieBegin = lastX;
		int rightXieEnd = lastX;
		
		//heng
		for(int t = lastX - 1;  t >= lastX - 4; t--){
			if(t == -1 || gameStatus.pointStatus[t][lastY] == 0 - color){
				hengBegin = t + 1;
				break;
			}else{
				hengBegin = t;
			}
		}
		for(int t = lastX + 1; t <= lastX + 4; t++){
			if(t == 15 || gameStatus.pointStatus[t][lastY] == 0 - color){
				hengEnd = t - 1;
				break;
			}else{
				hengEnd = t;
			}
		}
		//shu
		for(int t = lastY - 1; t >= lastY - 4; t--){
			if(t == -1 || gameStatus.pointStatus[lastX][t] == 0 - color){
				shuBegin = t + 1;
				break;
			}else{
				shuBegin = t;
			}
		}
		for(int t = lastY + 1; t <= lastY + 4; t++){
			if(t == 15 || gameStatus.pointStatus[lastX][t] == 0 - color){
				shuEnd = t - 1;
				break;
			}else{
				shuEnd = t;
			}
		}
		//rightXie
		for(int t = lastX - 1; t >= lastX - 4; t--){
			int z = lastY + lastX - t;
			if(t == -1 || z ==  15 || gameStatus.pointStatus[t][z] == 0 - color){
				rightXieBegin = t + 1;
				break;
			}else{
				rightXieBegin = t;
			}
		}
		for(int t = lastX + 1; t <= lastX + 4; t++){
			int z = lastY + lastX - t;
			if(t == 15 || z == -1 || gameStatus.pointStatus[t][z] == 0 - color){
				rightXieEnd = t - 1;
				break;
			}else{
				rightXieEnd = t;
			}
		}
		//leftXie
		for(int t = lastX - 1; t >= lastX - 4; t--){
			int z = lastY + t - lastX;
			if(t == -1 || z == -1 || gameStatus.pointStatus[t][z] == 0 - color){
				leftXieBegin = t + 1;
				break;
			}else{
				leftXieBegin = t;
			}
		}
		for(int t = lastX + 1; t <= lastX + 4; t++){
			int z = lastY + t - lastX;
			if(t == 15 || z == 15 || gameStatus.pointStatus[t][z] == 0 - color){
				leftXieEnd = t - 1;
				break;
			}else{
				leftXieEnd = t;
			}
		}
	  if(i == lastX){
		  if(j >= shuBegin && j <= shuEnd){
			  return true;
		  }else{
			  return false;
		  }
	  }else if(j == lastY){
		  if(i >= hengBegin && i <= hengEnd){
			  return true;
		  }else{
			  return false;
		  }
	  }else if(i - lastX == j - lastY){
		  //leftxie
		  if(i >= leftXieBegin && i <= leftXieEnd){
			  return true;
		  }else{
			  return false;
		  }
	  }else if(i - lastX == lastY - j){
		  //rightxie
		  if(i >= rightXieBegin && i <= rightXieEnd){
			  return true;
		  }else{
			  return false;
		  }
	  }else{
		  return false;
	  }
   }
   //攻方冲点范围裁剪
   //有逻辑的bug，因为堵的时候，下一步会误认为从堵的地方开始发展攻势
  private boolean checkG(int i, int j){
	  if(ai.forwardPoint.size() == 0){
		  if(AIvctRecoder != -1){
			  //已经赢了，当然绝不能漏杀
			  return true;
		  }else{
			  byte[] lastPosition = getHistoryLastGPoint();
			  boolean lastB = check(i, j, lastPosition[0], lastPosition[1], getGcolor());
			  return lastB;
		  }
		  
	  }else{
		  int lastX = -1;
		  int lastY = -1;
		  int lastsecondX = -1;
		  int lastsecondY = -1;
		  int lastthirdX = -1;
		  int lastthirdY = -1;
		  int color = getGcolor();
		  if(ai.forwardPoint.size() % 2 == 0){
			  lastX = ai.forwardPoint.get(ai.forwardPoint.size() - 2)[0];
			  lastY = ai.forwardPoint.get(ai.forwardPoint.size() - 2)[1];
			  if(ai.forwardPoint.size() - 6 >= 0){
				  lastsecondX = ai.forwardPoint.get(ai.forwardPoint.size() - 4)[0];
				  lastsecondY = ai.forwardPoint.get(ai.forwardPoint.size() - 4)[1];
				  lastthirdX = ai.forwardPoint.get(ai.forwardPoint.size() - 6)[0];
				  lastthirdY = ai.forwardPoint.get(ai.forwardPoint.size() - 6)[1];
			  }else if(ai.forwardPoint.size() - 4 >= 0){
				  lastsecondX = ai.forwardPoint.get(ai.forwardPoint.size() - 4)[0];
				  lastsecondY = ai.forwardPoint.get(ai.forwardPoint.size() - 4)[1];
				  
			  }
		  }else{
			  lastX = ai.forwardPoint.get(ai.forwardPoint.size() - 1)[0];
			  lastY = ai.forwardPoint.get(ai.forwardPoint.size() - 1)[1];
			  if(ai.forwardPoint.size() - 5 >= 0){
				  lastsecondX = ai.forwardPoint.get(ai.forwardPoint.size() - 3)[0];
				  lastsecondY = ai.forwardPoint.get(ai.forwardPoint.size() - 3)[1];
				  lastthirdX = ai.forwardPoint.get(ai.forwardPoint.size() - 5)[0];
				  lastthirdY = ai.forwardPoint.get(ai.forwardPoint.size() - 5)[1];
			  }else if(ai.forwardPoint.size() - 3 >= 0){
				  lastsecondX = ai.forwardPoint.get(ai.forwardPoint.size() - 3)[0];
				  lastsecondY = ai.forwardPoint.get(ai.forwardPoint.size() - 3)[1];
			  }
		  }
		  
		  boolean lastB = false;
		  boolean lastsecondB = false;
		  boolean lastthirdB = false;
		  lastB = check(i, j, lastX, lastY, color);
		  if(fGoFour.size() > 0 && 
				  ((ai.forwardPoint.size() == fGoFour.get(fGoFour.size() - 1) + 2) || (ai.forwardPoint.size() == fGoFour.get(fGoFour.size() - 1) + 1))
				  ){
			  if(lastsecondX != -1){
					  lastsecondB = check(i, j, lastsecondX, lastsecondY, color);		  	  
			  }
			  if(lastthirdX != -1){
				  lastthirdB = check(i, j, lastthirdX, lastthirdY, color);
			  }
		  }  
		  return lastB || lastsecondB || lastthirdB;
	  }
  }
  
  //守方冲点范围裁剪
  
  private boolean checkF(int i, int j){

	  if(ai.forwardPoint.size() == 0){
		  //第一步假设攻方冲点
		  return false;
	  }else if(ai.forwardPoint.size() == 1){
		  //给守方全屏冲点的可能
		  return true;
	  }else if(
			  (ai.forwardPoint.size() % 2 == 1 && initF == -1)
			  || (ai.forwardPoint.size() % 2 == 1 && initF == ai.forwardPoint.size())			  
			  ){
		   //给守方全屏冲点的可能
		   return true;
	  }else{
		  int lastX = 0;
		  int lastY = 0;
		  int color = 0 - getGcolor();
		  if(ai.forwardPoint.size() % 2 == 0){
			  lastX = ai.forwardPoint.get(ai.forwardPoint.size() - 1)[0];
			  lastY = ai.forwardPoint.get(ai.forwardPoint.size() - 1)[1];
			
		  }else{
			  lastX = ai.forwardPoint.get(ai.forwardPoint.size() - 2)[0];
			  lastY = ai.forwardPoint.get(ai.forwardPoint.size() - 2)[1];
			 
		  }
		  boolean lastB = false;
		  
		  lastB = check(i, j, lastX, lastY, color);
		 
		  return lastB;
		 
	  }
  
  }
  
  //界定攻方第一步的范围时需要查询最后一步的位置，承接它进行分析
  public byte[] getHistoryLastGPoint(){
	  byte[] position = new byte[2];
	  if(getGcolor() == GameStatus.BLACK){
		  //寻找最后的黑棋
		  if(gameStatus.historyPoint.size() % 2 == 0){
			  position = gameStatus.historyPoint.get(gameStatus.historyPoint.size() - 2);
		  }else{
			  position = gameStatus.historyPoint.get(gameStatus.historyPoint.size() - 1);
		  }
		  
	  }else{
           if(gameStatus.historyPoint.size() % 2 == 0){
        	   position = gameStatus.historyPoint.get(gameStatus.historyPoint.size() - 1);
		  }else{
			  position = gameStatus.historyPoint.get(gameStatus.historyPoint.size() - 2);
		  }
	  }
	  
	  return position;
  }
  
}
