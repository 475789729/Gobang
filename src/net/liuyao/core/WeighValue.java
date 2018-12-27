package net.liuyao.core;

public class WeighValue {
	/**
	 * 不含状态，纯粹是工具类
	 * 不会改变gameStatus
	 * 注意:本方法无法判断活三是真活三还是假活三(禁手规则下)，这意味着含三特殊点判断都不严格
	 *  */
	   private GameStatus gameStatus;
	   
	   //普通
	   public final static float DEFAULT_LOW_LEVEL = 0f;
	   //活三特殊点
	   public final static float DEFAULT_THREE_LEVEL = 1f;
	   //死四特殊点
	   public final static float DEFAULT_DFOUR_LEVEL = 2f;
	   //活四特殊点
	   public final static float DEFAULT_AFOUR_LEVEL = 3f;
	   //禁手
	   public final static float DEFAULT_MANY_LEVEL = 4f; 
	   //取胜点
	   public final static float DEFAULT_FIVE_LEVEL = 5f;
	   //每个方向汇总分析之后。总结为下面的点
       //三三点
	   public final static float DEFAULT_DOUBLETHREE = 6f;
	   //四四点
	   public final static float DEFAULT_DOUBLEFOUR = 7f;
	   //三四点
	   public final static float DEFAULT_THREEFOUR = 8f;
	   
	   
	   public final static float SCALE_D = 0.66f;
	   public final static float SCALE_A = 0.33f;
	   
	 //单个冲刺点的分值
	   public final static float POINT_SCORE= 7f;
	   
	   //斜线加成
	   public final static float XIE_PLUS = 1.2f;
	   //下面这个值越高，活三和死四的估值越高
	   public  static float CRAZY_SCORE= 0.7f;
	   public final static float SCALE_ATWO_ONE = 2f;
	   public final static float SCALE_ATWO_TWO = 1.5f;
	   public final static float SCALE_ATWO_THREE = 1f;
	   public final static float SCORE_DTHREE = POINT_SCORE;
	   public final static float SCORE_DTWO = SCORE_DTHREE * SCALE_D;
	   public final static float SCORE_DONE = SCORE_DTWO * SCALE_D;
	   public final static float SCORE_ATWO = SCALE_ATWO_ONE * POINT_SCORE + SCORE_DTWO;
	   public final static float SCORE_AONE = SCALE_A  * SCORE_ATWO;
	   public  static float SCORE_DFOUR = (SCORE_ATWO * 2 - SCORE_DTHREE - SCORE_DTWO) * CRAZY_SCORE;
	   public  static float SCORE_ATHREE = SCORE_DFOUR + SCORE_DTHREE;
	   //禁手规则
	   private boolean role = false;
	   
	  
	   //两者相同会产生bug
	   //攻防控制器之攻击，黑棋狂攻，白棋狂守
	   public final static float gfController_G = 0.99f;
	   //攻防控制器之防守，黑棋狂守，白棋狂攻
	   public final static float gfController_F = 0.01f;
	   
	   
	   
	   public  static float gfController = 0.1f;
	   private static byte aiColor;
	   public WeighValue(GameStatus gameStatus, boolean role){
		    this.gameStatus = gameStatus;
		    this.role = role;
		    
	   }
	   
	   public void setRole(boolean role){
		   this.role = role;
	   }
	 
	  public static void init(byte color){
		  
		      aiColor = color;
		  if(color == GameStatus.BLACK){
			  turnG();
		  }else{
			  turnF();
		  }
		  
		  if(aiColor == GameStatus.BLACK){
			  CRAZY_SCORE= 0.7f;
			  SCORE_DFOUR = (SCORE_ATWO * 2 - SCORE_DTHREE - SCORE_DTWO) * CRAZY_SCORE;
			  SCORE_ATHREE = SCORE_DFOUR + SCORE_DTHREE;
		  }else{
			  CRAZY_SCORE= 0.7f;
			  SCORE_DFOUR = (SCORE_ATWO * 2 - SCORE_DTHREE - SCORE_DTWO) * CRAZY_SCORE;
			  SCORE_ATHREE = SCORE_DFOUR + SCORE_DTHREE;
		  }
	  }
	   
	  public static void turnG(){
		  if(aiColor == GameStatus.BLACK){
			  WeighValue.gfController = gfController_G;
		  }else{
			  WeighValue.gfController = gfController_F;
		  }
		  
	  }
	  
	  public static void turnF(){
		  if(aiColor == GameStatus.BLACK){
			  WeighValue.gfController = gfController_F;
		  }else{
			  WeighValue.gfController = gfController_G;
		  }
	  }
	  
	  
	  
	  
	  
	   
	   
