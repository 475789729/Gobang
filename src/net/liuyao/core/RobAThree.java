package net.liuyao.core;

public class RobAThree {
	private GameStatus gameStatus;
	private AI ai;
   public RobAThree(GameStatus gameStatus, AI ai){
	   this.gameStatus = gameStatus;
	   this.ai = ai;
   }
   
   public  byte[] getRobATHreePoint(){
	   byte[] result = new byte[2];
	   result[0] = -1;
	   for(byte i = gameStatus.left; i <= gameStatus.right; i++){
		   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
			   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
				   if(
						   (gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_THREE_LEVEL || gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL)
						   && (gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_THREE_LEVEL || gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL)
						   
						   ){
					   result[0] = i;
					   result[1] = j;
					   return result;
				   }
			   }
		   }
		  }
	   return result;
   }
   
   //前一半是黑棋攻点，后一半是白棋攻点。所以如果color是黑棋，裁剪前一半，如果是白棋，裁剪后一半
   //此方法是裁剪一些进攻方的攻点
   public boolean filterGMaybe(byte[][] maybe, byte color){
	   int beginIndex = 0;
		int length = maybe.length / 2;
		if(color == GameStatus.BLACK){
			beginIndex = 0;
		}else{
			beginIndex = length;
		}
		float[][][] pointScore = null;
		if(color == GameStatus.BLACK){
			pointScore = gameStatus.pointBlackScore;
		}else{
			pointScore = gameStatus.pointWhiteScore;
		}
		
		for(int i= 0; i < length; i++){
			if(maybe[i + beginIndex][0] != -1 && pointScore[maybe[i + beginIndex][0]][maybe[i + beginIndex][1]][1] == WeighValue.DEFAULT_LOW_LEVEL){
				byte[] thisStep = new byte[2];
				thisStep[0] = maybe[i + beginIndex][0];
				thisStep[1] = maybe[i + beginIndex][1];
				ai.forward(thisStep, color, AI.DEEPANALYZE);
				if(getRobATHreePoint()[0] != -1){
					maybe[i + beginIndex][0] = -1;
				}
				ai.backward();
			}
		}
		for(int i= 0; i < length; i++){
			if(maybe[i + beginIndex][0] != -1){
				return true;
			}
		}
		return false;
   }
}