	   //总分和点类型
	   //可以分析空格和有颜色，分析有颜色时候，先设置成空格，再恢复颜色
	   //haixuan为true的时候，是指的粗略估计目前走这个棋的可能性
	   public float[]  weighPoint(byte[] position, byte color){
		 
		   boolean empty = true;
		   if(gameStatus.pointStatus[position[0]][position[1]] == 0 - color){
			   throwWrong();
		   }else if(gameStatus.pointStatus[position[0]][position[1]] == color){
			   empty = false;
			   
			   gameStatus.pointStatus[position[0]][position[1]] = GameStatus.EMPTY;
		   }
		  int[] hengInformation = getHengStick(position, color);
		  int[] shuInformation = getShuStick(position, color);
		  int[] leftXieInformation = getLeftXie(position, color);
		  int[] rightXieInformation = getRightXie(position, color);
		  float[] hengResult = weighHeng(position, color, hengInformation);
		  float[] shuResult = weighShu(position, color, shuInformation);
		  float[] leftXieResult = weighLeftXie(position, color, leftXieInformation);
		  float[] rightXieResult = weighRightXie(position, color, rightXieInformation);
		  if(!empty){
			
		    gameStatus.pointStatus[position[0]][position[1]] = color;
		  }
		  
		  
		  float[] pointResult = new float[2];
		  pointResult[0] = 0f;
		  pointResult[1] = DEFAULT_LOW_LEVEL;
		  int count_Five = 0;
		  int count_AFour = 0;
		  int count_ATHREE = 0;
		  int count_DFOUR = 0;
		  int count_Role = 0;
		  if(hengResult[1] == DEFAULT_FIVE_LEVEL){
			  count_Five++;
		  }else if(hengResult[1] == DEFAULT_AFOUR_LEVEL){
			  count_AFour++;
		  }else if(hengResult[1] == DEFAULT_DFOUR_LEVEL){
			  count_DFOUR++;
		  }else if(hengResult[1] == DEFAULT_THREE_LEVEL){
			  count_ATHREE++;
		  }else if(hengResult[1] == DEFAULT_MANY_LEVEL){
			  count_Role++;
		  }
		  
		  if(shuResult[1] == DEFAULT_FIVE_LEVEL){
			  count_Five++;
		  }else if(shuResult[1] == DEFAULT_AFOUR_LEVEL){
			  count_AFour++;
		  }else if(shuResult[1] == DEFAULT_DFOUR_LEVEL){
			  count_DFOUR++;
		  }else if(shuResult[1] == DEFAULT_THREE_LEVEL){
			  count_ATHREE++;
		  }else if(shuResult[1] == DEFAULT_MANY_LEVEL){
			  count_Role++;
		  }
		  
		  if(leftXieResult[1] == DEFAULT_FIVE_LEVEL){
			  count_Five++;
		  }else if(leftXieResult[1] == DEFAULT_AFOUR_LEVEL){
			  count_AFour++;
		  }else if(leftXieResult[1] == DEFAULT_DFOUR_LEVEL){
			  count_DFOUR++;
		  }else if(leftXieResult[1] == DEFAULT_THREE_LEVEL){
			  count_ATHREE++;
		  }else if(leftXieResult[1] == DEFAULT_MANY_LEVEL){
			  count_Role++;
		  }
		  
		  if(rightXieResult[1] == DEFAULT_FIVE_LEVEL){
			  count_Five++;
		  }else if(rightXieResult[1] == DEFAULT_AFOUR_LEVEL){
			  count_AFour++;
		  }else if(rightXieResult[1] == DEFAULT_DFOUR_LEVEL){
			  count_DFOUR++;
		  }else if(rightXieResult[1] == DEFAULT_THREE_LEVEL){
			  count_ATHREE++;
		  }else if(rightXieResult[1] == DEFAULT_MANY_LEVEL){
			  count_Role++;
		  }
		  
		  if(count_Five > 0){
			  pointResult[0] = 2000000f;
			  pointResult[1] = DEFAULT_FIVE_LEVEL;
						  
			  return pointResult;
		  }else if(count_Role > 0 && role && color == GameStatus.BLACK){
			  pointResult[0] = -2000000f;
			  pointResult[1] = DEFAULT_MANY_LEVEL;
			
			  return pointResult;
		  }else if(count_AFour + count_DFOUR > 1){
			  if(role && color == GameStatus.BLACK){
				  pointResult[0] = -2000000f;
				  pointResult[1] = DEFAULT_MANY_LEVEL;
				
				  return pointResult;
			  }else{
				  pointResult[0] = 200000f;
				  pointResult[1] = DEFAULT_DOUBLEFOUR;							
				  return pointResult;
			  }
		  }else if(count_AFour + count_DFOUR == 1 && count_ATHREE > 1){
			  if(role && color == GameStatus.BLACK){
				  pointResult[0] = -2000000f;
				  pointResult[1] = DEFAULT_DOUBLETHREE;
				  return pointResult;
			  }else{
				  pointResult[0] = 200000f;
				  pointResult[1] = DEFAULT_THREEFOUR;
				  return pointResult;
			  }
		  }else if(count_AFour + count_DFOUR == 0 && count_ATHREE > 1){
			  if(role && color == GameStatus.BLACK){
				  pointResult[0] = -2000000f;
				  pointResult[1] = DEFAULT_DOUBLETHREE;
				  return pointResult;
			  }else{
				  pointResult[0] = 20000f;
				  pointResult[1] = DEFAULT_DOUBLETHREE;
				  return pointResult;
			  }
		  }else if(count_ATHREE == 1 && count_AFour + count_DFOUR == 1){
			  pointResult[0] = 200000f;
			  pointResult[1] = DEFAULT_THREEFOUR;
			  return pointResult;
		  }else if(count_AFour > 0){
			  pointResult[0] = 200000f;
			  pointResult[1] = DEFAULT_AFOUR_LEVEL;
			  return pointResult;
		  }else if(count_ATHREE == 1 && count_AFour + count_DFOUR == 0){
			  pointResult[0] = calculate(hengResult[0], shuResult[0], leftXieResult[0], rightXieResult[0]);
			  pointResult[1] = DEFAULT_THREE_LEVEL;
			  return pointResult;
		  }else if(count_ATHREE == 0 && count_DFOUR == 1){
			  pointResult[0] = calculate(hengResult[0], shuResult[0], leftXieResult[0], rightXieResult[0]);
			  pointResult[1] = DEFAULT_DFOUR_LEVEL;
			  return pointResult;
		  }else{		  
			  pointResult[0] = calculate(hengResult[0], shuResult[0], leftXieResult[0], rightXieResult[0]);
			  pointResult[1] = DEFAULT_LOW_LEVEL;  
			  return pointResult;
		  }
		  
		  
	   }
	   
	   private float calculate(float heng, float shu, float leftxie, float rightxie){
		   if(gameStatus.historyPoint.size() < 15){
			   return heng + shu + leftxie + rightxie;
		   }else{
			   return heng + shu + leftxie * XIE_PLUS + rightxie * XIE_PLUS;
		   }
			   
			   
		   
				  
	   }
	  
	   private float[] weighHeng(byte[] position, byte color, int[] information){
		   if(information[3] - information[2] + 1 < 5){
			   float[] result = new float[2];
			   result[0] = 0f;
			   result[1] = DEFAULT_LOW_LEVEL;
			   return result;
			   
		   }
		     byte[] src = new byte[information[3] - information[2] + 1];
		     for(int i = 0; i < information[3] - information[2] + 1; i++){
		    	 src[i] = gameStatus.pointStatus[information[2]+i][position[1]];
		     }
		     return weighStick(src, position[0] - information[2], information[0], information[1], color);
		    
	   }
	   
	   private float[] weighShu(byte[] position, byte color, int[] information){
		   if(information[3] - information[2] + 1 < 5){
			   float[] result = new float[2];
			   result[0] = 0f;
			   result[1] = DEFAULT_LOW_LEVEL;
			   return result;
			   
		   }
		   byte[] src = new byte[information[3] - information[2] + 1];
		   for(int i = 0; i < information[3] - information[2] + 1; i++){
			   src[i] = gameStatus.pointStatus[position[0]][information[2]+i];
		   }
		     return weighStick(src, position[1] - information[2], information[0], information[1], color);
	   }
	   
	   private float[] weighRightXie(byte[] position, byte color, int[] information){
		   if(information[3] - information[2] + 1 < 5){
			   float[] result = new float[2];
			   result[0] = 0f;
			   result[1] = DEFAULT_LOW_LEVEL;
			   return result;
			   
		   }
		   byte[] src = new byte[information[3] - information[2] + 1];
		   for(int i = 0; i < information[3] - information[2] + 1; i++){
			   src[i] = gameStatus.pointStatus[information[2]+i][position[1] + (position[0]-information[2]) - i];
		   }
		     return weighStick(src, position[0] - information[2], information[0], information[1], color);
	   }
	   
	   private float[] weighLeftXie(byte[] position, byte color, int[] information){
		   if(information[3] - information[2] + 1 < 5){
			   float[] result = new float[2];
			   result[0] = 0f;
			   result[1] = DEFAULT_LOW_LEVEL;
			   return result;
			   
		   }
		   byte[] src = new byte[information[3] - information[2] + 1];
		   for(int i = 0; i < information[3] - information[2] + 1; i++){
			   src[i] = gameStatus.pointStatus[information[2]+i][position[1] - (position[0] - information[2]) + i];
		   }
		     return weighStick(src, position[0] - information[2], information[0], information[1], color);
	   }
	   //返回分数增量和特殊点等级
	   private float[] weighStick(byte[] src, int index, int indexInFive,int num, byte color){
		   float[] result = new float[2];
		   //五长线段的起点在数组的index
		   int stickBeginIndex =index - indexInFive;
		   int stickEndIndex = index + 4 - indexInFive;
		   if(num == 4){
			   //无禁手
			   if(!role || color == GameStatus.WHITE){
				   //无禁手规则下
				   result[0] = 2000000f;
				   result[1] = DEFAULT_FIVE_LEVEL;
				   return result;
			   }else{
				   //有禁手
				   if(
						   (stickBeginIndex > 0 && src[stickBeginIndex - 1] == color)
						   || (stickEndIndex != src.length - 1 && src[stickEndIndex + 1] == color)					
						   ){
					   result[0] = -2000000f;
					   result[1] = DEFAULT_MANY_LEVEL;
					   return result;
				   }else{
					   //不是禁手
					   result[0] = 2000000f;
					   result[1] = DEFAULT_FIVE_LEVEL;
					   return result;
				   }
			   }
		   }else if(num == 3){
			   
			   /*已经含有3个友军 */
			   //判断四四点
			   
			   if(indexInFive == 3 && src[stickBeginIndex + 1] == GameStatus.EMPTY && stickEndIndex + 2 <= src.length - 1){
				   if(
						   (stickBeginIndex == 0 || (stickBeginIndex > 0 && src[stickBeginIndex - 1] == GameStatus.EMPTY))
						   && src[stickEndIndex + 1] == GameStatus.EMPTY
						   && src[stickEndIndex + 2] == color
						   && ((stickEndIndex + 2 == src.length - 1) || (stickEndIndex + 2 < src.length - 1 && src[stickEndIndex + 3] == GameStatus.EMPTY))
						   ){
					   if(role && color == GameStatus.BLACK){
						   result[0] = -2000000f;
						   result[1] = DEFAULT_MANY_LEVEL;
						   return result;
					   }else{
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }
				   }
			   }
			   
			  if(indexInFive == 3 && src[stickBeginIndex + 2] == GameStatus.EMPTY && stickEndIndex + 3 <= src.length - 1){
				  if(
						src[stickEndIndex + 1] == GameStatus.EMPTY
						&& src[stickEndIndex + 2] == color
						&& src[stickEndIndex + 3] == color
						&& (stickBeginIndex == 0 || (stickBeginIndex > 0 && src[stickBeginIndex - 1] == GameStatus.EMPTY))
						&&(stickEndIndex + 3 == src.length - 1 || (stickEndIndex + 3 < src.length - 1 && src[stickEndIndex + 4] == GameStatus.EMPTY))
						  ){
					  
					  if(role && color == GameStatus.BLACK){
						   result[0] = -2000000f;
						   result[1] = DEFAULT_MANY_LEVEL;
						   return result;
					   }else{
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }
				  }
			  }
			   
			   
			   //一般情况
			   if(src[stickBeginIndex] == GameStatus.EMPTY  && src[stickEndIndex] == GameStatus.EMPTY){
				   //中间三子相连+1ooo1+
				  
				   
				   if(stickBeginIndex == 0 && indexInFive == 0 && (!role || color == GameStatus.WHITE)){
					   //死三变死四					   					   
					   result[0] = SCORE_DFOUR;
					   result[1] = DEFAULT_DFOUR_LEVEL;
					   return result;
				   }else if(stickBeginIndex == 0 && indexInFive == 0 && (role && color == GameStatus.BLACK)){
					   //小心长连骨架
					   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
				   }
				   else if(stickEndIndex == src.length - 1 && indexInFive == 4 && (!role || color == GameStatus.WHITE)){
					   result[0] = SCORE_DFOUR;
					   result[1] = DEFAULT_DFOUR_LEVEL;
					   return result;
				   }else if(stickEndIndex == src.length - 1 && indexInFive == 4 && (role && color == GameStatus.BLACK)){
					   //小心长连骨架
					   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
				   }
				   
				   else{
					   //形成活四
					   result[0] = 200000f;
					   result[1] = DEFAULT_AFOUR_LEVEL;
					   return result;
				   }
			   }else if(src[stickBeginIndex] == GameStatus.EMPTY && src[stickEndIndex-1] == GameStatus.EMPTY){
				   //+1oo1o+ 
				  
				   
				   if(stickBeginIndex >= 2 && stickEndIndex + 4 <= src.length - 1){
					  
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   //小心长连骨架
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   //小心骨架长连
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 4 > src.length - 1 && stickEndIndex+1 <= src.length-1){
					 //+1oo1o+ 
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   //小心长连骨架
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
							   
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   //小心长连骨架
						 //+1oo1o+ 
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex < 2 && stickEndIndex + 4 <= src.length - 1){
					 //+1oo1o+
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						 
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   //+1oo1o+
						   //小心长连骨架
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex+1 > src.length-1){
					 //+1oo1o+
					   if(indexInFive == 0){
						  
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickBeginIndex < 2 && stickEndIndex + 4 > src.length - 1 && stickEndIndex+1 <= src.length-1){
					
					 //+1oo1o+
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
						   
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickBeginIndex < 2 &&  stickEndIndex+1 > src.length-1){
					   
					   
					   result[0] = SCORE_DFOUR;
					   result[1] = DEFAULT_DFOUR_LEVEL;
					   return result;
				   }
				   else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == GameStatus.EMPTY && src[stickBeginIndex+1] == GameStatus.EMPTY){
				   //+o1oo1+ 和上面的分支是对称的
				   
				   
				  
				   if(stickEndIndex+2 <= src.length-1 && stickBeginIndex >= 4){
					   
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						 //+o1oo1+
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 <= src.length-1 && stickBeginIndex < 4 && stickBeginIndex >= 1){
					 //+o1oo1+
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						 
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 > src.length-1 && stickBeginIndex >= 4){
					 //+o1oo1+
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 <= src.length-1 && stickBeginIndex < 1){
					 //+o1oo1+
					  
					   if(indexInFive == 4){
						  
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else{
						  
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
				   }else if(stickEndIndex+2 > src.length-1 && stickBeginIndex < 4 && stickBeginIndex >= 1){
					 //+o1oo1+
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
						   
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex+2 > src.length-1 &&  stickBeginIndex < 1){
					 //+o1oo1+
					   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   if(indexInFive == 1){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
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
						
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						 throwWrong();
						   
					   }
				   }else if(stickEndIndex == src.length-1 && stickBeginIndex < 3){
					 //+1o1oo+
					   if(role && color == GameStatus.BLACK && indexInFive == 2 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
					   
				   }else if(stickEndIndex < src.length-1 && stickEndIndex+3 > src.length-1 && stickBeginIndex >= 3){
					 //+1o1oo+
					   
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTWO;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   &&stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
						   
					   }
				   }else if(stickEndIndex < src.length-1 && stickEndIndex+3 > src.length-1 && stickBeginIndex < 3){
					 //+1o1oo+
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickEndIndex+3 <= src.length-1 && stickBeginIndex >= 3){
					 //+1o1oo+
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTWO;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickEndIndex+3 <= src.length-1 && stickBeginIndex < 3){
					 //+1o1oo+
					   
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						    
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTWO;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
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
						   
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
					   else{
						   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }  
					   }
				   }else if(stickBeginIndex == 0 && stickEndIndex + 3 >  src.length - 1){
					 //+oo1o1+
					   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
					   
				   }else if(stickBeginIndex > 0 && stickBeginIndex < 3 && stickEndIndex + 3 <=  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						  
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTWO;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
						   
					   }
				   }else if(stickBeginIndex > 0 && stickBeginIndex < 3 && stickEndIndex + 3 >  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickBeginIndex >= 3 && stickEndIndex + 3 <=  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						  
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							    result[0] = SCORE_DTWO;
							    result[1] = DEFAULT_LOW_LEVEL;
							    return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
							   
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result; 
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickBeginIndex >= 3 && stickEndIndex + 3 >  src.length - 1){
					 //+oo1o1+
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTWO;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
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
					  
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						  //+o1ooo+
						   result[0] = SCORE_DFOUR+SCORE_DONE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DONE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR+SCORE_DONE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickEndIndex +2 <= src.length - 1 && stickBeginIndex < 4){
					 //+2ooo+
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						  if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							  result[0] = 0f;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else{
							  result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						  }
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }
				   else if(stickEndIndex + 1 == src.length - 1 && stickBeginIndex >= 4){
					 //+2ooo+
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
								  result[0] = SCORE_DONE;
								  result[1] = DEFAULT_LOW_LEVEL;
								  return result;
							  }else{
								  result[0] = SCORE_DFOUR + SCORE_DONE;
								   result[1] = DEFAULT_DFOUR_LEVEL;
								   return result;
							  }
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickEndIndex + 1 == src.length - 1 && stickBeginIndex < 4){
					 //+2ooo+
					   if(indexInFive == 0 && (!role || color == GameStatus.WHITE)){
						 
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 0 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
								  result[0] = 0f;
								  result[1] = DEFAULT_LOW_LEVEL;
								  return result;
							  }else{
								  result[0] = SCORE_DFOUR;
								   result[1] = DEFAULT_DFOUR_LEVEL;
								   return result;
							  }
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						 //+2ooo+
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
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
						  
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(stickEndIndex == src.length - 1 && stickBeginIndex < 4){
					 //+2ooo+
					   if(role && color == GameStatus.BLACK && indexInFive == 1 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
					   
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == GameStatus.EMPTY  && src[stickEndIndex - 1] == GameStatus.EMPTY){
				   //+ooo2+ 和上面情况对称				   
				   
				   if(stickBeginIndex >= 2 && stickEndIndex + 4 <= src.length - 1){
					 
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						  
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DONE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DONE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 4 > src.length - 1){
					 //+ooo2+ 
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						  
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }
				   else if(stickBeginIndex == 1 && stickEndIndex + 4 <= src.length - 1){
					 //+ooo2+ 
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DONE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DONE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
					   
				   }else if(stickBeginIndex == 1 && stickEndIndex + 4 > src.length - 1){
					 //+ooo2+ 
					   
					   if(indexInFive == 4 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = 200000f;
						   result[1] = DEFAULT_AFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }else{
							   result[0] = 200000f;
							   result[1] = DEFAULT_AFOUR_LEVEL;
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
						   
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(indexInFive == 3 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }
					   else{
						  throwWrong();
						   
					   }
				   }else if(stickBeginIndex == 0 && stickEndIndex + 4 > src.length - 1){
					   if(role && color == GameStatus.BLACK && indexInFive == 3 && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
					   
				   }else{
					   throwWrong();
				   }
			   
				   
				   
			   }else if(src[stickBeginIndex + 1] == GameStatus.EMPTY  && src[stickEndIndex - 1] == GameStatus.EMPTY){
				   //+o1o1o+
				   
				   if(stickBeginIndex < 2 && stickEndIndex + 2 > src.length - 1){
					   if(role && color == GameStatus.BLACK && indexInFive == 1 && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(role && color == GameStatus.BLACK && indexInFive == 3 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   result[0] = 0f;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
					   
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 2 > src.length - 1){
					   if(indexInFive == 1){
						   
						 //+o1o1o+
						   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   if(
									   (stickBeginIndex == 2 || (stickBeginIndex >= 3 && src[stickBeginIndex - 3] != GameStatus.BLACK))
									   && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
								   result[0] = SCORE_DFOUR;
								   result[1] = DEFAULT_DFOUR_LEVEL;
								   return result; 
							   }else{
								   result[0] = SCORE_DTHREE;
								   result[1] = DEFAULT_LOW_LEVEL;
								   return result;
							   }
							   
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else{
						   //indexInFive == 3
						  if(role && color == GameStatus.BLACK && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							  result[0] = 0f;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else{
							  result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						  }
						   
					   }
				   }else if(stickBeginIndex < 2 && stickEndIndex + 2 <= src.length - 1){
					 //+o1o1o+
					   if(indexInFive == 1){
						   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else{
						 //indexInFive == 3
						   if(role && color == GameStatus.BLACK && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   if(
									   (stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK)) 
									   && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
								   result[0] = SCORE_DFOUR;
								   result[1] = DEFAULT_DFOUR_LEVEL;
								   return result;
							   }else{
								   result[0] = SCORE_DTHREE;
								   result[1] = DEFAULT_LOW_LEVEL;
								   return result;
							   }
							   
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }
				   }else if(stickBeginIndex >= 2 && stickEndIndex + 2 <= src.length - 1){
					 //+o1o1o+
					   if(role && color == GameStatus.BLACK && indexInFive == 1 && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
						   if(
								   (stickBeginIndex == 2 || (stickBeginIndex >= 3 && src[stickBeginIndex - 3] != GameStatus.BLACK))
								   && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							  result[0] =  SCORE_DFOUR;
							  result[1] = DEFAULT_DFOUR_LEVEL;
							  return result;
						   }else{
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   
					   }else if(role && color == GameStatus.BLACK && indexInFive == 3 && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
						   if(
								   (stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK))
								   && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							      result[0] =  SCORE_DFOUR;
								  result[1] = DEFAULT_DFOUR_LEVEL;
								  return result;
						   }else{
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   
					   }else{
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }
						   
					  
				   }
			   }else if(src[stickBeginIndex + 1] == GameStatus.EMPTY  && src[stickBeginIndex + 2] == GameStatus.EMPTY){
				   //+o2oo+
				   
				   if(indexInFive == 1){
					   if(stickBeginIndex >= 3){
						   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   if(
									   (stickBeginIndex == 3 || (stickBeginIndex >= 4 && src[stickBeginIndex - 4] != GameStatus.BLACK))
									   && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK && src[stickBeginIndex - 3] == GameStatus.BLACK){
								   result[0] = SCORE_DFOUR;
								   result[1] = DEFAULT_DFOUR_LEVEL;
								   return result;
							   }else{
								   result[0] = SCORE_DTWO;
								   result[1] = DEFAULT_LOW_LEVEL;
								   return result;
							   }
							   
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else if(stickBeginIndex < 3){
						   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					 //+o2oo+
					   
					   if(stickEndIndex + 2 <= src.length - 1){
						   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   ){
							   if(stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK)){
								   result[0] = SCORE_DFOUR;
								   result[1] = DEFAULT_DFOUR_LEVEL;
								   return result;
							   }else{
								   result[0] = 0f;
								   result[1] = DEFAULT_LOW_LEVEL;
								   return result;
							   }
							   
						   }else if(role && color == GameStatus.BLACK && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
								   if(stickEndIndex + 2 == src.length - 1 || (stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] != GameStatus.BLACK)){
									   result[0] = SCORE_DFOUR;
									   result[1] = DEFAULT_DFOUR_LEVEL;
									   return result;
								   }else{
									   result[0] = 0f;
									   result[1] = DEFAULT_LOW_LEVEL;
									   return result; 
								   }
								   
							   }else{
								   result[0] = SCORE_DTHREE;
								   result[1] = DEFAULT_LOW_LEVEL;
								   return result;
							   }
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else if(stickEndIndex + 2 > src.length - 1){
						   if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length -1 && src[stickEndIndex + 1] == GameStatus.BLACK
								   && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(role && color == GameStatus.BLACK && stickEndIndex + 1 <= src.length -1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(role && color == GameStatus.BLACK && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
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
						   if(role && color == GameStatus.BLACK && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   if(src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK && src[stickEndIndex + 3] == GameStatus.BLACK){
								  if(stickEndIndex + 3 == src.length - 1 || (stickEndIndex + 4 <= src.length - 1 && src[stickEndIndex + 4] != GameStatus.BLACK)){
									  result[0] = SCORE_DFOUR;
									   result[1] = DEFAULT_DFOUR_LEVEL;
									   return result;
								  }else{
									  result[0] = 0f;
									  result[1] = DEFAULT_LOW_LEVEL;
									  return result;
								  }
								   
							   }else{
								   result[0] = SCORE_DTWO;
								   result[1] = DEFAULT_LOW_LEVEL;
								   return result;
							   }
							   
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else if(stickEndIndex + 3 > src.length - 1){
						   if(role && color == GameStatus.BLACK && stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
						   
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					 //oo11o
					   if(stickBeginIndex >= 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR  + SCORE_DTHREE;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 2 && (role && color == GameStatus.BLACK)){
						   if(
								   (stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK)
								   || (stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK)
								   ){
							   if(src[stickBeginIndex - 1] == GameStatus.BLACK || src[stickBeginIndex - 2] == GameStatus.BLACK){
								   if(stickBeginIndex == 2 || (stickBeginIndex >= 3 && src[stickBeginIndex - 3] != GameStatus.BLACK)){
									   result[0] = SCORE_DFOUR;
									   result[1] = DEFAULT_DFOUR_LEVEL;
									   return result;
								   }else{
									   result[0] = 0f;
									   result[1] = DEFAULT_LOW_LEVEL;
									   return result;
								   }
							   }else{
								   result[0] = SCORE_DTHREE;
								   result[1] = DEFAULT_LOW_LEVEL;
								   return result;
							   }
						   }else{
							   result[0] = SCORE_DFOUR  + SCORE_DTHREE;
							   result[1] = DEFAULT_DFOUR_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex < 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_DFOUR_LEVEL;
						   return result;
					   }else if(stickBeginIndex < 2 && (role && color == GameStatus.BLACK)){
						   if(
								   (stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK)
								   || (stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK)
								   ){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_DFOUR_LEVEL;
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
			   			   
			  			   			  			   		   			   
			   if(src[stickBeginIndex] == color && src[stickEndIndex] == color){
				   //o3o
				   if(indexInFive == 1){
					   //oo11o
					   if(stickBeginIndex >= 3){
						   result[0] = SCORE_DTHREE + SCORE_DTWO;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(stickBeginIndex < 3){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					   //o1o1o
					   if(role && color == GameStatus.BLACK){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   if(stickBeginIndex >= 2 && stickEndIndex + 2 <= src.length -1){
							   result[0] = 4 * POINT_SCORE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex == 0 && stickEndIndex + 2 <= src.length - 1){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex == 1 && stickEndIndex + 2 <= src.length - 1){
							   result[0] = 3 * POINT_SCORE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && stickEndIndex == src.length -1){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex == 0 && stickEndIndex == src.length -1){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex == 1 && stickEndIndex == src.length -1){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 2 && stickEndIndex + 1 == src.length -1){
							   result[0] = 3 * POINT_SCORE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex == 0 && stickEndIndex + 1 == src.length -1){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex == 1 && stickEndIndex + 1 == src.length -1){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   throwWrong();
						   }
					   }
					   
				   }else if(indexInFive == 3){
					   //o2oo
					   if(stickEndIndex + 3 <= src.length - 1){
						   result[0] = SCORE_DTHREE  + SCORE_DTWO;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(stickEndIndex + 3 > src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
				   
				   
				   
			   }else if(src[stickBeginIndex] == color && src[stickEndIndex - 1] == color){
				   //o11o1
				   if(indexInFive == 1){
					   //oo1o1
					   if(stickBeginIndex >= 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 3 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex < 3 && stickBeginIndex > 0 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex < 3 && stickBeginIndex > 0 && (role && color == GameStatus.BLACK)){
						 //o11o1
						 //oo1o1
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					   //o11o1
					   //o1oo1
					   
					   if(stickBeginIndex > 0 && stickEndIndex + 2 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex + 2 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result; 
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex > 0 && stickEndIndex + 2 > src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex + 2 > src.length - 1 && (role && color == GameStatus.BLACK)){
						 //o11o1
						   //o1oo1
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result; 
						   }else if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0 && stickEndIndex + 2 <= src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(stickBeginIndex == 0 && stickEndIndex + 2 > src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 4){
					   //o11o1
					   //o11oo
					   if(stickEndIndex + 3 <= src.length - 1){
						   result[0] = SCORE_DTHREE  + SCORE_DTWO;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(stickEndIndex + 3 > src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == color && src[stickBeginIndex + 1] == color){
				   //1o11o 和上面对称

				   
				   if(indexInFive == 3){
					 //1o11o
					   //1o1oo
					   
					   if(stickEndIndex + 3 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   
					   }else if(stickEndIndex + 3 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 3 > src.length - 1 && stickEndIndex != src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 3 > src.length - 1 && stickEndIndex != src.length - 1 && (role && color == GameStatus.BLACK)){
						 //1o11o
						   //1o1oo
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					   //1o11o
					   //1oo1o
					   
					   if(stickEndIndex != src.length - 1 && stickBeginIndex >= 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex != src.length - 1 && stickBeginIndex >= 2 && (role && color == GameStatus.BLACK)){
						 //1o11o
						   //1oo1o
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex != src.length - 1 && stickBeginIndex < 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex != src.length - 1 && stickBeginIndex < 2 && (role && color == GameStatus.BLACK)){
						 //1o11o
						   //1oo1o
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 0){
					   //1o11o
					   //oo11o
					   
					   if(stickBeginIndex >= 3){
						   result[0] = SCORE_DTHREE + SCORE_DTWO;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(stickBeginIndex < 3){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
			   
			   }else if(src[stickBeginIndex] == color && src[stickEndIndex - 2] == color){
				   //o1o11
				   if(indexInFive == 1){
					   //ooo11
					   
					   if(stickBeginIndex >= 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex == 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = 0f;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 3){
					   //o1o11
					   //o1oo1
					   if(stickBeginIndex > 0 && stickEndIndex + 2 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex + 2 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex > 0 && stickEndIndex + 2 > src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex + 2 > src.length - 1 && (role && color == GameStatus.BLACK)){
						 //o1o11
						   //o1oo1
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 4){
					 //o1o11
					 //o1o1o
					  if(role && color == GameStatus.BLACK){
						  result[0] = SCORE_DTHREE;
						  result[1] = DEFAULT_LOW_LEVEL;
						  return result;
					  }else{
						  if(stickBeginIndex >= 2 && stickEndIndex + 2 <= src.length - 1){
							  result[0] = 4 * POINT_SCORE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else if(stickBeginIndex < 2 && stickEndIndex + 2 <= src.length - 1){
							  result[0] = 3 * POINT_SCORE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else if(stickBeginIndex >= 2 && stickEndIndex + 2 > src.length - 1){
							  result[0] = 3 * POINT_SCORE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else if(stickBeginIndex < 2 && stickEndIndex + 2 > src.length - 1){
							  result[0] = SCORE_DTHREE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else{
							  throwWrong();
						  }
					  }
				   }
			   }else if(src[stickEndIndex] == color && src[stickBeginIndex + 2] == color){
				   //11o1o 和上面对称

				   
				   if(indexInFive == 3){
					   //11o1o
					   //11ooo
					   
					   if(stickEndIndex + 2 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK
								   && stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
							   
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 1 == src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 1 == src.length - 1 && (role && color == GameStatus.BLACK)){
						   //11o1o
						   //11ooo
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 1){
					   //11o1o
					   //1oo1o
					   
					   if(stickEndIndex + 4 <= src.length - 1 && stickBeginIndex >= 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 4 <= src.length - 1 && stickBeginIndex >= 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 4 <= src.length - 1 && stickBeginIndex < 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 4 <= src.length - 1 && stickBeginIndex < 2 && (role && color == GameStatus.BLACK)){
						   //11o1o
						   //1oo1o
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1 && stickBeginIndex >= 2){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(stickEndIndex == src.length - 1 && stickBeginIndex < 2){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else if(stickEndIndex + 4 > src.length - 1 && stickEndIndex != src.length - 1 && stickBeginIndex >= 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 4 > src.length - 1 && stickEndIndex != src.length - 1 && stickBeginIndex >= 2 && (role && color == GameStatus.BLACK)){
						   //11o1o
						   //1oo1o
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 4 > src.length - 1 && stickEndIndex != src.length - 1 && stickBeginIndex < 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 4 > src.length - 1 && stickEndIndex != src.length - 1 && stickBeginIndex < 2 && (role && color == GameStatus.BLACK)){
						 //11o1o
						   //1oo1o
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else{
						   throwWrong();
					   }
				   }else if(indexInFive == 0){
					 //o1o1o
					 
					  if(role && color == GameStatus.BLACK){
						  result[0] = SCORE_DTHREE;
						  result[1] = DEFAULT_LOW_LEVEL;
						  return result;
					  }else{
						  if(stickBeginIndex >= 2 && stickEndIndex + 2 <= src.length - 1){
							  result[0] = 4 * POINT_SCORE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else if(stickBeginIndex < 2 && stickEndIndex + 2 <= src.length - 1){
							  result[0] = 3 * POINT_SCORE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else if(stickBeginIndex >= 2 && stickEndIndex + 2 > src.length - 1){
							  result[0] = 3 * POINT_SCORE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else if(stickBeginIndex < 2 && stickEndIndex + 2 > src.length - 1){
							  result[0] = SCORE_DTHREE;
							  result[1] = DEFAULT_LOW_LEVEL;
							  return result;
						  }else{
							  throwWrong();
						  }
					  }
				   }else{
					   throwWrong();
				   }
			   
			   }else if(src[stickBeginIndex] == color && src[stickEndIndex - 3] == color){
				   //oo111
				   if(indexInFive == 2){
					   //oo111
					   //ooo11
					   
					   if(stickBeginIndex >= 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   
					   else if(stickBeginIndex == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex == 1 && (role && color == GameStatus.BLACK)){
						 //oo111
						   //ooo11
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 3){
					   //oo111
					   //oo1o1
					   if(stickBeginIndex > 0 && stickEndIndex + 3 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex + 3 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR + SCORE_DONE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex > 0 && stickEndIndex + 3 > src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex + 3 > src.length - 1 && (role && color == GameStatus.BLACK)){
						 //oo111
						   //oo1o1
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }
						   else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 4){
					 //oo111
					 //oo11o
					  if(stickEndIndex + 4 <= src.length - 1){
						      result[0] = SCORE_DTHREE + SCORE_DONE;
						      result[1] = DEFAULT_LOW_LEVEL;
						      return result;
					  }else if(stickEndIndex + 4 > src.length - 1){
						  result[0] = SCORE_DTHREE;
					      result[1] = DEFAULT_LOW_LEVEL;
					      return result;
					  }else{
						  throwWrong();
					  }
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex] == color && src[stickBeginIndex + 3] == color){
				   //111oo 和上面对称

				   
				   if(indexInFive == 2){
					   //111oo
					   //11ooo
					   if(stickEndIndex + 2 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK 
								   && stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.EMPTY && src[stickEndIndex + 3] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
							   
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 1 == src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 1 == src.length - 1 && (role && color == GameStatus.BLACK)){
						 //111oo
						   //11ooo
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 1){
					   //111oo
					   //1o1oo
					   if(stickEndIndex != src.length - 1 && stickBeginIndex >= 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex != src.length - 1 && stickBeginIndex >= 3 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DONE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex != src.length - 1 && stickBeginIndex < 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex != src.length - 1 && stickBeginIndex < 3 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 0){
					 //111oo
					 //o11oo
					   
					  if(stickBeginIndex >= 4){
						      result[0] = SCORE_DTHREE + SCORE_DONE;
						      result[1] = DEFAULT_LOW_LEVEL;
						      return result;
					  }else if(stickBeginIndex < 4){
						  result[0] = SCORE_DTHREE;
					      result[1] = DEFAULT_LOW_LEVEL;
					      return result;
					  }else{
						  throwWrong();
					  }
				   }else{
					   throwWrong();
				   }
			   
			   }else if(src[stickBeginIndex + 1] == color && src[stickEndIndex - 1] == color){
				   //1o1o1
				   if(indexInFive == 0){
					 //1o1o1
					   //oo1o1
					   if(stickBeginIndex >= 3 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 3 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							      result[0] = SCORE_DTHREE;
							      result[1] = DEFAULT_LOW_LEVEL;
							      return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							      result[0] = SCORE_DTHREE;
							      result[1] = DEFAULT_LOW_LEVEL;
							      return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex < 3 && stickBeginIndex > 0 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex < 3 && stickBeginIndex > 0 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							      result[0] = SCORE_DTHREE;
							      result[1] = DEFAULT_LOW_LEVEL;
							      return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							      result[0] = SCORE_DTHREE;
							      result[1] = DEFAULT_LOW_LEVEL;
							      return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 2){
					   //1ooo1
					   if(stickBeginIndex > 0 && stickEndIndex != src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex != src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0 && stickEndIndex != src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex == 0 && stickEndIndex != src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex > 0 && stickEndIndex == src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex > 0 && stickEndIndex == src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0 && stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 4){
					   //1o1oo
					   if(stickEndIndex + 3 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTWO;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 3 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTWO;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 3 > src.length - 1 && stickEndIndex != src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 3 > src.length - 1 && stickEndIndex != src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickBeginIndex + 1] == color && src[stickBeginIndex + 2] == color){
				   //1oo11
				   if(indexInFive == 0){
					 //1oo11
					   //ooo11
					   if(stickBeginIndex >= 2 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 2 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 3 && src[stickBeginIndex - 3] == GameStatus.BLACK
								   && stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex == 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 3){
					   //1ooo1
					   if(stickBeginIndex >= 1 && stickEndIndex + 1 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 1 && stickEndIndex + 1 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0 && stickEndIndex + 1 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex == 0 && stickEndIndex + 1 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex >= 1 && stickEndIndex + 1 > src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 1 && stickEndIndex + 1 > src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0 && stickEndIndex + 1 > src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 4){
					   //1oo1o
					   if(stickEndIndex + 4 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 4 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DONE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 4 > src.length - 1 && stickEndIndex != src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 4 > src.length - 1 && stickEndIndex != src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
			   }else if(src[stickEndIndex - 1] == color && src[stickBeginIndex + 2] == color){
				   
				   //11oo1 和上面对称

				   
				   if(indexInFive == 4){
					   //11ooo
					   if(stickEndIndex + 2 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 2 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK
								   && stickEndIndex + 3 <= src.length - 1 && src[stickEndIndex + 3] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex + 1 == src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickEndIndex + 1 == src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 1 && src[stickBeginIndex - 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickEndIndex == src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 1){
					   //1ooo1
					   if(stickBeginIndex >= 1 && stickEndIndex + 1 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DTHREE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 1 && stickEndIndex + 1 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK
								   && stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DTHREE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0 && stickEndIndex + 1 <= src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex == 0 && stickEndIndex + 1 <= src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickEndIndex + 2 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.EMPTY && src[stickEndIndex + 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex >= 1 && stickEndIndex + 1 > src.length - 1 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 1 && stickEndIndex + 1 > src.length - 1 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0 && stickEndIndex + 1 > src.length - 1){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else if(indexInFive == 0){
					   //11oo1 
					   //o1oo1
					   if(stickBeginIndex >= 4 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR + SCORE_DONE;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex >= 4 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR + SCORE_DONE;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex < 4 && stickBeginIndex != 0 && (!role || color == GameStatus.WHITE)){
						   result[0] = SCORE_DFOUR;
						   result[1] = DEFAULT_THREE_LEVEL;
						   return result;
					   }else if(stickBeginIndex < 4 && stickBeginIndex != 0 && (role && color == GameStatus.BLACK)){
						   if(stickBeginIndex >= 2 && src[stickBeginIndex - 1] == GameStatus.EMPTY && src[stickBeginIndex - 2] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else if(stickEndIndex + 1 <= src.length - 1 && src[stickEndIndex + 1] == GameStatus.BLACK){
							   result[0] = SCORE_DTHREE;
							   result[1] = DEFAULT_LOW_LEVEL;
							   return result;
						   }else{
							   result[0] = SCORE_DFOUR;
							   result[1] = DEFAULT_THREE_LEVEL;
							   return result;
						   }
					   }
					   else if(stickBeginIndex == 0){
						   result[0] = SCORE_DTHREE;
						   result[1] = DEFAULT_LOW_LEVEL;
						   return result;
					   }else{
						   throwWrong();
					   }
				   }else{
					   throwWrong();
				   }
			   
			   }else{
				   throwWrong();
			   }
			   
			   
			   
		   }else if(num == 1){
			   //已经有一名友军
			   //友军位置
			   int indexNotEmpty = 0;
			   for(int i = 0; i < 5; i++){
				   if(src[stickBeginIndex + i] == color){
					   indexNotEmpty = stickBeginIndex + i;
					   break;
				   }
			   }
			   //待下棋位置和友军位置中比大小，都是在src中的位置
			   int litteIndex = Math.min(indexNotEmpty, index);
			   int bigIndex = Math.max(indexNotEmpty, index);
			   if(Math.abs(index - indexNotEmpty) == 1){
				   //相邻
				   if(litteIndex >= 3 && bigIndex + 3 <= src.length - 1){
					   result[0] = SCORE_ATWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
					   
				   }else if(
						   (litteIndex == 2  && bigIndex + 3 <= src.length - 1) 
						   || (litteIndex >= 3  && bigIndex + 2 == src.length - 1)
						   ){
					   result[0] = SCALE_ATWO_TWO * POINT_SCORE + SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else if(
						   (litteIndex == 1  && bigIndex + 3 <= src.length - 1)
						   || (litteIndex >= 3  && bigIndex + 1 == src.length - 1)
						   ){
					   result[0] = SCORE_DTHREE;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else if(litteIndex == 2 && bigIndex + 2 == src.length - 1){
					   result[0] = SCORE_DTHREE;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else if(litteIndex == 0 || bigIndex == src.length - 1){
					   result[0] = SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else{
					   result[0] = SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }
			   }else if(Math.abs(index - indexNotEmpty) == 2){
				   if(litteIndex >= 2 && bigIndex + 2 <= src.length - 1){
					   result[0] = SCALE_ATWO_TWO * POINT_SCORE + SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else if(litteIndex == 1 && bigIndex + 1 == src.length - 1){
					   result[0] = SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else if(litteIndex == 0 || bigIndex == src.length - 1){
					   result[0] = SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else{
					   result[0] = SCORE_DTHREE;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
							   
				   }
			   }else if(Math.abs(index - indexNotEmpty) == 3){
				   if(litteIndex >= 2 && bigIndex + 2 <= src.length - 1){
					   result[0] = SCALE_ATWO_THREE * POINT_SCORE + SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else if(litteIndex == 0 || bigIndex == src.length - 1){
					   result[0] = SCORE_DTWO;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else{
					   result[0] = SCORE_DTHREE;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
							   
				   }
			   }
			   else if(Math.abs(index - indexNotEmpty) == 4){
				     result[0] = SCORE_AONE;
				     result[1] = DEFAULT_LOW_LEVEL;
				     return result;
			   }else{
				   throwWrong();
			   }
			   
			   
		   }else if(num == 0){
			   //还没有友军
			   if(indexInFive == 2){
				   if(stickBeginIndex >= 2 && stickEndIndex + 2 <= src.length - 1){
					   result[0] = SCORE_AONE;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else if((stickBeginIndex >= 2 && stickEndIndex + 2 > src.length - 1)
						   ||(stickBeginIndex < 2 && stickEndIndex + 2 <= src.length - 1)){
					   result[0] = SCORE_AONE * 0.6f;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }else{
					   result[0] = SCORE_DONE;
					   result[1] = DEFAULT_LOW_LEVEL;
					   return result;
				   }
				   
			   }else if(indexInFive != 2){
				   result[0] = SCORE_DONE;
				   result[1] = DEFAULT_LOW_LEVEL;
				   return result;
			   }else{
				   throwWrong();
			   }
		   }else{
			   throwWrong();
			   return null;
		   }
		   
		    return null;
	   }
	   
	   
	   
	   //返回-1到4,-1表示两路堵死，其他结果表示position处于棍子的第几
	   //第二位，返回线段的友军总数
	   //第三位,begin
	   //第四位，end
	   public int[] getHengStick(byte[] position, byte color){
		   int[] result = new int[4];
		   int begin = position[0];
		   for(int i = position[0] - 1;i >= 0;i--){
			   if(gameStatus.pointStatus[i][position[1]] == 0-color){
				   begin = i+1;
				   break;
			   }else if(i == 0){
				   begin = 0;
				   break;
			   }else{
				   begin = i;
			   }
		   }
		   int end = position[0];
		   for(int i = position[0] + 1; i<=14; i++){
			   if(gameStatus.pointStatus[i][position[1]] == 0 - color){
				   end = i - 1;
				   break;
			   }else if(i == 14){
				   end = 14;
				   break;
			   }else{
				   end = i;
			   }
		   }
		   if(end - begin < 4){
			   result[0] = -1;
			   return result;
		   }else{
			   //两个备选方案，返回插入点更加靠近线段中心的那个
			   result[0] = 4;			  
			   int maxNum = 0;
			   
			   for(int i = 4; i >= 0; i--){
				   if(position[0] - i >= begin && position[0] - i + 4 <= end){
					   int count = 0;
					   for(int j = 0; j<5; j++){
						   if(gameStatus.pointStatus[position[0] - i + j][position[1]] == color){
							   count++;
						   }
					   }
					   if(count > maxNum){
						   maxNum = count;
						   result[0] = i;
						   result[1] = maxNum;
					   }else if(count == maxNum){
						   if(Math.abs(2-i) < Math.abs(2-result[0])){
							   result[0] = i;
						   }
					   }
				   }
			   }
			   result[1] = maxNum;
			   result[2] = begin;
			   result[3] = end;
			   
			   return result;
		   }
	   }
	  
	   public int[] getShuStick(byte[] position, byte color){
		   int[] result = new int[4];
		   int begin = position[1];
		   for(int i = position[1] - 1; i >= 0; i--){
			   if(gameStatus.pointStatus[position[0]][i] == 0 - color){
				   begin = i + 1;
				   break;
			   }else if(i == 0){
				   begin = 0;
				   break;
			   }else{
				   begin = i;
			   }
		   }
		   int end = position[1];
		   for(int i = position[1] + 1; i <= 14; i++){
			   if(gameStatus.pointStatus[position[0]][i] == 0 - color){
				   end = i - 1;
				   break;
			   }else if(i == 14){
				   end = 14;
				   break;
			   }else{
				   end = i;
			   }
		   }
		   if(end - begin < 4){
			   result[0] = -1;
			   return result;
		   }
		   result[0] = 4;
		   int maxNum = 0;
		 
		   for(int i = 4; i >= 0; i--){
			   if(position[1] - i >= begin && position[1] - i + 4 <= end){
				   int count = 0;
				   for(int j = 0; j < 5; j++){
					   
					   if(gameStatus.pointStatus[position[0]][position[1] - i + j] == color){
						   count++;
						   
					   }
				   }
				   if(count > maxNum){
					   maxNum = count;
					   result[0] = i;
					   result[1] = maxNum;
				   }else if(count == maxNum){
					   if(Math.abs(2 - i) < Math.abs(2 - result[0])){
						   result[0] = i;
					   }
				   }
			   }
		   }
		   result[1] = maxNum;
		   result[2] = begin;
		   result[3] = end;
		   return result;
	   }
	   //上部右斜，返回的第一位是从左往右数
	   public int[] getRightXie(byte[] position, byte color){
		   int[] result = new int[4];
		   int begin = position[0];
		   for(int i = position[0] - 1; i >= 0 && position[1]+position[0] - i <= 14; i--){
			   if(gameStatus.pointStatus[i][position[1]+position[0]-i] == 0 - color){
				   begin = i+1;
				   break;
			   }
			   if(position[1]+position[0] - i == 14){
				   begin = i;
				   break;
			   }
			   if(i == 0){
				   begin = 0;
				   break;
			   }
		   }
		   int end = position[0];
		   for(int i = position[0] + 1; i <= 14 && position[1] + position[0] - i >= 0; i++){
			   if(gameStatus.pointStatus[i][position[1]+position[0]-i] == 0 - color){
				   end = i - 1;
				   break;
			   }
			   if(position[1] + position[0] - i == 0){
				   end = i;
				   break;
			   }
			   if(i == 14){
				   end = 14;
				   break;
			   }
		   }
		   if(end - begin < 4){
			   result[0] = -1;
			   return result;
		   }
		  
		   result[0] = 4;
		   int maxNum = 0;
		   for(int i = 4; i >= 0; i--){
			    if(position[0] - i >= begin && position[0] - i + 4 <= end){
			    	 int count = 0;
			    	 for(int j = 0; j < 5; j++){
			    		 if(gameStatus.pointStatus[position[0] - i + j][position[1] + i - j] == color){
			    			 count++;
			    		 }
			    	 }
			    	 if(count > maxNum){
			    		 maxNum = count;
			    		 result[0] = i;
			    		 result[1] = maxNum;
			    	 }else if(count == maxNum){
			    		 if(Math.abs(2 - i) < Math.abs(2 - result[0])){
			    			 result[0] = i;
			    			
			    		 }
			    	 }
			    }
			   
			}
		   result[2] = begin;
		   result[3] = end;
		   return result;
	   }
	   //上部左斜,返回第一位是从左往右数
	   
	   public int[] getLeftXie(byte[] position, byte color){
		   int[] result = new int[4];
		   int begin = position[0];
		   for(int i = position[0] - 1; i >= 0 && position[1] + i - position[0] >= 0; i--){
			   if(gameStatus.pointStatus[i][position[1] + i - position[0]] == 0 - color){
				   begin = i + 1;
				   break;
			   }
			   if(position[1] + i - position[0] == 0){
				   begin = i;
				   break;
			   }
			   if(i == 0){
				   begin = 0;
				   break;
			   }
		   }
		   int end = position[0];
		   for(int i = position[0] + 1; i <= 14 && position[1] + i - position[0] <= 14; i++){
			   if(gameStatus.pointStatus[i][position[1] + i - position[0]] == 0 - color){
				   end = i - 1;
				   break;
			   }
			   if(position[1] + i - position[0] == 14){
				   end = i;
				   break;
			   }
			   if(i == 14){
				   end = 14;
				   break;
			   }
		   }
		   if(end - begin < 4){
			   result[0] = -1;
			   return result;
		   }
		   int maxNum = 0;
		   result[0] = 4;
		   for(int i = 4; i >= 0; i--){
			   if(position[0] - i >= begin && position[0] - i + 4 <= end){
				   int count = 0;
				   for(int j = 0; j < 5; j++){
					   if(gameStatus.pointStatus[position[0]-i+j][position[1]-i+j] == color){
						   count++;
					   }
				   }
				   if(count > maxNum){
					   maxNum = count;
					   result[0] = i;
					   result[1] = maxNum;
				   }else if(count == maxNum){
					   if(Math.abs(2-i) < Math.abs(2-result[0])){
						   result[0] = i;
					   }
				   }
			   }
		   }
		   result[2] = begin;
		   result[3] = end;
		   return result;
	   }
       public void throwWrong(){
    	   int a = 1/0;
       }
       
       
}
